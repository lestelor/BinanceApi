package lestelabs.binanceapi.calculations


import android.content.Context
import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import lestelabs.binanceapi.R
import lestelabs.binanceapi.tools.Tools
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min
import lestelabs.binanceapi.MainActivity

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter





class Charts(context: Context) {

    val TAG = "TALIB"
    private val lineColors:  List<Int> = listOf(Color.BLACK, Color.RED)
    val mContext = context


    fun linearChart(graph: GraphView, xAxis: LongArray, yAxis:MutableList<DoubleArray>, offset: Int) {

        var minY = 50000.0
        var maxY = 0.0
        val series: MutableList<LineGraphSeries<DataPoint>> = mutableListOf()



        for (i in yAxis.indices) {
            series.add(LineGraphSeries<DataPoint>())
            for (j in 0+offset .. yAxis[i].size-1) {
                val dataPoint = DataPoint(Date(xAxis[j]),yAxis[i][j])
                series[i].appendData(dataPoint, true,xAxis.size-offset)
            }

            minY = Tools().findMin(yAxis[i], offset, minY)
            maxY = Tools().findMax(yAxis[i], offset, maxY)

            series[i].color = lineColors[i]
            graph.addSeries(series[i])
        }

        graph.viewport.setMinY(minY*0.8)
        graph.viewport.setMaxY(maxY*1.2)


        graph.viewport.isYAxisBoundsManual = true;
        graph.viewport.isXAxisBoundsManual = true;

        graph.gridLabelRenderer.labelFormatter =
            DateAsXAxisLabelFormatter(mContext, SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        graph.gridLabelRenderer.numHorizontalLabels = 5;
        graph.gridLabelRenderer.setHorizontalLabelsAngle(90);
    }



    fun setBarChart(barChart: BarChart) {

        // variable for our bar data.
        val barData: BarData
        // variable for our bar data set.
        val barDataSet: BarDataSet

        // array list for storing entries.

        // array list for storing entries.
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(8f, 0f))
        entries.add(BarEntry(2f, 1f))
        entries.add(BarEntry(5f, 2f))
        entries.add(BarEntry(20f, 3f))
        entries.add(BarEntry(15f, 4f))
        entries.add(BarEntry(19f, 5f))

        // creating a new bar data set.
        // creating a new bar data set.
        barDataSet = BarDataSet(entries, "Geeks for Geeks")

        // creating a new bar data and
        // passing our bar data set.

        // creating a new bar data and
        // passing our bar data set.
        barData = BarData(barDataSet)

        // below line is to set data
        // to our bar chart.

        // below line is to set data
        // to our bar chart.
        barChart.data = barData

        // adding color to our bar data set.

        // adding color to our bar data set.
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

        // setting text color.

        // setting text color.
        barDataSet.valueTextColor = Color.BLACK

        // setting text size

        // setting text size
        barDataSet.valueTextSize = 16f
        barChart.description.isEnabled = false


        barChart.animateY(5000)
    }


}