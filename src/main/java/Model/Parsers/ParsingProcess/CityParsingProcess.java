package Model.Parsers.ParsingProcess;

import Model.DataObjects.CityData;
import Model.DataObjects.ParseableObjects.JasonObject;
import Model.DataObjects.ParseableObjects.IParseableObject;
import Model.Parsers.ParserTypes.NumberParser;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * responsible for parsing the city json file and save the required data about each city
 *
 * @cityData - contains the required data about each city
 */
public class CityParsingProcess implements IParsingProcess {
    private HashMap<String, CityData> cityData;
    private HashMap<String, Pair<String, String>> countryData;//country -> population, currency
    private JSONParser parser;
    private File directory;
    private HashMap<String, CityData> test = new HashMap<>();

    /**
     * C'tor
     * Get json file and call init
     */
    private CityParsingProcess() {
        this.cityData = new HashMap<>();
        this.countryData = new HashMap<>();
        this.parser = new JSONParser();
        this.directory = new File("./");
        try {
            Object obj = parser.parse(new FileReader(directory.getAbsoluteFile() + "\\src\\main\\resources\\cities.json"));
            JSONArray jsonArray = (JSONArray) obj;
            JasonObject JasonObject = new JasonObject(jsonArray);
            parsing(JasonObject);

            //cityAndCountry
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
//            e.printStackTrace();
        }
    }

    private static class CityParsingProcessHolder {
        private final static CityParsingProcess INSTANCE = new CityParsingProcess();
    }

    public static CityParsingProcess getInstance(){
        return CityParsingProcessHolder.INSTANCE;
    }


    /**
     * Initialize cityData HashMap
     */
    public void parsing(IParseableObject parseObject) {
        JSONArray jsonArray = ((JasonObject) parseObject).getJasonArray();
        for (Object s : jsonArray) {
            String cityName = (String) ((JSONObject) s).get("capital");
            String countryName = (String) ((JSONObject) s).get("name");
            Long population = (Long) ((JSONObject) s).get("population");
            String populationToString = population.toString();
            NumberParser numberParser = new NumberParser();
            String correctPopulation = numberParser.parsing(populationToString, "", "", "");
            String fixRound;
//            if (correctPopulation.contains(".") && correctPopulation.length() > 4) {
//                fixRound = roundby2digits(correctPopulation);
//            } else {
//                fixRound = correctPopulation;
//            }
            //correctPopulation = String.format(correctPopulation,"%.2f");
            JSONArray currencyArray = (JSONArray) ((JSONObject) s).get("currencies");
            String currency = (String) (((JSONObject) currencyArray.get(0)).get("code"));
            //add data about country
            countryData.put(countryName, new Pair<>(correctPopulation, currency));
            CityData data = new CityData(countryName, correctPopulation, currency);
            cityData.put(cityName, data);
            test.put(cityName, data);
        }

        JSONObject obj2 = null;
        try {
            obj2 = (JSONObject) parser.parse(new FileReader(directory.getAbsoluteFile() + "\\src\\main\\resources\\countriesAndCities.json"));
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        Iterator it = obj2.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry current = (Map.Entry) it.next(); //current entry in a loop
            String country = (String) current.getKey();
            ArrayList<String> cities = (ArrayList<String>) current.getValue();
            for (String city : cities) {
                if (countryData.get(country) != null) {
                    String populationData = countryData.get(country).getKey();
                    String currencyData = countryData.get(country).getValue();
                    cityData.put(city, new CityData(country, populationData, currencyData));
                }

            }
        }
    }

    public HashMap<String, CityData> getTest() {
        return test;
    }

    /**
     * Getter
     *
     * @param city - city to get data about
     * @return - CityData object
     */
    public CityData getData(String city) {
        //if city contains two words
        String correctCityName;
        if(city.contains(" ")){
            String[] parts = city.split(" ");
            String left = parts[0];
            String right = parts[1];
            String correctLeft = Character.toUpperCase(left.charAt(0)) + (left.substring(1,left.length())).toLowerCase();
            String correctRight = Character.toUpperCase(right.charAt(0)) + (right.substring(1,right.length())).toLowerCase();
            correctCityName = correctLeft + " " + correctRight;
        }else { //if city contains one word
            String secondParth = city.substring(1, city.length()).toLowerCase();
            correctCityName = Character.toUpperCase(city.charAt(0)) + secondParth;
        }
        return this.cityData.get(correctCityName);
    }

    /**
     *
     * @param s - name of city
     * @return - if name is in cityData
     */
    public boolean checkIfExists(String s){
        if(s.equals(""))
            return false;
        String[] tmp = s.split(" ");
        String correctCityName;
        String secondParth = tmp[0].substring(1, tmp[0].length()).toLowerCase();
        correctCityName = Character.toUpperCase(tmp[0].charAt(0)) + secondParth;
        if(tmp.length>1) {
            secondParth = tmp[1].substring(1, tmp[1].length()).toLowerCase();
            correctCityName += " " + Character.toUpperCase(tmp[1].charAt(0)) + secondParth;
        }
        return cityData.containsKey(correctCityName);
    }

}

