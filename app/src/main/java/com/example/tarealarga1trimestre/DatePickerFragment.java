package com.example.tarealarga1trimestre;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface OnDateSelected {
        void onDate(LocalDate date);
    }

    private final OnDateSelected listener;

    public DatePickerFragment(OnDateSelected listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate hoy = LocalDate.now();

        return new DatePickerDialog(
                requireContext(),
                this,
                hoy.getYear(),
                hoy.getMonthValue() - 1,   // DatePicker usa meses 0–11
                hoy.getDayOfMonth()
        );
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // month viene 0–11 del DatePicker → convertir a 1–12
        LocalDate fecha = LocalDate.of(year, month + 1, day);
        listener.onDate(fecha);
    }
}
