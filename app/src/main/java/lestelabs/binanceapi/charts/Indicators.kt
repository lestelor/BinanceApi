package lestelabs.binanceapi.charts

import android.util.Log
import com.tictactec.ta.lib.Core
import com.tictactec.ta.lib.MInteger
import com.tictactec.ta.lib.RetCode
import lestelabs.binanceapi.tools.Tools
import java.lang.StringBuilder


object Indicators {
    val core = Core()
    val TAB = "Indicators"


    fun rsi(input: DoubleArray, period: Int): DoubleArray {
        val out = DoubleArray(input.size)
        val begin = MInteger()
        val length = MInteger()
        var retCode = RetCode.InternalError
        begin.value = -1
        length.value = -1
        retCode = core.rsi(0, input.size - 1, input, period, begin, length, out)

        return if (retCode == RetCode.Success) {
            out.dropLast(period).toDoubleArray()
        } else {
            Log.d(TAB, "TALIB Error rsi $retCode")
            doubleArrayOf()
        }
    }


    fun movingAverage(input: DoubleArray, period:Int): DoubleArray {
        // begin: Where to start to calculate the average, i.e. out[0] = sma(closePrice[begin]),
       // begin+lenght: Last calculated average out[begin+lenght]
        val begin = MInteger()
        val length = MInteger()
        val out = DoubleArray(input.size)
        var output = DoubleArray(input.size-period)
        val retCode = core.sma(0, input.size-1, input, period, begin, length, out)
        return if (retCode == RetCode.Success) {
            out.dropLast(period).toDoubleArray()
        } else {
            Log.d(TAB, "TALIB Error rsi $retCode")
            doubleArrayOf()
        }
    }


}