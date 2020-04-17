package jp.co.stv_tech.android_13_1

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lvCityList = findViewById<ListView>(R.id.lvCityList)

        val cityList: MutableList<MutableMap<String, String>> = mutableListOf()

        var city = mutableMapOf<String, String>("name" to "大阪", "id" to "27000")
        cityList.add(city)

        city = mutableMapOf<String, String>("name" to "神戸", "id" to "280010")
        cityList.add(city)

        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)

        val adapter = SimpleAdapter(
            applicationContext,
            cityList,
            android.R.layout.simple_expandable_list_item_1,
            from,
            to
        )
        lvCityList.adapter = adapter
        lvCityList.onItemClickListener = ListItemClickListener()
    }

    private inner class ListItemClickListener: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, positon: Int, id: Long) {

            val item = parent?.getItemAtPosition(positon) as Map<String, String>
            val city = item["city"]
            val cityName = item["cityName"]
            val cityId = item["id"]

            val tvCityName = findViewById<TextView>(R.id.tvCityName)
            tvCityName.setText(cityName + "の天気 : ")

            //WeaterInfoReceiverを実行
            val reviever = WeaterInfoReceiver()
            reviever.execute()
        }
    }

    private inner class WeaterInfoReceiver() : AsyncTask<String, String, String>() {

        //非同期処理を実行する（バックグラウンド処理）
        override fun doInBackground(vararg params: String?): String {

            val id = params[0]
            val urlStr = "http://weather.livedoor.com/forecast/webservice/json/v1?city=${id}"

            val url = URL(urlStr)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.connectTimeout = 1000
            con.connect()

            val stream = con.inputStream
            val result = is2String(stream)
            con.disconnect()
            stream.close()

            return result
        }

        //UIスレッドで実行する
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //JSONを解析する(パース）
            val rootJSON = JSONObject(result)
            val description = rootJSON.getJSONObject("description")
            val desc = description.getString("text")

            val forecasts = rootJSON.getJSONArray("forecasts")
            val forecastNow = forecasts.getJSONObject(0)
            val telop = forecastNow.getString("telop")

            val tvWeaterTelop = findViewById<TextView>(R.id.tvWeatherTelop)
            val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
            tvWeaterTelop.text = telop
            tvWeatherDesc.text = desc
        }

        //InputStreamを文字列に変換する
        private fun is2String(stream: InputStream): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()

            while(line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }
}
