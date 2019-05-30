package Model.DataObjects;

import java.io.Serializable;

/**
 * This class save the data of each term for the corpus dictionary
 */
public class TermData implements Serializable{
    /**
     * Fields
     */
    private int df; // df in the corpus
    private int totalTF; //total df in the corpus
    private int position; //the position in the final posting files

    public TermData(int df, int totalTF, int position){
        this.df = df;
        this.totalTF = totalTF;
        this.position = position;
    }


    public int getTotalTF() {
        return totalTF;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDf() {
        return df;
    }

    public int getPosition() {
        return position;
    }

    public void incDF(int plusDF){
        this.df += plusDF;
    }

    public void incTotalTF(int plusTF){
        this.totalTF += plusTF;
    }
}
