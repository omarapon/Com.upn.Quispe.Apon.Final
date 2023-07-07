package com.example.comupnquispeaponfinal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comupnquispeaponfinal.Clases.Cartas;
import com.example.comupnquispeaponfinal.DetalleCartaActivity;
import com.example.comupnquispeaponfinal.MapsActivity;
import com.example.comupnquispeaponfinal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartasAdapter extends RecyclerView.Adapter {

    private List<Cartas> cartas;
    Context context;

    public CartasAdapter(List<Cartas> items, Context context) {
        this.cartas = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartasAdapter.NameViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == 1) {
            View view = inflater.inflate(R.layout.item_carta, parent, false);
            viewHolder = new CartasAdapter.NameViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_progressbardue, parent, false);
            viewHolder = new CartasAdapter.NameViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Cartas carta = cartas.get(position);

        if(carta == null) return;

        View view = holder.itemView;

        TextView NombreC = view.findViewById(R.id.NombreCarta);
        TextView AtaqueC = view.findViewById(R.id.AtaqueCarta);
        TextView DefensaC = view.findViewById(R.id.DefensaCarta);
        ImageView img = view.findViewById(R.id.Cartaimagen);


        NombreC.setText(carta.getNombre());
        AtaqueC.setText("Ataque: " + carta.getPuntosdeataque());
        DefensaC.setText("Defensa: " + carta.getPuntosdedefensa());
        Picasso.get().load(carta.getImagen())
                .resize(300, 400) //tamaño específico
                .into(img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context, MapsActivity.class);
                intent.putExtra("position", carta.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartas.size();
    }

    @Override
    public int getItemViewType(int position) {
        Cartas item = cartas.get(position);
        return item == null ? 0 : 1;
    }

    public class NameViewHolder extends RecyclerView.ViewHolder {
        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setCartas(List<Cartas> carta) {
        this.cartas = carta;
    }
}
