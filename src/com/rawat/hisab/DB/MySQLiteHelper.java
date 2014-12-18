package com.rawat.hisab.DB;





import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
	

	private Context ct;
	
	private static final String CREATE_TABLE1 = "CREATE TABLE  "
			+ DBConst.Table1_Name + " (" + DBConst.Table1_Column1
			+ " REAL, " + DBConst.Table1_Column2 + " INTEGER," + DBConst.Table1_Column3
			+ " REAL, "+ DBConst.Table1_Column4+ " TEXT, "+ DBConst.Table1_Column5+ " TEXT, " 
			+ DBConst.Table1_Column6+ " INTEGER " + ")";

	private static final String CREATE_TABLE2="CREATE TABLE "+DBConst.Table2_Name + "( " + DBConst.Table2_Column1 + 
			" INTEGER ," + DBConst.Table2_Column2 + " INTEGER "  +" )";

	private static final String CREATE_TABLE3="CREATE TABLE "+DBConst.Table3_Name + " ( " + DBConst.Table3_Column1 + 
			" INTEGER, " + DBConst.Table3_Column2 + " TEXT, " 
			+ DBConst.Table3_Column3 + " INTEGER, "+ DBConst.Table3_Column4 + " INTEGER " +" )";

	private static final String CREATE_TABLE4="CREATE TABLE " + DBConst.Table4_Name + " ( " + DBConst.Table4_Column1 + 
			" INTEGER PRIMARY KEY, " + DBConst.Table4_Column2 + " TEXT " + " )";

	private static final String CREATE_TABLE5="CREATE TABLE "+DBConst.Table5_Name + " ( " + DBConst.Table5_Column1 + 
			" TEXT ," + DBConst.Table5_Column2 + " INTEGER," 
			+ DBConst.Table5_Column3 + " INTEGER " +" )";
	
	private static final String CREATE_TABLE6="CREATE TABLE " +DBConst.Table6_Name + " ( " + DBConst.Table6_Column1 +
			" REAL " + " ) ";
	
	private static final String CREATE_TABLE7="CREATE TABLE " + DBConst.Table7_Name + " ( " + DBConst.Table7_Column1 + 
			" TEXT, " + DBConst.Table7_Column2 + " TEXT " + " )";
	


	public MySQLiteHelper(Context context) {
		super(context, DBConst.DATABASE_NAME, null, DBConst.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		ct=context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	
		//Create all the databases 
		try{
		database.execSQL(CREATE_TABLE1);
		database.execSQL(CREATE_TABLE2);
		database.execSQL(CREATE_TABLE3);
		database.execSQL(CREATE_TABLE4);
		database.execSQL(CREATE_TABLE5);
		database.execSQL(CREATE_TABLE6);
		database.execSQL(CREATE_TABLE7);
		
		Log.w("Database real", "created");
		
		
		/*
		 * Define all the methods to insert data into database
		 */
		InsertCardData hds = new InsertCardData(database,ct);
		
		//Opening database 
	
		hds.fisrtInsert();
		Log.w("Database inserted", "DB");
		}
		catch(Exception e)
		{
			Log.w("Error in Create Data ", e.getMessage());
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
	
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS "+DBConst.Table1_Name);
		onCreate(db);

	}

}
