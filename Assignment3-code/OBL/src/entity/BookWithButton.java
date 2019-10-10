package entity;

import java.util.ArrayList;
import java.util.Date;

import javafx.scene.control.Button;
/**
 * a special entity class that stores books with the addition of a button, 
 * it's use is to display in a tableView.
 * the reason for creating this class is because JavaFX Button class is not {@code Serializable}.
 */
public class BookWithButton extends Book {
	private Button viewDetailsBtn;
	/**
	 * {@inheritDoc}
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
	public BookWithButton(String CatalogNumber, String Title, String AuthorName, String publication, int numberOfCopies,
			Date purchaseDate, String locationOnShelf, MyFile tableOfContents, String Description,
			enums.BookType Type,ArrayList<String> categories) {
		super(CatalogNumber, Title, AuthorName, publication, numberOfCopies, purchaseDate, locationOnShelf,
				tableOfContents, Description, Type,categories);
		this.viewDetailsBtn = new Button("View book details");
	}
	/**
	 * 
	 * @return
	 */
	public Button getViewDetailsBtn() {
		return viewDetailsBtn;
	}

}
