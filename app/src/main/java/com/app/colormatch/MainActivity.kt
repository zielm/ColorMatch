package com.app.colormatch

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.ceil
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    val colors = listOf<String>("black", "blue", "green", "red") //"yellow", "orange", "purple", "pink", "brown")
    val colorsN = colors.size
    var correctAnswer: Boolean = false

    val redHeart = String(Character.toChars(0x2764))
    val blackHeart = String(Character.toChars(0x1F5A4))
    val livesArray = listOf<String>("""$blackHeart$blackHeart$blackHeart""", """$redHeart$blackHeart$blackHeart""", """$redHeart$redHeart$blackHeart""", """$redHeart$redHeart$redHeart""")

    var leftLives = 3
    var points = 0

    var timer: Timer = Timer(10000)
    inner class Timer(millis: Long) : CountDownTimer(millis, 10000) {
        var timeLeft: Long = 0

        override fun onTick(timeUntilFinished: Long) {
            timeLeft = timeUntilFinished
        }

        override fun onFinish() {
            Toast.makeText(applicationContext, "You took too long!", Toast.LENGTH_LONG).show()
            loseLife()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }


    fun init() {
        buttonTrue.setOnClickListener() {
            checkAnswer(true)
        }
        buttonFalse.setOnClickListener() {
            checkAnswer(false)
        }
        buttonRefresh.setOnClickListener(){
            getQuestion()
            textResult.text = ""
        }

        newGame()
    }

    fun newGame() {
        leftLives = 3
        points = 0
        livesText.text = livesArray[leftLives]
        getQuestion()
    }


    fun getQuestion() {
        val questionColor = colors[Random.nextInt(from = 0, until = colorsN)]
        val text = colors[Random.nextInt(from = 0, until = colorsN)]
        val answerColor = colors[Random.nextInt(from = 0, until = colorsN)]
        correctAnswer = (questionColor == answerColor)

        textQuestion.text = questionColor
        textAnswer.text = text
        textAnswer.setTextColor(Color.parseColor(answerColor))
        timer.start()
    }

    fun checkAnswer(answer: Boolean) {
        val answeredTime = timer.timeLeft
        if(answer == correctAnswer) {
            addPoints(answeredTime)
        }
        else {
            loseLife()
        }
    }


    fun addPoints(answeredTime: Long) {
        var newPoints = ceil(answeredTime/1000.0)
        points += newPoints.toInt()
        textResult.text = "$points"

        getQuestion()
    }


    fun loseLife() {
        leftLives -= 1
        livesText.text = livesArray[leftLives]

        if (leftLives == 0) {
            endGame()
        }

        else {
            getQuestion()
        }
    }


    fun endGame() {
        makeDialog()
    }

    fun makeDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("END GAME")
        builder.setMessage("Great job! Your result: $points")

        builder.setPositiveButton("OK") {dialog, which ->
            this.finish()
        }
        val dialog :AlertDialog = builder.create()
        dialog.show()
    }


}
