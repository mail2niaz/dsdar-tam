package com.dsdar.thosearoundme.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Class to get location based on the available listeners
 * 
 * @author david_reynolds
 */
public class LocationFinder {
	private LocationFinder() {
	}

	boolean is_gps_enabled = false;
	LocationResult itsLocationResult;
	boolean is_network_enabled = false;
	LocationManager itsCurrentLocManager;
	private static LocationFinder itsLocationFinder;

	public static LocationFinder getInstance() {
		if (itsLocationFinder == null) {
			itsLocationFinder = new LocationFinder();
		}
		return itsLocationFinder;
	}

	public boolean readCurrentLocation(Context theContext,
			LocationResult theLocationResult) {
		try {
			itsLocationResult = theLocationResult;
			itsCurrentLocManager = (LocationManager) theContext
					.getSystemService(Context.LOCATION_SERVICE);

			// exceptions will be thrown if provider is not permitted.
			try {
				is_gps_enabled = itsCurrentLocManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
			} catch (Exception ex) {
			}
			try {
				is_network_enabled = itsCurrentLocManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception ex) {
			}

			// don't start listeners if no provider is enabled
			if (!is_gps_enabled && !is_network_enabled)
				return false;

			if (is_gps_enabled) {
				itsCurrentLocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0,
						currentLocationListenerGps);
			}

			if (is_network_enabled) {
				itsCurrentLocManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						currentLocationListenerNetwork);
			}
		} catch (Exception theException) {
			Log.e("LocationFinder",
					"Exception occurred while retrieving current location",
					theException);
		}
		return true;
	}

	LocationListener currentLocationListenerGps = new LocationListener() {
		public void onLocationChanged(Location theLocation) {
			itsCurrentLocManager.removeUpdates(currentLocationListenerGps);
			itsCurrentLocManager.removeUpdates(currentLocationListenerNetwork);

			itsLocationResult.gotLocation(theLocation);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener currentLocationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location theLocation) {
			itsCurrentLocManager.removeUpdates(currentLocationListenerNetwork);
			itsCurrentLocManager.removeUpdates(currentLocationListenerGps);

			itsLocationResult.gotLocation(theLocation);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}