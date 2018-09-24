package be.leerstad.chezjava.reports;

import be.leerstad.chezjava.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;

public class TopWaitersReport extends Report {
    private static Logger logger = Logger.getLogger(TopWaitersReport.class.getName());
    private Map<User, Double> orders;
    private LocalDate date;
    private Double totalRevenue;


    public TopWaitersReport(LocalDate date, Map<User, Double> orders) {
        super("waiterReport.properties");
        this.orders = orders;
        this.date = date;
        totalRevenue = orders.values().stream().mapToDouble(Double::doubleValue).sum();
        logger.debug("Report total revenue: " + totalRevenue);

    }

    public File createPDF() {
        Document doc = new Document();
        PdfWriter docWriter = null;

        DecimalFormat df = new DecimalFormat("0.00");

        try {

            //special font sizes
            Font bfBold12 = new Font(FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(FontFamily.HELVETICA, 12);

            //file path
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(DEST));

            //document header attributes
            doc.addAuthor("Jeroen Herman");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("chezjava.leerstad.be");
            doc.addTitle("Waiter Report Chez Java");
            doc.setPageSize(PageSize.A4);

            //open document
            doc.open();
            logger.debug("PDF document opened");
            setHeader();
            doc.add(header);
            //create a paragraph
            StringBuilder sb = new StringBuilder(headerSubject).append(" ").append(date).append("\n").append(headerText).append(" ");
            Paragraph paragraph = new Paragraph(sb.toString());
            //
            // Create Table
            //specify column widths
            float[] columnWidths = {2f, 5f, 5f, 2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert column headings
            insertCell(table, "Position", Element.ALIGN_MIDDLE, 1, bfBold12);
            insertCell(table, "First Name", Element.ALIGN_MIDDLE, 1, bfBold12);
            insertCell(table, "Last Name", Element.ALIGN_MIDDLE, 1, bfBold12);
            insertCell(table, "Total Revenue", Element.ALIGN_MIDDLE, 1, bfBold12);
            table.setHeaderRows(1);
            //
            Integer position = 1;
            for (User waiter : orders.keySet()) {
                Double totalWaiterRevenue = orders.get(waiter).doubleValue();
                //insert an empty row
                insertCell(table, "", Element.ALIGN_LEFT, 4, bfBold12);
                if (totalWaiterRevenue > 0) {
                    //Waiters total revenues
                    insertCell(table, position.toString(), Element.ALIGN_MIDDLE, 1, bf12);
                    insertCell(table, waiter.getFirstName(), Element.ALIGN_MIDDLE, 1, bf12);
                    insertCell(table, waiter.getLastName(), Element.ALIGN_MIDDLE, 1, bf12);
                    insertCell(table, df.format(totalWaiterRevenue) + " €", Element.ALIGN_MIDDLE, 1, bf12);
                    position++;
                }
            }
            // add total to paragraph
            paragraph.add(df.format(totalRevenue) + " €");
            logger.debug("Total revenue added");

            //add the PDF table to the paragraph
            paragraph.add(table);
            logger.debug("Topwaiter table created");
            // add the paragraph to the document
            doc.add(paragraph);
            logger.debug("Topwaiter table added to PDF");
            // add PieChart
            PieChart pieChart = new PieChart("Top Waiters", orders);
            logger.debug("Topwaiter Piechart created");
            Paragraph chartParagraph = new Paragraph("Pie Chart");
            Image chartImage = Image.getInstance(pieChart.getPieChart().getAbsolutePath());

            chartParagraph.add(chartImage);
            doc.add(chartParagraph);
            logger.debug("Piechart added to PDF");
        } catch (DocumentException | IOException e) {
            logger.error("Topwaiter report failed to create", e);

        } finally {
            if (doc != null) {
                //close the document
                doc.close();
                logger.debug("Topwaiter report closed");
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }
        return file;
    }

}
