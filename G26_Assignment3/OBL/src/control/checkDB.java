package control;

import java.text.ParseException;
import java.util.TimerTask;

public class checkDB extends TimerTask{
	DBController dbController;
	public checkDB(DBController dbController)
	{
		this.dbController=dbController;
	}
	@Override
	public void run() {
		checkLateReturn();
		checkDayBeforeReturn();
	}
	
	
	//function that check for late returns if any one has freeze his card
	//add his number of stikes if there is 3 strikes send to manager messege to lock his card
	public void checkLateReturn()
	{
		try {
			dbController.checkLateReturnAndFreeze();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void checkDayBeforeReturn()
	{
			dbController.checkDayBeforeReturnAndSendMessage();	
	}
	
	
}
