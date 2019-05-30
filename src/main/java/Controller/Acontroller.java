package Controller;

import Model.Model;

/**
 * This controller is responsible to create a single object of model
 */
public class Acontroller {
    protected static Model myModel;

    public Acontroller(){
        if(myModel==null)
            myModel = new Model();
    }
}
