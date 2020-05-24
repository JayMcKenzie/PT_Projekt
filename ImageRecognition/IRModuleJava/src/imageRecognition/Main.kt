package imageRecognition

fun main(args: Array<String>){
    val grabber = ImageGrabber("board.jpg")
    grabber.makeWindow("Wideo")
    val fred = Thread(grabber)
    fred.start()
    readLine()
    //grabber.capture.release()
    fred.join()
}