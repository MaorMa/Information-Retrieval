import Model.DataObjects.ParseableObjects.Doc;
import Model.DataObjects.Term;
import Model.Parsers.ParsingProcess.DocParsingProcess;
import Model.Parsers.ParsingProcess.IParsingProcess;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maor on 11/7/2018.
 */
public class ParseTests {
    public static void main(String[] args){
        int pass = 0;
        int count = 0;
//
//        /**
//         * NUMBERS
//         */
        System.out.println("~*~ Numbers Tests ~*~");
        pass += test("1","Ministry's","1.0095K");
//        count +=1;
//        pass += test("0","10123","7500B");
//        count +=1;
//        pass += test("1","10,123.56","");
//        count +=1;
//        pass += test("0","10123.56","");
//        count +=1;
//        pass += test("0","10123 1/4","");
//        count +=1;
//        pass += test("1","123 Thousand","");
//        count +=1;
//        pass += test("1","123.5 Thousand","");
//        count +=1;
//        pass += test("2", "23 3/4","");
//        count +=1;
//        pass += test("3", "10,123,000,000.5","");
//        count +=1;
//        pass += test("3", "10123000000.5 ","");
//        count +=1;
//        pass += test("4","10123000000","13B");
//        count +=1;
//        pass += test("5","55 Million","19.");
//        count +=1;
//        pass += test("5","55.6 Million","19.");
//        count +=1;
//        pass += test("6","55.6 Billion","");
//        count +=1;
//        pass += test("7","55.6 Trillion","10.");
//        count +=1;
//        pass += test("8","1010.56","1.");
//        count +=1;
//
//        /**
//         * Percentage
//         */
//        System.out.println("~*~ Percentage Tests ~*~");
//        pass += test("1","6%","6%");
//        count +=1;
//        pass += test("2","10.6 percent","10.6%");
//        count +=1;
//        pass += test("3","6.5%","6.5%");
//        count +=1;
//        pass += test("4","10.6 percentage","10.6%");
//        count +=1;
//        pass += test("5","1000%","1000%");
//        count +=1;
//        pass += test("5","1,000.7%","1000%");
//        count +=1;
//        pass += test("5","1000.6%","1000.6%");
//        count +=1;
//
//        /**
//         * Prices
//         */
//        System.out.println("~*~ Price Dollars ~*~");
//        pass += test("1","1.7320 Dollars","1.7320 ");
//        count +=1;
//        pass += test("2","22 3/4 Dollars","22.26 ");
//        count +=1;
//        pass += test("3","395.7 Dollars","395.7 ");
//        count +=1;
//        pass += test("4","10,100 Dollars","10,100 ");
//        count +=1;
//        pass += test("5","10,100.2 Dollars","10,100.2 ");
//        count +=1;
//        pass += test("6","250,100 Dollars","250,100 ");
//        count +=1;
//        pass += test("7","$450,000","1,856 ");
//        count +=1;
//        pass += test("7","$450,000.6","1,856 ");
//        count +=1;
//        pass += test("7","$450000","1,856 ");
//        count +=1;
//        pass += test("7","¥450000.6","1,856 ");
//        count +=1;
//
//        System.out.println("~*~*~* Price fraction Dollars ~*~*~*");
//        pass += test("1","22 3/4 Dollars","22 3/4 ");
//        count +=1;
//        pass += test("2","252 3/4 Dollars","252 3/4 ");
//        count +=1;
//        pass += test("3","2,520 3/4 Dollars","2,520 3/4 ");
//        count +=1;
//        pass += test("4","122,520 3/4 Dollars","122,520 3/4 ");
//        count +=1;
//        pass += test("5","999999 1/8 Dollars","999999 1/8 "); //todo -fail
//        count +=1;
//        pass += test("6","999,999 1/8 Dollars","999,999 1/8 ");
//        count +=1;
//        pass += test("7","1/8 Dollars","1/8 "); //todo - fail
//        count +=1;
//
//        System.out.println("~*~*~* $price ~*~*~*");
//        pass += test("1","1000000 Dollars","450,000 ");
//        count +=1;
//        pass += test("2","1,000,000 Dollars","780,000 ");
//        count +=1;
//        pass += test("3","$780,000,000.7","780000 ");
//        count +=1;
//        pass += test("4","$10.5","10.5 ");
//        count +=1;
//        pass += test("5","$1","1 ");
//        count +=1;
//
//        System.out.println("~*~*~* $above million price ~*~*~*");
//        pass += test("1","100,000,999 Dollars","1 M ");
//        count +=1;
//        pass += test("2","$450,000,000","450 M ");
//        count +=1;
//        pass += test("3","$100 million","100 M ");
//        count +=1;
//        pass += test("4","200.6 m Dollars","20.6 M ");
//        count +=1;
//        pass += test("5","$200.8 billion","200000 M ");
//        count +=1;
//        pass += test("6","100 bn Dollars","100000 M ");
//        count +=1;
//        pass += test("6","100.8 bn Dollars","100000 M ");
//        count +=1;
//        pass += test("6","1,000.8 bn Dollars","100000 M ");
//        count +=1;
//        pass += test("7","100 billion U.S. dollars","100000 M ");
//        count +=1;
//        pass += test("8","320 million U.S. dollars","320 M ");
//        count +=1;
//        pass += test("9","1 trillion U.S. dollars","1000000 M ");
//        count +=1;
//
//        /**
//         * Date
//         */
//        System.out.println("~*~ Date Tests ~*~");
//        pass += test("1","14 MAY","05-");
//        count +=1;
//        pass += test("2","14 May","05-");
//        count +=1;
//        pass += test("3","JUNE 4","06-");
//        count +=1;
//        pass += test("4","June 5","06-");
//        count +=1;
//        pass += test("5","May 299","-05");
//        count +=1;
//        pass += test("6","MAY 1994","194-05");
//        count +=1;
//        pass += test("7","4 March","03-"); //check
//        count +=1;
//
//      /**
//       * Hyphen
//       */
//        System.out.println("~*~ Hyphen Tests ~*~");
//        pass += test("1","step-by-step","step-by-step");
//        count +=1;
//        pass += test("2","1-1","1-1");
//        count +=1;
//        pass += test("3","word-word,","XINHUA");
//        count +=1;
//        pass += test("4","word-word-word","XINHUA");
//        count +=1;
//        pass += test("5","1-word","XINHUA");
//        count +=1;
//        pass += test("6","word-1","XINHUA");
//        count +=1;
//        pass += test("7","1-2.5","yaniv");
//        count +=1;
//        pass += test("8","Value-added","Value-added");
//        count +=1;
//        pass += test("9","10-part","10-part");
//        count +=1;
//        pass += test("10","6-7","6-7");
//        count +=1;
//        pass += test("10","and","");
//        count +=1;
//        pass += test("11","between moshe and dana","moshe"); //todo not working
//        count +=1;
//        pass += test("12","between 30 and 40","between 30 and 40");
//        count +=1;
//        pass += test("13","between 18 and 24","between 18 and 24");
//        count +=1;
//        pass += test("14","between","");
//        count +=1;
//
//
//    /**
//     * More
//       */
//      System.out.println("~*~ More ~*~");
//        pass += test("0","Europe's","EUROPE");
//        count +=1;
//        pass += test("1","\"Europe's","EUROPE");
//        count +=1;
//        pass += test("2","...and",""); //retun null todo all good!
//        count +=1;
//        pass += test("3","-","");
//        count +=1;
//        pass += test("4","DASA'","DASA");
//        count +=1;
//        pass += test("5","\"\"","");
//        count +=1;
//        pass += test("6","Bosnia-Herzegovina'","BOSNIA-HERZEGOVINA");
//        count +=1;
//        pass += test("7","Coalition!","COALITION");
//        count +=1;
//        pass += test("8","+14","14");
//        count +=1;
//        pass += test("9","\"2020,\"","2.02K");
//        count +=1;
//        pass += test("10","/[Gao","GAO");
//        count +=1;
//        pass += test("11","year./","year");
//        count +=1;
//        pass += test("12","|1-12/93","1-12/93");
//        count +=1;
        pass += test("13","R3R3#R#r3R2R@r","5 1/5");
        count +=1;

        System.out.println("~*~ SUMMERY: PASS " + pass + "/" + (count) +" ~*~");
    }

    public static int test(String number, String input,String output) {
        IParsingProcess p = new DocParsingProcess("C:\\Users\\Maor\\Desktop\\corpus",false);
        Doc doc1 = new Doc();
        doc1.setDoc_content(input);
        p.parsing(doc1);
        HashMap<String,Term> termsReturn = doc1.getTermsInDoc();
        if(termsReturn.size() == 0 && output.length() !=0){
            System.out.println("TEST " + number + " FAILED| INPUT: " + input + "| OUTPUT: " + "| EXCEPTED: " + output);
            return 0;
        }
        else if(termsReturn.size() == 0 && output.length() ==0){
            System.out.println("TEST " + number + " PASS");
            return 1;
        }

        Map.Entry<String, Term> entry = termsReturn.entrySet().iterator().next();
        String result = entry.getKey();
        if (result.equals(output)) {
            System.out.println("TEST " + number + " PASS");
            return 1;
        } else {
            System.out.println("TEST " + number + " FAILED| INPUT: " + input + "| OUTPUT: " + result + "| EXCEPTED: " + output);
            return 0;
        }
    }
}
