package com.example.musicmap.util.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.widget.EditText;

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

    /**
     * This method returns an {@code OnDateSetListener} that applies the selected date to the given
     * EditText attribute.
     *
     * @param editText the given {@code EditText} view to apply the selected date
     * @return the OnDateSetListener
     */
    public static OnDateSetListener applyDateToEditText(EditText editText) {
        return (datePicker, year, month, day) -> {
            month++;
            String date = day + "/" + month + "/" + year;
            editText.setText(date);
        };
    }

}
