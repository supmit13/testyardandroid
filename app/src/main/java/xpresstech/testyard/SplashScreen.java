package xpresstech.testyard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    //finish();
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
        try {
            timerThread.join();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
