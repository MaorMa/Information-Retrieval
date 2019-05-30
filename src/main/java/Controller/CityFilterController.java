package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This controller responsible to set the filter cities in the model
 * */
public class CityFilterController extends Acontroller {


    public HashMap<String, Integer> getCityDictionary() {
        return myModel.getCitysDictionary();
    }

    /**
     * Set filter cities
     * @param Cityfilter - the cities from the UI
     */
    public void setFilterCities(ArrayList<String> Cityfilter) {
        myModel.setFilterCities(Cityfilter);
    }
}
