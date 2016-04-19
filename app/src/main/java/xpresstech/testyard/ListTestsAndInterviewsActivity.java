package xpresstech.testyard;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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


public class ListTestsAndInterviewsActivity extends ListActivity {

    public static String response = "";
    private ProgressDialog progressMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    response = listTestsAndInterviews();
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
            //Log.d("RESPONSE 1 ======", response);
            JSONObject jsonobj = new JSONObject(response);
            JSONObject asCandidate = new JSONObject();
            asCandidate = jsonobj.getJSONObject("asCandidate");
            JSONObject asCreator = new JSONObject();
            asCreator = jsonobj.getJSONObject("asCreator");
            JSONObject asEvaluator = new JSONObject();
            asEvaluator = jsonobj.getJSONObject("asEvaluator");
            JSONObject asInterviewCandidates = new JSONObject();
            asInterviewCandidates = jsonobj.getJSONObject("asInterviewCandidates");
            JSONObject asInterviewer = new JSONObject();
            asInterviewer = jsonobj.getJSONObject("asInterviewer");

            JSONArray candidatekeys = asCandidate.names();
            if(candidatekeys == null){
                candidatekeys = new JSONArray();
            }
            JSONArray creatorkeys = asCreator.names();
            if(creatorkeys == null){
                creatorkeys = new JSONArray();
            }
            JSONArray evaluatorkeys = asEvaluator.names();
            if(evaluatorkeys == null){
                evaluatorkeys = new JSONArray();
            }
            JSONArray interviewcandidatekeys = asInterviewCandidates.names();
            if(interviewcandidatekeys == null){
                interviewcandidatekeys = new JSONArray();
            }
            JSONArray interviewerkeys = asInterviewer.names();
            if(interviewerkeys == null){
                interviewerkeys = new JSONArray();
            }

            List asCandidateList = new ArrayList();
            List asCreatorList = new ArrayList();
            List asEvaluatorList = new ArrayList();
            List asInterviewCandidateList = new ArrayList();
            List asInterviewerList = new ArrayList();

            for(int i=0;i < candidatekeys.length(); i++){
                String testname = candidatekeys.getString(i);
                JSONArray attribs = asCandidate.getJSONArray(testname);
                String testscore = attribs.getString(0);
                String outcome = attribs.getString(1);
                String testdate = attribs.getString(2);
                String testtopic = attribs.getString(3);
                HashMap map = new HashMap();
                map.put("testname", testname);
                map.put("testscore", testscore);
                map.put("outcome", outcome);
                map.put("testdate", testdate);
                map.put("testtopic", testtopic);
                asCandidateList.add(map);
            }
            for(int i=0;i < creatorkeys.length(); i++){
                String testname = creatorkeys.getString(i);
                JSONArray attribs = asCreator.getJSONArray(testname);
                String testtakerscount = attribs.getString(0);
                String testtopic = attribs.getString(1);
                HashMap map = new HashMap();
                map.put("testname", testname);
                map.put("testtakerscount", testtakerscount);
                map.put("testtopic", testtopic);
                asCreatorList.add(map);
            }
            for(int i=0;i < evaluatorkeys.length(); i++){
                String testname = evaluatorkeys.getString(i);
                JSONArray attribs = asEvaluator.getJSONArray(testname);
                String testtopic = attribs.getString(0);
                HashMap map = new HashMap();
                map.put("testname", testname);
                map.put("testtopic", testtopic);
                asEvaluatorList.add(map);
            }
            for(int i=0;i < interviewcandidatekeys.length(); i++){
                String interviewname = interviewcandidatekeys.getString(i);
                JSONArray attribs = asInterviewCandidates.getJSONArray(interviewname);
                String interviewtopic = attribs.getString(0);
                HashMap map = new HashMap();
                map.put("interviewname", interviewname);
                map.put("interviewtopic", interviewtopic);
                asInterviewCandidateList.add(map);
            }
            for(int i=0;i < interviewerkeys.length(); i++){
                String interviewname = interviewerkeys.getString(i);
                JSONArray attribs = asInterviewer.getJSONArray(interviewname);
                String interviewtopic = attribs.getString(0);
                HashMap map = new HashMap();
                map.put("interviewname", interviewname);
                map.put("interviewtopic", interviewtopic);
                asInterviewerList.add(map);
            }
            setContentView(R.layout.activity_list_tests_and_interviews);
            ListView mainlistview =  (ListView) findViewById(android.R.id.list);
            ArrayAdapter<String> listadapter = new ArrayAdapter<String>(ListTestsAndInterviewsActivity.this, R.layout.content_list_tests_and_interviews, new String[]{"testname", "testscore", "outcome", "testdate", "testtopic"});
            //SimpleAdapter tcadapter = new SimpleAdapter(ListTestsAndInterviewsActivity.this, asCandidateList, R.layout.content_list_tests_and_interviews, new String[]{"testname", "testscore", "outcome", "testdate", "testtopic"}, new int[]{R.id.testname, R.id.testscore, R.id.outcome, R.id.testdate, R.id.testtopic});
            mainlistview.setAdapter(listadapter);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }


    public String listTestsAndInterviews(){
        // We are in this activity only if the user is in logged in state
        //HashMap<String, String> user = new HashMap<String, String>();
        SharedPreferences pref;
        //SharedPreferences cookiepref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        //cookiepref = getApplicationContext().getSharedPreferences("CookiePref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");
        //String cookiestr = cookiepref.getString("cookieheader", "");
        //String csrftoken = "Oby7PxZxKOaJJdMNlGmd1GSDpJwx4Rja";
        // Create a POST request to the server with the username and sessionid values
        try {
            URL url;
            final String listTestsInterviewsUrl = getString(R.string.list_tests_interviews_url);
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
            Log.d("RESPONSE", response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
}
