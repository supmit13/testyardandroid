package xpresstech.testyard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    public final static String SELECTED_OPTION = "selected_option";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void listTestsandInterviews(View view) {
        SharedPreferences apppref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = apppref.edit();
        String sessionId = apppref.getString("session_id", "");
        Intent intent = new Intent(this, ListTestsAndInterviewsActivity.class);
        if(sessionId == "") {
           intent = new Intent(this, LoginScreenActivity.class);
        }
        EditText editText = (EditText) findViewById(R.id.edit_testname);
        editText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        String message = editText.getText().toString();
        intent.putExtra(SELECTED_OPTION, 1);
        finish();
        startActivity(intent);
    }


    public void conductInterview(View view){
        Intent intent = new Intent(this, ConductInterviewActivity.class);
        intent.putExtra(SELECTED_OPTION, 3);
        finish();
        startActivity(intent);
    }


    public void scheduleTest(View view){
        Intent intent = new Intent(this, CreateTestActivity.class);
        intent.putExtra(SELECTED_OPTION, 2);
        finish();
        startActivity(intent);
    }


    public void scheduleInterview(View view){
        Intent intent = new Intent(this, CreateTestActivity.class);
        intent.putExtra(SELECTED_OPTION, 2);
        finish();
        startActivity(intent);
    }
}