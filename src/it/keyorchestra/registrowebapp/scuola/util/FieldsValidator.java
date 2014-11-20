package it.keyorchestra.registrowebapp.scuola.util;

import android.text.Editable;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.EditText;

public class FieldsValidator {
	public static boolean Is_Valid_Database_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (!edt.getText().toString()
				.matches("^([a-zA-Z])([a-zA-Z0-9_])*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:^([a-zA-Z])([a-zA-Z0-9_])*</font>"));
			return false;
		}
		edt.setError(null);
		return true;
	}

	public static boolean Is_Valid_Database_Table_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (!edt.getText().toString().matches("^([a-z])([a-zA-Z0-9_])*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:^([a-z])([a-zA-Z0-9_])*</font>"));
			return false;
		}
		edt.setError(null);

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
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (!edt
				.getText()
				.toString()
				.matches(
						"(INTEGER)|(BOOL)|(REAL)|(DOUBLE)|(FLOAT)|(CHAR)|(TEXT)|(BLOB)|(NUMERIC)|(DATETIME)")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:INTEGER|BOOL|REAL|DOUBLE|FLOAT|CHAR|TEXT|BLOB|NUMERIC|DATETIME</font>"));
			return false;
		}
		edt.setError(null);
		return true;
	}

	public static boolean Is_Valid_Database_Field_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (!edt.getText().toString().matches("^([a-z_])([a-z0-9_])*")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:^([a-z_])([a-z0-9_])*</font>"));
			return false;
		}
		edt.setError(null);
		return true;

	}

	public static boolean Is_Valid_Database_Field_Default(EditText edt)
			throws NumberFormatException {
		if (!edt.getText().toString().matches("null")) {
			edt.setText("null");
			edt.setError(Html
					.fromHtml("<font color='red'>Accetta solo [null] oppure [a-zA-Z0-9]*]</font>"));
			return false;
		}
		edt.setError(null);
		return true;

	}

	public static boolean Is_Valid_Person_Name(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (!edt.getText().toString().matches("[a-zA-Z ]+")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:[a-zA-Z ]</font>"));
			return false;
		}
		edt.setError(null);
		return true;
	}

	public static boolean Is_Valid_Password(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() < 4) {
			edt.setError(Html
					.fromHtml("<font color='red'>La lunghezza deve essere >= 4</font>"));
			return false;
		} else if (!edt.getText().toString()
				.matches("((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,20})")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,20})</font>"));
			return false;
		}
		edt.setError(null);
		return true;
	}

	public static boolean Is_Valid_RetypedPassword(EditText edt, Editable editable)
			throws NumberFormatException {
		// TODO Auto-generated method stub
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (edt.getText().toString().length() < 4) {
			edt.setError(Html
					.fromHtml("<font color='red'>La lunghezza deve essere >= 4</font>"));
			return false;
		} else if (!edt.getText().toString()
				.matches("((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,20})")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,20})</font>"));
			return false;
		}
		if (!edt.getText().toString().equals(editable.toString())) {
			edt.setError(Html.fromHtml("<font color='red'>Le password sono differenti</font>"));
			return false;
		}
		edt.setError(null);
		return true;
	}

	public static boolean Is_Valid_Email(EditText edt)
			throws NumberFormatException {
		if (edt.getText().toString().length() <= 0) {
			edt.setError(Html
					.fromHtml("<font color='red'>Informazione necessaria</font>"));
			return false;
		} else if (!edt
				.getText()
				.toString()
				.matches(
						"^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+")) {
			edt.setError(Html
					.fromHtml("<font color='red'>Formato accettato:^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+</font>"));
			return false;
		}
		edt.setError(null);
		return true;
	}

	public static boolean CheckIsNull(CheckBox cbNotNull) {
		// TODO Auto-generated method stub
		if (cbNotNull.isChecked()) {
			cbNotNull.setChecked(false);
			// cbNotNull.setError(Html
			// .fromHtml("NOT NULL? MUST BE NULL IN PRIMARY KEY"));
			return false;
		}
		cbNotNull.setError(null);
		return true;

	}

	public static boolean CheckIsPK(CheckBox cbPK) {
		// TODO Auto-generated method stub
		if (!cbPK.isChecked()) {
			cbPK.setChecked(true);
			// cbPK.setError(Html.fromHtml("OFF? MUST BE A PRIMARY KEY"));
			return false;
		}
		cbPK.setError(null);
		return true;

	}

}
