package Model.Parsers.ParsingProcess;

import Model.DataObjects.ParseableObjects.IParseableObject;
import Model.DataObjects.ParseableObjects.Query;
import Model.Parsers.ParserTypes.ParserClassifier;
import Model.Parsers.ParserTypes.Stemmer;
import javafx.util.Pair;

import java.util.ArrayList;

/**
 * This class responsible for parsing the query
 */
public class QueryParsingProcess implements IParsingProcess {
    private ArrayList<String> queryAfterParsing;
    private boolean isStem;

    public QueryParsingProcess(boolean isStem) {
        this.queryAfterParsing = new ArrayList<>();
        this.isStem = isStem;
    }

    public ArrayList<String> getQueryAfterParsing() {
        return queryAfterParsing;
    }

    @Override
    public void parsing(IParseableObject IParseableObject) {
        Pair<Integer, String> currPair; //return pair of <Integer,String> Integer not interesting
        ParserClassifier parserClassifier = new ParserClassifier();
        String query = ((Query)IParseableObject).getQuery();
        String[] queryWords = query.split(" ");
        for (int i = 0; i < queryWords.length; i++) {
            String current = queryWords[i];
            if (i + 1 >= queryWords.length)//last token
                currPair = parserClassifier.parse(current, "", "", "");
            else if (i + 2 >= queryWords.length)//second before last token
                currPair = parserClassifier.parse(current, queryWords[i + 1], "", "");
            else if (i + 3 >= queryWords.length)//third before last token
                currPair = parserClassifier.parse(current, queryWords[i + 1], queryWords[i + 2], "");
            else//not close to last token
                currPair = parserClassifier.parse(current, queryWords[i + 1], queryWords[i + 2], queryWords[i + 3]);
            if (currPair == null)
                continue;
            else {
                i += currPair.getKey().intValue();//get i to raise by amount of tokenz used
            }
            if (isStem) {//if stem == true-> stem the term
                Stemmer stemmer = new Stemmer();
                stemmer.add(currPair.getValue().toCharArray(), currPair.getValue().length());
                stemmer.stem();
                String afterStemming = stemmer.toString();
                queryAfterParsing.add(afterStemming);
            }else {
                queryAfterParsing.add(currPair.getValue());
            }
        }
    }
}
