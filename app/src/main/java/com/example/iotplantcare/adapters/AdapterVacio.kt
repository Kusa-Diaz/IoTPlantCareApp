package com.example.iotplantcare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iotplantcare.MeasuresModel
import com.example.iotplantcare.R


class AdapterVacio (private val plantList: ArrayList<MeasuresModel>): RecyclerView.Adapter<AdapterVacio.ViewHolder>(){
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):ViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycleritem, parent, false)
        return ViewHolder(itemView, mListener)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener){
        mListener = clickListener
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int){
        val currentPlant = plantList[position]
        holder.txtPlantName.text = currentPlant.plantName
        holder.txtPlantID.text = currentPlant.plantID
    }

    override fun getItemCount():Int{
        return plantList.size
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        val txtPlantName : TextView = itemView.findViewById(R.id.txtPlantName)
        val txtPlantID: TextView = itemView.findViewById(R.id.txtPlantID)

        init {
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)

            }
        }
    }



}