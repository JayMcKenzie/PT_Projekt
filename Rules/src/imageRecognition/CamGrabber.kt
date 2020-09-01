package imageRecognition

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.videoio.VideoCapture

class CamGrabber(capId:Int, val filename:String) : Runnable{
    val capture : VideoCapture

    init{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        capture = VideoCapture()
        capture.open(capId)
    }

    override fun run() {
        var starttime = System.currentTimeMillis()
        while (capture.isOpened){
            val frame = Mat()
            if (System.currentTimeMillis() - starttime >= 1000) {
                if(capture.read(frame)){
                    Imgcodecs.imwrite(filename, frame)
                    starttime = System.currentTimeMillis()
                }
            }
            else Thread.sleep(10)
        }
    }
}