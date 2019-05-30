package Controller;

import Model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This controller is responsible for showing the Language in the GUI
 */
public class LanguageController extends Acontroller {

    public HashMap<String,String> getLanguageDictionary(){
        return myModel.getLanguageDictionary();
    }
}
