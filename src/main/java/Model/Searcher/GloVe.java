package Model.Searcher;
import java.io.*;
import java.util.*;

public class GloVe {
    private static HashMap<String, Vector> word2Vec;

    public GloVe() {
        File directory = new File("./");
        word2Vec = new HashMap(); //first string is the word, vector
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(directory.getAbsoluteFile() + "\\src\\main\\resources\\glove.txt"), "UTF-8"));
            String s = br.readLine();
            while (s != null) {
                String term = s.substring(0, s.indexOf(" "));
                Vector vec = stringToVec(s.substring(s.indexOf(" ")));
                word2Vec.put(term, vec);
                s = br.readLine();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    /**
     * 0.5453 0.6534 0.765 -> Vector
     *
     * @param s - string
     * @return
     */
    private Vector stringToVec(String s) {
        float x;
        Vector vec = new Vector();
        String[] split = s.split(" ");
        for (String num : split) {
            try {
                if (!num.isEmpty()) {
                    x = Float.parseFloat(num);
                    vec.add(x);
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return vec;
    }

    /**
     * A method that receives a term and returns a few term that are similar to it
     * @param s1 - the term
     * @return - Array list of terms that are similar to s1
     */
    public ArrayList<String> cosineSimilarity(String s1) {
        ArrayList<String> result = new ArrayList<>();
        Iterator it = word2Vec.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String term = pair.getKey().toString();
            if(term.equals(s1))
                continue;
            Vector vec1 = (Vector) pair.getValue();
            Vector vec2 = word2Vec.get(s1);
            if (vec2 == null)
                return null;
            float cosSim = vecMult(vec1, vec2) / (vecSize(vec1) * vecSize(vec2));
            if(cosSim >= 0.8) {
                result.add(term);
            }
        }
        return result;
    }

    /**
     * @param s1 - term 1
     * @param s2 - term 2
     * @return - the similarity between the two terms
     */
    public float cosineSimilarity(String s1, String s2) {
        Vector vec1 = word2Vec.get(s1);
        Vector vec2 = word2Vec.get(s2);
        if (vec2 == null)
            return 0;
        float cosSim = vecMult(vec1, vec2) / (vecSize(vec1) * vecSize(vec2));
        System.out.println(cosSim);
        return cosSim;
    }

    /**
     * Calc the size (length) of a vector
     * @param vec - vector
     * @return
     */
    private float vecSize(Vector vec) {
        float size = 0;
        for(int i=0; i<vec.size(); i++){
            size += Math.pow((float)vec.get(i),2);
        }
        size = (float)Math.sqrt(size);
        return size;
    }

    /**
     * Vec1 * vec2
     *
     * @param vec1
     * @param vec2
     * @return
     */
    private float vecMult(Vector vec1, Vector vec2) {
        float multResult = 0;
        if (vec1.size() != vec2.size())
            return 0;
        for (int i = 0; i < vec1.size(); i++) {
            multResult += (float) vec1.get(i) * (float) vec2.get(i);
        }
        return multResult;
    }

}


