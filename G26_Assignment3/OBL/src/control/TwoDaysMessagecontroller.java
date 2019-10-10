package control;

import java.util.TimerTask;

import entity.Reservations;
/**
 * This class is responsible for sending the users a 2 days notice when the reservation arrives.
 * {@code extends} TimerTask (@see java.util.TimerTask)
 */
class TwoDaysMessagecontroller extends TimerTask {
	/**
	 * instance variables:
	 * dbController - an instance of DBController to call the method that updates the DB.
	 * reserve - the reservation that arrived
	 * catalogNumber - the catalog number of the book reserved.
	 * realBarcode - the barcode of the book that arrived.
	 */
	DBController dbController;
	Reservations reserve;
	String catalogNumber;
	String realBarcode;

	/**
	 * constructor, sets the instance variables.
	 * @param dbController
	 * @param reserve
	 * @param catalogNumber
	 * @param realBarcode
	 */
	public TwoDaysMessagecontroller(DBController dbController, Reservations reserve, String catalogNumber,
			String realBarcode) {
		this.dbController = dbController;
		this.reserve = reserve;
		this.catalogNumber = catalogNumber;
		this.realBarcode = realBarcode;
	}
	/**
	 * the method that runs when the timer ends.
	 */
	public void run() {

		dbController.deleteReservation(reserve);
		dbController.sendMessageToNextOne(catalogNumber, realBarcode, reserve.getUserID());

	}

}