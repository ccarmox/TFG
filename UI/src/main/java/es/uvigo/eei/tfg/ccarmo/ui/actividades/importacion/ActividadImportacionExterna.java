/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.TipoDato;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.Ruta;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.exportacion.ExportarGeoJSON;

public class ActividadImportacionExterna extends ActividadImportacionBase {

    public static void copiarArchivoEnCache(Context context, String nombre, Uri uri) throws IOException {

        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);

        if (inputStream != null) {

            File outputFile = new File(context.getCacheDir(), nombre);

            android.util.Log.v("Contenido", "=> " + outputFile.getAbsolutePath());

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

        } else {
            throw new IOException("fallo al leer el archivo");
        }
    }

    public static void copiarRutaEnCache(@NonNull Context context, @NonNull String nombre, @NonNull String camino) throws IOException {

        String json = ExportarGeoJSON.getGeoJSONGenerado(camino, context);


        File outputFile = new File(context.getCacheDir(), nombre);

        FileOutputStream writer = new FileOutputStream(outputFile);
        writer.write(json.getBytes());
        writer.close();

    }

    public static void copiarRutaEnCache(@NonNull Context context, @NonNull String nombre, @NonNull Ruta ruta) throws IOException {

        String json = ExportarGeoJSON.getGeoJSONGenerado(ruta.getNombreDB(), context);


        File outputFile = new File(context.getCacheDir(), nombre);

        FileOutputStream writer = new FileOutputStream(outputFile);
        writer.write(json.getBytes());
        writer.close();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_inicio);

        boolean error = true;

        try {


            Intent intent = getIntent();
            if (intent != null && intent.getData() != null) {
                Uri uri = intent.getData() == null ? null : Uri.parse(intent.getData().toString());
                if (uri != null) {
                    String nombre = ActividadMenuImportacion.getNombreDeArchivo(uri, getApplicationContext());
                    if (Fuente.getTipoDato(nombre) != TipoDato.DESCONOCIDO) {

                        copiarArchivoEnCache(getApplicationContext(), nombre, uri);

                        //Si es un formato valido comienzo la importacion
                        Fuente fuente = new Fuente("uri://" + nombre, nombre);
                        ArrayList<Fuente> fuentes = new ArrayList<>();
                        fuentes.add(fuente);

                        //Lanzo la importacion
                        ActividadImportacionSeleccionarTipo.abrir(fuentes, ActividadImportacionExterna.this);

                        finish();

                        error = false;

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (error) {

            //Se abre el dialogo de error
            new MaterialAlertDialogBuilder(ActividadImportacionExterna.this)
                    .setTitle(R.string.importacion_archivo_no_valido_titulo)
                    .setMessage(R.string.importacion_archivo_no_valido_descripcion)
                    .setCancelable(true)
                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActividadMenuImportacion.abrir(ActividadImportacionExterna.this);
                            finish();
                        }
                    })
                    .create().show();

        }

    }

}