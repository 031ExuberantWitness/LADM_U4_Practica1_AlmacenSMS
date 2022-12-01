package mx.tecnm.tepic.ladm_u4_practica1_almacensms

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u4_practica1_almacensms.databinding.ActivityHistorialSmsBinding

class HistorialSMS : AppCompatActivity() {
    lateinit var binding : ActivityHistorialSmsBinding
    val listaIds = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mostrarHistoorialSMS()

        binding.btnRegresar.setOnClickListener {
            val intent = Intent(binding.root.context, EnviarSMS::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
        }
    }

    private fun mostrarHistoorialSMS() {
        FirebaseFirestore.getInstance()
            .collection("smsenviados")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    alerta("No se pudo realizar la consulta $error")
                    return@addSnapshotListener
                }
                val lista = ArrayList<String>()
                listaIds.clear()
                for (documento in value!!) {
                    lista.add(
                        "${documento.getString("telefono")} -- " +
                        "${documento.get("fecha").toString()}\n" +
                        "${documento.getString("mensaje")}\n"
                    )
                    listaIds.add(documento.id)
                }
                binding.listaMensajes.adapter = ArrayAdapter<String>(
                    this, android.R.layout
                        .simple_list_item_1, lista
                )
            }
    }

    private fun mostrarHistorial() {
        val lista = ArrayList<String>()
        FirebaseFirestore.getInstance()
            .collection("smsenviados")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    alerta("No se pudo realizar la consulta $error")
                    return@addSnapshotListener
                }

                listaIds.clear()
                for (documento in value!!) {
                    lista.add(
                        "${documento.getString("telefono")}\n" +
                                "${documento.getString("mensaje")}\n" +
                                "${documento.get("fecha")}\n"
                    )
                    listaIds.add(documento.id)
                }
                binding.listaMensajes.adapter = ArrayAdapter<String>(
                    this, R.layout
                        .simple_list_item_1, lista
                )
            }

        binding.listaMensajes.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_list_item_1, lista
        )
    }

    fun toast(m: String) {
        Toast.makeText(this, m, Toast.LENGTH_LONG).show()
    }

    fun alerta(m: String) {
        AlertDialog.Builder(this).setTitle("ATENCIÓN").setMessage(m)
            .setPositiveButton("OK") { d, i -> }.show()
    }
}