package Controller;

import java.util.ArrayList;

/**
 * This controller responsible to get the collection of ranked documents from the model
 */
public class SearchController extends Acontroller {

    /**
     * Get the results (ranked documents) for a query
     * @return
     */
    public ArrayList<String> getResults(){
        return myModel.getResults();
    }

    /**
     * This method set the entities for a given doc (give from the UI) we need to show
     * @param docName - the selected doc
     */
    public void setEntities(String docName) {
        myModel.setEntities(docName);
    }
}
