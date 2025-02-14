package com.example.appadministrador;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgregarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarFragment extends Fragment {
    EditText etIMEI, etPrecio, etNombreCliente, etDomicilio, etEdad, etPagoSemanal, etMontoTotal;
    Button btnGuardar;
    private TextInputEditText etFechaInicio, etFechaFin;
    Spinner spinnerInteres;
    private TextView tvTotalSemanas;
    private Calendar fechaInicio, fechaFin;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Spinner spinnerMarca;
    DatabaseReference databaseReference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AgregarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarFragment newInstance(String param1, String param2) {
        AgregarFragment fragment = new AgregarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar, container, false);

        String[] opcionesMarca = {"Samsung", "Motorola"};

        etIMEI = view.findViewById(R.id.etIMEI);
        spinnerMarca = view.findViewById(R.id.etMarca);
        etPrecio = view.findViewById(R.id.etPrecio);

        // Adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"Samsung", "Motorola"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarca.setAdapter(adapter);

        spinnerInteres = view.findViewById(R.id.spinnerInteres);

        etNombreCliente = view.findViewById(R.id.etNombreCliente);
        etDomicilio = view.findViewById(R.id.etDomicilio);
        etEdad = view.findViewById(R.id.etEdad);
        etPagoSemanal = view.findViewById(R.id.etPagoSemanal);
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        tvTotalSemanas = view.findViewById(R.id.tvTotalSemanas);

        fechaInicio = Calendar.getInstance();
        fechaFin = Calendar.getInstance();

        etFechaInicio.setOnClickListener (v-> seleccionarFecha(etFechaInicio, fechaInicio, true));
        etFechaFin.setOnClickListener(v -> seleccionarFecha(etFechaFin, fechaFin, false));
        etMontoTotal = view.findViewById(R.id.etMontoTotal);

        btnGuardar = view.findViewById(R.id.btnGuardar);

        databaseReference = FirebaseDatabase.getInstance().getReference("DatosDispositivos");

        btnGuardar.setOnClickListener(v -> guardarDatos());

        etMontoTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcularPagoSemanal();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        tvTotalSemanas.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcularPagoSemanal();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        etPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcularMontoTotal();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        spinnerInteres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calcularMontoTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }
    private void calcularPagoSemanal() {
        String montoTotalStr = etMontoTotal.getText().toString().trim();
        String semanasTotalStr = tvTotalSemanas.getText().toString().replaceAll("[^0-9]", "").trim(); // Extrae solo números

        if (!montoTotalStr.isEmpty() && !semanasTotalStr.isEmpty()) {
            try {
                double montoTotal = Double.parseDouble(montoTotalStr);
                int semanasTotal = Integer.parseInt(semanasTotalStr);

                if (semanasTotal > 0) {
                    double pagoSemanal = montoTotal / semanasTotal;
                    etPagoSemanal.setText(String.format(Locale.getDefault(), "%.2f", pagoSemanal));
                } else {
                    etPagoSemanal.setText(""); // Evitar dividir entre 0
                }
            } catch (NumberFormatException e) {
                etPagoSemanal.setText(""); // Manejo de error si hay valores inválidos
            }
        }
    }

    private void seleccionarFecha(TextInputEditText editText, Calendar fecha, boolean esInicio) {
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    fecha.set(year, month, dayOfMonth);
                    editText.setText(dateFormat.format(fecha.getTime()));

                    if (!esInicio && !etFechaInicio.getText().toString().isEmpty()) {
                        // Cuando se seleccione la fecha de fin, calculamos las semanas
                        calcularSemanas();
                    }
                },
                fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void calcularMontoTotal() {
        String precioStr = etPrecio.getText().toString();
        if (!precioStr.isEmpty()) {
            double precio = Double.parseDouble(precioStr);

            // Obtener la opción seleccionada
            String interesSeleccionado = spinnerInteres.getSelectedItem().toString();

            double montoTotal = precio; // Por defecto, el monto total es igual al precio

            if (!interesSeleccionado.equals("Sin interés")) {
                int porcentaje = Integer.parseInt(interesSeleccionado.replace("%", ""));
                montoTotal = precio + (precio * porcentaje / 100);
            }

            etMontoTotal.setText(String.valueOf(montoTotal));
        }
    }

    private void calcularSemanas() {
        String fechaInicioStr = etFechaInicio.getText().toString();
        String fechaFinStr = etFechaFin.getText().toString();

        if (!fechaInicioStr.isEmpty() && !fechaFinStr.isEmpty()) {
            try {
                Date fechaInicio = dateFormat.parse(fechaInicioStr);
                Date fechaFin = dateFormat.parse(fechaFinStr);

                int totalSemanas = calcularSemanas(fechaInicio, fechaFin);
                tvTotalSemanas.setText(String.valueOf(totalSemanas));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private int calcularSemanas(Date inicio, Date fin) {
        long diferencia = fin.getTime() - inicio.getTime();
        return (int) (diferencia / (1000 * 60 * 60 * 24 * 7)); // Convertir milisegundos a semanas
    }


    private void guardarDatos() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        String IMEI = etIMEI.getText().toString().trim();
        String marcaTelefono = spinnerMarca.getSelectedItem().toString();
        String Precio = etPrecio.getText().toString();
        String nombreCliente = etNombreCliente.getText().toString();
        String domicilio = etDomicilio.getText().toString();
        String edad = etEdad.getText().toString();
        String fechaInicio = etFechaInicio.getText().toString();
        String fechaFin = etFechaFin.getText().toString();
        String pagoSemanal = etPagoSemanal.getText().toString();
        // Obtener el interes
        String porcentajeInteres = spinnerInteres.getSelectedItem().toString();
        if (porcentajeInteres.equals("Sin interés")) {
            porcentajeInteres = "0%"; // Guarda 0% para indicar que no se aplicó interés
        }
        // Obtén las semanas calculadas
        int totalSemanas = Integer.parseInt(tvTotalSemanas.getText().toString()); // Convertir el texto a int
        String montoTotal = etMontoTotal.getText().toString();

        if (!esIMEIValido(IMEI)) {
            Toast.makeText(requireContext(), "El IMEI debe tener 15 dígitos numéricos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!marcaTelefono.isEmpty() && !Precio.isEmpty() && !nombreCliente.isEmpty() && !domicilio.isEmpty() && !edad.isEmpty() && !fechaInicio.isEmpty() && !pagoSemanal.isEmpty() && !montoTotal.isEmpty()) {
            String id = databaseReference.push().getKey();
            DispositivosRegistrados dispositivos = new DispositivosRegistrados(id, userId, IMEI, marcaTelefono, Precio, nombreCliente, domicilio, edad, fechaInicio,fechaFin, pagoSemanal, montoTotal, "activo", porcentajeInteres, totalSemanas);
            databaseReference.child(id).setValue(dispositivos);

            // Mostrar un mensaje de éxito
            Toast.makeText(getActivity(), "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();

            // Limpiar los campos del formulario
            etIMEI.setText("");
            etPrecio.setText("");
            etNombreCliente.setText("");
            etDomicilio.setText("");
            etEdad.setText("");
            etFechaInicio.setText("");
            etFechaFin.setText("");
            etPagoSemanal.setText("");
            tvTotalSemanas.setText("");  // Limpiar el campo de semanas
            etMontoTotal.setText("");

            // Volver al fragmento de inicio con commit()
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InicioFragment()) // Reemplaza el fragmento actual
                    .commit(); // Asegúrate de llamar a commit para que la transacción se complete
        } else {
            Toast.makeText(getActivity(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean esIMEIValido(String imei) {
        return imei.matches("\\d{15}");
    }
}