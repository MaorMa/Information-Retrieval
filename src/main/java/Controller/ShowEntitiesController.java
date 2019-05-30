package Controller;

import java.util.ArrayList;

/**
 * This controller responsible to get the collection entities from the model
 */
public class ShowEntitiesController extends Acontroller{

    public ArrayList<String> getEntities() {
        return myModel.getEntities();
    }
}
