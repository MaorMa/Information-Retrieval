package View;

import Controller.LanguageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * This class represents the language view
 */
public class LanguageView implements Initializable{
    @FXML
    public javafx.scene.control.ListView langugageView;

    //fields
    LanguageController myLanguageController = new LanguageController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(myLanguageController.getLanguageDictionary()!=null) {
            HashMap<String, String> languageCollection = myLanguageController.getLanguageDictionary();
            ObservableList languageObservableList = FXCollections.observableArrayList();
            languageObservableList.addAll(languageCollection.keySet());
            langugageView.setItems(languageObservableList);
        }
    }
}
