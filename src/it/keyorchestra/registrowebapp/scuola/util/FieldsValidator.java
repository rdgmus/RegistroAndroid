package it.keyorchestra.registrowebapp.scuola.util;

import android.text.Html;
import android.widget.CheckBox;
import android.widget.EditText;

public class FieldsValidator {
	public static boolean Is_Valid_Database_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Alphabets Only.</font>"));
			return false;
		} else if (!edt.getText().toString()
				.matches("^([a-zA-Z])([a-zA-Z0-9_])*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Format:^([a-zA-Z])([a-zA-Z0-9_])*</font>"));
			return false;
		}
		return true;
	}

	public static boolean Is_Valid_Database_Table_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Alphabets Only.</font>"));
			return false;
		} else if (!edt.getText().toString().matches("^([a-z])([a-zA-Z0-9_])*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Format:^([a-z])([a-zA-Z0-9_])*</font>"));
			return false;
		}
		return true;
	}

	public static boolean Is_Valid_Database_Field_Type(EditText edt)
	/**
	 * [INTEGER]|[BOOL]|[REAL]|[DOUBLE]|[FLOAT][CHAR]|[TEXT]|[BLOB]|[NUMERIC]|[
	 * DATETIME]
	 */
	throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Only Data Types</font>"));
			return false;
		} else if (!edt
				.getText()
				.toString()
				.matches(
						"(INTEGER)|(BOOL)|(REAL)|(DOUBLE)|(FLOAT)|(CHAR)|(TEXT)|(BLOB)|(NUMERIC)|(DATETIME)")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Format:INTEGER|BOOL|REAL|DOUBLE|FLOAT|CHAR|TEXT|BLOB|NUMERIC|DATETIME</font>"));
			return false;
		} else {
			edt.setError(null);
			return true;
		}
	}

	public static boolean Is_Valid_Database_Field_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Alphabets Only.</font>"));
			return false;
		} else if (!edt.getText().toString().matches("^([a-z_])([a-z0-9_])*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Format:^([a-z_])([a-z0-9_])*</font>"));
			return false;
		} else {
			edt.setError(null);
			return true;
		}
	}

	public static boolean Is_Valid_Database_Field_Default(EditText edt)
			throws NumberFormatException {
		if (!edt.getText().toString().matches("null")) {
			edt.setText("null");
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Only [null | [a-zA-Z0-9]*]</font>"));
			return false;
		} else {
			edt.setError(null);
			return true;
		}
	}

	public static boolean Is_Valid_Person_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Alphabets Only.</font>"));
			return false;
		} else if (!edt.getText().toString().matches("[a-zA-Z ]+")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Alphabets Only.</font>"));
			return false;
		}
		return true;
	}

	public static boolean Is_Valid_Password(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() < 8) {
			edt.setError(Html
					.fromHtml("<font color='red'>Length must be >= 8</font>"));
			return false;
		} else if (!edt.getText().toString().matches("^([a-zA-Z0-9]{8,})*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Format:^([a-zA-Z0-9]{8,})*</font>"));
			return false;
		}
		return true;
	}

	public static boolean Is_Valid_Email(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Alphabets Only.</font>"));
			return false;
		} else if (!edt
				.getText()
				.toString()
				.matches(
						"^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Accept Format:^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+</font>"));
			return false;
		}
		return true;
	}

	public static boolean CheckIsNull(CheckBox cbNotNull) {
		// TODO Auto-generated method stub
		if (cbNotNull.isChecked()) {
			cbNotNull.setChecked(false);
			// cbNotNull.setError(Html
			// .fromHtml("NOT NULL? MUST BE NULL IN PRIMARY KEY"));
			return false;
		} else {
			cbNotNull.setError(null);
			return true;
		}
	}

	public static boolean CheckIsPK(CheckBox cbPK) {
		// TODO Auto-generated method stub
		if (!cbPK.isChecked()) {
			cbPK.setChecked(true);
			// cbPK.setError(Html.fromHtml("OFF? MUST BE A PRIMARY KEY"));
			return false;
		} else {
			cbPK.setError(null);
			return true;
		}
	}

}
