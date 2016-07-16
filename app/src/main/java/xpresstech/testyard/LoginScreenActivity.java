package xpresstech.testyard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

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
import java.util.regex.Pattern;


public class LoginScreenActivity extends Activity {

    public final static String EXTRA_MESSAGE_1 = "xpresstech.testyard.Username";
    public final static String EXTRA_MESSAGE_2 = "xpresstech.testyard.Password";
    public final static String SELECTED_OPTION = "selected_option";

    public static String response = "";
    public static String csrftoken = "";
    //public static String cookiestr = "";
    static final String COOKIES_HEADER = "Set-Cookie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login_screen);
    }


    public void processLogin(View view) {
        Intent intent = getIntent();
        Integer selected_option = intent.getIntExtra(SELECTED_OPTION, -1);
        //Define a new Intent
        intent = new Intent(this, LoginScreenActivity.class);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        String uname = username.getText().toString();
        String pwd = password.getText().toString();
        String sessioncode = "";
        intent.putExtra(EXTRA_MESSAGE_1, uname);
        intent.putExtra(EXTRA_MESSAGE_2, pwd);
        intent.putExtra(SELECTED_OPTION, selected_option); // Set the value of 'SELECTED_OPTION'.
        response = "";
        // Make a call to server to verify   the credentials provided by the user.
        // This will hit a webservice running on the server which sends back
        // the session variable (encrypted 3DES) for the username and password
        // sent to it. If the given credentials fail to verify, an empty string is
        // sent back. The username and password are sent as 3DES encrypted strings.
        final String verifyUrl = getString(R.string.verifyurl);
        //final String verifyUrl = "http://192.168.1.4:8080/skillstest/mobile/verifycreds/";
        final HashMap postDataParams = new HashMap<String, String>();
        postDataParams.put("username", uname);
        postDataParams.put("password", pwd);
        // Don't want the UI thread to actually handle these time consuming
        // network operations, so I am opening a new thread to do the fire-
        // fighting here...
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    response = sendPostRequest(verifyUrl, postDataParams);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        // Handle response value. If response starts with "Error: ...", or if it
        // is empty, then an error has occurred and we can't go ahead with this activity.
        // Otherwise, things are fine and we extract the session Id from the response.
        try {
            thread.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Pattern errorPattern = Pattern.compile("^Error:");
        Pattern sesscodePattern = Pattern.compile("sesscode=\\w+");
        String usertype = "";
        boolean m = errorPattern.matcher(response).matches();
        boolean s = sesscodePattern.matcher(response).matches();
        if (!m && response != "") {
            String[] respparts = response.split("&");
            String[] sesscodekeyvals = respparts[0].split("=");
            if (sesscodekeyvals[0].equals("sesscode")) {
                sessioncode = sesscodekeyvals[1];
            }
            String[] usertypekeyvals = respparts[1].split("=");
            usertype = usertypekeyvals[1];
        }
        else if(s) {
            String[] respparts = response.split("sesscode=");
            sessioncode = respparts[1];
        }
        else if(m) {
            // There has been an error - may be the creds were incorrect or user is not active or anything.
            sessioncode = "";
        }
        // if sessioncode != "", call the appropriate activity
        if(sessioncode != ""){
            // Set the shared preference value for session_id
            SharedPreferences apppref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = apppref.edit();
            editor.putString("session_id", sessioncode).commit();
            editor.putString("username", uname).commit();
            editor.putString("usertype", usertype).commit();
            // *****NOTE: Need to get usertype too.

            //SharedPreferences cookiepref = getApplicationContext().getSharedPreferences("CookiePref", 0); // 0 - for private mode
            //SharedPreferences.Editor editor2 = cookiepref.edit();
            //editor2.putString("cookieheader", cookiestr).commit();
            if(selected_option == 1) {
                intent = new Intent(this, ListTestsAndInterviewsActivity.class);
            }
            else if(selected_option == 2) {
                intent = new Intent(this, ListTestsByCreatorActivity.class);
            }
            else if(selected_option == 3) {
                intent = new Intent(this, ConductInterviewActivity.class);
            }
            else if(selected_option == 4) {
                intent = new Intent(this, ScheduleInterviewActivity.class);
            }
            else if(selected_option == 5) {
                intent = new Intent(this, TestCreationActivity.class);
            }
            else if(selected_option == 6) {
                intent = new Intent(this, TakeTestActivity.class);
            }
            else if(selected_option == 7) {
                intent = new Intent(this, AttendInterviewActivity.class);
            }
        }
        finish();
        startActivity(intent);
    }


    public String  sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        //String csrftoken = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "*/*");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //conn.connect();
            String postDataParamsString = getPostDataString(postDataParams);
            String keystring = "test";
            String ivstring = "test";
            String postDataParamsStringEncoded = base64Encode(postDataParamsString);
            String postDataParamsStringEncrypted = des3Encrypt(postDataParamsStringEncoded, keystring, ivstring);
            String data = "data=";
            //postDataParamsStringEncrypted = data.concat(postDataParamsStringEncrypted);
            postDataParamsStringEncrypted = data + postDataParamsStringEncrypted;
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
                    response+=line;
                }
                String cookie_recvd = conn.getHeaderField("Set-Cookie");
                String [] cookieslist = cookie_recvd.split(";");
                String [] cookieparts = cookieslist[0].split("=");
                String usertype = cookieparts[1];
                response += "&usertype=" + usertype;
            }
            else {
                response="";
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


    public String des3Encrypt(String value, String keyString, String ivString){
        return value;
        /*
        // encrypt value with 3DES encryption
        KeySpec keySpec;
        SecretKey key;
        IvParameterSpec iv;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest(keyString.getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }
            keySpec = new DESedeKeySpec(keyBytes);
            key = SecretKeyFactory.getInstance("CFB").generateSecret(keySpec);
            iv = new IvParameterSpec(ivString.getBytes());
            Cipher ecipher = Cipher.getInstance("DESede/CBC/PKCS5Padding","SunJCE");
            ecipher.init(Cipher.ENCRYPT_MODE, key, iv);

            if(value==null){
                return "";
            }
            // Encode the string into bytes using utf-8
            byte[] utf8 = value.getBytes("UTF8");
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            return new String(enc,"UTF-8");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
        //String encryptedstring = plaintextstr;
        //return (encryptedstring);
        */
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
}



