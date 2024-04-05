/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2017-2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.navegacion;


import android.content.Context;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.NonNull;

import java.util.Locale;


public class TTS {


    private final android.speech.tts.TextToSpeech textToSpeech;
    private boolean listo = false;

    public TTS(@NonNull Context context) {

        android.speech.tts.TextToSpeech.OnInitListener iniciado = new android.speech.tts.TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                if (status == android.speech.tts.TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(new Locale("es", "Es"));//Locale.getDefault());
                    if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {

                    } else {

                        listo = true;

                    }
                }


            }


        };

        textToSpeech = new android.speech.tts.TextToSpeech(context, iniciado);

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    public void leer(@NonNull String text) {

        if (!text.isEmpty() && listo && !textToSpeech.isSpeaking()) {

            String utteranceId = this.hashCode() + "";
            textToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }

    }

    public void parar() {

        if (textToSpeech != null) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        }

    }


}