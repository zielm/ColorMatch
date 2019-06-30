package com.app.colormatch

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.app.colormatch.sql.DatabaseHelper
import com.app.colormatch.sql.Player
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_ranking.*
import java.io.StringReader
import java.net.URL

class RankingActivity : AppCompatActivity() {

    var players: MutableList<Player> = mutableListOf()
    val klaxon = Klaxon()

    val dbHelper : DatabaseHelper = DatabaseHelper(this@RankingActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        getRanking()

        button_return.setOnClickListener() {
            this.finish()
        }
    }


    fun getRanking() {
        class GetRankingFromServer(private var activity: RankingActivity) : AsyncTask<String, String, String>() {
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
                    updateDatabase()
                }
                makeRanking()
            }
        }
        GetRankingFromServer(this).execute()
    }

    fun updateDatabase() {
        for (i in 0 until players.size) {
            if (dbHelper.checkIfPlayerExists(players[i])) {
                val pointsInSQLite = dbHelper.getPoints(players[i].login.toString())
                if(pointsInSQLite > players[i].points) {
                    sendResultsToServer(players[i].login.toString(), pointsInSQLite)
                }
                else if (pointsInSQLite < players[i].points) {
                    dbHelper.updatePlayer(players[i])
                }
            }
            else
                dbHelper.addPlayer(players[i])
        }
    }


    fun makeRanking() {
        var showNames = ""
        var showPoints = ""
        var showNumbers = ""
        var limit = 10
        players = dbHelper.getAllPlayers()
        if (players.size < 10) limit = players.size
        for (i in 0 until limit) {
            showNumbers += "${i + 1}. \t\t\n"
            showNames += "${players[i].login} \t\t\t \n"
            showPoints += "${players[i].points}\n"
        }
        textView_number.text = showNumbers
        textView_names.text = showNames
        textView_points.text = showPoints
    }

    fun sendResultsToServer(login: String, bestScore: Int) {
        class SendResultsToServer : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                val url = "http://colormatchserver.herokuapp.com/add/points?login=$login&points=$bestScore"
                try {
                    return URL(url).readText()
                } catch (e: java.lang.Exception) {
                    return "noConnection"
                }
            }
        }
        SendResultsToServer().execute()
    }


    fun getLogin() : String {
       val shared  = this.getSharedPreferences("com.app.colormatch.conf", 0)
       return shared.getString("login", null)
    }
}

