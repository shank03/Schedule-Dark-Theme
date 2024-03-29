/*
 * Copyright (c) 2023, Shashank Verma <shashank.verma2002@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.alpha.dev.schedule_dark_theme

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.alpha.dev.schedule_dark_theme.appService.Interfaces
import com.alpha.dev.schedule_dark_theme.databinding.TimePickerBinding
import java.util.*

class TimePicker(context: Context, private val modeDark: Int, onTimeChangeListener: Interfaces.OnTimeChangeListener) : AppCompatDialog(context) {

    private val ctx: Context = context
    private val listener = onTimeChangeListener

    private val tag = "Time Picker"

    private var hour = -9
    private var min = -9

    private lateinit var binding: TimePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setBackgroundDrawableResource(R.drawable.bg_recent)

        val cM = Calendar.getInstance()

        binding.timePicker.hour = cM.get(Calendar.HOUR_OF_DAY)
        binding.timePicker.minute = cM.get(Calendar.MINUTE)
        hour = cM.get(Calendar.HOUR_OF_DAY)
        min = cM.get(Calendar.MINUTE)
        binding.timePicker.setIs24HourView(DateFormat.is24HourFormat(ctx))

        log(tag, "onCreate: is24Hour ?= ${binding.timePicker.is24HourView}", ctx)

        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.timePicker.setOnTimeChangedListener { _, i, i2 ->
            hour = i
            min = i2

            log(tag, "onTimeChange: Hour = $i \n Minute = $i2", ctx)
        }

        binding.doneBtn.setOnClickListener {
            val pref = PreferenceHelper(ctx)
            if (modeDark == DARK) {
                log(tag, "mode = DARK", ctx)
                val c = Calendar.getInstance().put(Calendar.HOUR_OF_DAY, hour).put(Calendar.MINUTE, min).put(Calendar.SECOND, 0)

                val time = c.timeInMillis
                log(tag, "Time = $time", ctx)

                if (isTimeSame(pref.getLong(TIME_DISABLE, DEFAULT_DISABLE_TIME), time)) {
                    makeToast(ctx, "Dark theme toggle time can't be same :/", duration = Toast.LENGTH_LONG)
                } else {
                    pref.putLong(TIME_ENABLE, time)
                    listener.onChange(time)
                    dismiss()
                }
            } else {
                val k = Calendar.getInstance().put(Calendar.HOUR_OF_DAY, hour).put(Calendar.MINUTE, min).put(Calendar.SECOND, 0)

                val time = k.timeInMillis
                log(tag, "Time = $time", ctx)

                if (isTimeSame(pref.getLong(TIME_ENABLE, DEFAULT_ENABLE_TIME), time)) {
                    makeToast(ctx, "Dark theme toggle time can't be same :/", duration = Toast.LENGTH_LONG)
                } else {
                    pref.putLong(TIME_DISABLE, time)
                    listener.onChange(time)
                    dismiss()
                }
            }
        }
    }

    private fun isTimeSame(initial: Long, new: Long): Boolean {
        val i = Calendar.getInstance().putTimeInMillis(initial)
        val n = Calendar.getInstance().putTimeInMillis(new)

        return i.get(Calendar.HOUR_OF_DAY) == n.get(Calendar.HOUR_OF_DAY) && i.get(Calendar.MINUTE) == n.get(Calendar.MINUTE)
    }
}