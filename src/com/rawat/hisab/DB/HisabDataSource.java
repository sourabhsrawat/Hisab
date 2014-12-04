package com.rawat.hisab.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HisabDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	String mnt="NOVEMBER";
	String yr="14";
	Context ct;
	private List<Double> amt;
	private List<String> cardName;
	
	private String[] allTable1Columns = { DBConst.Table1_Column1,DBConst.Table1_Column2,
			DBConst.Table1_Column3,DBConst.Table1_Column4,DBConst.Table1_Column5,DBConst.Table1_Column6};
	private String[] allTable6Columns= {DBConst.Table6_Column1};


	public HisabDataSource(Context context) {
		ct=context;
		dbHelper = new MySQLiteHelper(context);
		amt = new ArrayList<Double>();
		cardName=new ArrayList<String>();

	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void selectData()
	{
		Cursor cr = database.query(DBConst.Table1_Name,null,DBConst.Table1_Column5+" =?  AND "
				+ DBConst.Table1_Column6 + " =? ", new String[]{"DECEMBER ",String.valueOf(2014)}, null, null, null);
		//Cursor cr = database.query(DBConst.Table1_Name, null, null, null, null, null, null);
		Log.w("In Select", cr.equals(null)+"");
		while(cr.moveToNext())
		{
			Log.w("Cursor Table 1"," Card ID "+cr.getInt(1)+ "Card amt"+cr.getInt(2));
		}
		Cursor cr1 = database.query(DBConst.Table3_Name,null,DBConst.Table3_Column2+" =?  AND "
				+ DBConst.Table3_Column3 + " =? ", new String[]{"DECEMBER ",String.valueOf(2014)}, null, null, null);
		while(cr1.moveToNext())
		{
			Log.w("Card mnt", "ID "+cr1.getInt(0) +"Total "+cr1.getInt(3));
		}
		Cursor cr2 = database.query(DBConst.Table5_Name,null,DBConst.Table5_Column1+" =?  AND "
				+ DBConst.Table5_Column2 + " =? ", new String[]{"DECEMBER ",String.valueOf(2014)}, null, null, null);
		while(cr2.moveToNext())
		{
			Log.w("Mnt", "Total "+cr2.getInt(2));
		}
		Log.w("In Select", "Select");
		long lastSync=getLastSync();
		Log.w("In Select", "Last Sync "+lastSync);
		/*Cursor cr1 = database.query(DBConst.Table1_Name, null, null, 
				null, null, null, null);
		while(cr1.moveToNext())
		{
			Log.w("Last sync check", cr1.getLong(0)+"");
		}*/

		Log.w("Last sy", lastSync+"");
		Cursor crls = database.query(DBConst.Table1_Name, null, DBConst.Table1_Column1 + " <= ? ", 
				new String[]{String.valueOf(lastSync)}, null, null, null);
		Log.w("Check count", crls.getCount()+"");

	}

	public void updateCardData(String msg,long ms)
	{
		UpdateCardData uCD= new UpdateCardData(database, ct);
		uCD.updateCardDetails(msg,ms);

	}

	public List<CardDetails> getAllCardDeatils()
	{
		Log.w("Select", "INS");
		List<CardDetails> cd = new ArrayList<CardDetails>();
		Cursor cursor = database.query(DBConst.Table1_Name,
				allTable1Columns, DBConst.Table1_Column5 +"=?"+" AND " + DBConst.Table1_Column6 +"=?", new String[]{mnt,yr}, null, null, null);
		Log.w("Select", "EX");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Log.w("cr", "cr"+cursor.getDouble(0));
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
		kd.setMsgtimeStamp(cr.getInt(0));
		kd.setID(cr.getInt(1));
		kd.setAmt(cr.getInt(2));
		kd.setAt(cr.getString(3));
		kd.setMnt(cr.getString(4));
		kd.setYr(cr.getInt(5));

		return kd;
	}
	public long getLastSync() 
	{
		long lastSync = 0;
		Cursor cursor =database.query(DBConst.Table6_Name,allTable6Columns, null, null, null, null, null);
		cursor.moveToFirst();
		lastSync=cursor.getLong(0);
		return lastSync;
	}
	public void updateLastSync(long ls)
	{
		long lastSync = getLastSync();
		if(lastSync == 0)
		{
			database.execSQL("Insert into " + DBConst.Table6_Name + "values ( '" +ls +"' )" );
		}
		else
		{
			database.execSQL("Update " + DBConst.Table6_Name + " set " + DBConst.Table6_Column1 + "=" +ls 
					+ " where " + DBConst.Table6_Name +"="+ lastSync);
		}
	}
	public int getCardTotal(String mnt, int yr)
	{
		int total=0;
		String [] str = new String[]{DBConst.Table3_Column4};
		Cursor crCardID = database.query(true,DBConst.Table1_Name,new String[]{DBConst.Table1_Column2}, 
										 DBConst.Table1_Column5 +"=?"+" AND "+ DBConst.Table1_Column6 
										 +"=?", new String[]{mnt,yr+""}, null, null, null,null,null);
		while(crCardID.moveToNext())
		{
			int cardID = crCardID.getInt(0);
			Cursor cursor=database.query(DBConst.Table3_Name,str, DBConst.Table3_Column1 +" =? AND "+
					DBConst.Table3_Column2 +"=? AND " + DBConst.Table3_Column3 + " =? ",
					new String[] {cardID+"",mnt,yr+""},
					null, null, null);
			cursor.moveToNext();
			Cursor crCardName = database.query(DBConst.Table4_Name, new String[]{DBConst.Table4_Column2},
												DBConst.Table4_Column1+"=?  ", new String[]{cardID+""}
												, null, null, null);
			crCardName.moveToNext();
			String cardName = new String(crCardName.getString(0));
			cardName=cardName.replace('_', ' ');
			Log.w("Data", "Name " + cardName +" MNT " + mnt + "YR " +yr+" AMT" + cursor.getInt(0));
			total= cursor.getInt(0);
			crCardName.close();
			cursor.close();
		}
		crCardID.close();
		return total;
	}
	public int getTotal(String mnt, int yr)
	{
		int total=0;
		String [] str = new String[]{DBConst.Table5_Column3};
		Cursor cursor = database.query(DBConst.Table5_Name, str, DBConst.Table5_Column1+" = ? AND "+DBConst.Table5_Column2 + " = ? ", 
				new String[]{mnt,yr+""}, null, null, null);
		cursor.moveToNext();
		total = cursor.getInt(0);
		Log.w("Total",""+total);
		cursor.close();
		return total;
	}
}
