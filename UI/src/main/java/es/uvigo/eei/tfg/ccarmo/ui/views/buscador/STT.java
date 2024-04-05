/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.buscador;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

public class STT {

    private SpeechRecognizer speechRecognizer;
    private final OnResultadoListener resultadoListener;
    private final Activity context;

    public STT(@NonNull Activity context, @NonNull OnResultadoListener resultadoListener) {

        this.resultadoListener = resultadoListener;
        this.context = context;

        if (isPermisoConcedido()) {
            cargar();
        }

    }

    private void cargar() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    resultadoListener.OnResultado(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    private boolean isPermisoConcedido() {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public void escuchar() {

        if (isPermisoConcedido()) {

            if (speechRecognizer == null) {
                cargar();
            }

            if (speechRecognizer != null) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognizer.startListening(intent);
            }

        } else {

            ActivityCompat.requestPermissions(context, new String[]{
                            Manifest.permission.RECORD_AUDIO},
                    99);
        }
    }

    public void destruir() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    public interface OnResultadoListener {
        void OnResultado(@NonNull String texto);
    }

}
