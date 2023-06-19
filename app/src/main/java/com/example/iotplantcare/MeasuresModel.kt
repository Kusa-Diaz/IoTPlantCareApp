package com.example.iotplantcare

data class MeasuresModel (
    var humedadCapacitiva: Double? = null,
    var humedadGlobal: Int? = null,
    var plantID: String? = null,
    var plantName: String? = null,
    var temperaturaGlobal: Double? = null
)