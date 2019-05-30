package Model.DataObjects;
/**
 * This class represents the date of each city
 * @countryName - country name of the city (for example New Zealand)
 * @population - population size of the country (for example: 1.32M)
 * @currency - currency of the country (for example: EUR
 */
public class CityData {
    private String countryName;
    private String population;
    private String currency;

    /**
     * C'tor
     */
    public CityData(String countryName, String population, String currency) {
        this.countryName = countryName;
        this.population = population;
        this.currency = currency;
    }

    /**
     * Getter
     * @return country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Getter
     * @return population size
     */
    public String getPopulation() {
        return population;
    }

    /**
     * Getter
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }
}
