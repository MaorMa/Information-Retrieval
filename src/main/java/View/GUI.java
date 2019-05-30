package View;
import Controller.GUIController;
import Model.DataObjects.ParseableObjects.Doc;
import Model.Indexers.ReadFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * This class represents the GUI of the engine
 */
public class GUI {

    /**
     * Fields
     * @myController - the controller
     * @corpusePathSelected - boolean flag for indicate if corpus path selected
     * @postingPathSelected - boolean flag for indicate if posting path selected
     * @pathOfCorpus - the path of the corpus
     * @pathOfPosting - the path of the posting
     */

    GUIController myGUIController = new GUIController();
    boolean corpusePathSelected = false;
    boolean postingPathSelected = false;
    boolean indexExists = false;
    String pathOfCorpus;
    String pathOfPosting;
    String pathOfSavedQueries = "";
    Scene cityFilterScene=null, corpusDictionary=null;

    @FXML
    public javafx.scene.control.Button LOAD;
    public javafx.scene.control.Button POSTING;
    public javafx.scene.control.Button START;
    public javafx.scene.control.Button saveDictionary;
    public javafx.scene.control.Button RESET;
    public javafx.scene.control.CheckBox STEMM;
    public javafx.scene.control.CheckBox RankWithSemantic;
    public javafx.scene.control.TextField CorpusPath;
    public javafx.scene.control.TextField PostingPath;
    public javafx.scene.control.TextField queryTextField;


    /**
     * get corpus path from browse button
     */
    public void loadCorpus(){
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("Set Corpus file Directory");
        File file = fc.showDialog(null);
        if (file != null) {
            corpusePathSelected = true;
            pathOfCorpus = file.getAbsolutePath();
            CorpusPath.setText(pathOfCorpus);
        }
    }

    /**
     * get posting path from browse button
     */
    public void loadPosting() {
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("Set Posting file Directory");
        File file = fc.showDialog(null);
        if (file != null) {
            postingPathSelected = true;
            pathOfPosting = file.getAbsolutePath();
            PostingPath.setText(pathOfPosting);
        }
    }

    /**
     * start indexing
     */
    public void startIndexing() {
		myGUIController.resetCityFilter();
        //if one of the browser buttons not clicked
        if(!corpusePathSelected || !postingPathSelected){
            //if both of the path text filed we need to start indexing
            if(!PostingPath.getText().equals("") && !CorpusPath.getText().equals("")){
                corpusePathSelected = true;
                postingPathSelected = true;
                pathOfCorpus = CorpusPath.getText();
                pathOfPosting = PostingPath.getText();
                //if stemming required
                if (STEMM.isSelected()) {
                    File directory = new File(pathOfPosting + "\\withStem");
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    //send to controller with stemming
                    Alert alert = showWindow();
                    myGUIController.startIndexing(pathOfCorpus, pathOfPosting, true);
                    indexExists=true;
                    alert.close();
                    saveDictionary.setDisable(false);
                    RESET.setDisable(false);
                }
                else{
                    //send to controller without stemming
                    Alert alert = showWindow();
                    myGUIController.startIndexing(pathOfCorpus, pathOfPosting, false);
                    indexExists=true;
                    alert.close();
                    saveDictionary.setDisable(false);
                    RESET.setDisable(false);
                }
            }
            //one of fields is empty
            else if(PostingPath.getText().equals("") || CorpusPath.getText().equals("")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText("One of field is empty");
                alert.setContentText("Please make sure all fields filled");
                alert.showAndWait();
            }
        }
        //if both browser buttons clicked
        else {
            //if stemming required
            if (STEMM.isSelected()) {
                File directory = new File(pathOfPosting + "\\withStem");
                //if directory of stemming not exists create one
                if (!directory.exists()) {
                    directory.mkdir();
                }
                //send to controller with stemming
                Alert alert = showWindow();
                myGUIController.startIndexing(pathOfCorpus, pathOfPosting + "\\withStem", true);
                indexExists=true;
                alert.close();
                saveDictionary.setDisable(false);
                RESET.setDisable(false);

            }
            //if stemming not required
            else{
                Alert alert = showWindow();
                myGUIController.startIndexing(pathOfCorpus, pathOfPosting, false);
                indexExists=true;
                alert.close();
                saveDictionary.setDisable(false);
                RESET.setDisable(false);
            }
        }
    }

    public void openLanguageList() {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/Language.fxml"));
        Scene scene=null;
        try{
            scene=new Scene(fxmlLoader.load(), 585, 400);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Stage stage=new Stage();
        stage.setTitle("Language List");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void reset() {
        //if posting path not give
        if(!postingPathSelected && pathOfPosting.equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Posting path is empty");
            alert.setContentText("Please make sure the field is filled for reset the posting folder");
            alert.showAndWait();
        }
        else {
            //delete all posting files
            File postingDirecory = new File(PostingPath.getText());
            for (File file : postingDirecory.listFiles()) {
                if(file.isDirectory()){
                    File[] files = file.listFiles();
                    for(File inner : files){
                        inner.delete();
                    }
                }
                file.delete();
            }
        }
        myGUIController.reset();
    }

    public void saveDictionary() {
        if (STEMM.isSelected()) {
            myGUIController.saveDictionry(true);
        }else{
            myGUIController.saveDictionry(false);
        }
    }

    public void loadDictionary() {
        if(PostingPath.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Path not specified");
            alert.setHeaderText("Path not specified");
            alert.setContentText("Please enter path in Posting path field");
            alert.show();
        }
        else {
            File file;
            if (!STEMM.isSelected()) {
                file = new File(PostingPath.getText() + "\\CorpusDictionary");
                if(!file.exists()){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Dictionary not exists");
                    alert.setHeaderText("Dictionary not exists");
                    alert.setContentText("Please run and save dictionary before");
                    alert.show();
                }
                else {
                    corpusDictionary=null;
                    myGUIController.loadDictionary(PostingPath.getText(),STEMM.isSelected());
                    indexExists=true;
                    myGUIController.resetCityFilter();
                }
            } else {
                file = new File(PostingPath.getText() + "\\withStem\\CorpusDictionary");
                if(!file.exists()){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Dictionary not exists");
                    alert.setHeaderText("Dictionary not exists");
                    alert.setContentText("Please run and save dictionary before");
                    alert.show();
                }else {
                    corpusDictionary=null;
                    myGUIController.loadDictionary(PostingPath.getText(),STEMM.isSelected());
                    indexExists=true;
                    myGUIController.resetCityFilter();
                }
            }
        }
    }

    private Alert showWindow(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Indexing starting...");
        alert.setHeaderText("Indexing start...");
        alert.setContentText("Indexing process start...");
        alert.setContentText("Indexing process start...\n"+"" +
                "Start indexing...");
        alert.show();
        return alert;
    }

    public void showDictionary() {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/Dictionary.fxml"));
        try{
            if(corpusDictionary==null)
                corpusDictionary=new Scene(fxmlLoader.load(), 580, 545);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Stage stage=new Stage();
        stage.setTitle("Corpus Dictionary");
        stage.setScene(corpusDictionary);
        stage.show();
    }

    public void runQuery() {
        String query = queryTextField.getText();
        boolean RankWithSemanticValue = RankWithSemantic.isSelected();
        if (query.equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Empty Query");
            alert.setHeaderText("Empty Query");
            alert.setContentText("Empty Query");
            alert.setContentText("Empty Query, please insert query or browse query file");
            alert.show();
        } else {
            myGUIController.runQuery(query, "1","", RankWithSemanticValue, STEMM.isSelected());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/Search.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 590, 425);
            } catch (IOException e) {
                //e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("Results");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void cityFilter() {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/CityFilter.fxml"));
        try{
            if(cityFilterScene==null || indexExists) {
                cityFilterScene = new Scene(fxmlLoader.load(), 750, 500);
                indexExists=false;
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Stage stage=new Stage();
        stage.setTitle("City Filter");
        stage.setScene(cityFilterScene);
        stage.show();
    }

    public void savedQueries(){
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("Set Saved Queries path");
        File file = fc.showDialog(null);
        if (file != null) {
            pathOfSavedQueries = file.getAbsolutePath();
            ArrayList<String> results = myGUIController.getTotalresults();
            if (results != null) {
                myGUIController.saveResultsToFile(pathOfSavedQueries);
            }
        }
    }

    public void browseQueryFile() {
        boolean RankWithSemanticValue = RankWithSemantic.isSelected();
        FileDialog dialog = new FileDialog((Frame) null, "Select Query file to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        if (dialog.getDirectory() == null || dialog.getFile() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File not found Error");
            alert.setHeaderText("File not found Error");
            alert.setContentText("File not found Error");
            alert.setContentText("Please choose a queries file");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Run Queries");
            alert.setHeaderText("Running...");
            alert.setContentText("Running...");
            alert.setContentText("Running...");
            alert.show();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            alert.close();
            String pathOfQuerisFile = dialog.getDirectory() + dialog.getFile();
            myGUIController.browseQueryFile(pathOfQuerisFile, RankWithSemanticValue, STEMM.isSelected());
        }
    }
}
