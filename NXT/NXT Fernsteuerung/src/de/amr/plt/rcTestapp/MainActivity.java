package de.amr.plt.rcTestapp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

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
import de.amr.plt.rcTestapp.Canvas.Car_canvas;
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

	public static float current_posx = 1000;
	public static float current_posy = 300;
	public static ArrayList<Integer> position_listx = new ArrayList<Integer>();
	public static ArrayList<Integer> position_listy = new ArrayList<Integer>();

	static public Map_canvas map;

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
		// Handle presses on the action bar items
		switch (item.getItemId()) {
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
					if (setToggle(findViewById(R.id.toggle), true)) {
						item.setChecked(false);
						Toast.makeText(this, "Toggle set to false",
								Toast.LENGTH_SHORT).show();
					}

				}

				else {
					if (!setToggle(findViewById(R.id.toggle), false)) {
						item.setChecked(true);
						Toast.makeText(this, "Toggle set to true",
								Toast.LENGTH_SHORT).show();
					}

				}
			} catch (Exception e) {
				Toast.makeText(this, "Toggle does not work!!!!!!",
						Toast.LENGTH_SHORT).show();
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

		// return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
			builder.setMessage(
					"Are you sure you want to terminate the connection?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// disconnect and return to initial screen
									terminateBluetoothConnection();
									restartActivity();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
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
				// After establishing the connection make sure the start mode of
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
				Toast.makeText(this, "Bluetooth connection failed!",
						Toast.LENGTH_SHORT).show();
				Toast.makeText(this,
						"Is the selected NXT really present & switched on?",
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
		String address = data.getExtras().getString(
				BluetoothActivity.EXTRA_DEVICE_ADDRESS);
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

		map = new Map_canvas(this, 1);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				while (orientation == true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Log.i("info","Display DataNXT");
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								if (hmiModule != null) {
									// display x value
									final TextView fld_xPos = (TextView) findViewById(R.id.textViewValueX);
									current_posx = hmiModule.getPosition()
											.getX();
									
									
									save_pos(current_posx, true);
									
									
									fld_xPos.setText(String
											.valueOf(current_posx + " cm"));
									// display y value
									final TextView fld_yPos = (TextView) findViewById(R.id.textViewValueY);
									current_posy = hmiModule.getPosition()
											.getY();
									save_pos(current_posy, true);
									fld_yPos.setText(String
											.valueOf(current_posy + " cm"));
									// display angle value
									final TextView fld_angle = (TextView) findViewById(R.id.TextViewValueAngle);
									fld_angle.setText(String.valueOf(hmiModule
											.getPosition().getAngle() + "°"));
									// display status of NXT
									final TextView fld_status = (TextView) findViewById(R.id.textViewValueStatus);
									fld_status.setText(String.valueOf(hmiModule
											.getCurrentStatus()));
									// display distance front
									final TextView fld_distance_front = (TextView) findViewById(R.id.textViewValueDistanceFront);
									fld_distance_front.setText(String
											.valueOf(hmiModule.getPosition()
													.getDistanceFront())
											+ " mm");
									// display distance back
									final TextView fld_distance_back = (TextView) findViewById(R.id.textViewValueDistanceBack);
									fld_distance_back.setText(String
											.valueOf(hmiModule.getPosition()
													.getDistanceBack())
											+ " mm");
									// display distance right
									final TextView fld_distance_front_side = (TextView) findViewById(R.id.textViewValueDistanceFrontSide);
									fld_distance_front_side.setText(String
											.valueOf(hmiModule.getPosition()
													.getDistanceFrontSide())
											+ " mm");
									// display distance left
									final TextView fld_distance_back_side = (TextView) findViewById(R.id.textViewValueDistanceBackSide);
									fld_distance_back_side.setText(String
											.valueOf(hmiModule.getPosition()
													.getDistanceBackSide())
											+ " mm");
									// display bluetooth connection status
									final TextView fld_bluetooth = (TextView) findViewById(R.id.textViewValueBluetooth);
									// display connection status
									if (hmiModule.isConnected()) {
										fld_bluetooth.setText("connected");
									} else {
										fld_bluetooth.setText("not connected");
									}
									// restart activity when disconnecting
									if (hmiModule.getCurrentStatus() == CurrentStatus.EXIT) {
										terminateBluetoothConnection();
										restartActivity();
									}

									// TODO create_map(true);

								}
							} catch (Exception e) {
								// Log.i("info","error getData cant show data");
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

			Toast.makeText(this, "Bluetooth connection was terminated!",
					Toast.LENGTH_LONG).show();
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
		Intent restartIntent = new Intent(getApplicationContext(),
				MainActivity.class);
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
		// Toast.makeText(this, "UAUAUAUA6516UAUAUA",
		// Toast.LENGTH_SHORT).show();

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		try {
			// on click call the BluetoothActivity to choose a listed device

			Intent serverIntent = new Intent(getApplicationContext(),
					BluetoothActivity.class);
			startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);

		} catch (Exception e) {
			Toast.makeText(this, "Bluetooth does not work!!!!!!",
					Toast.LENGTH_SHORT).show();
			Log.i("Bluetooth", " Error " + e.getMessage());
		}

	}

	/**
     * 
     */
	public boolean setToggle(View view, boolean check) {
		try {
			if (check) {
				// if toggle is checked change mode to SCOUT
				hmiModule.setMode(Mode.SCOUT);
				Log.e("Toggle", "Toggled to Scout");
			} else {

				// otherwise change mode to PAUSE
				hmiModule.setMode(Mode.PAUSE);
				Log.e("Toggle", "Toggled to Pause");
			}

			return true;

		} catch (Exception e) {
			Toast.makeText(this, "Toggle does not work!!!!!!",
					Toast.LENGTH_SHORT).show();
			Log.i("Toggle ", "Error2 " + e.getMessage());
			return false;
		}
	}

	/**
	 * set bluetooth and toggle (old)
	 */
	public void setBluetoothOld() {
		setContentView(R.layout.activity_main);
		// get the BT-Adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		final Button connectButton = (Button) findViewById(R.id.buttonSetupBluetooth);
		// on click call the BluetoothActivity to choose a listed device
		connectButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Intent serverIntent = new Intent(getApplicationContext(),
							BluetoothActivity.class);
					startActivityForResult(serverIntent,
							REQUEST_SETUP_BT_CONNECTION);
					Log.i("setonclicklistener", "bluetooth button aktiviert");
				} catch (Exception e) {
					// Toast.makeText(this, "BLUETOOTH ERRORORORORORORROROR",
					// Toast.LENGTH_SHORT).show();
					Log.e("Bluetooth", "error Connect ");
				}

			}
		});

		// toggle button allows user to set mode of the NXT device
		final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleMode);
		// disable button initially
		// toggleButton.setEnabled(false);
		// on click change mode
		toggleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				boolean checked = ((ToggleButton) v).isChecked();
				try {

					if (checked) {
						// if toggle is checked change mode to SCOUT
						hmiModule.setMode(Mode.SCOUT);
						Log.e("Toggle", "Toggled to Scout");
					} else {
						// otherwise change mode to PAUSE
						hmiModule.setMode(Mode.PAUSE);
						Log.e("Toggle", "Toggled to Pause");
					}

				} catch (Exception e) {
					// Toast.makeText(this, "Toggle does not work!!!!!!",
					// Toast.LENGTH_SHORT).show();
					Log.i("Toggle ", "Error2 " + e.getMessage());
				}

			}
		});

	}

	/**
	 * create a fragment for landscape/ portrait mode -> switch on configuration
	 * change
	 */
	public void setFragment() {
		Log.i("info", "setFrag");

		try {

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			Configuration configInfo = getResources().getConfiguration();
			Log.i("setFrag", "2");
			if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				orientation = false;
				Log.i("setFrag", "orientation = false");
				getData();
				FragmentLandscape fragmentLandscape = new FragmentLandscape();
				fragmentTransaction.replace(android.R.id.content,
						fragmentLandscape);
				map = new Map_canvas(this, 0);
				setContentView(map);

			} else {
				orientation = true;
				Log.i("setFrag", "orientation = true");
				displayDataNXT();
				FragmentPortrait fragmentPortrait = new FragmentPortrait();
				fragmentTransaction.replace(android.R.id.content,
						fragmentPortrait);
				// map = new Map_canvas(this, 1);
				// TODO setContentView(map);

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
		current_posx = 0;
		current_posy = 0;

		position_listx.clear();
		position_listy.clear();

		if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			map = new Map_canvas(this, 0);
			setContentView(map);
		} else {

			// TODO 1 fehlerhaftes verhalten
			// map = new Map_canvas(this, 1);
			// TODO setContentView(map);

			// setContentView(new Car_canvas(this));

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

		map = new Map_canvas(this, 1);

		try {

			Log.i("info", "getData1");

			new Timer().schedule(new TimerTask() {

				// public Map_canvas map;

				@Override
				public void run() {
					Log.i("info", "getData 2");
					while (orientation == false) {
						Log.i("info", "getData 3");
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// Log.i("info","getData2");
						runOnUiThread(new Runnable() {
							public void run() {
								if (hmiModule != null) {
									try {

										// display x value
										final TextView fld_xPos = (TextView) findViewById(R.id.textViewValueX);
										current_posx = hmiModule.getPosition()
												.getX();

										save_pos(current_posx, true);

										fld_xPos.setText(String
												.valueOf(current_posx + " cm"));
										// display y value
										final TextView fld_yPos = (TextView) findViewById(R.id.textViewValueY);
										current_posy = hmiModule.getPosition()
												.getY();

										save_pos(current_posy, false);

										fld_yPos.setText(String
												.valueOf(current_posy + " cm"));
										// display angle value
										final TextView fld_angle = (TextView) findViewById(R.id.TextViewValueAngle);
										String.valueOf(hmiModule.getPosition()
												.getAngle());
										// display status of NXT
										final TextView fld_status = (TextView) findViewById(R.id.textViewValueStatus);
										String.valueOf(hmiModule
												.getCurrentStatus());
										// display distance front
										final TextView fld_distance_front = (TextView) findViewById(R.id.textViewValueDistanceFront);
										String.valueOf(hmiModule.getPosition()
												.getDistanceFront());
										// display distance back
										final TextView fld_distance_back = (TextView) findViewById(R.id.textViewValueDistanceBack);
										String.valueOf(hmiModule.getPosition()
												.getDistanceBack());
										// display distance right
										final TextView fld_distance_front_side = (TextView) findViewById(R.id.textViewValueDistanceFrontSide);
										String.valueOf(hmiModule.getPosition()
												.getDistanceFrontSide());
										// display distance left
										final TextView fld_distance_back_side = (TextView) findViewById(R.id.textViewValueDistanceBackSide);
										String.valueOf(hmiModule.getPosition()
												.getDistanceBackSide());
										// display bluetooth connection status
										final TextView fld_bluetooth = (TextView) findViewById(R.id.textViewValueBluetooth);
										// display connection status
										if (hmiModule.isConnected()) {
											fld_bluetooth.setText("connected");
										} else {
											fld_bluetooth
													.setText("not connected");
										}
										// restart activity when disconnecting
										if (hmiModule.getCurrentStatus() == CurrentStatus.EXIT) {
											terminateBluetoothConnection();
											restartActivity();
										}
									} catch (Exception e) {
										// Log.i("info","error getData cant show data");
									}
									try {
										create_map(false);
									} catch (Exception e) {
										Log.i("getData", "Error create canvas");
									}
									Log.i("info", "getData end");

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

	public void create_map(boolean ori) {
		if (orientation == false && ori == false) { // landscape
			map = new Map_canvas(this, 0);
			setContentView(map);
		} else {
			// TODO map = new Map_canvas(this, 1);
		}

	}

	public void error_ausgabe(String tag, String text) {
		Log.i("tag", "Error " + text);
	}

	public static boolean getOrientation() {
		return orientation;
	}

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

}
