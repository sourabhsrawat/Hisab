package com.rawat.hisab;

public interface CardIdentifier {

	//Values for all check for all credit cards in if else 
	public static final String iciciCredit_Check = "Tranx of INR";
	public static final String hdfcCredit_Check = "HDFCBank CREDIT Card";
	public static final String hdfcCreditSMT_Check = "HDFC Bank credit card to pay your SMARTPAY";
	public static final String cityAtm_Check = "Citibank ATM";
	public static final String cityAtm_Check1="withdrawn";
	public static final String cityDebit_Check1="Your debit card";
	public static final String cityDebit_Check2="make a purchase";
	public static final String cityCredit_Check="was spent on your Credit Card";
	public static final String iciciDebit_Check1 = "Dear Customer, Your Ac";
	public static final String iciciDebit_Check2 = "debited";
	public static final String iciciDebit_Check3 = "WDL*";
	public static final String iciciDebitPurchase_Check = "Dear Customer, You have made a Debit Card purchase";
	public static final String sbiDebit_Check = "SBI Debit Card";
	public static final String sbiDebit_Check1 = "Thank you for using your SBI Debit Card";
	public static final String sbiDebit_Check2 = "Debited INR";
	public static final String sbiDebit_in1_Check2 = "purchase";
	public static final String sbiDebit_in2_Check2 ="withdrawing";
	public static final String sbiDebit_in3_Check2 ="It would be our";
	public static final String sbiDebitIB_Check = "State Bank Internet Banking";
	public static final String amex_Check1 = "American Express Card";
	public static final String amex_Check2 = "A charge";
	public static final String hdfcDebit_Check = "HDFC Bank DEBIT";
	public static final String stanChartCredit_Check1 = "StanChart Credit";
	public static final String stanChartCredit_Check2 = "Call";
	public static final String iciciDebit_in_Check = "WDL";
	public static final String iciciDebit_in1_Check = "ATM";
	public static final String iciciDebit_in2_Check = "NFS";
	public static final String kotakDebit_Check="Kotak Debit card";
	public static final String bob_Check="is Debited to";
	public static final String sbi_credit_Check="SBI Credit card";

	
	//Arrays for splitting SMS
	public static final String[] iciciCredit_Split = {"Tranx of INR","using"};
	public static final String[] hdfcCredit_Split = {"Rs.","was"};
	public static final String[] hdfcCreditSMT_Split = {"Rs."};
	public static final String[] hdfcDedit_Split = {"Rs.","towards"};
	public static final String[] cityAtm_Split = {"Rs.","was"};
	public static final String[] cityAtm2_Split = {"Rs.","withdrawn"};
	public static final String[] cityDebit_Split={"Rs.","at"};
	public static final String[] cityCredit_Split={"Rs","was"};
	public static final String[] iciciDebit_Purchase_Check1_Check2_Split = {"INR","on"};
	public static final String[] iciciDebit_in1_Split = {"INR","ATM"};
	public static final String[] iciciDebit_in2_Split = {"INR","NFS"};
	public static final String[] sbiDebit_Check1_Split = {"Rs","on"};
	public static final String[] sbiDebit_Check2_Split = {"INR","on"};
	public static final String[] sbiDebit_Check2_in1_Split = {"Rs","on"};
	public static final String[] sbiDebit_Check2_in2_Split = {"Rs","from"};
	public static final String[] sbiDebit_Check2_in3_Split = {"Rs"};
	public static final String[] sbiDebitIB_Check_Split = {"Rs.","on"};
	public static final String[] amex_Split = {"INR","has"};
	public static final String[] stanChartCredit_Split1 = {"INR",". For"};
	public static final String[] stanChartCredit_Split2 = {"INR",". Call"};
	public static final String[] kotakDebit_Split={"of Rs.","made"};
	public static final String[] bob_Split={"Rs.", "is"};
	public static final String[] sbi_Credit_Split={"Rs.","made"};


}
