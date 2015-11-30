package com.dsdar.thosearoundme;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.dsdar.util.TeamBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/*
 *  This is a class that will run as a timer.  It will take the latest stored GPS information and plot the user ont he map
 *  the map.
 *  
 *   	Rules:
  */


public class RefreshMyPositionView {
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
	com.google.android.gms.maps.model.Marker myPosition = null;

//	public static TeamMapFragment newInstance(Context theContext, int position,
//			GoogleMap theGoogleMap, TextView theNormalMapView,
//			TextView theHybridMapView, TextView theSatelliteMapView,
//			JSONArray theTeamArray) {
//
//		Log.i("RefreshMyPositionView","newInstance.TeamMapFragment pos=" + position);
//
//		itsContext = theContext;
//		itsGoogleMap = theGoogleMap;
//		itsMyTeamArray = theTeamArray;
//		itsNormalMapView = theNormalMapView;
//		itsHybridMapView = theHybridMapView;
//		itsSatelliteMapView = theSatelliteMapView;
//		
//
////		TeamMapFragment aFragment = new TeamMapFragment();
////		Bundle aBundle = new Bundle();
////		aBundle.putInt(ARG_POSITION, position);
////		aFragment.setArguments(aBundle);
//
//		return aFragment;
//	}
	
	public RefreshMyPositionView(){
		Log.i("RefreshMyPositionView","RefreshMyPositionView()");
	}
	
	public RefreshMyPositionView(Context c, GoogleMap g,TextView normal, TextView satellite, TextView hybrid, String loginUserId ){

		Log.i("RefreshMyPositionView","RefreshMyPositionView(Context c, GoogleMap g,...)");

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
		
//		itsWifiDatabaseHandler = new WifiDatabaseHandler(this);
//		initializeTimer();
	}
	
	public void onPause(){
		
		Log.i("RefreshMyPositionView","onPause");
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
	
	public void onResume(){
		
		Log.i("RefreshMyPositionView","onResume");
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
	
//	public void updateTeamMemberID(String id){
//		itsMyTeamId = id;
//	}
	
	public void provisionMyLocationTimer(){
		
		//System.out.println("*************************CALL FUNCTION*************************");
		Log.i("RefreshMyPositionView","provisionMyLocationTimer "+ itsMyTeamId);
		try {
			// To get location updates for ever 1 minute (in milliseconds)
			long aTimeInterval = 1 * 60000;
			Timer aTimer = new Timer();
			aTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					
					itsLocationHandler.sendEmptyMessage(1);

			//		System.out.println("*************************CALL FUNCTION*************************");
				}
			}, 0, aTimeInterval);
		} catch (Exception e) {
			Log.e("RefreshMyPositionView",
					"Exception occurred while initializing timer.",
					e);
		}
		
	}
	
	private Handler itsLocationHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message theMessage) 
		{
			Log.i("RefreshMyPositionView","itsLocationHandler "+ itsMyTeamId);
			if(theMessage.what == 1)
			{
				//setMyTeamMembers();
				// seems like all I need to do is read the gps and plot on the graph here
				SharedPreferences aTeamSettings = itsContext.getSharedPreferences(
						"TamPreferences", Context.MODE_PRIVATE);

				if (aTeamSettings.getString(TeamBuilder.LAT, "").length()>0 && aTeamSettings.getString(TeamBuilder.LNG, "").length()>0) {
						
					aTeamSettings.getString(TeamBuilder.ACCURACY, "");
					double aLat = Double.valueOf(aTeamSettings.getString(TeamBuilder.LAT, ""));
					double aLng = Double.valueOf(aTeamSettings.getString(TeamBuilder.LNG, ""));
					aTeamSettings.getString(TeamBuilder.BEARING, "");
					aTeamSettings.getString(TeamBuilder.LASTUPDATE, "");
	
					LatLng aMemberLocation = new LatLng(aLat, aLng);
	
					addMarkersOnMap("Me", "", aMemberLocation);
					//displayMarker(aMemberLocation);
				}
				
			}
		}
	};

	private void addMarkersOnMap(String theMemberName, String thePhone,
			LatLng aMemberLocation) {


		//LatLng aMemberLocation = new LatLng(theLattitude, theLongitude);
		
		if (null == myPosition) {
			Log.i("RefreshMyPositionView","addMarkersOnMap NEW MARKER");
		myPosition=itsGoogleMap.addMarker(
				new MarkerOptions()
						.position(aMemberLocation)
						.title(theMemberName)
						.snippet(thePhone));
		} else {
			Log.i("RefreshMyPositionView","addMarkersOnMap update marker");
		
		myPosition.setPosition(aMemberLocation);
		}
		
		myPosition.hideInfoWindow();
	}

	private void displayMarker(LatLng aMemberLocation) {
		
		Log.i("RefreshMyPositionView","displayMarker " + aMemberLocation.latitude );

		itsGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				aMemberLocation, 17));

	}
	
/*	
	private void setMyTeamMembers() {
		
		Log.i("RefreshMyPositionView","setMyTeamMembers CLEAR MAP");

//		itsGoogleMap.r
//		
//		itsGoogleMap.addMarker(
//				new MarkerOptions()
//						.position(aMemberLocation)
//						.title(theMemberName)
//						.snippet(thePhone)
//						.icon(ImageUtil.createMemberMarker(itsContext)))
//				.hideInfoWindow();
//		
		itsGoogleMap.clear();  // todo only wnat to clear if team button pressed
		itsGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		itsNormalMapView.setBackgroundResource(R.drawable.map_button_active);
		itsHybridMapView
				.setBackgroundResource(R.drawable.hybrid_button_inactive);
		itsSatelliteMapView
				.setBackgroundResource(R.drawable.satellite_button_inactive);
//		try {
//			if(itsMyTeamArray!=null){
//				itsMyTeamJson = itsMyTeamArray.getJSONObject(itsTabPosition);
//				
//				itsMyTeamId = itsMyTeamJson.getString(TeamBuilder.TEAMID);

			itsMyTeamId = "222";

			Log.i("RefreshMyPositionView","setMyTeamMembers "+ itsMyTeamId);

				//System.out.println("*************TEAMID: "+itsMyTeamId);
		
				new getMyTeamMembersAsyncTask().execute();
//			}
//		} catch (JSONException theJsonException) {
//			theJsonException.printStackTrace();
//		}
	}

	private class getMyTeamMembersAsyncTask extends
			AsyncTask<String, String, String> {

		private JSONObject itsMemberJson;
		private ArrayList<LatLng> itsMemberLocList = new ArrayList<LatLng>();
		private Builder itsLocationBuilder;

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
				Log.i("RefreshMyPositionView","getMyTeamMembersAsyncTask "+ aTeamMembersJson.toString());

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
							String aName, aPhone = "", aId="";
							itsLocationBuilder = new LatLngBounds.Builder();
							for (int index1 = 0; index1 < aTeamMemberArray
									.length(); index1++) {
								itsMemberJson = aTeamMemberArray
										.getJSONObject(index1);

								aName = itsMemberJson
										.getString(TeamBuilder.ALIAS);
								aPhone = itsMemberJson
										.getString(TeamBuilder.PHONE);
								aLat = itsMemberJson.getJSONObject("location")
										.getDouble("lat");
								aLng = itsMemberJson.getJSONObject("location")
										.getDouble("lng");
								aId = itsMemberJson
										.getString(TeamBuilder.MEMBERID);
								Log.i("RefreshMyPositionView","addMembersToMyTeamArray.onPostExecute "+ aId + " aName " + aName);
								
								
								LatLng aMemberLocation = new LatLng(aLat, aLng);
								itsLocationBuilder.include(aMemberLocation);
								itsMemberLocList.add(aMemberLocation);

								addMarkersOnMap(aName, aPhone, aLat, aLng);
								displayMarkers();
							}
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
				LatLng aMemberLocation) {

			Log.i("RefreshMyPositionView","addMarkersOnMap "+ theMemberName );

			//LatLng aMemberLocation = new LatLng(theLattitude, theLongitude);
			itsGoogleMap.addMarker(
					new MarkerOptions()
							.position(aMemberLocation)
							.title(theMemberName)
							.snippet(thePhone)
							.icon(ImageUtil.createMemberMarker(itsContext)))
					.hideInfoWindow();
		}

		private void displayMarker(LatLng aMemberLocation) {
			
			Log.i("RefreshMyPositionView","displayMe" );

			itsGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					aMemberLocation, 17));

		}
	}
*/
	
}