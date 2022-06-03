package com.example.lab9_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Don't forget to add to manifest.xml
 *     <uses-permission android:name="android.permission.INTERNET" />
 */

public class MainActivity extends AppCompatActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextNumber);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncCaller asyncCaller = new AsyncCaller();
                asyncCaller.execute();
            }
        });
    }

    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private class AsyncCaller extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            String response = getJSON("https://jsonplaceholder.typicode.com/users/" + editText.getText().toString(), 1000);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                Logger.getLogger("LABADABADA").log(Level.INFO, jsonObject.getString("name"));
                ArrayList<String> data = new ArrayList<String>();
                data.add(jsonObject.getString("name"));
                Logger.getLogger("LABADABADA").log(Level.INFO, jsonObject.getString("email"));
                data.add(jsonObject.getString("email"));
                Logger.getLogger("LABADABADA").log(Level.INFO, jsonObject.getString("phone"));
                data.add(jsonObject.getString("phone"));
                return data;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            TextView nameText = (TextView)findViewById(R.id.textName);
            nameText.setText(strings.get(0));
            TextView emailText = (TextView)findViewById(R.id.textEmail);
            emailText.setText(strings.get(1));
            TextView phoneText = (TextView)findViewById(R.id.textPhone);
            phoneText.setText(strings.get(2));
        }
    }
}