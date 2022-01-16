package com.example.chipfloorplanningoptimization.gui;

import org.knowm.xchart.*;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataVisualizer {

  private final ArrayList<XYChart> charts;
  private final String folderPath;

  public DataVisualizer(String folderPath) {
    charts = new ArrayList<>();
    this.folderPath = folderPath;
  }

 public void addChart(XYChart chart) {
      chart.getStyler().setLegendVisible(false);
   charts.add(chart);
 }

 public void addFromCSV(String fileName) {
     addChart(makeCSVChart(folderPath + "/" + fileName + ".csv"));
 }

 public void displayCharts(boolean matrix) {
    if (!matrix)
      for (XYChart chart : charts)
        plot(chart);
    else {
        List<XYChart> remaining = charts;
        if (charts.size() > 4) {
            List<XYChart> sub = charts.subList(0, 4);
            new SwingWrapper<>(sub).displayChartMatrix();
            remaining = charts.subList(4, charts.size());
        }
        new SwingWrapper<>(remaining).displayChartMatrix();
    }
 }

 public void saveCharts() throws IOException {
    for (XYChart chart : charts)
     BitmapEncoder.saveBitmap(chart, folderPath + "/" + chart.getTitle(), BitmapEncoder.BitmapFormat.PNG);
 }

 public static void plot(XYChart chart) {
   new SwingWrapper<>(chart).displayChart();
 }

  public static XYChart makeChart(
      double[] xValues, double[] yValues, String title, String xTitle, String yTitle) {
    return QuickChart.getChart(title, xTitle, yTitle, title, xValues, yValues);
  }

  public static XYChart makeChart(double[] xValues, double[] yValues, String title) {
    return makeChart(xValues, yValues, title, "X", "Y");
  }

  public static XYChart makeChart(double[] xValues, double[] yValues) {
    return makeChart(xValues, yValues, " ");
  }

  public static XYChart makeCSVChart(String path) {
    CSVImporter.SeriesData data =
        CSVImporter.getSeriesDataFromCSVFile(path, CSVImporter.DataOrientation.Columns);

    double[] xValues = data.getxAxisData().stream().mapToDouble((n) -> (double) n).toArray();
    double[] yValues = data.getyAxisData().stream().mapToDouble((n) -> (double) n).toArray();

    if (xValues.length != yValues.length)
      throw new IllegalArgumentException("X size has to be equal to Y size!");

    String title = path.substring(path.lastIndexOf('/') + 1, path.length() - 4);

    if (title.contains("_")) {
      String[] labels = title.split("_");
      return makeChart(xValues, yValues, title.replace("_", " - "), labels[0], labels[1]);
    }
    return makeChart(xValues, yValues, title);
  }
}
