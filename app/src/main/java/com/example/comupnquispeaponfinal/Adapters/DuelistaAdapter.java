package com.example.comupnquispeaponfinal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comupnquispeaponfinal.Clases.Duelista;
import com.example.comupnquispeaponfinal.DuelistasDetallesActivity;
import com.example.comupnquispeaponfinal.R;

import java.util.List;

public class DuelistaAdapter extends RecyclerView.Adapter {
    private List<Duelista> duelistas;
    private Context context;

    public DuelistaAdapter(List<Duelista> duelistas, Context context) {
        this.duelistas = duelistas;
        this.context =context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NameViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == 1) {
            View view = inflater.inflate(R.layout.item_listaduelista, parent, false);
            viewHolder = new NameViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_progressbardue, parent, false);
            viewHolder = new NameViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Duelista item = duelistas.get(position);

        if(item == null) return;

        View view = holder.itemView;

        TextView tvName = view.findViewById(R.id.tvnombreD);


        tvName.setText(item.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context, DuelistasDetallesActivity.class);
                intent.putExtra("position", item.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return duelistas.size();
    }
    @Override
    public int getItemViewType(int position) {
        Duelista item = duelistas.get(position);
        return item == null ? 0 : 1;
    }

    public void setCuentas(List<Duelista> cuentas) {
        this.duelistas = duelistas;
    }

    public class NameViewHolder extends RecyclerView.ViewHolder {

        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

