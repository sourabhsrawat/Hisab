package com.rawat.hisab;

import com.rawat.hisab.DB.HisabDataSource;
import com.rawat.hisab.utility.ConfigDate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSms extends BroadcastReceiver {

	final SmsManager sms = SmsManager.getDefault();
	private ConfigDate CfgDate;

	private Notification nf;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub


		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

					String message = currentMessage.getDisplayMessageBody();
					HisabDataSource hds = new HisabDataSource(context);
					hds.open();
					nf = new Notification(context.getApplicationContext());
					hds.updateCardData(message,currentMessage.getTimestampMillis());
					String checkTotal = hds.getSettingTotalLmt()+"";

					CfgDate = (ConfigDate) context.getApplicationContext();
					

					Log.w("Mnt", CfgDate.getMonthInWrd()+hds.checkTotalLmt(checkTotal, CfgDate.getMonthInWrd(), CfgDate.getEndYear()));
					if(hds.isCardMsg())
					{
						Log.w("Card Msg", "Yes");
						Log.w("Total Limit", checkTotal);
							if(hds.checkTotalLmt(checkTotal, CfgDate.getMonthInWrd(), CfgDate.getEndYear()))
							{
								nf.displayNotification("Total",1);

							}
							String checkIndLmt = hds.getSettingInvCardLmt()+"";
							Log.w("Ind Limit", checkIndLmt);
							if(hds.checkIndLmt(checkIndLmt, CfgDate.getMonthInWrd(), CfgDate.getEndYear()))
							{

								nf.displayNotification("individual card",2);

							}
						
					}
					hds.close();
					//Log.w("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


					// Show Alert
					// int duration = Toast.LENGTH_LONG;
					//Toast toast = Toast.makeText(context, 
					//            "senderNum: "+ senderNum + ", message: " + message, duration);
					// toast.show();

				} // end for loop
			} // bundle is null

		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" +e);

		}
	}    

}
