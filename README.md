# newemployee_android_11
非同期処理とWeb API連携

## 非同期処理

```
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
 ```
 
 ### 関連メソッド
 
 | メソッド名　| 説明 |
 |---|---|
 | onPreExecute | doInBackground前にUIスレッド上で実行した処理を記載する|
 | doInBackground（必須） | 非同期で実施したい処理を記載する|
 | onProgressUpdate | ユーザにデータの読み込み状況を伝える処理を記載する。<br>doInBackground内でpublishProgresssを呼んだタイミングで実行される|
 | onPostExecute | 画面の表示など(UIスレッド） |

 
