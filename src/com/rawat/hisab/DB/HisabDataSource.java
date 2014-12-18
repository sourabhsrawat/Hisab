package com.rawat.hisab.DB;


import java.util.ArrayList;
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
	Context ct;
	private List<Double> amt;
	private List<String> cardName;
	private CardDetails cd;
	private List<CardDetails> listCardDetails;
	private boolean cardMsg=true;

	private String[] allTable6Columns= {DBConst.Table6_Column1};


	public HisabDataSource(Context context) {
		ct=context;
		dbHelper = new MySQLiteHelper(context);


	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}


	public void updateCardData(String msg,long ms)
	{
		try 
		{
			UpdateCardData uCD= new UpdateCardData(database, ct);
			uCD.updateCardDetails(msg,ms);
			setCardMsg(uCD.isCarMsg());
		}
		catch(Exception e)
		{
			Log.w("Error in Updating record ", e.getMessage());
		}
	}


	public long getLastSync() 
	{
		long lastSync = 0;
		try{
			Cursor cursor =database.query(DBConst.Table6_Name,allTable6Columns, null, null, null, null, null);
			cursor.moveToFirst();
			lastSync=cursor.getLong(0);
			cursor.close();
		}
		catch(Exception e)
		{
			Log.w("Error in Last sync ", e.getMessage());
		}
		return lastSync;
	}
	public void updateLastSync(long ls)
	{
		long lastSync = getLastSync();
		try{

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
		catch(Exception e)
		{
			Log.w("Error in Update Last sync ", e.getMessage());
		}

	}
	private void getCardTotal(String mnt, int yr)
	{
		try{
			amt = new ArrayList<Double>();
			cardName=new ArrayList<String>();
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
				String cardNameDB = new String(crCardName.getString(0));
				cardNameDB=cardNameDB.replace('_', ' ');
				amt.add(cursor.getDouble(0));
				cardName.add(cardNameDB);
				crCardName.close();
				cursor.close();
			}
			crCardID.close();
		}
		catch(Exception e)
		{
			Log.w("Error in get card total ", e.getMessage());
		}
	}

	/*
	 * Get card total and will call the method getCardTotal which will set card amt and card name
	 */
	public int getTotal(String mnt, int yr)
	{
		int total=0;
		try{
			getCardTotal(mnt, yr);
			String [] str = new String[]{DBConst.Table5_Column3};
			Cursor cursor = database.query(DBConst.Table5_Name, str, DBConst.Table5_Column1+" = ? AND "+DBConst.Table5_Column2 + " = ? ", 
					new String[]{mnt,yr+""}, null, null, null);
			cursor.moveToNext();
			total = cursor.getInt(0);
			cursor.close();
		}
		catch(Exception e)
		{
			Log.w("Error in get total ", e.getMessage());
		}
		return total;
	}

	/*
	 * 
	 */
	public List<CardDetails> displayCardDetails(String cardName,String mnt,int yr)
	{

		listCardDetails = new ArrayList<CardDetails>();
		try
		{
			int cardID = getCardID(cardName);

			Cursor cr = database.query(DBConst.Table1_Name,null,DBConst.Table1_Column2+" =? AND " + 
					DBConst.Table1_Column5+" =?  AND " + DBConst.Table1_Column6 + 
					" =? ", new String[]{cardID+"",mnt,String.valueOf(yr)}, null, null, null);

			while(cr.moveToNext())
			{
				cd = new CardDetails();
				cd.setMsgtimeStamp(cr.getLong(0));
				cd.setID(cardID);
				cd.setAmt(cr.getInt(2));
				cd.setAt(cr.getString(3));
				cd.setMnt(cr.getString(4));
				cd.setYr(cr.getInt(5));
				listCardDetails.add(cd);
			}
			cr.close();
		}
		catch(Exception e)
		{
			Log.w("Error in display card details ", e.getMessage());
		}
		return listCardDetails;
	}

	public int getCardID(String cardName)
	{
		int cID=0;
		try{
			cardName = new String(cardName);
			cardName=cardName.replace(' ', '_');
			Cursor crCardID = database.query(DBConst.Table4_Name, new String[]{DBConst.Table4_Column1},
					DBConst.Table4_Column2 +" =? ", new String[]{cardName}, null, null, null);
			crCardID.moveToNext();
			cID=crCardID.getInt(0);
			crCardID.close();
		}
		catch(Exception e)
		{
			Log.w("Error in get total ", e.getMessage());
		}
		return cID;

	}
	public void setSettingInvCardLmt(String cardLmt)
	{
		try{
			ContentValues cv = new ContentValues();
			cv.put(DBConst.Table2_Column1, cardLmt);
			int tmpLmt= getSettingInvCardLmt();
			database.update(DBConst.Table2_Name, cv,DBConst.Table2_Column1 +" =? ", new String[]{tmpLmt+""});
		}
		catch(Exception e)
		{
			Log.w("Error in set InvCardLmt ", e.getMessage());
		}
	}
	public int getSettingInvCardLmt()
	{
		int lmt=0;
		try{
			Cursor crCardLmt = database.query(DBConst.Table2_Name,new String[]{ DBConst.Table2_Column1}, null, 
					null, null, null, null);
			crCardLmt.moveToNext();
			lmt=crCardLmt.getInt(0);
			crCardLmt.close();
		}
		catch(Exception e)
		{
			Log.w("Error in get InvCardLmt ", e.getMessage());
		}

		return lmt;
	}
	public void setSettingTotalLmt(String totalLmt)
	{
		try{
			ContentValues cv = new ContentValues();
			cv.put(DBConst.Table2_Column2, totalLmt);
			int tmpLmt= getSettingTotalLmt();
			database.update(DBConst.Table2_Name, cv,DBConst.Table2_Column2 +" =? ", new String[]{tmpLmt+""});
		}
		catch(Exception e)
		{
			Log.w("Error in set TotalLmt ", e.getMessage());
		}
	}
	public int getSettingTotalLmt()
	{
		int lmt=0;
		try{
			Cursor crCardLmt = database.query(DBConst.Table2_Name,new String[]{ DBConst.Table2_Column2}, null, 
					null, null, null, null);
			crCardLmt.moveToNext();
			lmt=crCardLmt.getInt(0);
			crCardLmt.close();
		}
		catch(Exception e)
		{
			Log.w("Error in get TotalLmt ", e.getMessage());
		}

		return lmt;
	}
	public Boolean checkIndLmt(String lmt,String mnt,int yr)
	{
		Boolean check =false;
		try
		{
			Cursor crChkLmt = database.query(DBConst.Table3_Name, new String[]{DBConst.Table3_Column1}, 
					DBConst.Table3_Column2 +" =? AND "+DBConst.Table3_Column3 +" =? AND "+
							DBConst.Table3_Column4+" >? " , new String[]{mnt,yr+"",lmt+""},
							null, null, null);
			if(crChkLmt.getCount() > 0)
				check=true;

			crChkLmt.close();
		}
		catch(Exception e)
		{
			Log.w("Error in checkIndLmt ", e.getMessage());
		}
		return check;
	}
	public Boolean checkTotalLmt(String lmt,String mnt,int yr)
	{
		Boolean check =false;
		try
		{
			Cursor crChkLmt = database.query(DBConst.Table5_Name, new String[]{DBConst.Table5_Column1}, 
					DBConst.Table5_Column1 +" =? AND "+DBConst.Table5_Column2 +" =? AND "+
							DBConst.Table5_Column3+" >? " , new String[]{mnt,yr+"",lmt+""},
							null, null, null);
			if(crChkLmt.getCount() > 0)
				check=true;

			crChkLmt.close();
		}
		catch(Exception e)
		{
			Log.w("Error in checkTotalLmt ", e.getMessage());
		}
		return check;
	}
	public List<TagInfo> getTagInfo(String at)
	{
		List<TagInfo> lsTg = new ArrayList<TagInfo>();
		
		try
		{
			Cursor crTagInfo = database.query(DBConst.Table7_Name, null, DBConst.Table7_Column1+" =? ", new String[]{at}, 
												null, null, null);
			while(crTagInfo.moveToNext())
			{
				TagInfo tg = new TagInfo();
				tg.setAt(crTagInfo.getString(0));
				tg.setTag(crTagInfo.getString(1));
				lsTg.add(tg);
			}
		}
		catch(Exception e)
		{
			Log.w("Error in TagInfo ", e.getMessage());
		}
		return lsTg;
	}
	public List<Double> getCardAmt()
	{
		return amt;
	}
	public List<String> getCardName()
	{
		return cardName;
	}

	public boolean isCardMsg() {
		return cardMsg;
	}

	public void setCardMsg(boolean cardMsg) {
		this.cardMsg = cardMsg;
	}
}
