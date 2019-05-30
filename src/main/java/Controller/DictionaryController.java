package Controller;

import Model.DataObjects.TermData;
import javafx.util.Pair;

import java.util.HashMap;

/**
 * This controller is responsible for showing the Dictionary in the GUI
 */
public class DictionaryController extends Acontroller {
    public HashMap<String, TermData> showDictionary() {
        return myModel.showDictionary();
    }
}
