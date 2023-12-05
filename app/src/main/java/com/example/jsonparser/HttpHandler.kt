package com.example.jsonparser

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL


class HttpHandler {
    fun makeServiceCall(reqUrl: String?): String? {
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // read the response
            val `in`: InputStream = BufferedInputStream(conn.inputStream)
            response = convertStreamtoString(`in`)
        } catch (e: MalformedURLException) {
            Log.e("httpHandler", "MalformedURLException: " + e.message)
        } catch (e: ProtocolException) {
            Log.e("httpHandler", "ProtocolException: " + e.message)
        } catch (e: IOException) {
            Log.e("httpHandler", "IOException: " + e.message)
        } catch (e: Exception) {
            Log.e("httpHandler", "Exception: " + e.message)
        }
        return response
    }

    private fun convertStreamtoString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}