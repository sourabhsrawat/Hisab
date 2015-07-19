package com.rawat.hisab;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

import java.util.List;

import com.rawat.hisab.DB.HisabDataSource;
import com.rawat.hisab.utility.ConfigDate;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	static Context mContext ;
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	private static Notification nf;
	private static Activity ac;
	private static ConfigDate CfgDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		mContext=this;
		ac=this;

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_notifications);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);

		// Add 'data and sync' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_About);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_data_sync);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("Set_Total_Max"));
		bindPreferenceSummaryToValue(findPreference("Set_Card_Max"));
		//bindPreferenceSummaryToValue(findPreference("example_list"));
		//bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		bindPreferenceSummaryToValue(findPreference("share"));
		bindPreferenceSummaryToValue(findPreference("rate"));
		Preference prefRate = findPreference("rate");
		ButtonClick(prefRate);
		Preference prefshare = findPreference("share");
		ButtonShare(prefshare);
		Preference preffeedback = findPreference("feedback");
		ButtonFeedBack(preffeedback);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
				.setSummary(index >= 0 ? listPreference.getEntries()[index]
						: null);

			} 

			if(preference instanceof CheckBoxPreference)
			{
				Log.w("Check", stringValue);
			}
			if(preference instanceof EditTextPreference)
			{
				nf = new Notification(ac);
				CfgDate = (ConfigDate) ac.getApplication();
				HisabDataSource hdc = new HisabDataSource(mContext);
				preference.setSummary(stringValue);
				if(preference.getKey().equals("Set_Total_Max"))
				{
					Log.w("Total",stringValue);

					hdc.open();
					hdc.setSettingTotalLmt(stringValue);
					Log.w("Changed Total lmt",hdc.getSettingTotalLmt()+"");
					if(hdc.checkTotalLmt(stringValue, CfgDate.getMonthInWrd(), CfgDate.getEndYear()))
					{
						nf.displayNotification("Total",1);
						Log.w("Limit", "Total");
					}
					hdc.close();
				}
				if(preference.getKey().equals("Set_Card_Max"))
				{
					Log.w("Card",stringValue);
					hdc.open();
					hdc.setSettingInvCardLmt(stringValue);
					Log.w("Changed Inv Lmt",hdc.getSettingInvCardLmt()+"");
					if(hdc.checkIndLmt(stringValue, CfgDate.getMonthInWrd(), CfgDate.getEndYear()))
					{

						nf.displayNotification("individual card",2);
						Log.w("Limit", "IND");
					}
					hdc.close();
				}
			}
			else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}

	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
		.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
								""));


	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("Set_Total_Max"));
			bindPreferenceSummaryToValue(findPreference("Set_Card_Max"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends
	PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			//bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		}
	}

	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext);
		final String APP_TITLE ="Hisab";
		final String APP_PNAME = "com.rawat.hisab";
		dialog.setTitle("Rate " + APP_TITLE);

		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(mContext);
		tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
		tv.setWidth(240);
		tv.setPadding(4, 0, 4, 10);
		ll.addView(tv);

		Button b1 = new Button(mContext);
		b1.setText("Rate " + APP_TITLE);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				dialog.dismiss();
			}



		});        
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText("Remind me later");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText("No, thanks");
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);        
		dialog.show();        
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("CommitPrefEdits") public static class DataSyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("share"));
			bindPreferenceSummaryToValue(findPreference("rate"));

		}
	}
	public void ButtonClick(Preference prf)
	{
		SharedPreferences prefs = mContext.getSharedPreferences("hisab", 0);
		final SharedPreferences.Editor editor = prefs.edit();
		if (prf != null) {
			prf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {
					showRateDialog(mContext, editor);

					return true;
				}
			});     
		}
		editor.commit();
	}
	public void ButtonShare(Preference prf)
	{
		final String APP_PNAME = "com.rawat.hisab";

		if (prf != null) {
			prf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("text/plain");
					String txt="Hisab on my mobile - I just discovered the Hisab App! It's a great App. " +
							"Download it here https://play.google.com/store/apps/details?id=com.rawat.hisab";
					share.putExtra(Intent.EXTRA_TEXT, txt);
					startActivity(Intent.createChooser(share, "Share Text"));

					return true;
				}
			});     
		}

	}
	public void ButtonFeedBack(Preference prf)
	{
		if (prf != null) {
			prf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {

					/*Intent email = new Intent(android.content.Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[]{"youremail@yahoo.com"});		  
					email.putExtra(Intent.EXTRA_SUBJECT, "subject");
					email.putExtra(Intent.EXTRA_TEXT, "message");
					email.setType("email");
					startActivity(Intent.createChooser(email, "Choose an Email client :"));*/
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				            "mailto",getString(R.string.mail_feedback_email), null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT,getString( R.string.mail_feedback_subject));
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
					
					return true;

				}
			});     
		}
	}

}
