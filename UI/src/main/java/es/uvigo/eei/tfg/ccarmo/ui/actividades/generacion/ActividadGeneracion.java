/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.generacion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.Ruta;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.exportacion.classes.Exportable;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils.GPS;
import es.uvigo.eei.tfg.ccarmo.ui.views.mapa.MapaView;

public class ActividadGeneracion extends GPS implements Exportable {


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

    private SQLite dbRuta;

    private int posicion = -1;

    private Vertice ultimoNudo = null;
    private Ruta ruta;

    private double distanciaRecorrida = 0;

    public static void abrir(@NonNull Activity activity) {
        Intent intent = new Intent(activity, ActividadGeneracion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_mapa);

        dbRuta = SQLite.getBaseDeDatos(SQLite.CACHE_RUTA, ActividadGeneracion.this);
        dbRuta.borrar();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Se abre el dialogo de error
                    new MaterialAlertDialogBuilder(ActividadGeneracion.this)
                            .setTitle(R.string.salir_generacion_puntos_titulo)
                            .setMessage(R.string.salir_generacion_puntos_descripcion)
                            .setCancelable(true)
                            .setIcon(R.drawable.ic_alerta)
                            .setPositiveButton(R.string.salir, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    //Lanzo la actividad de menu
                                    ActividadMenu.abrir(ActividadGeneracion.this);

                                    //Termino esta actividad
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();


                }
            }
        });


        mapa2 = findViewById(R.id.vista_mapa);

        mapa2.setDatos(null);


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setLocationListener(new CustomLocationListener() {
                            @Override
                            public void onLocation(LatLon latLon) {

                                mapa2.actualizarPosicionUsuario(latLon);


                                //Obtengo la fecha actual
                                Date date = new Date();

                                //Define el formato deseado para la fecha
                                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                                //Formatea la fecha actual segun el formato especificado
                                String fecha = formato.format(date);

                                posicion++;

                                Vertice b = new Vertice(fecha, 6, latLon.getLatitud(), latLon.getLongitud(), latLon.getAltitud(), TipoVia.VIA_PEATONAL, Vertice.Unible.UNIBLE_CON_TODO, posicion);
                                dbRuta.addVertices(b);


                                if (ultimoNudo != null) {
                                    distanciaRecorrida = distanciaRecorrida + ultimoNudo.getDistanciaFisicaA(b);
                                }
                                ultimoNudo = b;


                                if (ruta == null) {
                                    ruta = new Ruta(SQLite.CACHE_RUTA, distanciaRecorrida, null);
                                    mapa2.setRuta(ruta);
                                } else {
                                    ruta.setDistancia(distanciaRecorrida);
                                }

                                mapa2.refrescarRuta();

                            }
                        });


                        setBrujulaListener(new CustomBrujulaListener() {
                            @Override
                            public void onData(float azimuth) {
                                mapa2.actualizarBrujula(azimuth);
                            }
                        });


                    }
                });

            }
        }).start();

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