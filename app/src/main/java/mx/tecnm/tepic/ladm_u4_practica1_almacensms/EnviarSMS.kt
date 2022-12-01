package mx.tecnm.tepic.ladm_u4_practica1_almacensms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u4_practica1_almacensms.databinding.ActivityEnviarSmsBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class EnviarSMS : AppCompatActivity() {
    lateinit var binding : ActivityEnviarSmsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnviarSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)

        binding.btnEnviar.setOnClickListener {
            if (enviarSMS(binding.txtTelefono.text.toString(), binding.txtMensaje.text.toString())){
                registrarMensajeFireBase()
                limpiarCampos()
            }
        }

        binding.btnHistorial.setOnClickListener {
            val intent = Intent(binding.root.context, HistorialSMS::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private fun enviarSMS(tel: String, msg: String) : Boolean{
        try {
            val smsManager : SmsManager

            if (Build.VERSION.SDK_INT >= 23){
                smsManager = this.getSystemService(SmsManager::class.java)
            }else{
                smsManager = SmsManager.getDefault()
            }

            smsManager.sendTextMessage(tel, null, msg, null, null)
            Toast.makeText(this, "Alerta: Se envio con exito el mensaje.", Toast.LENGTH_LONG).show()
            return true
        }catch (e: Exception){
            Toast.makeText(this, "Error: No se pudo enviar el mensaje.", Toast.LENGTH_LONG).show()
            binding.txtConsola.setText(e.toString())
            return false
        }
    }

    private fun registrarMensajeFireBase(){
        val datos =
            hashMapOf(
                "telefono" to binding.txtTelefono.text.toString(),
                "mensaje" to binding.txtMensaje.text.toString(),
                "fecha" to Date().toString()
            )

        FirebaseFirestore.getInstance()
            .collection("smsenviados")
            .add(datos)
            .addOnSuccessListener {
                Toast.makeText(this, "Alerta: Se registro el candidato con exito", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
            }
    }

    private fun limpiarCampos(){
        binding.txtMensaje.setText("")
        binding.txtTelefono.setText("")
    }
}