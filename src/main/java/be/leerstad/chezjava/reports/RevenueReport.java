package be.leerstad.chezjava.reports;

import be.leerstad.chezjava.model.Order;
import be.leerstad.chezjava.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class RevenueReport extends Report {
    private static final String IMGCHEZ = "resources/img/chez.png";
    private static final String IMGJAVA = "resources/img/java.png";
    private static Logger logger = Logger.getLogger(RevenueReport.class.getName());
    private Map<User, List<Order>> orders;
    private LocalDate from;
    private LocalDate to;
    private Double periodTotal;

    public RevenueReport(LocalDate from, LocalDate to, Map<User, List<Order>> orders) {
        super("RevenueReport.properties");
        this.orders = orders;
        this.from = from;
        this.to = to;
        logger.debug("Period From Date: " + from);
        logger.debug("Period To Date: " + to);
        periodTotal = 0.00;
    }

    // Returns absolute path from created PDF
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
            doc.addTitle("Period Report");
            doc.setPageSize(PageSize.A4);

            //open document
            doc.open();
            logger.debug("PDF document opened");
            setHeader();
            doc.add(header);
            logger.debug("added header to PDF");
            //create a paragraph
            StringBuilder sb = new StringBuilder(headerSubject).append(" ").append(from).append(" ").append(to).append("\n")
                    .append(headerText).append(" ");
            Paragraph paragraph = new Paragraph(sb.toString());
            //
            // Create Table
            //specify column widths
            float[] columnWidths = {5f, 1f, 1f, 1f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert column headings
            insertCell(table, "Beverage", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Price", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Quantity", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Beverage Total", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);
            //
            for (User waiter : orders.keySet()) {
                Double orderTotal = orders.get(waiter).stream().mapToDouble(Order::getOrderPrice).sum();
                this.periodTotal = periodTotal + orderTotal;
                if (orderTotal > 0) {
                    //insert an empty row
                    insertCell(table, "", Element.ALIGN_LEFT, 4, bfBold12);

                    //create section heading by cell merging
                    insertCell(table, waiter.toString() + " Orders...", Element.ALIGN_LEFT, 4, bfBold12);

                    //Waiters orders
                    for (Order o : orders.get(waiter)) {

                        insertCell(table, o.getItemName(), Element.ALIGN_RIGHT, 1, bf12);
                        insertCell(table, df.format(o.getItemPrice()) + " €", Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, o.getQuantity().toString(), Element.ALIGN_LEFT, 1, bf12);
                        insertCell(table, df.format(o.getOrderPrice()) + " €", Element.ALIGN_RIGHT, 1, bf12);
                    }
                    //merge the cells to create a footer for that section
                    insertCell(table, waiter.toString() + " Total...", Element.ALIGN_RIGHT, 3, bfBold12);
                    insertCell(table, df.format(orderTotal) + " €", Element.ALIGN_RIGHT, 1, bfBold12);
                }
            }

            // add total to paragraph
            paragraph.add(df.format(periodTotal) + " €");
            logger.debug("revenue report period total: " + periodTotal);
            //add the PDF table to the paragraph
            paragraph.add(table);
            logger.debug("Revenue Table created");
            // add the paragraph to the document
            doc.add(paragraph);
            logger.debug("Revenue Table added");

        } catch (DocumentException | FileNotFoundException e) {
            logger.error("Failed to Create Revenue Report PDF Document", e);

        } finally {
            if (doc != null) {
                //close the document
                doc.close();
                logger.debug("PDF document closed");
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }
        return file;
    }


}
