/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.mapa;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.listeners.InterfazMapa;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.ruta.Algoritmo;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.AlgoritmoBase;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.Ruta;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils.Filtro2;
import es.uvigo.eei.tfg.ccarmo.ui.views.buscador.BuscadorView;
import es.uvigo.eei.tfg.ccarmo.ui.views.classes.CompoundBase;
import es.uvigo.eei.tfg.ccarmo.utils.Colores;
import es.uvigo.eei.tfg.ccarmo.utils.Cronometro;

public abstract class MapaBaseView extends CompoundBase implements InterfazMapa {


    protected static final int MAX_CLICK_DURATION = 200;
    private final ArrayList<FiguraMapa> figurasMapa = new ArrayList<>();
    private final Filtro2 filtro2 = new Filtro2(getContext().getApplicationContext());
    private int alto = -1;
    private int ancho = -1;
    private float angulo;
    private double cosenoGiro, senoGiro;
    private double latitud = 0;
    private double longitud = 0;
    private double zoom = 1;
    private Ruta ruta;
    private long ultimaActualizacionBrujula = 0;
    private LatLon userLocationLocal = null;
    private float azimuthLocal = 999;
    private float azimuthShowedLocal = 999;
    private Bitmap map;
    private Bitmap figuras;
    private Bitmap calor;
    private Bitmap usuario;
    private Bitmap clicks;
    private boolean cargadoMapa = false;
    private boolean cargadoFiguras = false;
    private boolean cargadoCalor = false;
    private boolean cargadoUsuario = false;
    private boolean cargadoClicks = false;
    //Variables usadas para controlar la previsualizacion del desplazamiento
    private float previewDesplazamientoX = 0;
    private float previewDesplazamientoY = 0;
    //Variables usadas para controlar la previsualizacion de la rotacion
    private float previewAngulo;
    private float previewAnguloX;
    private float previewAnguloY;
    //Variables usadas para controlar la previsualizacion de zoom
    private float previewZoomEscala = 1;
    private float previewZoomCentroX = 0;
    private float previewZoomCentroY = 0;
    private final Handler handlerUI = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            redibujarBitmapsEnPantalla();
        }
    };
    private Paint lineaPoi;
    private Paint lineaViaPeatonal;
    private Paint lineaViaServicio;
    private Paint lineaViaResidencial;
    private Paint lineaSendero;
    private Paint lineaPasoDePeatones;
    private Paint lineaPasoElevado;
    private Paint lineaEscaleras;
    private Paint lineaAcera;
    private Paint lineaNada;
    private Paint mCamino;
    private Paint paintPuntoCalor;
    @Nullable
    private String nombreDB;
    private SQLite db;


    public MapaBaseView(Context mContext) {
        super(mContext);
        actualizarPinceles();
    }

    public MapaBaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        actualizarPinceles();
    }

    public MapaBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        actualizarPinceles();
    }

    public MapaBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        actualizarPinceles();
    }

    public float getAnguloCentroPantalla() {
        return angulo;
    }

    public void setAnguloCentroPantalla(float angulo) {
        this.angulo = angulo;
    }

    public double getLatitudCentro() {
        return latitud;
    }

    public void setLatitudCentro(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitudCentro() {
        return longitud;
    }

    public void setLongitudCentro(double longitud) {
        this.longitud = longitud;
    }

    /**
     * Centrar el plano de pantalla en un punto
     *
     * @param origen Coordenadas de la posicion que debe ser el origen
     */
    public void centrarPlano(@NonNull LatLon origen) {

        setLatitudCentro(origen.getLatitud());
        setLongitudCentro(origen.getLongitud());

        android.util.Log.v("CentroPlano", origen.getLatitud() + ", " + origen.getLongitud());

        recalcularCoordenadas();
        recargarTodo();

    }

    @Override
    public double getCosenoGiro() {
        return cosenoGiro;
    }

    @Override
    public double getSenoGiro() {
        return senoGiro;
    }

    /**
     * Recalculo las posiciones maximas de lo que se ve en pantalla
     */
    protected void recalcularCoordenadas() {

        double giroRadianes = getAnguloCentroPantalla() * Math.PI / 180;

        cosenoGiro = Math.cos(giroRadianes);
        senoGiro = Math.sin(giroRadianes);

    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        actualizarPinceles();
    }

    @Override
    public double getDiferencialLatitud() {
        return getDiferencialLongitud() * getHeight() / getAncho();
    }

    @Override
    public double getDiferencialLongitud() {
        return (1 / getZoom());
    }

    protected void reiniciarCargas() {
        this.cargadoMapa = false;
        this.cargadoFiguras = false;
        this.cargadoCalor = false;
        this.cargadoUsuario = false;
        this.cargadoClicks = false;
    }

    public boolean isTodoCargado() {
        return cargadoMapa && cargadoFiguras && cargadoCalor && cargadoUsuario && cargadoClicks;
    }

    public void setMap(Bitmap map) {
        this.map = map;
        this.cargadoMapa = true;
        actualizarUI();
    }

    public void setFiguras(Bitmap figuras) {
        this.figuras = figuras;
        this.cargadoFiguras = true;
        actualizarUI();
    }

    public void setCalor(Bitmap calor) {
        this.calor = calor;
        this.cargadoCalor = true;
        actualizarUI();
    }

    public void setUserPosition(Bitmap userPosition, float azimuth) {
        this.usuario = userPosition;
        this.azimuthShowedLocal = azimuth;
        this.cargadoUsuario = true;
        actualizarUI();
    }

    public void setClicks(Bitmap clicks) {
        this.clicks = clicks;
        this.cargadoClicks = true;
        actualizarUI();
    }

    public void redibujarBitmapsEnPantalla() {

        if (getAncho() > 0 && getHeight() > 0) {

            Bitmap bitmap = Bitmap.createBitmap(getAncho(), getAlto(), Bitmap.Config.ARGB_4444);
            bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawColor(Color.parseColor("#f0f0f0"));

            dibujarBitmapEnCanvas(canvas, map);

            dibujarBitmapEnCanvas(canvas, calor);

            dibujarBitmapEnCanvas(canvas, figuras);

            dibujarBitmapEnCanvas(canvas, usuario);

            dibujarBitmapEnCanvas(canvas, clicks);

            ImageView capa_mapa = findViewById(R.id.capas);
            capa_mapa.setImageBitmap(bitmap);

            setCargando(false);

        }

    }

    protected void setPreviewDesplazamiento(float x, float y) {
        this.previewDesplazamientoX = x;
        this.previewDesplazamientoY = y;
    }

    public float getPreviewDesplazamientoX() {
        return previewDesplazamientoX;
    }

    public float getPreviewDesplazamientoY() {
        return previewDesplazamientoY;
    }

    protected void setPreviewDesplazamientoDesactivado() {
        setPreviewDesplazamiento(0, 0);
    }

    public float getPreviewAngulo() {
        return previewAngulo;
    }

    protected void setPreviewAngulo(float angulo, float x, float y) {
        this.previewAngulo = angulo;
        this.previewAnguloX = x;
        this.previewAnguloY = y;
        onAnguloUpdate(getAnguloCentroPantalla() + previewAngulo);
    }

    protected void setPreviewAnguloDesactivado() {
        setPreviewAngulo(0, 0, 0);
    }

    public float getPreviewAnguloX() {
        return previewAnguloX;
    }

    public float getPreviewAnguloY() {
        return previewAnguloY;
    }

    //Variables para atender el color de los pinceles

    protected abstract void onAnguloUpdate(double angulo);

    protected void setPreviewZoom(float escala, float x, float y) {
        this.previewZoomEscala = escala;
        this.previewZoomCentroX = x;
        this.previewZoomCentroY = y;
    }

    protected void setPreviewZoomDesactivado() {
        setPreviewZoom(1, 0, 0);
    }

    public float getPreviewZoomEscala() {
        return previewZoomEscala;
    }

    public float getPreviewZoomCentroX() {
        return previewZoomCentroX;
    }

    public float getPreviewZoomCentroY() {
        return previewZoomCentroY;
    }

    //Metodos usados para añadir valores al mapa
    public synchronized void addFiguraMapa(FiguraMapa uiOverlap) {
        figurasMapa.add(uiOverlap);
        recargarFiguras();
    }

    public synchronized void addFiguraMapa(ArrayList<FiguraMapa> uiOverlap) {
        figurasMapa.addAll(uiOverlap);
        recargarFiguras();
    }

    public synchronized void addFiguraMapa(FiguraMapa uiOverlapIni, FiguraMapa uiOverlapFin) {
        figurasMapa.add(uiOverlapIni);
        figurasMapa.add(uiOverlapFin);
        recargarFiguras();
    }

    public synchronized void limpiarFigurasMapa() {
        figurasMapa.clear();
        recargarFiguras();
    }

    @NonNull
    public ArrayList<FiguraMapa> getFigurasMapa() {
        //Se clonan para evitar que los originales puedan ser modificados
        ArrayList<FiguraMapa> figuras = new ArrayList<>();
        for (FiguraMapa figura : figurasMapa) {
            figuras.add(figura.clonar());
        }
        return figuras;
    }

    private void dibujarBitmapEnCanvas(Canvas canvas, @Nullable Bitmap bitmap) {

        if (bitmap != null) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            Matrix matrix = new Matrix();

            if (previewAngulo > 0 || previewAngulo < 0) {
                // Establecer la posición de rotación y el punto central
                float pivotX = bitmap.getWidth() / 2F;
                float pivotY = bitmap.getHeight() / 2F;
                //matrix.postRotate(previewAngulo, previewAnguloX, previewAnguloY);
                matrix.postRotate(previewAngulo, pivotX, pivotY);
            }

            if (previewZoomEscala != 1) {

                // Especifica el punto central para escalar la imagen alrededor de él
                float puntoX = previewZoomCentroX;
                float puntoY = previewZoomCentroY;

                matrix.postScale(previewZoomEscala, previewZoomEscala, puntoX, puntoY);

            }

            if (previewDesplazamientoX != 0 || previewDesplazamientoY != 0) {
                matrix.postTranslate(previewDesplazamientoX, previewDesplazamientoY);
            }


            // Dibujar el bitmap escalado en el canvas
            canvas.drawBitmap(bitmap, matrix, paint);

        }
    }

    protected Matrix getMatriz() {
        Matrix matrix = new Matrix();

        if (previewAngulo > 0 || previewAngulo < 0) {
            // Establecer la posición de rotación y el punto central
            float pivotX = getAncho() / 2F;
            float pivotY = getAlto() / 2F;
            //matrix.postRotate(previewAngulo, previewAnguloX, previewAnguloY);
            matrix.postRotate(previewAngulo, pivotX, pivotY);
        }

        if (previewZoomEscala != 1) {

            // Especifica el punto central para escalar la imagen alrededor de él
            float puntoX = previewZoomCentroX;
            float puntoY = previewZoomCentroY;

            matrix.postScale(previewZoomEscala, previewZoomEscala, puntoX, puntoY);

        }

        if (previewDesplazamientoX != 0 || previewDesplazamientoY != 0) {
            matrix.postTranslate(previewDesplazamientoX, previewDesplazamientoY);
        }

        return matrix;
    }

    /**
     * Racarga todas las capas del mapa
     */
    public void recargarTodo() {

        if (getAlto() > 0 && getAncho() > 0) {

            setCargando(true);

            this.map = null;
            this.calor = null;
            this.figuras = null;
            this.usuario = null;
            this.clicks = null;

            recargarMapaBase();

            recargarFiguras();

            recargarPosicionUsuario();

            recargarMapaDeCalor();

            recargarClicks();

        }

    }

    /**
     * Iniciar las clases para su uso
     */
    private void actualizarPinceles() {

        //Iniciar colores

        int anchoPincel = (int) Math.round(getZoom() / 16D);

        if (anchoPincel > 15) {
            anchoPincel = 15;
        }

        if (anchoPincel < 1) {
            anchoPincel = 1;
        }

        lineaPoi = new Paint();
        lineaPoi.setAntiAlias(true);
        lineaPoi.setStyle(Paint.Style.STROKE);
        lineaPoi.setColor(Colores.GREEN_400);
        lineaPoi.setStrokeWidth(anchoPincel);

        lineaViaPeatonal = new Paint();
        lineaViaPeatonal.setAntiAlias(true);
        lineaViaPeatonal.setStyle(Paint.Style.STROKE);
        lineaViaPeatonal.setColor(Colores.RED_400);
        lineaViaPeatonal.setStrokeWidth(anchoPincel);

        lineaViaServicio = new Paint();
        lineaViaServicio.setAntiAlias(true);
        lineaViaServicio.setStyle(Paint.Style.STROKE);
        lineaViaServicio.setColor(Colores.BLUE_400);
        lineaViaServicio.setStrokeWidth(anchoPincel);

        lineaViaResidencial = new Paint();
        lineaViaResidencial.setAntiAlias(true);
        lineaViaResidencial.setStyle(Paint.Style.STROKE);
        lineaViaResidencial.setColor(Colores.TEAL_400);
        lineaViaResidencial.setStrokeWidth(anchoPincel);

        lineaSendero = new Paint();
        lineaSendero.setAntiAlias(true);
        lineaSendero.setStyle(Paint.Style.STROKE);
        lineaSendero.setColor(Colores.BROWN_400);
        lineaSendero.setStrokeWidth(anchoPincel);

        lineaPasoDePeatones = new Paint();
        lineaPasoDePeatones.setAntiAlias(true);
        lineaPasoDePeatones.setStyle(Paint.Style.STROKE);
        lineaPasoDePeatones.setColor(Colores.DARK_PURPLE_400);
        lineaPasoDePeatones.setStrokeWidth(anchoPincel);

        lineaPasoElevado = new Paint();
        lineaPasoElevado.setAntiAlias(true);
        lineaPasoElevado.setStyle(Paint.Style.STROKE);
        lineaPasoElevado.setColor(Colores.DARK_PURPLE_A200);
        lineaPasoElevado.setStrokeWidth(anchoPincel);

        lineaEscaleras = new Paint();
        lineaEscaleras.setAntiAlias(true);
        lineaEscaleras.setStyle(Paint.Style.STROKE);
        lineaEscaleras.setColor(Colores.CYAN_400);
        lineaEscaleras.setStrokeWidth(anchoPincel);

        lineaAcera = new Paint();
        lineaAcera.setAntiAlias(true);
        lineaAcera.setStyle(Paint.Style.STROKE);
        lineaAcera.setColor(Colores.GREY_400);
        lineaAcera.setStrokeWidth(anchoPincel);

        lineaNada = new Paint();
        lineaNada.setAntiAlias(true);
        lineaNada.setStyle(Paint.Style.STROKE);
        lineaNada.setColor(Color.BLACK);
        lineaNada.setStrokeWidth(anchoPincel);


        mCamino = new Paint();
        mCamino.setAntiAlias(true);
        mCamino.setStyle(Paint.Style.STROKE);
        mCamino.setColor(Color.RED);
        mCamino.setStrokeWidth(anchoPincel * 3);

        paintPuntoCalor = new Paint();
        paintPuntoCalor.setAntiAlias(true);
        paintPuntoCalor.setStyle(Paint.Style.STROKE);
        paintPuntoCalor.setColor(Color.RED);
        paintPuntoCalor.setStrokeWidth(anchoPincel * 2);

    }

    public Paint getLinea(TipoVia tipoVia) {

        switch (tipoVia) {
            case POI:
                return lineaPoi;
            case VIA_PEATONAL:
                return lineaViaPeatonal;
            case VIA_SERVICIO:
                return lineaViaServicio;
            case VIA_RESIDENCIAL:
                return lineaViaResidencial;
            case SENDERO:
                return lineaSendero;
            case PASO_DE_PEATONES:
                return lineaPasoDePeatones;
            case PASO_ELEVADO:
                return lineaPasoElevado;
            case ESCALERAS:
                return lineaEscaleras;
            case ACERA:
                return lineaAcera;
            case NONE:
            default:
                return lineaNada;
        }

    }

    @NonNull
    public Paint getPaintMapaDeCalor() {
        return paintPuntoCalor;
    }

    @NonNull
    public Paint getPaintRuta() {
        return mCamino;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            recargarTodo();
        }
    }

    /**
     * Refresca la posicion del usuario
     */
    public void actualizarPosicionUsuario(LatLon latLon) {

        if (userLocationLocal == null) {
            this.userLocationLocal = latLon;
            centrarPlano(latLon);
        } else {
            //Refresco la imagen solo si se ha movido la posicion 4 metros
            if (userLocationLocal.getDistanciaFisicaA(latLon) > 4) {
                this.userLocationLocal = latLon;
                if (isModoGeneracion()) {
                    centrarPlano(latLon);
                } else {
                    recargarPosicionUsuario();
                }
            }
        }

    }

    public LatLon getUbicacionUsuario() {
        return userLocationLocal;
    }

    public void centrarPlanoEnElUsuario() {
        if (userLocationLocal != null) {
            centrarPlano(userLocationLocal);
        }
    }

    /**
     * Refresca la orientacion de la brujula
     */
    public void actualizarBrujula(float azimuth) {

        this.azimuthLocal = azimuth;

        //Refresco la brujula solo si hay mas de 10 grados de diferencia y hace mas de 1 segundo que no se refresca
        if (Math.abs(azimuthShowedLocal - azimuth) > 10) {
            if ((System.currentTimeMillis() - ultimaActualizacionBrujula) > 1000) {
                ultimaActualizacionBrujula = System.currentTimeMillis();
                recargarPosicionUsuario();
            }
        }

    }

    public float getAzimuthLocal() {
        return azimuthLocal;
    }

    public Filtro2 getFiltro() {
        return filtro2;
    }

    public void generarRuta(@NonNull LatLon origen, @NonNull LatLon destino) {


        setCargandoRuta();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Cronometro cronometro = new Cronometro("AlgoritmosBusqueda");
                    cronometro.comenzar();

                    AlgoritmoBase rutable = Algoritmo.getAlgoritmo(nombreDB, getFiltro().getConfiguracion(), getContext())
                            .buscarRuta(origen, destino)
                            .terminar();


                    final Ruta rutaNueva = rutable.getRuta();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            setRuta(rutaNueva);

                            if (rutaNueva == null) {
                                //Se abre el dialogo de error
                                new MaterialAlertDialogBuilder(getActivity())
                                        .setTitle(R.string.error_generacion_ruta_titulo)
                                        .setMessage(R.string.error_generacion_ruta_descripcion2)
                                        .setCancelable(true)
                                        .setIcon(R.drawable.ic_alerta)
                                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create().show();
                            }

                            setCargando(false);

                        }
                    });

                } catch (Throwable ignored) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Se abre el dialogo de error
                            new MaterialAlertDialogBuilder(getActivity())
                                    .setTitle(R.string.error_generacion_ruta_titulo)
                                    .setMessage(R.string.error_generacion_ruta_descripcion)
                                    .setCancelable(true)
                                    .setIcon(R.drawable.ic_alerta)
                                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();

                            setCargando(false);

                        }
                    });
                }

            }
        }).start();

    }

    @Nullable
    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(@Nullable Ruta ruta2) {

        if (this.ruta != null) {
            this.ruta.cerrar();
        }

        this.ruta = ruta2;

        refrescarRuta();

    }

    //Variables utilizadas para la gestion de datos

    public void refrescarRuta() {

        if (ruta != null && ruta.getDistancia() > 0) {

            if (isModoGeneracion()) {
                findViewById(R.id.tarjeta_generacion).setVisibility(VISIBLE);
                TextView textoRuta = findViewById(R.id.texto_datos_generacion);
                textoRuta.setText(getContext().getResources().getString(R.string.distancia_recorrida).replace("123", String.valueOf((int) ruta.getDistancia())));
            } else {
                findViewById(R.id.tarjeta_ruta).setVisibility(VISIBLE);
                TextView textoRuta = findViewById(R.id.texto_datos_ruta);
                textoRuta.setText(getContext().getResources().getString(R.string.distancia_ruta).replace("123", String.valueOf((int) ruta.getDistancia())));
            }

        } else {
            findViewById(R.id.tarjeta_ruta).setVisibility(GONE);
            findViewById(R.id.tarjeta_generacion).setVisibility(GONE);
        }

        recargarFiguras();
    }

    public void actualizarUI() {
        Message message = new Message();
        message.what = 1;
        handlerUI.sendMessage(message);
    }

    public void setDatos(@Nullable String origenDB) {
        setDatos(origenDB, 1000, 1000);
    }

    public void setDatos(@Nullable String origenDB, double latitud, double longitud) {
        this.nombreDB = origenDB;
        //Compruebo que las coordenadas son validas
        if (latitud <= 180 && longitud <= 180) {
            this.userLocationLocal = new LatLon(latitud, longitud, 0);
            centrarPlano(new LatLon(latitud, longitud, 0));
        }
        recargarTodo();

        if (origenDB == null || origenDB.isEmpty()) {
            ((BuscadorView) findViewById(R.id.buscador)).setVisibility(GONE);
        } else {
            ((BuscadorView) findViewById(R.id.buscador)).iniciar(origenDB, new BuscadorView.OnResultadoListener() {
                @Override
                public void OnLatLon(@NonNull LatLon latLon) {
                    if (getUbicacionUsuario() != null) {
                        generarRuta(getUbicacionUsuario(), latLon);
                    }
                }
            });
        }

    }

    public void activarBuscador() {
        ((BuscadorView) findViewById(R.id.buscador)).setVisibility(VISIBLE);
    }

    public void desactivarBuscador() {
        ((BuscadorView) findViewById(R.id.buscador)).setVisibility(GONE);
    }

    @NonNull
    protected SQLite getDB() {
        if (db == null && nombreDB != null) {
            this.db = SQLite.getBaseDeDatos(nombreDB, getContext());
        }
        return db;
    }

    @Nullable
    public String getNombreDB() {
        return nombreDB;
    }

    protected boolean isModoGeneracion() {
        return nombreDB == null;
    }

    public abstract void recargarMapaBase();

    public abstract void recargarFiguras();

    public abstract void recargarPosicionUsuario();

    public abstract void recargarMapaDeCalor();

    public abstract void recargarClicks();

    public abstract void setCargando(boolean cargando);

    public void setCargandoRuta() {

        this.cargadoFiguras = false;

        findViewById(R.id.cargando).setVisibility(VISIBLE);

    }

    @Override
    public int getAncho() {
        if (ancho < 1) {
            View canvas = findViewById(R.id.capas);
            if (canvas == null) {
                return 0;
            }
            ancho = canvas.getWidth();
            if (ancho < 1) {
                return 0;
            }
        }
        return ancho;
    }

    @Override
    public int getAlto() {
        if (alto < 1) {
            View canvas = findViewById(R.id.capas);
            if (canvas == null) {
                return 0;
            }
            alto = canvas.getHeight();
            if (alto < 1) {
                return 0;
            }
        }
        return alto;
    }

    public boolean isIniciado() {
        return (getAncho() > 0 && getAlto() > 0);
    }


}
