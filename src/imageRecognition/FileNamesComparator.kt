package imageRecognition

import java.nio.file.Path

class FileNamesComparator : Comparator<Path>{
    override fun compare(o1: Path?, o2: Path?): Int {
        val o1Number = extractNumber(o1.toString())
        val o2Number = extractNumber(o2.toString())
        return o1Number-o2Number
    }

    private fun extractNumber(name: String): Int {
        var i = 0
        i = try {
            val s = name.indexOf('_') + 1
            val e = name.lastIndexOf('.')
            val number = name.substring(s, e)
            number.toInt()
        } catch (e: java.lang.Exception) {
            0
        }
        return i
    }


}