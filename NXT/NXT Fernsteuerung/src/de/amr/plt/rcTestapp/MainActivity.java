package de.amr.plt.rcTestapp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import lejos.geom.Line;

import parkingRobot.INxtHmi.Mode;
import parkingRobot.hsamr3.Guidance.Guidance.CurrentStatus;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import de.amr.plt.rcParkingRobot.AndroidHmiPLT;
import de.amr.plt.rcParkingRobot.IAndroidHmi.ParkingSlot;
import de.amr.plt.rcTestapp.Canvas.Map_canvas;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Bundle;

/**
 * The activity visualizes NXT data such as bluetooth connection, current
 * status, x-,y-coordinate, angle and distance values.
 * 
 * @author PLT
 */

public class MainActivity extends Activity {
	
	public static float current_posx;
	public static float current_posy;
	public static float last_posx;
	public static float last_posy;
	
	public static ArrayList<Integer> position_listx = new ArrayList<Integer>();
	public static ArrayList<Integer> position_listy = new ArrayList<Integer>();
	public static int no_slots;
	
	public static float angle;
	
	static public Map_canvas map;
	public static boolean connection;
	
	// representing local Bluetooth adapter
	BluetoothAdapter mBtAdapter = null;
	// representing the bluetooth hardware device
	BluetoothDevice btDevice = null;
	// instance handels bluetooth communication to NXT
	
	AndroidHmiPLT hmiModule = null;
	// request code
	final int REQUEST_SETUP_BT_CONNECTION = 1;
	
	static boolean orientation = false; // true =portrait, false = landscape
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("info", "onCreate");
		setFragment();
		current_posx = calc_posx(0);
		current_posy = calc_posy(0);
		last_posx = current_posx;
		last_posy = current_posy;
		connection = false;
		// setContentView(R.layout.activity_main);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			Log.i("info", "onConfigChange");
			
			setFragment();
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
			Log.i("Fragment", "error " + e.toString());
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		
			case R.id.restart:
				restartActivity();
				break;
			
			case R.id.testmenu:
				TestButton(findViewById(R.id.testmenu));
				
				break;
			
			case R.id.dc_bluetooth: // disconnect
				terminateBluetoothConnection();
				break;
			
			case R.id.bluetooth:
				setBluetooth(findViewById(R.id.bluetooth));
				break;
			
			case R.id.toggle:
				try {
					
					if (item.isChecked()) {
						if (setToggle(findViewById(R.id.toggle), false)) {
							item.setChecked(false);
						}
						
					}
					
					else {
						if (setToggle(findViewById(R.id.toggle), true)) {
							item.setChecked(true);
						}
						
					}
				} catch (Exception e) {
					Toast.makeText(this, "Toggle does not work!!!!!!", Toast.LENGTH_SHORT).show();
					Log.i("Toggle", " Error 1 " + e.getMessage());
				}
				
				break;
			case R.id.resetLine:
				resetLine();
				
				break;
			
			default:
				return super.onOptionsItemSelected(item);
				
		}
		return true;
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_action, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		hmiModule.setMode(Mode.PAUSE);
		terminateBluetoothConnection();
		
		Log.i("info", "onDestroy");
		if (mBtAdapter != null) {
			// release resources
			mBtAdapter.cancelDiscovery();
		}
		
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	/**
	 * handle pressing button with alert dialog if connected(non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (hmiModule != null && hmiModule.connected) {
			// creating new AlertDialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to terminate the connection?").setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// disconnect and return to initial screen
							terminateBluetoothConnection();
							restartActivity();
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	/**
	 * instantiating AndroidHmiPlt object and display received data(non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("info", "onActivityResult");
		switch (resultCode) {
		
		// user pressed back button on bluetooth activity, so return to initial
		// screen
			case Activity.RESULT_CANCELED:
				break;
			// user chose device
			case Activity.RESULT_OK:
				// connect to chosen NXT
				establishBluetoothConnection(data);
				// display received data from NXT
				if (hmiModule.connected) {
					// After establishing the connection make sure the start
					// mode of
					// the NXT is set to PAUSE
					hmiModule.setMode(Mode.PAUSE);
					/*
					 * //enable toggle button final ToggleButton toggleMode =
					 * (ToggleButton) findViewById(R.id.toggleMode);
					 * toggleMode.setEnabled(true);
					 * 
					 * //disable connect button final Button connectButton =
					 * (Button) findViewById(R.id.buttonSetupBluetooth);
					 * connectButton.setEnabled(false);
					 */
					
					displayDataNXT();
					break;
				} else {
					Toast.makeText(this, "Bluetooth connection failed!", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "Is the selected NXT really present & switched on?",
							Toast.LENGTH_LONG).show();
					break;
				}
		}
	}
	
	/**
	 * Connect to the chosen device
	 * 
	 * @param data
	 */
	private void establishBluetoothConnection(Intent data) {
		Log.i("info", "establish BluetoothConnection");
		// get instance of the chosen bluetooth device
		String address = data.getExtras().getString(BluetoothActivity.EXTRA_DEVICE_ADDRESS);
		btDevice = mBtAdapter.getRemoteDevice(address);
		
		// get name and address of the device
		String btDeviceAddress = btDevice.getAddress();
		String btDeviceName = btDevice.getName();
		
		// instantiate client modul
		hmiModule = new AndroidHmiPLT(btDeviceName, btDeviceAddress);
		
		// connect to the specified device
		hmiModule.connect();
		
		// wait till connection really is established and
		int i = 0;
		while (!hmiModule.isConnected() && i < 100000000 / 2) {
			i++;
		}
	}
	
	/**
	 * Display the current data of NXT
	 */
	private void displayDataNXT() {
		
		// map = new Map_canvas(this, 1);
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				while (orientation == true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// Log.i("info","Display DataNXT");
					runOnUiThread(new Runnable() {
						
						public void run() {
							float x_anzeige;
							float y_anzeige;
							
							try {
								if (hmiModule != null) {
									// display x value
									final TextView fld_xPos = (TextView) findViewById(R.id.textViewValueX);
									
									x_anzeige = hmiModule.getPosition().getX();
									
									current_posx = calc_posx(x_anzeige);
									
									try {
										if (current_posx != position_listx.get(position_listx.size())) {
											position_listx.add((int) current_posx);
										}
									} catch (Exception e) {
										position_listx.add((int) current_posx);
									}
									
									fld_xPos.setText(String.valueOf(x_anzeige + " cm"));
									
									// display y value
									final TextView fld_yPos = (TextView) findViewById(R.id.textViewValueY);
									y_anzeige = hmiModule.getPosition().getY();
									current_posy = calc_posy(y_anzeige);
									
									// display y value
									current_posy = calc_posy(hmiModule.getPosition().getY());
									try {
										if (current_posy != position_listy.get(position_listy.size())) {
											position_listy.add((int) current_posy);
										}
									} catch (Exception e) {
										position_listy.add((int) current_posy);
									}
									
									fld_yPos.setText(String.valueOf(y_anzeige + " cm"));
									
									last_posx = current_posx;
									last_posy = current_posy;
									
									// display angle value
									final TextView fld_angle = (TextView) findViewById(R.id.TextViewValueAngle);
									angle = hmiModule.getPosition().getAngle();
									fld_angle.setText(String.valueOf(angle + "°"));
									
									// display status of NXT
									final TextView fld_status = (TextView) findViewById(R.id.textViewValueStatus);
									fld_status.setText(String.valueOf(hmiModule.getCurrentStatus()));
									
									// display distance front
									final TextView fld_distance_front = (TextView) findViewById(R.id.textViewValueDistanceFront);
									fld_distance_front.setText(String.valueOf(hmiModule.getPosition()
											.getDistanceFront()) + " mm");
									// display distance back
									final TextView fld_distance_back = (TextView) findViewById(R.id.textViewValueDistanceBack);
									fld_distance_back.setText(String.valueOf(hmiModule.getPosition()
											.getDistanceBack()) + " mm");
									// display distance right
									final TextView fld_distance_front_side = (TextView) findViewById(R.id.textViewValueDistanceFrontSide);
									fld_distance_front_side.setText(String.valueOf(hmiModule.getPosition()
											.getDistanceFrontSide()) + " mm");
									// display distance left
									final TextView fld_distance_back_side = (TextView) findViewById(R.id.textViewValueDistanceBackSide);
									fld_distance_back_side.setText(String.valueOf(hmiModule.getPosition()
											.getDistanceBackSide()) + " mm");
									
									// TODO //display number of Parking Slots
									final TextView fld_no_parkingslots = (TextView) findViewById(R.id.textViewValueNoParkingSlots);
									save_no_park(hmiModule.getNoOfParkingSlots());
									
									fld_no_parkingslots.setText(String.valueOf(hmiModule
											.getNoOfParkingSlots()));
									
									// display bluetooth connection status
									final TextView fld_bluetooth = (TextView) findViewById(R.id.textViewValueBluetooth);
									// display connection status
									if (hmiModule.isConnected()) {
										fld_bluetooth.setText("connected");
										connection = true;
									} else {
										fld_bluetooth.setText("not connected");
										connection = false;
									}
									// restart activity when disconnecting
									if (hmiModule.getCurrentStatus() == CurrentStatus.EXIT) {
										terminateBluetoothConnection();
										restartActivity();
									}
									
								} else {
									final TextView fld_bluetooth = (TextView) findViewById(R.id.textViewValueBluetooth);
									fld_bluetooth.setText("no Connection");
								}
							} catch (Exception e) {
								// TODO
								try {
									// display x value
									final TextView fld_xPos = (TextView) findViewById(R.id.textViewValueX);
									fld_xPos.setText("0");
									// display y value
									final TextView fld_yPos = (TextView) findViewById(R.id.textViewValueY);
									fld_yPos.setText("0");
									// display angle value
									final TextView fld_angle = (TextView) findViewById(R.id.TextViewValueAngle);
									fld_angle.setText("x");
									// display status of NXT
									final TextView fld_status = (TextView) findViewById(R.id.textViewValueStatus);
									fld_status.setText("x");
									// display distance front
									final TextView fld_distance_front = (TextView) findViewById(R.id.textViewValueDistanceFront);
									fld_distance_front.setText("x");
									// display distance back
									final TextView fld_distance_back = (TextView) findViewById(R.id.textViewValueDistanceBack);
									fld_distance_back.setText("x");
									// display distance right
									final TextView fld_distance_front_side = (TextView) findViewById(R.id.textViewValueDistanceFrontSide);
									fld_distance_front_side.setText("x");
									// display distance left
									final TextView fld_distance_back_side = (TextView) findViewById(R.id.textViewValueDistanceBackSide);
									fld_distance_back_side.setText("x");
									
									final TextView fld_no_parkingslots = (TextView) findViewById(R.id.textViewValueNoParkingSlots);
									fld_no_parkingslots.setText("x");
									
									// display bluetooth connection status
									final TextView fld_bluetooth = (TextView) findViewById(R.id.textViewValueBluetooth);
									// display connection status
									
									fld_bluetooth.setText("not connected");
								} catch (Exception a) {
									Log.i("ERROR", " DISPLAYDATA NXT Exception a " + a.getMessage());
								}
								connection = false;
								// TODO
								create_map(true);
								current_posx = calc_posx(0);
								current_posy = calc_posy(0);
								Log.i("ERROR", " DISPLAYDATA NXT Exception e " + e.getMessage());
								
							}
							
							if (position_listx.size() > 3000) {
								position_listx.remove(0);
							}
							
							if (position_listy.size() > 3000) {
								position_listy.remove(0);
							}
							
						}
						
					});
				}
			}
		}, 200, 100);
		
	}
	
	/**
	 * Terminate the bluetooth connection to NXT
	 */
	private void terminateBluetoothConnection() {
		Log.i("info", "terminate Bluetooth");
		try {
			
			Toast.makeText(this, "Bluetooth connection was terminated!", Toast.LENGTH_LONG).show();
			hmiModule.setMode(Mode.DISCONNECT);
			hmiModule.disconnect();
			
			while (hmiModule.isConnected()) {
				// wait until disconnected
			}
		} catch (Exception e) {
			Log.i("terminate Bluetooth", "error no hmi connection");
		}
		hmiModule = null;
	}
	
	/**
	 * restart the activity
	 */
	private void restartActivity() {
		terminateBluetoothConnection();
		Intent restartIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(restartIntent);
		finish();
	}
	
	// --NEW--------------------------------------------------------------------------------
	/**
	 * switch from portrait to Landscape mode
	 */
	public void LandscapeButton(View view) {
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		// setContentView(R.layout.landscapemode);
	}
	
	/**
	 * switch from Landscape to portrait mode
	 */
	public void PortraitButton(View view) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// setContentView(R.layout.activity_main);
	}
	
	/**
	 * testing stuff
	 */
	public void TestButton(View view) {
		
		Toast.makeText(this, "UAUAUAUA6516UAUAUA", Toast.LENGTH_SHORT).show();
		
		position_listx.add(0);
		position_listy.add(0);
		
		position_listx.add(100);
		position_listy.add(100);
		
		position_listx.add(500);
		position_listy.add(150);
		
		position_listx.add(600);
		position_listy.add(100);
		
		position_listx.add(700);
		position_listy.add(245);
		
		position_listx.add(800);
		position_listy.add(296);
		
		position_listx.add(calc_posx(100));
		position_listy.add(calc_posy(100));
		
		position_listx.add(calc_posx(0));
		position_listy.add(calc_posy(0));
		
		position_listx.add(calc_posx(5));
		position_listy.add(calc_posy(-5));
		
		position_listx.add(calc_posx(-5));
		position_listy.add(calc_posy(-5));
		
		position_listx.add(calc_posx(-5));
		position_listy.add(calc_posy(5));
		
		position_listx.add(calc_posx(5));
		position_listy.add(calc_posy(5));
		
		position_listx.add(calc_posx(0));
		position_listy.add(calc_posy(0));
		
		position_listx.add(calc_posx(180));
		position_listy.add(calc_posy(0));
		
		position_listx.add(calc_posx(180));
		position_listy.add(calc_posy(60));
		
		Configuration configInfo = getResources().getConfiguration();
		
		if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			map = new Map_canvas(this, 0);
			setContentView(map);
			
		} else {
			// setContentView(new Canvas_reset(this));
			
		}
		
	}
	
	/**
	 * create bluetooth connection
	 */
	public void setBluetooth(View view) {
		
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		new Thread(new Runnable() {
			public void run() {
				// If the adapter is null, then Bluetooth is not supported
				if (mBtAdapter == null) {
					// Toast.makeText(this,
					// "Bluetooth is not available",Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
				try {
					// on click call the BluetoothActivity to choose a listed
					// device
					
					Intent serverIntent = new Intent(getApplicationContext(), BluetoothActivity.class);
					startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
					
				} catch (Exception e) {
					// Toast.makeText(this,
					// "Bluetooth does not work!!!!!!",Toast.LENGTH_SHORT).show();
					Log.i("Bluetooth", " Error " + e.getMessage());
				}
				
			}
		}).start();
		
	}
	
	/**
     * 
     */
	public boolean setToggle(View view, boolean check) {
		try {
			if (check) {
				// TODO toggle does not work - vielleicht auf der NXT seite
				// nicht initialisiert?
				// if toggle is checked change mode to SCOUT
				hmiModule.setMode(Mode.SCOUT);
				Log.e("Toggle", "Toggled to Scout");
				Toast.makeText(this, "Toggle set to scout", Toast.LENGTH_SHORT).show();
			} else {
				
				// otherwise change mode to PAUSE
				hmiModule.setMode(Mode.PAUSE);
				Log.e("Toggle", "Toggled to Pause");
				Toast.makeText(this, "Toggle set to Pause", Toast.LENGTH_SHORT).show();
			}
			
			return true;
			
		} catch (Exception e) {
			Toast.makeText(this, "Toggle does not work!!!!!!", Toast.LENGTH_SHORT).show();
			Log.i("Toggle ", "Error2 " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * create a fragment for landscape/ portrait mode -> switch on configuration
	 * change
	 */
	public void setFragment() {
		Log.i("info", "setFrag");
		
		try {
			
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			Configuration configInfo = getResources().getConfiguration();
			Log.i("setFrag", "2");
			if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				orientation = false;
				Log.i("setFrag", "orientation = false");
				getData();
				FragmentLandscape fragmentLandscape = new FragmentLandscape();
				fragmentTransaction.replace(android.R.id.content, fragmentLandscape);
				map = new Map_canvas(this, 0);
				setContentView(map);
				
			} else {
				orientation = true;
				Log.i("setFrag", "orientation = true");
				displayDataNXT();
				FragmentPortrait fragmentPortrait = new FragmentPortrait();
				fragmentTransaction.replace(android.R.id.content, fragmentPortrait);
				
			}
			
			fragmentTransaction.commit();
			
		} catch (Exception e) {
			Log.i("setFrag ", "error" + e.toString());
		}
		Log.i("setFrag", "last");
	}
	
	/**
	 * reset line on map
	 */
	public void resetLine() {
		Configuration configInfo = getResources().getConfiguration();
		current_posx = calc_posx(0);
		current_posy = calc_posy(0);
		
		position_listx.clear();
		position_listy.clear();
		
		if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			map = new Map_canvas(this, 0);
			setContentView(map);
		} else {
			
		}
		
	}
	
	/**
	 * return x or y position true ->x false ->y
	 */
	public int current_pos(boolean x) { // true = x, false = y
		// Log.i("info","current_pos");
		if (x == true)
			return (int) current_posx;
		else
			return (int) current_posy;
	}
	
	/**
	 * return x or y position list true ->x false ->y
	 */
	public ArrayList<Integer> get_pos_list(boolean list_x) {
		// Log.i("info","get_pos_list");
		if (list_x == true)
			return position_listx;
		else
			return position_listy;
		
	}
	
	/**
	 * get position, angle ... in landscape mode for canvas
	 */
	private void getData() {
		
		// map = new Map_canvas(this, 1);
		
		try {
			
			Log.i("info", "getData1");
			
			new Timer().schedule(new TimerTask() {
				
				// public Map_canvas map;
				
				@Override
				public void run() {
					
					while (getOrientation() == false) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						runOnUiThread(new Runnable() {
							public void run() {
								
								if (hmiModule != null) {
									try {
										
										// display x value
										current_posx = calc_posx(hmiModule.getPosition().getX());
										try {
											if (current_posx != position_listx.get(position_listx.size())) {
												position_listx.add((int) current_posx);
											}
										} catch (Exception e) {
											position_listx.add((int) current_posx);
										}
										
										// display y value
										current_posy = calc_posy(hmiModule.getPosition().getY());
										try {
											if (current_posy != position_listy.get(position_listy.size())) {
												position_listy.add((int) current_posy);
											}
										} catch (Exception e) {
											position_listy.add((int) current_posy);
										}
										
										// display angle value
										angle = hmiModule.getPosition().getAngle();
										String.valueOf(angle);
										// display status of NXT
										
										String.valueOf(hmiModule.getCurrentStatus());
										// display distance front
										
										String.valueOf(hmiModule.getPosition().getDistanceFront());
										// display distance back
										
										String.valueOf(hmiModule.getPosition().getDistanceBack());
										// display distance right
										
										String.valueOf(hmiModule.getPosition().getDistanceFrontSide());
										// display distance left
										
										String.valueOf(hmiModule.getPosition().getDistanceBackSide());
										
										// display number of Parking Slots
										save_no_park(hmiModule.getNoOfParkingSlots());
										String.valueOf(hmiModule.getNoOfParkingSlots());
										
										// display connection status
										if (hmiModule.isConnected()) {
											connection = true;
											
										} else {
											connection = false;
										}
										// restart activity when disconnecting
										if (hmiModule.getCurrentStatus() == CurrentStatus.EXIT) {
											terminateBluetoothConnection();
											restartActivity();
										}
									} catch (Exception e) {
										current_posx = calc_posx(0);
										current_posy = calc_posy(0);
										
										Log.i("info", "error getData cant show data");
									}
									try {
										create_map(false);
									} catch (Exception e) {
										Log.i("getData", "Error create canvas");
									}
									Log.i("info", "getData end");
									
								}
								if (position_listx.size() > 3000) {
									position_listx.remove(0);
								}
								
								if (position_listy.size() > 3000) {
									position_listy.remove(0);
								}
							}
							
						});
					}
				}
				
			}, 200, 100);
			
		} catch (Exception e) {
			
			Log.i("getData", "Error " + e.toString());
			
		}
	}
	
	/**
	 * create new Map_canvas orientation = false -> landscape
	 */
	public void create_map(boolean ori) {
		if (orientation == false && ori == false) { // landscape
			map = new Map_canvas(this, 0);
			setContentView(map);
		}
		
	}
	
	/**
	 * return orientation orientation = false -> landscape
	 */
	public static boolean getOrientation() {
		return orientation;
	}
	
	/**
	 * save current position in position_list boolean x = true -> x x= false ->
	 * y
	 */
	public void save_pos(float pos, boolean x) {
		
		if (x == true) {
			if (pos != position_listx.get(position_listx.size())) {
				position_listx.add((int) pos);
			}
		}
		
		else if (x != true) {
			if (pos != position_listy.get(position_listy.size())) {
				position_listy.add((int) pos);
			}
		}
		
	}
	
	/**
	 * calculate cm -> px 1cm = 4 pc in y
	 */
	public int calc_posx(float x_koort) {
		int x;
		x = (int) Math.round((x_koort * 4.1795918367) + (20 * (4.1795918367)));
		
		return x;
	}
	
	/**
	 * calculate cm -> px 1cm = 4 pc in y
	 */
	public int calc_posy(float y_koort) {
		int y;
		y = (int) Math.round(-y_koort * 4 + 293);
		
		return y;
	}
	
	public boolean getConnection() {
		return connection;
	}
	
	public void save_no_park(int number) {
		no_slots = number;
	}
	
	public int get_no_park() {
		return no_slots;
	}
	
	public ParkingSlot get_slot(int slot) {
		// hmiModule.getFrontBoundaryPosition();
		
		return hmiModule.getParkingSlot(slot);
	}
	
	public float get_Angle() {
		return angle;
	}
	
}
