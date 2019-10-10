package control;

import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import entity.ActivityReport;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.IReport;
import entity.MsgParser;
import entity.MyFile;
import entity.Report;
import entity.StatData;
import entity.StatDataForLateReturnedReport;

public class LateReturnReportController implements IClient, IReport {
	private Client client;
	private static LateReturnReportController singleton = null;
	private Semaphore sem;
	private Report report;
	private ArrayList<String> arr;
	private ArrayList<StatDataForLateReturnedReport> sdflr = null;

	private LateReturnReportController(String host, int port) {
		try {
			client = new Client(host, port, this);
			sem = new Semaphore(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized static LateReturnReportController getInstance(String host, int port) {
		if (singleton == null)
			singleton = new LateReturnReportController(host, port);
		return singleton;
	}
	//=====================================================================
	
	
	
	private void getDataForLateReturnedReport(Report report) throws InterruptedException {
		MsgParser<StatDataForLateReturnedReport> msg = new MsgParser();
		msg.setTask(ConstantsAndGlobalVars.getDataForLateReturnedReportTask);
		client.sendMessageToServer(msg);
		sem.acquire();
	}
	
	//==========================================================================
	@Override
	public boolean createReport(Report report) {
		this.report = report;
		try {
			getDataForLateReturnedReport(report);
			if(sdflr.get(0) == null) return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return createLateReturnedReport(report);
	}

	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		sdflr = (ArrayList<StatDataForLateReturnedReport>) msg.getCommPipe();

		sem.release();
	}

	
	/*
	 * *****************************************************
	 * private methods to create reports
	 * 
	 * ****************************************************/
	

	private boolean createLateReturnedReport(Report report) {
		if(sdflr == null)
			return false;
		try {
			createLateReturnReport();
		} catch (URISyntaxException | IOException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//==============================================================================================
	private void createLateReturnReport()throws URISyntaxException, IOException, DocumentException  {

		PdfPTable table;
		arr = new ArrayList<>();
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(report.getPath()+"\\"+report.getName()+".pdf"));
		
		//=======create a table for all books=====================================================
		document.open();
		ArrayList<Integer> medianArr = new ArrayList<Integer>();
		int numOfAllCopies = 0;
		int sumOfAllDurations = 0;
		int maxOfAllDurations = 0;
		int medianOfAllDurations = 0;
		double avgOfAllDurations = 0;
		int dst = 0;
		int dstSize = 0;
		for (StatDataForLateReturnedReport sd : sdflr) {
			numOfAllCopies += sd.getTotalNumberOfDurations();
			sumOfAllDurations += sd.getSumOfDuration();
			for(int i=0 ; i< sd.getDurations().size() ; i++) medianArr.add(sd.getDurations().get(i));
			if(sd.getMaxDuration() > maxOfAllDurations) maxOfAllDurations = sd.getMaxDuration();
			avgOfAllDurations += sd.getAvgDuration();
		}
		avgOfAllDurations /= sdflr.size();
		Arrays.sort(medianArr.toArray());
		medianOfAllDurations = medianArr.get(medianArr.size()/2);
		dst = (int)Math.ceil(maxOfAllDurations/10.0);
		dstSize = (int)Math.ceil(maxOfAllDurations/dst);
		
		createFileHeader(document);
		addTitle(document, "LateReturned Report");
		addNewLine(document);
		addNewLine(document);
		addTitle(document, "All Books");
		addNewLine(document);
		table  = createNewTable(document, 2);
		arr.clear();
		arr.add("Data");
		arr.add("Number");
		addTableHeader(table, arr);
		arr.clear();
		arr.add("Number of all copies");
		arr.add(Integer.toString(numOfAllCopies));
		arr.add("Max duration of all books");
		arr.add(Integer.toString(maxOfAllDurations));
		arr.add("Decimal distribution");
		arr.add(Integer.toString(dst));
		arr.add("Median of all books");
		arr.add(Integer.toString(medianOfAllDurations));
		arr.add("Average of all durations");
		arr.add(Double.toString(avgOfAllDurations));
		
		addRowsToTable(table, arr, 2);
		document.add(table);
		//========================================================================================
		
		// =================Distribution================================================ 
		arr.clear();

		ArrayList<Integer> dayArr = new ArrayList<Integer>();
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		
		for (int i = 0; i <= maxOfAllDurations; i++) {
			dayArr.add(0);
			rangeArr.add(0);
		}

		if (medianArr != null) {
			addNewLine(document);
			addNewLine(document);
			int N = dstSize;
			// table
			table = createNewTable(document, N + 1);

			// counter array
			for (int i = 0; i < medianArr.size(); i++) {
				dayArr.set(medianArr.get(i), dayArr.get(medianArr.get(i)) + 1);
			}
			System.out.println(dayArr);
			System.out.println(rangeArr);
			System.out.println(arr);
			System.out.println(medianArr);
			// calc the range string (0-* | $-# | ....)
			int x = 0;
			int count = 0;
			for (int i = 0; i <= N; i++) {
				arr.add(Integer.toString(x) + "-" + Integer.toString(x + dst - 1));
				x += dst;
			}

			// fill the range array
			if (dst > 1) {
				for (int i = 0; i <= maxOfAllDurations; i++) {
					rangeArr.set(i / dst, rangeArr.get(i / dst) + dayArr.get(i));
					/*
					 * if (i % dst == dst - 1) { rangeArr.set(i / dst, count); count = 0; } else {
					 * count += dayArr.get(i) + dayArr.get(i + 1); }
					 */
				}
			} else {
				for (int i = 0; i <= maxOfAllDurations; i++)
					rangeArr.set(i, dayArr.get(i));
			}

			// arr.clear();
			for (int i = 0; i <= N; i++)
				arr.add(Integer.toString(rangeArr.get(i)));

			addRowsToTableTwo(table, arr, arr.size());
			document.add(table);
		}
		// ============================================================================= 
		
		document.newPage();
		///==========================new chart====================================
		addNewLine(document);
		addNewLine(document);
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for (int i = 0; i < arr.size() / 2; i++) {
			dataSet.setValue(rangeArr.get(i), "Des", arr.get(i));
		}

		JFreeChart chart = ChartFactory.createBarChart("", "Distrbution for All books", "", dataSet, PlotOrientation.VERTICAL, false,
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
		
		//=============create table for all books=================================================
		addTitle(document, "Individual Books");
		addNewLine(document);
		table  = createNewTable(document, 7);
		arr.clear();
		arr.add("File name");
		arr.add("Number of late copies");
		arr.add("Overall duration");
		arr.add("Max duration");
		arr.add("Average duration");
		arr.add("Median");
		arr.add("Decimal distribution");
		addTableHeader(table, arr);
		System.out.println(sdflr);
		for (StatDataForLateReturnedReport sd : sdflr) {
			System.out.println("1");
			arr.clear();
			arr.add(sd.getBookName());
			arr.add(Integer.toString(sd.getTotalNumberOfDurations()));
			arr.add(Integer.toString(sd.getSumOfDuration()));
			arr.add(Integer.toString(sd.getMaxDuration()));
			arr.add(Double.toString(sd.getAvgDuration()));
			Arrays.sort(sd.getDurations().toArray());
			int median = sd.getDurations().get(sd.getDurations().size()/2);
			arr.add(Integer.toString(median));
			int tmpDst =(int) Math.ceil(sd.getMaxDuration()/10.0);
			arr.add(Integer.toString(tmpDst));
			addRowsToTable(table, arr, 7);
			document.add(table);
		}
		
		//========================================================================================
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
