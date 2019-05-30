package Model.Searcher;

import Model.DataObjects.TermData;
import Model.Indexers.Indexer;
import javafx.util.Pair;

import java.io.RandomAccessFile;
import java.util.*;

/**
 * This class responsible to rank the documents
 * */
public class Ranker {
    double b = 0.75;
    double k = 1.2;
    private HashMap<Character, String> letters; // every letter and the name of the file
    private ArrayList<String> docAndScore;
    private HashMap<String, Pair<Double, Integer>> docAndScoreCalc;
    private Indexer indexer;
    private ArrayList<String> semantics;
    private ArrayList<String> originalParsedQuery;
    private ArrayList<String> parsedQueryWordsWithSemanticsAndDesc;
    private ArrayList<String> description;
    private HashMap<String,String> docVector;
    private HashMap<String,Double> queryVectorOfIdf;
    private HashMap<String,Double> queryVector;
    private double lengthOfQuery;

    /**
     * C'tor
     * @param originalParsedQuery - all the words of the query
     * @param semantics - the new words from semantics
     * @param indexer - indexer object
     * @param description - words of description
     */
    public Ranker(ArrayList<String> originalParsedQuery,ArrayList<String> semantics,ArrayList<String> description,ArrayList<String> parsedQueryWordsWithSemanticsAndDesc,Indexer indexer) {
        this.lengthOfQuery = 1;
        this.docAndScore = new ArrayList<>();
        this.indexer = indexer;
        this.letters = new HashMap<>();
        this.docAndScoreCalc = new HashMap<>();
        this.semantics = semantics;
        this.originalParsedQuery = originalParsedQuery;
        this.description = description;
        this.parsedQueryWordsWithSemanticsAndDesc = parsedQueryWordsWithSemanticsAndDesc;
        this.docVector = new HashMap<>();
        this.queryVectorOfIdf = new HashMap<>();
        this.queryVector = new HashMap<>();

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

        //HashMap of each qi and the docs exists in (first string is the qi, second string is the data(hey, FBIS3=4)
        HashMap<String, String> qiAndDocs = getqiAndDocs();

        calculateBM25(qiAndDocs);

        initializeDocVector(qiAndDocs);

        initializeQueryidfVector();

        docVectorMultipleByIdf();

        initialQueryVector(parsedQueryWordsWithSemanticsAndDesc);

        finalScore();

        setdocAndScore();

        sort();
    }

    /**
     * This method create query vector
     */
    private void initialQueryVector(ArrayList<String> q) {
        double maxFrequencyInQuery = (double)getMaxFrequencyInQuery(); //just for test
        for(String word:queryVectorOfIdf.keySet()){
            double wordFreq = (double)getWordFreqInQuery(word);
            queryVector.put(word,(wordFreq/maxFrequencyInQuery)*queryVectorOfIdf.get(word));
        }
    }

    /**
     *
     * @param word - the word we want to see the freq in the query
     * @return - the freq of the word
     */
    private int getWordFreqInQuery(String word) {
        int freq = 0;
        for(String s:this.parsedQueryWordsWithSemanticsAndDesc){
            if(s.equals(word))
                freq++;
        }
        return freq;
    }

    /**
     *
     * @return - the max frequency on the query of a term
     */
    private int getMaxFrequencyInQuery() {
        ArrayList<String>q = this.parsedQueryWordsWithSemanticsAndDesc;
        int maxF = 0;
        int f = 0;
        for(int i=0; i<q.size(); i++){
            for(int j=i; j<q.size(); j++){
                if(q.get(i).equals(q.get(j)))
                    f++;
            }
            if(f>maxF)
                maxF = f;
            f=0;
        }
        return maxF;
    }

    /**
     * This method multiple doc vector by idf
     */
    private void docVectorMultipleByIdf() {
        HashMap<String,String> newDocVector = new HashMap<>();
        for(String key: docVector.keySet()){
            String newVector = "";
            String currentDocVector = docVector.get(key);
            int id = 0;
            for(int i=0;i<currentDocVector.length();i++){
                double vec_val = Double.parseDouble(currentDocVector.charAt(i)+"");
                String qi = parsedQueryWordsWithSemanticsAndDesc.get(id);
                if(queryVectorOfIdf.get(qi)!=null) {
                    double newValue = vec_val * queryVectorOfIdf.get(qi);
                    newVector = newVector + "," + newValue;
                    id++;
                }else {
                    id++;
                    i--;
                }
            }
            //insert new value
            newDocVector.put(key,newVector);
        }
        docVector = newDocVector;
    }


    /**
     * This method create query idf vector
     */
    private void initializeQueryidfVector(){
        int N = indexer.getNumberOfDocs();
        //iterate all over the query
        for(String qi: parsedQueryWordsWithSemanticsAndDesc){
            TermData td = indexer.getTermDataFromDic(qi.toLowerCase());
            //if td null check for upper case
            if (td == null)
                td = indexer.getTermDataFromDic(qi.toUpperCase());//term data for current query word
            if (td == null)
                td = indexer.getTermDataFromDic(qi);//if has " " is the middle -> Money usually
            //if td not null
            if (td != null) {
                int totalTF = td.getTotalTF();
                double log = ((double)N/(double)totalTF);
                double idf = Math.log(log);
                queryVectorOfIdf.put(qi,idf);
            }
        }
    }


    /**
     * This method create a vector for all the document each qi exists in
     * For example: Document FBIS3-1235 and query Falkland petroleum exploration, Vector is: 010
     * @param qiAndDocs
     */
    private void initializeDocVector(HashMap<String,String> qiAndDocs){
        int current = 0;
        HashMap <String,String> copyOfDocVector = new HashMap<>();
        for(String qi: parsedQueryWordsWithSemanticsAndDesc) {
            String docs = qiAndDocs.get(qi);
            if (docs != null) {
                String[] splitted = docs.split(", ");
                for (String docAndTf : splitted) {
                    String docName = docAndTf.split("=")[0];
                    if (current == 0) {
                        docVector.put(docName, "1");
                    } else {
                        if (!docVector.containsKey(docName)) {
                            String value = "";
                            for (int i = 0; i < current; i++) {
                                value += "0";
                            }
                            docVector.put(docName, value + "1");
                        } else {
                            String value = docVector.get(docName);
                            value += "1";
                            docVector.put(docName, value);
                        }
                    }
                }
                for (String key : copyOfDocVector.keySet()) {
                    String value = docVector.get(key);
                    if (value.length() < current + 1) {
                        value += 0;
                        docVector.put(key, value);
                    }
                }
                copyOfDocVector = docVector;
                current++;
            }
        }
    }

    /**
     * This method calculate cosSim for a given doc
     * @param docName
     * @param lengthOfQuery
     * @return
     */
    private double calculateCossim(String docName,double lengthOfQuery) {
        String vectorOfCurrentDoc = docVector.get(docName);
        String[] vector_cordinates = vectorOfCurrentDoc.split(",");
        double length_of_document = 0, nominator = 0;
        int i = 0;
        for(String cur: vector_cordinates){
            if(cur.equals("")){
                continue;
            }
            double curDouble = Double.parseDouble(cur); //to double
            length_of_document = length_of_document + Math.pow(curDouble,2);
            String qi = parsedQueryWordsWithSemanticsAndDesc.get(i);
            while(!queryVector.containsKey(qi)){
                i++;
                qi = parsedQueryWordsWithSemanticsAndDesc.get(i);
            }
            double valueOfQuery = queryVector.get(qi);
            nominator = nominator + curDouble*valueOfQuery;
            i++;
        }
        length_of_document = Math.sqrt(length_of_document);
        double denominator = length_of_document*lengthOfQuery;
        double cosSim = nominator / denominator;
        return cosSim;
    }

    /**
     * This method calculate BM25
     * @param qiAndDocs - HashMap of all the qi and docs exists in
     */
    private void calculateBM25(HashMap<String, String> qiAndDocs) {
        //number of docs in the corpus
        double numberOfDocs = indexer.getNumberOfDocs();
        //if indexer not null
        if (indexer != null) {
            //for each word in the complex query
            for (String qi : parsedQueryWordsWithSemanticsAndDesc) {
                //check if not null
                if (qiAndDocs != null && qiAndDocs.get(qi) == null) {
                    continue; // if null means the word not in the corpus then continue
                }

                //get data about qi (A=1,B=2,C=3,D=4)
                String data = qiAndDocs.get(qi).toString();
                String[] docsAndTF = data.split(", ");
                double df = docsAndTF.length; //size of data is the df

                //for each docAndTF in docsAndTF we calculate score
                for (String docAndTF : docsAndTF) {
                    double score = 0;
                    String currentDoc = docAndTF.split("=")[0];
                    double tf = Double.parseDouble(docAndTF.split("=")[1]); //tf = f(qi,D)
                    double documentLength = indexer.getDocDataFromDic(currentDoc).getLength();
                    double idf = Math.log((numberOfDocs - df + 0.5) / (df + 0.5));//idf of qi
                    double nominator = tf * (k + 1);//nominator
                    double avgDl = indexer.getAvgdl();
                    double one = b * (documentLength / avgDl);
                    double denominator = tf + k * (1 - b + one);
                    double tmpScore = (idf * nominator) / denominator;
                    score += tmpScore;
                    //if doc doesn't exist - all new info
                    if (this.docAndScoreCalc.get(currentDoc) == null)
                        this.docAndScoreCalc.put(currentDoc, new Pair<>(score, 1));
                    else {//if doc exists - add to old info
                        Pair<Double, Integer> tmp = this.docAndScoreCalc.get(currentDoc);
                        double newScore = tmp.getKey() + score;
                        int newQiInDoc = tmp.getValue() + 1;
                        Pair<Double, Integer> newTmp = new Pair<>(newScore, newQiInDoc);
                        this.docAndScoreCalc.remove(currentDoc);//remove old value
                        this.docAndScoreCalc.put(currentDoc, newTmp);//insert new value
                    }
                }
            }
        }
    }

    /**
     * combination of BM25 and cosSim
     * BM25 Weight - 0.7 , cosSim - 0.3
     */
    public void finalScore(){
        initializeLengthOfQuery();
        HashMap<String,Pair<Double,Integer>> docAndScoreFinal = new HashMap<>();
        for(String docname:docAndScoreCalc.keySet()){
            double scorefromBM25 = (docAndScoreCalc.get(docname).getKey())/(double)(10);
            double scorefromCosSim = calculateCossim(docname,this.lengthOfQuery);
            double newScore = 0.95*scorefromBM25 + 0.05*scorefromCosSim;
            docAndScoreFinal.put(docname,new Pair<>(newScore,1));
        }
        this.docAndScoreCalc = docAndScoreFinal;
    }

    /**
     * This method initialize query length of vector
     */
    private void initializeLengthOfQuery() {
        double sum = 0;
        for(double value: queryVector.values()){
            sum += Math.pow(value,2);
        }
        this.lengthOfQuery = Math.sqrt(sum);
    }

    /**
     * This metod returns HashMap of each query word and his docs
     * @return HashMap of each query word and his docs
     */
    private HashMap<String, String> getqiAndDocs(){
        HashMap<String, String> qiAndDocs = new HashMap(); //first string is the qi, second string is the data (Yaniv,FBIS3=4)
        HashSet<String> qis = new HashSet<>();

        //add to qis all the words (union of semantic words and queryAfterParsing words)
        for(String qiSem: semantics) {
            if(!qiSem.equals(""))
                qis.add(qiSem);
        }
        for (String qi : originalParsedQuery) {
            if(!qi.equals("")) {
                qis.add(qi);
                //term data for current query word (lower case)
                TermData td = indexer.getTermDataFromDic(qi.toLowerCase());
                //if td null check for upper case
                if (td == null)
                    td = indexer.getTermDataFromDic(qi.toUpperCase());//term data for current query word
                if (td == null)
                    td = indexer.getTermDataFromDic(qi);//if has " " is the middle -> Money usually
                //if td not null
                if (td != null) {
                    //position for posting file
                    int position = td.getPosition();
                    //data from posting file
                    String lineData = getLine(position, qi);
                    //remove {}
                    lineData = lineData.substring(1, lineData.length() - 1);
                    qiAndDocs.put(qi, lineData);//qiAndDocs: qi = yaniv maor|<yaniv,A=1,B=2,C=3,D=4>|<maor,A=13,B=12,E=23,G=19>
                }
            }
        }
        return qiAndDocs;
    }
    /**
     * This method iterate over all the docs and set docAndScore array list
     * contains doc name and score
     */
    private void setdocAndScore(){
        Iterator it = this.docAndScoreCalc.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String docName = pair.getKey().toString();
            double score = (double)((Pair)pair.getValue()).getKey();
            int qiInDoc = (int)((Pair)pair.getValue()).getValue();
//                score *= ((double) qiInDoc / (double) queryAfterParsing.size());
            docAndScore.add(docName + "|" + new Double(score));
        }
    }


    /**
     * This method sort the lines in a temp posting file before writing to disk
     */
    private void sort() {
        docAndScore.sort((o1, o2) -> {
            String s1 = o1.substring(o1.indexOf('|') + 1, o1.length());
            String s2 = o2.substring(o2.indexOf('|') + 1, o2.length());
            Double s1D = Double.parseDouble(s1);
            Double s2D = Double.parseDouble(s2);
            return s2D.compareTo(s1D);
        });
    }

    /**
     * This method return the line of a given position from the posting files
     * @param position - given position
     * @param qi - the word
     * @return line
     */
    public String getLine(long position, String qi) {
        String fileName;
        Character firstChar = Character.toLowerCase(qi.charAt(0));
        //if digit
        if (!Character.isLetter(firstChar)) {
            fileName = "OTHER";
        } else {
            fileName = letters.get(firstChar);
        }
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
     * @return all the docs name after ranking (cutting the score)
     */
    public ArrayList<String> getDocs() {
        ArrayList<String> docs = new ArrayList<>();
        for (String s : docAndScore) {
            String sSub = s.substring(0, s.indexOf("|"));
            docs.add(sSub);
        }
        return docs;
    }
}
