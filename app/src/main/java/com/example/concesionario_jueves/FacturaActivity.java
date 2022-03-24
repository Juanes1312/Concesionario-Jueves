package com.example.concesionario_jueves;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FacturaActivity extends AppCompatActivity {

    EditText jetcodigo,jetfecha,jetidentificacion,jetplaca;
    Button jbtguardar, jbtconsultar, jbtanular, jbtcancelar, jbtregresar;
    long resp, sw;
    String codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        getSupportActionBar().hide();
        jetcodigo = findViewById(R.id.etcodigo);
        jetfecha  = findViewById(R.id.etfecha);
        jetidentificacion  = findViewById(R.id.etidentificacion);
        jetplaca = findViewById(R.id.etplaca);

        jbtguardar = findViewById(R.id.btguardar);
        jbtanular = findViewById(R.id.btanular);
        jbtcancelar = findViewById(R.id.btcancelar);
        jbtconsultar = findViewById(R.id.btconsultar);
        jbtregresar = findViewById(R.id.btregresar);
    }

    public void Guardar(View view) {
        String fecha, identificacion, placa;
        codigo = jetcodigo.getText().toString();
        fecha = jetfecha.getText().toString();
        identificacion = jetidentificacion.getText().toString();
        placa = jetplaca.getText().toString();

        if (codigo.isEmpty() || fecha.isEmpty() || identificacion.isEmpty() || placa.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        } else {
            Conexion_concesionario admin = new Conexion_concesionario(this, "concesionario5.bd", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("codFactura", codigo);
            registro.put("fecha", fecha);
            registro.put("identificacion", identificacion);
            registro.put("placa", placa);
            ConsultarFactura();
            if (sw == 1) {
                sw = 0;
                resp = db.update("TblFactura", registro, "codFactura='" + codigo + "'", null);
            } else {
                resp = db.insert("TblFactura", null, registro);
            }

            if (resp > 0) {
                Limpiar_campos();
                Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error en guardar el registro", Toast.LENGTH_SHORT).show();
            }
               db.close();
        }

    }

    public void Consulta_factura(View view) {
        ConsultarFactura();
    }

    public void ConsultarFactura() {

        codigo = jetcodigo.getText().toString();
        if (codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es requerido para buscar", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        } else {
            Conexion_concesionario admin = new Conexion_concesionario(this, "concesionario5.bd", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select * from TblFactura where codFactura='" + codigo + "'", null);
            if (fila.moveToNext()) {
                sw = 1;
                jetfecha.setText(fila.getString(1));
                jetidentificacion.setText(fila.getString(2));
                jetplaca.setText(fila.getString(3));
            } else {
                Toast.makeText(this, "Registro no hallado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }

    public void AnularFactura(View view) {
        ConsultarFactura();
        if (sw == 1) {
            Conexion_concesionario admin = new Conexion_concesionario(this, "concesionario5.bd", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("codFactura", codigo);
            registro.put("activo", "no");
            resp = db.update("TblFactura", registro, "codFactura='" + codigo + "'", null);
            if (resp > 0) {
                Toast.makeText(this, "Registro Anulado", Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            } else {
                Toast.makeText(this, "Error al anular el registro", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "La factura no esta registrada", Toast.LENGTH_SHORT).show();
        }
    }

    public void Limpiar_campos() {
        sw = 0;
        jetcodigo.setText("");
        jetfecha.setText("");
        jetidentificacion.setText("");
        jetplaca.setText("");
        jetcodigo.requestFocus();
    }

    public void Regresar(View view) {
        Intent main = new Intent(this, MenuActivity.class);
        startActivity(main);
    }

    public void Cancelar(View view){
        Limpiar_campos();
    }
}