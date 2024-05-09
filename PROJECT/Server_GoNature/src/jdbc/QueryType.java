package jdbc;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the types of SQL queries that can be performed.
 * This enumeration simplifies the differentiation between different types of SQL operations,
 * including INSERT, SELECT, and UPDATE actions.
 */
public enum QueryType {
	Insert ("Insert"),
	Select ("Select"),
	Update ("Update");
	
	private static final Map<String, QueryType> enumMap = new HashMap<>();
	
	// Static initializer used to populate a map with enum constants for quick lookup.
    static {
        for (QueryType queryType : QueryType.values()) {
            enumMap.put(queryType.name, queryType);
        }
    }
	
	private String name;
	
    /**
     * Private constructor for initializing the enum constants.
     * 
     * @param name The name of the query type (e.g., "Insert", "Select", "Update").
     */
	private QueryType(String name) {
		this.name=name;
	}
	
    /**
     * Overrides the default toString method to return the custom name of the query type.
     * 
     * @return The name of the query type.
     */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Retrieves the {@link QueryType} instance associated with the specified name.
	 * This method looks up an enumeration value in a predefined map ({@code enumMap}) based on the provided name. If the name is found, the corresponding {@link QueryType} is returned. If the name does not exist in the map, this method returns {@code null}.
	 * It is case-sensitive and expects the exact name of the enumeration value. Care should be taken to ensure the accuracy of the input name to avoid {@code null} results.
	 * @param name the name of the {@link QueryType} to retrieve. It must not be {@code null} and should exactly match one of the predefined names in the map.
	 * @return the {@link QueryType} associated with the given name, or {@code null} if the name does not match any {@link QueryType}.
	 */
    public static QueryType fromString(String name) {
        return enumMap.get(name);
    }
}
