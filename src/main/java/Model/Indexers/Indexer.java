package Model.Indexers;

import Model.DataObjects.CityData;
import Model.DataObjects.DocData;
import Model.DataObjects.ParseableObjects.Doc;
import Model.DataObjects.Term;
import Model.DataObjects.TermData;
import Model.Parsers.ParsingProcess.DocParsingProcess;
import Model.Parsers.ParsingProcess.CityParsingProcess;
import Model.Parsers.ParsingProcess.IParsingProcess;
import javafx.scene.control.Alert;
import javafx.util.Pair;

import javax.swing.text.Document;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents the Indexer class
 * Indexer class create index for all the files
 *
 * @rootPath - path of the corpus
 * @readFileObject - read document content using ReadFile class
 * @DocumentsToParse - save a list of document to parse
 * @postingObject - a posting class object
 * @TermAndDocumentsData - save date in the given format <<String,HashMap<String,Integer>> "moshe | FBIS-3 - 5"
 * @toStem - boolean field indicates stemming or not
 * @parser - parser class object which parsing the document content
 */
public class Indexer {
    //paths
    private String rootPath;
    private String pathOfPosting;

    //objects
    private ReadFile readFileObject;
    private Posting postingObject;
    private DocParsingProcess ParserObject;
    private IParsingProcess cityParsingProcess;

    //dictionaries
    private HashMap<String, Integer> cityDictionary; //City name -> position in posting
    private HashMap<String, Pair<ArrayList<String>, CityData>> cityPostingData;// City -> cityData

    private HashMap<String, TermData> corpusDictionary; //term,totalTF,position in merged posting file

    private HashMap<String, String> LanguageDictionary;     //Language -> Docs

    private HashMap<String, DocData> DocumentDictionary; //DocumentName -> position
    private HashMap<String, Doc> DocumentPostingData; //DocumentName -> Doc object


    //other
    private ArrayList<Doc> DocumentsToParse;
    private int numberofDocs;
    private int totalDocLenth;
    private double avgdl;

    /**
     * C'tor
     */
    public Indexer() {
    }
//

    /**
     * C'tor
     *
     * @param rootPath      - the path of the corpus
     * @param pathOfPosting - the path of the posting
     * @param toStem        - if stem or not
     */
    public Indexer(String rootPath, String pathOfPosting, boolean toStem) {
        this.rootPath = rootPath;
        this.pathOfPosting = pathOfPosting;
        this.DocumentsToParse = new ArrayList<>();

        this.postingObject = new Posting(pathOfPosting);
        this.postingObject.resetPostingCounter();

        this.readFileObject = new ReadFile();
        this.corpusDictionary = new HashMap<>();
        this.DocumentDictionary = new HashMap<>();
        this.DocumentPostingData = new HashMap<>();
        this.cityPostingData = new HashMap<>();
        this.LanguageDictionary = new HashMap<>();
        this.cityDictionary = new HashMap<>();

        this.cityParsingProcess = CityParsingProcess.getInstance();
        this.ParserObject = new DocParsingProcess(rootPath, toStem);
        this.numberofDocs = 0;
        this.totalDocLenth = 0;
        this.avgdl = 0;
    }

    /**
     * this function initialize index
     * send all the docs to parsing
     * insert to TermAndDocumentsData structure and to the dictionary if need to update
     *
     * @param root - entry file
     *             this function send date to posting class which create the posting files
     */
    public void init(final File root) throws IOException {
        HashMap<String, HashMap<String, Integer>> TermAndDocumentsData = new HashMap<>();
        for (final File directory : root.listFiles()) {//for each folder in root
            if (directory.isDirectory())
                for (File currFile : directory.listFiles()) {//for each file in folder
                    DocumentsToParse = readFileObject.fromFileToDoc(currFile);
                    for (Doc d : DocumentsToParse) {
                        numberofDocs++;
                        //create document dictionary
                        if (!d.getLanguage().equals("")) {
                            addToLanguageDictionary(d);
                        }
                        //add to city dictionary
                        if (!d.getCity().equals("")) {
                            addToCityPosting(d);
                        }
                        ParserObject.parsing(d);
                        d.rankDominantTerms();
                        totalDocLenth += d.getLength();
                        //add to DocumentDictionary
                        Doc toInsert = new Doc(d.getPath(), d.getMax_tf(), d.getSpecialWordCount(), d.getMax_tf_String(),d.getCity(),d.getLength(),d.getDominantWords()); //doc to insert to the dictionary
                        DocumentPostingData.put(d.getDoc_num(), toInsert); //insert to dictionary (Doc name | Doc object)

                        for (Map.Entry<String, Term> entry : d.getTermsInDoc().entrySet()) {
                            String termName = entry.getKey();//term from doc
                            Term value = entry.getValue();
                            String doc_name = d.getDoc_num();
                            //if anything but Dollars - make Uppercase
                            if(Character.isDigit(termName.charAt(0)) && !termName.contains("Dollars") && !termName.contains("Yen"))
                                termName = termName.toUpperCase();
                            if (corpusDictionary.containsKey(termName)) {//Dic contains the term
                                updateDF(termName);
                                updateTF(termName, value.getTf(doc_name));
                                //check also if the first char is a letter (to filter only Words)
                            } else if (!termName.contains("Dollars") && !termName.contains("Yen") && corpusDictionary.containsKey(termName.toLowerCase())) {//if Dic has lowercase of this word
                                termName = termName.toLowerCase();
                                updateDF(termName);
                                updateTF(termName, value.getTf(doc_name));
                            } else if (!termName.contains("Dollars") && !termName.contains("Yen") && corpusDictionary.containsKey(termName.toUpperCase())) {
                                changeULDic(termName);
                                updateDF(termName);
                                updateTF(termName, value.getTf(doc_name));
                            } else {
                                corpusDictionary.put(termName, new TermData(1, value.getTf(doc_name), 0)); //term name, file name, position
                            }
                            if (TermAndDocumentsData.containsKey(termName)) {//term is in TermAndDocumentsData already
                                Integer newInt = new Integer(value.getTf(doc_name));
                                TermAndDocumentsData.get(termName).put(d.getDoc_num(), newInt);
                            } else if (TermAndDocumentsData.containsKey(termName.toLowerCase())) {//term name is upper, TADD contains lower
                                Integer newInt = new Integer(value.getTf(doc_name));
                                TermAndDocumentsData.get(termName.toLowerCase()).put(d.getDoc_num(), newInt);
                            } else if (TermAndDocumentsData.containsKey(termName.toUpperCase())) {//term is lowercase, TADD contains upper
                                HashMap<String, Integer> tmpHM = TermAndDocumentsData.get(termName.toUpperCase());
                                TermAndDocumentsData.remove(termName.toUpperCase());
                                TermAndDocumentsData.put(termName, tmpHM);
                                Integer newInt = new Integer(value.getTf(doc_name));
                                TermAndDocumentsData.get(termName).put(d.getDoc_num(), newInt);
                            } else {
                                HashMap<String, Integer> current = new HashMap();
                                current.put(doc_name, new Integer(value.getTf(doc_name)));
                                TermAndDocumentsData.put(termName, current);
                            }
                        }
                    }
                    postingObject.createTempPostingFile(TermAndDocumentsData);
                    TermAndDocumentsData.clear();
                }
        }
        this.avgdl = this.totalDocLenth / this.numberofDocs;
        DocumentDictionary.put("avgdl",new DocData(this.numberofDocs,this.totalDocLenth));
        createFinalPosting();
        writeCityPostingFile();
        addAllCitiesToDocs();
        //********************
        writeDocumentPostingFile();
    }

    /**
     * Add all cities from texts to correct docs
     */
    private void addAllCitiesToDocs() {
        for(String cityName:cityDictionary.keySet()){//s = city name
            TermData td = corpusDictionary.get(cityName);
            if(td==null)
                continue;
            int position = td.getPosition();
            String fileName = postingObject.getFileName(Character.toLowerCase(cityName.charAt(0)));
            String line = (getLine(position,fileName));
            line = line.substring(1,line.length()-1);
            String[]splitLine = line.split(", ");
            for(String docAndTF:splitLine){
                String doc = docAndTF.split("=")[0];
                Doc currDoc = DocumentPostingData.get(doc);
                currDoc.addCityFromText(cityName);
                DocumentPostingData.remove(doc);
                DocumentPostingData.put(doc,currDoc);
            }
        }
    }

    public String getLine(int position, String fileName) {
        try {
            RandomAccessFile rndFile = new RandomAccessFile(this.pathOfPosting + "\\" + fileName, "r");
            rndFile.seek(position);
            String line = rndFile.readLine();
            rndFile.close();
            return line;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "";
    }

    /**
     * This method add languages to language dictionary
     * @param d - the doc
     */
    private void addToLanguageDictionary(Doc d) {
        //if language dictionary contains the language
        if (this.LanguageDictionary.containsKey(d.getLanguage())) {
            String Docs = this.LanguageDictionary.get(d.getLanguage()); //get old value
            Docs = Docs + "|" + d.getDoc_num(); //update docs string
            this.LanguageDictionary.remove(d.getLanguage()); //remove the old key
            this.LanguageDictionary.put(d.getLanguage(), Docs); //add new key
        }
        //if language dictionary not contains the language
        else {
            this.LanguageDictionary.put(d.getLanguage(), d.getDoc_num());
        }
    }


    /**
     * @param termName - corpusDictionary contains this term
     */
    private void updateDF(String termName) {
        corpusDictionary.get(termName).incDF(1);
    }

    /**
     * Update totalTF in TD in the Dic
     *
     * @param plusTF   - the tf to add
     * @param termName - the term to update
     */
    private void updateTF(String termName, int plusTF) {
        corpusDictionary.get(termName).incTotalTF(plusTF);
    }

    /**
     * @param termName - lowercase of something in corpusDictionary that is uppercase
     */
    private void changeULDic(String termName) {
        TermData td = corpusDictionary.get(termName.toUpperCase());
        corpusDictionary.remove(termName.toUpperCase());
        corpusDictionary.put(termName, td);
    }

    /**
     * This method add city data from document to the city dictionary
     *
     * @param document - current document
     */
    public void addToCityPosting(Doc document) {
        if (!cityPostingData.containsKey(document.getCity())) { //if city not in the dictionary
            String city = document.getCity(); //get city name from the doc
            //remove spam
            if (city.contains("FOR") || city.contains("--") || city.equals("THE") || Character.isDigit(city.charAt(0))) {
                return;
            }
            String doc_num = document.getDoc_num(); // get doc name from the dock
            String positions_in_doc = document.getPositionOfCity().toString();
            CityData cityData = ((CityParsingProcess) cityParsingProcess).getData(city); //get data about the city
            ArrayList<String> list = new ArrayList<>();
            list.add(doc_num + ":" + positions_in_doc); //add doc name to array list with positions in doc
            Pair<ArrayList<String>, CityData> pair = new Pair<>(list, cityData); //insert data into new pair
            this.cityPostingData.put(city, pair); //insert to dictionary
        } else {
            //if city is in the dictionary
            Pair<ArrayList<String>, CityData> tmp = cityPostingData.get(document.getCity()); //get existing pair
            ArrayList<String> current = tmp.getKey(); //get array list from the pair
            CityData city_data = tmp.getValue(); //get city data from the pair
            current.add(document.getDoc_num() + ":" + document.getPositionOfCity());//add new doc and position in doc to the pair
            Pair<ArrayList<String>, CityData> newPair = new Pair<>(current, city_data); //create new pair with the additional data
            cityPostingData.replace(document.getCity(), newPair); //replace the existing data in the dictionary with the new data
        }
    }

    public void createFinalPosting() {
        postingObject.createFinalPosting(corpusDictionary);
        postingObject.splitFinalPosting(corpusDictionary);
    }

    public HashMap<String, TermData> getCorpusDictionary() {
        return corpusDictionary;
    }

    public HashMap<String, Integer>  getCityDictionary() {
        return cityDictionary;
    }

    public HashMap<String, DocData> getDocumentDictionary() {
        return DocumentDictionary;
    }

    public HashMap<String, String> getLanguageDictionary() {
        return this.LanguageDictionary;
    }


    /**
     * This method write document posting file
     */
    public void writeDocumentPostingFile() {
        Writer fw=null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(this.pathOfPosting + "\\" + "DocumentPostingFile"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        }
        int position = 0;
        int currentDocLength = 0;
        try {
            Iterator it = this.DocumentPostingData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String docName = pair.getKey().toString();
                Doc value = (Doc) pair.getValue();
                String path = value.getPath();
                String[]splitPath = path.split("\\\\");
                path = splitPath[splitPath.length-1];
                String maxTf = value.getMax_tf() + "";
                String specialWordCount = value.getSpecialWordCount() + "";
                String maxTfString = value.getMax_tf_String();
                String city = value.getCity();
                value.addCityFromText(city);
                HashSet<String>citiesInText = value.getCitiesInText();
                String citiesToWrite="",dominatToWrite="";
                for(String s:citiesInText){
                    citiesToWrite+= s+",";
                }
                currentDocLength = value.getLength();
                ArrayList<String> dominantWords = value.getDominantWords();

                for(String dominant:dominantWords){
                    dominatToWrite+= dominant+",";
                }

                String Data = docName + "|" + path + "|" + maxTf + "|" + specialWordCount + "|" + maxTfString +"|" + dominatToWrite + "|" + citiesToWrite;
                DocumentDictionary.put(docName, new DocData(position,currentDocLength));
                String toAdd = Data.substring(Data.indexOf("|") + 1);
                fw.write(toAdd +"\n");//***********Data->toAdd
                position += toAdd.length()+1;//*********Data->toAdd
                it.remove();
            }
            fw.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    /**
     * This method write city posting file
     */
    public void writeCityPostingFile() {
        int position = 0;
        try {
            ArrayList<String> s = new ArrayList();
            Iterator it = this.cityPostingData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String cityName = pair.getKey().toString();
                String alist = (((Pair) pair.getValue()).getKey()).toString();
                CityData c = (CityData) ((Pair) pair.getValue()).getValue();
                try {
                    if (c == null) {
                        s.add(cityName + "|" + alist + "|");
                    } else {
                        String data = c.getCountryName() + "|" + c.getPopulation() + "|" + c.getCurrency();
                        s.add(cityName + "|" + alist + "|" + data);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                it.remove();
            }
            s.sort(Comparator.naturalOrder());
            Writer fw = new OutputStreamWriter(new FileOutputStream(this.pathOfPosting + "\\" + "CityPostingFile"), StandardCharsets.UTF_8);
            for (String a : s) {
                String toAdd = a.substring(a.indexOf("|") + 1);
                cityDictionary.put(a.substring(0, a.indexOf("|")), new Integer(position));
                fw.write(toAdd + "\n");
                position += (toAdd.length()+1) ;
            }
            fw.close();
            s.clear();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    /**
     * This method save the dictionary to the disk
     *
     * @param stem - if stem or not
     */
    public void saveDictionary(boolean stem) {
        try {
            File corpusDicFile;
            File cityDicFile;
            File langDicFile;
            File docDicFile;
            corpusDicFile = new File(this.pathOfPosting + "\\" + "CorpusDictionary");
            cityDicFile = new File(this.pathOfPosting + "\\" + "CityDictionary");
            langDicFile = new File(this.pathOfPosting + "\\" + "LanguageDictionary");
            docDicFile = new File(this.pathOfPosting + "\\" + "DocumentDictionary");

            if (corpusDictionary.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Dictionary is empty");
                alert.setHeaderText("Dictionary is empty");
                alert.setContentText("Please run indexing to create the dictionary");
                alert.show();
            } else {
                //Corpus dic
                FileOutputStream fos = new FileOutputStream(corpusDicFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(corpusDictionary);
                oos.flush();
                fos.close();
                //city dic
                fos = new FileOutputStream(cityDicFile);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(cityDictionary);
                oos.flush();
                fos.close();
                //language dic
                fos = new FileOutputStream(langDicFile);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(LanguageDictionary);
                oos.flush();
                fos.close();
                //Document dic
                fos = new FileOutputStream(docDicFile);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(DocumentDictionary);
                oos.flush();
                oos.close();
                fos.close();
                //alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Dictionaries saved successfully");
                alert.setHeaderText("Dictionaries saved successfully");
                alert.setContentText("Dictionaries saved successfully");
                alert.show();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    /**
     * This method load the dictionary from the disk
     *
     */
    public void loadDictionary(String path, boolean stem) {
        this.pathOfPosting = path;
        try {
            try {
                if(stem) {
                    path += "\\withStem";
                    this.pathOfPosting = path;
                }
                //Corpus Dictionary
                FileInputStream fis = new FileInputStream(path + "\\CorpusDictionary");
                ObjectInputStream ois = new ObjectInputStream(fis);
                this.corpusDictionary = (HashMap<String, TermData>) ois.readObject();
                ois.close();
                fis.close();
                //City Dictionary
                fis = new FileInputStream(path + "\\CityDictionary");
                ois = new ObjectInputStream(fis);
                this.cityDictionary = (HashMap<String, Integer>) ois.readObject();
                ois.close();
                fis.close();
                //Language Dictionary
                fis = new FileInputStream(path + "\\LanguageDictionary");
                ois = new ObjectInputStream(fis);
                this.LanguageDictionary = (HashMap<String, String>) ois.readObject();
                ois.close();
                fis.close();
                //Document Dictionary
                fis = new FileInputStream(path + "\\DocumentDictionary");
                ois = new ObjectInputStream(fis);
                this.DocumentDictionary = (HashMap<String, DocData>) ois.readObject();
                this.numberofDocs = DocumentDictionary.get("avgdl").getPosition();
                this.totalDocLenth = DocumentDictionary.get("avgdl").getLength();
                this.avgdl = (double)this.totalDocLenth/(double)this.numberofDocs;
                ois.close();
                fis.close();
                //Alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Dictionaries Loaded successfully");
                alert.setHeaderText("Dictionaries Loaded successfully");
                alert.setContentText("Dictionaries Loaded successfully");
                alert.show();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public int getNumberOfDocs() {
        return numberofDocs;
    }

    public int getUniqueTermsCount() {
        return this.corpusDictionary.size();
    }

    public void reset() {
        if (corpusDictionary != null) {
            corpusDictionary.clear();
        }
        if (cityPostingData != null) {
            cityPostingData.clear();
        }
        if (DocumentDictionary != null) {
            DocumentDictionary.clear();
        }
        if (LanguageDictionary != null) {
            LanguageDictionary.clear();
        }
    }

    public double getAvgdl() {
        return avgdl;
    }

    public TermData getTermDataFromDic(String term){
        return corpusDictionary.get(term);
    }

    public DocData getDocDataFromDic(String doc){
        return DocumentDictionary.get(doc);
    }

    public String getPathOfPosting() {
        return pathOfPosting;
    }
}