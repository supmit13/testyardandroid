package xpresstech.testyard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



public class TakeTestActivity extends AppCompatActivity {
    public final static String SELECTED_OPTION = "selected_option";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_take_test);

    }
}
