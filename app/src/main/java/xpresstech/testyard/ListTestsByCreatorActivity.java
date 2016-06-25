package xpresstech.testyard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

// To Do: Need to add a EditText to allow the user to enter the number of hours/days for which the
// test will be valid after the scheduled start date. The value in the EditText area should be an integer.


public class ListTestsByCreatorActivity extends AppCompatActivity {

    public static String response = "";
    //private ProgressDialog progressMessage;
    private int target;
    static EditText edittext_schedtime;

    String cookiestr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    response = showTestsByCreator();
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
        final android.content.Context cxt = this;

        try {
            JSONObject jsonobj = new JSONObject(response);
            //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            JSONArray creatorkeys = jsonobj.names();
            if(creatorkeys == null){
                creatorkeys = new JSONArray();
            }
            setContentView(R.layout.activity_list_tests_by_creator);
            LinearLayout lv = (LinearLayout)findViewById(R.id.list_creator_tests);
            for(int i=0;i < creatorkeys.length(); i++){
                final String testname = creatorkeys.getString(i);
                JSONArray attribs = jsonobj.getJSONArray(testname);
                String testtopic = attribs.getString(0);
				String fullscore = attribs.getString(1);
                String duration = attribs.getString(2);
                final String testid = attribs.getString(4);
                TextView testNode = new TextView(this);
                String testText = "";
                testText += "Test Name: " + testname + " (" + testid + ")\n";
                testText += "Test Topic: " + testtopic + "\n";
                testText += "Full Score: " + fullscore + "\n";
                if(Integer.parseInt(duration) > 60) {
                    int duration_min = Integer.parseInt(duration)/60;
                    testText += "Duration: " + Integer.toString(duration_min) + " mins.\n";
                }
                else{
                    testText += "Duration: " + duration + " secs.\n";
                }
                testText += "Note: A scheduled test will remain valid for 3 days only. Candidates should be requested to take the test within this time frame.";
                testNode.setText(testText);
                lv.addView(testNode);
                LinearLayout ll = new LinearLayout(this);

                ll.setId(i + 40000);
                ll.setOrientation(LinearLayout.VERTICAL);
                lv.addView(ll);
                TextView dividerNode = new TextView(this);
                dividerNode.setText(getString(R.string.schedule_test_divider));
                int scrwidth = screenWidth();
                dividerNode.setWidth(scrwidth);
                final Button btn = new Button(this);
                btn.setId(i + 10000);
                btn.setText(getString(R.string.schedule_test_button));
                final int ictr = i;
                final LinearLayout flv = lv;
                final LinearLayout fll = ll;
                final View.OnClickListener listener = new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText emaillist = new EditText(cxt);
                        emaillist.setId(ictr + 20000);
                        emaillist.setLines(4); // Display 4 lines by default.
                        emaillist.setMaxLines(100); // Provide a large number of lines if the user needs.
                        int scrwidth = screenWidth();
                        emaillist.setWidth(scrwidth);
                        emaillist.setHint(getString(R.string.schedule_test_email_ids));
                        fll.addView(emaillist);
                        EditText scheduleDate = new EditText(cxt);
                        scheduleDate.setId(ictr + 60000);
                        scheduleDate.setHint(R.string.schedule_date_hint);
                        fll.addView(scheduleDate);
                        ImageButton scheduleDateBtn = new ImageButton(cxt);
                        scheduleDateBtn.setId(ictr + 70000);
                        Drawable drawableschedule = ContextCompat.getDrawable(getApplicationContext(), R.drawable.activate_button);
                        scheduleDateBtn.setImageDrawable(drawableschedule);
                        fll.addView(scheduleDateBtn);
                        EditText scheduleTime = new EditText(cxt);
                        scheduleTime.setId(ictr + 80000);
                        scheduleTime.setHint("hh:mm");
                        fll.addView(scheduleTime);
                        ImageButton scheduleTimeBtn = new ImageButton(cxt);
                        scheduleTimeBtn.setId(ictr + 90000);
                        scheduleTimeBtn.setImageDrawable(drawableschedule);
                        fll.addView(scheduleTimeBtn);
                        Button gobtn = new Button(cxt);
                        gobtn.setId(ictr + 30000);
                        gobtn.setText(getString(R.string.go_schedule));
                        btn.setVisibility(View.INVISIBLE);
                        final Button cancelbtn = new Button(cxt);
                        cancelbtn.setId(ictr + 50000);
                        cancelbtn.setText(getString(R.string.cancel_test_button));
                        scheduleDateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // On button click show datepicker dialog
                                target = ictr;
                                showDatePicker();
                            }
                        });
                        scheduleTimeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // On button click show datepicker dialog
                                target = ictr;
                                showTimePicker();
                                edittext_schedtime = (EditText) findViewById(target + 80000);
                            }
                        });
                        gobtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int stat = submitSchedule(ictr, testid);
                                if(stat == 1) {
                                    EditText emailtext = (EditText) findViewById(ictr + 20000);
                                    if (emailtext != null) {
                                        ((ViewManager) emailtext.getParent()).removeView(emailtext);
                                    }
                                    Button gobtnex = (Button) findViewById(ictr + 30000);
                                    if (gobtnex != null) {
                                        ((ViewManager) gobtnex.getParent()).removeView(gobtnex);
                                    }
                                    if (cancelbtn != null) {
                                        ((ViewManager) cancelbtn.getParent()).removeView(cancelbtn);
                                    }
                                    ImageButton scheduleDateBtnex = (ImageButton) findViewById(ictr + 70000);
                                    if (scheduleDateBtnex != null) {
                                        ((ViewManager) scheduleDateBtnex.getParent()).removeView(scheduleDateBtnex);
                                    }
                                    ImageButton scheduleTimeBtnex = (ImageButton) findViewById(ictr + 90000);
                                    if (scheduleTimeBtnex != null) {
                                        ((ViewManager) scheduleTimeBtnex.getParent()).removeView(scheduleTimeBtnex);
                                    }
                                    EditText scheduleDateex = (EditText) findViewById(ictr + 60000);
                                    if (scheduleDateex != null) {
                                        ((ViewManager) scheduleDateex.getParent()).removeView(scheduleDateex);
                                    }
                                    EditText scheduleTimeex = (EditText) findViewById(ictr + 80000);
                                    if (scheduleTimeex != null) {
                                        ((ViewManager) scheduleTimeex.getParent()).removeView(scheduleTimeex);
                                    }
                                    btn.setText(getString(R.string.schedule_test_button));
                                    btn.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        btn.setId(ictr + 10000);
                        cancelbtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v2) {
                                EditText emailtext = (EditText)findViewById(ictr + 20000);
                                if(emailtext != null) {
                                    ((ViewManager) emailtext.getParent()).removeView(emailtext);
                                }
                                Button gobtnex = (Button)findViewById(ictr + 30000);
                                if(gobtnex != null) {
                                    ((ViewManager) gobtnex.getParent()).removeView(gobtnex);
                                }
                                if(cancelbtn != null) {
                                    ((ViewManager) cancelbtn.getParent()).removeView(cancelbtn);
                                }
                                ImageButton scheduleDateBtnex = (ImageButton) findViewById(ictr + 70000);
                                if(scheduleDateBtnex != null){
                                    ((ViewManager) scheduleDateBtnex.getParent()).removeView(scheduleDateBtnex);
                                }
                                ImageButton scheduleTimeBtnex = (ImageButton) findViewById(ictr + 90000);
                                if(scheduleTimeBtnex != null){
                                    ((ViewManager) scheduleTimeBtnex.getParent()).removeView(scheduleTimeBtnex);
                                }
                                EditText scheduleDateex = (EditText)findViewById(ictr + 60000);
                                if(scheduleDateex != null){
                                    ((ViewManager) scheduleDateex.getParent()).removeView(scheduleDateex);
                                }
                                EditText scheduleTimeex = (EditText)findViewById(ictr + 80000);
                                if(scheduleTimeex != null){
                                    ((ViewManager) scheduleTimeex.getParent()).removeView(scheduleTimeex);
                                }
                                btn.setText(getString(R.string.schedule_test_button));
                                btn.setVisibility(View.VISIBLE);
                            }
                        });
                        fll.addView(gobtn);
                        fll.addView(cancelbtn);
                    }
                };
                btn.setOnClickListener(listener);
                lv.addView(btn);
                lv.addView(dividerNode);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }


    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Bundle args = new Bundle();
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        args.putInt("year",year);
        args.putInt("month", month);
        args.putInt("day", day);
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }


    private void showTimePicker() {
        DialogFragment time = new TimePickerFragment();
        Bundle args = new Bundle();
        Calendar cal=Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        int min=cal.get(Calendar.MINUTE);
        args.putInt("hour", hour);
        args.putInt("minute", min);
        time.setArguments(args);
        time.show(getFragmentManager(), "Time Picker");
    }


    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1; // this is 0 indexed, so had to add 1
            String mmstr = Integer.toString(monthOfYear);
            if(mmstr.length() < 2){
                mmstr = "0" + mmstr;
            }
            String ddstr = Integer.toString(dayOfMonth);
            if(ddstr.length() < 2){
                ddstr = "0" + ddstr;
            }
            EditText edittext_scheddate = (EditText) findViewById(target + 60000);
            edittext_scheddate.setText(new StringBuilder().append(String.valueOf(year)).append("-").append(mmstr).append("-").append(ddstr));
        }
    };


    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, min,DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hour, int min) {
            String hourstr = Integer.toString(hour);
            if(hourstr.length() < 2){
                hourstr = "0" + hourstr;
            }
            String minstr = Integer.toString(min);
            if(minstr.length() < 2){
                minstr = "0" + minstr;
            }
            edittext_schedtime.setText(new StringBuilder().append(String.valueOf(hourstr)).append(":").append(minstr));
        }
    }


    public int screenWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        return(width);
    }


    public int submitSchedule(int ctr, String testid){
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");
        EditText emailtext_edit = (EditText) findViewById(ctr + 20000);
        String emailtext = emailtext_edit.getText().toString();
        EditText sched_date_edit = (EditText)findViewById(ctr + 60000);
        String sched_date = sched_date_edit.getText().toString();
        EditText sched_time_edit = (EditText)findViewById(ctr + 80000);
        String sched_time = sched_time_edit.getText().toString();
        // Check the sanity of the data entered by user.
        String emailPattern = "\\w+@\\w+\\.";
        Pattern ep = Pattern.compile(emailPattern);
        Matcher epm = ep.matcher(emailtext);
        if(!epm.find()){
            // Display toast and quit
            Toast.makeText(getApplicationContext(), "You entered an invalid email address. Please rectify to continue", Toast.LENGTH_SHORT).show();
            return (0);
        }
        String datePattern = "\\d{4}\\-\\d{2}\\-\\d{2}";
        Pattern ed = Pattern.compile(datePattern);
        Matcher epd = ed.matcher(sched_date);
        if(!epd.find()){
            // Display toast and quit
            Toast.makeText(getApplicationContext(), "You entered an invalid date or a date in incorrect format. Please rectify to continue", Toast.LENGTH_SHORT).show();
            return (0);
        }
        String timePattern = "\\d{2}:\\d{2}";
        Pattern et = Pattern.compile(timePattern);
        Matcher ept = et.matcher(sched_time);
        if(!ept.find()){
            // Display toast and quit
            Toast.makeText(getApplicationContext(), "You entered an invalid time or a time in incorrect format. Please rectify to continue", Toast.LENGTH_SHORT).show();
            return (0);
        }
        final HashMap postDataParams = new HashMap<String, String>();
        postDataParams.put("emailtext", emailtext);
        postDataParams.put("sched_date", sched_date);
        postDataParams.put("sched_time", sched_time);
        postDataParams.put("testid", testid);
        postDataParams.put("username", username);

        cookiestr = "sessioncode=" + sessionid + "; usertype=" + usertype + "; csrftoken=";
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    String scheduleTestUrl = getString(R.string.schedule_test_url);
                    response = sendPostRequest(scheduleTestUrl, postDataParams);
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
        return (1);
    }


    public String showTestsByCreator(){
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");
        try {
            URL url;
            final String listTestsCreatorUrl = getString(R.string.list_tests_by_creator_url);
            url = new URL(listTestsCreatorUrl);
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
            //Log.d("RESPONSE CODE-----", Integer.toString(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null){
                    response+=line;
                }
            }
            else {
                response="{}";
            }
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

    // Some functions that should be in a common file... I must comsolidate these one of these days.
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
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
            String keystring = "schedule";
            String ivstring = "schedule";
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
                //response = Integer.toString(responseCode);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return (response);
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
}

