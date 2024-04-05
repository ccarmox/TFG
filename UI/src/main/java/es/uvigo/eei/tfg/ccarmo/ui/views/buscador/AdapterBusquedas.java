/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.buscador;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;

public class AdapterBusquedas extends RecyclerView.Adapter<AdapterBusquedas.ViewHolder> {

    BuscadorView.OnResultadoListener resultadoListener;
    private ArrayList<Vertice> resultados;

    public AdapterBusquedas(BuscadorView.OnResultadoListener resultadoListener) {
        this.resultadoListener = resultadoListener;
    }

    public void setResultados(ArrayList<Vertice> resultados) {
        this.resultados = resultados;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View listItem = layoutInflater.inflate(R.layout.adapter_buscador, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.texto.setText(resultados.get(position).getInformacion());

        holder.boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (resultadoListener != null) {
                    resultadoListener.OnLatLon(resultados.get(position).getLatLon());
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return resultados == null ? 0 : resultados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView texto;
        public View boton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.boton = itemView;
            this.texto = (TextView) itemView.findViewById(R.id.texto_nombre);
        }
    }

}