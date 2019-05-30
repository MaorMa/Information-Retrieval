package View;

import Controller.CityFilterController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

/**
 * This is a city Filter view Class
 * */
public class CityFilterView implements Initializable{
    @FXML
    public javafx.scene.control.ListView cityListView;
    public javafx.scene.control.Button AddCity;
    public javafx.scene.control.Button RemoveCity;
    public javafx.scene.control.Button Ok;
    public javafx.scene.control.ListView SelectedListView;

    CityFilterController cityFilterController=  new CityFilterController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HashMap<String,Integer> cityDictionary = cityFilterController.getCityDictionary();
        if(cityDictionary != null) {
            ArrayList<String> cities = new ArrayList<>(cityDictionary.keySet());
            cities.sort(Comparator.naturalOrder());
            ObservableList languageObservableList = FXCollections.observableArrayList();
            languageObservableList.addAll(cities);
            cityListView.setItems(languageObservableList);
        }
        ArrayList<String> selected = new ArrayList<>();
        //add city
        AddCity.setOnAction(event -> {
            if(cityListView.getSelectionModel().getSelectedItem()!=null) {
                selected.add(cityListView.getSelectionModel().getSelectedItem() + "");
                ObservableList languageObservableList = FXCollections.observableArrayList();
                languageObservableList.addAll(selected);
                SelectedListView.setItems(languageObservableList);
            }
        });

        //remove city
        RemoveCity.setOnAction(event -> {
            selected.remove(SelectedListView.getSelectionModel().getSelectedItem()+"");
            ObservableList languageObservableList = FXCollections.observableArrayList();
            languageObservableList.addAll(selected);
            SelectedListView.setItems(languageObservableList);
        });

        Ok.setOnAction(event -> {
            cityFilterController.setFilterCities(selected);
            Stage stage = (Stage) Ok.getScene().getWindow();
            stage.close();
        });


    }
}
