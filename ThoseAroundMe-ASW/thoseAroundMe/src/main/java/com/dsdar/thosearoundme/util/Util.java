package com.dsdar.thosearoundme.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {
	/**
	 * Returns the Internet connection status
	 * 
	 * @param theContext
	 * @return boolean
	 */
	public static boolean isInternetConnected(Context theContext) {
		ConnectivityManager aConnectivityManager = (ConnectivityManager) theContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo aNetworkInfo = aConnectivityManager.getActiveNetworkInfo();
		if (aNetworkInfo != null && aNetworkInfo.isAvailable()
				&& aNetworkInfo.isConnected()) {
			Log.i("Util", "isInternetConnected=true");
			return true;
		} else {
			return false;
		}
	}
	

	/**
	 * Method to hide softkeypad in android
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	/**
	 * Method to show softkeypad in android
	 * @param activity
	 */
	public static void showSoftKeyboard(Activity activity, View theView) {
		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(theView, InputMethodManager.SHOW_FORCED);
	}
	
	/**
	 * Method to show softkeypad on activity load
	 * @param theActivity
	 */
	public static void showKeypadOnActivityLoad(Activity theActivity) {
		InputMethodManager aInputMethodManager = (InputMethodManager) theActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		aInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}
	
	/**
	 * Method to validate phone number
	 * @param theMobileNumber
	 * @return
	 */
	public static boolean ValidateMobileNumber(String theMobileNumber)
	{
		boolean returnValue = true;
		if(theMobileNumber.equals(""))
		{
			returnValue = false;
		}
		else if (theMobileNumber.length() < 10)
		{
			returnValue = false;
		}
		else if((theMobileNumber.length() == 10))
		{
			if(theMobileNumber.substring(0, 1).equals("0"))
			{
				returnValue = false;
			}
			else
			{
				Pattern p1 = Pattern.compile("^[0-9]+$");
				Matcher m1 = p1.matcher(theMobileNumber);
				boolean b1 = m1.find();

				if (!b1) {
					returnValue = false;
				}
			}
		}
		else if((theMobileNumber.length() == 11) && (!theMobileNumber.substring(0, 1).equals("0")))
		{
			returnValue =  false;
		}
		else if((theMobileNumber.length() == 12) && (!theMobileNumber.substring(0, 2).equals("91")))
		{
			returnValue = false;
		}
		else if((theMobileNumber.length() == 13) && !theMobileNumber.substring(0, 3).equals("+91") && !theMobileNumber.substring(0, 3).equals("#91"))
		{
			returnValue = false;
		}
		else
		{
			Pattern p = Pattern.compile("[+]",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(theMobileNumber.substring(0,1));
			boolean b = m.find();

			if (b) {
				Pattern p1 = Pattern.compile("^[0-9]+$",
						Pattern.CASE_INSENSITIVE);
				Matcher m1 = p1.matcher(theMobileNumber.substring(1,theMobileNumber.length()));
				boolean b1 = m1.find();

				if (!b1) {
					returnValue = false;
				}
				else
				{
					returnValue = true;
				}
			}
			else
			{
				Pattern p1 = Pattern.compile("^[0-9]+$");
				Matcher m1 = p1.matcher(theMobileNumber);
				boolean b1 = m1.find();

				if (!b1) {
					returnValue = false;
				}
				else
				{
					returnValue = true;
				}
			}
		}
		return returnValue;
	}
	
	 /**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}


