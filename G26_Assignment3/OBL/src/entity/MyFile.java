package entity;

import java.io.Serializable;
/**
 * this class store a serialization of a file so it can be sent to the server and back.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class MyFile implements Serializable {
	/**
	 * instance variables:
	 * fileName - the name of the file (including file format - pdf,docx,etc.)
	 * size - the size of the file in bytes.
	 * mybytearray - a byte array that represents the serialization of the file.
	 */
	private String Description=null;
	private String fileName=null;	
	private int size=0;
	public  byte[] mybytearray;
	
	/**
	 * initializes the byte array
	 * @param size - the size of the array
	 */
	public void initArray(int size)
	{
		mybytearray = new byte [size];	
	}
	/**
	 * a single argument constructor that initializes the file name.
	 * @param fileName
	 */
	public MyFile( String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/**
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * 
	 * @return
	 */
	public byte[] getMybytearray() {
		return mybytearray;
	}
	/**
	 * 
	 * @param i
	 * @return
	 */
	public byte getMybytearray(int i) {
		return mybytearray[i];
	}
	/**
	 * 
	 * @param mybytearray
	 */
	public void setMybytearray(byte[] mybytearray) {
		
		for(int i=0;i<mybytearray.length;i++)
		this.mybytearray[i] = mybytearray[i];
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}	
}

