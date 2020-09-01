package game

import sample.Controller
import kotlin.concurrent.thread
import kotlin.random.Random

open class Game(player1: Player, player2: Player, private val controller: Controller) : GameCore(player1, player2), Runnable {

    private var startPlayer : Player? = null
    private var gameThread: Thread? = null
    var running = false

    var newMove : Pair<Int, Int>? = null

    fun start() {
        prepareBoard()
        prepareGame()
        startPlayer = if (Random.nextInt(1, 3) == 1) player1 else player2
        currentPlayer = startPlayer
        gameThread = thread {
            try {
                run()
            } catch (ignored: InterruptedException) {}
        }
    }

    fun start(inputMatrix:Array<IntArray>){
        prepareBoard(inputMatrix)
        prepareGame()
        startPlayer = if (Random.nextInt(1, 3) == 1) player1 else player2
        currentPlayer = startPlayer
        gameThread = thread {
            try {
                run()
            } catch (ignored: InterruptedException) {}
        }
    }

    override fun run() {
        var lastMoveWasCapture = false
        var lastCapturePawn = -1
        running = true
        while (!gameOver) {
            //checkCaptureObligation()
            ruch@while (true) {
                controller.fillBoard(matrix)
                //tutaj trzeba będzie wrzucić ruch z klasy zasad (zmiana macierzy odebranych będzie nowym ruchem)
                var nextMove = newMove
                while (nextMove == null) {
                    Thread.sleep(100)
                    nextMove = newMove
                }
                newMove = null
                try {
                    lastMoveWasCapture = captureRequired
                    if(lastMoveWasCapture)
                        lastCapturePawn = nextMove.second
                    move(nextMove.first, nextMove.second)
                    //controller.wyswietlWiadomosc("")
                } catch (e: InvalidMoveException) {
                    controller.wyswietlWiadomosc("Nie można wykonać ruchu")
                    continue@ruch
                } catch (e: CaptureRequiredException) {
                    controller.wyswietlWiadomosc("Należy wykonać bicie")
                    continue@ruch
                }
                catch (e: Throwable){
                    println("Game.kt:100 - " + e.localizedMessage)
                }
                checkCaptureObligation()
                if(!(lastMoveWasCapture && captureRequired && captureMoves.find { it.first == lastCapturePawn } is Pair<Int, Int>))
                    break@ruch
            }
            currentPlayer = getOponent(currentPlayer)
            Thread.sleep(100)
        }
        controller.fillBoard(matrix)
        controller.wyswietlWiadomosc("Koniec gry! Wygrał gracz ${winner!!.number}")
        println("End of game")
    }

    fun stop(){
        gameThread?.interrupt()
    }
}