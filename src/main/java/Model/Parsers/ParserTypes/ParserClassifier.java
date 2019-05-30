package Model.Parsers.ParserTypes;

import javafx.util.Pair;

public class ParserClassifier extends AParser {
//    private Pair<Integer,String>doneTerm;
    private NumberParser numberParser;
    private DollarParser dollarParser;
    private DateParser dateParser;
    private PercentParser percentParser;

    public ParserClassifier() {
        numberParser = new NumberParser();
        dollarParser = new DollarParser();
        dateParser = new DateParser();
        percentParser = new PercentParser();
    }

    //If term that got back is "" we will return null and the DocParsingProcess will continue
    public Pair<Integer, String> parse(String s1, String s2, String s3, String s4){
        String term = parsing(s1, s2, s3, s4);
        if(term.equals(""))
            return null;
        return new Pair<>(i,term);
    }

    protected String parsing(String s1, String s2, String s3, String s4){
        String current = s1;
        current = trimming(current);
        if(current.equals("")) {
            i=0;
            return "";
        }
        String currValue;
        //Contains '-' VVV
        if (current.contains("-")) {
            i=0;
            if(current.charAt(0)=='-' && isValidNum(current.substring(1))){//-number
                currValue = current;
            }
            else{//10-part,6-7
                currValue = current;
            }
        }
        //"Between xyz and abc" (xyz,abc = number) VVV
        else if ((s1.equals("Between") || s1.equals("between")) && isValidNum(s2) && s3.equals("and") && isValidNum(s4)) {
            currValue = current + " " + trimming(s2) + " " + s3 + " " + trimming(s4);
            i = 3;
        }
        //$
        else if (current.charAt(0) == '$' || current.charAt(0) == '¥') {//if first char is '$' or '¥'
            if (s2.equals(""))//$price
                currValue = dollarParser.parsing(current, "","","");
            else//$price million\billion\trillion
                currValue = dollarParser.parsing(current, s2,"","");
        }
        //number first
        else if (isValidNum(current)) {//check if token is a valid number - todo
            if (s2.equals(""))
                currValue = numberFirst(current, "", "", "");
            else if (s3.equals(""))
                currValue = numberFirst(current, s2, "", "");
            else if (s4.equals(""))
                currValue = numberFirst(current, s2, s3, "");
            else
                currValue = numberFirst(current, s2, s3, s4);
        }
        //% VVV
        else if (current.contains("%")) {//%6 etc' VVV
            currValue = percentParser.parsing(current,"","","");
        }
        //First token is Month VVV
        else if (hmDate.containsKey(current)) {
            if (s2.equals("")) {//if only Month
                currValue = current;
                i=0;
            }
            else {//s2 = year\day
                currValue = dateParser.parsing(current, trimming(s2),"","");
            }
        }
        else if(checkRiskyEnding(current)){
            i=0;
            currValue = current.toUpperCase();
        }
        //check if word with 's
        else {
            i=0;
            current = apostropheS(current);
            currValue = current;
        }
        return currValue;
    }

    private boolean checkRiskyEnding(String current) {
        String left = current.substring(0,current.length()-1);
        char right = current.charAt(current.length()-1);
        if((right=='m' || right=='k' || right=='b') && isValidNum(left))
            return true;
        return false;
    }

    /**
     * @param s1 - valid number
     * @param s2 - second token
     * @param s3 - third token
     * @param s4 - fourth token
     * @return
     */
    private String numberFirst(String s1, String s2, String s3, String s4) {
        if(s1.length()<=2 & hmDate.containsKey(trimming(s2))){//the second token is a month
            return dateParser.dayFirst(s1,trimming(s2),trimming(s3));
        }
        else if(s2.equals("percentage") || s2.equals("percent") || s2.equals("Percent") || s2.equals("Percentage")){//the second word is percent\percentage
            return percentParser.parsing(s1,s2,"","");
        }
        else if (s2.equals("Dollars") || s2.equals("dollars")){//valid number Dollars
            return dollarParser.PriceDollars(s1,s2);
        }
        else if(s2.contains("/")){//valid number fraction ...
            if(s3.equals("Dollars") || s3.equals("dollars"))//valid number fraction Dollars
                return dollarParser.PriceFractionDollars(s1,trimming(s2),s3);
            else//valid number fraction only
                return numberParser.parsing(s1,trimming(s2),"","");
        }
        else if(hmPriceSize.containsKey(s2)){//valid number million\billion\trillion\m\bn\tn
            if(s3.equals("Dollars") || s3.equals("dollars")) {//...Dollars
                i = 2;
                return dollarParser.parsing(s1, s2,s3,"");
            }
            else if((s3.equals("U.S.") && s4.equals("dollars"))){//...U.S Dollars
                i = 3;
                return dollarParser.parsing(s1, s2,s3,s4);
            }
            return s1;
        }
        else if(hmNum.containsKey(s2)){//valid number Thousand\Million\Trillion\Billion
            return numberParser.parsing(s1,s2,"","");
        }
        else{
            return numberParser.parsing(s1,"","","");
        }
    }

    /**
     * Return a token without "'s"
     * @param current - the current token to check
     * @return
     */
    private String apostropheS(String current) {
        if(current.length()<2)
            return current;
        if(current.charAt(current.length()-1)=='s' && current.charAt(current.length()-2)=='\'')
            current = current.substring(0,current.length()-2);
        return current;
    }
}
