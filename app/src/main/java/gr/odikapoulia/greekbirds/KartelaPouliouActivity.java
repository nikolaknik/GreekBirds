package gr.odikapoulia.greekbirds;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;


public class KartelaPouliouActivity extends AppCompatActivity {

    String internetCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Intent intent = new Intent(getApplicationContext(), ListaPoulionActivity.class);
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

                    resultSet.moveToNext();

                }
            }

        } catch (IndexOutOfBoundsException e) {

              Toast.makeText(getApplicationContext(), "Ουπς κάτι πήγε στραβά... Παμε πάλι αργότερα", Toast.LENGTH_SHORT).show();
        }

}
}
