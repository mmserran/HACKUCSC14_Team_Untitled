package com.example.wheresthebus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MapActivity extends Activity {

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
				Log.d("socket", socket.isConnected()==true ? "true" : "false");
				Log.e("Connected", "Connected to server");
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			Log.e("Exited", "exited client thread");

		}

	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

		new Thread(new ClientThread()).start();
		Log.d("debug", "ClientThread set up");
		
		Integer num = 1;
		String requestStr = 'G' + Integer.toString(num);
		
		String line;
		TextView MsgFromServer = (TextView) findViewById(R.id.testArea);
		
		// Send data to server
		OutputStreamWriter outputStream = null;

		Log.d("debug", "BufferedReader in1");
		try {
			Log.d("debug", "asdfasdf1");
			OutputStream outStr = socket.getOutputStream();
			Log.d("debug", "asdfasdf2");
			outputStream = new OutputStreamWriter(outStr);
			Log.d("debug", "asdfasdf3");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.d("debug", "BufferedReader in2");
		BufferedWriter buffWriter = new BufferedWriter(outputStream);

		Log.d("debug", "BufferedReader in3");
		try {
			PrintWriter out = new PrintWriter(buffWriter, true);
			
			
			out.println(requestStr);

			Log.d("debug", "BufferedReader in4");

			final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while((line=in.readLine())!=null){
                MsgFromServer.append(line);
        		Log.d("DATA!!", "Received Data from Server");
            }
			Log.d("debug", "BufferedReader in5");
                
                
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Wait for inbound packet

		
		// Parse and display data
		
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}