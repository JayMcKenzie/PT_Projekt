package imageRecognition

import org.opencv.core.Core
import org.opencv.videoio.VideoCapture

fun main(args: Array<String>){
    val grabber = Grabber(1)
    grabber.makeWindow("Wideo")
    val fred = Thread(grabber)
    fred.start()
    readLine()
    grabber.capture.release()
    fred.join()
}