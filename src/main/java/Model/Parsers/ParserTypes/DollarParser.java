package Model.Parsers.ParserTypes;

public class DollarParser extends MoneyParser {

    @Override
    protected String parsing(String s1, String s2, String s3, String s4) {
        String price;
        if(s1.charAt(0)=='$' || s1.charAt(0) == '¥')
            price = s1.substring(1);//get the price out of $price
        else
            price = s1;
        if(!isValidNum(price)) {
            i=0;
            return "";
        }
        if(s2.equals("") || !hmPriceSize.containsKey(s2)) {
            i=0;
            if (s1.charAt(0) == '¥') {
                return priceOnly(price) + " " + "Yen";
            }
            else {
                return priceOnly(price) + " " + "Dollars";
            }
        }
        else if(s3.equals("")){
            i=1;
        }
        else if(s4.equals("")){
            i=2;
        }
        else{
            i=3;
        }
        if (s1.charAt(0) == '¥')
            return priceAndSize(price, s2) + " " + "Yen";
        return priceAndSize(price, s2) + " " + "Dollars";
    }

    /**
     *
     * @param s1 - valid number
     * @param s2 - the word Dollars
     * @return
     */
    public String PriceDollars(String s1, String s2){
        i=1;
        return priceOnly(s1) + " " + "Dollars";
    }

    /**
     *
     * @param s1 valid number
     * @param s2 fraction to check
     * @param s3 the word Dollars
     * @return
     */
    public String PriceFractionDollars(String s1, String s2, String s3){
        if(isValidFrac(s2)) {
            i=2;
            return priceOnly(s1) + " " + s2 + " " + "Dollars";
        }
        i=0;
        return s1;
    }

}
