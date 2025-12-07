package com.mangaproject.data.local

import androidx.datastore.core.Serializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mangaproject.data.model.JikanManga
import java.io.InputStream
import java.io.OutputStream

object TrendingSerializer : Serializer<List<JikanManga>> {

    private val gson = Gson()
    private val type = object : TypeToken<List<JikanManga>>() {}.type

    override val defaultValue: List<JikanManga> = emptyList()

    override suspend fun readFrom(input: InputStream): List<JikanManga> {
        return try {
            val json = input.readBytes().decodeToString()
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun writeTo(t: List<JikanManga>, output: OutputStream) {
        val json = gson.toJson(t, type)
        output.write(json.toByteArray())
    }
}
