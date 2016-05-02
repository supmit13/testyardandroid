package xpresstech.testyard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChallengeCreationActivity extends AppCompatActivity {

    TextView messageText;
    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;

    final String uploadFilePath = "/mnt/sdcard/";

    String response;
    String cookiestr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_challenge_creation);
        // this fucking bit of code doesn't work for no reason... damn it. Similar code works for other screens!
        // I had to put the options in strings.xml, which I do not want to do, as ideally the spinner should be populated from here.
        //Spinner dropdownqualities = (Spinner)findViewById(R.id.challenge_quality);
        //String[] qualitytypes = new String[]{"Beginner", "Intermediate", "Professional"};
        //ArrayAdapter<String> adapterqualities = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, qualitytypes);
        //dropdownqualities.setAdapter(adapterqualities);
    }


    public void addChallenge(View view){
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");

        final String uploadFileName = "challenge_" + username + "_" + sessionid; // File extension needs to be extracted from the real filename later.

        EditText challenge_statement   = (EditText)findViewById(R.id.challenge_statement);
        String challengeStatement = challenge_statement.getText().toString();

        EditText external_resource_url = (EditText)findViewById(R.id.external_resource_url);
        String externalResourceUrl = external_resource_url.getText().toString();

        EditText response_lines_count = (EditText)findViewById(R.id.response_lines_count);
        String responseLinesCount = response_lines_count.getText().toString();

        EditText challenge_score = (EditText)findViewById(R.id.challenge_score);
        String challengeScore = challenge_score.getText().toString();

        EditText negative_score = (EditText)findViewById(R.id.negative_score);
        String negativeScore = negative_score.getText().toString();

        EditText max_time_limit = (EditText)findViewById(R.id.max_time_limit);
        String maxTimeLimit = max_time_limit.getText().toString();

        Spinner challenge_quality = (Spinner)findViewById(R.id.challenge_quality);
        String challengeQuality = challenge_quality.getSelectedItem().toString();

        CheckBox compulsory_challenge = (CheckBox)findViewById(R.id.compulsory_challenge);
        String compulsoryChallenge = "0";
        if(compulsory_challenge.isChecked()){
            compulsoryChallenge = "1";
        }
        /*
        uploadButton = (Button)findViewById(R.id.media_file);
        messageText  = (TextView)findViewById(R.id.media_file_text);
        messageText.setText("Uploading file...");
        upLoadServerUri = getString(R.string.upload_server_url);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(ChallengeCreationActivity.this, "", "Uploading file...", true);
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                messageText.setText("uploading started.....");
                            }
                        });

                        uploadFile(uploadFilePath + "" + uploadFileName);

                    }
                }).start();
            }
        });
        */
        final HashMap postDataParams = new HashMap<String, String>();
        postDataParams.put("challengeStatement", challengeStatement);
        postDataParams.put("externalResourceUrl", externalResourceUrl);
        postDataParams.put("responseLinesCount", responseLinesCount);
        postDataParams.put("challengeScore", challengeScore);
        postDataParams.put("negativeScore", negativeScore);
        postDataParams.put("maxTimeLimit", maxTimeLimit);
        postDataParams.put("challengeQuality", challengeQuality);
        postDataParams.put("compulsoryChallenge", compulsoryChallenge);
        cookiestr = "sessioncode=" + sessionid + "; usertype=" + usertype + "; csrftoken=";
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    String addChallengeUrl = getString(R.string.add_challenge_url);
                    response = sendPostRequest(addChallengeUrl, postDataParams);
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
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        Pattern p = Pattern.compile("Challenge\\s+object\\s+created\\s+successfully");
        Matcher m = p.matcher(response);
        if(m.matches()) {
            //Display screen according to the button on which the user tapped.
        }
    }


    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        return (0);
    }


    public void saveClose(View view){

    }


    public void closeScreen(View view){

    }


    public void selectMediaFile(View view){

    }

    // Some functions that should be in a common file... I must comsolidate these one of these days.
    public String  sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        //String csrftoken = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "*/*");
            String csrftoken = UUID.randomUUID().toString();
            String newcsrftoken = csrftoken.replaceAll("-", ""); // remove all hyphens
            cookiestr += newcsrftoken;
            conn.setRequestProperty("Cookie", cookiestr);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            String postDataParamsString = getPostDataString(postDataParams);
            String keystring = "challenge";
            String ivstring = "challenge";
            String postDataParamsStringEncoded = base64Encode(postDataParamsString);
            String postDataParamsStringEncrypted = des3Encrypt(postDataParamsStringEncoded, keystring, ivstring);
            String data = "data=";
            postDataParamsStringEncrypted = data + postDataParamsStringEncrypted + "&csrfmiddlewaretoken=" + newcsrftoken;
            //Log.d("ENCRYPTED PARAM STRING:", postDataParamsStringEncrypted);
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(postDataParamsStringEncrypted);
            writer.flush();
            writer.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null){
                    response = line;
                }
            }
            else {
                response="Unsuccessful HTTP request.";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return (response);
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


    public String des3Encrypt(String value, String keyString, String ivString) {
        return (value);
    }


    public String base64Encode(String encryptedString){
        // Do base64 encoding of the argument string
        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = encryptedString.getBytes("UTF-8");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        String encodedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        return (encodedString);
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
