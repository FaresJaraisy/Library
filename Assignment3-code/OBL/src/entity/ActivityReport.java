package entity;

import java.io.Serializable;
import java.sql.Date;
/**
 * {@inheritDoc}
 * The entity class that stores Activity Reports, a type of report.
 * {@code extends} {@link entity.Report} because it's a type of report, 
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class ActivityReport extends Report implements Serializable{
	/**
	 * instance variables:
	 * fromDate - the start date of the report.
	 * toDate - the end date of the report.
	 * file - a serializable file that contains the report.
	 */
	private java.sql.Date fromDate;
	private java.sql.Date toDate;
	private MyFile file;
	
	/**
	 * {@inheritDoc}
	 */
	public ActivityReport() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * @param fromDate - the start date of the report.
	 * @param toDate - the end date of the report.
	 */
	public ActivityReport(java.sql.Date fromDate, java.sql.Date toDate) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	/**
	 * 
	 * @return the start date of the report
	 */
	public java.sql.Date getFromDate() {
		return fromDate;
	}

	/**
	 * 
	 * @param fromDate - the start date of the report
	 */
	public void setFromDate(java.sql.Date fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * 
	 * @return the end date of the report.
	 */
	public java.sql.Date getToDate() {
		return toDate;
	}


	/**
	 * 
	 * @param toDate - the end date of the report.
	 */
	public void setToDate(java.sql.Date toDate) {
		this.toDate = toDate;
	}


	/**
	 * 
	 * @return a serializable file that contains the report.
	 */
	public MyFile getFile() {
		return file;
	}


	/**
	 * 
	 * @param file - a serializable file that contains the report.
	 */
	public void setFile(MyFile file) {
		this.file = file;
	}
}
