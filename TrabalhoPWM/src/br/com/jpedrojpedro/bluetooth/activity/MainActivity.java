package br.com.jpedrojpedro.bluetooth.activity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import br.com.jpedrojpedro.trabalhopwm.R;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity 
{
	
	private BluetoothAdapter myBluetoothAdapter;
	private Button listBtn;
	private TextView msgTextView;
	private TextView macAddress;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	
	private static final String TAG = "AppMusica";
	
	// Well known SPP UUID
	private static final UUID MY_UUID =
	      UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Insert your bluetooth devices MAC address
	//private static String address = "00:1C:7B:AC:B7:49";
	private static String address = "20:13:06:19:16:37";
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	
    	myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	
        
        if(myBluetoothAdapter == null) 
        {
      	  Toast.makeText(getApplicationContext(),"Seu celular não suporta Bluetooth",
           		 Toast.LENGTH_LONG).show();
        }
        
        else 
        {
        	listBtn = (Button)findViewById(R.id.btnLst);
        	msgTextView = (TextView)findViewById(R.id.msgText);
        	macAddress = (TextView)findViewById(R.id.macText);
        //	address = macAddress.getText().toString();
  	        listBtn.setOnClickListener(new OnClickListener() 
  	        {
  	  		
  	  		@Override
  	  		public void onClick(View v)
  	  		{
  	  			// TODO Auto-generated method stub
  	  		    address = macAddress.getText().toString();
  	  		    sendData(msgTextView.getText().toString());
  	  		}
  	      });
  	        
  	   //   myListView = (ListView)findViewById(R.id.listView1);
  		
	      // create the arrayAdapter that contains the BTDevices, and set it to the ListView
	   //   BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
	     // myListView.setAdapter(BTArrayAdapter);
        }
        
   }
    
    
    @Override
    public void onResume() 
    {
      super.onResume();

      Log.d(TAG, "...In onResume - Attempting client connect...");
    
      // Set up a pointer to the remote node using it's address.
    
      BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);
     
    
      // Two things are needed to make a connection:
      //   A MAC address, which we got above.
      //   A Service ID or UUID.  In this case we are using the
      //     UUID for SPP.
      try {
        btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
      } catch (IOException e) {
        errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
      }
    
      // Discovery is resource intensive.  Make sure it isn't going on
      // when you attempt to connect and pass your message.
      myBluetoothAdapter.cancelDiscovery();
    
      // Establish the connection.  This will block until it connects.
      Log.d(TAG, "...Connecting to Remote...");
      try {
        btSocket.connect();
        Log.d(TAG, "...Connection established and data link opened...");
      } catch (IOException e) {
        try {
          btSocket.close();
        } catch (IOException e2) {
          errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
        }
      }
      
      // Create a data stream so we can talk to server.
      Log.d(TAG, "...Creating Socket...");

      try {
        outStream = btSocket.getOutputStream();
      } catch (IOException e) {
        errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
      }
    }
    
    @Override
    public void onPause()
    {
      super.onPause();

      Log.d(TAG, "...In onPause()...");

      if (outStream != null) {
        try {
          outStream.flush();
        } catch (IOException e) {
          errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
        }
      }

      try     {
        btSocket.close();
      } catch (IOException e2) {
        errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
      }
    }
    
    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
            title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
      }

    
	
    private void sendData(String message) 
    {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Sending data: " + message + "...");

        try {
          outStream.write(msgBuffer);
        } catch (IOException e) {
          String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
          if (address.equals("00:00:00:00:00:00")) 
            msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
          msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
          
          errorExit("Fatal Error", msg);       
        }
      }

    
    private void PlayWav(String location, View v)
    {
    	// Faz um byteArray ;-)
    	final Context context = v.getContext();
        int bytesRead;
    	try
    	{
	    	InputStream is = context.openFileInput(location);
	
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	        byte[] b = new byte[1024];
	
	        while ((bytesRead = is.read(b)) != -1) 
	        {
	            bos.write(b, 0, bytesRead);
	        }
	
	        byte[] bytes = bos.toByteArray();
	        System.out.println(" One random values is: "+bytes[5]+" \n");
    	}catch(Exception e) 
    	{
            Toast.makeText(this, "Error starting draw. ",Toast.LENGTH_SHORT).show();

        }     
    	
    	
           
    }
}