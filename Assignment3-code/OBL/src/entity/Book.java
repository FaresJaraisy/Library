package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
/**
 * The entity class that stores books.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Book implements Serializable {
	/**
	 * instance variables:
	 * 
	 */
	protected String catalogNumber;
	protected String title;
	protected String authorName;
	protected String publication;
	protected int numberOfCopies;
	protected Date purchaseDate;
	protected String locationOnShelf;
	protected MyFile tableOfContents;
	protected String description;
	protected enums.BookType bookType;
	protected ArrayList<String> categories;
	/**
	 * a full-argument constructor.
	 * @param CatalogNumber
	 * @param Title
	 * @param AuthorName
	 * @param publication
	 * @param numberOfCopies
	 * @param purchaseDate
	 * @param locationOnShelf
	 * @param tableOfContents
	 * @param Description
	 * @param Type
	 * @param categories
	 */
	public Book(String CatalogNumber, String Title, String AuthorName, String publication, int numberOfCopies,
			Date purchaseDate, String locationOnShelf, MyFile tableOfContents, String Description, enums.BookType Type,
			ArrayList<String> categories) {
		this.catalogNumber = CatalogNumber;
		this.title = Title;
		this.authorName = AuthorName;
		this.publication = publication;
		this.numberOfCopies = numberOfCopies;
		this.purchaseDate = purchaseDate;
		this.locationOnShelf = locationOnShelf;
		this.tableOfContents = tableOfContents;
		this.description = Description;
		this.bookType = Type;
		this.categories = categories;
	}
	/**
	 * a constructor that doesn't initialize the categories ArrayList
	 * @param CatalogNumber
	 * @param Title
	 * @param AuthorName
	 * @param publication
	 * @param numberOfCopies
	 * @param purchaseDate
	 * @param locationOnShelf
	 * @param tableOfContents
	 * @param Description
	 * @param Type
	 */
	public Book(String CatalogNumber, String Title, String AuthorName, String publication, int numberOfCopies,
			Date purchaseDate, String locationOnShelf, MyFile tableOfContents, String Description,
			enums.BookType Type) {
		this.catalogNumber = CatalogNumber;
		this.title = Title;
		this.authorName = AuthorName;
		this.publication = publication;
		this.numberOfCopies = numberOfCopies;
		this.purchaseDate = purchaseDate;
		this.locationOnShelf = locationOnShelf;
		this.tableOfContents = tableOfContents;
		this.description = Description;
		this.bookType = Type;
	}
	/**
	 * an argument-less constructor that only initializes the categories ArrayList.
	 */
	public Book() {
		this.categories = new ArrayList<>();
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
	 * @return
	 */
	public enums.BookType getBookType() {
		return bookType;
	}
	/**
	 * 
	 * @param bookType
	 */
	public void setBookType(enums.BookType bookType) {
		this.bookType = bookType;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getCategories() {
		return categories;
	}
	/**
	 * 
	 * @param categories
	 */
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	/**
	 * 
	 * @param catalogNumber
	 */
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * @return
	 */
	public String getAuthorName() {
		return authorName;
	}
	/**
	 * 
	 * @param authorName
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	/**
	 * 
	 * @return
	 */
	public String getPublication() {
		return publication;
	}
	/**
	 * 
	 * @param publication
	 */
	public void setPublication(String publication) {
		this.publication = publication;
	}
	/**
	 * 
	 * @return
	 */
	public int getNumberOfCopies() {
		return numberOfCopies;
	}
	/**
	 * 
	 * @param numberOfCopies
	 */
	public void setNumberOfCopies(int numberOfCopies) {
		this.numberOfCopies = numberOfCopies;
	}
	/**
	 * 
	 * @return
	 */
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	/**
	 * 
	 * @param purchaseDate
	 */
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	/**
	 * 
	 * @return
	 */
	public String getLocationOnShelf() {
		return locationOnShelf;
	}
	/**
	 * 
	 * @param locationOnShelf
	 */
	public void setLocationOnShelf(String locationOnShelf) {
		this.locationOnShelf = locationOnShelf;
	}
	/**
	 * 
	 * @return
	 */
	public MyFile getTableOfContents() {
		return tableOfContents;
	}
	/**
	 * 
	 * @param tableOfContents
	 */
	public void setTableOfContents(MyFile tableOfContents) {
		this.tableOfContents = tableOfContents;
	}
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 
	 * @return
	 */
	public enums.BookType getType() {
		return bookType;
	}
	/**
	 * 
	 * @param type
	 */
	public void setType(enums.BookType type) {
		this.bookType = type;
	}
}
