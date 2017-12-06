package io.esparta.androidcommons

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import io.esparta.library.ui.extensions.disableView
import io.esparta.library.ui.extensions.enableView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt = findViewById<TextView>(R.id.txt)
        txt.disableView()
        txt.postDelayed({ txt.enableView() }, 1000)
    }
}
