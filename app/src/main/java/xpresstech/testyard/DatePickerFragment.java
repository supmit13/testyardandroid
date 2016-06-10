package xpresstech.testyard;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    OnDateSetListener ondateSet;

    public DatePickerFragment() {
    }

    public void setCallBack(OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        String mmstr = Integer.toString(month);
        if(mmstr.length() < 2){
            mmstr = '0' + mmstr;
            month = Integer.parseInt(mmstr);
        }
        day = args.getInt("day");
        String ddstr = Integer.toString(day);
        if(ddstr.length() < 2){
            ddstr = "0" + ddstr;
            day = Integer.parseInt(ddstr);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog picker = new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return (picker);
    }
}