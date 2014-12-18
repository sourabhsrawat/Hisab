package com.rawat.hisab.DB;

public interface DBConst {

	//Database 
	public static final String DATABASE_NAME = "hisab.db";
	public static final int DATABASE_VERSION = 1;

	//Tables
	//Table One 
	public static final String Table1_Name="Card_Details";
	public static final String Table1_Column1="Msg_Timestamp";
	public static final String Table1_Column2="Card_ID";
	public static final String Table1_Column3="Card_Amt";
	public static final String Table1_Column4="Card_At";
	public static final String Table1_Column5="Card_Mnt";
	public static final String Table1_Column6="Card_Yr";

	//Table Two
	public static final String Table2_Name="Card_Setting";
	public static final String Table2_Column1="Card_Inv_Lmt";
	public static final String Table2_Column2="Card_Total_Lmt";

	//Table Three
	public static final String Table3_Name="Card_MNT_Total";
	public static final String Table3_Column1="Card_ID";
	public static final String Table3_Column2="Card_Mnt";
	public static final String Table3_Column3="Card_Yr";
	public static final String Table3_Column4="Card_Total";

	//Table Four
	public static final String Table4_Name="Cards";
	public static final String Table4_Column1="Card_ID";
	public static final String Table4_Column2="Card_Name";

	//Table Five
	public static final String Table5_Name="Total";
	public static final String Table5_Column1="Card_Mnt";
	public static final String Table5_Column2="Card_Yr";
	public static final String Table5_Column3="Mnt_Total";
	
	//Table Six
	public static final String Table6_Name="Sync";
	public static final String Table6_Column1="Last_Sync";
	
	//Table Seven
	public static final String Table7_Name="TagInfo";
	public static final String Table7_Column1="AT";
	public static final String Table7_Column2="Tag";
	
	
	//Card ID and Name
	public static final String card [][]={
		{"01","ICICI_Credit"},
		{"02","HDFC_Credit"},
		{"03","CITI_Debit"},
		{"04","ICICI_Debit"},
		{"05","SBI_Debit"},
		{"06","Amex_Credit"},
		{"07","HDFC_Debit"},
		{"8","Standard_Chartered_Credit"},
		{"9","Kotak_Debit"},
		{"10","Citi_Credit"},
		{"11","SBI_Credit"},
		{"12","BOB"},
	};
	
	//Settings
	public static final int Card_Limit=5000;
	public static final int Total_Limit=10000;
}
