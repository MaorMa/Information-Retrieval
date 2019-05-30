package Model.Parsers.ParserTypes;

public class MoneyParser extends AParser {
    @Override
    protected String parsing(String s1, String s2, String s3, String s4) {
        return null;
    }

    /**
     * s1 = 1.7320, 450,000,
     *
     * @param s1 - price alone
     * @return - the correct way to show that price
     */
    protected String priceOnly(String s1) {
        s1 = addComa(s1);
        s1 = toMillionPrice(s1);
        return s1;
    }

    /**
     * s1 = 1.7320, 450,000
     * s2 = m,bn,tn,million,billion,trillion
     *
     * @param s1 - price alone
     * @param s2 - m,bn,tn,million,billion,trillion
     * @return - the correct way to show that price in M
     */
    protected String priceAndSize(String s1, String s2) {
        s1 = addComa(s1);
        s1 = fixSize(s1,s2);
        //s1 = toMillionPrice(s1);
        if(s1.contains(","))
            s1 = delComa(s1);
        return s1 + " " + "M";
    }

    /**
     * Make price the right size 100 million -> 100000000, 100.5 million -> 100000000.5
     * @param s1 - price
     * @param s2 - size
     * @return
     */
    private String fixSize(String s1, String s2) {
        String tmpS1 = s1;//number without dot
        if(s1.contains(".")){
            return toMillionPriceWithDot(s1,s2);
        }
        tmpS1 += hmPriceSize.get(s2);
        return tmpS1;
    }

    /**
     * 450,000,000, 1,000,000
     *
     * @param s1 - price with comas
     * @return - the correct representation
     */
    private String toMillionPrice(String s1) {
        boolean done = false;
        String tmpS1 = s1;
        String[] s1SplitDot=null;
        boolean containsDot = false;
        if(s1.contains(".")){
            containsDot = true;
            done = true;
            s1SplitDot = s1.split("\\.");
            //return toMillionPriceWithDot(s1);
            tmpS1 = s1SplitDot[0];
        }
        String[] tmp = tmpS1.split(",");
        if (tmp.length < 3)//if less then 1,000,000
        {
            return s1;
        }
        else {
            String s1Million = "";
            int count = 0;
            int j = tmp.length - 1;//the last array cell
            while (tmp[j].equals("000") && !done) {
                count++;
                j--;
                if (count == 2) {
                    done = true;
                    break;
                }
            }
            if (count != 2) {
                while (count != 2) {
                    s1Million = tmp[j] + s1Million;
                    count++;
                    j--;
                }
                s1Million = "." + s1Million;
            }
            for (; j >= 0; j--) {
                s1Million = tmp[j] + s1Million;
            }
            if(containsDot){
                s1Million += s1SplitDot[1];
            }
            return s1Million + " " + "M";
        }
    }

    /**
     *
     * @param s1 - the number with .
     * @param s2 - the billion/million/trillion
     * @return - the correct representative
     */
    private String toMillionPriceWithDot(String s1,String s2) {
        String[] s1SplitDot = s1.split("\\.");//[1000,5]
        String before = s1SplitDot[0];//1000
        String after = s1SplitDot[1];//5
        String zeroToAdd = (String)hmPriceSize.get(s2);
        int length = after.length();
        if(zeroToAdd.equals(""))
            return s1;
        if(length==1)//7.5
            return before + after + zeroToAdd.substring(1);
        if(length==2)//7.75
            return before + after + zeroToAdd.substring(2);
        if(length==3)//7.775
            return before + after + zeroToAdd.substring(3);
        else{//7.num>3
            return before + after.substring(0,3) + "." + after.substring(3);
        }
    }

    private String delComa(String s1) {
        String[] tmp = s1.split(",");
        String res = "";
        for(String cell: tmp){
            res += cell;
        }
        return res;
    }
}
