package Controller;

import Model.DataObjects.TermData;
import Model.Model;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This controller is responsible for the GUI
 */
public class GUIController extends Acontroller {

    //call to model to start indexing
    public void startIndexing(String pathOfCorpus, String pathOfPosting, boolean stem){
        myModel.startIndexing(pathOfCorpus,pathOfPosting,stem);//
    }

    public void saveDictionry(boolean stem) {
        myModel.saveDictionary(stem);
    }

    public void loadDictionary(String path,boolean stem){
        myModel.loadDictionary(path, stem);
    }

    public HashMap<String, TermData> showDictionary() {
        return myModel.showDictionary();
    }

    public void reset() {
        myModel.reset();
    }

    public void runQuery(String query,String query_num,String desc, boolean RankWithSemantic,boolean isStem) {
        myModel.runSingleQuery(query,query_num,desc,RankWithSemantic,isStem);
    }

    public ArrayList<String> getTotalresults(){
        return myModel.getTotalresults();
    }

    public void saveResultsToFile(String saveResultsToFile) {
        myModel.saveResultsToFile(saveResultsToFile);
    }

    public void browseQueryFile(String pathOfQuerisFile,boolean semantics, boolean STEMM){
        myModel.runQueryFile(pathOfQuerisFile,semantics,STEMM);
    }

    public void resetCityFilter() {
        myModel.setFilterCities(new ArrayList<String>());
    }
}
