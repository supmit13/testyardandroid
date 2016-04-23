package xpresstech.testyard;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
            String asCandidateListStr = "\n\nAs Candidate:\n\n";
            List allNodes = new ArrayList();
            for(int i=0;i < candidatekeys.length(); i++){
                String testname = candidatekeys.getString(i);
                JSONArray attribs = asCandidate.getJSONArray(testname);
                String testscore = attribs.getString(0);
                String outcome = attribs.getString(1);
                String testdate = attribs.getString(2);
                String testtopic = attribs.getString(3);
                HashMap map = new HashMap();
                map.put(0, testname);
                map.put(1, testscore);
                map.put(2, outcome);
                map.put(3, testdate);
                map.put(4, testtopic);
                asCandidateList.add(map);
                asCandidateListStr += "Test Name: " + testname + "\n";
                asCandidateListStr += "Test Score: " + testscore + "\n";
                asCandidateListStr += "Outcome: " + outcome + "\n";
                asCandidateListStr += "Test Date: " + testdate + "\n";
                asCandidateListStr += "Test Topic: " + testtopic + "\n";
                asCandidateListStr += "\n\n";
            }
            allNodes.add(asCandidateListStr);
            String asCreatorListStr = "\n\nAs Creator:\n\n";
            for(int i=0;i < creatorkeys.length(); i++){
                String testname = creatorkeys.getString(i);
                JSONArray attribs = asCreator.getJSONArray(testname);
                String testtakerscount = attribs.getString(0);
                String testtopic = attribs.getString(1);
                HashMap map = new HashMap();
                map.put(0, testname);
                map.put(1, testtakerscount);
                map.put(2, testtopic);
                asCreatorList.add(map);
                asCreatorListStr += "Test Name: " + testname + "\n";
                asCreatorListStr += "Test Takers Count: " + testtakerscount + "\n";
                asCreatorListStr += "Test Topic: " + testtopic + "\n";
                asCreatorListStr += "\n\n";
            }
            allNodes.add(asCreatorListStr);
            String asEvaluatorListStr = "\n\nAs Evaluator: \n\n";
            for(int i=0;i < evaluatorkeys.length(); i++){
                String testname = evaluatorkeys.getString(i);
                JSONArray attribs = asEvaluator.getJSONArray(testname);
                String testtopic = attribs.getString(0);
                HashMap map = new HashMap();
                map.put(0, testname);
                map.put(1, testtopic);
                asEvaluatorList.add(map);
                asEvaluatorListStr += "Test Name: " + testname + "\n";
                asEvaluatorListStr += "Test Topic: " + testtopic + "\n";
                asEvaluatorListStr += "\n\n";
            }
            allNodes.add(asEvaluatorListStr);
            String asInterviewCandidateListStr = "\n\nAs Interview Candidate: \n\n";
            for(int i=0;i < interviewcandidatekeys.length(); i++){
                String interviewname = interviewcandidatekeys.getString(i);
                JSONArray attribs = asInterviewCandidates.getJSONArray(interviewname);
                String interviewtopic = attribs.getString(0);
                HashMap map = new HashMap();
                map.put(0, interviewname);
                map.put(1, interviewtopic);
                asInterviewCandidateList.add(map);
                asInterviewCandidateListStr += "Interview Name: " + interviewname + "\n";
                asInterviewCandidateListStr += "Interview Topic: " + interviewtopic + "\n";
                asInterviewCandidateListStr += "\n\n";
            }
            allNodes.add(asInterviewCandidateListStr);
            String asInterviewerListStr = "\n\nAs Interviewer: \n\n";
            for(int i=0;i < interviewerkeys.length(); i++){
                String interviewname = interviewerkeys.getString(i);
                JSONArray attribs = asInterviewer.getJSONArray(interviewname);
                String interviewtopic = attribs.getString(0);
                HashMap map = new HashMap();
                map.put(0, interviewname);
                map.put(1, interviewtopic);
                asInterviewerList.add(map);
                asInterviewerListStr += "Interview Name: " + interviewname + "\n";
                asInterviewerListStr += "Interview Topic: " + interviewtopic + "\n";
                asInterviewerListStr += "\n\n";
            }
            allNodes.add(asInterviewerListStr);
            setContentView(R.layout.activity_list_tests_and_interviews);
            ListView mainlistview =  (ListView) findViewById(android.R.id.list);
            HashMap hashobj = (HashMap)asCandidateList.get(0);
            ArrayAdapter<String> listadapter = new ArrayAdapter<String>(ListTestsAndInterviewsActivity.this, R.layout.content_list_tests_and_interviews, new String[]{(String)allNodes.get(0), (String)allNodes.get(1) , (String)allNodes.get(2), (String)allNodes.get(3), (String)allNodes.get(4)});
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
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");
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
