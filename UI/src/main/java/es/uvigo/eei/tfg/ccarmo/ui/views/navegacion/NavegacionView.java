/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.navegacion;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Arista;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Margen;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.ui.views.classes.LooperExecutor;
import es.uvigo.eei.tfg.ccarmo.utils.Cronometro;

public class NavegacionView extends FrameLayout {

    private final Context context;
    LooperExecutor looperExecutorNavigation = new LooperExecutor("navigation", Process.THREAD_PRIORITY_DEFAULT);
    LooperExecutor looperExecutorInstrucciones = new LooperExecutor("instrucciones", Process.THREAD_PRIORITY_DEFAULT);
    private String rutaLocal;
    private LatLon userLocationLocal;
    private float azimuthLocal = 0f;
    private Bitmap map;
    private final Handler handlerUI = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            refrescar();
        }
    };
    private Paint mLinea;
    private Paint mCamino;
    private Paint mUser;
    private Paint paint;
    private long ultimaActualizacion = 0L;
    private Instruccion ultimaInstruccion = null;
    private final Handler handlerUI2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            refrescarInstruccion();
        }
    };
    private SQLite db;
    private SQLite dbRuta;
    private TTS speechSynthesizer;    private final Handler handlerNavigation = new Handler(looperExecutorNavigation.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            handlerNavigation.removeMessages(1);


            LatLon userLocation = userLocationLocal;
            float azimuth = azimuthLocal;

            //Referencio el azimuth respecto al eje OX
            double azimuthOX = azimuth + 90;

            //Simplifico el problema haciendo el angulo girado siempre positivo
            if (azimuthOX < 0) {
                azimuthOX = 360 + azimuthOX;
            }


            Cronometro debugueador = new Cronometro("Navegacion");

            android.util.Log.v("Azimuthox", "--" + azimuthOX);

            if (getWidth() == 0 || getHeight() == 0 || userLocation == null) {
                return;
            }

            debugueador.print("Vista valida");

            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            Canvas canvas = new Canvas(bitmap);


            canvas.drawColor(Color.parseColor("#f0f0f0"));

            debugueador.print("Creado el canvas");

            double distancia = 1D / 250D;

            double latC1 = userLocation.getLatitud() + Math.sin(Math.toRadians(azimuthOX + 45)) * distancia;
            double lonC1 = userLocation.getLongitud() + Math.cos(Math.toRadians(azimuthOX + 45)) * distancia;

            double latC2 = userLocation.getLatitud() + Math.sin(Math.toRadians(azimuthOX - 45)) * distancia;
            double lonC2 = userLocation.getLongitud() + Math.cos(Math.toRadians(azimuthOX - 45)) * distancia;

            LatLon puntoC1 = new LatLon(latC1, lonC1, 0);
            LatLon puntoC2 = new LatLon(latC2, lonC2, 0);

            LatLon[] vector = new LatLon[]{userLocation, puntoC1, puntoC2};

            double maxLon = getMaximumLongitude(vector);
            double minLon = getMinimumLongitude(vector);
            double maxLat = getMaximumLatitude(vector);
            double minLat = getMinimumLatitude(vector);

            debugueador.print("Cargadas dimensiones base");


            Map<String, Vertice> quick = db.getVerticesMap(Margen.MARGEN_VISTA, minLat, maxLat, minLon, maxLon);
            List<Vertice> ruta = dbRuta.getVerticeListArray(Margen.MARGEN_VISTA, minLat, maxLat, minLon, maxLon);
            Collections.sort(ruta);

            debugueador.print("Cargada base de datos");


            //Resto 90 grados para que quede respecto al eje OY
            azimuthOX = azimuthOX - 90;
            //Como el giro es antihorario, el giro que tengo que hacer es negativo
            azimuthOX = -azimuthOX;

            //Actualizo los datos para los valores rotados
            vector = new LatLon[]{userLocation, rotarPunto(puntoC1, userLocation, azimuthOX), rotarPunto(puntoC2, userLocation, azimuthOX)};

            maxLon = getMaximumLongitude(vector);
            minLon = getMinimumLongitude(vector);
            maxLat = getMaximumLatitude(vector);
            minLat = getMinimumLatitude(vector);

            for (Vertice nodo : quick.values()) {
                PixelNavegacion punto = new PixelNavegacion(nodo.getLatLon(), userLocation, azimuthOX);
                mLinea.setColor(nodo.getColor());

                int x = punto.getX(minLat, maxLat, minLon, maxLon, NavegacionView.this);
                int y = punto.getY(minLat, maxLat, minLon, maxLon, NavegacionView.this);

                for (Arista nod : nodo.getAristas()) {

                    Vertice noda = quick.get(nod.getId());
                    if (noda != null) {
                        PixelNavegacion punto2 = new PixelNavegacion(noda.getLatLon(), userLocation, azimuthOX);
                        canvas.drawLine(x, y, punto2.getX(minLat, maxLat, minLon, maxLon, NavegacionView.this), punto2.getY(minLat, maxLat, minLon, maxLon, NavegacionView.this), mLinea);
                    }

                }
            }

            debugueador.print("Dibujado mapa");


            if (ruta != null && ruta.size() > 1) {
                for (int i = 1; i < ruta.size(); i++) {

                    //Compruebo que los nudos que se van a unir se sigan dentro de la ruta
                    if (ruta.get(i - 1).getPosicion() == (ruta.get(i).getPosicion() - 1)) {
                        PixelNavegacion punto = new PixelNavegacion(ruta.get(i - 1).getLatLon(), userLocation, azimuthOX);
                        PixelNavegacion punto2 = new PixelNavegacion(ruta.get(i).getLatLon(), userLocation, azimuthOX);

                        canvas.drawLine(punto.getX(minLat, maxLat, minLon, maxLon, NavegacionView.this), punto.getY(minLat, maxLat, minLon, maxLon, NavegacionView.this), punto2.getX(minLat, maxLat, minLon, maxLon, NavegacionView.this), punto2.getY(minLat, maxLat, minLon, maxLon, NavegacionView.this), mCamino);
                    }
                }
            }

            debugueador.print("Dibujada ruta");


            PixelNavegacion usuario = new PixelNavegacion(userLocation, userLocation, azimuthOX);
            canvas.drawCircle(usuario.getX(minLat, maxLat, minLon, maxLon, NavegacionView.this), usuario.getY(minLat, maxLat, minLon, maxLon, NavegacionView.this), 70, mUser);


            NavegacionView.this.map = bitmap;

            Message message = new Message();
            message.what = 1;
            handlerUI.sendMessage(message);

        }
    };
    private boolean lectura = false;    private final Handler handlerInstrucciones = new Handler(looperExecutorInstrucciones.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            handlerInstrucciones.removeMessages(1);


            LatLon userLocation = userLocationLocal;
            float azimuth = azimuthLocal;
            double azimuthOX = 0;

            if (userLocation != null) {

                double margen = 0.5D;

                double maxLon = userLocation.getLongitud() + margen;
                double minLon = userLocation.getLongitud() - margen;
                double maxLat = userLocation.getLatitud() + margen;
                double minLat = userLocation.getLatitud() - margen;

                List<Vertice> ruta = dbRuta.getVerticeListArray(Margen.MARGEN_VISTA, minLat, maxLat, minLon, maxLon);


                if (ruta != null && ruta.size() > 0) {

                    //Referencio el azimuth respecto al eje OX
                    azimuthOX = azimuth + 90;

                    //Simplifico el problema haciendo el angulo girado siempre positivo
                    if (azimuthOX < 0) {
                        azimuthOX = 360 + azimuthOX;
                    }


                    int proximo = 0;
                    double minimo = ruta.get(proximo).getDistanciaFisicaA(userLocation);

                    for (int i = 0; i < ruta.size(); i++) {
                        double dist = ruta.get(i).getDistanciaFisicaA(userLocation);
                        if (dist <= minimo) {
                            proximo = i;
                            minimo = dist;
                        }
                    }

                    Vertice destino;

                    if (minimo > 10) {

                        destino = ruta.get(proximo);

                    } else {

                        if (ruta.size() > (proximo + 1)) {
                            destino = ruta.get(proximo + 1);
                        } else {
                            destino = ruta.get(proximo);
                        }

                    }

                    LatLon puntoB = destino.getLatLon();

                    boolean finalizado = userLocation.getDistanciaFisicaA(ruta.get(ruta.size() - 1)) < 5;

                    double distancia = userLocation.getDistanciaFisicaA(puntoB);

                    //Calculo el angulo del punto de destino respecto al usuario tomando como ejes los ejes de latitud y longitud
                    double tany = puntoB.getLatitud() - userLocation.getLatitud();
                    double tanx = puntoB.getLongitud() - userLocation.getLongitud();

                    //atan2 es similar a la funcion atan pero corrigiendo automaticamente el cuadrante en el que se encuentra el angulo
                    double atan = Math.atan2(tany, tanx);

                    double anguloDestino = Math.toDegrees(atan);
                    if (anguloDestino < 0) {
                        anguloDestino = 360 + anguloDestino;
                    }


                    double anguloObjetivoRespectoUsuario = anguloDestino - azimuthOX;


                    //android.util.Log.v("Angulos","D="+anguloDestino+" | U="+anguloObjetivoRespectoUsuario +" | A="+azimuthOX);

                    //Como ya queda referenciado al eje usuario-azimuth si el diferencia del angulo es negativa
                    //se trata de un giro horario
                    //El angulo es el valor absoluto del calculado
                    //Con esto transformamos un angulo respecto al usuario en dos variables, direccion del giro y angulo de giro
                    double angulo = Math.abs(anguloObjetivoRespectoUsuario);
                    boolean giroHorario = anguloObjetivoRespectoUsuario < 0;

                    Instruccion instruccion = new Instruccion(giroHorario, angulo, azimuthOX, distancia, finalizado);


                    if (ultimaInstruccion == null || ultimaInstruccion.getInstruccionTexto().getTexto() != instruccion.getInstruccionTexto().getTexto()) {
                        ultimaInstruccion = instruccion;
                        android.util.Log.v("Instruccion", instruccion.toString());

                        if (lectura) {
                            speechSynthesizer.leer(getContext().getResources().getString(instruccion.getInstruccionTexto().getTexto()));
                        }

                        Message message = new Message();
                        message.what = 1;
                        handlerUI2.sendMessage(message);
                    }

                }
            }
        }
    };

    public NavegacionView(Context mContext) {
        super(mContext);

        this.context = mContext;
        init();

    }


    public NavegacionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        init();
    }

    public NavegacionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public NavegacionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    //https://stackoverflow.com/questions/2259476/rotating-a-point-about-another-point-2d
    public static LatLon rotarPunto(LatLon point, LatLon center, double angle) {
        LatLon p = point.clone();

        if (false) {
            return p;
        }

        double s = Math.sin(Math.toRadians(angle));
        double c = Math.cos(Math.toRadians(angle));

        //le restamos el origen
        p.setLongitud(p.getLongitud() - center.getLongitud());
        p.setLatitud(p.getLatitud() - center.getLatitud());

        //rotamos el punto
        double xnew = p.getLongitud() * c - p.getLatitud() * s;
        double ynew = p.getLongitud() * s + p.getLatitud() * c;

        //volvemos a sumar el origen
        p.setLongitud(xnew + center.getLongitud());
        p.setLatitud(ynew + center.getLatitud());

        return p;
    }

    public static double getMinimumLongitude(LatLon... l) {
        double m = l[0].getLongitud();
        for (LatLon latLon : l) {
            if (latLon.getLongitud() < m) {
                m = latLon.getLongitud();
            }
        }
        return m;
    }

    public static double getMaximumLongitude(LatLon... l) {
        double m = l[0].getLongitud();
        for (LatLon latLon : l) {
            if (latLon.getLongitud() > m) {
                m = latLon.getLongitud();
            }
        }
        return m;
    }

    public static double getMinimumLatitude(LatLon... l) {
        double m = l[0].getLatitud();
        for (LatLon latLon : l) {
            if (latLon.getLatitud() < m) {
                m = latLon.getLatitud();
            }
        }
        return m;
    }

    public static double getMaximumLatitude(LatLon... l) {
        double m = l[0].getLatitud();
        for (LatLon latLon : l) {
            if (latLon.getLatitud() > m) {
                m = latLon.getLatitud();
            }
        }
        return m;
    }

    public void refrescar() {

        if (getWidth() > 0 && getHeight() > 0) {

            if (map != null) {
                ImageView capa_mapa = findViewById(R.id.capas);
                capa_mapa.setImageBitmap(map);
            }

        }
    }

    public void refrescarInstruccion() {

        if (ultimaInstruccion != null) {
            TextView textView = findViewById(R.id.texto_instruccion);
            textView.setText(ultimaInstruccion.getInstruccionTexto().getTexto());
            ImageView imagen = findViewById(R.id.imagen_instruccion);
            imagen.setImageResource(ultimaInstruccion.getInstruccionTexto().getDrawable());
        }
    }

    public void update(LatLon userLocation) {

        this.userLocationLocal = userLocation;

        actualizarMapaNavegacion();
    }

    public void update(float azimuth) {

        this.azimuthLocal = azimuth;

        actualizarMapaNavegacion();
    }

    private void actualizarMapaNavegacion() {
        if (System.currentTimeMillis() - ultimaActualizacion > 200L) {

            ultimaActualizacion = System.currentTimeMillis();

            Message message = new Message();
            message.what = 1;
            handlerNavigation.sendMessage(message);


            Message message2 = new Message();
            message2.what = 1;
            handlerInstrucciones.sendMessage(message2);

        }
    }

    private void init() {

        LayoutInflater.
                from(getContext())
                .inflate(
                        R.layout.compound_navegacion,
                        this);

        speechSynthesizer = new TTS(getContext());

        findViewById(R.id.boton_voz).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                FloatingActionButton boton = findViewById(R.id.boton_voz);

                if (lectura) {
                    speechSynthesizer.parar();
                    lectura = false;
                    boton.setImageResource(R.drawable.ic_voz_apagada);
                } else {
                    lectura = true;
                    boton.setImageResource(R.drawable.ic_voz_encendida);
                }

            }
        });

        mLinea = new Paint();
        mLinea.setAntiAlias(true);
        mLinea.setStyle(Paint.Style.STROKE);
        mLinea.setColor(Color.BLACK);
        mLinea.setStrokeWidth(10);

        mCamino = new Paint();
        mCamino.setAntiAlias(true);
        mCamino.setStyle(Paint.Style.STROKE);
        mCamino.setColor(Color.RED);
        mCamino.setStrokeWidth(18);

        mUser = new Paint();
        mUser.setColor(Color.BLUE);
        mUser.setAntiAlias(true);
        mUser.setStrokeWidth(5);
        mUser.setStyle(Paint.Style.FILL_AND_STROKE);
        mUser.setStrokeJoin(Paint.Join.ROUND);
        mUser.setStrokeCap(Paint.Cap.ROUND);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

    }

    public void setRuta(@NonNull String nombreDB, @Nullable String ruta) {
        this.rutaLocal = ruta;

        if (db != null) {
            db.close();
            db = null;
        }

        if (dbRuta != null) {
            dbRuta.close();
            dbRuta = null;
        }

        if (db == null) {
            db = SQLite.getBaseDeDatos(nombreDB, context);
        }

        if (dbRuta == null) {
            dbRuta = SQLite.getBaseDeDatos(rutaLocal, context);
        }
    }





}
