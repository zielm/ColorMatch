package com.app.colormatch

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.app.colormatch.sql.DatabaseHelper
import com.app.colormatch.sql.Player
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_start_game.*
import java.io.StringReader
import java.lang.Exception
import java.net.URL

class StartGame : AppCompatActivity() {

    val dbHelper : DatabaseHelper = DatabaseHelper(this@StartGame)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        welcomeMessage.text = "Welcome ${getLogin()}"
        saveDatabaseFromServer()

        buttonStart.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonRanking.setOnClickListener() {
            sendResultsToServer()
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        buttonLogout.setOnClickListener() {
            sendResultsToServer()
            logout()
        }
    }

    fun getLogin(): String {
        val shared = this.getSharedPreferences("com.app.colormatch.conf", 0)
        return shared.getString("login", "")
    }

    fun logout() {
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

    fun saveDatabaseFromServer() {
        var players: MutableList<Player> = mutableListOf()
        val klaxon = Klaxon()

        class GetRankingFromServer(private var activity: StartGame) : AsyncTask<String, String, String>() {
            override fun doInBackground(vararg params: String?): String? {
                val url = "http://colormatchserver.herokuapp.com/get/ranking"
                try {
                    return URL(url).readText()
                } catch (e: Exception) {
                    return "noConnection"
                }
            }

            override fun onPostExecute(result: String?) {
                if (result == "noConnection") {
                    println("Nie można połączyć z serwerem")
                } else {
                    JsonReader(StringReader(result)).use { reader ->
                        reader.beginArray {
                            while (reader.hasNext()) {
                                val newPlayer = klaxon.parse<Player>(reader)
                                players.add(newPlayer!!)
                            }
                        }
                    }
                    updateDatabase(players)
                }
            }
        }
        GetRankingFromServer(this).execute()
    }


    fun updateDatabase(players: MutableList<Player>) {
        for (i in 0 until players.size) {
            if (dbHelper.checkIfPlayerExists(players[i])) {
                dbHelper.updatePlayer(players[i])
            }
            else
                dbHelper.addPlayer(players[i])
        }
    }

}