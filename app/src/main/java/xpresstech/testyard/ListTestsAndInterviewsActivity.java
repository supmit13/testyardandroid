package xpresstech.testyard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


public class ListTestsAndInterviewsActivity extends Activity {

    public static String response = "";
    private ProgressDialog progressMessage;
    private Context context = null;


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
            System.out.println("RESPONSE 1 ======" + response);
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

            // Inflate the associated view
            setContentView(R.layout.content_list_tests_and_interviews);
            LinearLayout mainList =  (LinearLayout)findViewById(R.id.linlayout);
            Button backbuttontop = new Button(this);
            backbuttontop.setText("Back To Activity List");
            backbuttontop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    backToMainMenu(v);
                }
            });
            mainList.addView(backbuttontop);
            String asCandidateListHeaderStr = "\n\n-------------------------------\nAs Candidate:\n--------------------------------\n\n";
            TextView acnd_txt_header = new TextView(this);
            acnd_txt_header.setText(asCandidateListHeaderStr);
            acnd_txt_header.setTypeface(null, Typeface.BOLD_ITALIC);
            mainList.addView(acnd_txt_header);
            TextView acnd_txt = null;
            String asCandidateListStr = "";
            for(int i=0;i < candidatekeys.length(); i++){
                String testname = candidatekeys.getString(i);
                JSONArray attribs = asCandidate.getJSONArray(testname);
                String testscore = attribs.getString(0);
                String outcome = attribs.getString(1);
                String testdate = attribs.getString(2);
                String testtopic = attribs.getString(3);
                acnd_txt = new TextView(this);
                asCandidateListStr = "";
                asCandidateListStr += "\nTest Name: " + testname + "\n";
                asCandidateListStr += "Test Score: " + testscore + "\n";
                asCandidateListStr += "Outcome: " + outcome + "\n";
                asCandidateListStr += "Test Date: " + testdate + "\n";
                asCandidateListStr += "Test Topic: " + testtopic + "\n";
                asCandidateListStr += "\n________________________________\n\n";
                acnd_txt.setText(asCandidateListStr);
                mainList.addView(acnd_txt);
            }
            String asCreatorListHeaderStr = "\n\n-------------------------------\nAs Creator:\n--------------------------------\n\n";
            final TextView acrt_txt_header = new TextView(this);
            acrt_txt_header.setText(asCreatorListHeaderStr);
            acrt_txt_header.setTypeface(null, Typeface.BOLD_ITALIC);
            mainList.addView(acrt_txt_header);
            TextView acrt_txt = null;
            String asCreatorListStr = "";
            for(int i=0;i < creatorkeys.length(); i++){
                final String testname = creatorkeys.getString(i);
                JSONArray attribs = asCreator.getJSONArray(testname);
                String testtakerscount = attribs.getString(0);
                String testtopic = attribs.getString(1);
                acrt_txt = new TextView(this);
                asCreatorListStr = "";
                asCreatorListStr += "Test Name: " + testname + "\n";
                asCreatorListStr += "Test Takers Count: " + testtakerscount + "\n";
                asCreatorListStr += "Test Topic: " + testtopic + "\n";
                Button btn_addedit_challenge = new Button(this);
                btn_addedit_challenge.setTextSize(12);
                btn_addedit_challenge.setMaxHeight(10);
                btn_addedit_challenge.setMaxWidth(10);
                btn_addedit_challenge.setText("Add/Edit Challenges");
                btn_addedit_challenge.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        addEditChallenges(testname);
                    }
                });
                mainList.addView(btn_addedit_challenge);
                asCreatorListStr += "\n________________________________\n\n";
                acrt_txt.setText(asCreatorListStr);
                mainList.addView(acrt_txt);
            }
            String asEvaluatorListHeaderStr = "\n\n-------------------------------\nAs Evaluator: \n--------------------------------\n\n";
            final TextView aevl_txt_header = new TextView(this);
            aevl_txt_header.setText(asEvaluatorListHeaderStr);
            aevl_txt_header.setTypeface(null, Typeface.BOLD_ITALIC);
            mainList.addView(aevl_txt_header);
            TextView aevl_txt = null;
            String asEvaluatorListStr = "";
            for(int i=0;i < evaluatorkeys.length(); i++){
                String testname = evaluatorkeys.getString(i);
                JSONArray attribs = asEvaluator.getJSONArray(testname);
                String testtopic = attribs.getString(0);
                aevl_txt = new TextView(this);
                asEvaluatorListStr = "";
                asEvaluatorListStr += "Test Name: " + testname + "\n";
                asEvaluatorListStr += "Test Topic: " + testtopic + "\n";
                asEvaluatorListStr += "\n________________________________\n\n";
                aevl_txt.setText(asEvaluatorListStr);
                mainList.addView(aevl_txt);
            }
            String asInterviewCandidateListHeaderStr = "\n\n-------------------------------\nAs Interview Candidate: \n--------------------------------\n\n";
            final TextView aicnd_txt_header = new TextView(this);
            aicnd_txt_header.setText(asInterviewCandidateListHeaderStr);
            aicnd_txt_header.setTypeface(null, Typeface.BOLD_ITALIC);
            mainList.addView(aicnd_txt_header);
            String asInterviewCandidateListStr = "";
            for(int i=0;i < interviewcandidatekeys.length(); i++){
                String interviewname = interviewcandidatekeys.getString(i);
                JSONArray attribs = asInterviewCandidates.getJSONArray(interviewname);
                String interviewtopic = attribs.getString(0);
                TextView icnd_txt = new TextView(this);
                asInterviewCandidateListStr = "";
                asInterviewCandidateListStr += "Interview Name: " + interviewname + "\n";
                asInterviewCandidateListStr += "Interview Topic: " + interviewtopic + "\n";
                asInterviewCandidateListStr += "\n________________________________\n\n";
                icnd_txt.setText(asInterviewCandidateListStr);
                mainList.addView(icnd_txt);
            }
            String asInterviewerListHeaderStr = "\n\n-------------------------------\nAs Interviewer: \n--------------------------------\n\n";
            final TextView aicrt_txt_header = new TextView(this);
            aicrt_txt_header.setText(asInterviewerListHeaderStr);
            aicrt_txt_header.setTypeface(null, Typeface.BOLD_ITALIC);
            mainList.addView(aicrt_txt_header);
            String asInterviewerListStr = "";
            for(int i=0;i < interviewerkeys.length(); i++){
                String interviewname = interviewerkeys.getString(i);
                JSONArray attribs = asInterviewer.getJSONArray(interviewname);
                String interviewtopic = attribs.getString(0);
                TextView icrt_txt = new TextView(this);
                asInterviewerListStr = "";
                asInterviewerListStr += "Interview Name: " + interviewname + "\n";
                asInterviewerListStr += "Interview Topic: " + interviewtopic + "\n";
                asInterviewerListStr += "\n________________________________\n\n";
                icrt_txt.setText(asInterviewerListStr);
                mainList.addView(icrt_txt);
            }
            //System.out.println("+++++++++++++++++++++++++++++++ HERE 5 ++++++++++++++++++++++++++++" + mainList);
            Button backbuttonbottom = new Button(this);
            backbuttonbottom.setText("Back To Activity List");
            backbuttonbottom.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    backToMainMenu(v);
                }
            });
            mainList.addView(backbuttonbottom);
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
        SSLContext context = null;
        ///////////////////////////////// The following part handles self signed certificates. /////////////////////////////
        // Start of code to evade "java.security.cert.CertPathValidatorException"
        CertificateFactory cf = null;
        InputStream caInput = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(new FileInputStream("/sdcard/sware/testyard.crt"));
            //caInput = new BufferedInputStream(new FileInputStream("/home/supriyo/work/testyard/testyard/skillstest/etc_conf/testyard.crt"));
            //System.out.println("========================== caInput = " + caInput + "====================================");
        }
        catch(Exception e){
            System.out.println("Couldn't create certificate factory: " + e.getMessage());
            return null;
        }
        Certificate ca = null;
        try {
            ca = cf.generateCertificate(caInput);
            //System.out.println("******************************ca=" + ((X509Certificate) ca).getSubjectDN());
        }
        catch(Exception e) {
            //caInput.close();
            System.out.println("========================== Incurred exception - " + e.getMessage() + " ===============================");
        }
        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("testyard", ca);
        }
        catch(Exception e){
            System.out.println("Choking here.... after setCertificateEntry: " + e.getMessage());
        }
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // Handle hostname verifier to return true irrespective of whether the host is verifiable or not.
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }

            });
            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), new SecureRandom());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
            ////////////////////////////////// Handling of self signed certificate ends here //////////////////////////////////////
            // End of "java.security.cert.CertPathValidatorException" evading code.
        try {
            URL url;
                final String listTestsInterviewsUrl = getString(R.string.list_tests_interviews_url);
            url = new URL(listTestsInterviewsUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(context.getSocketFactory());
            String csrftoken = UUID.randomUUID().toString();
            String newcsrftoken = csrftoken.replaceAll("-", ""); // remove all hyphens
            String cookiestr = "sessioncode=" + sessionid + "; usertype=" + usertype + "; csrftoken=" + newcsrftoken + ";";
            //System.out.println("\n======================================\nCOOKIESTR = " + cookiestr + "\n===============================================\n");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Cookie", cookiestr);
            conn.setRequestProperty("X-CSRFToken", newcsrftoken);
            conn.setRequestProperty("Referer", getString(R.string.verifyurl));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            final HashMap postDataParams = new HashMap<String, String>();
            postDataParams.put("username", username);
            postDataParams.put("sessionid", sessionid);
            postDataParams.put("csrfmiddlewaretoken", newcsrftoken);
            //System.out.println("*************************** CSRF TOKEN *******************************" + newcsrftoken);
            String postDataParamsString = getPostDataString(postDataParams);
            postDataParamsString = postDataParamsString.replace("%24", "$");
            postDataParamsString = postDataParamsString.replace("%2F", "/");
            System.out.println("\n++++++++++++++++++++++++++\n" + postDataParamsString + "\n++++++++++++++++++++++++\n");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(postDataParamsString);
            writer.flush();
            writer.close();
            int responseCode=conn.getResponseCode();
            System.out.println("*************************** Response Code *******************************" + responseCode );
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
            System.out.println("*************************** RESPONSE *******************************" + response);
            Log.d("RESPONSE", response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return(response);
    }


    public void addEditChallenges(String testname){
        Toast.makeText(getApplicationContext(), "Adding Editing Challenges", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.content_challenge_creation);
        SharedPreferences apppref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = apppref.edit();
        String sessionId = apppref.getString("session_id", "");
        Intent intent = new Intent(this, ChallengeCreationActivity.class);
        if(sessionId == "") {
            intent = new Intent(this, LoginScreenActivity.class);
        }
        EditText editText = (EditText) findViewById(R.id.add_challenges);
        //System.out.println("editText 1 ======" + editText);
        editText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        String message = editText.getText().toString();
        finish();
        startActivity(intent);
        //return(message);
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
