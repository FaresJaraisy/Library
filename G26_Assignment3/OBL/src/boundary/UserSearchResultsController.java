package boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import entity.Book;
import entity.BookWithButton;
import entity.MyFile;
import entity.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * {@inheritDoc}
 * @see boundary.ReaderSearchResultsController, very similar only difference is the addition of a "reserve" button in the book details window.
 */
public class UserSearchResultsController extends ReaderSearchResultsController{
	protected User currentUser;
	
	@Override
	public void setResults(ArrayList<Book> results) {
		this.results = new ArrayList<>();
		// set the onAction method for the buttons
		for (Book b : results) {
			String catalogNumber = b.getCatalogNumber();
			String title = b.getTitle();
			String authorName = b.getAuthorName();
			String publication = b.getPublication();
			int numberOfCopies = b.getNumberOfCopies();
			Date purchaseDate = b.getPurchaseDate();
			String locationOnShelf = b.getLocationOnShelf();
			MyFile tableOfContents = b.getTableOfContents();
			String description = b.getDescription();
			enums.BookType bookType = b.getType();
			ArrayList<String> categories = b.getCategories();
			BookWithButton book = new BookWithButton(catalogNumber, title, authorName, publication, numberOfCopies,
					purchaseDate, locationOnShelf, tableOfContents, description, bookType,categories);
			book.getViewDetailsBtn().setOnAction(e -> {
				try {
					viewDetailsHandler(b,currentUser);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			this.results.add(book);
		}
		data = FXCollections.observableArrayList(this.results);
		searchResultsTable.setItems(data);
		CNCol.setCellValueFactory(new PropertyValueFactory<Book, String>("catalogNumber"));
		titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
		shelfCol.setCellValueFactory(new PropertyValueFactory<Book, String>("locationOnShelf"));
		viewDetailsCol.setCellValueFactory(new PropertyValueFactory<Book, Button>("viewDetailsBtn"));
	}
	/**
	 * very similar to the method found in the super class, with a small tweak:
	 * loading the current user to know to whom to place the reservation.
	 * @param b - the book to load.
	 * @param u - the currently logged in user.
	 * @throws IOException - thrown should loading the FXML file fail.
	 */
	private void viewDetailsHandler(Book b,User u) throws IOException {
		Stage primaryStage = new Stage();
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/BookDetailsForUserGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Book Details");
		BookDetailsForUserController bdc = loader.getController();
		if(bdc.loadBook(b)) {
			bdc.loadUser(currentUser);
			primaryStage.show();
		}
	}
	public void loadUser(User u) {
		this.currentUser = u;
	}
}
