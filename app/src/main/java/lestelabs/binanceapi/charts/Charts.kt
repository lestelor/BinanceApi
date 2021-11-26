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
import lestelabs.binanceapi.MainActivity


class Charts(context: Context) {

    val TAG = "TALIB"
    private val lineColors:  List<Int> = listOf(Color.BLACK, Color.RED)
    val mContext = context

    fun printLinearGraph(graphView: GraphView, xAxis:List<Long>, yAxis:List<DoubleArray>){
        var minY:Double
        var maxY:Double

        graphView.removeAllSeries()
        Charts(mContext).linearChart(graphView, xAxis, yAxis[0],
            MainActivity.OFFSET, Color.BLACK, false)

        if (yAxis.size <= 2) {
            minY = yAxis[0].minOrNull() ?: 0.0
            maxY = yAxis[0].maxOrNull() ?: 10.0
            Charts(mContext).linearChart(graphView, xAxis, yAxis[1],
                MainActivity.OFFSET, Color.RED, false)
        } else {
            minY = 0.0
            maxY = 100.0
            Charts(mContext).linearChart(graphView, xAxis, yAxis[1],
                MainActivity.OFFSET, Color.RED, true)
            Charts(mContext).linearChart(graphView, xAxis, yAxis[2],
                MainActivity.OFFSET, Color.RED, true)
        }

        Charts(mContext).linearChartSettings(graphView,minY,maxY)
    }

    fun linearChart(graph: GraphView, xAxis: List<Long>, yAxis:DoubleArray, offset: Int, color: Int, dotted:Boolean) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries()

            for (j in 0 .. yAxis.size-1) {
                val dataPoint = DataPoint(Date(xAxis[j]),yAxis[j])
                series.appendData(dataPoint, true,yAxis.size)
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

        // Set Yaxis bound
        graph.gridLabelRenderer.numHorizontalLabels = 10;
        graph.viewport.setMinY(minY*0.8)
        graph.viewport.setMaxY(maxY)
        graph.viewport.isYAxisBoundsManual = true;


        if (minY == 0.0) {
            graph.gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(mContext, SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        } else {
            graph.gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(mContext, SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        }
        graph.gridLabelRenderer.numVerticalLabels = 5
        graph.gridLabelRenderer.setHorizontalLabelsAngle(90);
        //if(minY == 0.0) graph.gridLabelRenderer.isHorizontalLabelsVisible = false
    }



}