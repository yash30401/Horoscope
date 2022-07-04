package com.devyash.horoscope



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*



class MainActivity : AppCompatActivity() {

    var sunSign= "Aries"

    var resultView:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val spSign=findViewById<Spinner>(R.id.spSign)
        val signs = resources.getStringArray(R.array.sign)
        resultView=findViewById(R.id.tvSign)

        if (spSign != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item,signs)
            spSign.adapter = adapter

            spSign.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    sunSign=parent.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        var buttonView: Button = findViewById(R.id.btGetResult)
        buttonView.setOnClickListener {

            GlobalScope.async {
                getPredictions(buttonView)
            }
        }




    }

    private suspend fun getPredictions(view: android.view.View) {

        try {

            val result=GlobalScope.async {
                callAztroAPI("https://aztro.sameerkumar.website?sign="+sunSign+"&day=today")

            }.await()

            onResponse(result)

        }catch (e: Exception){
            e.printStackTrace()
        }

    }



    private fun callAztroAPI(apiUrl: String):String? {

        var result:String?=""
        val url:URL
        var connection:HttpURLConnection?=null

        try{

            url=URL(apiUrl)
            connection=url.openConnection() as HttpURLConnection

            connection.setRequestProperty("X-RapidAPI-Host", "sameer-kumar-aztro-v1.p.rapidapi.com")
            // set the rapid-api key
            connection.setRequestProperty("X-RapidAPI-Key", "<xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx>")

            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded")

            connection.requestMethod = "POST"

            val r = connection.inputStream
            val reader = InputStreamReader(r)

            var data=reader.read()
            while(data!=-1){
                val current=data.toChar()
                result+=current
                data=reader.read()
            }

            Log.d("yes","YO")
            return result

        }catch (e:Exception){
            e.printStackTrace()
        }



        return null
    }

    private fun onResponse(result: String?) {

        try {
            // convert the string to JSON object for better reading
            val resultJson = JSONObject(result)
            // Initialize prediction text
            var prediction ="Today's prediction \n"
            prediction += this.sunSign+"\n"
            // Update text with various fields from response
            prediction += resultJson.getString("date_range")+"\n"
            prediction += resultJson.getString("description")
            //Update the prediction to the view

            setText(this.resultView,prediction)

        } catch (e: Exception) {
            e.printStackTrace()
            this.resultView!!.text = "Oops!! something went wrong, please try again"
        }

    }

    private fun setText(text:TextView?,value: String) {

        runOnUiThread{text!!.text=value}
        Log.d("Text",value)

    }

}
