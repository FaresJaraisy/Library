package entity;

import java.io.Serializable;
import java.util.Date;
/**
 * The entity class that stores book copies.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class BookCopies implements Serializable{
	/**
	 * instance variables:
	 * book - the book for which the copy belongs to.
	 */
	private String barcode;
	private String catalogNumber;
	private Date purchaseDate;
	private enums.BookCopyStatus status;
	private Book book;
	/**
	 * a full-argument constructor
	 * @param barcode
	 * @param catalogNumber
	 * @param purchaseDate
	 * @param status
	 * @param book
	 */
	public BookCopies(String barcode,String catalogNumber,Date purchaseDate,enums.BookCopyStatus status,Book book) {
		
		this.barcode=barcode;
		this.catalogNumber=catalogNumber;
		this.purchaseDate=purchaseDate;
		this.status=status;
		this.book=book;
	}
	/**
	 * a constructor that initializes all attributes except book.
	 * @param barcode
	 * @param catalogNumber
	 * @param purchaseDate
	 * @param status
	 */
	public BookCopies(String barcode,String catalogNumber,Date purchaseDate,enums.BookCopyStatus status) {
		
		this.barcode=barcode;
		this.catalogNumber=catalogNumber;
		this.purchaseDate=purchaseDate;
		this.status=status;
	}
	/**
	 * a 2-argument constructor
	 * @param barcode
	 * @param catalogNumber
	 */
	public BookCopies(String barcode,String catalogNumber) {
		this.catalogNumber=catalogNumber;
		this.barcode=barcode;
	}
	/**
	 * a single-argument constructor.
	 * @param barcode
	 */
	public BookCopies(String barcode) {
		this.barcode=barcode;
	}
	/**
	 * an argument-less constructor
	 */
	public BookCopies() {
		
	}
	/**
	 * 
	 * @return
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * 
	 * @param barcode
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
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
	public enums.BookCopyStatus getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status
	 */
	public void setStatus(enums.BookCopyStatus status) {
		this.status = status;
	}
	/**
	 * 
	 * @return
	 */
	public Book getBook() {
		return book;
	}
	/**
	 * 
	 * @param book
	 */
	public void setBook(Book book) {
		this.book = book;
	}
}
