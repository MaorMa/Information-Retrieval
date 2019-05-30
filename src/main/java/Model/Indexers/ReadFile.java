package Model.Indexers;
import Model.DataObjects.CityData;
import Model.DataObjects.ParseableObjects.Doc;
import Model.Parsers.ParsingProcess.CityParsingProcess;
import Model.Parsers.ParsingProcess.IParsingProcess;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class read all files in a given path
 * @path - the path of the corpus
 * @root - root object
 */
public class ReadFile {
    private CityParsingProcess cityParsingProcess;

    /**
     * C'tor
     */
    public ReadFile() {
    }

    /**
     * This method take a file and separate each document to doc number, doc content, doc city.
     * after that, create a document object
     * @param file - file to separate to documents
     * return hash set of documents for the next station - parsing
     */
    public ArrayList<Doc> fromFileToDoc(File file) {
        this.cityParsingProcess = CityParsingProcess.getInstance();
        String path = file.toString();
        Document doc = Jsoup.parse(readFile(path));
        Elements docs = doc.select("DOC");
        Doc document;
        ArrayList<Doc> documents = new ArrayList<>();
        //loop throughout all the documents
        for (Element d: docs){
            String doc_num = d.select("DOCNO").text();
            String doc_content = d.select("TEXT").text();
            String doc_city = getCityName(d.select("F[P=104]").text());
            String language = getLang(d.select("F[P=105]").text());
            String doc_city_to_upper = doc_city.toUpperCase();
            int position;
            HashSet<Integer> positions = new HashSet<>();
            position = doc_content.indexOf(doc_city);
            if (!(doc_city.equals(""))) {
                while (position >= 0) {
                    positions.add(new Integer(position));
                    position = doc_content.indexOf(doc_city, position + 1);
                }
            }
            document = new Doc(path,doc_num,doc_content,doc_city_to_upper,positions,language);
            documents.add(document);
        }
        return documents;
    }

    private String getLang(String text) {
        if(text.equals("") || text.equals(" "))
            return "";
        String tmp = text.split(" ")[0];
        if(!Character.isDigit(tmp.charAt(0)))
            return tmp;
        else
            return "";
    }

    private String getCityName(String text) {
        if(text.equals("") || text.equals(" "))
            return "";
        String[] tmp = text.split(" ");
        if(tmp.length>=2)
            if(this.cityParsingProcess.checkIfExists(text))
                return (tmp[0] + " " + tmp[1]);
        for(String s : tmp){
            if(this.cityParsingProcess.checkIfExists(s))
                return s;
        }
        return "";
    }

    /**
     * C'tor
     * read file using bufferedReader
     * @param fileName - file to read
     * @return the content of the file
     * @throws IOException
     */
    public String readFile(String fileName) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        } catch (Exception e) {
//            e.printStackTrace();
        }
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}