package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by Administrator on 2017/9/21.
 */

public class DatePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(CrimeFragment.DIALOG_DATE);
        return DatePickerFragment.newInstance(date);
    }
}
