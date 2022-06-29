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
        // button_all 을 클릭시 GetData|AsyncTask 가 실행된다.
        button_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                // 안드로이드 코드에서 실행시킬 서버의 IP 주소와 PHP 파일의 이름을 지정
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
            // 에러가 있는 경우 에러메시지를 표시하고 아니면 JSON 데이터를 파싱하여 화면에 보여주는 showResult()메소드를 호출한다.
            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {
                mTextViewResult.setText(errorString);
            } else {
                mJsonString = result;
                showResult(); // json 데이터를 파싱하여 화면에 뿌리는 메소드
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //TODO 서버에 있는 PHP 파일을 실행시키고 응답을 저장하고 스트링으로 변환하여 리턴하는 구간

            //PHP파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비한다.
            //POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않아도 된다.
            String serverURL = params[0];

            // HTTP 메시지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야한다.
            // POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않아도 된다.
            // 여기에 적어준 이름을 나중에 PHP에서 사용하여 값을 얻게 됩니다.
            // String postParameters = "name=" + name + "&country=" + country;
            // 마찬가지로 POST 방식으로 데이터 전달시에는 데이터가 파라미터에에 직접 입력되지 않아 된다.
            String postParameters = params[1];

            try {
                //HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송한다.

                URL url = new URL(serverURL); // 주소가 저장된 변수를 이곳에 입력한다.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000); // 5초안에 응답이 오지 않으면 예외가 발생한다.
                httpURLConnection.setConnectTimeout(5000); // 5초안에 연결이 않되면 예외가 발생한다.
                httpURLConnection.setRequestMethod("POST"); // 요청 방식을 POST로 지정한다.
                httpURLConnection.setDoInput(true); //InputStream 으로 서버로부터 응답을 받겠다는 옵션이다.
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8)); // 전송할 데이턱가 저장된 변수를 이곳에 입력한다. 물론 인코딩을 고려해야만 한다.
                outputStream.flush(); //Request Body 에 Data 입력
                outputStream.close(); //OutPutStream 종료

                //https://triest.tistory.com/14 - HttpURLConnection 설정 관련 옵션 설명★★★

                //TODO 응답을 읽는 구간
                int responseStatesCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response Code - " + responseStatesCode);

                InputStream inputStream;
                // 현재 responseStatesCode 가 200일때 정상적인 연결데이터를 inputStream 에 저장한다.
                if (responseStatesCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream(); // 정상적인 응답 데이터
                } else {
                    inputStream = httpURLConnection.getErrorStream(); // 에러 발생
                }

                //TODO StringBuilder 를 사용하여 수신되는 데이터를 저장하는 구간
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                //TODO 저장된 데이터를 스트링으로 변환하여 반환한다.
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
            //TODO json 데이터를 파싱하여 화면에 뿌리는 구간

            //json 기본 형식의 첫번째 괄호는 {} 이기에 JSONObject 로 데이터를 받음
            JSONObject jsonObject = new JSONObject(mJsonString);
            //jsonObject 에서 TAG_JSON 의 키값을 가지고 있는 JSONArray 를 가져온다.
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            //JsonArray에는 JSONObject가 데이터 갯수만큼 포함되어있어, 인덱스를 사용하여 JsonArray 에서 JsonObject 를 하나씩 가져온다.
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                //JSONObject 에서 키 sid, tjNum, sTitle 의 값을 가져온다. (단, 해당 키값들은 json 의 키값이다.)
                //ex) {"result":[{"sid":"202206096c6d","tjNum":"28745","kyNum":"44187","sTitle":"검은 고양이","sSinger":"GUMI","sAniTitle":"","sAniPart":""}
                String id = item.getString(TAG_ID);
                String num = item.getString(TAG_NUM);
                String title = item.getString(TAG_TITLE);

                //데이터를 새로 생성한 PersonalData 클래스의 멤버변수에 입력하고 ArrayList 에 추가한다.
                PersonalData personalData = new PersonalData();

                // personalData에 데이터 입력
                personalData.setKaraoke_id(id);
                personalData.setKaraoke_num(num);
                personalData.setKaraoke_title(title);

                mArrayList.add(personalData);
                //리스트에 데이터가 변경되었음을 알리고, 화면에 추가된 데이터를 보이게한다.
                mAdapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "showResult : ", e);
        }
    }
}

//참고자료: https://webnautes.tistory.com/829 - android+php json파싱하여 연결하기
//추가자료: https://programmierfrage.com/items/android-studio-sslhandshakeexception-trust-anchor-for-certification-path-not
//┗SSLHandshakeException 에러 발생시 해결방법
//추가자료: https://triest.tistory.com/14 - HttpURLConnection 설정및 옵션 관련 설명집