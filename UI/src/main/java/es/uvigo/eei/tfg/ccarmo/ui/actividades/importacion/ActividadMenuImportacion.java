/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion;

import static es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion.ActividadImportacionExterna.copiarArchivoEnCache;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.TipoDato;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;

public class ActividadMenuImportacion extends ActividadImportacionBase {


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
                                    String nombre = ActividadMenuImportacion.getNombreDeArchivo(uri, getApplicationContext());
                                    if (Fuente.getTipoDato(nombre) != TipoDato.DESCONOCIDO) {

                                        copiarArchivoEnCache(getApplicationContext(), nombre, uri);

                                        //Si es un formato valido comienzo la importacion
                                        Fuente fuente = new Fuente("uri://" + nombre, nombre);
                                        ArrayList<Fuente> fuentes = new ArrayList<>();
                                        fuentes.add(fuente);

                                        //Lanzo la importacion
                                        ActividadImportacionSeleccionarTipo.abrir(fuentes, ActividadMenuImportacion.this);

                                        finalizarYAvanzar();

                                    } else {


                                        //Se abre el dialogo de error
                                        new MaterialAlertDialogBuilder(ActividadMenuImportacion.this)
                                                .setTitle(R.string.importacion_archivo_no_valido_titulo)
                                                .setMessage(R.string.importacion_archivo_no_valido_descripcion)
                                                .setCancelable(true)
                                                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .create().show();
                                    }
                                }

                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public static void abrir(@NonNull Context context) {

        Intent intent = new Intent(context, ActividadMenuImportacion.class);
        context.startActivity(intent);

    }

    public static String getNombreDeArchivo(@NonNull Uri uri, @NonNull Context context) {
        String nombre = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    nombre = cursor.getString(displayNameIndex);
                }
            }
        } else if (uri.getScheme() != null && uri.getScheme().equals("file")) {
            nombre = new File(uri.getPath()).getName();
        }
        return nombre;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_importacion_menu);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenu.abrir(ActividadMenuImportacion.this);

                    //Termino esta actividad
                    finalizarYRetroceder();
                }
            }
        });

        findViewById(R.id.boton_archivo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el selector

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                activityResultLauncher.launch(intent);

            }
        });

        findViewById(R.id.boton_enlace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Se abre el dialogo

                //View dialogView = getLayoutInflater().inflate(R.layout.dialogo_importacion_url,null);
                //TextInputLayout inputLayout = dialogView.findViewById(R.id.)

                new MaterialAlertDialogBuilder(ActividadMenuImportacion.this)
                        .setTitle(R.string.importacion_archivo_opcion_enlace)
                        .setView(R.layout.dialogo_importacion_url)
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean error = true;

                                if (dialog instanceof AlertDialog) {
                                    AlertDialog alertDialog = (AlertDialog) dialog;
                                    TextInputLayout textInputLayout = alertDialog.findViewById(R.id.texto_enlace);
                                    if (textInputLayout != null) {
                                        EditText editText = textInputLayout.getEditText();
                                        if (editText != null) {
                                            String url = editText.getText().toString();
                                            if (url.length() > 0 && Fuente.getTipoDato(url) != TipoDato.DESCONOCIDO && (url.startsWith("https://") || url.startsWith("http://"))) {
                                                error = false;

                                                //Si es un formato valido comienzo la importacion
                                                Fuente fuente = new Fuente(url);
                                                ArrayList<Fuente> fuentes = new ArrayList<>();
                                                fuentes.add(fuente);

                                                //Lanzo la importacion
                                                ActividadImportacionSeleccionarTipo.abrir(fuentes, ActividadMenuImportacion.this);

                                                finalizarYAvanzar();
                                            }
                                        }
                                    }
                                }


                                if (error) {

                                    //Se abre el dialogo de error
                                    new MaterialAlertDialogBuilder(ActividadMenuImportacion.this)
                                            .setTitle(R.string.importacion_archivo_no_valido_titulo)
                                            .setMessage(R.string.importacion_archivo_url_no_valido_descripcion)
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create().show();

                                }

                            }
                        })
                        .create().show();


            }
        });

    }


}