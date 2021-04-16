package com.easywriter.saam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

//Custom Adapter I created to display the writing.xml for Each note in the database
//Each of these note is displayed in the main screen (Where text and delete and share button shows)
//This Custom Adapter class will be called in view_writing.java

//This will extends the BaseAdapter class to use methods and function that Class
public class CustomAdapter extends BaseAdapter {
    //Initializing the variables to use in this class
    private ArrayList<Notes> noteList;
    private Context context;
    private SQLiteDatabase db;
    LayoutInflater inflater;


    //The contructor to set the passed items to this class, which I will need in later functions.
    public CustomAdapter(ArrayList<Notes> list, Context cont, SQLiteDatabase data){
        this.noteList = list;
        this.context = cont;
        this.db = data;
        inflater = (LayoutInflater.from(cont));
    }

    //I will be overRiding the getters of BaseAdapter class to return the specific note from the array list<Notes>.

    //get the size of the list.
    @Override
    public int getCount() {
        return this.noteList.size();
    }

    //Position is the index of the note in the NoteList list.

    //get The current Object of the note
    @Override
    public Object getItem(int position) {
        return this.noteList.get(position);
    }

    //get the id of the note.
    //As the recieved position is same as the id of the note, I am returning position
    //If it is different I need to do "return this.noteList.get(position).getId();"
    @Override
    public long getItemId(int position) {
        return position;
    }

    //This is the main part where the view of the note(saved writing in database) is returned.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //I used Inflater to set the returned (xml/layout) is the writing.xml
        convertView = inflater.inflate(R.layout.writting,null);
        //Set the variables to the design in layout files(writing.xml).
        TextView txt = convertView.findViewById(R.id.showUserEnteredText);
        TextView imageToBe = convertView.findViewById(R.id.showTextToGenerate);
        ImageButton share = convertView.findViewById(R.id.onShareButton);
        ImageButton delete = convertView.findViewById(R.id.onDeleteButton);
        Notes note = noteList.get(position);
        Typeface font = getTypeFace(note.getFont());
        //This function will style the view returned on home page, or view activity
        getBasicDesign(txt, note, font, imageToBe);
        //Handle when user click delete btn
        handleDeleteClicked(delete,note);
        //Handle when user click share btn
        handleShareClicked(share,imageToBe, convertView, note);
        return convertView;

    }

    //Handles generating and sharing of note in home screen(view_writting)
    //This share button will always share the text as image unlike the share button in show_writting.
    private void handleShareClicked(ImageButton share, TextView imageToBe, View convertView, Notes note) {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //I am setting the output(image that will be shared) as visible in home screen
                //But this wont be visible as the process is very fast
                imageToBe.setVisibility(View.VISIBLE);

                //Generating the snackbar to display when user click share button
                Snackbar confirm = Snackbar.make(convertView.findViewById(R.id.WritingLayout),"Still you can edit this text",Snackbar.LENGTH_SHORT);
                //Showing a clickable btn on the snackbar
                //the shareConfirmListener is called when clicked on the btn(Edit)
                //The shareConfirmListener is located under java files(not in this file)
                confirm.setAction("Edit", new shareConfirmListener(note, context));
                confirm.show();
                //This is called when the snakbar is dismissed( Then only sharing image is occur)
                confirm.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //Enable drawing cache on TextView so that I can create a bitmap from it
                        imageToBe.setDrawingCacheEnabled(true);
                        Bitmap pic = Bitmap.createBitmap(imageToBe.getDrawingCache());
                        //I am destroying the drawing cache so that memory will free up
                        imageToBe.destroyDrawingCache();
                        //Creating the image file
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        pic.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(context.getApplicationContext().getContentResolver(), pic,"EasyWriter"+ Calendar.getInstance().getTime(),null);
                        Uri imagePath = Uri.parse(path);

                        //Putting the image in the intent and sharing
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.putExtra(Intent.EXTRA_STREAM, imagePath);
                        //This setting type will make sure the shown apps are the apps that can recieve image.
                        share.setType("image/*");
                        context.startActivity(share);
                        imageToBe.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    /*This is create delete the selected note, I have created an Alert box to display a confirm message before deleting from database*/
    private void handleDeleteClicked(ImageButton delete, Notes note) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder alert;
                    alert = new AlertDialog.Builder(context);
                    alert.setMessage("Do you want to delete this text?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                //Query to delete from database when clicked yes
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String id = Integer.toString(note.getId());
                                    db.execSQL("delete from contents where id = "+id);
                                    Toast.makeText(context.getApplicationContext(),"Successfully Deleted", Toast.LENGTH_SHORT).show();
                                    ((view_writing) context).finish();
                                    //overRideTransition will hide the odd blackout hen the deleted item disappear from screen
                                    ((view_writing) context).overridePendingTransition(0,0);
                                    ((view_writing) context).startActivity(((view_writing) context).getIntent());
                                    ((view_writing) context).overridePendingTransition(0,0);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog showAlert = alert.create();
                    String title;
                    //Customizing the title of the alert dialog box
                    if (note.getText().length() > 4){
                        title = note.getText().substring(0,4)+ "...";
                    } else {
                        title = note.getText();
                    }
                    showAlert.setTitle(title);
                    showAlert.show();
                }catch (Exception e){
                    Toast.makeText(context.getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Set the design of the writting.xml to display in the home screen(view_wriitting)
    private void getBasicDesign(TextView txt, Notes note, Typeface font, TextView imageToBe) {
        txt.setTypeface(font);
        txt.setText(note.getText());
        txt.setTextColor(note.getColor());
        txt.setBackgroundColor(note.getBackgroundcolor());
        txt.setPadding(note.getPadding(),note.getPadding(),note.getPadding(),note.getPadding());
        txt.setTextSize(30);
        //This will to show screen by putting the required data(note) in the intent
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent show = new Intent(context, show_writing.class);
                show.putExtra("note", note);
                ((view_writing) context).startActivityForResult(show, 100);
            }
        });
        imageToBe.setBackgroundColor(note.getBackgroundcolor());
        imageToBe.setTypeface(font);
        imageToBe.setText(note.getText());
        imageToBe.setTextSize(TypedValue.COMPLEX_UNIT_PX,note.getSize());
        imageToBe.setPadding(note.getPadding(),note.getPadding(),note.getPadding(),note.getPadding());
        imageToBe.setTextColor(note.getColor());
        imageToBe.setGravity(note.getAlignment());
    }

    //A method I created that will return the custom typeface of the passed font name.
    private Typeface getTypeFace(String font) {
        Typeface fontFamily;
        switch (font){
            case "Arial":
                fontFamily = ResourcesCompat.getFont(context,R.font.arial);
                return fontFamily;
            case "Aka_Dora":
                fontFamily = ResourcesCompat.getFont(context,R.font.aka_dora);
                return fontFamily;
            case "Beyond Wonder Land":
                fontFamily = ResourcesCompat.getFont(context,R.font.beyond_wonderland);
                return fontFamily;
            case "Bold Stylish Calligraphy":
                fontFamily = ResourcesCompat.getFont(context,R.font.bold_stylish_calligraphy);
                return fontFamily;
            case "Extra Ornamentalno":
                fontFamily = ResourcesCompat.getFont(context,R.font.extra_ornamentalno);
                return fontFamily;
            case "Hugs Kisses Xoxo":
                fontFamily = ResourcesCompat.getFont(context,R.font.hugs_kisses_xoxo);
                return fontFamily;
            case "Little Lord Font Leroy":
                fontFamily = ResourcesCompat.getFont(context,R.font.little_lord_fontleroy);
                return fontFamily;
            case "Nemo Nightmares":
                fontFamily = ResourcesCompat.getFont(context,R.font.nemo_nightmares);
                return fontFamily;
            case "Princess Sofia":
                fontFamily = ResourcesCompat.getFont(context,R.font.princess_sofia);
                return fontFamily;
            case "Snipper":
                fontFamily = ResourcesCompat.getFont(context,R.font.sniper);
                return fontFamily;
            case "Underground":
                fontFamily = ResourcesCompat.getFont(context,R.font.underground);
                return fontFamily;
        }
        fontFamily = ResourcesCompat.getFont(context,R.font.arial);
        return fontFamily;
    }
}
