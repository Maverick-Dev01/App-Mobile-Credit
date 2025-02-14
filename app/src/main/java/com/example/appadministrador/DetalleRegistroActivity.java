package com.example.appadministrador;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetalleRegistroActivity extends AppCompatActivity {
    TextView tvIMEI,tvMarca, tvPrecio, tvNombreCliente, tvDomicilio, tvEdad, tvFechaInicio,tvFechaFin, tvPagoSemanal, tvSemanasTotal, tvMontoTotal;
    Button btnCerrar;
    TextView tvInteres;
    private DatabaseReference databaseReference;
    private Button btnBloquear, btnDesbloquear;
    private String dispositivoId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_registro);

        tvIMEI = findViewById(R.id.tvIMEI);
        tvMarca = findViewById(R.id.tvMarca);
        tvPrecio = findViewById(R.id.tvPrecio);
        tvInteres = findViewById(R.id.tvInteres);
        tvNombreCliente = findViewById(R.id.tvNombreCliente);
        tvDomicilio = findViewById(R.id.tvDomicilio);
        tvEdad = findViewById(R.id.tvEdad);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFin = findViewById(R.id.tvFechaFin);
        tvPagoSemanal = findViewById(R.id.tvPagoSemanal);
        tvSemanasTotal = findViewById(R.id.tvSemanasTotal);
        tvMontoTotal = findViewById(R.id.tvMontoTotal);

        btnCerrar = findViewById(R.id.btnCerrar);
        btnBloquear = findViewById(R.id.btnBloquear);
        btnDesbloquear = findViewById(R.id.btnDesbloquear);

        // Recuperar el ID del Intent
        dispositivoId = getIntent().getStringExtra("id");

        if (dispositivoId == null || dispositivoId.isEmpty()) {
            Toast.makeText(this, "Error: No se recibió el ID del dispositivo", Toast.LENGTH_LONG).show();
            finish(); // Evita que la app crashee
            return;
        }


        // Inicializar la referencia de la base de datos después de obtener el ID
        databaseReference = FirebaseDatabase.getInstance().getReference("DatosDispositivos").child(dispositivoId);

        btnBloquear.setOnClickListener(v -> cambiarEstado("bloqueado"));
        btnDesbloquear.setOnClickListener(v -> cambiarEstado("activo"));


        // Obtener datos del Intent
        Intent intent = getIntent();
        if (intent != null) {
            String imei = intent.getStringExtra("IMEI");
            tvIMEI.setText("IMEI: " + imei);
            String marca = intent.getStringExtra("marcaTelefono");
            tvMarca.setText("Marca del Telefono: " + marca);
            String price = intent.getStringExtra("Precio");
            tvPrecio.setText("Precio: " + price);
            String interes = intent.getStringExtra("porcentajeInteres");
            tvInteres.setText("Porcentaje de Interes" + interes);
            String nom = intent.getStringExtra("nombreCliente");
            tvNombreCliente.setText("Nombre del Cliente: " + nom);
            String domic = intent.getStringExtra("domicilio");
            tvDomicilio.setText("Domicilio: " + domic);
            String ed = intent.getStringExtra("edad");
            tvEdad.setText("Edad: " + ed);
            String fechaIni = intent.getStringExtra("fechaInicio");
            tvFechaInicio.setText("Periodo Fecha Inicio: " + fechaIni);
            String fechaFin = intent.getStringExtra("fechaFin");
            tvFechaFin.setText("Periodo Fecha Final: " + fechaFin);
            String pagoSem = intent.getStringExtra("pagoSemanal");
            tvPagoSemanal.setText("Pago Semanal: " + pagoSem);
            int semTotal = intent.getIntExtra("totalSemanas", 0);
            tvSemanasTotal.setText(String.valueOf("Semanas Totales: " + semTotal));
            String monto = intent.getStringExtra("montoTotal");
            tvMontoTotal.setText("Monto Total: " + monto);

            Button btnEditar = findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(v -> {
                Intent intent1 = new Intent(DetalleRegistroActivity.this, EditarRegistroActivity.class);
                intent1.putExtra("id", dispositivoId); // ✅ Pasar el ID correctamente
                intent1.putExtra("IMEI", imei);
                intent1.putExtra("marcaTelefono", marca);
                intent1.putExtra("Precio", price);
                intent1.putExtra("porcentajeInteres", interes);
                intent1.putExtra("nombreCliente", nom);
                intent1.putExtra("domicilio", domic);
                intent1.putExtra("edad", ed);
                intent1.putExtra("fechaInicio", fechaIni);
                intent1.putExtra("fechaFin", fechaFin);
                intent1.putExtra("PagoSemanal", pagoSem);
                intent1.putExtra("totalSemanas", semTotal);
                intent1.putExtra("MontoTotal", monto);
                startActivity(intent1);
            });
        } else {
            Toast.makeText(this, "No se recibieron datos", Toast.LENGTH_SHORT).show();
        }

        Button btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(v -> mostrarDialogoConfirmacion(dispositivoId));

        btnCerrar.setOnClickListener(v -> finish()); // Cierra la actividad
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recargar los datos directamente desde Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imei = snapshot.child("imei").getValue(String.class);
                    String marca = snapshot.child("marcaTelefono").getValue(String.class);
                    String precio = snapshot.child("precio").getValue(String.class);
                    String interes = snapshot.child("porcentajeInteres").getValue(String.class);
                    String nombre = snapshot.child("nombreCliente").getValue(String.class);
                    String domicilio = snapshot.child("domicilio").getValue(String.class);
                    String edad = snapshot.child("edad").getValue(String.class);
                    String fechaInicio = snapshot.child("fechaInicio").getValue(String.class);
                    String fechaFin = snapshot.child("fechaFin").getValue(String.class);
                    String pagoSemanal = snapshot.child("pagoSemanal").getValue(String.class);
                    int semanasTotal = snapshot.child("totalSemanas").getValue(int.class);
                    String montoTotal = snapshot.child("montoTotal").getValue(String.class);

                    // Actualizar las vistas con los datos obtenidos
                    tvIMEI.setText("IMEI: " + imei);
                    tvMarca.setText("Marca del Teléfono: " + marca);
                    tvPrecio.setText("Precio: " + precio);
                    tvInteres.setText("Porcentaje de Interes: " + interes);
                    tvInteres.setEnabled(false);  // Hace que no sea editable
                    tvNombreCliente.setText("Nombre del Cliente: " + nombre);
                    tvDomicilio.setText("Domicilio: " + domicilio);
                    tvEdad.setText("Edad: " + edad);
                    tvFechaInicio.setText("Periodo Fecha Inicio: " + fechaInicio);
                    tvFechaFin.setText("Periodo Fecha Final: " + fechaFin);
                    tvPagoSemanal.setText("Pago Semanal: " + pagoSemanal);
                    tvSemanasTotal.setText("Semanas Totales: " + semanasTotal);
                    tvMontoTotal.setText("Monto Total: " + montoTotal);
                } else {
                    Toast.makeText(DetalleRegistroActivity.this, "No se encontraron los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetalleRegistroActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cambiarEstado(String nuevoEstado) {
        databaseReference.child("estado").setValue(nuevoEstado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad y vuelve al inicio
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDialogoConfirmacion(String dispositivoId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar este registro? Esta acción no se puede deshacer.");

        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            eliminarRegistro(dispositivoId);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarRegistro(String dispositivoId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DatosDispositivos");

        databaseReference.child(dispositivoId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetalleRegistroActivity.this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad después de eliminar
                })
                .addOnFailureListener(e ->
                        Toast.makeText(DetalleRegistroActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                );
    }

}