package Model.Searcher;

import Model.DataObjects.ParseableObjects.Query;
import Model.DataObjects.TermData;
import Model.Indexers.Indexer;
import Model.Parsers.ParsingProcess.QueryParsingProcess;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents the searcher class
 * */
public class Searcher {
    private Ranker ranker;
    private String query,desc;
    private ArrayList<String> results, filtered, filterCities;
    private static GloVe gloVe;
    Indexer indexer;
    boolean RankWithSemantic,isStem;
    private HashMap<Character, String> letters; // every letter and the name of the file

    public Searcher(String query,String desc, Indexer indexer, ArrayList<String> filterCities, boolean RankWithSemantic,boolean isStem) {
        this.query = query;
        this.desc = desc;
        this.isStem = isStem;
        this.RankWithSemantic = RankWithSemantic;
        this.indexer = indexer;
        this.filterCities = filterCities;
        this.letters = new HashMap<>();
        letters.put('a', "ABCD");
        letters.put('b', "ABCD");
        letters.put('c', "ABCD");
        letters.put('d', "ABCD");
        letters.put('e', "EFGH");
        letters.put('f', "EFGH");
        letters.put('g', "EFGH");
        letters.put('h', "EFGH");
        letters.put('i', "IJKL");
        letters.put('j', "IJKL");
        letters.put('k', "IJKL");
        letters.put('l', "IJKL");
        letters.put('m', "MNOP");
        letters.put('n', "MNOP");
        letters.put('o', "MNOP");
        letters.put('p', "MNOP");
        letters.put('q', "QRST");
        letters.put('r', "QRST");
        letters.put('s', "QRST");
        letters.put('t', "QRST");
        letters.put('u', "UVWXYZ");
        letters.put('v', "UVWXYZ");
        letters.put('w', "UVWXYZ");
        letters.put('x', "UVWXYZ");
        letters.put('y', "UVWXYZ");
        letters.put('z', "UVWXYZ");
    }

    /**
     * This method parsing the query and sent it to the ranker
     */
    public void search(){
        //parsing the original query
        Query queryObject = new Query(query);
        QueryParsingProcess queryParsingProcess = new QueryParsingProcess(isStem);
        queryParsingProcess.parsing(queryObject);
        ArrayList<String> originalParsedQuery = queryParsingProcess.getQueryAfterParsing();

        //initialize semantics which include all the the semantics word for the original query
        ArrayList<String> semantics = new ArrayList<>();
        ArrayList <String> parsedQueryWordsWithSemanticsAndDesc = originalParsedQuery;

        //description
        ArrayList<String> description;
        Query queryDescObject = new Query(desc);
        QueryParsingProcess descriptionParsingProcess = new QueryParsingProcess(isStem);
        descriptionParsingProcess.parsing(queryDescObject);
        description = descriptionParsingProcess.getQueryAfterParsing();

        //if with semantic is chosen
        if(RankWithSemantic){
            //if glove is null we create glove object
            if(gloVe==null)
                gloVe = new GloVe();

            //newQi includes semantic words
            HashSet<String>newQi = new HashSet<>();

            //for each word in the original query
            for(String qi: originalParsedQuery){
                //get semantic for current qi
                semantics = gloVe.cosineSimilarity(qi.toLowerCase());
                //if return semantic words to given word then we add it to newQi
                if(semantics!=null) {
                    for (String semanticsRes : semantics) {
                        newQi.add(semanticsRes);
                    }
                }
            }

            //create string of all the semantic words
            String queryWithSem="";
            for(String withSemantics: newQi) {
                queryWithSem += withSemantics + " ";
            }

            //parsing the new query with semantics and then we get array list with the original query plus semantics
            Query queryWithStem = new Query(queryWithSem);
            queryParsingProcess = new QueryParsingProcess(isStem);
            queryParsingProcess.parsing(queryWithStem);
            semantics = queryParsingProcess.getQueryAfterParsing();
            for(String s: semantics){
                if(!parsedQueryWordsWithSemanticsAndDesc.contains(s))
                    parsedQueryWordsWithSemanticsAndDesc.add(s);
            }
        }

        //add description
        for(String s: description){
            if(!parsedQueryWordsWithSemanticsAndDesc.contains(s))
                parsedQueryWordsWithSemanticsAndDesc.add(s);
        }

        //initialize ranker object with all the arguments
        this.ranker = new Ranker(originalParsedQuery,semantics,description,parsedQueryWordsWithSemanticsAndDesc,indexer);

        //get ranked docs
        results = ranker.getDocs();

        //if user filtered by cities
        if (filterCities.size() > 0) {
            filtered = new ArrayList<>();
            HashSet<String> docsOfFilteredCity = new HashSet<>();
            //for each city get the docs exists in and insert to docsOfFilteredCity
            for(String s:filterCities){
                char firstChar = Character.toLowerCase(s.charAt(0)); //first char in lower case
                String fileName = letters.get(firstChar);
                TermData td = indexer.getTermDataFromDic(s);
                if(td !=null) {
                    int position = td.getPosition(); //position in file
                    try {
                        String line = getLine(position, fileName); //get line from posting file
                        line = line.substring(1, line.length() - 1);
                        String[] docsAndTF = line.split(", ");
                        for (String docAndTf : docsAndTF) {
                            String docName = docAndTf.split("=")[0];
                            docsOfFilteredCity.add(docName);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            //now we have all the docs the city exist in and we need to make intersection between results and docsOfFilteredCity
            //we iterate all over the docs in results and check if doc exists in docsOfFilteredCity
            for (int j = 0; j < results.size(); j++) {
                String doc = results.get(j); //doc name
                //if docs of filtered city contains doc then we add it to filter
                if (docsOfFilteredCity.contains(doc)){
                    filtered.add(doc);
                }
            }
        }
    }

    /**
     * This method get docs of term from posting file using random access file
     * @param position - the position of the term in the posting file (take from the corpus dictionary)
     * @param fileName - file name of posting the term exists in
     * @return - the line
     */
    public String getLine(long position,String fileName) {
        try {
            RandomAccessFile rndFile = new RandomAccessFile(this.indexer.getPathOfPosting() + "\\" + fileName, "r");
            rndFile.seek(position);
            String line = rndFile.readLine();
            rndFile.close();
            return line;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "";
    }

    /**
     * This method return array list of to top 50 ranked docs
     * @return top50 docs
     */
    public ArrayList<String> getResults() {
        ArrayList<String> top50 = new ArrayList<>();
        int i = 0;
        if (filterCities.size() > 0) {
            if (filtered.size() > 50) {
                for (String s : filtered) {
                    if (i == 50)
                        break;
                    top50.add(s);
                    i++;
                }
                return top50;
            }
            return filtered;
        } else {
            if (results.size() > 50) {
                for (String s : results) {
                    if (i == 50)
                        break;
                    top50.add(s);
                    i++;
                }
                return top50;
            }
            else
                return results;
        }
    }
}
