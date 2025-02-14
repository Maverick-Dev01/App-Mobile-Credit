package com.example.appadministrador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.ViewHolder> {
    private List<DispositivosRegistrados> listaDispositivos;
    private Context context;

    public RegistroAdapter(List<DispositivosRegistrados> listaRegistros, Context context) {
        this.listaDispositivos = listaRegistros;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_registro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DispositivosRegistrados registro = listaDispositivos.get(position);
        if (registro != null) {
            holder.tvNombreCliente.setText(registro.getNombreCliente());
            holder.tvMarca.setText(registro.getMarcaTelefono());

            if (registro.getEstado().equals("activo")) {
                holder.ivEstado.setImageResource(R.drawable.activo); // Ícono verde
            } else {
                holder.ivEstado.setImageResource(R.drawable.bloqueado); // Ícono rojo
            }

            // Click en el elemento para abrir detalles
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetalleRegistroActivity.class);
                intent.putExtra("IMEI", registro.getIMEI());
                intent.putExtra("marcaTelefono", registro.getMarcaTelefono());
                intent.putExtra("Precio", registro.getPrecio());
                intent.putExtra("porcentajeInteres: ", registro.getPorcentajeInteres());
                intent.putExtra("nombreCliente", registro.getNombreCliente());
                intent.putExtra("domicilio", registro.getDomicilio());
                intent.putExtra("edad", registro.getEdad());
                intent.putExtra("fechaInicio", registro.getFechaInicio());
                intent.putExtra("fechaFin", registro.getFechaFin());
                intent.putExtra("pagoSemanal", registro.getPagoSemanal());
                intent.putExtra("totalSemanas", registro.getTotalSemanas());
                intent.putExtra("montoTotal", registro.getMontoTotal());
                intent.putExtra("id", registro.getDispositivoId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaDispositivos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente, tvMarca;
        ImageView ivEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            ivEstado = itemView.findViewById(R.id.ivEstado);
        }
    }
}

