package com.app.colormatch

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    val colors = listOf<String>("black", "blue", "green", "red") //"yellow", "orange", "purple", "pink", "brown")
    val colorsN = colors.size
    var correctAnswer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        getQuestion()
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
    }

    fun getQuestion() {
        val questionColor = colors[Random.nextInt(from = 0, until = colorsN)]
        val text = colors[Random.nextInt(from = 0, until = colorsN)]
        val answerColor = colors[Random.nextInt(from = 0, until = colorsN)]
        correctAnswer = (questionColor == answerColor)

        textQuestion.text = questionColor
        textAnswer.text = text
        textAnswer.setTextColor(Color.parseColor(answerColor))
    }

    fun checkAnswer(answer: Boolean) {
        if(answer == correctAnswer) {
            textResult.text = "Good"
        }
        else {
            textResult.text = "Wrong"
        }
        getQuestion()
    }


}
