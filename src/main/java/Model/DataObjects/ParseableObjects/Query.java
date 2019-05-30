package Model.DataObjects.ParseableObjects;

/**
 * This class represents the Query parseable object
 */
public class Query implements IParseableObject {
    private String query;

    public Query(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}


