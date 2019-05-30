package Model.DataObjects;

import java.io.Serializable;

/**
 * this class represents the doc data
 */
public class DocData implements Serializable{
    private int position;
    private int length;

    /**
     * C'tor
     * @param position - position in posting file
     * @param length - length of the doc
     */
    public DocData(int position, int length) {
        this.position = position;
        this.length = length;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
