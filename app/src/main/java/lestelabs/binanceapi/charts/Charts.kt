package lestelabs.binanceapi.charts


import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import lestelabs.binanceapi.tools.Tools
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter





class Charts(context: Context) {

    val TAG = "TALIB"
    private val lineColors:  List<Int> = listOf(Color.BLACK, Color.RED)
    val mContext = context


    fun linearChart(graph: GraphView, xAxis: LongArray, yAxis:DoubleArray, offset: Int, color: Int, dotted:Boolean) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries()

            for (j in 0+offset .. yAxis.size-1) {
                val dataPoint = DataPoint(Date(xAxis[j]),yAxis[j])
                series.appendData(dataPoint, true,xAxis.size-offset)
            }
            series.color = color
        if (dotted) {
            val paint: Paint = Paint();
            paint.style = Paint.Style.STROKE;
            paint.strokeWidth = 5f;
            paint.pathEffect = DashPathEffect(floatArrayOf(0.008f, 0.005f), 0F)
            paint.color = color
            series.isDrawAsPath = true
            series.setCustomPaint(paint)
        }
            graph.addSeries(series)
    }

    fun linearChartSettings(graph: GraphView, minY:Double, maxY:Double) {
        graph.gridLabelRenderer.numHorizontalLabels = 10;
        graph.viewport.setMinY(minY*0.8)
        graph.viewport.setMaxY(maxY)
        graph.viewport.isYAxisBoundsManual = true;
        graph.viewport.isXAxisBoundsManual = true;
        if (minY == 0.0) {
            graph.gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(mContext, SimpleDateFormat(""))
        } else {
            graph.gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(mContext, SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        }
        graph.gridLabelRenderer.numVerticalLabels = 5
        graph.gridLabelRenderer.setHorizontalLabelsAngle(90);
        //if(minY == 0.0) graph.gridLabelRenderer.isHorizontalLabelsVisible = false
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