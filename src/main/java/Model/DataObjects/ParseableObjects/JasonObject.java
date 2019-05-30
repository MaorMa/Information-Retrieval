package Model.DataObjects.ParseableObjects;

import org.json.simple.JSONArray;

/**
 * This class represents a jason object
 */
public class JasonObject implements IParseableObject {
    private JSONArray jsonArray;
    public JasonObject(JSONArray jsonArray){
        this.jsonArray = jsonArray;
    }
    public JSONArray getJasonArray(){
        return this.jsonArray;
    }
}
