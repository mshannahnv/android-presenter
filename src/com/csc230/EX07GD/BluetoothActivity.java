// EX07GD: BlueTooth Client Starter Template
// Coach / Fall 2014

// 10-27-2014: simplify, adapt from Steve's BlueToothCar project

// Objective:
//==========
//	* Provide Starter Template for Blue Tooth Project: Android/PC Interface

// Tasks:
//=======
// 1. Read this code

// 2. Upload & Run this code on Your target Android device 
//		Using Eclipse: Run/ Debug Configurations / debug

// 3. a. Run BlueToothServer @ PC
//    b. Run Control Interface @ PC

// 4. Verify system operates as expected:
//		* Press 1, character "1" is sent to PC, Image5 reflects selection 1
//		* Press 2, character "2" is sent to PC, Image5 reflects selection 2

// 5. Resolve any operation issues

// 6. Add Images & codes for 3 & 4

// 7. Verify system is operational

// 8. Consult with Coach for next steps
//=========================================================================


package com.csc230.EX07GD;

//import android.os.SystemClock;     //required for sleep(ms)
//import java.lang.Object;           //required for sleep(ms)

import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
//import android.text.format.Time;     //1/3/2013: required for declare type Time

//import android.media.MediaPlayer;		//***

import android.os.Bundle;
import android.os.Handler;           //1/3/2013: required for timer function




//******************************************************************************

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


//import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

//=== for animation =====
import android.view.animation.Animation;

//(not used) import android.view.animation.Animation.AnimationListener;

import android.view.animation.AnimationUtils;

//(not used) import android.view.animation.LayoutAnimationController;
//==========================================




public class BluetoothActivity extends Activity implements OnTouchListener {
	
	
			private Timer timer = new Timer();
		Handler handler = new Handler();
	//*********************************************************************	

	
	
	// Debugging
	private static final String TAG = "BluetoothActivity";
	
	// Intent Request Codes
	private static final int REQUEST_CONNECT_DEVICE = 0;
	private static final int REQUEST_ENABLE_BT = 1;
	
	// Bluetooth Items
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothService mBluetoothService;

	// UI Elements
	
	private TextView Text_Day_Date;
	private View Image1;
	private View Image2;	
	private View Image3;
	private View Image4;	

	private ImageView Image5;		


	private View Image_Left;	
	private View Image_Right;			

	private boolean connected = false;
	
    /** Called when the activity is first created. */
    @Override
    
    //=====================================================
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       Text_Day_Date = (TextView) findViewById(R.id.Text_Day_Date);        
       Image1 = (View) findViewById(R.id.Image1); 
       Image2 = (View) findViewById(R.id.Image2); 
       Image3 = (View) findViewById(R.id.Image3);  
       Image4 = (View) findViewById(R.id.Image4);         
      
       Image5 = (ImageView) findViewById(R.id.Image5);            
 
       Image_Left = (View) findViewById(R.id.Image_Left);        
       Image_Right = (View) findViewById(R.id.Image_Right);               
  
        Image1.setOnTouchListener(this); 
        Image2.setOnTouchListener(this);        
        Image3.setOnTouchListener(this); 
        Image4.setOnTouchListener(this);         
   
        Image5.setOnTouchListener(this);            
 
        Image_Left.setOnTouchListener(this);          
        Image_Right.setOnTouchListener(this);   
        
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mBluetoothService = new BluetoothService(this);
     
        // *** Search for BlueTooth Device (PC)
		Intent intent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(intent, REQUEST_CONNECT_DEVICE);	
		
		// Get 1s timer started, 1-shot:		
		doTimerTask();
		
		
    }  // end public void onCreate
    //==========================================================
    private void doTimerTask(){
   	

    	
     	timer.scheduleAtFixedRate(new TimerTask(){
			
    		public void run(){   // outer run #1

    			//=======================================================
    			handler.post(new Runnable()       // details of handler is beyond scope of this class, we just drive it.
    			{

    		    	
    				public void run(){  // inner
    					
    					displayTimeDate();    //***
    					
    				} // end public void run()    //inner
    			});  // end handler.post(new Runnable()
    			//========================================================
    			
    	} // end public void run()     // outer run #1
    		
    	},0,1000);   // end timer.scheduleAtFixedRate(new TimerTask()   // 1000 ms = 1 s

    	
    } // end  private void doTimerTask()
    //**************************************************************************************
    private void displayTimeDate()
    {
    	Date CurDate = new Date();  
 
    	//SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d '/' hh:mm:ss a");  
    	SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM d, yyyy");  
    	
    	String formattedDateString = formatter.format(CurDate);  
    	Text_Day_Date.setText(formattedDateString);
    }
    //*********************************************************************************
   
    
    
    
    @Override
	protected void onStart() {
		super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
		if(!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}


	@Override
	protected void onDestroy() {

		super.onDestroy();
		if(mBluetoothService != null) {
			mBluetoothService.stop();
		}
	}
//=============================================================
	public boolean onTouch(View view, MotionEvent event) {
		
		 //*** Load animations 
		 Animation linear = AnimationUtils.loadAnimation(this, R.anim.in_out);	   

		
		
		if(connected) {
			switch(event.getAction()) { 
			case MotionEvent.ACTION_DOWN:
				switch(view.getId()) {
				case R.id.Image1:                 //***
					mBluetoothService.write('1'); 
					Image1.startAnimation(linear);
					
					Image5.setImageResource(R.drawable.coffee_cup_1_1920x1080); 
					
					break;
				case R.id.Image2:
					mBluetoothService.write('2'); 
					Image2.startAnimation(linear);
					
					Image5.setImageResource(R.drawable.summer_white_beach_1920x1080); 
					
					break;
				case R.id.Image3:
					mBluetoothService.write('3'); 
					Image3.startAnimation(linear);
					
					Image5.setImageResource(R.drawable.violin_1920x1080);
					break;
					
				case R.id.Image4:                 
					mBluetoothService.write('4'); 
					Image4.startAnimation(linear);
					
					Image5.setImageResource(R.drawable.sky_1920x1080);
					break;	
					
				
				case R.id.Image_Left:                 
					mBluetoothService.write('B'); 
					Image_Left.startAnimation(linear);
					break;					
					
				case R.id.Image_Right:                 
					mBluetoothService.write('C'); 
					Image_Right.startAnimation(linear);
					break;											
					
				}  // end switch(view.getId())
				break;
			} // end switch(event.getAction())
		} // end if(connected)
		return true;
	} // end onTouch(View view, MotionEvent event) 
//===============================================================	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.scan:
			Intent intent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(intent, REQUEST_CONNECT_DEVICE);			
			return true;

		}
		return false;
	} // // end public boolean onOptionsItemSelected(MenuItem item) 


	@Override
	//=================================================================================
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if(resultCode == Activity.RESULT_OK) {
				// Get Device MAC Address
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get BluetoothDevice Object
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				// Attempt to Connect to Device
				mBluetoothService.connect(device);				
		        connected = true;
			}
			break;
		case REQUEST_ENABLE_BT:
			if(resultCode != Activity.RESULT_OK) {
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
			}
			break;
		}   // end switch(requestCode)
	}  // end protected void onActivityResult
	//====================================================================================

		
}    // End of BluetoothCarActivity 

		
		


