// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package control;

import ocsf.client.*;
import java.io.*;

import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
import entity.User;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 */
public class Client extends AbstractClient {
	/**
	 * instance variables:
	 * iClient - an instance of IClient interface so different parts of the system can receive messages from the server.
	 */
	IClient iClient;
	
	
	// Constructor ************************************************
	
	public Client(String host, int port) throws IOException {
		super(host, port);
		openConnection();
	}
	/**
	 * @param host - the IP address of the server
	 * @param port - the port which the server dwells on.
	 * @param iClient - an instance of the interface IClient to initialize the instance variable. 
	 * @throws IOException
	 */
	public Client(String host, int port,IClient iClient) throws IOException {
		super(host, port);
		this.iClient = iClient;
		openConnection();
	}
	
	// Instance methods ************************************************
	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		MsgParser msgFromServer = (MsgParser) msg;
		iClient.recieveMessageFromServer(msgFromServer);
	}


	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
			//System.out.println("Client connection closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	/**
	 * receives a message from different parts of the system and sends it to the server.
	 * @param msg - the message to send to the server.
	 */
	public void sendMessageToServer(Object msg) {
		try {
			this.sendToServer(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
//End of ChatClient class
