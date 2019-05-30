package Model.Parsers.ParserTypes;

public class NumberParser extends AParser {

    @Override
    /**
     * s1 - Valid number
     * s2 - Thousand\Million\Trillion\Billion OR ""
     */
    public String parsing(String s1, String s2, String s3, String s4) {
        if(s2.equals("")){//just number
            i=0;
            return numberOnly(s1);
        }
        else if(s2.contains("/")) {//number fraction
            i=1;
            return s1 + " " + s2;
        }
        else if(hmNum.containsKey(s2)){//s2 -> Thousand\Million\Trillion\Billion
            i=1;
            return numberAndSize(s1,s2);
        }
        else{
            i=0;
            return numberOnly(s1);
        }

    }

    /**
     *
     * @param s1 - valid number
     * @return - proper representation of number
     */
    private String numberOnly(String s1) {
        s1 = addComa(s1);
        String res = numberOnlyToWord(s1);
        return res;
    }

    private String numberAndSize(String s1, String s2) {
        i=1;
        String[]tmp;
        String afterDot="",s1Tr="",result;
        boolean contains = false;
        if(s1.contains(".")) {
            s1Tr = s1;
            contains = true;
            tmp = s1.split("\\.");
            s1 = tmp[0];
            afterDot = tmp[1];
        }
        if(contains) {//there is a .
            if (s2.equals("Trillion") || s2.equals("trillion")) {//s2 = trillion
                result = fixTrillion(s1Tr,s2);
            }
            else//s2 = all the rest
                result = s1 + "." + afterDot + hmNum.get(s2);
        }
        else
            result = s1 + hmNum.get(s2);

        return result;
    }

    /**
     * there is a .
     * @param s1 - valid number
     * @param s2 - trillion
     * @return
     */
    private String fixTrillion(String s1, String s2) {
        String[] split = s1.split("\\.");
        int length = split[1].length();
        String result="";
        if(length==1)//7.5
            return split[0] + split[1] + "00B";
        if(length==2)//7.75
            return split[0] + split[1] + "0B";
        if(length==3)//7.775
            return split[0] + split[1] + "B";
        else{//7.num>3
            result = split[0] + split[1].substring(0,3) + "." + split[1].substring(3) + "B";
        }
        return result;
    }

    /**
     * Func that turns numbers with coma into the right representative
     * @param s1 - the number with the coma (1,234 etc)
     * @return The correct representative of the number (1,234 -> 1.234K, 10,340 -> 10.34K)
     */
    private String numberOnlyToWord(String s1) {
        String[]tmp;
        String afterDot="";
        boolean contains = false;
        if(s1.contains(".")) {
            contains = true;
            tmp = s1.split("\\.");
            s1 = tmp[0];
            afterDot=tmp[1];
        }
        String[]num = s1.split(",");
        String result = "";
        boolean done = false;
        if(contains)
            done = true;
        for (int i = num.length - 1; i > 0; i--) {
            for (int j = num[i].length() - 1; j >= 0; j--) {
                if (num[i].charAt(j) != '0' && !done)
                    done = true;
                if (done) {
                    result = num[i].charAt(j) + result;
                }
            }
            if (i == 1 && done)
                result = '.' + result;
        }
        char letter = ' ';
        if (num.length == 2)
            letter = 'K';
        else if (num.length == 3)
            letter = 'M';
        else if (num.length == 4)
            letter = 'B';
        else
            letter = 0;
        if (letter == 0){
            if(contains)
                result = num[0] + result + "." + afterDot;
            else
                result = num[0] + result;
        }else {
            result = num[0] + result + afterDot + letter;
        }
        return result;
    }
}

