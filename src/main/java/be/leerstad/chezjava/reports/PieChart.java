package be.leerstad.chezjava.reports;

import be.leerstad.chezjava.model.User;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PieChart {
    private static Logger logger = Logger.getLogger(PieChart.class.getName());
    private DefaultPieDataset dataset;
    private File pieChart;
    private String title;

    public PieChart(String title, Map<User, Double> revenues) throws IOException {
        this.title = title;
        dataset = new DefaultPieDataset();
        for (User waiter : revenues.keySet()) {
            dataset.setValue(waiter.toString(), revenues.get(waiter));
        }
        logger.debug("Piechart Dataset filled");
        createPieChart();
    }

    private void createPieChart() {
        try {
            JFreeChart chart = ChartFactory.createPieChart(title, dataset,
                    true,             // include legend
                    true,
                    false);

            int width = 500;   /* Width of the image */
            int height = 400;  /* Height of the image */

            pieChart = new File("src/main/resources/img/piechart/" + title + ".png");

            ChartUtilities.saveChartAsJPEG(pieChart, chart, width, height);
        } catch (IOException e) {
            logger.error("Create Pie chart failed", e);
        }
    }

    public File getPieChart() {
        return pieChart;
    }
}
