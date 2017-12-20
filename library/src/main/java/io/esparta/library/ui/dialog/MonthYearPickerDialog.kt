package io.esparta.library.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.NumberPicker
import io.esparta.library.R
import java.util.*


/**
 *
 * Created by gmribas on 16/10/17.
 */
class MonthYearPickerDialog: DialogFragment() {

    @Suppress("PrivatePropertyName")
    private val MAX_YEAR = 2099

    var onDateSetExecutor: ((month: Int, year: Int) -> Unit)? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val cal = Calendar.getInstance()

        val dialog = activity.layoutInflater.inflate(R.layout.picker_month_year, null)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.picker_month)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.picker_year)

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH) + 1

        val year = cal.get(Calendar.YEAR)
        yearPicker.minValue = year
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year

        builder.setView(dialog)
                .setPositiveButton(R.string.ok, { _, _ -> onDateSetExecutor?.invoke(monthPicker.value, yearPicker.value) })
                .setNegativeButton(R.string.cancel, { _, _ -> this@MonthYearPickerDialog.dialog.cancel() })
        return builder.create()
    }
}