package com.example.appadministrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etContrasena, etConfirmarContrasena;
    private Button btnRegistrar, btnCancelar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            }
        );

        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(RegistroActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contrasena.equals(confirmarContrasena)) {
                Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser usuario = auth.getCurrentUser();
                            String userId = usuario.getUid();

                            // Mostrar mensaje de éxito
                            Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            // Guardar datos en Firestore
                            Map<String, Object> datosUsuario = new HashMap<>();
                            datosUsuario.put("nombre", nombre);
                            datosUsuario.put("correo", correo);

                            db.collection("Usuarios").document(userId).set(datosUsuario)
                                    .addOnSuccessListener(aVoid -> {
                                        // Asegúrate de que los datos se guarden correctamente
                                        Log.d("Firestore", "Datos guardados correctamente");

                                        // Redirigir al login
                                        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error al guardar datos: " + e.getMessage());
                                        Toast.makeText(RegistroActivity.this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.e("Registro", "Error en el registro: " + task.getException().getMessage());
                            Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}