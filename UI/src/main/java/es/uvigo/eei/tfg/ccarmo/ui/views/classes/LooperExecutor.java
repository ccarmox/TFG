/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.classes;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class LooperExecutor extends AbstractExecutorService {

    private final Handler handler;

    public LooperExecutor(String name, int priority) {
        HandlerThread thread = new HandlerThread(name, priority);
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public Handler getHandler() {
        return handler;
    }


    @Override
    public void execute(Runnable runnable) {
        if (getHandler().getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    @Override
    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    @Deprecated
    public boolean awaitTermination(long l, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public Looper getLooper() {
        return getHandler().getLooper();
    }


}