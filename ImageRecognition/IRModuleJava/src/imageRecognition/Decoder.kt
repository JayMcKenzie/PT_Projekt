package imageRecognition

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class Decoder {

    val p1_data = arrayOf(10, 25, 1)
    val p2_data = arrayOf(40, 63, 2)
    val players = arrayOf(p1_data, p2_data)
    val blank_color = arrayOf(255, 255, 255)

    fun recognize(image: Mat):Mat{
        val imageGray = image.clone()
        Imgproc.cvtColor(image,imageGray,Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(imageGray,imageGray,100.0,255.0, Imgproc.THRESH_BINARY)
        val circles = Mat()
        Imgproc.HoughCircles(
            imageGray,
            circles,
            Imgproc.HOUGH_GRADIENT,
            1.5,
            imageGray.height()/10.0,
            90.0,
            20.0,
            20,
            30
        )
        return circles
    }

    private fun convertToArray(circles:Mat):Array<IntArray>{
        val circlesJava = Array(circles.cols()){ IntArray(2) }
        for(col in 0 until circles.cols()){
            circlesJava[col][0] = circles[0,col][0].toInt()
            circlesJava[col][1] = circles[0,col][1].toInt()
        }
        return circlesJava
    }

    @Deprecated("To delete")
    private fun printAll(collection: Array<Array<IntArray>>){
        println("[")
        for(row in collection){
            println("\t[")
            for(elem in row){
                print("\t\t[ ")
                for (value in elem){
                    print("$value ")
                }
                println("]")
            }
            println("\t]")
        }
        println("]")
    }

    fun get_player(image:Mat,x:Int,y:Int) : Int {
        if (x != -1 || y != -1) {
            val hsv = Mat()
            Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV)
            val pixel = hsv[x, y]
            if (pixel[1] < 50 && pixel[2] > 150) {
                return 0
            }
            for (player_data in players) {
                if (pixel[0] > player_data[0] && pixel[0] < player_data[1])
                    return player_data[2]
            }
            return -1
        }
        return -1
    }

    fun create_matrix(image:Mat, rows:Array<Array<IntArray>>):Array<IntArray> {
        val matrix = Array(7){ IntArray(3) }
        for ((i, row) in rows.withIndex()) {
            for(x in 0..2) {
                matrix[i][x] = get_player(image, row[x][1], row[x][0])
            }
        }
        return matrix
    }

    fun process(image: Mat, circles:Mat) : Array<IntArray>?{
        // println(circles)
        if (circles.cols() == 19){
            val circlesArray = convertToArray(circles)
            circlesArray.sortBy { it[1] }
            // println(circleVector)
            val rows = mutableListOf<Array<IntArray>>()
            for(i in arrayOf(0,3,6,9,10,13,16)){
                if(i==9){
                    val empty = intArrayOf(-1, -1)
                    rows.add(arrayOf(empty, circlesArray[i],empty))
                    continue
                }
                rows.add(circlesArray.copyOfRange(i,i+3))
            }
            return create_matrix(image,rows.toTypedArray())
        }
        return null
    }
}