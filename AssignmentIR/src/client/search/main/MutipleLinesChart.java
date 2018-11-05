package client.search.main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MutipleLinesChart extends JFrame { // the class extends the JFrame class


    List<Float> cossineListData = new ArrayList<Float>();
    List<Float> jaccardListData = new ArrayList<Float>();

    public MutipleLinesChart() {   // the constructor will contain the panel of a certain size and the close operations
        super("XY Line Chart Example with JFreechart"); // calls the super class constructor

        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createChartPanel() { // this method will create the chart panel containin the graph
        String chartTitle = "Objects Movement Chart";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        customizeChart(chart);

        // saves the chart as an image files
        File imageFile = new File("XYLineChart.png");
        int width = 640;
        int height = 480;

        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {    // this method creates the data as time seris
        XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries seriesCos = new XYSeries("Cossine Similarity Data");
        final XYSeries seriesJac = new XYSeries("Jaccard Similarity Data");

        int queryNumber = 1;
        for(Float x:cossineListData){
            seriesCos.add(queryNumber,x);
            queryNumber++;
        }


        queryNumber = 1;
        for(Float x:cossineListData){
            seriesCos.add(queryNumber,x);
            queryNumber++;
        }

        dataset.addSeries(seriesCos);
        dataset.addSeries(seriesJac);

        return dataset;
    }

    private void customizeChart(JFreeChart chart) {   // here we make some customization
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);


        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));


        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLUE);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

    }

    public void setGraphData(ArrayList<Float> cossineList, ArrayList<Float> jaccardList){
        this.cossineListData = cossineList;
        this.jaccardListData = jaccardList;
    }

}