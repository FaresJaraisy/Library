package control;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import entity.MsgParser;
import entity.User;
import enums.UserStatus;

public class LockCardUponGraduation extends TimerTask{
	String ID;
	public LockCardUponGraduation(String ID)
	{
		this.ID=ID;
	}
	@Override
	public void run() {
		if(Server.dbController.checkBorrowsExistance(ID)==false) // lock user card
    	{
    		MsgParser<User> msg =  new MsgParser();
    		User user = new User(ID);
    		user.setStatus(UserStatus.Locked);
    		msg.addToCommPipe(user);
    		Server.dbController.changeMemberStatus(msg);

    	}
    	else // wait to return all his books and freeze his card
    	{
    		MsgParser<User> msg =  new MsgParser();
    		User user = new User(ID);
    		user.setStatus(UserStatus.Frozen);
    		msg.addToCommPipe(user);
    		Server.dbController.changeMemberStatus(msg);

    		
    		Timer timer = new Timer();
        	Date currentDate = new Date();
    	    Calendar c = Calendar.getInstance();
    	    c.setTime(currentDate);
    	    // c.add(Calendar.HOUR, 48);
    	    c.add(Calendar.SECOND, 200);
    		// convert calendar to date
    	    Date currentDatePlus200 = c.getTime();
    	    timer.schedule(new LockCardUponGraduation(ID),currentDatePlus200);
    		
    	}
	}
}