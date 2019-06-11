package com.app.colormatch

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        val thread = Thread(){
            run {
                Thread.sleep(3000)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        }
        thread.start()
    }
}
