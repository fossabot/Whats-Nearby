package com.teester.whatsnearby.model.data.location;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;
import com.teester.whatsnearby.main.MainActivity;
import com.teester.whatsnearby.model.QueryOverpass;

public class LocationService extends Service implements LocationServiceContract.Service {

	private static final String TAG = LocationService.class.getSimpleName();

	private static final int PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

	private LocationServiceContract.Presenter locationPresenter;
	LocationListener listener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			locationPresenter.processLocation(location);
		}
	};
	private LostApiClient client;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		locationPresenter = new LocationPresenter(this);
		locationPresenter.init();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override public void onDestroy() {
		super.onDestroy();
		LocationServices.FusedLocationApi.removeLocationUpdates(client, listener);
		client.disconnect();
	}

	@Override
	public void setPresenter(LocationServiceContract.Presenter presenter) {
		locationPresenter = presenter;
	}

	@Override
	public void cancelNotifications() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	@Override
	public void createLostClient(final int interval) {
		client = new LostApiClient.Builder(this).addConnectionCallbacks(new LostApiClient.ConnectionCallbacks() {
			@Override
			public void onConnected() {
				LocationRequest request = LocationRequest.create();
				request.setPriority(PRIORITY);
				request.setInterval(interval);

				checkLocationPermission();

				LocationServices.FusedLocationApi.requestLocationUpdates(client, request, listener);
			}

			@Override
			public void onConnectionSuspended() {

			}
		}).build();
		client.connect();
	}

	@Override
	public void performOverpassQuery(Location location) {
		new QueryOverpass(location, getApplicationContext());
	}

	public void checkLocationPermission() {
		if (ActivityCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			startActivity(intent);

			return;
		}
	}
}
