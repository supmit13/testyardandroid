package xpresstech.testyard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChallengeCreationActivity extends AppCompatActivity {

    TextView messageText;
    Button uploadButton;
    int serverResponseCode = 0;
    String upLoadServerUri = null;
    final String uploadFilePath = "/mnt/sdcard/";

    String response;
    String cookiestr;

    TextView max_lines_text;
    EditText max_lines_count;
    Spinner challenge_types;
    TextView challenge_types_text;
    TextView one_or_more_text;
    RadioGroup one_or_more_rdgrp;
    RadioButton one_or_more_rdbtn_yes;
    RadioButton one_or_more_rdbtn_no;
    TextView choices_header_text;
    EditText fitb_editbox;
    TextView fitb_text;

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
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String testtypeselected = pref.getString("testtypeselected", "");
        String negativescorevalue = pref.getString("negativescorevalue", "");
        LinearLayout runtime_widgets_layout = (LinearLayout) findViewById(R.id.runtime_widgets_layout);
        if(negativescorevalue.equals("No")){
            EditText negscorefield = (EditText)findViewById(R.id.negative_score);
            TextView negscoretext = (TextView)findViewById(R.id.negativeScoreText);
            if(negscorefield != null){
                ((ViewManager)negscorefield.getParent()).removeView(negscorefield);
                ((ViewManager)negscoretext.getParent()).removeView(negscoretext);
            }
        }
        if(testtypeselected == "Coding" || testtypeselected == "Algorithm" || testtypeselected == "Subjective"){
            max_lines_text = new TextView(this);
            max_lines_count = new EditText(this);
            max_lines_text.setText(getString(R.string.response_lines_text));
            max_lines_count.setGravity(1);
            runtime_widgets_layout.addView(max_lines_text);
            runtime_widgets_layout.addView(max_lines_count);
        }
        else if(testtypeselected == "Composite") {
            // Add a select dropdown where the user may select the type of the current challenge
            challenge_types_text = new TextView(this);
            challenge_types_text.setText("Select Challenge Type: ");
            runtime_widgets_layout.addView(challenge_types_text);
            challenge_types = new Spinner(this);
            List<String> challengetypeoptions = new ArrayList<String>();
            challengetypeoptions.add("Coding");
            challengetypeoptions.add("Fill up the Blanks");
            challengetypeoptions.add("Algorithm");
            challengetypeoptions.add("Subjective");
            challengetypeoptions.add("Multiple Choice");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, challengetypeoptions);
            challenge_types.setAdapter(dataAdapter);
            challenge_types.setId(R.id.challenge_type);
            final android.content.Context cxt = this;
            //challenge_types.
            runtime_widgets_layout.addView(challenge_types);
            //final LinearLayout runtime_widgets_layout_inner = runtime_widgets_layout;
            final LinearLayout runtime_widgets_layout_initializer = runtime_widgets_layout;
            // Now handle user's selection
            challenge_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                    LinearLayout runtime_widgets_layout_inner = runtime_widgets_layout_initializer;
                    String selectedEntry = challenge_types.getItemAtPosition(position).toString();
                    if (selectedEntry == "Coding" || selectedEntry == "Algorithm" || selectedEntry == "Subjective"){
                        // Remove all multiple choices and fill up the blank views if they exist
                        RadioGroup rg_oneormore = null;
                        RadioButton rb_oneormore_yes = null;
                        RadioButton rb_oneormore_no = null;
                        try{
                            rg_oneormore = (RadioGroup)findViewById(R.id.oneormore);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try{
                            rb_oneormore_yes = (RadioButton)findViewById(R.id.oneormore_yes);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try{
                            rb_oneormore_no = (RadioButton)findViewById(R.id.oneormore_no);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(rg_oneormore != null){
                            ((ViewManager)rg_oneormore.getParent()).removeView(rg_oneormore);
                        }
                        if(rb_oneormore_yes != null){
                            ((ViewManager)rb_oneormore_yes.getParent()).removeView(rb_oneormore_yes);
                        }
                        if(rb_oneormore_no != null){
                            ((ViewManager)rb_oneormore_no.getParent()).removeView(rb_oneormore_no);
                        }
                        TextView multiple_chc_text_view = null;
                        try{
                            multiple_chc_text_view = (TextView)findViewById(R.id.multiple_chc_text);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(multiple_chc_text_view != null){
                            ((ViewManager)multiple_chc_text_view.getParent()).removeView(multiple_chc_text_view);
                        }
                        EditText [] optionstext = new EditText[8];
                        for(int i=0; i < 8; i++){
                            optionstext[i] = null;
                            try{
                                int optid = i + 100;
                                optionstext[i] = (EditText)findViewById(optid);
                            }
                            catch(Exception e){
                                Log.d("Error: ", "Could not find the desired element");
                                e.printStackTrace();
                            }
                            if(optionstext[i] != null){
                                ((ViewManager)optionstext[i].getParent()).removeView(optionstext[i]);
                            }
                        }
                        // Remove fill up the blank views
                        TextView filb_caption = null;
                        EditText filb_text_field = null;
                        try {
                            filb_caption = (TextView) findViewById(R.id.filb_correct_resp_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try {
                            filb_text_field = (EditText) findViewById(R.id.filb_correct_resp_field_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(filb_caption != null){
                            ((ViewManager)filb_caption.getParent()).removeView(filb_caption);
                        }
                        if(filb_text_field != null){
                            ((ViewManager)filb_text_field.getParent()).removeView(filb_text_field);
                        }
                        // Now draw the relevant views for this challenge type
                        TextView output = null;
                        try{
                            output = (TextView) findViewById(R.id.max_text_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(output == null){
                            max_lines_text = new TextView(cxt);
                            max_lines_count = new EditText(cxt);
                            max_lines_text.setText(getString(R.string.response_lines_text));
                            max_lines_text.setId(R.id.max_text_id);
                            max_lines_count.setId(R.id.max_count_id);
                            max_lines_count.setGravity(1);
                            runtime_widgets_layout_inner.addView(max_lines_text);
                            runtime_widgets_layout_inner.addView(max_lines_count);
                        }
                    }
                    else if(selectedEntry == "Multiple Choice"){
                        // Delete the textviews with id = 'max_test_id' and 'max_count_id'
                        TextView max_text_view = null;
                        TextView max_count_view = null;
                        try {
                            max_text_view = (TextView) findViewById(R.id.max_text_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try {
                            max_count_view = (TextView) findViewById(R.id.max_count_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(max_text_view != null){
                            ((ViewManager)max_text_view.getParent()).removeView(max_text_view);
                        }
                        if(max_count_view != null){
                            ((ViewManager)max_count_view.getParent()).removeView(max_count_view);
                        }
                        // Eliminate all Fill up the blank stuff
                        TextView filb_caption = null;
                        EditText filb_text_field = null;
                        try {
                            filb_caption = (TextView) findViewById(R.id.filb_correct_resp_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try {
                            filb_text_field = (EditText) findViewById(R.id.filb_correct_resp_field_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(filb_caption != null){
                            ((ViewManager)filb_caption.getParent()).removeView(filb_caption);
                        }
                        if(filb_text_field != null){
                            ((ViewManager)filb_text_field.getParent()).removeView(filb_text_field);
                        }
                        // Now draw the views relevant to this challenge type
                        RadioGroup output = null;
                        try{
                            output = (RadioGroup)findViewById(R.id.oneormore);
                        }
                        catch(Exception e){
                            Log.d("News: ", "Could not find the radio group I was looking for. So we need to create it.");
                        }
                        if(output == null) {
                            final TextView multiple_chc_oneormore = new TextView(cxt);
                            multiple_chc_oneormore.setText(getString(R.string.multiple_chc_oneormore_text));
                            multiple_chc_oneormore.setId(R.id.multiple_chc_text);
                            runtime_widgets_layout_inner.addView(multiple_chc_oneormore);
                            final RadioButton[] rb = new RadioButton[2];
                            RadioGroup oneormore = new RadioGroup(cxt); //create the RadioGroup
                            oneormore.setOrientation(RadioGroup.HORIZONTAL);
                            rb[0] = new RadioButton(cxt);
                            rb[1] = new RadioButton(cxt);
                            rb[0].setText("Yes");
                            rb[1].setText("No");
                            rb[0].setId(R.id.oneormore_yes);
                            rb[1].setId(R.id.oneormore_no);
                            oneormore.setId(R.id.oneormore);
                            oneormore.addView(rb[0]);
                            oneormore.addView(rb[1]);
                            runtime_widgets_layout_inner.addView(oneormore);

                            final TextView multiple_chc_options_text = new TextView(cxt);
                            multiple_chc_options_text.setText(getString(R.string.multiple_chc_options_text));
                            multiple_chc_options_text.setId(R.id.multiple_chc_text);
                            runtime_widgets_layout_inner.addView(multiple_chc_options_text);

                            final EditText [] options = new EditText[8];
                            for(int i=0; i < 8; i++){
                                options[i] = new EditText(cxt);
                                options[i].setHint("Option #" + (i + 1) + ") ");
                                if(i == 0) {
                                    options[i].setId(R.id.option_1);
                                }
                                else if(i == 1){
                                    options[i].setId(R.id.option_2);
                                }
                                else if(i == 2){
                                    options[i].setId(R.id.option_3);
                                }
                                else if(i == 3){
                                    options[i].setId(R.id.option_4);
                                }
                                else if(i == 4){
                                    options[i].setId(R.id.option_5);
                                }
                                else if(i == 5){
                                    options[i].setId(R.id.option_6);
                                }
                                else if(i == 6){
                                    options[i].setId(R.id.option_7);
                                }
                                else if(i == 7){
                                    options[i].setId(R.id.option_8);
                                }
                                runtime_widgets_layout_inner.addView(options[i]);
                            }
                        }
                    }
                    else if(selectedEntry == "Fill up the Blanks"){
                        // Remove all multiple choices and coding, algo and subjective views if they exist
                        RadioGroup rg_oneormore = null;
                        RadioButton rb_oneormore_yes = null;
                        RadioButton rb_oneormore_no = null;
                        try{
                            rg_oneormore = (RadioGroup)findViewById(R.id.oneormore);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try{
                            rb_oneormore_yes = (RadioButton)findViewById(R.id.oneormore_yes);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try{
                            rb_oneormore_no = (RadioButton)findViewById(R.id.oneormore_no);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(rg_oneormore != null){
                            ((ViewManager)rg_oneormore.getParent()).removeView(rg_oneormore);
                        }
                        if(rb_oneormore_yes != null){
                            ((ViewManager)rb_oneormore_yes.getParent()).removeView(rb_oneormore_yes);
                        }
                        if(rb_oneormore_no != null){
                            ((ViewManager)rb_oneormore_no.getParent()).removeView(rb_oneormore_no);
                        }
                        TextView multiple_chc_text_view = null;
                        try{
                            multiple_chc_text_view = (TextView)findViewById(R.id.multiple_chc_text);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(multiple_chc_text_view != null){
                            ((ViewManager)multiple_chc_text_view.getParent()).removeView(multiple_chc_text_view);
                        }
                        EditText [] optionstext = new EditText[8];
                        for(int i=0; i < 8; i++){
                            optionstext[i] = null;
                            try{
                                int optid = i + 100;
                                optionstext[i] = (EditText)findViewById(optid);
                            }
                            catch(Exception e){
                                Log.d("Error: ", "Could not find the desired element");
                                e.printStackTrace();
                            }
                            if(optionstext[i] != null){
                                ((ViewManager)optionstext[i].getParent()).removeView(optionstext[i]);
                            }
                        }
                        TextView max_text_view = null;
                        TextView max_count_view = null;
                        try {
                            max_text_view = (TextView) findViewById(R.id.max_text_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        try {
                            max_count_view = (TextView) findViewById(R.id.max_count_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(max_text_view != null){
                            ((ViewManager)max_text_view.getParent()).removeView(max_text_view);
                        }
                        if(max_count_view != null){
                            ((ViewManager)max_count_view.getParent()).removeView(max_count_view);
                        }
                        // Now draw the views relevant for this challenge type;
                        TextView correct_resp_caption = null;
                        try{
                            correct_resp_caption = (TextView) findViewById(R.id.filb_correct_resp_id);
                        }
                        catch(Exception e){
                            Log.d("Error: ", "Could not find the desired element");
                            e.printStackTrace();
                        }
                        if(correct_resp_caption == null){
                            correct_resp_caption = new TextView(cxt);
                            correct_resp_caption.setText(getString(R.string.filb_response_caption_text));
                            correct_resp_caption.setId(R.id.filb_correct_resp_id);
                            runtime_widgets_layout_inner.addView(correct_resp_caption);
                            EditText filb_correct_resp_field = new EditText(cxt);
                            filb_correct_resp_field.setId(R.id.filb_correct_resp_field_id);
                            runtime_widgets_layout_inner.addView(filb_correct_resp_field);
                        }
                    }
                    else{
                        Log.d("ERROR: ", "Unsupported Challenge Type");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing...
                    return;
                }
            });
        }
        else if(testtypeselected == "Multiple Choice"){
            one_or_more_text = new TextView(this);
            one_or_more_text.setText("Can there be more than one correct option: ");
            runtime_widgets_layout.addView(one_or_more_text);
            one_or_more_rdgrp = new RadioGroup(this);
            one_or_more_rdgrp.setOrientation(RadioGroup.HORIZONTAL);
            one_or_more_rdbtn_yes = new RadioButton(this);
            one_or_more_rdbtn_no = new RadioButton(this);
            one_or_more_rdgrp.addView(one_or_more_rdbtn_yes);
            one_or_more_rdgrp.addView(one_or_more_rdbtn_no);
            one_or_more_rdbtn_yes.setText("Yes");
            one_or_more_rdbtn_no.setText("No");
            runtime_widgets_layout.addView(one_or_more_rdgrp);
            choices_header_text = new TextView(this);
            choices_header_text.setText("Please enter available options (max 8):");
            runtime_widgets_layout.addView(choices_header_text);
            // Add checkboxes options available for the challenge
            int max_choices_count = 8;
            final EditText[] choice_statements = new EditText[max_choices_count];
            for(int ctr=0; ctr < max_choices_count; ctr++){
                choice_statements[ctr] = new EditText(this);
                runtime_widgets_layout.addView(choice_statements[ctr]);
                //choice_statements[ctr].setText("Option #" + (ctr + 1));
                choice_statements[ctr].setHint("Option #" + (ctr + 1));
                if(ctr == 0) {
                    choice_statements[ctr].setId(R.id.option_1);
                }
                else if(ctr == 1){
                    choice_statements[ctr].setId(R.id.option_2);
                }
                else if(ctr == 2){
                    choice_statements[ctr].setId(R.id.option_3);
                }
                else if(ctr == 3){
                    choice_statements[ctr].setId(R.id.option_4);
                }
                else if(ctr == 4){
                    choice_statements[ctr].setId(R.id.option_5);
                }
                else if(ctr == 5){
                    choice_statements[ctr].setId(R.id.option_6);
                }
                else if(ctr == 6){
                    choice_statements[ctr].setId(R.id.option_7);
                }
                else if(ctr == 7){
                    choice_statements[ctr].setId(R.id.option_8);
                }
                else{
                    // This is past our expectations right now
                }
            }
        }
        else if(testtypeselected == "Fill up the Blanks"){
            // Add a textbox inside a statement in which the creator might add a reference answer
            fitb_text = new TextView(this);
            fitb_text.setText("Enter the missing value below:");
            runtime_widgets_layout.addView(fitb_text);
            fitb_editbox = new EditText(this);
            fitb_editbox.setHint("Enter missing value here");
            fitb_editbox.setId(R.id.filb_correct_resp_field_id);
            runtime_widgets_layout.addView(fitb_editbox);
        }
    }


    public void addChallenge(View view){
        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", "");
        String sessionid = pref.getString("session_id", "");
        String usertype = pref.getString("usertype", "");
        String testName = pref.getString("testname", "");
        String testtypeselected = pref.getString("testtypeselected", "");
        String negativescorevalue = pref.getString("negativescorevalue", "");
        String testlinkid = pref.getString("testlinkid", "");

        String filbresponsestr = "";
        String challengeType = "";
        String oneormorevalue = "";

        String opt_1_val = "";
        String opt_2_val = "";
        String opt_3_val = "";
        String opt_4_val = "";
        String opt_5_val = "";
        String opt_6_val = "";
        String opt_7_val = "";
        String opt_8_val = "";

        final String uploadFileName = "challenge_" + username + "_" + sessionid; // File extension needs to be extracted from the real filename later.

        EditText challenge_statement   = (EditText)findViewById(R.id.challenge_statement);
        String challengeStatement = challenge_statement.getText().toString();

        EditText external_resource_url = (EditText)findViewById(R.id.external_resource_url);
        String externalResourceUrl = external_resource_url.getText().toString();

        String responseLinesCount = "";
        if(testtypeselected == "Coding" || testtypeselected == "Algorithm" || testtypeselected == "Subjective"){
            EditText response_lines_count = (EditText)findViewById(R.id.response_lines_count);
            responseLinesCount = response_lines_count.getText().toString();
            challengeType = testtypeselected;
        }
        else if(testtypeselected == "Fill up the Blanks"){
            EditText filbresponse = (EditText)findViewById(R.id.filb_correct_resp_field_id);
            filbresponsestr = filbresponse.getText().toString();
            challengeType = testtypeselected;
        }
        else if(testtypeselected == "Multiple Choice"){
            RadioGroup oneormoregrp = (RadioGroup) findViewById(R.id.oneormore);
            int selectedId1 = oneormoregrp.getCheckedRadioButtonId();
            RadioButton rdbtn1 = (RadioButton) findViewById(selectedId1);
            oneormorevalue = (String) rdbtn1.getText();
            challengeType = testtypeselected;

            EditText option01 = (EditText)findViewById(R.id.option_1);
            opt_1_val = option01.getText().toString();
            EditText option02 = (EditText)findViewById(R.id.option_2);
            opt_2_val = option02.getText().toString();
            EditText option03 = (EditText)findViewById(R.id.option_3);
            opt_3_val = option03.getText().toString();
            EditText option04 = (EditText)findViewById(R.id.option_4);
            opt_4_val = option04.getText().toString();
            EditText option05 = (EditText)findViewById(R.id.option_5);
            opt_5_val = option05.getText().toString();
            EditText option06 = (EditText)findViewById(R.id.option_6);
            opt_6_val = option06.getText().toString();
            EditText option07 = (EditText)findViewById(R.id.option_7);
            opt_7_val = option07.getText().toString();
            EditText option08 = (EditText)findViewById(R.id.option_8);
            opt_8_val = option08.getText().toString();
        }
        else if(testtypeselected == "Composite"){
            Spinner challenge_type = (Spinner)findViewById(R.id.challenge_type);
            challengeType = challenge_type.getSelectedItem().toString();
            if(challengeType == "Coding" || challengeType == "Subjective" || challengeType == "Algorithm"){
                EditText response_lines_count = (EditText)findViewById(R.id.response_lines_count);
                responseLinesCount = response_lines_count.getText().toString();
            }
            else if(challengeType == "Fill up the Blanks"){
                EditText filbresponse = (EditText)findViewById(R.id.filb_correct_resp_field_id);
                filbresponsestr = filbresponse.getText().toString();
            }
            else if(challengeType == "Multiple Choice"){
                RadioGroup oneormoregrp = (RadioGroup) findViewById(R.id.oneormore);
                int selectedId1 = oneormoregrp.getCheckedRadioButtonId();
                RadioButton rdbtn1 = (RadioButton) findViewById(selectedId1);
                oneormorevalue = (String) rdbtn1.getText();
                EditText option01 = (EditText)findViewById(R.id.option_1);
                opt_1_val = option01.getText().toString();
                EditText option02 = (EditText)findViewById(R.id.option_2);
                opt_2_val = option02.getText().toString();
                EditText option03 = (EditText)findViewById(R.id.option_3);
                opt_3_val = option03.getText().toString();
                EditText option04 = (EditText)findViewById(R.id.option_4);
                opt_4_val = option04.getText().toString();
                EditText option05 = (EditText)findViewById(R.id.option_5);
                opt_5_val = option05.getText().toString();
                EditText option06 = (EditText)findViewById(R.id.option_6);
                opt_6_val = option06.getText().toString();
                EditText option07 = (EditText)findViewById(R.id.option_7);
                opt_7_val = option07.getText().toString();
                EditText option08 = (EditText)findViewById(R.id.option_8);
                opt_8_val = option08.getText().toString();
            }
        }

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
        //postDataParams.put("responseLinesCount", responseLinesCount);
        postDataParams.put("challengeScore", challengeScore);
        postDataParams.put("negativeScore", negativeScore);
        postDataParams.put("maxTimeLimit", maxTimeLimit);
        postDataParams.put("challengeQuality", challengeQuality);
        postDataParams.put("compulsoryChallenge", compulsoryChallenge);
        postDataParams.put("testname", testName);
        postDataParams.put("testlinkid", testlinkid);
        postDataParams.put("testtypeselected", testtypeselected);
        postDataParams.put("challengeType", challengeType);
        postDataParams.put("maxResponseLinesCount", responseLinesCount);
        postDataParams.put("filbResponseStr", filbresponsestr);
        postDataParams.put("oneOrMoreValues", oneormorevalue);
        ;
        postDataParams.put("option1Value", opt_1_val);
        postDataParams.put("option2Value", opt_2_val);
        postDataParams.put("option3Value", opt_3_val);
        postDataParams.put("option4Value", opt_4_val);
        postDataParams.put("option5Value", opt_5_val);
        postDataParams.put("option6Value", opt_6_val);
        postDataParams.put("option7Value", opt_7_val);
        postDataParams.put("option8Value", opt_8_val);

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
