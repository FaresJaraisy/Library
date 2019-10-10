package control;

import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
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

import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.IReport;
import entity.MsgParser;
import entity.Report;
import entity.StatData;
import entity.StatDataForLateReturnedReport;

public class BorrowedReportController implements IClient, IReport{
	private Client client;
	private static BorrowedReportController singleton = null;
	private Semaphore sem;
	private StatData sd;
	private Report report;
	private ArrayList<String> arr;

	//================================================================
	private BorrowedReportController(String host, int port) {
		try {
			client = new Client(host, port, this);
			sem = new Semaphore(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized static BorrowedReportController getInstance(String host, int port) {
		if (singleton == null)
			singleton = new BorrowedReportController(host, port);
		return singleton;
	}
	//=====================================================================
	
	private void getDataForBorrowedReport(Report report) throws InterruptedException {
		MsgParser<StatData> msg = new MsgParser();
		msg.setTask(ConstantsAndGlobalVars.getDataForBorrowedReportTask);
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	//=====================================================================
	@Override
	public boolean createReport(Report report) {
		this.report = report;
		try {
			getDataForBorrowedReport(report);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return createBorrowedReport();

	}

	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		// TODO Auto-generated method stub
		sd = (StatData) msg.getCommPipe().get(0);
		sem.release();
	}

	//===============================================================
	private boolean createBorrowedReport() {
		if(sd == null)
			return false;
		
		try {
			createBorrowReport();
		} catch (URISyntaxException | IOException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//==========================================================================
	private void createBorrowReport()throws URISyntaxException, IOException, DocumentException  {
		int dst = 0;
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(report.getPath()+"\\"+report.getName()+".pdf"));
		//==================================================================================
		
		PdfPTable wantedTable, regularTable;
		arr = new ArrayList<>();
		
		document.open();
		createFileHeader(document);
		
		addTitle(document, "Borrow Report");
		addNewLine(document);
		addNewLine(document);
		
		//wanted table
		addTitle(document, "Wanted Books");
		addNewLine(document);
		wantedTable  = createNewTable(document, 2);
		arr.add("Operation");
		arr.add("Number");
		addTableHeader(wantedTable, arr);
		arr.clear();

		arr.add("Number Of Borrowed Books");
		arr.add(Integer.toString(sd.getNumOfWantedBooks()));
		arr.add("Maximum Borrow Range");
		arr.add(Integer.toString(sd.getMaxRangeForWantedBooks()));
		arr.add("Total Borrow Days");
		arr.add(Integer.toString(sd.getSumOfDaysForWantedBooks()));
		arr.add("Average Borrow Interval");
		arr.add(Double.toString(sd.getAvgOfWantedBooks()));
		arr.add("Median");
		arr.add(Integer.toString(sd.getMedianOfWantedBooks()));
		addRowsToTable(wantedTable, arr, 2);
		document.add(wantedTable);
		///---===========-----------================-------------------==============
		// =================Distribution================================================ 
		arr.clear();
		dst = (int) Math.ceil(sd.getMaxRangeForWantedBooks() / 10.0);

		ArrayList<Integer> dayArr0 = new ArrayList<Integer>();
		ArrayList<Integer> rangeArr0 = new ArrayList<Integer>();
		ArrayList<Integer> wantedArr = sd.getWantedArr();
		for (int i = 0; i <= sd.getMaxRangeForWantedBooks(); i++) {
			dayArr0.add(0);
			rangeArr0.add(0);
		}

		if (wantedArr != null) {
			addNewLine(document);
			addNewLine(document);
			int N = (int) Math.ceil(sd.getMaxRangeForWantedBooks() / dst * 1.0);
			// table
			wantedTable = createNewTable(document, N+1);

			// counter array
			for (int i = 0; i < wantedArr.size(); i++) {
				dayArr0.set(wantedArr.get(i), dayArr0.get(wantedArr.get(i)) + 1);
			}

			// calc the range string (0-* | $-# | ....)
			int x = 0;
			int count = 0;
			for (int i = 0; i <= N; i++) {
				arr.add(Integer.toString(x) + "-" + Integer.toString(x + dst - 1));
				x += dst;
			}

			// fill the range array
			if (dst > 1) {
				for (int i = 0; i <= sd.getMaxRangeForWantedBooks(); i++) {
					rangeArr0.set(i / dst, rangeArr0.get(i / dst) + dayArr0.get(i));
					/*
					 * if (i % dst == dst - 1) { rangeArr.set(i / dst, count); count = 0; } else {
					 * count += dayArr.get(i) + dayArr.get(i + 1); }
					 */
				}
			} else {
				for (int i = 0; i <= sd.getMaxRangeForWantedBooks(); i++)
					rangeArr0.set(i, dayArr0.get(i));
			}

			// arr.clear();
			for (int i = 0; i <= N; i++)
				arr.add(Integer.toString(rangeArr0.get(i)));

			addRowsToTableTwo(wantedTable, arr, arr.size());
			document.add(wantedTable);
		}
		// ============================================================================= 
		document.newPage();
		///==========================new chart====================================
		addNewLine(document);
		addNewLine(document);
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for (int i = 0; i < arr.size() / 2; i++) {
			dataSet.setValue(rangeArr0.get(i), "Des", arr.get(i));
		}

		JFreeChart chart = ChartFactory.createBarChart("", "Distrbution for Wanted book", "", dataSet, PlotOrientation.VERTICAL, false,
				true, false);
		// writer = PdfWriter.getInstance(document,new FileOutputStream(report.getPath()
		// + "\\LateReturnedReport.pdf"));

		document.open();

		PdfContentByte pdfContentByte = writer.getDirectContent();
		PdfTemplate pdfTemplate = pdfContentByte.createTemplate(400, 300);

		// create graphics
		@SuppressWarnings("deprecation")
		Graphics2D graphics2d = pdfTemplate.createGraphics(400, 300, new DefaultFontMapper());

		// create rectangle
		java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, 400, 300);

		chart.draw(graphics2d, rectangle2d);

		graphics2d.dispose();
		pdfContentByte.addTemplate(pdfTemplate, 100, 500);

		///=====================================================================
		document.newPage();
		// regular table
		arr.clear();
		addTitle(document, "Regular Books");
		addNewLine(document);
		regularTable  = createNewTable(document, 2);
		arr.add("Operation");
		arr.add("Number");
		addTableHeader(regularTable, arr);
		arr.clear();

		arr.add("Number Of Borrowed Books");
		arr.add(Integer.toString(sd.getNumOfRegularBooks()));
		arr.add("Maximum Borrow Range");
		arr.add(Integer.toString(sd.getMaxRangeForRegularBooks()));
		arr.add("Total Borrow Days");
		arr.add(Integer.toString(sd.getSumOfDaysForRegularBooks()));
		arr.add("Average Borrow Interval");
		arr.add(Double.toString(sd.getAvgOfRegularBooks()));
		arr.add("Median");
		arr.add(Integer.toString(sd.getMedianOfRegularBooks()));
		addRowsToTable(regularTable, arr, 2);
		document.add(regularTable);
		// =================Distribution================================================ 
		arr.clear();
		dst = (int) Math.ceil(sd.getMaxRangeForRegularBooks() / 10.0);

		ArrayList<Integer> dayArr = new ArrayList<Integer>();
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> regularArr = sd.getRegularArr();
		for (int i = 0; i <= sd.getMaxRangeForRegularBooks(); i++) {
			dayArr.add(0);
			rangeArr.add(0);
		}

		if (regularArr != null) {
			addNewLine(document);
			addNewLine(document);
			int N = (int) Math.ceil(sd.getMaxRangeForRegularBooks() / dst * 1.0);
			// table
			regularTable = createNewTable(document, N+1);

			// counter array
			for (int i = 0; i < regularArr.size(); i++) {
				dayArr.set(regularArr.get(i), dayArr.get(regularArr.get(i)) + 1);
			}

			// calc the range string (0-* | $-# | ....)
			int x = 0;
			int count = 0;
			for (int i = 0; i <= N; i++) {
				arr.add(Integer.toString(x) + "-" + Integer.toString(x + dst - 1));
				x += dst;
			}

			// fill the range array
			if (dst > 1) {
				for (int i = 0; i <= sd.getMaxRangeForRegularBooks(); i++) {
					rangeArr.set(i / dst, rangeArr.get(i / dst) + dayArr.get(i));
					/*
					 * if (i % dst == dst - 1) { rangeArr.set(i / dst, count); count = 0; } else {
					 * count += dayArr.get(i) + dayArr.get(i + 1); }
					 */
				}
			} else {
				for (int i = 0; i <= sd.getMaxRangeForRegularBooks(); i++)
					rangeArr.set(i, dayArr.get(i));
			}
			
			// arr.clear();
			for (int i = 0; i <= N; i++)
				arr.add(Integer.toString(rangeArr.get(i)));
			
			addRowsToTableTwo(regularTable, arr, arr.size());
			document.add(regularTable);
		}
		// ============================================================================= 
		
		document.newPage();
		///==========================new chart====================================
		addNewLine(document);
		addNewLine(document);
		dataSet = new DefaultCategoryDataset();
		for (int i=0 ; i< arr.size()/2 ; i++) {
			dataSet.setValue(rangeArr.get(i), "Des", arr.get(i));
		}
		

		chart = ChartFactory.createBarChart("", "Distrbution for Regular books","",dataSet, PlotOrientation.VERTICAL, false, true, false);
		//writer = PdfWriter.getInstance(document,new FileOutputStream(report.getPath() + "\\LateReturnedReport.pdf"));

		document.open();
		

		pdfContentByte = writer.getDirectContent();
		pdfTemplate = pdfContentByte.createTemplate(400, 300);

		// create graphics
		@SuppressWarnings("deprecation")
		Graphics2D graphics2dd = pdfTemplate.createGraphics(400, 300, new DefaultFontMapper());

		// create rectangle
		java.awt.geom.Rectangle2D rectangle2dd = new java.awt.geom.Rectangle2D.Double(0, 0, 400, 300);

		chart.draw(graphics2dd, rectangle2dd);

		graphics2dd.dispose();
		pdfContentByte.addTemplate(pdfTemplate, 100, 500);
		
		///=====================================================================
		
		document.close();
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
	
	private void addRowsToTableTwo(PdfPTable table, ArrayList<String> arr, int numOfColumns) {
		Phrase phrase;
		PdfPCell cell;
		int i = 0;
		
		for (String str : arr) {
			phrase = new Phrase(str);
			cell = new PdfPCell(phrase);
			if(i < numOfColumns/2) 
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				
			else cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			i++;
		}
	}
	
}
