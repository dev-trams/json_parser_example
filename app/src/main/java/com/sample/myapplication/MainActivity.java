package com.sample.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "jvak.dothome.co.kr";
    private static String TAG = "phptest";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;
    private ArrayList<PersonalData> mArrayList;
    private UserAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditTextSearchKeyword;
    private String mJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView) findViewById(R.id.textview_main_result);
        mRecyclerView = (RecyclerView) findViewById(R.id.listview_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());


        mArrayList = new ArrayList<>();

        mAdapter = new UserAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        Button button_all = (Button) findViewById(R.id.button_main_all);
        // button_all ??? ????????? GetData|AsyncTask ??? ????????????.
        button_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                // ??????????????? ???????????? ???????????? ????????? IP ????????? PHP ????????? ????????? ??????
                task.execute("http://" + IP_ADDRESS + "/main/getjson.php", "");
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // ????????? ?????? ?????? ?????????????????? ???????????? ????????? JSON ???????????? ???????????? ????????? ???????????? showResult()???????????? ????????????.
            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {
                mTextViewResult.setText(errorString);
            } else {
                mJsonString = result;
                showResult(); // json ???????????? ???????????? ????????? ????????? ?????????
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //TODO ????????? ?????? PHP ????????? ??????????????? ????????? ???????????? ??????????????? ???????????? ???????????? ??????

            //PHP????????? ???????????? ??? ?????? ????????? ????????? ???????????? ????????????.
            //POST ???????????? ????????? ??????????????? ???????????? ????????? ?????? ???????????? ????????? ??????.
            String serverURL = params[0];

            // HTTP ????????? ????????? ???????????? ???????????? ????????? ?????? ???????????? ??????????????????.
            // POST ???????????? ????????? ??????????????? ???????????? ????????? ?????? ???????????? ????????? ??????.
            // ????????? ????????? ????????? ????????? PHP?????? ???????????? ?????? ?????? ?????????.
            // String postParameters = "name=" + name + "&country=" + country;
            // ??????????????? POST ???????????? ????????? ??????????????? ???????????? ?????????????????? ?????? ???????????? ?????? ??????.
            String postParameters = params[1];

            try {
                //HttpURLConnection ???????????? ???????????? POST ???????????? ???????????? ????????????.

                URL url = new URL(serverURL); // ????????? ????????? ????????? ????????? ????????????.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000); // 5????????? ????????? ?????? ????????? ????????? ????????????.
                httpURLConnection.setConnectTimeout(5000); // 5????????? ????????? ????????? ????????? ????????????.
                httpURLConnection.setRequestMethod("POST"); // ?????? ????????? POST??? ????????????.
                httpURLConnection.setDoInput(true); //InputStream ?????? ??????????????? ????????? ???????????? ????????????.
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8)); // ????????? ???????????? ????????? ????????? ????????? ????????????. ?????? ???????????? ??????????????? ??????.
                outputStream.flush(); //Request Body ??? Data ??????
                outputStream.close(); //OutPutStream ??????

                //https://triest.tistory.com/14 - HttpURLConnection ?????? ?????? ?????? ???????????????

                //TODO ????????? ?????? ??????
                int responseStatesCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response Code - " + responseStatesCode);

                InputStream inputStream;
                // ?????? responseStatesCode ??? 200?????? ???????????? ?????????????????? inputStream ??? ????????????.
                if (responseStatesCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream(); // ???????????? ?????? ?????????
                } else {
                    inputStream = httpURLConnection.getErrorStream(); // ?????? ??????
                }

                //TODO StringBuilder ??? ???????????? ???????????? ???????????? ???????????? ??????
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                //TODO ????????? ???????????? ??????????????? ???????????? ????????????.
                return sb.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }

    private void showResult() {
        String TAG_JSON = "result";
        String TAG_ID = "sid";
        String TAG_NUM = "tjNum";
        String TAG_TITLE = "sTitle";

        try {
            //TODO json ???????????? ???????????? ????????? ????????? ??????

            //json ?????? ????????? ????????? ????????? {} ????????? JSONObject ??? ???????????? ??????
            JSONObject jsonObject = new JSONObject(mJsonString);
            //jsonObject ?????? TAG_JSON ??? ????????? ????????? ?????? JSONArray ??? ????????????.
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            //JsonArray?????? JSONObject??? ????????? ???????????? ??????????????????, ???????????? ???????????? JsonArray ?????? JsonObject ??? ????????? ????????????.
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                //JSONObject ?????? ??? sid, tjNum, sTitle ??? ?????? ????????????. (???, ?????? ???????????? json ??? ????????????.)
                //ex) {"result":[{"sid":"202206096c6d","tjNum":"28745","kyNum":"44187","sTitle":"?????? ?????????","sSinger":"GUMI","sAniTitle":"","sAniPart":""}
                String id = item.getString(TAG_ID);
                String num = item.getString(TAG_NUM);
                String title = item.getString(TAG_TITLE);

                //???????????? ?????? ????????? PersonalData ???????????? ??????????????? ???????????? ArrayList ??? ????????????.
                PersonalData personalData = new PersonalData();

                // personalData??? ????????? ??????
                personalData.setKaraoke_id(id);
                personalData.setKaraoke_num(num);
                personalData.setKaraoke_title(title);

                mArrayList.add(personalData);
                //???????????? ???????????? ?????????????????? ?????????, ????????? ????????? ???????????? ???????????????.
                mAdapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "showResult : ", e);
        }
    }
}

//????????????: https://webnautes.tistory.com/829 - android+php json???????????? ????????????
//????????????: https://programmierfrage.com/items/android-studio-sslhandshakeexception-trust-anchor-for-certification-path-not
//???SSLHandshakeException ?????? ????????? ????????????
//????????????: https://triest.tistory.com/14 - HttpURLConnection ????????? ?????? ?????? ?????????