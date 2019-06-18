package com.app.colormatch

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start_game.*

class StartGame : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        buttonStart.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonRanking.setOnClickListener() {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }
    }
}
