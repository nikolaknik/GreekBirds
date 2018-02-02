package gr.odikapoulia.greekbirds;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;
import android.widget.SeekBar;
import android.os.Handler;

public class KartelaPouliouActivity extends AppCompatActivity {

    String internetCheck;
    private ImageButton soundButton,b2;
    public MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    public static int oneTimeOnly = 0;


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ListaPoulionActivity.class);
        mediaPlayer.stop();

        mediaPlayer.reset();

        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getIntent().setAction("");
        startActivity(intent);


    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        internetCheck = intent.getStringExtra("internetCheck");
        System.out.println("internetCheck"+internetCheck);

        setContentView(R.layout.activity_kartela_pouliou);

        Button buttonBack;

        buttonBack = (Button)findViewById(R.id.buttonBack);


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent intent = new Intent(getApplicationContext(), ListaPoulionActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getIntent().setAction("");
                startActivity(intent);
            }
        });


        // fetch value from key-value pair and make it visible on TextView.
        String birdidFromList = intent.getStringExtra("birdid");
        System.out.println("birdidFromList "+birdidFromList);

        try {

            SQLiteDatabase mydatabase = openOrCreateDatabase("Bird", MODE_PRIVATE, null);

            Cursor resultSet = mydatabase.rawQuery("Select * from birdlist WHERE Id = "+birdidFromList, null);


            if (resultSet == null) {

                Toast.makeText(getApplicationContext(), "Ουπς κάτι πήγε στραβά... Παμε πάλι αργότερα", Toast.LENGTH_SHORT).show();

            }


            if (resultSet.moveToFirst()) {
                while (resultSet.isAfterLast()==false) {


                    String birdname = resultSet.getString(resultSet.getColumnIndex("Birdname"));

                    TextView birdnameTextView = (TextView)findViewById(R.id.birdName);
                    birdnameTextView.setText(birdname);
                    birdnameTextView.setMovementMethod(new ScrollingMovementMethod());


                    String birdInfo = resultSet.getString(resultSet.getColumnIndex("BirdInfo"));

                    TextView BirdInfoTextView = (TextView)findViewById(R.id.birdInfo);
                    BirdInfoTextView.setText(Html.fromHtml(birdInfo));
                    //BirdInfoTextView.setText(birdInfo);

                    int birdid = resultSet.getInt(resultSet.getColumnIndex("Id"));
                    int birdidNew = birdid - 1;
                    System.out.println("birdidNew " +birdidNew);



                    ImageView img= (ImageView) findViewById(R.id.birdImage);

                    String photoName = resultSet.getString(resultSet.getColumnIndex("Photo"));

                    int id = getResources().getIdentifier(photoName, "drawable", getPackageName());
                    img.setImageResource(id);
                    System.out.println("id " +id);

                    int playerid = getResources().getIdentifier(photoName, "raw", getPackageName());
                    System.out.println("playerid " +playerid);
                    if (playerid!=0){
                        mediaPlayer = MediaPlayer.create(this,playerid);
                    }else{
                        mediaPlayer = MediaPlayer.create(this, R.raw.nomusic);
                    }
                    resultSet.moveToNext();

                }
            }

        } catch (IndexOutOfBoundsException e) {

              Toast.makeText(getApplicationContext(), "Ουπς κάτι πήγε στραβά... Παμε πάλι αργότερα", Toast.LENGTH_SHORT).show();
        }


        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(true);
        b2 = (ImageButton) findViewById(R.id.bird_sound_pouse);
        soundButton = (ImageButton) findViewById(R.id.bird_sound);

        // Set a click listener for the popup window close button
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                b2.setEnabled(true);
                soundButton.setEnabled(false);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                b2.setEnabled(false);
                soundButton.setEnabled(true);
            }
        });

}

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

}
