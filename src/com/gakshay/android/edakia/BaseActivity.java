package com.gakshay.android.edakia;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class BaseActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_base);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_base, menu);
		return true;
	}


	public void rediretHomeActivity(View view) {
		Intent homeIntent = new Intent(getApplicationContext(), Edakia.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}

	public String getSerialNumber(){
		String serial = android.os.Build.SERIAL;

		if(serial != null && !"".equalsIgnoreCase(serial) && !"unknown".equalsIgnoreCase(serial))
			return serial;

		serial = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		if(serial != null && !"".equalsIgnoreCase(serial))
			return serial;


		if(serial == null || "unknown".equalsIgnoreCase(serial) || "".equalsIgnoreCase(serial)){
			try {
				Class<?> c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class);
				serial = (String) get.invoke(c, "ro.serialno");
			} catch (Exception ignored) {
			}
		}	

		return serial;
	}

	public void toggleEmail(View view){
		LinearLayout mobile_layout = (LinearLayout)this.findViewById(R.id.optionalMobileLayout);
		LinearLayout email_layout = (LinearLayout)this.findViewById(R.id.optionalEmailLayout);
		mobile_layout.setVisibility(LinearLayout.GONE);
		email_layout.setVisibility(LinearLayout.VISIBLE);
	}

	public void toggleMobile(View view){
		LinearLayout mobile_layout = (LinearLayout)this.findViewById(R.id.optionalMobileLayout);
		LinearLayout email_layout = (LinearLayout)this.findViewById(R.id.optionalEmailLayout);
		mobile_layout.setVisibility(LinearLayout.VISIBLE);
		email_layout.setVisibility(LinearLayout.GONE);
	}

	public boolean isNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	public Intent initiateHomePage(boolean isErrorType,String errorMsg){
		Intent homeIntent = new Intent(getApplicationContext(), Edakia.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		homeIntent.putExtra("showResultDialogBox", "true");
		if(isErrorType){
			homeIntent.putExtra("isError", "true");
			homeIntent.putExtra("errorMessageText", errorMsg);
		}else{
			homeIntent.putExtra("isError", "false");
		}
		
		return homeIntent;
	}

	public void preparedSharedPref(){
		// Read from the /assets directory
		SharedPreferences eDakiaSharedPref = getSharedPreferences("FIRST_TIME_BOOT_PREF", MODE_PRIVATE);
		if(eDakiaSharedPref.getBoolean("isFirstBoot", true)){
			try {
				Resources resources = this.getResources();
				AssetManager assetManager = resources.getAssets();
				InputStream inputStream = assetManager.open("eDakia.properties");
				Properties properties = new Properties();
				properties.load(inputStream);

				SharedPreferences.Editor prefsEditor = eDakiaSharedPref.edit();
				for(String aKey : properties.stringPropertyNames()){
					prefsEditor.putString(aKey, properties.getProperty(aKey));
				}
				prefsEditor.putBoolean("isFirstBoot", false);
				prefsEditor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
