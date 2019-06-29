package com.app.colormatch

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start_game.*
import java.lang.Exception
import java.net.URL

class StartGame : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        welcomeMessage.text = "Welcome ${getLogin()}"

        buttonStart.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonRanking.setOnClickListener() {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        buttonLogout.setOnClickListener() {
            logout()
        }
    }

    fun getLogin(): String {
        val shared = this.getSharedPreferences("com.app.colormatch.conf", 0)
        return shared.getString("login", "")
    }

    fun logout() {
        sendResultsToServer()

        val shared = this.getSharedPreferences("com.app.colormatch.conf", 0)
        val editor = shared.edit()
        editor.putString("login", null)
        editor.putInt("record", 0)
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    fun sendResultsToServer() {
        class SendResultsToServer : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                val url = "http://colormatchserver.herokuapp.com/add/points?login=${getLogin()}&points=${getRecord()}"
                try {
                    return URL(url).readText()
                } catch (e: Exception) {
                    return "noConnection"
                }
            }
        }
        SendResultsToServer().execute()
    }

    fun getRecord(): Int {
        val shared = this.getSharedPreferences("com.app.colormatch.conf", 0)
        return shared.getInt("record", 0)
    }
}