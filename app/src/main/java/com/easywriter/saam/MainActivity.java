package com.easywriter.saam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;


//The main activity file, First java file to load when app run.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This will disable night mode for app even if system night mode is on.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        //This is a lottie animation file that I have displayed on the home screen().
        //The add button that have zoom in out animation
        final LottieAnimationView addBtn = findViewById(R.id.addBtn);

        //This will start add writing when clicked(on + button)
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(getApplicationContext(), add_writings.class);
                //I am handling the result of request code 100 at bottom
                startActivityForResult(add,100);
            }
        });

        //Check if user has created any notes
        try {
            SQLiteDatabase db = openOrCreateDatabase("easyText",MODE_PRIVATE,null);
            final Cursor c = db.rawQuery("select * from contents",null);
            if (c.getCount() <= 0){
                c.close();
            }else {
                //If there is notes created previously it will show view_writing layout
                c.close();
                Intent view = new Intent(getApplicationContext(), view_writing.class);
                startActivityForResult(view,100);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Create a writing to get started",Toast.LENGTH_LONG).show();
        }
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            //This is called when the result from the above mentioned class is getted(This will happen shen user close the add_writing class)
            // I am recreate() this activity because, then only the this whole code will run again so that database willl not be empty
            // Other wise even if user create a note it wont be showing on the home screen
            recreate();
        }
    }
}