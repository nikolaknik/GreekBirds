package gr.odikapoulia.greekbirds;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class ListaPoulionActivity extends AppCompatActivity {

    private ListView lv;
    int numOfBirds = 1;
    String internetCheck;


    ArrayList<HashMap<String, String>> BirdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        internetCheck = intent.getStringExtra("internetCheck");
        System.out.println("internetCheck"+internetCheck);


        new GetData().execute();
        setContentView(R.layout.lista_poulion);
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

                Cursor resultSet = mydatabase.rawQuery("Select * from birdlist ", null);

                int checkBirds = resultSet.getCount();

                if(checkBirds != numOfBirds){

                    mydatabase.execSQL("DROP TABLE IF EXISTS birdlist;");

                    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS birdlist (Id INTEGER PRIMARY KEY AUTOINCREMENT, Birdname VARCHAR , BirdInfo VARCHAR , Photo VARCHAR, Result VARCHAR);");

                    mydatabase.execSQL(new QueryInsert().QueryInsert());
                }

                BirdList = new ArrayList<>();

                int sumForum = 0;
                String sumForumStr;

                Cursor allBirds = mydatabase.rawQuery("Select * from birdlist ", null);

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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

           }

        }

    }



}