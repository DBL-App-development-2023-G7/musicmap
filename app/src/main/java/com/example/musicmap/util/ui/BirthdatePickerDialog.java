package com.example.musicmap.util.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;

import androidx.annotation.NonNull;

import java.util.Date;

public class BirthdatePickerDialog extends DatePickerDialog {
    public BirthdatePickerDialog(@NonNull Context context) {
        super(context);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -13);
        Date date = calendar.getTime();
        this.getDatePicker().setMaxDate(date.getTime());
    }

    public BirthdatePickerDialog(@NonNull Context context, OnDateSetListener listener) {
        this(context);
        this.setOnDateSetListener(listener);
    }
}
