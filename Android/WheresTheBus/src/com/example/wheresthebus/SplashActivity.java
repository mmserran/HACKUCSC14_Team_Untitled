package com.example.wheresthebus;
// Mentor email: robert@civinomics.com

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	private Location currentBestLocation = null;
	static final int TWO_MINUTES = 1000 * 60 * 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initiate the Upload Button
		Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
		uploadBtn.setTextColor(Color.WHITE);
		uploadBtn.setBackgroundResource(R.drawable.green_btn_statelist);
		uploadBtn.setGravity(Gravity.CENTER);
		//uploadBtn.setTextSize(24);
		uploadBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				TextView test = (TextView) findViewById(R.id.testArea);
					// check if GPS is enabled or not
				
				Location coords = getLastBestLocation();
				double latty = coords.getLatitude();
				double longy = coords.getLongitude();
				String testMsg = Double.toString(latty) + ", " + Double.toString(longy);
				test.setText(testMsg);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * @return the last know best location
	 * Source: http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
	 */
	private Location getLastBestLocation() {
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

	    long GPSLocationTime = 0;
	    if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

	    long NetLocationTime = 0;

	    if (null != locationNet) {
	        NetLocationTime = locationNet.getTime();
	    }

	    if ( 0 < GPSLocationTime - NetLocationTime ) {
	        return locationGPS;
	    }
	    else {
	        return locationNet;
	    }
	}
	
	public void onLocationChanged(Location location) {

	    makeUseOfNewLocation(location);

	    if(currentBestLocation == null){
	        currentBestLocation = location;
	    }
	}
	
	/**
	 * This method modify the last know good location according to the arguments.
	 *
	 * @param location The possible new location.
	 */
	void makeUseOfNewLocation(Location location) {
	    if ( isBetterLocation(location, currentBestLocation) ) {
	        currentBestLocation = location;
	    }
	}
	
	/** Determines whether one location reading is better than the current location fix
	 * @param location  The new location that you want to evaluate
	 * @param currentBestLocation  The current location fix, to which you want to compare the new one.
	 * Source: http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location,
	    // because the user has likely moved.
	    if (isSignificantlyNewer) {
	        return true;
	        // If the new location is more than two minutes older, it must be worse.
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	                                                currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same 
	 * Source: http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
	 */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	        return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
