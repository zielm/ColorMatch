package com.app.colormatch

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.app.colormatch.sql.DatabaseHelper
import com.app.colormatch.sql.Player
import kotlinx.android.synthetic.main.activity_register.*
import java.net.URL

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper : DatabaseHelper
    private lateinit var  builder : AlertDialog.Builder
    private lateinit var  dialog : AlertDialog
    private var login : String? = null
    private var pwd : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()

        buttonRegister.setOnClickListener() {
            if(validate()) {
//                makeDialog("Wait a moment. We're trying to connect to the server")


                class addPlayer(private val activity: RegisterActivity) : AsyncTask<Void, Void, String>() {
                    override fun doInBackground(vararg params: Void?): String? {
                        val url = "http://colormatchserver.herokuapp.com/player/add?login=$login&password=$pwd"
                        try {
                            return URL(url).readText()
                        }catch (e: Exception){
                            return "noConnection"
                        }
                    }

                    override fun onPostExecute(result: String?) {
//                        dialog?.dismiss()
                        if (result=="ok"){
                            makeDialog("New account created. You can log in now")
                            startActivity(Intent(activity, LoginActivity::class.java))
                            activity.finish()
                        }
                        else {
                            if(result=="noConnection"){
                                makeDialog("Can't connect with the server")
                            } else
                                makeDialog("Login is already being used")
                        }
                    }
                }

                addPlayer(this).execute()

            }
        }
    }

    private fun init() {
        dbHelper = DatabaseHelper(this@RegisterActivity)
        builder = AlertDialog.Builder(this@RegisterActivity)

    }

    fun validate() : Boolean {
        login = registerLogin.text.toString()
        pwd = registerPassword.text.toString()
        val pwd2 = registerPassword2.text.toString()

        if(pwd.isNullOrEmpty() or login.isNullOrEmpty()) {
            makeDialog( "The fields cannot be empty")
            return false
        }

        if (!pwd.equals(pwd2)) {
            makeDialog("Passwords aren't the same")
            return false
        }

        return true

    }

    fun makeDialog(message: String) {
        builder.setMessage(message)

        dialog = builder.create()
        dialog.show()
    }
}

