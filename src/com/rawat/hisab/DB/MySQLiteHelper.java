package com.rawat.hisab.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{

	private static final String CREATE_TABLE1 = "CREATE TABLE "
			+ DBConst.Table1_Name + "(" + DBConst.Table1_Column1
			+ " TEXT," + DBConst.Table1_Column2 + " INTEGER," + DBConst.Table1_Column3
			+ " TEXT"+ DBConst.Table1_Column4+ "TEXT" + ")";

	private static final String CREATE_TABLE2="CREATE TABLE "+DBConst.Table2_Name + "(" + DBConst.Table2_Column1 + 
			" INTEGER PRIMARY KEY," + DBConst.Table2_Column2 + "INTEGER" 
			+ DBConst.Table2_Column3 + "INTEGER" +")";

	private static final String CREATE_TABLE3="CREATE TABLE "+DBConst.Table3_Name + "(" + DBConst.Table3_Column1 + 
			" TEXT PRIMARY KEY," + DBConst.Table3_Column2 + "INTEGER PRIMARY KEY" 
			+ DBConst.Table3_Column3 + "INTEGER" +")";

	private static final String CREATE_TABLE4="CREATE TABLE" + DBConst.Table1_Name + "(" + DBConst.Table4_Column1 + 
			" INTEGER PRIMARY KEY," + DBConst.Table4_Column2 + "TEXT" + ")";

	private static final String CREATE_TABLE5="CREATE TABLE "+DBConst.Table5_Name + "(" + DBConst.Table5_Column1 + 
			" TEXT PRIMARY KEY," + DBConst.Table5_Column2 + "INTEGER PRIMARY KEY" 
			+ DBConst.Table5_Column3 + "INTEGER" +")";


	public MySQLiteHelper(Context context) {
		super(context, DBConst.DATABASE_NAME, null, DBConst.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		database.execSQL(CREATE_TABLE1);
		database.execSQL(CREATE_TABLE2);
		database.execSQL(CREATE_TABLE3);
		database.execSQL(CREATE_TABLE4);
		database.execSQL(CREATE_TABLE5);
		insertCardIDName(database);

		Log.w("Database", "Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
		// TODO Auto-generated method stub
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS ");
		onCreate(db);

	}
	public void insertCardIDName(SQLiteDatabase database)
	{
		int count = DBConst.card.length;
		for(int i =0 ;i <count;i++)
		{
			database.execSQL("Insert into"+DBConst.Table4_Name+ "values ("+DBConst.card[i][0]+DBConst.card[i][1] +")" );
		}
	}

}
