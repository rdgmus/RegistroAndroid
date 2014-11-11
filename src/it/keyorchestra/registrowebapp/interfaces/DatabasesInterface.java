package it.keyorchestra.registrowebapp.interfaces;

import java.util.ArrayList;

public interface DatabasesInterface {
	// VARIABLES
	final static String[] sqliteColumnsInfoNames = new String[] { "name",
			"type", "notnull", "dflt_value", "pk" };

	// FUNCTIONS
	public ArrayList<String> getTablesNames();

	public ArrayList<TableColumnsInfo> getColumnsInfo(
			ArrayList<String> arrTblNames);

	// public SQLiteDatabase createDatabase(String dbName, int dbVersion);

	// CLASSES
	public class DatabaseTablesInfo {
		String dbName;
		ArrayList<String> arrTblNames;
		ArrayList<TableColumnsInfo> tColInfoArray;

		public DatabaseTablesInfo(String dbName, ArrayList<String> arrTblNames,
				ArrayList<TableColumnsInfo> tColInfoArray) {
			super();
			this.dbName = dbName;
			this.arrTblNames = arrTblNames;
			this.tColInfoArray = tColInfoArray;
		}

		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public ArrayList<String> getArrTblNames() {
			return arrTblNames;
		}

		public void setArrTblNames(ArrayList<String> arrTblNames) {
			this.arrTblNames = arrTblNames;
		}

		public ArrayList<TableColumnsInfo> gettColInfoArray() {
			return tColInfoArray;
		}

		public void settColInfoArray(ArrayList<TableColumnsInfo> tColInfoArray) {
			this.tColInfoArray = tColInfoArray;
		}

	}

	public class TableColumnsInfo {
		String tblName;
		ArrayList<ColumnInfo> colInfoArray;

		public TableColumnsInfo(String tblName,
				ArrayList<ColumnInfo> colInfoArray) {
			super();
			this.tblName = tblName;
			this.colInfoArray = colInfoArray;
		}

		public String getTblName() {
			return tblName;
		}

		public void setTblName(String tblName) {
			this.tblName = tblName;
		}

		public ArrayList<ColumnInfo> getColInfoArray() {
			return colInfoArray;
		}

		public void setColInfoArray(ArrayList<ColumnInfo> colInfoArray) {
			this.colInfoArray = colInfoArray;
		}

	}

	public class ColumnInfo {
		String name;
		String type;
		int notNull;
		String dfltValue;
		int primaryKey;

		public ColumnInfo() {
			super();
			name = type = dfltValue = "";
			notNull = 0;
		}

		public ColumnInfo(String name, String type, int notNull,
				String dfltValue, int primaryKey) {
			super();
			this.name = name;
			this.type = type;
			this.notNull = notNull;
			this.dfltValue = dfltValue;
			this.primaryKey = primaryKey;
		}

		public int getPrimaryKey() {
			return primaryKey;
		}

		public void setPrimaryKey(int primaryKey) {
			this.primaryKey = primaryKey;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getNotNull() {
			return notNull;
		}

		public void setNotNull(int notNull) {
			this.notNull = notNull;
		}

		public String getDfltValue() {
			return dfltValue;
		}

		public void setDfltValue(String dfltValue) {
			this.dfltValue = dfltValue;
		}

	}
}
