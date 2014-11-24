package it.keyorchestra.registrowebapp.fragments;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

@SuppressLint("ValidFragment")
public class DatePickerDialogFragment extends DialogFragment {
	
	private OnDateSetListener mDateSetListener;
	
	public DatePickerDialogFragment() {
		// TODO Auto-generated constructor stub
	}

	public DatePickerDialogFragment(OnDateSetListener callback) {
		mDateSetListener = (OnDateSetListener) callback;
	}
 
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar cal = Calendar.getInstance();
 
		return new DatePickerDialog(getActivity(),
				mDateSetListener, cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
}
