package com.example.federico.objects;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.CheckBox;

/**
 * Created by federico on 08/10/2015.
 */
public class GPSTraker extends Service implements LocationListener{

    private final Context mContext;

    //flag for GPS status
    boolean isGPSEnabled = false;

    //flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    //minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    //minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    public static final long MAX_DISTANCE_FROM_LOCATION = 25;

    //Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTraker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    //asi como esta la función utiliza el GPS o wifi, dependiendo quien esté disponible. Si ninguno esta
    //disponible, no hay problema, listo todos los lugares encontrados.
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                //NO NETWORK PROVIDER IS ENABLED
                Log.d("Nothing enabled", "Nothing enabled");
                return null;
            } else {
                this.canGetLocation = true;
                //first get location from network provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if(locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location !=  null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                //if GPS enabled get lat/long using GPS services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //ES LA ÚNICA MANERA DE ACTIVAR EL GPS YA Q EN LAS VERSIONES MÁS NUEVAS DE ANDROID NO PERMITEN
    // QUE SEA ACTIVADO POR APLICACIONES DE TERCEROS
    public void showSettingsAlert(final CheckBox checkBoxGps){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
                LocationManager mlocManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);;
                boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                System.out.println("enabled: " + enabled);
                checkBoxGps.setChecked(!enabled);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                checkBoxGps.setChecked(!checkBoxGps.isChecked());
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
}
