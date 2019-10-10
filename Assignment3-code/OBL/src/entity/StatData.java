package entity;

import java.io.Serializable;
import java.util.ArrayList;

public class StatData implements Serializable{
	private int numOfWantedBooks;
	private int sumOfDaysForWantedBooks;
	private int maxRangeForWantedBooks;
	private double avgOfWantedBooks;
	private int medianOfWantedBooks;
	private ArrayList<Integer> wantedArr;
	private int numOfRegularBooks;
	private int sumOfDaysForRegularBooks;
	private int maxRangeForRegularBooks;
	private double avgOfRegularBooks;
	private int medianOfRegularBooks;
	private ArrayList<Integer> regularArr;

	public StatData() {}

	public int getNumOfWantedBooks() {
		return numOfWantedBooks;
	}

	public void setNumOfWantedBooks(int numOfWantedBooks) {
		this.numOfWantedBooks = numOfWantedBooks;
	}

	public int getSumOfDaysForWantedBooks() {
		return sumOfDaysForWantedBooks;
	}

	public void setSumOfDaysForWantedBooks(int sumOfDaysForWantedBooks) {
		this.sumOfDaysForWantedBooks = sumOfDaysForWantedBooks;
	}

	public int getMaxRangeForWantedBooks() {
		return maxRangeForWantedBooks;
	}

	public void setMaxRangeForWantedBooks(int maxRangeForWantedBooks) {
		this.maxRangeForWantedBooks = maxRangeForWantedBooks;
	}

	public double getAvgOfWantedBooks() {
		return avgOfWantedBooks;
	}

	public void setAvgOfWantedBooks(double avgOfWantedBooks) {
		this.avgOfWantedBooks = avgOfWantedBooks;
	}

	public int getMedianOfWantedBooks() {
		return medianOfWantedBooks;
	}

	public void setMedianOfWantedBooks(int medianOfWantedBooks) {
		this.medianOfWantedBooks = medianOfWantedBooks;
	}

	public int getNumOfRegularBooks() {
		return numOfRegularBooks;
	}

	public void setNumOfRegularBooks(int numOfRegularBooks) {
		this.numOfRegularBooks = numOfRegularBooks;
	}

	public int getSumOfDaysForRegularBooks() {
		return sumOfDaysForRegularBooks;
	}

	public void setSumOfDaysForRegularBooks(int sumOfDaysForRegularBooks) {
		this.sumOfDaysForRegularBooks = sumOfDaysForRegularBooks;
	}

	public int getMaxRangeForRegularBooks() {
		return maxRangeForRegularBooks;
	}

	public void setMaxRangeForRegularBooks(int maxRangeForRegularBooks) {
		this.maxRangeForRegularBooks = maxRangeForRegularBooks;
	}

	public double getAvgOfRegularBooks() {
		return avgOfRegularBooks;
	}

	public void setAvgOfRegularBooks(double avgOfRegularBooks) {
		this.avgOfRegularBooks = avgOfRegularBooks;
	}

	public int getMedianOfRegularBooks() {
		return medianOfRegularBooks;
	}

	public void setMedianOfRegularBooks(int medianOfRegularBooks) {
		this.medianOfRegularBooks = medianOfRegularBooks;
	}

	public ArrayList<Integer> getWantedArr() {
		return wantedArr;
	}

	public void setWantedArr(ArrayList<Integer> wantedArr) {
		this.wantedArr = wantedArr;
	}

	public ArrayList<Integer> getRegularArr() {
		return regularArr;
	}

	public void setRegularArr(ArrayList<Integer> regularArr) {
		this.regularArr = regularArr;
	}
	
	
}
