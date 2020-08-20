package imageRecognition

fun main(args: Array<String>){
    val camGrabber = CamGrabber(0, "board.jpg")
    val grabber = ImageGrabber("board.jpg")
    grabber.makeWindow("Wideo")
    val fred1 = Thread(camGrabber)
    val fred2 = Thread(grabber)
    fred1.start()
    fred2.start()
    readLine()
    //grabber.capture.release()
    grabber.close()
    camGrabber.capture.release()
    fred1.join()
    fred2.join()
}