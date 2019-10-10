package entity;
/**
 * The entity class that stores categories.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Categories {
	/**
	 * instance variables
	 */
	private String categoryName;
	private String catalogNumber;
	/**
	 * a full argument constructor
	 * @param categoryName
	 * @param catalogNumber
	 */
	public Categories(String categoryName,String catalogNumber) {
		
		this.categoryName=categoryName;
		this.catalogNumber=catalogNumber;
		
	}
	/**
	 * 
	 * @return
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * 
	 * @param categoryName
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * 
	 * @return
	 */
	public String getCatalogNumber() {
		return catalogNumber;
	}
	/**
	 * 
	 * @param catalogNumber
	 */
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

}
