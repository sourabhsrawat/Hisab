package com.rawat.hisab.DB;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HisabDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	String mnt;
	
	private String[] allTable1Columns = { DBConst.Table1_Column1,DBConst.Table1_Column2,
			DBConst.Table1_Column3,DBConst.Table1_Column4};
	
	
	public HisabDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  
	  public void insertCardDeatils(CardDetails cd)
	  {
		  
		  
	  }
	  public List<CardDetails> getAllCardDeatils()
	  {
		  List<CardDetails> cd = new ArrayList<CardDetails>();
		  Cursor cursor = database.query(DBConst.Table1_Name,
				  allTable1Columns, DBConst.Table1_Column4 +"=?", new String[]{mnt}, null, null, null);
		  
		  cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	CardDetails CardD = cursorToCardDeatil(cursor);
		    	cd.add(CardD);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		  
		  return cd;
	  }
	  private CardDetails cursorToCardDeatil(Cursor cr)
	  {
		  CardDetails kd = new CardDetails();
		  kd.setID(cr.getInt(0));
		  kd.setName(cr.getString(1));
		  kd.setAmt(cr.getInt(2));
		  kd.setMnt(cr.getString(3));
		  kd.setYr(cr.getInt(4));
		  
		  return kd;
	  }
}
