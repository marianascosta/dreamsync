package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Layer (val id: String, val name: String){
    constructor(
        name: String
    ) : this(
        id = UUID.randomUUID().toString(),
        name = name)
}