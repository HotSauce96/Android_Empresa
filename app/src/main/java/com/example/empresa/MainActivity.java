package com.example.empresa;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity<jetusuario, jetnombre, jetcorreo, jetclave, jcbactivo, rq> extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //Ocultar la barra de menu por defecto
    getSupportActionBar().hide();
    //Libreria para el manejo de vistas (fragment)
    FragmentManager fm = getSupportFragmentManager();
//fm.beginTransaction().replace(R.id.Escenario,new IngresoFragment()).commit();


    EditText jetusuario,jetnombre,jetcorreo,jetclave;
    CheckBox jcbactivo;
    RequestQueue rq;
    JsonRequest jrq;
    String usr,nombre,correo,clave;

    //getSupportActionBar().hide();
    jetusuario=findViewById(R.id.etusuario);
    jetnombre=findViewById(R.id.etnombre);
    jetcorreo=findViewById(R.id.etcorreo);
    jetclave=findViewById(R.id.etclave);
    jcbactivo=findViewById(R.id.cbactivo);
    rq= Volley.newRequestQueue(this);

    implements Response.Listener<JSONObject>,Response.ErrorListener

    public void Consultar(View view) {
        usr = jetusuario.getText().toString();
        if (usr.isEmpty()) {
            Toast.makeText(this, "Usuario es requerido", Toast.LENGTH_SHORT).show();
            jetusuario.requestFocus();
        } else {
            String url = "http://172.16.60.42:80/WebServices/consulta.php?usr=" + usr;
            jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            rq.add(jrq);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error, usuario no registrado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject(0);//posicion 0 del arreglo....
            jetnombre.setText(jsonObject.optString("nombre"));
            jetcorreo.setText(jsonObject.optString("correo"));
            jetclave.setText(jsonObject.optString("clave"));
            if (jsonObject.optString("activo").equals("si"))
                jcbactivo.setChecked(true);
            else
                jcbactivo.setChecked(false);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void Guardar(View view){
        usr=jetusuario.getText().toString();
        nombre=jetnombre.getText().toString();
        correo=jetcorreo.getText().toString();
        clave=jetclave.getText().toString();
        if (usr.isEmpty() || nombre.isEmpty() || correo.isEmpty() || clave.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetusuario.requestFocus();
        }
        else{
            String url = "http://172.16.60.42:80/WebServices/registrocorreo.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            Limpiar_campos();
                            Toast.makeText(getApplicationContext(), "Registro de usuario realizado correctamente!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Registro de usuario incorrecto!", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("usr",jetusuario.getText().toString().trim());
                    params.put("nombre", jetnombre.getText().toString().trim());
                    params.put("correo",jetcorreo.getText().toString().trim());
                    params.put("clave",jetclave.getText().toString().trim());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        }
    }

    public void Limpiar(View view){
        Limpiar_campos();
    }

    public void Regresar(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

    private void Limpiar_campos(){
        jetusuario.setText("");
        jetclave.setText("");
        jetcorreo.setText("");
        jetnombre.setText("");
        jcbactivo.setChecked(false);
        jetusuario.requestFocus();
    }
}