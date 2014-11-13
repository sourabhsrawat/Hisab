package com.rawat.hisab.DB;

public interface DBConst {

	//Database 
	public static final String DATABASE_NAME = "hisab.db";
	public static final int DATABASE_VERSION = 1;

	//Tables
	//Table One 
	public static final String Table1_Name="Card_Details";
	public static final String Table1_Column1="Card_Name";
	public static final String Table1_Column2="Card_Amt";
	public static final String Table1_Column3="Card_Mnt";
	public static final String Table1_Column4="Card_Yr";

	//Table Two
	public static final String Table2_Name="Card_Setting";
	public static final String Table2_Column1="Card_ID";
	public static final String Table2_Column2="Card_Inv_Lmt";
	public static final String Table2_Column3="Card_Total_Lmt";

	//Table Three
	public static final String Table3_Name="Card_Total";
	public static final String Table3_Column1="Card_Mnt";
	public static final String Table3_Column2="Card_Yr";
	public static final String Table3_Column3="Card_Total";

	//Table Four
	public static final String Table4_Name="Cards";
	public static final String Table4_Column1="Card_ID";
	public static final String Table4_Column2="Card_Name";

	//Table Five
	public static final String Table5_Name="Mnt_Total";
	public static final String Table5_Column1="Card_Mnt";
	public static final String Table5_Column2="Card_Yr";
	public static final String Table5_Column3="Mnt_Total";
	
	//Card ID and Name
	public static final String card [][]={
		{"01","ICICI Credit"},
		{"02","HDFC Credit"},
		{"03","CITI Debit"},
		{"04","ICICI Debit"},
		{"05","SBI Debit"},
		{"06","Amex Credit"},
		{"07","HDFC Debit"},
		{"08","Standard Chartered Credit"},
		{"09","Kotak Debit Card"},
		{"10","Citi Credit"},
		{"11","SBI Credit Card"},
	};


}
