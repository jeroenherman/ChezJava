package be.leerstad.chezjava.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


abstract class Report {
    protected static String filePath;
    protected static String fileName;
    protected static String imgPath;
    protected static String imgCompany;
    protected static String imgLogo;
    protected static String headerSubject;
    protected static String headerText;
    protected static String DEST;
    private static Logger log = Logger.getLogger(Report.class);
    protected Paragraph header;
    protected File file;

    public Report(String propertiesFile) {
        getProperties(propertiesFile);
        file = new File(setDestination());
        file.getParentFile().mkdirs();
    }

    protected static PdfPCell createImageCell(String path) throws DocumentException, IOException {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell(img, true);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private String setDestination() {
        StringBuilder sb = new StringBuilder(filePath).append("/").append(fileName).append("/")
                .append(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").format(LocalDateTime.now()))
                .append(".pdf");
        DEST = sb.toString();
        return DEST;
    }

    private void getProperties(String propertiesFile) {
        Properties props = new Properties();
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertiesFile)) {

            props.load(inputStream);

        } catch (Exception e) {
            log.error(e);
        }
        fileName = props.getProperty("report.file.name");
        filePath = props.getProperty("report.file.path");
        //
        imgPath = props.getProperty("report.img.path");
        imgLogo = props.getProperty("report.img.logo");
        imgCompany = props.getProperty("report.img.company");
        //
        headerSubject = props.getProperty("report.header.subject");
        headerText = props.getProperty("report.header.text");
    }

    protected void setHeader() {
        header = new Paragraph("Welcome to");
        // Create imageTable
        float[] headerWidths = {1.5f, 2f};
        PdfPTable imageTable = new PdfPTable(headerWidths);
        imageTable.setWidthPercentage(90f);

        try {
            imageTable.addCell(createImageCell(setImage(imgPath, imgCompany))).setFixedHeight(50f);
            imageTable.addCell(createImageCell(setImage(imgPath, imgLogo))).setFixedHeight(50f);
            header.add(imageTable);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String setImage(String path, String imgName) {
        StringBuilder sb = new StringBuilder(path).append("/").append(imgName);
        return sb.toString();
    }

    protected void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }
}
