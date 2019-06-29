package com.app.colormatch

import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.app.colormatch.sql.DatabaseHelper
import com.app.colormatch.sql.Player
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.net.URL
import kotlin.math.ceil
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var login : String
    val dbHelper : DatabaseHelper = DatabaseHelper(this@MainActivity)

    val colors = listOf<String>("blue", "green", "red", "black", "purple", "magenta", "cyan", "gray", "yellow")
    var colorsN = 4
    var nextLvlPoints = 20
    var correctAnswer: Boolean = false

    val redHeart = String(Character.toChars(0x2764))
    val blackHeart = String(Character.toChars(0x1F5A4))
    val livesArray = listOf<String>("""$blackHeart$blackHeart$blackHeart""", """$redHeart$blackHeart$blackHeart""", """$redHeart$redHeart$blackHeart""", """$redHeart$redHeart$redHeart""")

    var leftLives = 3
    var points = 0

    var bestScoreFromServer = 0
    var bestScore = 0

    var timer: Timer = Timer(5000)
    inner class Timer(millis: Long) : CountDownTimer(millis, 5000) {
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
        getLogin()

        buttonYes.setOnClickListener() {
            checkAnswer(true)
        }
        buttonNo.setOnClickListener() {
            checkAnswer(false)
        }
        buttonNewGame.setOnClickListener() {
            newGame()
        }
        buttonReturn.setOnClickListener() {
            timer.cancel()
            if (bestScoreFromServer < bestScore) {
                sendResultsToServer()
            }
            this.finish()
        }

        getRecord()
        newGame()
    }


    fun newGame() {
        leftLives = 3
        points = 0
        colorsN = 4
        nextLvlPoints = 20
        livesText.text = livesArray[leftLives]
        textResult.text = "Your points: 0"
        textRecord.text = "Your record: ${bestScore}"
        buttonYes.isEnabled = true
        buttonNo.isEnabled = true
        getQuestion()
    }


    fun getQuestion() {
        val questionColor = colors[Random.nextInt(from = 0, until = colorsN)]
        val text = colors[Random.nextInt(from = 0, until = colorsN)]
        var answerColor = questionColor
        if (Random.nextInt(from = 0, until = 4) != 0)
            answerColor = colors[Random.nextInt(from = 0, until = colorsN)]
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
        val newPoints = ceil(answeredTime / 1000.0 / 2)
        points += newPoints.toInt()
        textResult.text = "Your points: $points"

        if (points < 140) {
            if (points > nextLvlPoints) {
                nextLvlPoints += 20
                colorsN += 1
            }
        }

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
        timer.cancel()
        buttonYes.isEnabled = false
        buttonNo.isEnabled = false
        if (points > bestScore) {
            setRecord(points)
            sendResultsToServer()
            textRecord.text = "Your NEW record: ${bestScore}"
        }
        makeDialog()
    }

    fun makeDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("END GAME")
        builder.setMessage("Great job! Your result: $points")

        builder.setPositiveButton("OK") {_, _ -> }

        val dialog :AlertDialog = builder.create()
        dialog.show()
    }


    fun getLogin() {
        val shared  = this.getSharedPreferences("com.app.colormatch.conf", 0)
        login = shared.getString("login", null)
    }

    fun getRecord() {
        val shared  = this.getSharedPreferences("com.app.colormatch.conf", 0)
        bestScore = shared.getInt("record", 0)

        class BestScoreFromServer : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                val url = "http://colormatchserver.herokuapp.com/get/points?login=$login"
                try {
                    return URL(url).readText()
                } catch (e: Exception) {
                    return "noConnection"
                }
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                if (result != "err" && result != "noConnection") {
                    val res = result?.split(" ")
                    val get = res?.get(0)
                    if (get != null) {
                        bestScoreFromServer = get.toInt()
                    }

                    if (bestScoreFromServer > bestScore) {
                        setRecord(bestScoreFromServer)
                    }

                    if (bestScoreFromServer < bestScore) {
                        sendResultsToServer()
                    }
                }
            }
        }
        BestScoreFromServer().execute()

    }

    fun setRecord(newRecord: Int) {
        bestScore = newRecord
        val shared  = this.getSharedPreferences("com.app.colormatch.conf", 0)
        val editor = shared!!.edit()
        editor.putInt("record", bestScore)
        editor.apply()
        textRecord.text = "Your record: ${bestScore}"

        dbHelper.updatePlayer(Player(login, bestScore))
    }


    fun sendResultsToServer() {
        class SendResultsToServer : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                val url = "http://colormatchserver.herokuapp.com/add/points?login=$login&points=$bestScore"
                try {
                    return URL(url).readText()
                } catch (e: Exception) {
                    return "noConnection"
                }
            }
        }
        SendResultsToServer().execute()
    }

}
