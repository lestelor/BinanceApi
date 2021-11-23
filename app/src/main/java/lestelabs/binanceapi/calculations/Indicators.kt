package lestelabs.binanceapi.calculations

import com.tictactec.ta.lib.Core
import com.tictactec.ta.lib.MInteger
import com.tictactec.ta.lib.RetCode
import java.lang.StringBuilder
import kotlin.math.absoluteValue


object Indicators {
    val core = Core()
    /**
     * The total number of periods to generate data for.
     */
    const val TOTAL_PERIODS = 100

    /**
     * The number of periods to average together.
     */
    const val PERIODS_AVERAGE = 30


    fun rsi(prices: DoubleArray, period: Int): DoubleArray {
        val output = DoubleArray(prices.size)
        val tempOutPut = DoubleArray(prices.size)
        val begin = MInteger()
        val length = MInteger()
        var retCode = RetCode.InternalError
        begin.value = -1
        length.value = -1
        retCode = core.rsi(0, prices.size - 1, prices, period, begin, length, tempOutPut)
        for (i in 0 until period) {
            output[i] = 0.0
        }
        var i = period
        while (0 < i && i < prices.size) {
            output[i] = tempOutPut[i - period]
            i++
        }
        return output
    }


    fun movingAverage(closePrice: DoubleArray, periodsAverage:Int): DoubleArray {
        // begin: Where to start to calculate the average, i.e. out[0] = sma(closePrice[begin]),
       // begin+lenght: Last calculated average out[begin+lenght]
        val begin = MInteger()
        val length = MInteger()
        val out = DoubleArray(closePrice.size)
        val retCode = core.sma(0, closePrice.size-1 , closePrice, periodsAverage, begin, length, out)
        if (retCode == RetCode.Success) {
            println("Output Start Period: " + begin.value)
            println("Output End Period: " + (begin.value + length.value - 1))
            for (i in begin.value until begin.value + length.value) {
                val line = StringBuilder()
                line.append("Period #")
                line.append(i)
                line.append(" close=")
                line.append(closePrice[i])
                line.append(" mov_avg=")
                line.append(out[i - begin.value])
                println(line.toString())
            }
        } else {
            println("Error")
        }
        return out
    }
}