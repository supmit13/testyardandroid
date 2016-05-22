package xpresstech.testyard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;
import com.dialog.DatePickerFragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestCreationActivity extends FragmentActivity {
    public final static String SELECTED_OPTION = "selected_option";
    String response;
    String cookiestr;

    private ImageButton imageButtonPublish;
    private ImageButton imageButtonActivate;

    private EditText publishDate;
    private EditText activateDate;

    static final int DATE_PICKER_ID = 2222;
    private int yyyy;
    private int mm;
    private int dd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_creation);

        publishDate = (EditText)findViewById(R.id.testPublishDate);
        activateDate = (EditText)findViewById(R.id.testActivationDate);

        imageButtonPublish = (ImageButton)findViewById(R.id.imageButtonPublish);
        imageButtonActivate = (ImageButton)findViewById(R.id.imageButtonActivate);

        final Calendar c = Calendar.getInstance();
        yyyy  = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH) + 1;
        String mmstr = Integer.toString(mm);
        if(mmstr.length() < 2){
            mmstr = "0" + mmstr;
        }
        dd   = c.get(Calendar.DAY_OF_MONTH);
        String ddstr = Integer.toString(dd);
        if(ddstr.length() < 2){
            ddstr = "0" + ddstr;
        }

        publishDate.setText(new StringBuilder().append(yyyy).append("-").append(mmstr).append("-").append(ddstr));
        activateDate.setText(new StringBuilder().append(yyyy).append("-").append(mmstr).append("-").append(ddstr));

        imageButtonPublish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // On button click show datepicker dialog
                showDatePicker();
            }
        });

        imageButtonActivate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // On button click show datepicker dialog
                showDatePicker();
            }
        });

        Spinner dropdowntypes = (Spinner)findViewById(R.id.testtypes);
        String[] itemstypes = new String[]{"Coding", "Composite", "Fill up the Blanks", "Algorithm", "Subjective", "Multiple Choice"};
        ArrayAdapter<String> adaptertypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemstypes);
        dropdowntypes.setAdapter(adaptertypes);

        Spinner dropdownrules = (Spinner)findViewById(R.id.testrules);
        String[] itemsrules = new String[]{"showatonce"};
        ArrayAdapter<String> adapterrules = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsrules);
        dropdownrules.setAdapter(adapterrules);

        Spinner dropdowntopics = (Spinner)findViewById(R.id.testtopics);
        String[] itemstopics = new String[]{"Programming", "Project Management", "Database Management", "Quality Assurance", "Software Testing", "Business Development", "Product Development", "Customer Service", "Software Architecture", "Delivery Management", "System Administration", "System Analyst", "UI Design", "Web Design", "Application Development", "Other"};
        ArrayAdapter<String> adaptertopics = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemstopics);
        dropdowntopics.setAdapter(adaptertopics);

        Spinner dropdownskilllevels = (Spinner)findViewById(R.id.targetskilllevels);
        String[] skilllevels = new String[]{"Beginner", "Intermediate", "Proficient"};
        ArrayAdapter<String> adapterskilllevels = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, skilllevels);
        dropdownskilllevels.setAdapter(adapterskilllevels);

        Spinner dropdowntestscopes = (Spinner)findViewById(R.id.testscope);
        String[] testscopes = new String[]{"public", "protected", "private"};
        ArrayAdapter<String> adaptertestscopes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, testscopes);
        dropdowntestscopes.setAdapter(adaptertestscopes);

        Spinner dropdownansweringlanguages = (Spinner)findViewById(R.id.answeringlanguages);
        String[] answeringlanguages = new String[]{"French", "English - UK", "Bengali - WB", "English - US", "Latin", "Bengali - Bangladesh", "Hindi"};
        ArrayAdapter<String> adapteranslangs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, answeringlanguages);
        dropdownansweringlanguages.setAdapter(adapteranslangs);

        Spinner dropdownprogenv = (Spinner)findViewById(R.id.progenv);
        String[] programmingenv = new String[]{"None", "Java", "Scala", "Delphi", "Javascript", "Perl", "Lua", "Python", "Tcl", "VBScript", "Ruby", "Bash", "C", "List", "VB.Net", "C++", "Objective-C", "Smalltalk", "C#", "Fortran", "Coldfusion", "CShell", "Pascal", "Curl", "PHP", "Ada95"};
        ArrayAdapter<String> adapterprogenv = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, programmingenv);
        dropdownprogenv.setAdapter(adapterprogenv);
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, yyyy, mm, dd);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            yyyy  = selectedYear;
            mm = selectedMonth;
            dd   = selectedDay;
            publishDate.setText(new StringBuilder().append(yyyy).append("-").append(mm + 1).append("-").append(dd).append(" "));
        }
    };



    public String onMultiAttemptsClicked(){
        return("");
    }


    public void createTest(View view){
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");

        EditText testNameEdit   = (EditText)findViewById(R.id.testname);
        String testname = testNameEdit.getText().toString();

        EditText totalScoreEdit = (EditText)findViewById(R.id.totalscore);
        String totalscore = totalScoreEdit.getText().toString();

        EditText challengesCountEdit = (EditText)findViewById(R.id.challengesCount);
        String challengescount = challengesCountEdit.getText().toString();

        EditText passScoreEdit = (EditText)findViewById(R.id.passScore);
        String passscore = passScoreEdit.getText().toString();

        EditText testDurationEdit = (EditText)findViewById(R.id.testDuration);
        String testduration = testDurationEdit.getText().toString();

        EditText maxChallengeDurationEdit = (EditText)findViewById(R.id.maxChallengeDuration);
        String maxchallengeduration = maxChallengeDurationEdit.getText().toString();

        EditText evalEmailIdsEdit = (EditText)findViewById(R.id.evalEmailIds);
        String evalemailids = evalEmailIdsEdit.getText().toString();

        EditText evalGroupNameEdit = (EditText)findViewById(R.id.evalGroupName);
        String evalgroupname = evalGroupNameEdit.getText().toString();

        EditText testPublishDateEdit = (EditText)findViewById(R.id.testPublishDate);
        String testpublishdate = testPublishDateEdit.getText().toString();

        EditText testActivationDateEdit = (EditText)findViewById(R.id.testActivationDate);
        String testactivationdate = testActivationDateEdit.getText().toString();

        Spinner testtypesSpinner = (Spinner)findViewById(R.id.testtypes);
        String testtypeselected = testtypesSpinner.getSelectedItem().toString();

        Spinner testrulesSpinner = (Spinner)findViewById(R.id.testrules);
        String testruleselected = testrulesSpinner.getSelectedItem().toString();

        Spinner testtopicsSpinner = (Spinner)findViewById(R.id.testtopics);
        String testtopicselected = testtopicsSpinner.getSelectedItem().toString();

        Spinner targetskilllevelsSpinner = (Spinner)findViewById(R.id.targetskilllevels);
        String targetskilllevelselected = targetskilllevelsSpinner.getSelectedItem().toString();

        Spinner testscopeSpinner = (Spinner)findViewById(R.id.testscope);
        String testscopeselected = testscopeSpinner.getSelectedItem().toString();

        Spinner answeringlanguagesSpinner = (Spinner)findViewById(R.id.answeringlanguages);
        String answeringlanguageselected = answeringlanguagesSpinner.getSelectedItem().toString();

        Spinner progenvSpinner = (Spinner)findViewById(R.id.progenv);
        String progenvselected = progenvSpinner.getSelectedItem().toString();

        RadioGroup sameScoreGroup = (RadioGroup) findViewById(R.id.sameScore);
        int selectedId1 = sameScoreGroup.getCheckedRadioButtonId();
        RadioButton rdbtn1 = (RadioButton) findViewById(selectedId1);
        String samescorevalue = (String) rdbtn1.getText();

        RadioGroup negativeScoreGroup = (RadioGroup) findViewById(R.id.negativeScore);
        int selectedId2 = negativeScoreGroup.getCheckedRadioButtonId();
        RadioButton rdbtn2 = (RadioButton) findViewById(selectedId2);
        String negativescorevalue = (String) rdbtn2.getText();

        CheckBox multimediaAllowed = (CheckBox)findViewById(R.id.allowmultimedia);
        CheckBox randomSequenced = (CheckBox)findViewById(R.id.randomsequenced);
        CheckBox multipleAttempts = (CheckBox)findViewById(R.id.multipleattempts);

        String multimediaAllowedFlag = "0";
        String randomSequencedFlag = "0";
        String multipleAttemptsFlag = "0";
        if(multimediaAllowed.isChecked()){
            multimediaAllowedFlag = "1";
        }
        if(randomSequenced.isChecked()){
            randomSequencedFlag = "1";
        }
        if(multipleAttempts.isChecked()){
            multipleAttemptsFlag = "1";
        }
        final String testcreationurl = getString(R.string.test_creation_url);
        final HashMap postDataParams = new HashMap<String, String>();
        postDataParams.put("testname", testname);
        postDataParams.put("testscore", totalscore);
        postDataParams.put("challengescount", challengescount);
        postDataParams.put("passscore", passscore);
        postDataParams.put("testduration", testduration);
        postDataParams.put("maxchallengeduration_secs", maxchallengeduration);
        postDataParams.put("evalgroupname", evalgroupname);
        postDataParams.put("evalemailids", evalemailids);
        postDataParams.put("testpublishdate", testpublishdate);
        postDataParams.put("testactivationdate", testactivationdate);
        postDataParams.put("testtypeselected", testtypeselected);
        postDataParams.put("testruleselected", testruleselected);
        postDataParams.put("testtopicselected", testtopicselected);
        postDataParams.put("targetskilllevelselected", targetskilllevelselected);
        postDataParams.put("testscopeselected", testscopeselected);
        postDataParams.put("answeringlanguageselected", answeringlanguageselected);
        postDataParams.put("progenvselected", progenvselected);
        postDataParams.put("samescorevalue", samescorevalue);
        postDataParams.put("negativescorevalue", negativescorevalue);
        postDataParams.put("multimediaAllowedFlag", multimediaAllowedFlag);
        postDataParams.put("randomSequencedFlag", randomSequencedFlag);
        postDataParams.put("multipleAttemptsFlag", multipleAttemptsFlag);
        postDataParams.put("sessionid", sessionid);
        postDataParams.put("username", username);
        cookiestr = "sessioncode=" + sessionid + "; usertype=" + usertype + "; csrftoken=";
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    response = sendPostRequest(testcreationurl, postDataParams);
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
        String message = "";
        String testlinkid = "";
        try {
            JSONObject jsonObj = new JSONObject(response); // Handle session expiry here
            message = jsonObj.getString("message");
            testlinkid = jsonObj.getString("testlinkid");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        Pattern p = Pattern.compile("Test\\s+object\\s+created\\s+successfully");
        Matcher m = p.matcher(message);
        if(m.matches()) {
            Button btncreatetest = (Button) findViewById(R.id.createtest);
            btncreatetest.setEnabled(false);
            Button btnaddchallenges = (Button) findViewById(R.id.addchallenges);
            btnaddchallenges.setEnabled(true);
            SharedPreferences apppref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = apppref.edit();
            editor.putString("testname", testname).commit();
            editor.putString("testtypeselected", testtypeselected).commit();
            editor.putString("negativescorevalue", negativescorevalue).commit();
            editor.putString("testlinkid", testlinkid).commit();
        }
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
            String csrftoken = UUID.randomUUID().toString();
            String newcsrftoken = csrftoken.replaceAll("-", ""); // remove all hyphens
            cookiestr += newcsrftoken;
            conn.setRequestProperty("Cookie", cookiestr);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            String postDataParamsString = getPostDataString(postDataParams);
            String keystring = "test";
            String ivstring = "test";
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

    public void closeapp(View view){

    }

    public void onMultiAttemptsClicked(View view){

    }

    public void addChallenges(View view){
        finish();
        Intent intent = new Intent(this, ChallengeCreationActivity.class);
        startActivity(intent);
        //setContentView(R.layout.content_challenge_creation);
    }

}
