package entity;

import java.io.Serializable;
/**
 * an entity that stores reports in general, any specific report will extend this class.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Report implements Serializable{
	/**
	 * instance variables:
	 * name - the name of the report file
	 * path - the path of the file on the users' computer.
	 */
	private String name;
	private String path;
	/**
	 * a zero argument constructor.
	 */
	public Report() {}
	/**
	 * 
	 * @param name
	 */
	public Report(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}
	/**
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
