package com.easywriter.saam;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;

//The basic Home Screen If user has created notes.
public class view_writing extends AppCompatActivity {

    ListView displayList;
    ArrayList<Notes> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_writing);

        //The lottie animation button and display list getting
        final LottieAnimationView addBtn = findViewById(R.id.addBtn);
        displayList = findViewById(R.id.displayList);

        //Add function to open add_writing layout when pressed
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(getApplicationContext(), add_writings.class);
                startActivityForResult(add,100);
            }
        });

        //gets the database contents to display
        SQLiteDatabase db = openOrCreateDatabase("easyText",MODE_PRIVATE,null);

        final Cursor c = db.rawQuery("select * from contents",null);
        int id = c.getColumnIndex("id");
        int text = c.getColumnIndex("text");
        int font = c.getColumnIndex("font");
        int size = c.getColumnIndex("size");
        int padding = c.getColumnIndex("padding");
        int color = c.getColumnIndex("color");
        int backgroundcolor = c.getColumnIndex("backgroundcolor");
        int alignment = c.getColumnIndex("alignment");

        //This will fill the details array with all records from database
        while (c.moveToNext()){
            String userId = c.getString(id);
            String userText = c.getString(text);
            String userFont = c.getString(font);
            String userSize = c.getString(size);
            String userPadding = c.getString(padding);
            String userColor = c.getString(color);
            String userBackgroundcolor = c.getString(backgroundcolor);
            String userAlignment = c.getString(alignment);

            Notes newNote = new Notes(userId,userText,userFont,userSize,userPadding,userColor,userBackgroundcolor,userAlignment);
            details.add(newNote);
        }
        //I am initializing my CustomAdapter to show each note as writing.xml
        //Details are explained in CustomAdapter class
        CustomAdapter myAdapter = new CustomAdapter(details,this, db);
        displayList.setAdapter(myAdapter);

    }

    //When user return back from add_writting this is called so that records will be updated.
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            recreate();
        }
    }


}