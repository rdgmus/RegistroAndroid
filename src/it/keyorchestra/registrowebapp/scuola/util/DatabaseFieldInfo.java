package it.keyorchestra.registrowebapp.scuola.util;

import android.os.Parcel;
import android.os.Parcelable;

public class DatabaseFieldInfo implements Parcelable {
	private String dbname, tableName, fieldName;

	public DatabaseFieldInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DatabaseFieldInfo(String dbname, String tableName, String fieldName) {
		super();
		this.dbname = dbname;
		this.tableName = tableName;
		this.fieldName = fieldName;
	}

	public DatabaseFieldInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		String[] data = new String[3];

		in.readStringArray(data);
		this.dbname = data[0];
		this.tableName = data[1];
		this.fieldName = data[2];
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeStringArray(new String[] { this.dbname, this.tableName,
				this.fieldName });
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public DatabaseFieldInfo createFromParcel(Parcel in) {
			return new DatabaseFieldInfo(in);
		}

		public DatabaseFieldInfo[] newArray(int size) {
			return new DatabaseFieldInfo[size];
		}
	};
}
