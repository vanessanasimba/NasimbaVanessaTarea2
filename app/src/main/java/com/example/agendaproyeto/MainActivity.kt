package com.example.agendaproyeto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject



class MainActivity : AppCompatActivity() {

    var booleanGuardar = 0
    val codigos = ArrayList<String>()
    lateinit var txtCedula: EditText
    lateinit var txtNombre: EditText
    lateinit var txtApellido: EditText
    lateinit var txtClave : EditText
    lateinit var txtMail: EditText
    lateinit var lista: ListView
    lateinit var btnGuardar : Button
    lateinit var btnConsultar : Button
    lateinit var btnActualizar : Button
    lateinit var btnEliminar : Button
    lateinit var btnLimpiar : Button
    lateinit var btnNuevo : Button
    lateinit var txtCodigo : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapeo()
        limpiarFormulario()
        botonesDisable()
        textDisable()
        btnNuevo.isEnabled = true
        consultar()



        btnNuevo.setOnClickListener{
            booleanGuardar = 1
            botonesDisable()
            textEnable()
            btnGuardar.isEnabled = true
            btnLimpiar.isEnabled = true
        }

        lista.setOnItemClickListener{ adapterView, view,i, l->
            //Toast.makeText(applicationContext,codigos[i], Toast.LENGTH_SHORT).show()
            editar(codigos[i])
            botonesDisable()
            btnLimpiar.isEnabled = true
            btnEliminar.isEnabled = true
            btnActualizar.isEnabled = true
        }

        btnGuardar.setOnClickListener{
            /// guardar en la base de datos la informacion
            if (txtCedula.text.isEmpty()
                || txtNombre.text.toString().equals("")
                || txtApellido.text.toString().equals("")
                || txtClave.text.toString().equals("")
                || txtMail.text.toString().equals("")){
                Toast.makeText(applicationContext,"Faltan Datos", Toast.LENGTH_SHORT).show()

            }else {
                if (booleanGuardar == 1){
                    guardar()
                }
                if (booleanGuardar == 2){
                    actualizar()
                }
                consultar()
                limpiarFormulario()
                botonesDisable()
                textDisable()
                btnNuevo.isEnabled = true
            }

        }

        btnActualizar.setOnClickListener{
            booleanGuardar = 2
            botonesDisable()
            textEnable()
            btnGuardar.isEnabled = true
            btnLimpiar.isEnabled = true
        }

        btnLimpiar.setOnClickListener{
            limpiarFormulario()
            botonesDisable()
            textDisable()
            btnNuevo.isEnabled = true
        }
        btnEliminar.setOnClickListener{
            eliminar()
            botonesDisable()
            limpiarFormulario()
            btnNuevo.isEnabled = true
        }

        txtCedula.setOnFocusChangeListener{view, b->
            if(!b){
                vcedula(txtCedula.text.toString())
            }

        }



    }
    private fun editar(codigo: String){
        val al = ArrayList<String>()
        txtCodigo.setText(codigo)
        //al.clear()
        var url = "http://192.168.100.46:8080/Ws_agenda2024/persona.php"
        val campos = JSONObject()
        campos.put("accion","dato")
        campos.put("codigo",codigo)
        val rq = Volley.newRequestQueue(this)
        val json= JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try {
                    val obj = (s)
                    if(obj.getBoolean("estado")){
                        val dato = obj.getJSONObject("persona")
                        txtCedula.setText(dato.getString("cedula"))
                        txtNombre.setText(dato.getString("nombre"))
                        txtApellido.setText(dato.getString("apellido"))
                        txtClave.setText(dato.getString("clave"))
                        txtMail.setText(dato.getString("correo"))
                    }

                }catch (e: JSONException){
                    Toast.makeText(applicationContext,e.toString(),Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener{volleyError-> Toast.makeText(applicationContext,volleyError.message,Toast.LENGTH_SHORT).show()}
        )
        rq.add(json)
    }






    fun limpiarFormulario(){
        txtCedula.setText("")
        txtNombre.setText("")
        txtApellido.setText("")
        txtMail.setText("")
        txtClave.setText("")
        txtCodigo.setText("")
    }

    fun botonesDisable(){
        btnGuardar.isEnabled = false
        btnEliminar.isEnabled = false
        btnConsultar.isEnabled = false
        btnActualizar.isEnabled = false
        btnLimpiar.isEnabled = false
        btnNuevo.isEnabled = false
    }

    fun textEnable(){
        txtCedula.isEnabled = true
        txtNombre.isEnabled = true
        txtApellido.isEnabled = true
        txtClave.isEnabled = true
        txtMail.isEnabled = true
    }

    fun textDisable(){
        txtCedula.isEnabled = false
        txtNombre.isEnabled = false
        txtApellido.isEnabled = false
        txtClave.isEnabled = false
        txtMail.isEnabled = false
    }

    fun mapeo(){
        txtCedula = findViewById(R.id.txt_cedula)
        txtNombre = findViewById(R.id.txt_nombre)
        txtApellido = findViewById(R.id.txt_apellido)
        txtClave = findViewById(R.id.txt_clave)
        txtMail = findViewById(R.id.txt_correo)
        lista = findViewById(R.id.lista)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnConsultar= findViewById(R.id.btn_consular)
        btnActualizar = findViewById(R.id.btn_actualizar)
        btnEliminar = findViewById(R.id.btn_eliminar)
        btnLimpiar = findViewById(R.id.btn_limpiar)
        btnNuevo = findViewById(R.id.btn_nuevo)
        txtCodigo = findViewById(R.id.txt_codigo)

    }

    private fun consultar(){
        codigos.clear()
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
                            al.add(fila.getString("cedula")+ " " + fila.getString("nombre") + " " + fila.getString("apellido"))
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

    private fun guardar(){
        var url = "http://192.168.100.46:8080/Ws_agenda2024/persona.php"
        val campos = JSONObject()
        campos.put("accion","insertar")
        campos.put("cedula",txtCedula.text.toString())
        campos.put("nombre",txtNombre.text.toString())
        campos.put("apellido",txtApellido.text.toString())
        campos.put("clave",txtClave.text.toString())
        campos.put("mail",txtMail.text.toString())
        val rq = Volley.newRequestQueue(this)
        val json = JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try{
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
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

    private fun actualizar(){
        var url = "http://192.168.100.46:8080/Ws_agenda2024/persona.php"
        val campos = JSONObject()
        campos.put("accion","actualizar")
        campos.put("cedula",txtCedula.text.toString())
        campos.put("nombre",txtNombre.text.toString())
        campos.put("apellido",txtApellido.text.toString())
        campos.put("clave",txtClave.text.toString())
        campos.put("mail",txtMail.text.toString())
        campos.put("codigo",txtCodigo.text.toString())
        val rq = Volley.newRequestQueue(this)
        val json = JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try{
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
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
        var url = "http://192.168.100.46:8080/Ws_agenda2024/persona.php"
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

    fun pregunta(){

    }

    private fun vcedula(cedula:String){
        var url = "http://192.168.100.46:8080/Ws_agenda2024/persona.php"
        val campos = JSONObject()
        campos.put("accion","vcedula")
        campos.put("cedula",txtCedula.text.toString())
        val rq = Volley.newRequestQueue(this)
        val json = JsonObjectRequest(Request.Method.POST, url,campos,
            Response.Listener { s->
                try{
                    val obj = (s)
                    if (obj.getBoolean("estado")){
                        Toast.makeText(applicationContext, obj.getString("mensaje").toString(),
                            Toast.LENGTH_SHORT).show()
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



}

