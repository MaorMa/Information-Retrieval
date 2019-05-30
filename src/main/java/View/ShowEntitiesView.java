package View;

import Controller.ShowEntitiesController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Maor on 12/17/2018.
 */
public class ShowEntitiesView implements Initializable {
    @FXML
            javafx.scene.control.ListView showEntitiesListView;
    ShowEntitiesController showEntitiesController = new ShowEntitiesController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(showEntitiesController.getEntities()!=null) {
            ArrayList<String> Results = showEntitiesController.getEntities();
            ObservableList entitesObservableList = FXCollections.observableArrayList();
            entitesObservableList.clear();
            entitesObservableList.addAll(Results);
            showEntitiesListView.setItems(entitesObservableList);
        }
    }
}
