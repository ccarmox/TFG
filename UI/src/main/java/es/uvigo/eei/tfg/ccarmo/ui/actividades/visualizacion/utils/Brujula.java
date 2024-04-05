/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.ui.actividades.utils.ActividadBase;

public class Brujula extends ActividadBase implements SensorEventListener {

    private final float[] mGravity = new float[3];
    private final float[] mMagnetic = new float[3];
    ArrayList<CustomBrujulaListener> customBrujulaListener = null;
    private Sensor acelerometro;
    private Sensor magnetometro;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    protected void onStart() {
        super.onStart();
        start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    public void start() {
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setBrujulaListener(CustomBrujulaListener customBrujulaListener) {
        if (this.customBrujulaListener == null) {
            this.customBrujulaListener = new ArrayList<>();
        }
        this.customBrujulaListener.add(customBrujulaListener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        final float alpha = 0.8f;

        synchronized (this) {

            //Magnetometro
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mMagnetic[0] = alpha * mMagnetic[0] + (1 - alpha) * sensorEvent.values[0];
                mMagnetic[1] = alpha * mMagnetic[1] + (1 - alpha) * sensorEvent.values[1];
                mMagnetic[2] = alpha * mMagnetic[2] + (1 - alpha) * sensorEvent.values[2];
            }

            //Acelerometro
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * sensorEvent.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * sensorEvent.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * sensorEvent.values[2];
            }

            float[] R = new float[9];
            float[] I = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mMagnetic)) {
                float[] orientation = new float[3];

                //Sacar el azimuth
                SensorManager.getOrientation(R, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);

                if (customBrujulaListener != null) {
                    for (CustomBrujulaListener custom : customBrujulaListener) {
                        custom.onData(-azimuth);
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface CustomBrujulaListener {
        void onData(float azimuth);
    }
}
