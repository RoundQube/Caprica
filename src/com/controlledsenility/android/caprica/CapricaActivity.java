package com.controlledsenility.android.caprica;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import de.yadrone.base.IARDrone;

public class CapricaActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	private static IARDrone drone = null;
	private static Button land;
	private static Button takeOff;
	private static TextView latVal;
	private static TextView lngVal;
	private static Switch followMe;
	private LocationClient mLocationClient;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	// Define an object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new CapricaFragment()).commit();
		}

		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mLocationClient = new LocationClient(this, this, this);

		CapricaApplication app = (CapricaApplication) getApplication();
		drone = app.getARDrone();

		// always keep the screen on while this app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		try {
			drone.start();
		} catch (Exception exc) {
			exc.printStackTrace();

			if (drone != null)
				drone.stop();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.actionSettings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A fragment containing a simple view.
	 */
	public static class CapricaFragment extends Fragment {

		public CapricaFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			latVal = (TextView) rootView.findViewById(R.id.latVal);
			lngVal = (TextView) rootView.findViewById(R.id.lngVal);
			followMe = (Switch) rootView.findViewById(R.id.followMe);
			land = (Button) rootView.findViewById(R.id.land);
			takeOff = (Button) rootView.findViewById(R.id.takeOff);

			followMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (isChecked) {
						followMe(true);
					} else {
						followMe(false);
					}

				}

			});

			land.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					drone.landing();

				}
			});

			takeOff.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					drone.takeOff();

				}
			});

			return rootView;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {

		super.onStart();
		mLocationClient.connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {

		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onConnected(Bundle arg0) {

		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);

	}

	@Override
	public void onDisconnected() {

		Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		mLocationClient.removeLocationUpdates(this);

	}

	@Override
	public void onLocationChanged(Location location) {

		// Report to the UI that the location was updated
		latVal.setText(Double.toString(location.getLatitude()));
		lngVal.setText(Double.toString(location.getLongitude()));

	}

	public static void followMe(boolean followMe) {

		// TODO: add logic to move drone based on GPS coord changes

	}
}
