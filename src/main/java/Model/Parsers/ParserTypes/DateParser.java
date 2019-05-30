package Model.Parsers.ParserTypes;

public class DateParser extends AParser {


    @Override
    /**
     *  Get month and another string- check if string is year>1000 or 1<=day<=31
     * @param s1 - month
     * @param s2 - year\day
     * @return - the correct version of the date - or if not date return month itself
     */
    protected String parsing(String s1, String s2, String s3, String s4) {
        if(isValidNum(s2)) {//s2 is a number
            if (isValidYear(s2)) {// s2 is a proper number year , . - just numbers
                i=1;
                return s2 + "-" + hmDate.get(s1);
            }
            else if ((s2.length() == 1 || s2.length() == 2) && !s2.equals("0")) {//the number is a day in a month and not 0
                i=1;
                int day = Integer.parseInt(s2);
                if (day < 10) {//if 1-9
                    return hmDate.get(s1) + "-0" + s2;
                }
                else if (day >= 10 && day <= 31) {//10-31
                    return hmDate.get(s1) + "-" + s2;
                }
            }
        }
        i=0;
        return s1;
    }

    private boolean isValidYear(String str) {
        if (str.length() == 0)
            return false;
        if (str.length() < 4)
            return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    /**
     *
     * @param s1 .length<=2
     * @param s2 is a month
     * @return - the correct version of the date - or if not date return day itself
     */
    protected String dayFirst(String s1, String s2,String s3){
        s1 = isValidDay(s1);
        if(s1.equals("0")) {
            i=0;
            return s1;
        }
        else{
            if(isValidYear(s3))
                i=0;
            else
                i=1;
            return hmDate.get(s2) + "-" + s1;
    }
    }

    /**
     *
     * @param s1 - the day
     * @return - the fixed day or 0 if not ok day
     */
    private String isValidDay(String s1){
        if (!s1.equals("0") || !s1.contains(".") || !s1.contains(",")) {//the number is a day in a month and not 0
            int day = Integer.parseInt(s1);
            if (day >= 1 && day<=31) {//if 1-31
                if (day < 10) {//if 1-9
                    return "0" + s1;
                }
                else if (day >= 10 && day <= 31) {//10-31
                    return s1;
                }
            }
        }
        return "0";
    }
}
