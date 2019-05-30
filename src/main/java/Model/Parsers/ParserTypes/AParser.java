package Model.Parsers.ParserTypes;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;

/**
 */
public abstract class AParser {
    protected HashMap hmNum ;
    protected HashMap hmDate;
    protected HashSet hsDot ;
    protected HashMap hmPriceSize ;
    protected static int i;

    public AParser() {
        hmNum = new HashMap<String, String>();
        hmDate = new HashMap<String, String>();
        hsDot = new HashSet<String>();
        hmPriceSize = new HashMap<String, String>();
        //initialize hm
        hmPriceSize.put("million",""); hmPriceSize.put("billion","000"); hmPriceSize.put("trillion","000000");
        hmPriceSize.put("m",""); hmPriceSize.put("bn","000"); hmPriceSize.put("tn","000000");
        hmNum.put("Thousand","K"); hmNum.put("Million","M"); hmNum.put("Billion","B"); hmNum.put("Trillion","000B");
        hmNum.put("thousand","K"); hmNum.put("million","M"); hmNum.put("billion","B"); hmNum.put("trillion","000B");
        hmNum.put("k","K"); hmNum.put("m","M"); hmNum.put("b","B"); hmNum.put("bn","B"); hmNum.put("t","000B"); hmNum.put("tn","000B");
        hmDate.put("Jan","01"); hmDate.put("JAN","01"); hmDate.put("January","01");  hmDate.put("JANUARY","01");
        hmDate.put("Feb","02"); hmDate.put("FEB","02"); hmDate.put("February","02"); hmDate.put("FEBRUARY","02");
        hmDate.put("Mar","03"); hmDate.put("MAR","03"); hmDate.put("March","03");    hmDate.put("MARCH","03");
        hmDate.put("Apr","04"); hmDate.put("APR","04"); hmDate.put("April","04");    hmDate.put("APRIL","04");
        hmDate.put("May","05"); hmDate.put("MAY","05");
        hmDate.put("Jun","06"); hmDate.put("JUN","06"); hmDate.put("June","06");     hmDate.put("JUNE","06");
        hmDate.put("Jul","07"); hmDate.put("JUL","07"); hmDate.put("July","07");     hmDate.put("JULY","07");
        hmDate.put("Aug","08"); hmDate.put("AUG","08"); hmDate.put("August","08");   hmDate.put("AUGUST","08");
        hmDate.put("Sep","09"); hmDate.put("SEP","09"); hmDate.put("September","09");hmDate.put("SEPTEMBER","09");
        hmDate.put("Oct","10"); hmDate.put("OCT","10"); hmDate.put("October","10");  hmDate.put("OCTOBER","10");
        hmDate.put("Nov","11"); hmDate.put("NOV","11"); hmDate.put("November","11"); hmDate.put("NOVEMBER","11");
        hmDate.put("Dec","12"); hmDate.put("DEC","12"); hmDate.put("December","12"); hmDate.put("DECEMBER","12");
        hsDot.add(','); hsDot.add('.'); hsDot.add(':'); hsDot.add(';'); hsDot.add('|'); hsDot.add(' '); hsDot.add('"');
        hsDot.add('['); hsDot.add(']'); hsDot.add('*'); hsDot.add('\'');hsDot.add('+'); hsDot.add('"'); hsDot.add('\\');
        hsDot.add('?'); hsDot.add('-'); hsDot.add('&'); hsDot.add('`'); hsDot.add('!'); hsDot.add('/'); hsDot.add('#'); hsDot.add('@');
    }

    protected abstract String parsing(String s1, String s2, String s3, String s4);

    public Pair<Integer, String> parse(String s1, String s2, String s3, String s4){return null;}

    /**
     * Func that checks if String is number (10, 10.6, 10,234, 10,234.6)
     * Uses isNumeric to check individual chars in string
     * @param current - to check if valid number
     * @return - true \ false
     */
    protected boolean isValidNum(String current){
        if(current.equals(""))
            return false;
        if(current.contains("/") && !moreThenOne(current,'/'))//fraction
            return isValidFrac(current);
        String[] currSplit;
        if(isNumeric(current))//just number
            return true;
        else if(current.contains(".") && current.contains(",")){//10,456.6
            currSplit = current.split("[\\,\\.]");//[10,456,6] - check that all parts are numeric
            for(String tmp:currSplit){
                if(!isNumeric(tmp))
                    return false;
            }
            return true;
        }
        else if(current.contains(",") && !current.equals(",")){//number with ,
            currSplit = current.split(",");//[10,123] - check that all parts are numeric
            for(String s : currSplit){
                if(!isNumeric(s))
                    return false;
            }
            return true;
        }
        else if(current.contains(".")  && !moreThenOne(current,'.')){//number with .
            currSplit = current.split("\\.");//[10,6] - check that all parts are numeric
            for(String s : currSplit){
                if(!isNumeric(s))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param s1 fraction to check
     * @return - is top is valid num and bottom is valid num
     */
    protected boolean isValidFrac(String s1){
        String[]split = s1.split("/");
        if(split.length<2)
            return false;
        if(isNumeric(split[0]) && isNumeric(split[1]))
            return true;
        return false;
    }

    protected static boolean isNumeric(String str)
    {
        if(str.length()==0)
            return false;
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }


    public boolean moreThenOne(String str,char c){
        int count = 0;
        for (char curr : str.toCharArray())
        {
            if (curr == c)
                count++;
        }
        return count>1;
    }

    /**
     *
     * @param current - the current token to deal with
     * @return - the current token without clutter
     */
    protected String trimming(String current) {
        if (current.equals("") || current.equals(" ") || current.equals("$") || current.equals("-") || current.equals(",")
                || current.equals(".") || current.equals("%"))//if empty token
            return "";
        if(current.charAt(0)=='-' && !isValidNum(current.substring(1)))// if -word and not -number
            return trimming(current.substring(1));
        if (hsDot.contains(current.charAt(current.length() - 1))) {//if there is a sign at the end
            do {
                current = current.substring(0, current.length() - 1);
            } while (current.length() > 0 && hsDot.contains(current.charAt(current.length() - 1)));
            if (current.length() == 0) {
                return "";
            }
        }
        if ((hsDot.contains(current.charAt(0)) && current.charAt(0)!='-') || current.charAt(0)=='%') {//if there is a sign in the beginning
            do {
                current = current.substring(1);
            } while (current.length() > 0 && hsDot.contains(current.charAt(0)));
            if (current.length() == 0) {
                return "";
            }
        }
        return current;
    }

    /**
     * Check if number is with comas
     *
     * @param s1 - price
     * @return - price with comas
     */
    protected String addComa(String s1) {
        if (s1.length() > 3 && !s1.contains(",")) {//if price>1,000 and without ','
            if (s1.contains(".")) {//if number with .
                String[] tmp = s1.split("\\.");
                s1 = toComa(tmp[0]) + "." + tmp[1];
            } else//number without .
                s1 = toComa(s1);
        }
        return s1;
    }

    /**
     * @param current - number without coma
     * @return - the number with comas
     */
    private String toComa(String current) {
        String temp = "";
        int count = 0;
        for (int i = current.length() - 1; i >= 0; i--) {
            if (count == 3) {
                temp = ',' + temp;
                count = 0;
            }
            temp = current.charAt(i) + temp;
            count++;
        }
        return temp;
    }

}
