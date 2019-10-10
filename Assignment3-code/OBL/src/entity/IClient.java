package entity;

import java.util.ArrayList;
/**
 * an interface that each class that is expected to receive a message from the server has to implement.
 */
public interface IClient {
	public void recieveMessageFromServer(MsgParser msg);
}
