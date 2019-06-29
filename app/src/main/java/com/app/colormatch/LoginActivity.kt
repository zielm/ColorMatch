package com.app.colormatch

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.app.colormatch.sql.DatabaseHelper
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.net.URL

class LoginActivity : AppCompatActivity() {


    private lateinit var dbHelper : DatabaseHelper
    private lateinit var  builder : AlertDialog.Builder
    private lateinit var dialog : AlertDialog
    private var login : String? = null
    private var pwd : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        errorMessage.text = ""

        getLogin()
        if(login != null) {
            startMain()
        }

        buttonLogin.setOnClickListener() {
//            makeDialog("Wait a moment. We're trying to connect to the server")
            class checkLogin(private var activity : LoginActivity) : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                val url = "http://colormatchserver.herokuapp.com/check/pswd?login=${loginLogin.text.toString()}&pswd=${loginPassword.text.toString()}"
                try {
                    return URL(url).readText()
                } catch(e: Exception) {
                    return "noConnection"
                }
            }

            override fun onPostExecute(result: String?) {
                if (result == "ok") {
                    setLogin(loginLogin.text.toString())
                    startMain()
                }
                else {
//                    dialog?.dismiss()
                    if (result == "noConnection") {
                        makeDialog("Can't connect with the server")
                    } else {
                        makeDialog("Wrong login or password")
                    }
                }
            }
            }
            checkLogin(this).execute()
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


    fun makeDialog(message: String) {
        builder.setMessage(message)

        dialog = builder.create()
        dialog.show()
    }


    fun getLogin() {
        val shared  = this.getSharedPreferences("com.app.colormatch.conf", 0)
        login = shared.getString("login", null)
    }

    fun setLogin(playerLogin: String) {
        val shared  = this.getSharedPreferences("com.app.colormatch.conf", 0)
        val editor = shared!!.edit()
        editor.putString("login", playerLogin)
        editor.apply()
    }


    fun startMain() {
        val intent = Intent(this, StartGame::class.java)
        startActivity(intent)
        this.finish()
    }
}



