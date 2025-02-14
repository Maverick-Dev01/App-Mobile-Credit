package com.example.appadministrador;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditarRegistroActivity extends AppCompatActivity {

    EditText etIMEI, etPrecio, etNombre, etDomicilio, etEdad, etPagoSemanal, etMonto;
    EditText etInteres;
    Button btnGuardar;
    TextView tvSemanasTotal;
    TextInputEditText etFechaInicio, etFechaFin;
    Spinner spinnerMarca;
    String[] opcionesMarca = {"Samsung", "Motorola"};
    DatabaseReference databaseReference;
    String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_registro);

        etIMEI = findViewById(R.id.etEditarIMEI);
        spinnerMarca = findViewById(R.id.spinnerMarca);
        etPrecio = findViewById(R.id.etEditarPrecio);
        etInteres = findViewById(R.id.tvInteres);
        etNombre = findViewById(R.id.etEditarNombre);
        etDomicilio = findViewById(R.id.etEditarDomicilio);
        etEdad = findViewById(R.id.etEditarEdad);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        etPagoSemanal = findViewById(R.id.etEditarPagoSemanal);
        tvSemanasTotal = findViewById(R.id.tvTotalSemanas);
        etMonto = findViewById(R.id.etEditarMonto);
        btnGuardar = findViewById(R.id.btnGuardarEdicion);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesMarca);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarca.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("DatosDispositivos");

        // Recuperar el ID real del registro desde el Intent
        String dispositivoId  = getIntent().getStringExtra("id");

        // Obtener datos del Intent
        String imei = getIntent().getStringExtra("IMEI");
        // Obtener la marca actual y preseleccionarla en el Spinner
        String marcaActual = getIntent().getStringExtra("marcaTelefono");
        if (marcaActual != null) {
            int posicion = Arrays.asList(opcionesMarca).indexOf(marcaActual);
            if (posicion >= 0) {
                spinnerMarca.setSelection(posicion);
            }
        }
        String precio = getIntent().getStringExtra("Precio");
        // Obtener el valor desde Firebase o el Bundle
        String interes = getIntent().getStringExtra("porcentajeInteres");
        String nombre = getIntent().getStringExtra("nombreCliente");
        String domicilio = getIntent().getStringExtra("domicilio");
        String edad = getIntent().getStringExtra("edad");
        String fechaIni = getIntent().getStringExtra("fechaInicio");
        String fechaFin = getIntent().getStringExtra("fechaFin");
        String pagoseman = getIntent().getStringExtra("PagoSemanal");
        int totalSemanas = getIntent().getIntExtra("totalSemanas", 0);
        String monto = getIntent().getStringExtra("MontoTotal");

        // Mostrar datos en los EditText
        etIMEI.setText(imei);
        spinnerMarca.setAdapter(adapter);
        etPrecio.setText(precio);
        etInteres.setText(interes);
        etNombre.setText(nombre);
        etDomicilio.setText(domicilio);
        etEdad.setText(edad);
        etFechaInicio.setText(fechaIni);
        etFechaFin.setText(fechaFin);
        etPagoSemanal.setText(pagoseman);
        tvSemanasTotal.setText(String.valueOf(totalSemanas));
        etMonto.setText(monto);

        btnGuardar.setOnClickListener(v -> guardarCambios());
        etFechaInicio.setOnClickListener(v -> seleccionarFecha(etFechaInicio, true));
        etFechaFin.setOnClickListener(v -> seleccionarFecha(etFechaFin, false));
    }

    private void seleccionarFecha(TextInputEditText editText, boolean esInicio) {
        Calendar fecha = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fecha.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editText.setText(sdf.format(fecha.getTime()));

            if (!etFechaInicio.getText().toString().isEmpty() && !etFechaFin.getText().toString().isEmpty()) {
                calcularSemanas();
            }
        }, fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    // Método para calcular semanas
    private void calcularSemanas() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date inicio = sdf.parse(etFechaInicio.getText().toString());
            Date fin = sdf.parse(etFechaFin.getText().toString());

            long diferencia = fin.getTime() - inicio.getTime();
            int semanas = (int) (diferencia / (1000 * 60 * 60 * 24 * 7));
            tvSemanasTotal.setText(String.valueOf(semanas));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void guardarCambios() {
        String dispositivoId  = getIntent().getStringExtra("id");
        if (dispositivoId == null || dispositivoId.isEmpty()) {
            Toast.makeText(this, "Error: No se encontró el ID del registro", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoImei = etIMEI.getText().toString().trim();
        String nuevoMarca = spinnerMarca.getSelectedItem().toString();
        String nuevoPrecio = etPrecio.getText().toString();
        //String nuevoInteres = etInteres.getText().toString();
        String nuevoNombre = etNombre.getText().toString();
        String nuevoDomicilio = etDomicilio.getText().toString();
        String nuevoEdad = etEdad.getText().toString();
        String nuevoFechaIni = etFechaInicio.getText().toString();
        String nuevoFechaFin = etFechaFin.getText().toString();
        String nuevoPagoseman = etPagoSemanal.getText().toString();
        int nuevasSemanasTotal = Integer.parseInt(tvSemanasTotal.getText().toString());
        String nuevoMonto = etMonto.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DatosDispositivos").child(dispositivoId);
        if (!esIMEIValido(nuevoImei)) {
            Toast.makeText(this, "El IMEI debe tener 15 dígitos numéricos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevoImei.isEmpty() && !nuevoMarca.isEmpty() && !nuevoPrecio.isEmpty() && !nuevoNombre.isEmpty() &&
                !nuevoDomicilio.isEmpty() && !nuevoEdad.isEmpty() && !nuevoFechaIni.isEmpty() &&
                !nuevoPagoseman.isEmpty() && !nuevoFechaFin.isEmpty() && !nuevoMonto.isEmpty()) {

            // Mantener el userId al actualizar los datos
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userId = snapshot.child("userId").getValue(String.class); // Obtener el userId original

                        HashMap<String, Object> datosActualizados = new HashMap<>();
                        datosActualizados.put("imei", nuevoImei);
                        datosActualizados.put("marcaTelefono", nuevoMarca);
                        datosActualizados.put("precio", nuevoPrecio);
                        //datosActualizados.put("porcentajeInteres", nuevoInteres);
                        datosActualizados.put("nombreCliente", nuevoNombre);
                        datosActualizados.put("domicilio", nuevoDomicilio);
                        datosActualizados.put("edad", nuevoEdad);
                        datosActualizados.put("fechaInicio", nuevoFechaIni);
                        datosActualizados.put("fechaFin", nuevoFechaFin);
                        datosActualizados.put("pagoSemanal", nuevoPagoseman);
                        datosActualizados.put("totalSemanas", nuevasSemanasTotal);
                        datosActualizados.put("montoTotal", nuevoMonto);
                        datosActualizados.put("userId", userId); // Asegurar que no se pierda el userId

                        databaseReference.updateChildren(datosActualizados)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(EditarRegistroActivity.this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                                    // Enviar resultado para indicar que hubo un cambio
                                    Intent intent = new Intent();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(EditarRegistroActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        Toast.makeText(EditarRegistroActivity.this, "Registro no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditarRegistroActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean esIMEIValido(String imei) {
        return imei.matches("\\d{15}"); // Expresión regular: 15 dígitos numéricos
    }

}