package lestelabs.binanceapi.tools

class Tools {

    fun findMin(list: DoubleArray, offset:Int, initialValue:Double): Double {
        var min = initialValue
        for (i in offset .. list.size-1) {
            if (list[i] < min) min = list[i]
        }
        return min
    }

    fun findMax(list: DoubleArray, offset:Int, initialValue:Double): Double {
        var max = initialValue
        for (i in offset .. list.size-1) {
            if (list[i] > max) max = list[i]
        }
        return max
    }

}