package View;

import Controller.SearchController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * This is a Search view class
 */
public class SearchView implements Initializable {

    public javafx.scene.control.ListView ResultsList;
    public javafx.scene.control.Label countLabel;

    SearchController searchController = new SearchController();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(searchController.getResults()!=null) {
            ArrayList<String> Results = searchController.getResults();
            ObservableList languageObservableList = FXCollections.observableArrayList();
            languageObservableList.addAll(Results);
            ResultsList.setItems(languageObservableList);
            countLabel.setText("Count: " +Results.size());
        }
    }

    public void showEntities() {
        String docName = ResultsList.getSelectionModel().getSelectedItem() + "";
        if(!docName.equals("null")) {
            searchController.setEntities(docName);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/ShowEntities.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 300, 200);
            } catch (IOException e) {
                //e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("Dominant Entities");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }
}
