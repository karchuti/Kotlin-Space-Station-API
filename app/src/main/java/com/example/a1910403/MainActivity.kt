package com.example.a1910403
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creating a view list to allow the user to make different json calls

        val values = arrayOf("Latitude", "Longitude", "Altitude")

        val mListView = findViewById<ListView>(R.id.listView)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values)

        mListView.adapter =adapter

        mListView.setOnItemClickListener{parent, view, position, id ->

            if (position==0){
                fetchData("https://api.wheretheiss.at/v1/satellites/25544",0)
            }

            if (position==1){
                fetchData("https://api.wheretheiss.at/v1/satellites/25544",1)
            }
            if (position==2){
                fetchData("https://api.wheretheiss.at/v1/satellites/25544",2)
            }
        }
    }

    private fun processSatJson(jsonString: String, position: Int): String {
        val satJsonObject = JSONObject(jsonString)
        var satInfo = ""

        if (position == 0){
            satInfo = "Latitude: " + satJsonObject.getString("latitude")
        }


        if(position == 1){
            satInfo = "Longitude: " + satJsonObject.getString("longitude")
        }
        if(position == 2){
            satInfo = "Altitude: " + satJsonObject.getString("altitude")
        }
        return satInfo
    }

    private fun updateTextView(text: String) {
        runOnUiThread {
            textView.text = text
        }
    }

    private fun fetchData(urlString: String, position : Int) {
        var ready = true
        if (ready) {
            ready = false
            listView.alpha = 0.25F
            val thread = Thread {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection()
                    if (connection is HttpURLConnection) {
                        connection.connectTimeout = 3000
                        connection.readTimeout = 3000
                        connection.requestMethod = "GET"
                        connection.connect()
                        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                            val scanner = Scanner(connection.inputStream).useDelimiter("\\A")
                            val scannerInput = if (scanner.hasNext()) scanner.next() else ""
                            val sat = processSatJson(scannerInput,position)
                            updateTextView(sat)
                            ready = true
                            listView.alpha = 1F
                        } else {
                            updateTextView("The server returned an error:")
                            ready = true
                            listView.alpha = 1F
                        }
                    }
                } catch (e: IOException) {
                    updateTextView("An error occurred retrieving data from the server")
                    ready = true
                    listView.alpha = 1F
                }
            }
            thread.start()
        }
    }
}
