package Model;

/**
 * This class represents the model class.
 * responsible for start the indexing process
 */

import Model.DataObjects.TermData;
import Model.Indexers.Indexer;
import Model.Indexers.ReadFile;
import Model.Searcher.Searcher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @indexr - indexer object
 * @postingObject - posting object
 * @readFileObject - readFile object
 */
public class Model {
    Indexer indexer;
    Searcher searcher;
    ArrayList filterCities = new ArrayList();
    ArrayList entitiesToShow = new ArrayList();
    ArrayList<String> Totalresults = new ArrayList<>();


    /**
     * This method start indexing process
     *
     * @param pathOfCorpus  - the path of the corpus file
     * @param pathOfPosting - the path of the posting file
     * @param stem          - indicate if stemming required or not
     */
    public void startIndexing(String pathOfCorpus, String pathOfPosting, boolean stem) {
        indexer = new Indexer(pathOfCorpus, pathOfPosting, stem);
        try {
            long startTime = System.currentTimeMillis();
            indexer.init(new File(pathOfCorpus));
            this.saveDictionary(stem);//save dic auto
            long endTime = System.currentTimeMillis();
            //finish indexing
            double numberOfDocs = indexer.getNumberOfDocs();
            int termsCount = indexer.getUniqueTermsCount();
            Thread.sleep(2000);
            long totalTime = (endTime - startTime) / 1000;

            //show alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Finished!");
            alert.setHeaderText("Indexing process finished");
            alert.setContentText("Number of Docs: " + numberOfDocs + "\n"
                    + "Unique Terms count: " + termsCount + "\n"
                    + "Total Run time: " + totalTime + " Seconds");
            alert.showAndWait();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void saveDictionary(boolean stem) {
        indexer.saveDictionary(stem);
    }

    public void loadDictionary(String path, boolean stem) {
        indexer = new Indexer();
        try {
            indexer.loadDictionary(path, stem);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public HashMap<String, TermData> showDictionary() {
        if (indexer == null) {
            return null;
        } else {
            return indexer.getCorpusDictionary();
        }
    }

    public void reset() {
        indexer.reset();
    }

    public HashMap<String, String> getLanguageDictionary() {
        if (indexer == null) {
            return null;
        }
        return indexer.getLanguageDictionary();
    }

    public void runSingleQuery(String query,String query_num, String desc, boolean RankWithSemantic, boolean isStem){
        this.Totalresults = new ArrayList<>();
        runQuery(query,query_num,desc,RankWithSemantic,isStem);
    }

    public void runQuery(String query,String query_num, String desc, boolean RankWithSemantic, boolean isStem) {
        if (!(indexer == null)) {
            Searcher searcher = new Searcher(query, desc, indexer, filterCities, RankWithSemantic, isStem);
            searcher.search();
            this.searcher = searcher;
            for(String s: searcher.getResults()){
                Totalresults.add(query_num + " 0 " + s + " 0 42.38 mt" + "\n");
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Dictionary not loaded");
            alert.setContentText("Dictionary not loaded");
            alert.setContentText("Please load Dictionary File first or Index the corpus");
            alert.show();
        }
    }

    public ArrayList<String> getResults() {
        if(searcher!=null) {
            return this.searcher.getResults();
        }
        return null;
    }

    public ArrayList<String> getTotalresults(){
        return this.Totalresults;
    }

    public HashMap<String, Integer> getCitysDictionary() {
        if (indexer != null) {
            return indexer.getCityDictionary();
        }
        return null;
    }

    public void setFilterCities(ArrayList<String> filterCities) {
        this.filterCities = filterCities;
    }

    public void setEntities(String docName) {
        docName = docName.split("\\|")[0];
        BufferedReader br;
        entitiesToShow.clear();
        String pathOfPosting = indexer.getPathOfPosting();
        int position = indexer.getDocumentDictionary().get(docName).getPosition();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(pathOfPosting + "\\" + "DocumentPostingFile"), "UTF-8"));
            getTo(position, br);
            String line = br.readLine();
            String[] split = line.split("\\|");
            String[] entities = (split[4].substring(0, split[4].length() - 1)).split(",");
            for (String s : entities) {
                entitiesToShow.add(s);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void getTo(int position, BufferedReader br) {
        int length = 0;
        while (length < position) {
            try {
                String line = br.readLine();
                length += line.length() + 1;
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getEntities() {
        return entitiesToShow;
    }

    public void saveResultsToFile(String pathOfSavedQueries) {
        Writer mergedWriter;
        BufferedWriter mergedBuffer;
        if(this.getTotalresults().size() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nothing to save");
            alert.setHeaderText("There are 0 Results");
            alert.setContentText("There are 0 Results");
            alert.show();
        }else {
            try {
                mergedWriter = new OutputStreamWriter(new FileOutputStream(pathOfSavedQueries + "\\queryResults.txt"), StandardCharsets.UTF_8);
                mergedBuffer = new BufferedWriter(mergedWriter);
                File file = new File(pathOfSavedQueries + "\\queryResults.txt");
                if (file.exists()) {
                    file.delete();
                }
                for (String s : getTotalresults()) {
                    mergedBuffer.write(s);
                }
                mergedBuffer.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Results Saved successfully");
                alert.setHeaderText("Results Saved successfully");
                alert.setContentText("Results Saved successfully");
                alert.show();
            } catch (Exception e) {
//            e.printStackTrace();
            }
        }
    }

    public void runQueryFile(String pathOfQueries,boolean RankWithSemanticValue, boolean STEMM) {
        Totalresults = new ArrayList<>();
        if(!(indexer ==null)) {
            ReadFile readFile = new ReadFile();
            Document doc = Jsoup.parse(readFile.readFile(pathOfQueries));
            Elements queries = doc.select("top");
            HashSet<String> desc_stop_words = new HashSet<>();
            desc_stop_words.add("information");
            desc_stop_words.add("the");
            desc_stop_words.add("have");
            desc_stop_words.add("role");
            desc_stop_words.add("a");
            desc_stop_words.add("has");
            desc_stop_words.add("play");
            desc_stop_words.add("an");
            desc_stop_words.add("they");
            desc_stop_words.add("identify");
            desc_stop_words.add("are");
            desc_stop_words.add("them");
            desc_stop_words.add("instances");
            desc_stop_words.add("is");
            desc_stop_words.add("their");
            desc_stop_words.add("instance");
            desc_stop_words.add("and");
            desc_stop_words.add("he");
            desc_stop_words.add("documents");
            desc_stop_words.add("or");
            desc_stop_words.add("she");
            desc_stop_words.add("document");
            desc_stop_words.add("of");
            desc_stop_words.add("it");
            desc_stop_words.add("discuss");
            desc_stop_words.add("to");
            desc_stop_words.add("that");
            desc_stop_words.add("concern");
            desc_stop_words.add("from");
            desc_stop_words.add("those");
            desc_stop_words.add("provide");
            desc_stop_words.add("where");
            desc_stop_words.add("these");
            desc_stop_words.add("providing");
            desc_stop_words.add("when");
            desc_stop_words.add("by");
            desc_stop_words.add("background");
            desc_stop_words.add("who");
            desc_stop_words.add("then");
            desc_stop_words.add("issue");
            desc_stop_words.add("why");
            desc_stop_words.add("than");
            desc_stop_words.add("issues");
            desc_stop_words.add("how");
            desc_stop_words.add("associate");
            desc_stop_words.add("what");
            for (Element query : queries) {
                String query_num = query.select("num").text();
                query_num = query_num.substring(0, query_num.indexOf("  ")).split(" ")[1];
                String query_content = query.select("title").text();
                String query_desc = query.select("desc").text();
                query_desc = query_desc.split("Narrative:")[0];
                query_desc = query_desc.split(":")[1];
                String[] query_desc_words = query_desc.split(" ");
                String query_desc_words_string = "";
                for (String s : query_desc_words) {
                    if (desc_stop_words.contains(s)) {
                        continue;
                    }
                    query_desc_words_string += " " + s;
                }
                this.runQuery(query_content,query_num, query_desc_words_string, RankWithSemanticValue, STEMM);
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Search.fxml"));
                try {
                    Stage stage = new Stage();
                    stage.setTitle("Results For " + query_num);
                    stage.setScene(new Scene(fxmlLoader.load(), 590, 425));
                    stage.show();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Running Finished");
            alert.setHeaderText("Running Finished");
            alert.setContentText("All the results for the queries are open");
            alert.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Index not loaded");
            alert.setContentText("Please load Index File first");
            alert.show();
        }
    }
}
