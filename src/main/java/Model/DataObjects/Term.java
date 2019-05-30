package Model.DataObjects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represent the Term object
 * term - the name of the term
 * amountInDoc - HashMap of all the docs and the tf in the current doc
 */
public class Term implements Serializable{
    private String term;
    private HashMap<String,Integer> amountInDoc; //String=Doc name, Integer = tf
    private int df;
    private double dominance;
    private ArrayList<Integer> positions;

    /**
     * C'tor
     * @param term - term name
     */
    public Term(String term) {
        this.dominance = 0;
        this.term = term;
        this.amountInDoc = new HashMap<>();
        this.df = 0;
        this.positions = new ArrayList<>();
    }

    /**
     * this method increment the tf
     * @param docName - increment amount of term in a given document
     */
    public void incAmounts(String docName){
        incTf(docName);
    }

    public int getTf(String docName) {
        return this.amountInDoc.get(docName);
    }

    public String getName(){
        return this.term;
    }
    private void incTf(String docName) {
        if (amountInDoc.containsKey(docName))
            this.amountInDoc.replace(docName, this.amountInDoc.get(docName).intValue() + 1);
        else{
            this.amountInDoc.put(docName, 1);
            this.df++;
        }
    }

    @Override
    public String toString() {
        return  "'" + term + '\'' +
                ", amountInDoc=" + amountInDoc;
    }

    public void setTerm(String term){
        this.term = term;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public void addPositionInDoc(int position){
        this.positions.add(new Integer(position));
    }

    public void setDominance(double x){
        this.dominance=x;
    }

    public double getDominance(){
        return dominance;
    }
}
