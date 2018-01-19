package gr.odikapoulia.greekbirds;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String internetCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"Καλώς Ήρθατε...",Toast.LENGTH_LONG).show();


        InternetCheck myIncheck = new InternetCheck();

                if(myIncheck.isNetworkAvailable(getApplicationContext())){

                    internetCheck = "internetOk";

                    System.out.println(internetCheck);

                }else{

                    internetCheck = "internetNotOk";

                    System.out.println(internetCheck);

                }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ListaPoulionActivity.class);
                intent.putExtra("internetCheck",internetCheck);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        },3000);

    }

}