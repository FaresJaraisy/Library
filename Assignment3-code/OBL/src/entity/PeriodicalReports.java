package entity;

import java.io.File;
import java.util.Date;

public class PeriodicalReports {

	private File fileName;
	private Date startDate;
	private Date endDate;
	
	public PeriodicalReports(File fileName,Date startDate,Date endDate) {
		this.fileName=fileName;
		this.startDate=startDate;
		this.endDate=endDate;
	}

	public File getFileName() {
		return fileName;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
