package client.search.main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.ArrayList;
import java.util.List;

public class XYSeriesDemo extends ApplicationFrame{

    List<Float> cossineListData = new ArrayList<Float>();
    List<Float> jaccardListData = new ArrayList<Float>();



    public XYSeriesDemo(final String title) {

        super(title);
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

        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(seriesCos);
        data.addSeries(seriesJac);

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "XY Series Demo",
                "X",
                "Y",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }


    public void setGraphData(ArrayList<Float> cossineList, ArrayList<Float> jaccardList){
        this.cossineListData = cossineList;
        this.jaccardListData = jaccardList;
    }

}
