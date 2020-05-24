package imageRecognition

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.math.*


class Grabber(capId: Int) : Runnable{
    internal val capture : VideoCapture
    private val decoder = Decoder()
    init{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        capture = VideoCapture()
        capture.open(capId)
    }

    private var window : JFrame? = null

    private fun rotate(image: Mat, angle: Double = -90.0) {
        //Calculate size of new matrix
        val radians = Math.toRadians(angle)
        val sin = abs(sin(radians))
        val cos = abs(cos(radians))
        val newWidth = (image.width() * cos + image.height() * sin).toInt()
        val newHeight = (image.width() * sin + image.height() * cos).toInt()

        // rotating image
        val center = Point((newWidth / 2).toDouble(), (newHeight / 2).toDouble())
        val rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0) //1.0 means 100 % scale
        val size = Size(newWidth.toDouble(), newHeight.toDouble())
        Imgproc.warpAffine(image, image, rotMatrix, size)
    }

    private fun transform(mat: Mat):Mat{
        var modFrame = mat.submat(0,mat.rows(),160,mat.cols()-160)
//        rotate(modFrame)

        val center = Point(modFrame.rows()/2.0,modFrame.cols()/2.0)
        val rotation = Imgproc.getRotationMatrix2D(center,-90.0,1.0)
        Imgproc.warpAffine(
            modFrame,
            modFrame,
            rotation,
            Size(modFrame.height()+105.0,modFrame.width()+122.0)
        )

        modFrame = modFrame.submat(105,modFrame.rows(),122,modFrame.cols())

        Imgproc.resize(
            modFrame,
            modFrame,
            Size(modFrame.width()/1.5, modFrame.height()/1.5)
        )
        return modFrame
    }

    private fun drawCircles(image:Mat, circles:Mat){
        for(col in 0 until circles.cols()){
            val x = circles[0,col][0]
            val y = circles[0,col][1]
            val r = circles[0,col][2]
            Imgproc.circle(image, Point(x, y),r.toInt(), Scalar(255.0, 0.0, 0.0), 5)
        }
    }

    override fun run() {
        var starttime = System.currentTimeMillis()
        var circles = Mat()
        while (capture.isOpened){
            val frame = Mat()
            if (System.currentTimeMillis() - starttime >= 1000) {
                if(capture.read(frame)){
                    circles = decoder.recognize(frame)
                    drawCircles(frame,circles)
                    decoder.process(frame, circles)
                    starttime = System.currentTimeMillis()
                }
                setImageToWindow(transform(frame))
            }
            else Thread.sleep(10)
        }
    }

    fun makeWindow(title:String? = null){
        window =
            if (title == null) JFrame()
            else JFrame(title)
        window!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        window!!.isResizable = false
        val label = JLabel()
        label.preferredSize = Dimension(480, 640)
        window!!.contentPane = label
        //window!!.bounds = Rectangle(480, 640)
        window!!.setLocationRelativeTo(null)
        window!!.isVisible = true
    }

    var windowTitle : String
        get() = window?.title ?: ""
        set(newTitle){
            window?.title = newTitle
        }

    private fun bufferedImage(mat:Mat?):BufferedImage{
        if(mat != null) {
            val image = BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR)
            mat.get(0, 0, (image.raster.dataBuffer as DataBufferByte).data)
            return image
        }
        return BufferedImage(200,200,BufferedImage.TYPE_BYTE_GRAY)
    }

    private fun setImageToWindow(mat:Mat){
        if (window == null){
            makeWindow()
        }
        if(window!!.bounds.width != mat.width() || window!!.bounds.height != mat.height()) {
            val (x,y) = Pair(window!!.bounds.x, window!!.bounds.y)
            window!!.bounds = Rectangle(x,y,mat.width(), mat.height())
        }
        (window!!.contentPane as JLabel).icon = ImageIcon(bufferedImage(mat))

    }
}