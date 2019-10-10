package entity;

import java.io.Serializable;
import java.util.Date;

import enums.MessageType;

/**
 * The entity class that stores message objects.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Message implements Serializable{
	protected MessageType messageType;
	protected String title;
	protected String msg;
	protected String belong;
	protected Date messageDate;
	protected String user;
	/**
	 * 
	 * @return
	 */
	public Date getMessageDate() {
		return messageDate;
	}
	/**
	 * 
	 * @param messageDate
	 */
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}


	/**
	 * 
	 * @param belong
	 */
	public Message(String belong)
	{	
		this.belong=belong;
	}
	/**
	 * a full argument constructor
	 * @param messageType
	 * @param title
	 * @param mess
	 * @param belong
	 * @param messageDate
	 * @param user
	 */
	public Message(MessageType messageType,String title, String mess,String belong,Date messageDate,String user)
	{
		this.messageType=messageType;
		this.title=title;
		this.msg=mess;
		this.belong=belong;
		this.messageDate=messageDate;
		this.user=user;
	}
	/**
	 * 
	 * @return
	 */
	public MessageType getMessageType() {
		return messageType;
	}
	/**
	 * 
	 * @param messageType
	 */
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * @return
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * 
	 * @param msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * 
	 * @return
	 */
	public String getBelong() {
		return belong;
	}
	/**
	 * 
	 * @param belong
	 */
	public void setBelong(String belong) {
		this.belong = belong;
	}

	/**
	 * 
	 * @return
	 */
	public String getUser() {
		return user;
	}
	/**
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	
	
	
}
