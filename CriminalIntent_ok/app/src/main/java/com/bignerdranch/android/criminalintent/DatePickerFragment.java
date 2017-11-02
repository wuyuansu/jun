package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date";
    private static final String ARG_DATE = "date";

    private int year;
    private int month;
    private int day;

    private DatePicker mDatePicker;
    private Button mButton;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.from(getActivity()).inflate(R.layout.dialog_date, null);
//
//        final Date date = (Date) getArguments().getSerializable(ARG_DATE);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        year = calendar.get(Calendar.YEAR);
//        month = calendar.get(Calendar.MONTH);
//        day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
//        mDatePicker.init(year, month, day, null);
//
//        mButton = (Button) v.findViewById(R.id.button_ok);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                year = mDatePicker.getYear();
//                month = mDatePicker.getMonth();
//                day = mDatePicker.getDayOfMonth();
//                Date date1 = new GregorianCalendar(year, month, day)
//                        .getTime();
//                sendResult(Activity.RESULT_OK, date1);
//            }
//        });
//
//        return v;
//    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Log.d("sd","" + date.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year, month, day).getTime();
                                sendResult(Activity.RESULT_OK, date);
                            }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            getActivity().setResult(resultCode, intent);
        }
        else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);

            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }

}
