/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.exportacion.classes.Exportable;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils.GPS;
import es.uvigo.eei.tfg.ccarmo.ui.views.mapa.MapaView;

public class ActividadMapa extends GPS implements Exportable {

    private final static String EXTRA_DB = "DB";
    private final static String EXTRA_LATITUD = "Latitud";
    private final static String EXTRA_LONGITUD = "Longitud";
    private String contenido;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {


                        try {
                            Intent resultData = result.getData();

                            if (resultData != null && resultData.getData() != null) {

                                Uri uri = Uri.parse(resultData.getData().toString());
                                if (uri != null) {

                                    OutputStream outputStream = getContentResolver().openOutputStream(resultData.getData());

                                    InputStream in = new ByteArrayInputStream(contenido.getBytes());

                                    // Copy the bits from instream to outstream
                                    byte[] buf = new byte[1024];
                                    int len;

                                    while ((len = in.read(buf)) > 0) {
                                        outputStream.write(buf, 0, len);
                                    }

                                    in.close();
                                    outputStream.close();

                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    private MapaView mapa2;

    public static void abrir(@NonNull String db, @NonNull Activity activity) {
        abrir(db, 1000, 1000, activity);
    }

    public static void abrir(@NonNull String db, double latitud, double longitud, @NonNull Activity activity) {
        Intent intent = new Intent(activity, ActividadMapa.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_DB, db);
        intent.putExtra(EXTRA_LATITUD, latitud);
        intent.putExtra(EXTRA_LONGITUD, longitud);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_mapa);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenu.abrir(ActividadMapa.this);

                    //Termino esta actividad
                    finish();
                }
            }
        });


        mapa2 = findViewById(R.id.vista_mapa);

        double latitud = getExtras2().getDouble(EXTRA_LATITUD, 1000);
        double longitud = getExtras2().getDouble(EXTRA_LONGITUD, 1000);

        mapa2.setDatos(getExtras2().getString(EXTRA_DB), latitud, longitud);


        setLocationListener(new CustomLocationListener() {
            @Override
            public void onLocation(LatLon latLon) {
                android.util.Log.v("Localizacion", "=>" + latLon.toString());
                mapa2.actualizarPosicionUsuario(latLon);
            }
        });


        setBrujulaListener(new CustomBrujulaListener() {
            @Override
            public void onData(float azimuth) {
                mapa2.actualizarBrujula(azimuth);
            }
        });


    }

    @Override
    public void exportarContenido(@NonNull String nombreArchivo, @NonNull String contenido) {

        this.contenido = contenido;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, nombreArchivo);

        activityResultLauncher.launch(intent);

    }
}