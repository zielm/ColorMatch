package com.app.colormatch.sql

import android.content.ContentValues
import android.content.Context
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
        private val COL_PASSWORD = "Password"
        private val COL_RESULT = "Result"
    }

    // PLAYERS
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ($COL_LOGIN TEXT PRIMARY KEY, " +
                "$COL_PASSWORD TEXT NOT NULL, $COL_RESULT INTEGER NOT NULL)")
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun addPlayer(player: Player) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_LOGIN, player.login)
        values.put(COL_PASSWORD, player.password)
        values.put(COL_RESULT, player.result)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updatePlayer(player: Player) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_LOGIN, player.login)
        values.put(COL_PASSWORD, player.password)
        values.put(COL_RESULT, player.result)

        db.update(
            TABLE_NAME, values, "$COL_LOGIN = ?",
            arrayOf(player.login)        )
        db.close()
    }

    fun checkPlayer(login: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COL_LOGIN)
        val db = this.readableDatabase

        // selection criteria
        val selection = "$COL_LOGIN = ?"

        // selection argument
        val selectionArgs = arrayOf(login)

        // query user table with condition
        val cursor = db.query(
            TABLE_NAME, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)  //The sort order


        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }

    fun checkPlayer(login: String, password: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COL_LOGIN)

        val db = this.readableDatabase

        // selection criteria
        val selection = "$COL_LOGIN = ? AND $COL_PASSWORD = ?"

        // selection arguments
        val selectionArgs = arrayOf(login, password)

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        val cursor = db.query(
            TABLE_NAME, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0)
            return true

        return false

    }

    fun getResult(login: String) : Int {
        // array of columns to fetch
        val db = this.readableDatabase

        // selection criteria
        val selection = "$COL_LOGIN = ?"

        // selection argument
        val selectionArgs = arrayOf(login)

        // query user table with condition
        val cursor = db.query(
            TABLE_NAME, //Table to query
            arrayOf(COL_RESULT),  //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)  //The sort order

        var points = 0
        if(cursor.moveToFirst()) {
            cursor.moveToFirst()
            points = Integer.parseInt(cursor.getString(0))
            cursor.close()
        }

        db.close()
        return points
    }

    fun setResult(login : String, points : Int) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_LOGIN, login)
        values.put(COL_RESULT, points)

        db.update(
            TABLE_NAME, values, "$COL_LOGIN = ?",
            arrayOf(login)        )
        db.close()
    }
}
