/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;

public class GPS extends Brujula {

    private final int REQUEST_PERMISSION_GPS = 34;

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private ArrayList<CustomLocationListener> customLocationListener = null;
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null && customLocationListener != null) {
                for (CustomLocationListener custom : customLocationListener) {
                    custom.onLocation(new LatLon(location.getLatitude(), location.getLongitude(), location.getAltitude()));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isPermisoDeUbicacionConcedido =
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (isPermisoDeUbicacionConcedido) {

            inicializar();

        } else {
            //Solicito el permiso
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS);
        }



        /*

        //Codigo para simular la ubicacion

        new Thread(new Runnable() {

            double latitude = 42.207764;
            double longitude = -8.758935;

            @Override
            public void run() {

                while(true) {

                    try {
                        Thread.sleep(2000);
                    } catch (Throwable ignored) {
                    }

                    latitude = latitude+0.0001;
                    longitude = longitude+0.0001;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if (customLocationListener != null) {
                                for (CustomLocationListener c : customLocationListener) {
                                    c.onLocation(new LatLon(latitude, longitude,0));
                                }
                            }

                        }
                    });

                }

            }
        }).start();*/

    }

    private void inicializar() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        requestNewLocationData();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_GPS) {
            boolean isPermisoDeUbicacionConcedido =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED;

            if (isPermisoDeUbicacionConcedido) {
                inicializar();
            }
        }

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (isLocationEnabled()) {

            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    try {
                        Location location = task.getResult();
                        if (location != null) {
                            if (customLocationListener != null) {
                                for (CustomLocationListener custom : customLocationListener) {
                                    custom.onLocation(new LatLon(location.getLatitude(), location.getLongitude(), location.getAltitude()));
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        }

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500)
                .setMinUpdateDistanceMeters(1)
                .setMaxUpdateAgeMillis(3000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setWaitForAccurateLocation(false)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (customLocationListener != null) {
            customLocationListener.clear();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void setLocationListener(CustomLocationListener customLocationListener) {
        if (this.customLocationListener == null) {
            this.customLocationListener = new ArrayList<CustomLocationListener>();
        }
        this.customLocationListener.add(customLocationListener);
    }

    public interface CustomLocationListener {
        void onLocation(LatLon latLon);
    }

}
