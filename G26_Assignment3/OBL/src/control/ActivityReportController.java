package control;

import java.awt.Graphics2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

import javax.print.Doc;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;

import boundary.ActivityReportGUIController;
import entity.ActivityReport;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.IReport;
import entity.MsgParser;
import entity.MyFile;
import entity.Report;
import entity.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;

public class ActivityReportController implements IReport, IClient {
	private Client client;
	private static ActivityReportController singleton = null;
	private Semaphore sem;
	private Semaphore returnSem;
	private ArrayList<String> arr;
	private ActivityReport activityReport;
	private MyFile file;
	private int numOfMembers;
	private int numOfActiveMembers;
	private int numOfFrozenMembers;
	private int numOfLockedMembers;
	private int numOfBorrowedBooks;
	private int numOfLateReturnMembers;
	private boolean retVal;

	private ActivityReportController(String host, int port) {
		try {
			client = new Client(host, port, this);
			sem = new Semaphore(0);
			returnSem = new Semaphore(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized static ActivityReportController getInstance(String host, int port) {
		if (singleton == null)
			singleton = new ActivityReportController(host, port);
		return singleton;
	}
	
	@Override
	public boolean createReport(Report report) {
		activityReport = (ActivityReport) report;
		try {
			checkReportExistence();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(file != null) {
			downloadFile();
			return true;
		}
		
		
		try {
			createActivityReport();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}
	//===================================================================================================
	private void checkReportExistence() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.checkReportExistenceTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void getNumOfMembers() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.getNumOfAllMembersTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void getNumOfActiveMembers() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.getNumOfActiveMembersTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void getNumOfFroozenMembers() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.getNumOfFrozenMembersTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void getNumOfLockedMembers() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.getNumOfLockedMembersTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void getNumOfBorrowedBooks() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.getNumOfBorrowedBooksTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void getNumOfLateReturnMembers() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.getNumOfLateReturnMembersTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	private void addReportToDB() throws InterruptedException {
		MsgParser<ActivityReport> msg = new MsgParser<>();
		msg.addToCommPipe(activityReport);
		msg.setTask(ConstantsAndGlobalVars.addReportToDBTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	//===================================================================================================
	

	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		// TODO Auto-generated method stub
		if(msg.getTask().equals(ConstantsAndGlobalVars.checkReportExistenceTask)) {
			file = (MyFile) msg.getCommPipe().get(0);
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumOfAllMembersTask)) {
			numOfMembers = msg.getIntResult();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumOfActiveMembersTask)) {
			numOfActiveMembers = msg.getIntResult();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumOfFrozenMembersTask)) {
			numOfFrozenMembers = msg.getIntResult();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumOfLockedMembersTask)) {
			numOfLockedMembers = msg.getIntResult();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumOfBorrowedBooksTask)) {
			numOfBorrowedBooks = msg.getIntResult();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumOfLateReturnMembersTask)) {
			numOfLateReturnMembers = msg.getIntResult();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.addReportToDBTask)) {
			retVal = (boolean) msg.getCommPipe().get(0);
		}
		
		sem.release();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	private void createActivityReport()throws URISyntaxException, IOException, DocumentException  {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(activityReport.getPath()+"\\"+activityReport.getName()+".pdf"));
		//==================================================================================
		try {
			//getNumOfMembers();
			getNumOfActiveMembers();
			getNumOfFroozenMembers();
			getNumOfLockedMembers();
			getNumOfBorrowedBooks();
			getNumOfLateReturnMembers();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PdfPTable table;
		arr = new ArrayList<>();
		
		document.open();
		createFileHeader(document);
		addTitle(document, "Activity Report");
		addNewLine(document);
		addNewLine(document);
		table = createNewTable(document, 2);
		arr.add("Operation");
		arr.add("Number");
		addTableHeader(table, arr);
		arr.clear();
		//arr.add("All members");
		//arr.add(Integer.toString(numOfMembers));
		arr.add("Active members");
		arr.add(Integer.toString(numOfActiveMembers));
		arr.add("Frozen members");
		arr.add(Integer.toString(numOfFrozenMembers));
		arr.add("Locked members");
		arr.add(Integer.toString(numOfLockedMembers));
		arr.add("Books in borrow");
		arr.add(Integer.toString(numOfBorrowedBooks));
		arr.add("LateReturn members");
		arr.add(Integer.toString(numOfLateReturnMembers));
		addRowsToTable(table, arr, 2);
		document.add(table);
		////
		document.newPage();
		addNewLine(document);
		addNewLine(document);
		try {
			addPieChart(document, writer);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			new Alert(AlertType.INFORMATION,"Cant create pieChart",ButtonType.OK).showAndWait();
		}
		///
		document.close();
		
		createFileToUploadToDB();
		
		try {
			addReportToDB();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//======================================================================================================
	private void createFileHeader(Document document) {
		Image img;
		Chunk chunk = new Chunk();
		Paragraph preface = new Paragraph();
		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
		try {
			img = Image.getInstance(getClass().getResource("/pictures/oblPdfLogo.png").toString());
			img.scaleAbsolute(300, 100);
			document.add(img);
			chunk = new Chunk("______________________________________________________", font);
			preface = new Paragraph(chunk);
			document.add(preface);
		} catch (BadElementException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void addNewLine(Document document) {
		Chunk chunk = new Chunk("\n");
		Paragraph preface = new Paragraph(chunk);
		try {
			document.add(preface);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void addTitle(Document document, String title) {
		Font font = FontFactory.getFont(FontFactory.COURIER, 36, Font.BOLD,BaseColor.BLACK);
		Chunk chunk = new Chunk(title, font);
		chunk.setUnderline(1f, -2f);
		Paragraph preface = new Paragraph(chunk);
		preface.setAlignment(Element.ALIGN_CENTER);
		try {
			document.add(preface);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private PdfPTable createNewTable(Document document, int numOfColumns) {
		return new PdfPTable(numOfColumns);
	}
	
	
	private void addTableHeader(PdfPTable table, ArrayList<String> arr) {
		PdfPCell header;
		for (String str : arr) {
			header = new PdfPCell();
			header.setBackgroundColor(BaseColor.ORANGE);
			header.setBorderWidth(1);
			header.setPhrase(new Phrase(str));
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		}
	}
	
	
	private void addRowsToTable(PdfPTable table, ArrayList<String> arr, int numOfColumns) {
		Phrase phrase;
		PdfPCell cell;
		int i = 0;
		
		for (String str : arr) {
			phrase = new Phrase(str);
			cell = new PdfPCell(phrase);
			if((i % numOfColumns)==0) {
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setHorizontalAlignment(Element.ALIGN_BOTTOM);
			}
			else cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			i++;
		}
	}
	
	private void downloadFile() {
		// get the byte array from the current book
		byte[] arr = this.file.getMybytearray();
		// get the absolute download path
		String filePath = activityReport.getPath() + "\\" + activityReport.getName() + ".pdf";
		// create a new file from the byte array and open it in the destination path
		try {
			// create the file using a FileOutputStream
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(arr);
			fos.close();// close the resource
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createFileToUploadToDB() {
		try {
			String filePath = activityReport.getPath() + "\\" + activityReport.getName() + ".pdf";
			File newFile = new File(filePath);// open the file
			// convert the file to a MyFile object so it can be sent to the server.
			// i.e. set the byte array, set the size, the name, etc.
			MyFile fileToUpload = new MyFile(newFile.getName());// instantiate a new MyFile object with the files' name
			// Initialise the byte array
			fileToUpload.initArray((int) newFile.length());
			// set the file size attribute
			fileToUpload.setSize((int) newFile.length());
			fileToUpload.setDescription(activityReport.getName());
			// a temp byte array
			byte[] mybytearray = new byte[(int) newFile.length()];
			// copy the byte array from newFile to fileToUpload byte array.
			FileInputStream fis;

			fis = new FileInputStream(newFile);

			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(fileToUpload.getMybytearray(), 0, mybytearray.length);
			// close the streams so the file won't be in use.
			bis.close();
			fis.close();
			activityReport.setFile(fileToUpload);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////////
	/*
	private void addCustomRows(PdfPTable table) throws URISyntaxException, BadElementException, IOException {

		PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
		horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(horizontalAlignCell);

		PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
		verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		table.addCell(verticalAlignCell);
	}*/
	
	//=======================================================================================
	private void addPieChart(Document document, PdfWriter writer) throws FileNotFoundException, DocumentException {
		//document.newPage();
		int total = numOfActiveMembers+numOfFrozenMembers+numOfLockedMembers;
		DefaultPieDataset defaultCategoryDataset = new DefaultPieDataset();
		defaultCategoryDataset.setValue("Active members" , numOfActiveMembers);
		defaultCategoryDataset.setValue("Frozen members" , numOfFrozenMembers);
		defaultCategoryDataset.setValue("Locked members" , numOfLockedMembers);
		defaultCategoryDataset.setValue("Books in borrow" , numOfBorrowedBooks);
		defaultCategoryDataset.setValue("LateReturn members" , numOfLateReturnMembers);
		
		JFreeChart jFreeChart = ChartFactory.createPieChart("Activity report chart", defaultCategoryDataset, true,false, false);
		document.open();

		PdfContentByte pdfContentByte = writer.getDirectContent();
		
		PdfTemplate pdfTemplate = pdfContentByte.createTemplate(300, 300);

		// create graphics
		@SuppressWarnings("deprecation")
		Graphics2D graphics2d = pdfTemplate.createGraphics(300, 300, new DefaultFontMapper());

		// create rectangle
		java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, 300, 300);

		jFreeChart.draw(graphics2d, rectangle2d);

		graphics2d.dispose();
		pdfContentByte.addTemplate(pdfTemplate, 100, 500);

		/* end pie chart */
	}
}
