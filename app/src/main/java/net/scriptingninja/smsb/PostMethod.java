package net.scriptingninja.smsb;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Created by michaelhabashy on 2/4/17.
 */

public class PostMethod extends AsyncTask<String, Integer, String> {


    private Config.OnTaskCompleted listener;
    private Map map = new HashMap();


    protected void setList(String k, Object v){
        map.put(k, v);
    };

    public String getStatus(String c) {

        String status = "";
        try {
            JSONObject jsonObject = new JSONObject(c);
            status = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }

    protected String queryString(){
        String sb = "";
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()){
            Object k   = iterator.next();
            Object v = map.get(k);
            sb += k.toString() + "=" + v.toString() + "&";
        }
        return sb.substring(0, sb.length() - 1);
    };

    public String getData(String c) {

        String data = "";
        try {
            JSONObject jsonObject = new JSONObject(c);
            data = jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String getValue(String c, String value){
        String data = "";
        try {
            JSONObject jsonObject = new JSONObject(c);
            data = jsonObject.getString(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void getCancel(){
        map.clear();
    }

    Config config = new Config();
    @Override
    protected String doInBackground(String... params) {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection = null;
        String param = queryString();
        byte[] postData;
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic "+ config.getBasicHttp());
            urlConnection.setInstanceFollowRedirects( false );
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput( true );
            //urlConnection.setRequestProperty("Accept", "application/json");
            //urlConnection.setRequestProperty("Content-type", "application/json");
            //urlConnection.setRequestProperty( "charset", "utf-8");
            //urlConnection.setRequestProperty("Content-Length", Integer.toString(param.length()));
           // postData =  queryString().getBytes(Charset.forName("UTF-8"));
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            write.write(param);
            Log.d("QUERY STRING", param);
            write.flush();
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            while ((inputLine = bin.readLine()) != null) {
                sb.append(inputLine);
            }
            os.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return sb.toString();
    }

}
