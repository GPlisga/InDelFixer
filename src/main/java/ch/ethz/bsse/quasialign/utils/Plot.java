/**
 * Copyright (c) 2011-2012 Armin Töpfer
 *
 * This file is part of QuasiAlign.
 *
 * QuasiAlign is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * QuasiAlign is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * QuasiAlign. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ethz.bsse.quasialign.utils;

import ch.ethz.bsse.quasialign.stored.Globals;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author Armin Töpfer (armin.toepfer [at] gmail.com)
 */
public class Plot {

    public static void plotCoverage(int[] coverage) {
        XYSeries dataset = new XYSeries("Coverage");
        for (int i = 0; i < coverage.length; i++) {
            dataset.add((double) i, (double) coverage[i]);
        }
        XYSeriesCollection collection = new XYSeriesCollection(dataset);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Coverage",
                "Position",
                "Coverage",
                collection,
                PlotOrientation.VERTICAL,
                false,
                false,
                false);

        final XYPlot plot = chart.getXYPlot();
//        final NumberAxis domainAxis = new NumberAxis("Position");
//        final NumberAxis rangeAxis = new LogarithmicAxis("Coverage (log)");
//        plot.setDomainAxis(domainAxis);
//        plot.setRangeAxis(rangeAxis);
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
        plot.setBackgroundPaint(Color.white);
//        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.decode("0xadadad"));

        try {
            ChartUtilities.saveChartAsPNG(new File(Globals.output + "coverage.png"),
                    chart, 1000, 500);
        } catch (IOException ex) {
            Logger.getLogger(Plot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
