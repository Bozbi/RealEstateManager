package com.sbizzera.real_estate_manager.data.model

enum class PropertyType (val typeName : String){
    HOUSE("House"),
    FLAT("Flat"),
    FIELD("Field"),
    MANSION("Mansion")
}

fun getTypeNameList():List<String>{
    val listToReturn = mutableListOf<String>()
    PropertyType.values().forEach {
        listToReturn.add(it.typeName)
    }
    return listToReturn
}