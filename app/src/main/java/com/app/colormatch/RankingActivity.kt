package com.app.colormatch

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_ranking.*
//import kotlinx.android.synthetic.main.item_list_layout.view.*
import java.io.StringReader
import java.lang.Exception
import java.net.URL

class RankingActivity : AppCompatActivity() {

    class PlayerInfo(val login: String, val points: Int)

    val players: MutableList<PlayerInfo> = mutableListOf()
    val klaxon = Klaxon()

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
                                val newPlayer = klaxon.parse<PlayerInfo>(reader)
                                players.add(newPlayer!!)
                            }
                        }
                    }
                }
                makeRanking()
//                listView.adapter = PlayersListViewAdapter(activity, players)
            }
        }
        GetRankingFromServer(this).execute()
    }


    fun makeRanking() {
        var showNames = ""
        var showPoints = ""
        var showNumbers = ""
        var limit = 10
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
}

