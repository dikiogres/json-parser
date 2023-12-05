package com.example.jsonparser

import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var lv: ListView
    var contactList: ArrayList<HashMap<String, String?>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactList = ArrayList()
        lv = findViewById(R.id.listView)
        val service = Executors.newSingleThreadExecutor()
        val status = service.submit<Int> {
            val sh = HttpHandler()
            // Making a request to url and get a response
            val url =
                "https://gist.githubusercontent.com/Baalmart/8414268/raw/83e5ea9d1d88c765712f9392aa31c45e889f4f09/contacts"
            val jsonstr = sh.makeServiceCall(url)
            Log.e("Main", "Response from url: $jsonstr")
            if (jsonstr != null) {
                try {
                    val jsonobj = JSONObject(jsonstr)
                    // Get JSONArray node
                    val contacts = jsonobj.getJSONArray("contacts")
                    for (i in 0 until contacts.length()) {
                        val c = contacts.getJSONObject(i)
                        val id = c.getString("id")
                        val name = c.getString("name")
                        val email = c.getString("email")
                        val gender =
                            if (c.getString("gender") == "male") "Laki-laki" else "Perempuan"
                        val phone = c.getJSONObject("phone")
                        val p_mobile = phone.getString("mobile")
                        // tmp hashmap for single contact
                        val contact =
                            HashMap<String, String?>()
                        // adding each child node to hashmap
                        contact["id"] = id
                        contact["name"] = name
                        contact["email"] = email
                        contact["gender"] = gender
                        contact["mobile"] = p_mobile
                        contactList!!.add(contact)
                    }
                } catch (e: JSONException) {
                    Log.e("Main", "JSON Parsing Error: " + e.message)
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "JSON Parsing Errror",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Log.e("Main", "Couldn't get JSON from Server")
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Couldn't get JSON from Server. Check Logcat for possible errors!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@submit 0
            }
            1
        }
        try {
            if (status.get() == 1) {
                val adapter: ListAdapter = SimpleAdapter(
                    this@MainActivity,
                    contactList,
                    R.layout.item_contact,
                    arrayOf("id", "name", "email", "gender", "mobile"),
                    intArrayOf(R.id.id, R.id.nama, R.id.email, R.id.gender, R.id.mobile)
                )
                lv.setAdapter(adapter)
            }
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}