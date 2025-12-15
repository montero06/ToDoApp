package com.example.tarealarga1trimestre;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    private final OnDateSelectedListener listener;

    public DatePickerFragment(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();

        return new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    // month +1 porque Calendar empieza en 0
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    listener.onDateSelected(date);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
    }
}
