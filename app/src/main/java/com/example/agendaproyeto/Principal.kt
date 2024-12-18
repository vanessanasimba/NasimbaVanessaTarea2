package com.example.agendaproyeto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnPersona: Button = findViewById(R.id.btn_persona)
        var btnContacto: Button = findViewById(R.id.btn_contacto)

        btnPersona.setOnClickListener{
            val intent:Intent= Intent(this, MainActivity:: class.java)
            startActivity(intent)
        }

        btnContacto.setOnClickListener{
            val intent:Intent= Intent(this, CrudContacto:: class.java)
            startActivity(intent)
        }


    }
}