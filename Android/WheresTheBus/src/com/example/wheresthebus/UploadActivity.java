package com.example.wheresthebus;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class UploadActivity extends Activity {

	private Location currentBestLocation = null;
	static final int TWO_MINUTES = 500 * 60;
	
	private Spinner spinner1, spinner2;
	String [] routesList, directionList;

	private Socket socket;

	private static final int SERVERPORT = 5867;
	private static final String SERVER_IP = "54.186.243.187";
	
	/*
	 * Source: http://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
	 */
	class ClientThread implements Runnable {

		@Override
		public void run() {

			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

				socket = new Socket(serverAddr, SERVERPORT);
				Log.e("Connected", "Connected");
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		new Thread(new ClientThread()).start();

		TextView routeTitle = (TextView) findViewById(R.id.busRouteTitle);
		TextView directionTitle = (TextView) findViewById(R.id.directionTitle);
		
		routeTitle.setTextColor(Color.WHITE);
		routeTitle.setTextSize(16);
		directionTitle.setTextColor(Color.WHITE);
		directionTitle.setTextSize(16);
		
		TextView title = (TextView) findViewById(R.id.uploadTitle);
		title.setTypeface(null, Typeface.BOLD);
		title.setTextColor(Color.WHITE);
		title.setGravity(Gravity.CENTER);
		title.setTextSize(36);
		
		// Create String arrays
		routesList = getResources().getStringArray(R.array.routesOut);
		directionList = getResources().getStringArray(R.array.directions);
		
		// Create an ArrayAdapter using default spinner layout
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>
        (this, android.R.layout.simple_spinner_item, routesList);
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>
        (this, android.R.layout.simple_spinner_item, directionList);
        
		// Specify the layout to use when the list of choices appears
		dataAdapter1.setDropDownViewResource
		        (android.R.layout.simple_spinner_dropdown_item);
		dataAdapter2.setDropDownViewResource
		        (android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapters to the spinners
        spinner1 = (Spinner) findViewById(R.id.busRouteSpinner);
		spinner1.setAdapter(dataAdapter1);
        spinner2 = (Spinner) findViewById(R.id.directionSpinner);
		spinner2.setAdapter(dataAdapter2);
		
		// Spinner item selection Listener  
		addListenerOnSpinnerItemSelection();
		
		// Button click Listener 
		addListenerOnButton();
		
		// Initiate the Upload Button
		final Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
		uploadBtn.setTextColor(Color.WHITE);
		uploadBtn.setBackgroundResource(R.drawable.orange_btn_statelist);
		uploadBtn.setGravity(Gravity.CENTER);
		//uploadBtn.setTextSize(24);
		uploadBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				// Perform action on click
				Location coords = getLastBestLocation();
				double latty = coords.getLatitude();
				double longy = coords.getLongitude();
				//String testMsg = Double.toString(latty) + "*" + Double.toString(longy);
				
				Spinner busSpinner = (Spinner)findViewById(R.id.busRouteSpinner);
				Spinner dirSpinner = (Spinner)findViewById(R.id.directionSpinner);
				
				int route = busSpinner.getSelectedItemPosition();
				String direction = dirSpinner.getSelectedItem().toString();
				char dirSignal = direction==directionList[0] ? 'S' : 'D';
						
				Calendar c = Calendar.getInstance(); 
				int time = c.get(Calendar.SECOND);
				
				String outStr = createOutString(latty, longy, route, time, dirSignal);
				uploadBtn.setText(outStr);
				
				// Send data to server
				try {
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);
					out.println(outStr);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}

	/* Add spinner data
	 * Source: http://androidexample.com/Spinner_Basics_-_Android_Example/index.php?view=article_discription&aid=82&aaid=105
	 */
    public void addListenerOnSpinnerItemSelection(){
         
                spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
     
    /* get the selected dropdown list value
     * Source: http://androidexample.com/Spinner_Basics_-_Android_Example/index.php?view=article_discription&aid=82&aaid=105
     */
    public void addListenerOnButton() {
 
        spinner1 = (Spinner) findViewById(R.id.busRouteSpinner);
         
        final Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
 
        uploadBtn.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
 
            	uploadBtn.setText(String.valueOf(spinner1.getSelectedItem()));
            }
 
        });
 
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
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
	
	//@Override
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
	
	public class CustomOnItemSelectedListener implements OnItemSelectedListener {
		 
	    public void onItemSelected(AdapterView<?> parent, View view, int pos,
	            long id) {
	         
	        Log.d("HERE", "On Item Select : \n" + parent.getItemAtPosition(pos).toString());
	    }
	 
	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	        // TODO Auto-generated method stub
	 
	    }
	 
	}
	
	/** Returns an output string accepted by our C server
	 *
	 */
	private String createOutString(double latty, double longy, 
									int route, double time, char direction) {
		
		// "SDOUBLE*DOUBLE*STRING*DOUBLE*D/S*
		String outStr = "S" + Double.toString(latty) + "*" + Double.toString(longy)
						+ "*" + route + "*" + Double.toString(time) + "*" 
						+ direction + "*";
		
		//outStr = 'S' + Double.toString(latty) + "*" + Double.toString(longy);
		
		return outStr;
	}
}

