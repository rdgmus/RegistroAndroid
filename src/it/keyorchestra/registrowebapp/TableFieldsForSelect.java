package it.keyorchestra.registrowebapp;

import android.os.Parcel;
import android.os.Parcelable;

public class TableFieldsForSelect implements Parcelable {
	private String table, field;

	public TableFieldsForSelect(String table, String field) {
		super();
		this.table = table;
		this.field = field;
	}

	public TableFieldsForSelect(Parcel in) {
		// TODO Auto-generated constructor stub
		String[] data = new String[2];

		in.readStringArray(data);
		this.table = data[0];
		this.field = data[1];
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof TableFieldsForSelect) {
			TableFieldsForSelect t = (TableFieldsForSelect) o;
			return t.getTable().equals(this.getTable())
					&& t.getField().equals(this.getField());
		}
		return super.equals(o);
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeStringArray(new String[] { this.table, this.field });
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public TableFieldsForSelect createFromParcel(Parcel in) {
			return new TableFieldsForSelect(in);
		}

		public TableFieldsForSelect[] newArray(int size) {
			return new TableFieldsForSelect[size];
		}
	};
}
