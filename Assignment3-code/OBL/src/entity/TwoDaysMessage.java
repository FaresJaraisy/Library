package entity;

import java.util.Timer;
/**
 * an entity that store the book arrival messages, upon creation a timer is startes for 2 days.
 */
public class TwoDaysMessage {
	/**
	 * instance variables:
	 * timer - the timer that starts upon creation
	 * reservation - the reservation of the book.
	 * realBarcode - the bracode of the book copy that arrived.
	 */
	private Timer timer ;
	Reservations reservation;
	String realBarcode;
	/**
	 * 
	 * @return
	 */
	public String getRealBarcode() {
		return realBarcode;
	}
	/**
	 * 
	 * @param realBarcode
	 */
	public void setRealBarcode(String realBarcode) {
		this.realBarcode = realBarcode;
	}
	/**
	 * 
	 * @return
	 */
	public Timer getTimer() {
		return timer;
	}
	/**
	 * 
	 * @param timer
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	/**
	 * 
	 * @return
	 */
	public Reservations getReservation() {
		return reservation;
	}
	/**
	 * 
	 * @param reservation
	 */
	public void setReservation(Reservations reservation) {
		this.reservation = reservation;
	}

	
}