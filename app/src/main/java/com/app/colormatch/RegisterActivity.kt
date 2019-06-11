package com.app.colormatch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.app.colormatch.sql.DatabaseHelper
import com.app.colormatch.sql.Player
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper : DatabaseHelper
    private lateinit var  builder : AlertDialog.Builder
    private var login : String? = null
    private var pwd : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()

        buttonRegister.setOnClickListener() {
            if(validate()) {
                createNewPlayer()
            }
        }
    }

    private fun init() {
        dbHelper = DatabaseHelper(this@RegisterActivity)
        builder = AlertDialog.Builder(this@RegisterActivity)

    }

    fun validate() : Boolean {
        builder.setTitle("Błąd!")

        login = registerLogin.text.toString()
        pwd = registerPassword.text.toString()
        val pwd2 = registerPassword2.text.toString()

        if(pwd.isNullOrEmpty() or login.isNullOrEmpty()) {
            makeDialog( "Pola nie mogą być puste")
            return false
        }
        if(dbHelper.checkPlayer(login.toString())){
            makeDialog("Gracz o podanym loginie już istnieje")
            return false
        }
        if (!pwd.equals(pwd2)) {
            makeDialog("Hasła się nie zgadzają")
            return false
        }

        return true

    }


    fun createNewPlayer() {

        val player = Player(login.toString(), pwd.toString())
        dbHelper.addPlayer(player)

        builder.setTitle("Sukces")
        builder.setMessage("Stworzono nowe konto. Możesz się teraz zalogować!")
        builder.setPositiveButton("OK") {dialog, which ->
            this.finish()
        }
        val dialog : AlertDialog = builder.create()
        dialog.show()

    }



    fun makeDialog(message: String) {
        builder.setMessage(message)

        val dialog : AlertDialog = builder.create()
        dialog.show()
    }
}

