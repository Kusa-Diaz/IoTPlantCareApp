package com.example.iotplantcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotplantcare.adapters.AdapterVacio
import com.google.firebase.database.*

class PlantRecycler : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerVacio: RecyclerView
    private lateinit var txtCargando: TextView
    private lateinit var plantListVacia: ArrayList<MeasuresModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_recycler)
        dbRef = FirebaseDatabase.getInstance().getReference("Measures")

        recyclerVacio = findViewById(R.id.recyclerPlantas)
        recyclerVacio.layoutManager = LinearLayoutManager(this)
        recyclerVacio.setHasFixedSize(true)
        txtCargando = findViewById(R.id.txtCargarDatos)

        plantListVacia = arrayListOf<MeasuresModel>()
        getPlantData()
    }

    private fun getPlantData(){
        recyclerVacio.visibility = View.GONE
        txtCargando.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("/datos/Romero/")


        dbRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                plantListVacia.clear()
                if (snapshot.exists()){
                    for(empSnap in snapshot.children){
                        val measuresData = empSnap.getValue(MeasuresModel::class.java)
                        plantListVacia.add(measuresData!!)
                    }

                    val mAdapter = AdapterVacio(plantListVacia)

                    mAdapter.setOnItemClickListener(object : AdapterVacio.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@PlantRecycler, PlantStatusActivity::class.java)
                            intent.putExtra("position", position)
                            startActivity(intent)
                        }

                    })
                    recyclerVacio.adapter = mAdapter
                    txtCargando.visibility = View.GONE
                    recyclerVacio.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}