package imageRecognition

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import org.opencv.imgcodecs.Imgcodecs
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.collections.ArrayList
import kotlin.math.*


class ImageGrabber(val filename: String) : Runnable{
    companion object{
        var matrix = Array(7){ IntArray(3) }
    }
    private val decoder = Decoder()
    private var image : Mat
    private var open:Boolean
    private val images = ArrayList<String>()
    private var imagesIterator = -1
    init{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        when{
            File(filename).isFile -> {
                images.add(filename)
            }
            File(filename).isDirectory -> {
                Files.list(File(filename).toPath()).forEach { images.add(it.toAbsolutePath().toString()) }
            }
            else -> {
                println(File(filename).absolutePath)
            }
        }
        image = Imgcodecs.imread(filename)
        open = true
    }

    private var window : JFrame? = null

    private fun getNextImage(): Mat{
        try{
            image = Imgcodecs.imread(images[imagesIterator+1])
            imagesIterator++
            println("Got image ${images[imagesIterator]}")
        }
        catch(ignored:Exception){ }
        return image
    }

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

    private fun printAll(collection: Array<IntArray>){
        println("[")
        for(row in collection){
            print("\t[ ")
            for(elem in row){
                print("$elem ")
            }
            println("]")
        }
        println("]")
    }

    override fun run() {
        var starttime = System.currentTimeMillis()
        var starttime2 = System.currentTimeMillis()
        var circles : Mat
        var frame = getNextImage()
        while (open){
            if (System.currentTimeMillis() - starttime >= 900) {
                if (System.currentTimeMillis() - starttime2 >= 5000) {
                    frame = getNextImage()
                    starttime2 = System.currentTimeMillis()
                }
                if(frame.cols() > 0 && frame.rows() > 0) {
                    val factor = 700.0/frame.height();
                    Imgproc.resize(frame,frame, Size(0.0,0.0),factor,factor)
                    circles = decoder.recognize(frame)
                    //drawCircles(frame, circles)
                    matrix = decoder.process(frame, circles) ?: matrix
                }
                starttime = System.currentTimeMillis()
            }
            /*if(frame.cols() > 0 && frame.rows() > 0) {
                setImageToWindow(frame)
            }*/
            //printAll(matrix)
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

    fun close(){
        open = false
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