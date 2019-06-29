package com.app.colormatch.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Game.db"
        // Tabela
        private val TABLE_NAME = "Players"

        private val COL_LOGIN = "Login"
        private val COL_RESULT = "Result"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ($COL_LOGIN TEXT PRIMARY KEY, " +
                "$COL_RESULT INTEGER NOT NULL)")
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun resetDatabase() {
        val db = this.writableDatabase
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ($COL_LOGIN TEXT PRIMARY KEY, " +
                "$COL_RESULT INTEGER NOT NULL)")
        db!!.execSQL(CREATE_TABLE)
    }

    fun addPlayer(player: Player) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_LOGIN, player.login)
        values.put(COL_RESULT, player.points)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updatePlayer(player: Player) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_RESULT, player.points)

        db.update(TABLE_NAME, values, "$COL_LOGIN = '${player.login}'", null)
        db.close()
    }

    fun checkIfPlayerExists(player: Player): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_LOGIN = '${player.login}'", null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }


    fun getAllPlayers(): MutableList<Player> {
        val db = this.readableDatabase
        val cursor =  db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        var players = mutableListOf<Player>()
        if (cursor.moveToFirst()) {
            do {
                val player = Player()
                player.login = cursor.getString(cursor.getColumnIndex(COL_LOGIN))
                player.points = cursor.getInt(cursor.getColumnIndex(COL_RESULT))

                players.add(player)
            } while (cursor.moveToNext())
        }

        return players
    }

    fun getCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        if(cursor != null) {
            cursor.moveToFirst()
            return cursor.getInt(0)
        }
        return 0
    }
}
