package com.rawat.hisab.DB;


import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InsertCardData {

	private SQLiteDatabase database;

	private boolean isEmpty=false;
	private Context ct;

	public InsertCardData(SQLiteDatabase database,Context ct)
	{
		this.database=database;
		this.ct=ct;
	}
	public void fisrtInsert() 
	{
		try{
			//Insert card ID and Name
			insertCardIDName();
			//Insert all card details into Card detail table
			insertCardDeatils();
			insertCardMontlyData();
			insertMntTotal();
			//Insert Default settings 
			insertSetting();
			insertSyncTime();
		}
		catch(Exception e)
		{
			Log.w("Error in insert ", e.getMessage());
		}
	}
	private void insertCardIDName() throws Exception 
	{
		int count = DBConst.card.length;
		for(int i =0 ;i <count;i++)
		{
			database.execSQL("Insert into "+DBConst.Table4_Name+" ( "+DBConst.Table4_Column1+" , "+DBConst.Table4_Column2 +") values ( '"+DBConst.card[i][0]+"','"+DBConst.card[i][1] +"' )" );
		}
	}
	private void insertCardDeatils() throws Exception 
	{
		CardDataProvider cdp = new CardDataProvider(ct);
		List<CardDetails> cdls = cdp.getSMS();
		if(cdls.isEmpty())
		{
			isEmpty=true;
		}
		else
		{
			Iterator<CardDetails> itr = cdls.iterator();

			while(itr.hasNext())
			{
				CardDetails cd=itr.next();

				ContentValues cv = new ContentValues(6);
				cv.put(DBConst.Table1_Column1, cd.getMsgtimeStamp());
				cv.put(DBConst.Table1_Column2, cd.getID());
				cv.put(DBConst.Table1_Column3, cd.getAmt());
				cv.put(DBConst.Table1_Column4, "AT");
				cv.put(DBConst.Table1_Column5, cd.getMnt());
				cv.put(DBConst.Table1_Column6, cd.getYr());
				database.insert(DBConst.Table1_Name, null, cv);

			}
		}
	}

	private void insertCardMontlyData() throws Exception 
	{
		if(!isEmpty)
		{
			Cursor cr = database.rawQuery("SELECT DISTINCT " + DBConst.Table1_Column5+" , "+ DBConst.Table1_Column6 + 
					" FROM "+ DBConst.Table1_Name, null);
			while(cr.moveToNext())
			{
				String [] args={cr.getString(0),cr.getInt(1)+ ""};
				String [] cl={DBConst.Table1_Column2};

				Cursor csID = database.query(true, DBConst.Table1_Name, cl, DBConst.Table1_Column5 +" = ?"+ " and  "+
						DBConst.Table1_Column6 +" = ?", args, null, null, null, null, null);
				while(csID.moveToNext())
				{
					String[] id = new String[]{csID.getInt(0)+"",cr.getString(0),cr.getInt(1)+""};
					Cursor crSum = database.rawQuery("SELECT SUM ( "+DBConst.Table1_Column3 +" ) " + " FROM " +
							DBConst.Table1_Name+" WHERE "+DBConst.Table1_Column2 +" = ? "+" AND "+DBConst.Table1_Column5
							+ " = ? "+" AND "+DBConst.Table1_Column6 + " =? ", id);
					crSum.moveToNext();
					ContentValues cv = new ContentValues(4);
					cv.put(DBConst.Table3_Column1, csID.getInt(0));
					cv.put(DBConst.Table3_Column2, cr.getString(0));
					cv.put(DBConst.Table3_Column3, cr.getInt(1));
					cv.put(DBConst.Table3_Column4, crSum.getInt(0));
					database.insert(DBConst.Table3_Name, null, cv);

				}
			}
		}
	}
	private void insertMntTotal() throws Exception 
	{
		if(!isEmpty)
		{
			Cursor cr = database.rawQuery("SELECT DISTINCT " + DBConst.Table3_Column2+" , "+ DBConst.Table3_Column3 + 
					" FROM "+ DBConst.Table3_Name, null);

			while(cr.moveToNext())
			{
				String [] args={cr.getString(0),cr.getInt(1)+ ""};
				Cursor crSum = database.rawQuery("SELECT SUM ( "+DBConst.Table3_Column4 +" ) " + " FROM " +
						DBConst.Table3_Name+" WHERE "+DBConst.Table3_Column2 +" = ? "+" AND "+DBConst.Table3_Column3
						+ " = ? ", args);
				while(crSum.moveToNext())
				{
					ContentValues cv = new ContentValues(3);
					cv.put(DBConst.Table5_Column1,cr.getString(0));
					cv.put(DBConst.Table5_Column2, cr.getInt(1));
					cv.put(DBConst.Table5_Column3, crSum.getInt(0));
					database.insert(DBConst.Table5_Name,null, cv);
				}
			}
		}
	}
	private void insertSetting() throws Exception 
	{
		ContentValues cv = new ContentValues(2);
		cv.put(DBConst.Table2_Column1, DBConst.Card_Limit);
		cv.put(DBConst.Table2_Column2, DBConst.Total_Limit);
		database.insert(DBConst.Table2_Name, null, cv);
	}
	private void insertSyncTime() throws Exception 
	{
		long date = System.currentTimeMillis();
		ContentValues cv = new ContentValues();
		cv.put(DBConst.Table6_Column1, date);
		database.insert(DBConst.Table6_Name, null, cv);
	}
}
