package io.esparta.androidcommons

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import io.esparta.library.ui.dialog.MonthYearPickerDialog
import io.esparta.library.ui.extensions.disableView
import io.esparta.library.ui.extensions.enableView

class MainActivity : AppCompatActivity() {

    private val picker by lazy {
        MonthYearPickerDialog().apply {
            onDateSetExecutor = { m, y ->
                Toast.makeText(this@MainActivity, "Month $m Year $y", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt = findViewById<TextView>(R.id.txt)
        txt.disableView()
        txt.postDelayed({ txt.enableView() }, 1000)


        val bt = findViewById<TextView>(R.id.bt_picker)
        bt.setOnClickListener {
            picker.show(supportFragmentManager, "picker")
        }
    }
}
