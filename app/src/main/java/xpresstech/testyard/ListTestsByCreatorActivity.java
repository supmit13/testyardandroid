package xpresstech.testyard;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ListTestsByCreatorActivity extends ListActivity {

    public static String response = "";
    private ProgressDialog progressMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    response = listTestsByCreator();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonobj = new JSONObject(response);

            JSONArray creatorkeys = jsonobj.names();
            if(creatorkeys == null){
                creatorkeys = new JSONArray();
            }
            List asCreatorList = new ArrayList();
            List allNodes = new ArrayList();
            
            String asCreatorListStr = "\n\nTests\n\n";
            for(int i=0;i < creatorkeys.length(); i++){
                String testname = creatorkeys.getString(i);
                JSONArray attribs = jsonobj.getJSONArray(testname);
                String testtopic = attribs.getString(0);
				String fullscore = attribs.getString(1);
                String duration = attribs.getString(2);
                HashMap map = new HashMap();
                map.put(0, testname);
                map.put(1, testtopic);
                map.put(2, fullscore);
                map.put(3, duration);
                asCreatorList.add(map);
                final Button btn = new Button(this);
                btn.setId(i + 1);
                btn.setText("(Schedule Test)");
                btn.setOnClickListener(new View.OnClickListener() {
                   public void onClick(View v) {
                       EditText emaillist = (EditText)findViewById(R.id.email_view);
                       emaillist.setHint("Enter Email Ids here");
                       emaillist.setHeight(300);
                       emaillist.setWidth(500);
                   }
                });
                asCreatorListStr += "Test Name: " + testname + "\n";
                asCreatorListStr += "Test Topic: " + testtopic + "\n";
				asCreatorListStr += "Full Score: " + fullscore + "\n";
                if(Integer.parseInt(duration) > 60) {
                    int duration_min = Integer.parseInt(duration)/60;
                    asCreatorListStr += "Duration: " + Integer.toString(duration_min) + " mins.\n";
                }
                else{
                    asCreatorListStr += "Duration: " + duration + " secs.\n";
                }
                asCreatorListStr += "\n\n";
            }
            allNodes.add(asCreatorListStr);
            
            setContentView(R.layout.activity_list_tests_by_creator);
            ListView mainlistview =  (ListView) findViewById(android.R.id.list);
            HashMap hashobj = (HashMap)asCreatorList.get(0);
            ArrayAdapter<String> listadapter = new ArrayAdapter<String>(ListTestsByCreatorActivity.this, R.layout.content_list_tests_by_creator, new String[]{(String)allNodes.get(0)});
            //SimpleAdapter tcadapter = new SimpleAdapter(ListTestsAndInterviewsActivity.this, asCandidateList, R.layout.content_list_tests_and_interviews, new String[]{"testname", "testscore", "outcome", "testdate", "testtopic"}, new int[]{R.id.testname, R.id.testscore, R.id.outcome, R.id.testdate, R.id.testtopic});
            mainlistview.setAdapter(listadapter);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public String listTestsByCreator(){
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");
        try {
            URL url;
            final String listTestsInterviewsUrl = getString(R.string.list_tests_by_creator_url);
            url = new URL(listTestsInterviewsUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String csrftoken = UUID.randomUUID().toString();
            String newcsrftoken = csrftoken.replaceAll("-", ""); // remove all hyphens
            String cookiestr = "sessioncode=" + sessionid + "; usertype=" + usertype + "; csrftoken=" + newcsrftoken;
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Cookie", cookiestr);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            final HashMap postDataParams = new HashMap<String, String>();
            postDataParams.put("username", username);
            postDataParams.put("sessionid", sessionid);
            postDataParams.put("csrfmiddlewaretoken", newcsrftoken);
            String postDataParamsString = getPostDataString(postDataParams);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(postDataParamsString);
            writer.flush();
            writer.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null){
                    response+=line;
                }
            }
            else {
                response="";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("=========RESPONSE======", response);
        return(response);
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return (result.toString());
    }

    public void backToMainMenu(View view){
        SharedPreferences apppref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String sessionId = apppref.getString("session_id", "");
        Intent intent = new Intent(this, MainActivity.class);
        if(sessionId == "") {
            intent = new Intent(this, LoginScreenActivity.class);
        }
        finish();
        startActivity(intent);
    }
}

