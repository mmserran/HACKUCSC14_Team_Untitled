package com.example.wheresthebus;
// Mentor email: robert@civinomics.com

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		

		TextView splashTitle = (TextView) findViewById(R.id.splashTitle);
		splashTitle.setTextSize(36);
		splashTitle.setGravity(Gravity.CENTER);
		
		// Initiate the Map Button
		Button mapBtn = (Button) findViewById(R.id.mapBtn);
		mapBtn.setTextColor(Color.WHITE);
		mapBtn.setBackgroundResource(R.drawable.orange_btn_statelist);
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
		uploadBtn.setBackgroundResource(R.drawable.orange_btn_statelist);
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