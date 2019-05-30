package View;

import Controller.DictionaryController;
import Model.DataObjects.TermData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.util.Pair;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
     * This class represents the dictionary View
 */
public class DictionaryView implements Initializable {

    /**
     * Fields
     */
    public javafx.scene.control.TableView DictionaryView;
    DictionaryController myDictionaryController = new DictionaryController();

    /**
     * initialize tableView of dictionary
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(myDictionaryController.showDictionary() != null) {
            HashMap<String, TermData> ourCorpusDictionary = myDictionaryController.showDictionary();

            TableColumn<Map.Entry<String, TermData>, String> column1 = new TableColumn<>("TERM");
            column1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));

            TableColumn<Map.Entry<String, TermData>, String> column2 = new TableColumn<>("DF");
            column2.setCellValueFactory(p -> new SimpleStringProperty((p.getValue().getValue().getDf()) + ""));

            TableColumn<Map.Entry<String, TermData>, String> column3 = new TableColumn<>("TotalTF");
            column3.setCellValueFactory(p -> new SimpleStringProperty((p.getValue().getValue().getTotalTF()) + ""));

            ObservableList<Map.Entry<String, TermData>> items = FXCollections.observableArrayList(ourCorpusDictionary.entrySet());
            DictionaryView.setItems(items.sorted());
            DictionaryView.getColumns().setAll(column1, column2, column3);
        }
    }
}
