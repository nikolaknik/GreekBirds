package gr.odikapoulia.greekbirds;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;
import android.media.MediaPlayer;


public class ListaPoulionActivity extends AppCompatActivity {

    private ListView lv;
    int numOfBirds = 1;
    public Boolean internetCheck;
    private AdView mAdView;
    public MediaPlayer mediaPlayer;
    private Context mContext;
    private Activity mActivity;

    private PopupWindow mPopupWindow;

    ArrayList<HashMap<String, String>> BirdList;


    @Override
    public void onBackPressed() {

        // Get the application context
        mContext = getApplicationContext();

        // Get the activity
        mActivity = ListaPoulionActivity.this;
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_exit,null);

        // Initialize a new instance of popup window
        boolean focusable = true;
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                focusable
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.nomusic);
        mediaPlayer.start();

       mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);

        Button buttonExit = (Button) customView.findViewById(R.id.exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getIntent().setAction("");
                startActivity(intent);
                finish();
                System.exit(0);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        new GetData().execute();

        setContentView(R.layout.lista_poulion);

        InternetCheck myIncheck = new InternetCheck();

        if(myIncheck.isNetworkAvailable(getApplicationContext())){

            internetCheck = true;

            MobileAds.initialize(this, "ca-app-pub-7771856024571036~9477512340");
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        }else{

            internetCheck = false;

            findViewById(R.id.adView).setVisibility(View.INVISIBLE);
        }

    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(ListaPoulionActivity.this,"Φόρτωση Δεδομένων",Toast.LENGTH_LONG).show();

        }


        @Override
        protected Void doInBackground(Void... arg0) {

            try {

                SQLiteDatabase mydatabase = openOrCreateDatabase("Bird", MODE_PRIVATE, null);

                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS birdlist (Id INTEGER PRIMARY KEY AUTOINCREMENT, Birdname VARCHAR , BirdInfo VARCHAR , Photo VARCHAR, Result VARCHAR);");

                Cursor resultSet = mydatabase.rawQuery("Select * from birdlist", null);

                int checkBirds = resultSet.getCount();
                System.out.println("checkBirds "+checkBirds);

                if(checkBirds != numOfBirds){

                    mydatabase.execSQL("DROP TABLE IF EXISTS birdlist;");

                    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS birdlist (Id INTEGER PRIMARY KEY AUTOINCREMENT, Birdname VARCHAR , BirdInfo VARCHAR , Photo VARCHAR, Result VARCHAR);");

                    mydatabase.execSQL(new QueryInsert().QueryInsert());
                }

                BirdList = new ArrayList<>();

                int sumForum = 0;
                String sumForumStr;

                Cursor allBirds = mydatabase.rawQuery("Select * from birdlist order by Birdname", null);

                if (allBirds.moveToFirst()) {
                                while (allBirds.isAfterLast()==false) {
                                    HashMap<String, String> list = new HashMap<>();

                                    sumForum = sumForum+1;

                                    sumForumStr = "" + sumForum;
                                    String birdname = allBirds.getString(allBirds.getColumnIndex("Birdname"));

                                    System.out.println("birdname "+birdname);

                                    String birdid = allBirds.getString(allBirds.getColumnIndex("Id"));

                                    String photoName = allBirds.getString(allBirds.getColumnIndex("Photo"));

                                    int id = getResources().getIdentifier(photoName, "drawable", getPackageName());

                                    list.put("Birdname", birdname);
                                    list.put("birdid", birdid);
                                    list.put("sumForumStr", sumForumStr);
                                    list.put("Photo", ""+id);
                                    BirdList.add(list);

                                    allBirds.moveToNext();
                                }
                            }

                if (allBirds != null && !allBirds.isClosed())
                    allBirds.close();

                mydatabase.close();

                } catch (IndexOutOfBoundsException e) {

                    Toast.makeText(getApplicationContext(), "Ουπς κάτι πήγε στραβά... Παμε πάλι αργότερα", Toast.LENGTH_SHORT).show();
                }


                return null;
            }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lv = (ListView) findViewById(R.id.bird_list);

           ListAdapter adapter = new SimpleAdapter(getBaseContext(), BirdList, R.layout.lista_poulion_texts,new String[]{"sumForumStr","Birdname","Photo","birdid"},new int[]{R.id.bird_id,R.id.bird_name,R.id.bird_photo,R.id.birdid});


            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new ListClickHandler());


        }

        public class ListClickHandler implements AdapterView.OnItemClickListener {

           @Override
           public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
        // TODO Auto-generated method stub
            TextView bird_id = view.findViewById(R.id.birdid);
            String birdid = bird_id.getText().toString();
            Intent intent = new Intent(ListaPoulionActivity.this, KartelaPouliouActivity.class);
            intent.putExtra("birdid", birdid);
            intent.putExtra("internetCheck",internetCheck);
            startActivity(intent);

           }

        }

    }

}