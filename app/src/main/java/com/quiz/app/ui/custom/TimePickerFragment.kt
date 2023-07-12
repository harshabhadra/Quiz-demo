package com.quiz.app.ui.custom

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class TimePickerFragment(private val pickListener: OnTimePickListener) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface OnTimePickListener{
        fun onTimePick(hourOfDay: Int,minute: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        pickListener.onTimePick(hourOfDay,minute)
        dialog?.dismiss()
    }
}