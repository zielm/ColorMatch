package com.app.colormatch

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.app.colormatch.sql.DatabaseHelper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private lateinit var dbHelper : DatabaseHelper
    private lateinit var  builder : AlertDialog.Builder
    private var login : String? = null
    private var pwd : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()

        getLogin()
        if(login != null) {
            startMain()
        }

        buttonLogin.setOnClickListener() {
            if(tryToLogIn()) {
                setLogin()
                startMain()
            }
        }

        buttonRegister.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        dbHelper = DatabaseHelper(this@LoginActivity)
        builder = AlertDialog.Builder(this@LoginActivity)

    }


    private fun tryToLogIn() : Boolean {

        login = loginLogin.text.toString()
        pwd = loginPassword.text.toString()


        if(pwd.isNullOrEmpty() or login.isNullOrEmpty()) {
            makeDialog( "Pola nie mogą być puste")
            return false
        }

        if(!dbHelper.checkPlayer(login.toString())){
            makeDialog("Gracz nie istnieje")
            return false
        }

        if(!dbHelper.checkPlayer(login.toString(), pwd.toString())){
            makeDialog("Błędne hasło")
            return false
        }

        return true

    }


    fun makeDialog(message: String) {
        builder.setMessage(message)

        val dialog : AlertDialog = builder.create()
        dialog.show()
    }


    fun getLogin() {
        val shared  = this.getSharedPreferences("com.example.conf", 0)
        login = shared.getString("login", null)
    }

    fun setLogin() {
        val shared  = this.getSharedPreferences("com.example.conf", 0)
        val editor = shared.edit()
        editor.putString("login", login)
        editor.apply()
    }


    fun startMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}



