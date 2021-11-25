package lestelabs.binanceapi.tools

class Tools {

    fun findMin(list: DoubleArray, offset:Int): Double {
        var min = 5000.0
        for (i in offset .. list.size-1) {
            if (list[i] < min) min = list[i]
        }
        return min
    }

    fun findMax(list: DoubleArray, offset:Int): Double {
        var max = 0.0
        for (i in offset .. list.size-1) {
            if (list[i] > max) max = list[i]
        }
        return max
    }

}