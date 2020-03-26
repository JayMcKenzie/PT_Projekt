package imageRecognition

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel


class Grabber(capId: Int) : Runnable{
    internal val capture : VideoCapture
    init{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        capture = VideoCapture(capId)
    }

    private var window : JFrame? = null

    private fun transform(mat: Mat):Mat{
        val modFrame = mat.submat(0,mat.rows(),160,mat.cols()-160)
        Imgproc.resize(
            modFrame,
            modFrame,
            Size(modFrame.width()/1.5, modFrame.height()/1.5)
        )
        val rotation = Imgproc.getRotationMatrix2D(Point(mat.cols()/2.0,mat.rows()/2.0),-90.0,1.0)
        Imgproc.warpAffine(modFrame,modFrame,rotation,Size(modFrame.rows().toDouble(),modFrame.cols().toDouble()))
        return modFrame
    }

    override fun run() {

        while (capture.isOpened){
            val frame = Mat()
            if(capture.read(frame)){
                setImage(transform(frame))
            }
            else Thread.sleep(10)
        }
    }

    fun makeWindow(title:String? = null){
        window =
            if (title == null) JFrame()
            else JFrame(title)
        window!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val label = JLabel()
        label.preferredSize = Dimension(100, 60)
        window!!.contentPane = label
        window!!.setLocationRelativeTo(null)
        window!!.bounds = Rectangle(640,480)
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

    private fun setImage(mat:Mat){
        if (window == null){
            makeWindow()
        }
        (window!!.contentPane as JLabel).icon = ImageIcon(bufferedImage(mat))
        //window!!.pack()
    }
}