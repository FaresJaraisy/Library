package entity;

import java.io.Serializable;
import java.util.ArrayList;

public class StatDataForLateReturnedReport implements Serializable{
	private String catalogNumber;
	private String bookName;
	private int totalNumberOfDurations;
	private ArrayList<Integer> durations;
	private int sumOfDuration;
	private int maxDuration;
	private double avgDuration;
	
	public void addToDuration(Integer d)
	{
		this.durations.add(d);
	}
	
	
	public StatDataForLateReturnedReport() {}

	public String getCatalogNumber() {
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public ArrayList<Integer> getDurations() {
		return durations;
	}

	public void setDurations(ArrayList<Integer> durations) {
		this.durations = durations;
	}

	public int getSumOfDuration() {
		return sumOfDuration;
	}

	public void setSumOfDuration(int sumOfDuration) {
		this.sumOfDuration = sumOfDuration;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}

	public double getAvgDuration() {
		return avgDuration;
	}

	public void setAvgDuration(double avgDuration) {
		this.avgDuration = avgDuration;
	}

	public int getTotalNumberOfDurations() {
		return totalNumberOfDurations;
	}

	public void setTotalNumberOfDurations(int totalNumberOfDurations) {
		this.totalNumberOfDurations = totalNumberOfDurations;
	}
	
	
}
