/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.mapa;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Arista;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Pixel;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Margen;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.PuntoCalor;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.Ruta;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.exportacion.ExportarGeoJSON;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.exportacion.classes.Exportable;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion.ActividadImportacionSeleccionarTipo;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.ActividadNavegacion;
import es.uvigo.eei.tfg.ccarmo.ui.views.classes.LooperExecutor;
import es.uvigo.eei.tfg.ccarmo.utils.Cronometro;

public class MapaView extends MapaBaseView {

    private final Context context;
    //Clases para atender los dibujados en hilos
    private final LooperExecutor looperExecutorMap = new LooperExecutor("map", Process.THREAD_PRIORITY_DEFAULT);
    private final LooperExecutor looperExecutorOver = new LooperExecutor("over", Process.THREAD_PRIORITY_DEFAULT);
    private final LooperExecutor looperExecutorCalor = new LooperExecutor("calor", Process.THREAD_PRIORITY_DEFAULT);
    private final LooperExecutor looperExecutorUser = new LooperExecutor("user", Process.THREAD_PRIORITY_DEFAULT);
    private boolean atenderAcciones = false;
    private long evento_click_inicio;
    private Pixel toqueA1, toqueA2, toqueB1, toqueB2;
    private LatLon ultimoClick, origenRuta, destinoRuta;
    private ScaleGestureDetector scaleGestureDetector;
    private final Handler handlerMap = new Handler(looperExecutorMap.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (isIniciado()) {

                if (!isModoGeneracion()) {

                    Cronometro debugueador = new Cronometro("Mapa2");
                    debugueador.comenzar();

                    handlerMap.removeMessages(1);

                    Bitmap bitmap = Bitmap.createBitmap(getAncho(), getAlto(), Bitmap.Config.ARGB_4444);
                    bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                    Canvas canvas = new Canvas(bitmap);

                    debugueador.print("canvas");

                    canvas.drawColor(Color.parseColor("#f0f0f0"));

                    debugueador.print("canvas color");

                    recalcularCoordenadas();

                    debugueador.print("recalcular");

                    double latitud1 = MapaView.this.getLatitudCentro() - 1 / getZoom();
                    double latitud2 = MapaView.this.getLatitudCentro() + 1 / getZoom();

                    double longitud1 = getLongitudCentro() - 1 / getZoom();
                    double longitud2 = getLongitudCentro() + 1 / getZoom();


                    Map<String, Vertice> quick = getDB().getVerticesMap(Margen.MARGEN_VISTA, latitud1, latitud2, longitud1, longitud2, getFiltro().getConfiguracion());


                    debugueador.print("db cargada");

                    for (Vertice nodo : quick.values()) {
                        nodo.referenciar(MapaView.this);
                    }

                    debugueador.print("puntos transformados");

                    for (Vertice nodo1 : quick.values()) {

                        if (getFiltro().isDibujarLineas()) {

                            for (Arista nod : nodo1.getAristas()) {

                                Vertice nodo2 = quick.get(nod.getId());
                                if (nodo2 != null) {
                                    canvas.drawLine(nodo1.getX(), nodo1.getY(), nodo2.getX(), nodo2.getY(), getLinea(nodo1.getTipoVia()));
                                }

                            }
                        } else {
                            canvas.drawPoint(nodo1.getX(), nodo1.getY(), getLinea(nodo1.getTipoVia()));
                        }
                    }

                    debugueador.print("lineas pintadas [" + quick.size() + "]");


                    setMap(bitmap);

                    debugueador.print("cache invalidada");

                } else {

                    setMap(null);

                }
            }

        }
    };
    private ClickListener mapaClickListener;
    private final Handler handlerOver = new Handler(looperExecutorOver.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            handlerOver.removeMessages(1);

            if (isIniciado()) {


                ArrayList<FiguraMapa> uiOverlaps = getFigurasMapa();


                Bitmap bitmap = Bitmap.createBitmap(getAncho(), getAlto(), Bitmap.Config.ARGB_8888);
                bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();
                paint.setAlpha(0);
                canvas.drawBitmap(bitmap, 0, 0, paint);

                recalcularCoordenadas();


                if (!uiOverlaps.isEmpty()) {

                    for (FiguraMapa uiOverlap : uiOverlaps) {
                        uiOverlap.drawOnCanvas(canvas, MapaView.this);
                    }

                }

                Ruta ruta = getRuta();

                if (ruta != null) {

                    android.util.Log.v("Ruta", ruta.toString());

                    double latitud1 = MapaView.this.getLatitudCentro() - 1 / getZoom();
                    double latitud2 = MapaView.this.getLatitudCentro() + 1 / getZoom();

                    double longitud1 = getLongitudCentro() - 1 / getZoom();
                    double longitud2 = getLongitudCentro() + 1 / getZoom();

                    Map<String, Vertice> r = ruta.getDB(getContext()).getVerticesMap(Margen.MARGEN_VISTA, latitud1, latitud2, longitud1, longitud2, null);

                    if (r != null) {

                        List<Vertice> ruta2 = new ArrayList<>(r.values());
                        Collections.sort(ruta2);

                        if (ruta2.size() > 1) {

                            Pixel a = new Pixel(ruta2.get(0).getLatLon(), MapaView.this);

                            if (ruta.isArea()) {

                                Path path = new Path();
                                path.moveTo(a.getX(), a.getY());

                                for (int i = 1; i < ruta2.size(); i++) {

                                    Pixel punto2 = new Pixel(ruta2.get(i).getLatLon(), MapaView.this);
                                    path.lineTo(punto2.getX(), punto2.getY());

                                }

                                Paint fill = new Paint();
                                fill.setAntiAlias(true);
                                fill.setStyle(Paint.Style.FILL);
                                fill.setColor(Color.parseColor("#30F01515"));

                                canvas.drawPath(path, fill);
                                canvas.drawPath(path, getPaintRuta());

                            } else {
                                for (int i = 1; i < ruta2.size(); i++) {

                                    if (ruta2.get(i - 1).getPosicion() == (ruta2.get(i).getPosicion() - 1)) {

                                        Pixel punto2 = new Pixel(ruta2.get(i).getLatLon(), MapaView.this);

                                        canvas.drawLine(a.getX(), a.getY(), punto2.getX(), punto2.getY(), getPaintRuta());

                                        a = punto2;

                                    } else {
                                        a = new Pixel(ruta2.get(i).getLatLon(), MapaView.this);
                                    }
                                }
                            }

                        }
                    }

                }

                setFiguras(bitmap);

            }

        }
    };

    public MapaView(Context mContext) {
        super(mContext);
        this.context = mContext;
        iniciar();
    }

    private final Handler handlerCalor = new Handler(looperExecutorCalor.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            handlerCalor.removeMessages(1);

            if (isIniciado()) {

                Bitmap bitmap = Bitmap.createBitmap(getAncho(), getAlto(), Bitmap.Config.ARGB_8888);
                bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();
                paint.setAlpha(0);
                canvas.drawBitmap(bitmap, 0, 0, paint);

                recalcularCoordenadas();

                Ruta ruta = getRuta();

                if (getFiltro().isPuntosDeCalor() && ruta != null && ruta.getMapaCalor() != null) {

                    canvas.drawColor(Color.parseColor("#f0f0f0"));
                    for (PuntoCalor puntoCalor : ruta.getMapaCalor()) {
                        getPaintMapaDeCalor().setColor(puntoCalor.getColor(200));
                        puntoCalor.referenciar(MapaView.this);
                        canvas.drawPoint(puntoCalor.getX(), puntoCalor.getY(), getPaintMapaDeCalor());
                    }
                }

                setCalor(bitmap);

            }

        }
    };

    public MapaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        iniciar();
    }

    private final Handler handlerUser = new Handler(looperExecutorUser.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {


            handlerUser.removeMessages(1);

            if (isIniciado()) {

                Bitmap bitmap = Bitmap.createBitmap(getAncho(), getAlto(), Bitmap.Config.ARGB_8888);
                bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();
                paint.setAlpha(0);
                canvas.drawBitmap(bitmap, 0, 0, paint);

                LatLon userLocation = getUbicacionUsuario();
                float azimuth = getAzimuthLocal();

                if (userLocation != null) {

                    recalcularCoordenadas();

                    Paint paintUser = new Paint();
                    paintUser.setAntiAlias(true);
                    paintUser.setStyle(Paint.Style.STROKE);
                    paintUser.setColor(Color.RED);
                    paintUser.setStrokeWidth(10);

                    //Dibujo la posicion del GPS
                    Pixel punto = new Pixel(userLocation, MapaView.this);
                    canvas.drawCircle(punto.getX(), punto.getY(), 40, paintUser);

                    //Dibujo el cono que representa a donde apunta la brujula
                    if (azimuth <= 360) {


                        int largo = 3160;
                        int amplitud = 30;


                        int nx1 = (int) (largo * Math.sin(Math.toRadians(azimuth + amplitud)));
                        int ny1 = (int) (largo * Math.cos(Math.toRadians(azimuth + amplitud)));

                        int nx2 = (int) (largo * Math.sin(Math.toRadians(azimuth - amplitud)));
                        int ny2 = (int) (largo * Math.cos(Math.toRadians(azimuth - amplitud)));

                        Pixel punto1 = new Pixel(punto.getLatitud() + ny1, punto.getLongitud() - nx1, MapaView.this);
                        Pixel punto2 = new Pixel(punto.getLatitud() + ny2, punto.getLongitud() - nx2, MapaView.this);


                        Paint wallpaint = new Paint();
                        wallpaint.setColor(Color.parseColor("#301975ff"));
                        wallpaint.setStyle(Paint.Style.FILL);

                        Path wallpath = new Path();
                        wallpath.reset();

                        wallpath.moveTo(punto.getX(), punto.getY());
                        wallpath.lineTo(punto1.getX(), punto1.getY());
                        wallpath.lineTo(punto2.getX(), punto2.getY());
                        wallpath.lineTo(punto.getX(), punto.getY());

                        canvas.drawPath(wallpath, wallpaint);


                    }

                }


                setUserPosition(bitmap, azimuth);

            }

        }
    };

    public MapaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        iniciar();
    }

    private final Handler handlerClicks = new Handler(looperExecutorUser.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {


            handlerClicks.removeMessages(1);

            if (isIniciado()) {

                LatLon clickado = ultimoClick != null ? ultimoClick.clone() : null;

                if (clickado == null && origenRuta == null && destinoRuta == null) {
                    setClicks(null);
                } else {

                    Bitmap bitmap = Bitmap.createBitmap(getAncho(), getAlto(), Bitmap.Config.ARGB_8888);
                    bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                    Canvas canvas = new Canvas(bitmap);

                    Paint paint = new Paint();
                    paint.setAlpha(0);
                    canvas.drawBitmap(bitmap, 0, 0, paint);

                    recalcularCoordenadas();

                    int ancho = 90;
                    int alto = 90;

                    if (clickado != null) {

                        Paint paintUser = new Paint();
                        paintUser.setAntiAlias(true);
                        paintUser.setStyle(Paint.Style.FILL);
                        paintUser.setColor(Color.BLUE);
                        paintUser.setStrokeWidth(10);

                        //Dibujo la posicion del GPS
                        Pixel punto = new Pixel(clickado, MapaView.this);


                        Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_bandera);
                        if (drawable != null) {
                            drawable.setBounds(punto.getX() - ancho / 2, punto.getY() - alto, punto.getX() + ancho / 2, punto.getY()); // Establece los límites del Drawable
                            drawable.draw(canvas);
                        }
                    }

                    if (origenRuta != null) {

                        Pixel punto = new Pixel(origenRuta, MapaView.this);

                        Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_bandera);
                        if (drawable != null) {
                            drawable.setBounds(punto.getX() - ancho / 2, punto.getY() - alto, punto.getX() + ancho / 2, punto.getY()); // Establece los límites del Drawable
                            drawable.draw(canvas);
                        }
                    }

                    if (destinoRuta != null) {

                        Pixel punto = new Pixel(destinoRuta, MapaView.this);

                        Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_bandera);
                        if (drawable != null) {
                            drawable.setBounds(punto.getX() - ancho / 2, punto.getY() - alto, punto.getX() + ancho / 2, punto.getY()); // Establece los límites del Drawable
                            drawable.draw(canvas);
                        }
                    }

                    setClicks(bitmap);

                }

            }


        }
    };


    public MapaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        iniciar();
    }

    /**
     * Metodo para obtener el angulo de una pulsacion
     *
     * @param a Pulsacion de un dedo
     * @param b Pulsacion de otro dedo
     * @return angulo representado en pantalla formado por los dos dedos
     */
    private static float obtenerAngulo(Pixel a, Pixel b) {
        double deltaX = b.getX() - a.getX();
        double deltaY = b.getY() - a.getY();
        return (float) (Math.atan2(deltaY, deltaX) * 180D / Math.PI);
    }

    @Override
    public void recargarFiguras() {
        Message message = new Message();
        message.what = 1;
        handlerOver.sendMessage(message);
    }

    @Override
    public void recargarMapaBase() {
        Message message = new Message();
        message.what = 1;
        handlerMap.sendMessage(message);
    }

    @Override
    public void recargarMapaDeCalor() {
        Message message = new Message();
        message.what = 1;
        handlerCalor.sendMessage(message);
    }

    @Override
    public void recargarPosicionUsuario() {
        Message message = new Message();
        message.what = 1;
        handlerUser.sendMessage(message);
    }

    @Override
    public void recargarClicks() {
        Message message = new Message();
        message.what = 1;
        handlerClicks.sendMessage(message);
    }

    @Override
    public void setCargando(boolean cargando) {

        //No se oculta hasta que todas las capas hayan sido cargadas
        boolean aplicar = false;

        if (cargando) {
            reiniciarCargas();
            aplicar = true;
        } else {
            if (isTodoCargado()) {
                aplicar = true;
            }
        }

        if (aplicar) {
            findViewById(R.id.cargando).setVisibility(cargando ? VISIBLE : GONE);
        }
    }

    /**
     * Iniciar las clases para su uso
     */
    private void iniciar() {

        setLatitudCentro(42.2056);
        setLongitudCentro(-8.7632);
        setZoom(1.000F / (-8.744322 - getLongitudCentro()));


        LayoutInflater.
                from(getContext())
                .inflate(
                        R.layout.compound_mapa,
                        this);

        click(null);

        findViewById(R.id.tarjeta_ruta).setVisibility(GONE);
        findViewById(R.id.tarjeta_generacion).setVisibility(GONE);

        findViewById(R.id.boton_ruta_hasta_aqui).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLon destino = ultimoClick;


                LatLon origen = origenRuta != null ? origenRuta : getUbicacionUsuario();

                if (destino != null && origen != null) {
                    destinoRuta = destino;
                    generarRuta(origen, destino);
                }
                click(null);
            }
        });

        findViewById(R.id.boton_ruta_desde_aqui).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                origenRuta = ultimoClick;
                click(null);
            }
        });

        findViewById(R.id.boton_compartir_ruta).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity activity = getActivity();
                Ruta ruta = getRuta();

                if (activity instanceof Exportable && ruta != null) {

                    String json = ExportarGeoJSON.getGeoJSONGenerado(ruta.getNombreDB(), getContext());

                    ((Exportable) activity).exportarContenido("ruta.geojson", json);

                }

            }

        });

        findViewById(R.id.boton_compartir_generacion).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity activity = getActivity();
                Ruta ruta = getRuta();

                if (activity instanceof Exportable && ruta != null) {

                    String json = ExportarGeoJSON.getGeoJSONGenerado(ruta.getNombreDB(), getContext());

                    ((Exportable) activity).exportarContenido("ruta.geojson", json);

                }

            }

        });

        findViewById(R.id.boton_cancelar_ruta).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                origenRuta = null;
                destinoRuta = null;
                ultimoClick = null;

                setRuta(null);

            }

        });

        findViewById(R.id.boton_cancelar_generacion).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity activity = getActivity();
                if (activity != null && !activity.isFinishing()) {
                    activity.onBackPressed();
                }


            }

        });

        findViewById(R.id.boton_navegacion).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isModoGeneracion()) {
                    ActividadNavegacion.abrir(getNombreDB(), getRuta().getNombreDB(), getActivity());

                    getActivity().finish();
                }

            }

        });

        findViewById(R.id.boton_importar_generacion).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity activity = getActivity();
                Ruta ruta = getRuta();

                if (activity instanceof Exportable && ruta != null) {

                    Fuente fuente = Fuente.getFuenteSQL(ruta.getNombreDB());
                    ArrayList<Fuente> fuentes = new ArrayList<>();
                    fuentes.add(fuente);

                    ActividadImportacionSeleccionarTipo.abrir(fuentes, getActivity());

                    getActivity().finish();

                }

            }

        });

        findViewById(R.id.boton_capas).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.capas_visibles)
                        .setCancelable(true)
                        .setMultiChoiceItems(getFiltro().getNombreCapas(), getFiltro().getSeleccionados(), new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                getFiltro().setSeleccionado(which, isChecked);
                                recargarTodo();
                            }
                        })
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            }
        });

        findViewById(R.id.boton_centrar).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                centrarPlanoEnElUsuario();
            }
        });

        findViewById(R.id.capas).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (scaleGestureDetector == null) {
                    scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
                        @Override
                        public boolean onScale(@NonNull ScaleGestureDetector detector) {

                            setZoom(getZoom() * detector.getScaleFactor());

                            float preview = getPreviewZoomEscala() * detector.getScaleFactor();

                            setPreviewZoom(preview, detector.getFocusX(), detector.getFocusY());

                            return true;
                        }

                        @Override
                        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                            //Clono los datos actuales para usarlos
                            return true;
                        }

                        @Override
                        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

                        }
                    });
                }

                scaleGestureDetector.onTouchEvent(event);

                if (!atenderAcciones) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        atenderAcciones = true;
                    }
                }

                if (atenderAcciones) {

                    //Ejecuto las funciones de escala antes del desplazamiento
                    //para evitar fallos


                    switch (event.getActionMasked()) {

                        case MotionEvent.ACTION_MOVE:

                            click(null);

                            toqueA2 = new Pixel();
                            toqueA2.invertirReferencia((int) event.getX(0), (int) event.getY(0), MapaView.this);

                            if (event.getPointerCount() > 1) {
                                toqueB2 = new Pixel();
                                toqueB2.invertirReferencia((int) event.getX(1), (int) event.getY(1), MapaView.this);
                            }


                            //Calculo el giro
                            if (toqueB2 != null && toqueB1 != null) {
                                float anguloActual = obtenerAngulo(toqueA2, toqueB2);
                                float anguloInicial = obtenerAngulo(toqueA1, toqueB1);

                                setPreviewAngulo(anguloActual - anguloInicial, (toqueA2.getX() + toqueB2.getX()) / 2F, (toqueA2.getY() + toqueB2.getY()) / 2F);
                            }

                            //Calculo desplazamientos
                            if (toqueB2 != null && toqueB1 != null) {

                                int x1 = (toqueA1.getX() + toqueB1.getX()) / 2;
                                int y1 = (toqueA1.getY() + toqueB1.getY()) / 2;

                                int x2 = (toqueA2.getX() + toqueB2.getX()) / 2;
                                int y2 = (toqueA2.getY() + toqueB2.getY()) / 2;

                                setPreviewDesplazamiento(x2 - x1, y2 - y1);
                            }

                            if (event.getPointerCount() == 1) {
                                setPreviewDesplazamiento(event.getX() - toqueA1.getX(), event.getY() - toqueA1.getY());
                            }

                            //Borro los clicks
                            ultimoClick = null;
                            setClicks(null);
                            redibujarBitmapsEnPantalla();

                            break;

                        case MotionEvent.ACTION_DOWN:
                            //Se llama cuando el primer dedo toca la pantalla

                            if (toqueA1 == null) {
                                toqueA1 = new Pixel();
                                toqueA1.invertirReferencia((int) event.getX(), (int) event.getY(), MapaView.this);

                                //Inicio el timer para el click
                                evento_click_inicio = System.currentTimeMillis();
                            }


                            break;

                        case MotionEvent.ACTION_POINTER_DOWN:
                            //Se llama cuando un segundo o tercer dedo toca la pantalla

                            if (toqueB1 == null) {

                                //Desprecio la primera pulsacion
                                toqueA1 = new Pixel();
                                toqueA1.invertirReferencia((int) event.getX(), (int) event.getY(), MapaView.this);

                                toqueB1 = new Pixel();
                                toqueB1.invertirReferencia((int) event.getX(1), (int) event.getY(1), MapaView.this);
                            }

                            break;


                        case MotionEvent.ACTION_UP:
                            //Se llama cuando no quedan dedos en la pantalla

                            //Detecto si ha sido una pulsacion corta
                            if ((System.currentTimeMillis() - evento_click_inicio) < MAX_CLICK_DURATION) {

                                Pixel pix = new Pixel();
                                pix.invertirReferencia((int) event.getX(), (int) event.getY(), MapaView.this);

                                //Calculo las coordenadas de la pulsacion y llamo al evento correspondiente
                                click(pix);

                                reiniciarPreview();

                                recargarClicks();

                                break;

                            }

                            //Se continua y se aplican los cambios como si fuese un ACTION_POINTER_UP

                        case MotionEvent.ACTION_POINTER_UP:
                            //Se llama cuando un segundo o tercer dedo suelta la pantalla

                            //Borro las pulsaciones
                            ultimoClick = null;
                            setClicks(null);

                            //La matriz relaciona puntos en pantalla con su posicion en la previsualizacion de los cambios
                            Matrix matrix = getMatriz();

                            //La matriz inversa relaciona los puntos de la previsualizacion con los de la pantalla
                            Matrix inversa = new Matrix();
                            matrix.invert(inversa);

                            //Se usa como centro el centro de la vista
                            float[] coordenadasCentro = {getAncho() / 2F, getAlto() / 2F};
                            inversa.mapPoints(coordenadasCentro);

                            //La clase pixel relaciona coordenadas con su posicion en el eje X Y
                            //Con el metodo invertir podemos transformar posicion X e Y en coordenadas
                            Pixel pixelCentro = new Pixel();
                            pixelCentro.invertirReferencia((int) coordenadasCentro[0], (int) coordenadasCentro[1], MapaView.this);

                            //Añado el angulo de la previsualizacion al angulo
                            setAnguloCentroPantalla(getAnguloCentroPantalla() + getPreviewAngulo());

                            //Centro el plano en el nuevo centro que da continuidad a la previsualizacion
                            centrarPlano(pixelCentro);

                            //Borro el formato de la previsualizacion
                            reiniciarPreview();

                            recargarTodo();

                            //Restrinjo las nuevas interacciones hasta que no se deje de presionar la pantalla
                            atenderAcciones = false;

                            break;

                        case MotionEvent.ACTION_CANCEL:

                            toqueA1 = null;
                            toqueA2 = null;
                            toqueB1 = null;
                            toqueB2 = null;

                            setPreviewZoomDesactivado();
                            setPreviewDesplazamientoDesactivado();
                            setPreviewAnguloDesactivado();

                            recargarTodo();

                            break;


                    }

                }


                return true;
            }
        });

    }

    /**
     * Metodo para reiniciar los parametros de la previsualizacion
     */
    private void reiniciarPreview() {

        toqueA1 = null;
        toqueA2 = null;
        toqueB1 = null;
        toqueB2 = null;

        setPreviewZoomDesactivado();
        setPreviewDesplazamientoDesactivado();
        setPreviewAnguloDesactivado();

    }

    @Override
    protected void onAnguloUpdate(double angulo) {
        ImageView brujula = findViewById(R.id.view_brujula);
        brujula.setRotation((float) angulo);
    }

    public void setMapaClickListener(@Nullable ClickListener mapaClickListener) {
        this.mapaClickListener = mapaClickListener;
    }

    private void click(@Nullable LatLon latLon) {

        if (mapaClickListener != null) {
            if (latLon != null) {
                mapaClickListener.click(latLon);
            }
        } else {
            if (!isModoGeneracion() && getRuta() == null) {
                if (latLon != null) {

                    ultimoClick = latLon;

                    recargarClicks();

                    findViewById(R.id.contenedor_botones_ruta).setVisibility(VISIBLE);

                } else {
                    findViewById(R.id.contenedor_botones_ruta).setVisibility(GONE);
                }
            } else {
                findViewById(R.id.contenedor_botones_ruta).setVisibility(GONE);
            }
        }
    }

    public interface ClickListener {
        void click(@NonNull LatLon latLon);
    }


}
