/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;

public class Utils {


    /**
     * Asigna como referencias al nudo anterior y el nudo siguiente a cada valor
     *
     * @param nudos
     */
    @Deprecated
    public static void referenciarLineaDeNudos(ArrayList<Vertice> nudos) {

        //Asigno el parametro de proximidad a los nudos sucesivos
        if (nudos.size() > 1) {
            nudos.get(0).addProximo(nudos.get(1));

            for (int i = 1; i < nudos.size(); i++) {

                nudos.get(i).addProximo(nudos.get(i - 1));

                if (nudos.size() > (i + 1)) {

                    nudos.get(i).addProximo(nudos.get(i + 1));

                }

            }
        }

    }

    /**
     * Se eliminan los nudos cuyos parametros se encuentren duplicados en una resolucion determinada
     *
     * @param list
     * @return
     */
    public static ArrayList<Vertice> getNudosSinRepetir(ArrayList<Vertice> list) {

        ArrayList<Vertice> b = new ArrayList<>();

        for (Vertice nudo : list) {
            boolean repetido = false;
            for (Vertice old : b) {
                if (nudo.getID().equals(old.getID())) {
                    repetido = true;
                    break;
                }
            }
            if (!repetido) {
                b.add(nudo);
            }
        }

        return b;
    }

    public static void eliminarCache(@NonNull Context context) {
        try {
            File directorioCache = context.getCacheDir();
            eliminarRecursivo(directorioCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void eliminarRecursivo(@NonNull File archivoODirectorio) {
        if (archivoODirectorio.isDirectory()) {
            for (File hijo : archivoODirectorio.listFiles()) {
                eliminarRecursivo(hijo);
            }
        }
        archivoODirectorio.delete();
    }

    public static String getStringFromAsset(String mapAsset, Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = context.getAssets().open(mapAsset);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String getStringFromUri(String mapAsset, Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = context.getContentResolver().openInputStream(Uri.parse(mapAsset));
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    public static String getStringFromCache(String mapAsset, Context context) {
        try {

            File outputFile = new File(context.getCacheDir(), mapAsset);

            StringBuilder sb = new StringBuilder();
            InputStream is = new FileInputStream(outputFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    public static String getStringFromUri(Uri uri, Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = context.getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    public static String getStringFromInternet(String website, Context context) {
        try {
            URL url = new URL(website);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(4000);
            urlConnection.setReadTimeout(4000);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String text = in.lines().collect(Collectors.joining("\n"));
            in.close();
            return text;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    public static LatLon getLocationFromAddress(String strAddress, Context context) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLon p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);

            p1 = new LatLon(location.getLatitude(), location.getLongitude(), 0);

        } catch (Exception ignored) {
        }

        return p1;

    }

}
