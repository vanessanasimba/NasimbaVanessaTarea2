package com.example.agendaproyeto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class CrudContacto : AppCompatActivity() {
    var booleanGuardar = 0
    val codigos = ArrayList<String>()
    val codigoPersona = ArrayList<String>()
    lateinit var txtNombre: EditText
    lateinit var txtApellido: EditText
    lateinit var txtTelefono : EditText
    lateinit var txtMail: EditText
    lateinit var txtPersona: EditText
    lateinit var lista: ListView
    lateinit var btnGuardar : Button
    lateinit var btnActualizar : Button
    lateinit var btnEliminar : Button
    lateinit var btnCancelar : Button
    lateinit var btnNuevo : Button
    lateinit var txtCodigo : EditText

    lateinit var spnPersona : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_contacto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mapeo()
        limpiarFormulario()
        botonesDisable()
        textDisable()
        btnNuevo.isEnabled = true
        consultar()
        consultarPersona()

        lista.setOnItemClickListener{ adapterView, view,i, l->
            //Toast.makeText(applicationContext,codigos[i], Toast.LENGTH_SHORT).show()
            editar(codigos[i])
            botonesDisable()
            btnCancelar.isEnabled = true
            btnEliminar.isEnabled = true
            btnActualizar.isEnabled = true
        }
        btnNuevo.setOnClickListener{
            booleanGuardar = 1
            botonesDisable()
            textEnable()
            btnGuardar.isEnabled = true
            btnCancelar.isEnabled = true
        }
        btnActualizar.setOnClickListener{
            booleanGuardar = 2
            botonesDisable()
            textEnable()
            btnGuardar.isEnabled = true
            btnCancelar.isEnabled = true
            txtPersona.isEnabled = false
        }
        btnCancelar.setOnClickListener{
            limpiarFormulario()
            botonesDisable()
            textDisable()
            btnNuevo.isEnabled = true
        }

        btnEliminar.setOnClickListener{
            pregunta()
        }
        btnGuardar.setOnClickListener{
            /// guardar en la base de datos la informacion
            if (txtNombre.text.isEmpty()
                || txtApellido.text.toString().equals("")
                || txtTelefono.text.toString().equals("")
                || txtMail.text.toString().equals("")
                || txtPersona.text.toString().equals("")){
                Toast.makeText(applicationContext,"Faltan Datos", Toast.LENGTH_SHORT).show()

            }else {
                if (booleanGuardar == 1){
                    guardar()
                }
                if (booleanGuardar == 2){
                    actualizar()
                }
                limpiarFormulario()
                botonesDisable()
                textDisable()
                btnNuevo.isEnabled = true
            }

        }

    }

    fun mapeo(){
        txtNombre = findViewById(R.id.txt_nombre_contacto)
        txtApellido = findViewById(R.id.txt_apellido_contacto)
        txtTelefono = findViewById(R.id.txt_telefono_contacto)
        txtMail = findViewById(R.id.txt_mail_contacto)
        txtPersona = findViewById(R.id.txt_persona_contacto)
        lista = findViewById(R.id.lista_contacto)
        btnGuardar = findViewById(R.id.btn_guardar_contacto)
        btnActualizar = findViewById(R.id.btn_actualizar_contacto)
        btnEliminar = findViewById(R.id.btn_eliminar_contacto)
        btnCancelar = findViewById(R.id.btn_cancelar_contacto)
        btnNuevo = findViewById(R.id.btn_nuevo_contacto)
        txtCodigo = findViewById(R.id.txt_codigo_contacto)
        spnPersona = findViewById(R.id.spn_persona)

    }

    fun limpiarFormulario(){
        txtTelefono.setText("")
        txtNombre.setText("")
        txtApellido.setText("")
        txtMail.setText("")
        txtPersona.setText("")
        txtCodigo.setText("")
    }

    fun botonesDisable(){
        btnGuardar.isEnabled = false
        btnEliminar.isEnabled = false
        btnActualizar.isEnabled = false
        btnCancelar.isEnabled = false
        btnNuevo.isEnabled = false
    }

    fun textEnable(){
        txtTelefono.isEnabled = true
        txtNombre.isEnabled = true
        txtApellido.isEnabled = true
        txtPersona.isEnabled = true
        txtMail.isEnabled = true
    }

    fun textDisable(){
        txtTelefono.isEnabled = false
        txtNombre.isEnabled = false
        txtApellido.isEnabled = false
        txtPersona.isEnabled = false
        txtMail.isEnabled = false
        spnPersona.isEnabled = false
    }

    fun pregunta (){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ATENCION")
        builder.setMessage("Esta seguro de eliminar el registro")
        builder.setPositiveButton(android.R.string.ok){dialog, which ->
            eliminar()
            botonesDisable()
            limpiarFormulario()
            btnNuevo.isEnabled = true
        }
        builder.setNegativeButton(android.R.string.cancel){dialog, which ->
        }
        builder.show()
    }


    private fun consultar(){
        codigos.clear()
        val al = ArrayList<String>()
        al.clear()
        var url = "http://192.168.100.46:8080/Ws_agenda2024/contacto.php"
        val campos = JSONObject()
        campos.put("accion","consultar")
        val rq = Volley.newRequestQueue(this)
        val json= JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try {
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        val array = obj.getJSONArray("personas")
                        for(i in 0 .. array.length()-1){
                            val fila = array.getJSONObject(i)
                            al.add(fila.getString("nombre") + " " + fila.getString("apellido"))
                            codigos.add(fila.getString("codigo"))
                        }
                        val ad = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,al)
                        lista.adapter=ad
                        ad.notifyDataSetChanged()
                    }else{
                        Toast.makeText(applicationContext,obj.getString("mensaje").toString(),Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(applicationContext,e.toString(),Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()})
        rq.add(json)
    }

    private fun editar(codigo: String){
        val al = ArrayList<String>()
        txtCodigo.setText(codigo)
        //al.clear()
        var url = "http://192.168.100.46:8080/Ws_agenda2024/contacto.php"
        val campos = JSONObject()
        campos.put("accion","dato")
        campos.put("codigo",codigo)
        val rq = Volley.newRequestQueue(this)
        val json= JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        val dato = obj.getJSONObject("contacto")
                        txtNombre.setText(dato.getString("nombre"))
                        txtApellido.setText(dato.getString("apellido"))
                        txtTelefono.setText(dato.getString("telefono"))
                        txtMail.setText(dato.getString("correo"))
                        txtPersona.setText(dato.getString("persona"))
                    }

                }catch (e: JSONException){
                    Toast.makeText(applicationContext,e.toString(),Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()}
        )
        rq.add(json)
       // spnPersona.setSelection(1)
    }

    private fun actualizar(){
        var url = "http://192.168.100.46:8080/Ws_agenda2024/contacto.php"
        val campos = JSONObject()
        campos.put("accion","actualizar")
        campos.put("nombre",txtNombre.text.toString())
        campos.put("apellido",txtApellido.text.toString())
        campos.put("mail",txtMail.text.toString())
        campos.put("telefono",txtTelefono.text.toString())
        campos.put("codigo",txtCodigo.text.toString())
        val rq = Volley.newRequestQueue(this)
        val json = JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try{
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
                        consultar()
                    }else{
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(),
                        Toast.LENGTH_SHORT).show()
                }

            },  Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()})
        rq.add(json)
    }

    private fun guardar(){
        var url = "http://192.168.100.46:8080/Ws_agenda2024/contacto.php"
        val campos = JSONObject()
        campos.put("accion","insertar")
        campos.put("nombre",txtNombre.text.toString())
        campos.put("apellido",txtApellido.text.toString())
        campos.put("mail",txtMail.text.toString())
        campos.put("telefono",txtTelefono.text.toString())
        campos.put("persona",txtPersona.text.toString())
        val rq = Volley.newRequestQueue(this)
        val json = JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try{
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
                        consultar()
                    }else{
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(),
                        Toast.LENGTH_SHORT).show()
                }

            },  Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()})
        rq.add(json)
    }

    private fun eliminar(){

        Toast.makeText(applicationContext, txtCodigo.text.toString(),
            Toast.LENGTH_SHORT).show()
        var url = "http://192.168.100.46:8080/Ws_agenda2024/contacto.php"
        val campos = JSONObject()
        campos.put("accion","eliminar")
        campos.put("codigo",txtCodigo.text.toString())
        val rq = Volley.newRequestQueue(this)
        val json = JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try{
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
                        consultar()
                    }else{
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(applicationContext, e.toString(),
                        Toast.LENGTH_SHORT).show()
                }

            },  Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()})
        rq.add(json)
    }

    private fun consultarPersona(){
        codigoPersona.clear()
        val al = ArrayList<String>()
        al.clear()
        var url = "http://192.168.100.46:8080/Ws_agenda2024/persona.php"
        val campos = JSONObject()
        campos.put("accion","consultar")
        val rq = Volley.newRequestQueue(this)
        val json= JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try {
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        val array = obj.getJSONArray("personas")
                        for(i in 0 .. array.length()-1){
                            val fila = array.getJSONObject(i)
                            al.add(fila.getString("codigo") + " " +fila.getString("nombre") + " " + fila.getString("apellido"))
                            codigoPersona.add(fila.getString("codigo"))
                        }

                        val ad = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,al)
                        spnPersona.adapter=ad
                        ad.notifyDataSetChanged()
                    }else{
                        Toast.makeText(applicationContext,obj.getString("mensaje").toString(),Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(applicationContext,e.toString(),Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()})
        rq.add(json)
    }

}