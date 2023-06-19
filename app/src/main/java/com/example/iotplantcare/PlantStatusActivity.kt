package com.example.iotplantcare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*

class PlantStatusActivity : AppCompatActivity() {

    private lateinit var txtPlantName: TextView
    private lateinit var txtHumedadGlobal: TextView
    private lateinit var txtTemperaturaGlobal: TextView
    private lateinit var txtHumedadCapacitiva: TextView
    private lateinit var cvHC: CardView
    private lateinit var cvTG: CardView

    private lateinit var dbRef: DatabaseReference

    companion object{
        const val PLANT_CHANNEL_ID = "plantIoTID"
    }

    private val plantListVacia = mutableListOf<MeasuresModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_status)
        initView()
        dbRef = FirebaseDatabase.getInstance().getReference("/datos/Romero")
        createChannel()
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                plantListVacia.clear()
                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        val measuresData = empSnap.getValue(MeasuresModel::class.java)
                        plantListVacia.add(measuresData!!)
                    }
                    setValuesToViews(plantListVacia)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }

    private fun initView() {
        cvHC = findViewById(R.id.cvHC)
        cvTG = findViewById(R.id.cvTG)
        txtPlantName = findViewById(R.id.txtPlantNameStatus)
        txtHumedadGlobal = findViewById(R.id.txtHumedadGlobal)
        txtTemperaturaGlobal = findViewById(R.id.txtTemperaturaGlobal)
        txtHumedadCapacitiva = findViewById(R.id.txtHumedadCapacitiva)
    }

    private fun setValuesToViews(plantListVacia: MutableList<MeasuresModel>) {
        Log.i("PlantStatusActivity", plantListVacia.toString())
        val postion : Int = intent.getStringExtra("position")?.toInt() ?: 0
        txtPlantName.text = plantListVacia[postion].plantName
        txtHumedadGlobal.text = plantListVacia[postion].humedadGlobal.toString() + "%"
        txtTemperaturaGlobal.text = plantListVacia[postion].temperaturaGlobal.toString() + " °C"
        txtHumedadCapacitiva.text = plantListVacia[postion].humedadCapacitiva.toString() + "%"

        validateHCStatus(plantListVacia[postion].humedadCapacitiva)
        validateTGStatus(plantListVacia[postion].temperaturaGlobal)
        createNotification(plantListVacia, postion)

    }

    private fun validateHCStatus(humedadCapacitiva: Double?){
        if (humedadCapacitiva!! < 60){
            cvHC.setCardBackgroundColor(Color.parseColor("#990000"))
        }
        else if (humedadCapacitiva!! >= 80){
            cvHC.setCardBackgroundColor(Color.parseColor("#07446f"))
        }
        else{
            cvHC.setCardBackgroundColor(Color.parseColor("#7fffd8"))
        }
    }

    private fun validateTGStatus(temperaturaGlobal: Double?){

        if (temperaturaGlobal!! < 20){
            cvTG.setCardBackgroundColor(Color.parseColor("#07446f"))
        }
        else if (temperaturaGlobal!! >= 34){
            cvTG.setCardBackgroundColor(Color.parseColor("#990000"))
        }
        else{
            cvTG.setCardBackgroundColor(Color.parseColor("#7fffd8"))
        }
    }

    private fun createNotification(plantListVacia: MutableList<MeasuresModel>,  position : Int){
        val intent = Intent(this, PlantStatusActivity::class.java).apply {
            //Con esto evitamos abrir mas de una instacia
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val PendingIntent:PendingIntent = PendingIntent.getActivity(this, 0,intent,flag)

        var message = selectNotificationMessage(plantListVacia, position)

        var builder = NotificationCompat.Builder(this, PLANT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Tu planta " + plantListVacia[position].plantName + " Necesita tu atención")
            .setContentText("Se ha dectectado una situación amenazante!")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(PendingIntent)
        with(NotificationManagerCompat.from(this)){
            notify(1,builder.build())
        }
    }

    private fun selectNotificationMessage(plantListVacia: MutableList<MeasuresModel>,  position : Int): String {
        var message = ""
        if (plantListVacia[position].humedadCapacitiva!! < 60){
            message = "Se ha registrado un nivel de humedad del " +
                    plantListVacia[position].humedadCapacitiva.toString() +
                    "% Por favor ve y riega tu " + plantListVacia[position].plantName + "!!"
        }
        else if (plantListVacia[position].humedadCapacitiva!! >= 80){
            message = "Se ha registrado un nivel de humedad del " +
                    plantListVacia[position].humedadCapacitiva.toString() +
                    "% Por favor NO rieges mas tu " + plantListVacia[position].plantName + "!!"
        }

        if (plantListVacia[position].temperaturaGlobal!! < 20){
            message = "Se ha registrado una temperatura de " +
                    plantListVacia[position].temperaturaGlobal.toString() +
                    "°C Por favor verifica si le falta sol a tu " + plantListVacia[position].plantName + "!!"
        }
        else if (plantListVacia[position].temperaturaGlobal!! >= 31){
            message = "Se ha registrado una temperatura de " +
                    plantListVacia[position].temperaturaGlobal.toString() +
                    "°C Por favor verifica si es DEMASIADO Sol para tu " + plantListVacia[position].plantName + "!!"
        }
        return message
    }
    private fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                PLANT_CHANNEL_ID,
                "IoTPlantCareChannel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "IOTPLANTNOTIFICATION"
            }
            val NotificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NotificationManager.createNotificationChannel(channel)
        }
    }
}