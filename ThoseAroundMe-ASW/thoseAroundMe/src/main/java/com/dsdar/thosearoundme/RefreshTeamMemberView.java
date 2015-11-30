package com.dsdar.thosearoundme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.dao.WifiDatabaseHandler;
import com.dsdar.thosearoundme.location.LocationFinder.LocationResult;
import com.dsdar.thosearoundme.others.ImageUtil;
import com.dsdar.thosearoundme.util.LocationUtil;
import com.dsdar.thosearoundme.util.Util;
import com.dsdar.thosearoundme.util.WifiUtil;
import com.dsdar.util.TeamBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

/*
 *  This is a class that will run as a timer.  Its responsibility is to retrieve team member locations and update
 *  the map.
 *  
 *   	Rules:
 *   		1. For the first time update, or initiation then a map clear and placement of all markers with an inclusive zoom will be done.
 *   		2. For 2+ runs, then only the team member markers will be moved and NO zoom or map activities will be done.
 *   		3. If this is a request for a new team selected, then #1 applies and a full refresh must happen.
 */

public class RefreshTeamMemberView {
	public String itsMyTeamId = "";
	private static Context itsContext;
	private static GoogleMap itsGoogleMap;
	private static JSONObject itsMyTeamJson;
	private static JSONArray itsMyTeamArray;
	private static ProgressDialog itsProgressDialog;
	private static final String ARG_POSITION = "position";
	private static JSONArray itsMyTeamMembersArray = null;
	private static TextView itsNormalMapView, itsHybridMapView,
			itsSatelliteMapView;
	private int itsTabPosition;
	private String itsLoginUserId;
	private Builder itsLocationBuilder;

	// public static TeamMapFragment newInstance(Context theContext, int
	// position,
	// GoogleMap theGoogleMap, TextView theNormalMapView,
	// TextView theHybridMapView, TextView theSatelliteMapView,
	// JSONArray theTeamArray) {
	//
	// Log.i("RefreshTeamMemberView","newInstance.TeamMapFragment pos=" +
	// position);
	//
	// itsContext = theContext;
	// itsGoogleMap = theGoogleMap;
	// itsMyTeamArray = theTeamArray;
	// itsNormalMapView = theNormalMapView;
	// itsHybridMapView = theHybridMapView;
	// itsSatelliteMapView = theSatelliteMapView;
	//
	//
	// TeamMapFragment aFragment = new TeamMapFragment();
	// Bundle aBundle = new Bundle();
	// aBundle.putInt(ARG_POSITION, position);
	// aFragment.setArguments(aBundle);
	//
	// return aFragment;
	// }

	public RefreshTeamMemberView() {
		Log.i("RefreshTeamMemberView", "RefreshTeamMemberView()");
	}

	public RefreshTeamMemberView(Context c, GoogleMap g, TextView normal,
			TextView satellite, TextView hybrid, String loginUserId) {

		Log.i("RefreshTeamMemberView",
				"RefreshTeamMemberView(Context c, GoogleMap g,...)");

		itsContext = c;
		itsGoogleMap = g;
		itsNormalMapView = normal;
		itsHybridMapView = hybrid;
		itsSatelliteMapView = satellite;
		itsProgressDialog = new ProgressDialog(itsContext);
		itsLoginUserId = loginUserId;
	}

	public void onCreate() {

		AlertDialog alertDialog = new AlertDialog.Builder(itsContext).create();
		alertDialog.setTitle("Create");
		alertDialog.setMessage("Service Created");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Add your code for the button here.
			}
		});
		alertDialog.show();

		// itsWifiDatabaseHandler = new WifiDatabaseHandler(this);
		// initializeTimer();
	}

	public void onPause() {

		Log.i("RefreshTeamMemberView", "onPause");
		AlertDialog alertDialog = new AlertDialog.Builder(itsContext).create();
		alertDialog.setTitle("Pause");
		alertDialog.setMessage("Service Paused");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Add your code for the button here.
			}
		});
		alertDialog.show();

	}

	public void onResume() {

		Log.i("RefreshTeamMemberView", "onResume");
		AlertDialog alertDialog = new AlertDialog.Builder(itsContext).create();
		alertDialog.setTitle("Resume");
		alertDialog.setMessage("Service Resumed");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Add your code for the button here.
			}
		});
		alertDialog.show();
	}

	// public void updateTeamMemberID(String id){
	// itsMyTeamId = id;
	// }

	public void showAllTeamMembersOnMap() {
		itsGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
				itsLocationBuilder.build(), 50));
	}

	public void provisionTeamMemberTimer() {

		// System.out.println("*************************CALL FUNCTION*************************");
		Log.i("RefreshTeamMemberView", "provisionTeamMemberTimer "
				+ itsMyTeamId);
		try {
			// To get location updates for ever 1 minute (in milliseconds)
			long aTimeInterval = 3 * 60000;
			Timer aTimer = new Timer();
			aTimer.schedule(new TimerTask() {
				@Override
				public void run() {

					itsLocationHandler.sendEmptyMessage(1);

					// System.out.println("*************************CALL FUNCTION*************************");
				}
			}, 0, aTimeInterval);
		} catch (Exception e) {
			Log.e("RefreshTeamMemberView",
					"Exception occurred while initializing timer.", e);
		}

	}

	private Handler itsLocationHandler = new Handler() {
		@Override
		public void handleMessage(Message theMessage) {
			Log.i("RefreshTeamMemberView", "itsLocationHandler " + itsMyTeamId);
			if (theMessage.what == 1) {
				setMyTeamMembers();
			}
		}
	};

	private void setMyTeamMembers() {

		Log.i("RefreshTeamMemberView", "setMyTeamMembers CLEAR MAP");

		/*
		 * itsGoogleMap.r
		 * 
		 * itsGoogleMap.addMarker( new MarkerOptions()
		 * .position(aMemberLocation) .title(theMemberName) .snippet(thePhone)
		 * .icon(ImageUtil.createMemberMarker(itsContext))) .hideInfoWindow();
		 */
		itsGoogleMap.clear(); // todo only wnat to clear if team button pressed
		itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		itsNormalMapView.setBackgroundResource(R.drawable.map_button_active);
		itsHybridMapView
				.setBackgroundResource(R.drawable.hybrid_button_inactive);
		itsSatelliteMapView
				.setBackgroundResource(R.drawable.satellite_button_inactive);
		// try {
		// if(itsMyTeamArray!=null){
		// itsMyTeamJson = itsMyTeamArray.getJSONObject(itsTabPosition);
		//
		// itsMyTeamId = itsMyTeamJson.getString(TeamBuilder.TEAMID);

		itsMyTeamId = "111";

		Log.i("RefreshTeamMemberView", "setMyTeamMembers " + itsMyTeamId);

		// System.out.println("*************TEAMID: "+itsMyTeamId);

		new getMyTeamMembersAsyncTask().execute();
		// }
		// } catch (JSONException theJsonException) {
		// theJsonException.printStackTrace();
		// }
	}

	private class getMyTeamMembersAsyncTask extends
			AsyncTask<String, String, String> {

		private JSONObject itsMemberJson;
		private ArrayList<LatLng> itsMemberLocList = new ArrayList<LatLng>();

		@Override
		protected void onPreExecute() {
			itsProgressDialog.setCancelable(false);
			itsProgressDialog
					.setMessage("Retrieving your team members information...");
			itsProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			itsProgressDialog.setProgress(0);
			itsProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			JSONObject aTeamMembersJson = new JSONObject();
			JSONObject aRequestJson = new JSONObject();
			try {
				aRequestJson.put(TeamBuilder.TEAMID, itsMyTeamId);

				SharedPreferences aTeamSettings = itsContext
						.getSharedPreferences("TamPreferences",
								Context.MODE_PRIVATE);
				URL aUrl = new URL(aTeamSettings.getString("serverURL", "")
						+ "getMyTeamMembers");
				HttpURLConnection aHttpConnection = (HttpURLConnection) aUrl
						.openConnection();
				aHttpConnection.setRequestMethod("POST");
				aHttpConnection.setRequestProperty("Content-Type",
						"application/json");
				aHttpConnection.setDoOutput(true);
				PrintStream printStream = new PrintStream(
						aHttpConnection.getOutputStream());
				printStream.print(aRequestJson);
				printStream.close();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(aHttpConnection.getInputStream()));
				String response = reader.readLine();
				reader.close();

				aTeamMembersJson = new JSONObject(response);
				Log.i("RefreshTeamMemberView", "getMyTeamMembersAsyncTask "
						+ aTeamMembersJson.toString());

				itsMyTeamMembersArray = addMembersToMyTeamArray(aTeamMembersJson);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private JSONArray addMembersToMyTeamArray(JSONObject theTeamMembersJson) {
			boolean isMembersAdded = false;
			try {
				if (itsMyTeamMembersArray == null) {
					itsMyTeamMembersArray = new JSONArray();
					theTeamMembersJson.put("tabId", itsTabPosition);
					itsMyTeamMembersArray.put(0, theTeamMembersJson);
					isMembersAdded = true;
				} else {
					JSONObject aTeamJson;
					for (int index = 0; index < itsMyTeamMembersArray.length(); index++) {
						aTeamJson = itsMyTeamMembersArray.getJSONObject(index);
						if ((aTeamJson.has("tabId"))
								&& (aTeamJson.getInt("tabId") == itsTabPosition)) {
							theTeamMembersJson.put("tabId", itsTabPosition);
							itsMyTeamMembersArray
									.put(index, theTeamMembersJson);
							isMembersAdded = true;
							break;
						}
					}
				}
				if (!isMembersAdded) {
					theTeamMembersJson.put("tabId", itsTabPosition);
					itsMyTeamMembersArray.put(itsMyTeamMembersArray.length(),
							theTeamMembersJson);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return itsMyTeamMembersArray;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (itsMyTeamMembersArray != null) {
					JSONObject aMyTeamJson;
					for (int index = 0; index < itsMyTeamMembersArray.length(); index++) {
						aMyTeamJson = itsMyTeamMembersArray
								.getJSONObject(index);
						if ((aMyTeamJson.getInt("tabId") == itsTabPosition)
								&& (aMyTeamJson.getJSONArray(
										TeamBuilder.TEAMMEMBERS).length() > 0)) {
							JSONArray aTeamMemberArray = aMyTeamJson
									.getJSONArray(TeamBuilder.TEAMMEMBERS);
							double aLat, aLng = 0;
							String aName, aPhone = "", aId = "";
							itsLocationBuilder = new LatLngBounds.Builder();

							// Clear the markers on the google map.
							itsGoogleMap.clear();
							// Toast.makeText(itsContext,
							// "Refresh-Team Array Length -> " +
							// aTeamMemberArray.length(),
							// Toast.LENGTH_LONG).show();

							for (int index1 = 0; index1 < aTeamMemberArray
									.length(); index1++) {
								itsMemberJson = aTeamMemberArray
										.getJSONObject(index1);
								if (!itsLoginUserId
										.equalsIgnoreCase(itsMemberJson
												.getString(TeamBuilder.MEMBERID))) { // Ignore
																						// the
																						// json
																						// object
																						// which
																						// has
																						// the
																						// logged-in
																						// user's
																						// member-id.
									aName = itsMemberJson
											.getString(TeamBuilder.ALIAS);
									aPhone = itsMemberJson
											.getString(TeamBuilder.PHONE);
									aLat = itsMemberJson.getJSONObject(
											"location").getDouble("lat");
									aLng = itsMemberJson.getJSONObject(
											"location").getDouble("lng");
									aId = itsMemberJson
											.getString(TeamBuilder.MEMBERID);
									Log.i("RefreshTeamMemberView",
											"addMembersToMyTeamArray.onPostExecute "
													+ aId + " aName " + aName);

									LatLng aMemberLocation = new LatLng(aLat,
											aLng);
									itsLocationBuilder.include(aMemberLocation);
									itsMemberLocList.add(aMemberLocation);

									addMarkersOnMap(aName, aPhone, aLat, aLng);
								}
							}
							// displayMarkers();
						}
					}
				}
			} catch (JSONException theJsonException) {
				theJsonException.printStackTrace();
			} finally {
				itsProgressDialog.cancel();
			}
		}

		private void addMarkersOnMap(String theMemberName, String thePhone,
				double theLattitude, double theLongitude) {

			Log.i("RefreshTeamMemberView", "addMarkersOnMap " + theMemberName);

			LatLng aMemberLocation = new LatLng(theLattitude, theLongitude);
			// itsGoogleMap.addMarker(
			// new MarkerOptions()
			// .position(aMemberLocation)
			// .title(theMemberName)
			// .snippet(thePhone)
			// .icon(ImageUtil.createMemberMarker(itsContext)))
			// .hideInfoWindow();
		}

		private void displayMarkers() {

			Log.i("RefreshTeamMemberView", "displayMarkers members="
					+ itsMemberLocList.size());

			if (itsMemberLocList.size() == 1) {
				itsGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						itsMemberLocList.get(0), 17));
			} else {
				int index1 = 0;
				for (int index = 0; index < itsMemberLocList.size(); index++) {
					index1 = index + 1;
					if (index1 < itsMemberLocList.size()) {
						LatLng aLocation1 = itsMemberLocList.get(index);
						LatLng aLocation2 = itsMemberLocList.get(index1);
						double aDistance = LocationUtil.getDistance(aLocation1,
								aLocation2);
						if (aDistance > 100) {
							itsGoogleMap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(
											itsLocationBuilder.build(), 50));
							break;
						}
					} else {
						itsGoogleMap.moveCamera(CameraUpdateFactory
								.newLatLngZoom(itsMemberLocList.get(0), 17));
					}
				}
			}
		}
	}

}