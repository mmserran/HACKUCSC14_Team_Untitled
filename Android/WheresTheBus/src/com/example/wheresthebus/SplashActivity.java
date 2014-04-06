package com.example.wheresthebus;
// Mentor email: robert@civinomics.com

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {
	
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
		setContentView(R.layout.activity_splash);

		new Thread(new ClientThread()).start();
		
		// Initiate the Map Button
		Button mapBtn = (Button) findViewById(R.id.mapBtn);
		mapBtn.setTextColor(Color.WHITE);
		mapBtn.setBackgroundResource(R.drawable.green_btn_statelist);
		mapBtn.setGravity(Gravity.CENTER);
		//uploadBtn.setTextSize(24);
		mapBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				// Open Map window on click
				Intent intent = new Intent(v.getContext(), MapActivity.class);
				v.getContext().startActivity(intent);
				
			}
		});
		
		// Initiate the Upload Button
		Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
		uploadBtn.setTextColor(Color.WHITE);
		uploadBtn.setBackgroundResource(R.drawable.green_btn_statelist);
		uploadBtn.setGravity(Gravity.CENTER);
		//uploadBtn.setTextSize(24);
		uploadBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				// Perform action on click
				Intent intent = new Intent(v.getContext(), UploadActivity.class);
				v.getContext().startActivity(intent);
				
			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}