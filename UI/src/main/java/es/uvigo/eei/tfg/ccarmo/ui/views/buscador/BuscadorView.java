/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.buscador;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.ui.views.classes.CompoundBase;
import es.uvigo.eei.tfg.ccarmo.ui.views.navegacion.TTS;

public class BuscadorView extends CompoundBase {

    private SQLite db;
    private OnResultadoListener resultadoListener;
    private AdapterBusquedas adapter;

    private STT stt;
    private TTS tts;

    public BuscadorView(@NonNull Context context) {
        super(context);
        iniciar();
    }

    public BuscadorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        iniciar();
    }

    public BuscadorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniciar();
    }

    public BuscadorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        iniciar();
    }

    private void iniciar() {

        LayoutInflater.
                from(getContext())
                .inflate(
                        R.layout.compound_buscar,
                        this);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new AdapterBusquedas(new OnResultadoListener() {
            @Override
            public void OnLatLon(@NonNull LatLon latLon) {
                if (resultadoListener != null) {
                    resultadoListener.OnLatLon(latLon);
                }
                cerrarBuscador();
            }
        });
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(GONE);

        EditText text = findViewById(R.id.cuadro_texto);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscar(s.toString());

                //Se cambia el boton visible segun el texto escrito
                if (!s.toString().isEmpty()) {
                    findViewById(R.id.boton_voz).setVisibility(GONE);
                    findViewById(R.id.boton_cerrar).setVisibility(VISIBLE);
                } else {
                    findViewById(R.id.boton_voz).setVisibility(VISIBLE);
                    findViewById(R.id.boton_cerrar).setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.boton_cerrar).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarBuscador();
            }
        });

        //Se inicia el sintetizador de voz
        tts = new TTS(getActivity());

        //Se inicia el reconocimiento de voz
        stt = new STT(getActivity(), new STT.OnResultadoListener() {
            @Override
            public void OnResultado(@NonNull String texto) {

                //Se filtra el resultado eliminando palabras clave que no seran buscadas
                String[] comandos = getContext().getResources().getStringArray(R.array.comandos_voz);
                String resultado = texto;
                for (String comando : comandos) {

                    String[] corte = resultado.toLowerCase().split(comando);
                    if (corte.length > 1) {
                        resultado = resultado.substring(corte[0].length() + comando.length()).trim();
                    }

                }

                //Se comprueba cuantos vertices existen
                ArrayList<Vertice> vertices = getVertices(resultado);

                //Si solo existe un vertice se navega a el
                if (vertices.size() == 1) {

                    if (resultadoListener != null) {
                        //Se lee en alto la informacion a la que se va a ir
                        tts.leer(getContext().getResources().getString(R.string.generando_ruta_vigo).replace("Vigo", vertices.get(0).getInformacion()));

                        //Se envia a la vista la posicion de destino
                        resultadoListener.OnLatLon(vertices.get(0));
                    }

                    cerrarBuscador();

                } else {
                    if (!resultado.isEmpty()) {
                        text.setText(resultado);
                    }
                }
            }
        });

        findViewById(R.id.boton_voz).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stt != null) {
                    stt.escuchar();
                }
            }
        });

        findViewById(R.id.boton_voz).setVisibility(VISIBLE);
        findViewById(R.id.boton_cerrar).setVisibility(GONE);
    }

    public void cerrarBuscador() {
        EditText text = findViewById(R.id.cuadro_texto);
        text.setText("");
        // Quita el foco del EditText
        text.clearFocus();

        // Oculta el teclado
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);

        findViewById(R.id.recyclerview).setVisibility(GONE);
    }

    public void iniciar(@NonNull String nombreDB, @NonNull OnResultadoListener resultadoListener) {
        this.resultadoListener = resultadoListener;
        this.db = SQLite.getBaseDeDatos(nombreDB, getContext());
    }

    public void buscar(@Nullable String texto) {
        if (db != null) {
            ArrayList<Vertice> vertices2 = getVertices(texto);
            adapter.setResultados(vertices2);
            findViewById(R.id.recyclerview).setVisibility(!vertices2.isEmpty() ? VISIBLE : GONE);
        }
    }

    private ArrayList<Vertice> getVertices(@Nullable String texto) {
        if (texto != null && !texto.isEmpty()) {
            Map<String, Vertice> vertices = db.getVerticesMap(texto, 20);
            return new ArrayList<>(vertices.values());
        } else {
            return new ArrayList<>();
        }
    }

    public interface OnResultadoListener {
        void OnLatLon(@NonNull LatLon latLon);
    }
}
