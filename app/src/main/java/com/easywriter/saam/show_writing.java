package com.easywriter.saam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.chip.Chip;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import top.defaults.colorpicker.ColorPickerPopup;

//The java(controller) file for show_writing layout that shows the created notes detailed way,
public class show_writing extends AppCompatActivity {
    //Variables to set the views in layout file and Other required items
    private LinearLayout showScreen;
    private TextView showText;
    private Button share,delete;
    private Notes note;
    private View fontColor,backColor;
    private Button save,cancel,copy;
    private Chip alignLeft,alignCenter,alignRight;
    private LinearLayout.LayoutParams params;
    private Spannable html;
    private String userText, userFont, userSize, userPadding, userColor, userBackgroundColor, userAlignment, backgroundColor, showValue, textId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_writing);

        //getting the data(parcel data) in Intent,
        Intent details = getIntent();
        note = details.getParcelableExtra("note");

        //Views assiging to the above variables
        showScreen = findViewById(R.id.showScreen);
        showText = findViewById(R.id.showText);
        share = findViewById(R.id.share);
        delete = findViewById(R.id.delete);
        fontColor = findViewById(R.id.showColor);
        backColor = findViewById(R.id.showBackColor);
        save = findViewById(R.id.showSave);
        cancel = findViewById(R.id.showCancel);
        copy = findViewById(R.id.copy);
        alignLeft = findViewById(R.id.showAlignLeft);
        alignCenter = findViewById(R.id.showAlignCenter);
        alignRight = findViewById(R.id.showAlignRight);

        SeekBar size = findViewById(R.id.showSize);
        Spinner font = findViewById(R.id.showFont);
        Spinner spacing = findViewById(R.id.showSpacing);

        //I am using a spannable to set in the main textview in show_writing layout
        //This is because I am copy the html data format from spannable, So that I share formated texts using thoose html data
        html = new SpannableString(note.getText());

        //Even though I have turned off night mode also, in some mobiles background is turned black and gives bad formatting in terms of colors.
        int nightModeFlags = getApplication().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            Toast.makeText(this,"Turn off night mode for accurate formatting",Toast.LENGTH_LONG).show();
        }

        //Designing the spannable using the values we received from intend(originally from database)'
        html.setSpan(new ForegroundColorSpan(note.getColor()),0, note.getText().length(),Spannable.SPAN_COMPOSING);
        html.setSpan(new AbsoluteSizeSpan(Math.round(note.getSize())),0, note.getText().length(),Spannable.SPAN_COMPOSING);
        html.setSpan(new BackgroundColorSpan(note.getBackgroundcolor()),0,note.getText().length(), Spannable.SPAN_COMPOSING);
        html.setSpan(new CustomTypefaceSpan("",getTypeFace(note.getFont())), 0,note.getText().length(), Spannable.SPAN_COMPOSING);
        showText.setText(html);
        //setting the view colors (which is showed in the show_writing layout) so that users will know clicking the button will change color
        fontColor.setBackgroundColor(note.getColor());
        backColor.setBackgroundColor(note.getBackgroundcolor());

        setAdapterForDropdown(font, spacing);

        setLayoutGravityAndFontSizeSeekBarMinMax(size);

        setOnClickListenerForDeletebtn();

        setOnClickListenerShareBtn();

        setOnClickListenerForSize(size);


        setOnClickListenerForFontAndSpacing(font, spacing);

        setOnClickListenerForAlignmentBtns();

        setOnClickListenerForTextColor();

        setOnClickListenerForBackColor();

        setOnClickListenerForCloseBtn();

        setOnClickListenerForUpdateSaveBtn();

    }

    //This is responsible for managing and creating the dropdown at first place in show_writing
    private void setAdapterForDropdown(Spinner font, Spinner spacing) {
        //I am not creating the array adapter using createFromArray this this.
        //Instead I am initializing a new ArrayAdapter class and overriding the two method I need to set font, and colors.
        ArrayAdapter<String> fontAdapter = new ArrayAdapter<String>(this, R.layout.font_spinner, Arrays.asList(getResources().getStringArray(R.array.font))){
            //This method is the view returned when an item from font spinner is selected
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView fontView = (TextView) super.getView(position, convertView, parent);
                /*This will set the font in each spinner to its font using the function that I created (getTypeFace())
                As the text in the view will be font Name I am sending that to my custom getTypeface function*/
                String FontName = (String) fontView.getText();
                Typeface generatedTypeface = getTypeFace(FontName);
                fontView.setTypeface(generatedTypeface);
                return fontView;
            }

            //This method is the view returned when an item from font spinner dropdown is opened.
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView fontView = (TextView) super.getDropDownView(position, convertView, parent);
                /*This will set the font in each spinner to its font using the function that I created (getTypeFace())
                As the text in the view will be font Name I am sending that to my custom getTypeface function*/
                String FontName = (String) fontView.getText();
                Typeface generatedTypeface = getTypeFace(FontName);
                fontView.setTypeface(generatedTypeface);
                fontView.setTextColor(note.getColor());
                fontView.setBackgroundColor(note.getBackgroundcolor());
                return fontView;
            }
        };
        //Basic array setting I did as same in add_writing

        ArrayAdapter<CharSequence> spacingAdapter = ArrayAdapter.createFromResource(this,R.array.spacing, android.R.layout.simple_spinner_dropdown_item);

        fontAdapter.setDropDownViewResource(R.layout.font_spinner);
        spacingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        font.setAdapter(fontAdapter);
        spacing.setAdapter(spacingAdapter);

        //Sets the current selection based parcel(note)
        font.setSelection(fontAdapter.getPosition(note.getFont()));
        spacing.setSelection(spacingAdapter.getPosition(Integer.toString((note.getPadding()))));
    }

    //Handle the setting layout gravity and seek bar min max based on the data on parcel(note)
    private void setLayoutGravityAndFontSizeSeekBarMinMax(SeekBar size) {
        //Setting fontsize slider maximum and minimum value
        Float half = note.getSize() / 2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            size.setMin(Math.round(note.getSize() - half));
        }
        size.setMax(Math.round(note.getSize() + half));
        size.setProgress(Math.round(note.getSize()));

        //Setting the layout gravity for allignment(This is not just gravity, It is layout gravity)
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.gravity = note.getAlignment();
        showText.setLayoutParams(params);
    }

    //Handle the method when Dekete Btn is clicked
    private void setOnClickListenerForDeletebtn() {
        //Setting the listener for delete btn
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromDatabase();
            }
        });
    }

    //set Sharing using the custom dialog, when share btn is pressed
    private void setOnClickListenerShareBtn() {
        //setting the listener for share btn
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The is the CustomShareDialog that I created
                CustomShareDialog sharing = new CustomShareDialog(show_writing.this) {
                    //calles whe a click happen in custom dialog
                    @Override
                    public void onClick(View v) {
                        //Based on the appropriate button clicked in this Custom Dialog the function will be coset.
                        switch (v.getId()){
                            case R.id.copy:
                                copyTextToClipBoard();
                                break;
                            case R.id.shareImage:
                                shareTextAsImage();
                                break;
                            case R.id.shareText:
                                shareTextAsText();
                                break;
                        }
                    }
                };
                //After setting the listeners it is showing the dialog
                sharing.show();
            }
        });
    }

    //Set the text Size based on seekbar value
    private void setOnClickListenerForSize(SeekBar size) {
        //The when seek bar change the handling of methods will occur here. This is equalevent to what I did in add_writting
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //sets the size of the fontsize to seeked value
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String value = Integer.toString(progress);
                Float fontSize = Float.parseFloat(value);
                //Even if the spannable object is displayes in the textview
                //I am setting size because this way I can get the size of textview without playing with spannable in update method.
                showText.setTextSize(fontSize);
                html.setSpan(new AbsoluteSizeSpan(Math.round(fontSize)),0,note.getText().length(), Spanned.SPAN_COMPOSING);
                showText.setText(html);
                userSize = Float.toString(fontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Handle font and Spacing(dropdown btns)
    private void setOnClickListenerForFontAndSpacing(Spinner font, Spinner spacing) {
        //Setting the font when user selects a font from the dropdown.
        font.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Getting the selected item.
                    showValue = parent.getItemAtPosition(position).toString();
                    //setting the typeface using the custom method I created
                    showText.setTypeface(getTypeFace(showValue));
                    html.setSpan(new CustomTypefaceSpan("",getTypeFace(showValue)), 0,note.getText().length(), Spanned.SPAN_COMPOSING);
                    showText.setText(html);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"An error: "+ e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Setting the space method listener
        spacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spaceValue = parent.getItemAtPosition(position).toString();
                //Setting the padding.
                int space = Integer.parseInt(spaceValue);
                showText.setPadding(space,space,space,space);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Handle alignning text when based on appropriate btn
    private void setOnClickListenerForAlignmentBtns() {
        //Set the alignment to left when clicked
        alignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //using the params initialized at the very beginning to set gravity
                if (alignLeft.isChecked()) {
                    try {
                        params.gravity = Gravity.LEFT;
                        showText.setLayoutParams(params);
                        showText.setGravity(Gravity.LEFT);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Cant set alignment: "+e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Set the alignment to center when clicked
        alignCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //using the params initialized at the very beginning to set gravity
                if (alignCenter.isChecked()) {
                    try {
                        params.gravity = Gravity.CENTER;
                        showText.setLayoutParams(params);
                        showText.setGravity(Gravity.CENTER);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Cant set alignment: "+e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Set the alignment to center when clicked
        alignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //using the params initialized at the very beginning to set gravity
                if (alignRight.isChecked()){
                    try {
                        params.gravity = Gravity.RIGHT;
                        showText.setLayoutParams(params);
                        showText.setGravity(Gravity.RIGHT);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Cant set alignment: "+e, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    //Handles changing font color when that btn is pressed
    private void setOnClickListenerForTextColor() {
        //setting the font color using the color picker
        fontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(getApplicationContext())
                        .initialColor(Color.BLACK)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Ok")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                showText.setTextColor(color);
                                html.setSpan(new ForegroundColorSpan(color),0,note.getText().length(), Spanned.SPAN_COMPOSING);
                                showText.setText(html);
                                fontColor.setBackgroundColor(color);
                                userColor = Integer.toString(color);
                            }
                        });
            }
        });
    }

    //Handles changing background color when the back color btn is pressed
    private void setOnClickListenerForBackColor() {
        //setting the back color using the color picker
        backColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(getApplicationContext())
                        .initialColor(getResources().getColor(R.color.LightGrey))
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Ok")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                showText.setBackgroundColor(color);
                                html.setSpan(new BackgroundColorSpan(color),0, note.getText().length(), Spanned.SPAN_COMPOSING);
                                backColor.setBackgroundColor(color);
                                showText.setText(html);
                                backgroundColor = Integer.toString(color);
                            }
                        });
            }
        });
    }

    //Close the activity when called,
    private void setOnClickListenerForCloseBtn() {
        //Setting the onclick listener for close btn
        cancel.setOnClickListener(new View.OnClickListener() {
            //Close the current intent when clicked
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Save/Update the current state of the shown text to database
    private void setOnClickListenerForUpdateSaveBtn() {
        //Setting the onclick listener for save btn(In here it is called update button in view)
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Getting the data to be inserted into database
                    textId = Integer.toString(note.getId());
                    userText = showText.getText().toString();
                    userFont = showValue;
                    userPadding = Integer.toString(showText.getPaddingLeft());
                    if (userColor == null){
                        userColor = Integer.toString(note.getColor());
                    }
                    if (backgroundColor == null){
                        userBackgroundColor = Integer.toString(note.getBackgroundcolor());
                    } else {
                        userBackgroundColor = backgroundColor;
                    }
                    if (userSize == null){
                        userSize = Float.toString(note.getSize());
                    }
                    userAlignment  = Integer.toString(showText.getGravity());

                    SQLiteDatabase db = openOrCreateDatabase("easyText", MODE_PRIVATE, null);

                    //Updating the database
                    String sql = "update contents set text = ?,font = ?,size = ?,padding = ?,color = ?,backgroundcolor = ?,alignment = ? where id = ?";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1,userText);
                    statement.bindString(2,userFont);
                    statement.bindString(3,userSize);
                    statement.bindString(4,userPadding);
                    statement.bindString(5,userColor);
                    statement.bindString(6,userBackgroundColor);
                    statement.bindString(7,userAlignment);
                    statement.bindString(8,textId);
                    statement.execute();
                    Toast.makeText(getApplicationContext(), "Content updated!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error: "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //This function is called when delete button is present.
    private void deleteFromDatabase() {
        //get the current shown note ID and delete the record from database and close the activity, so that
        //view_writing will refresh and update the shown notes
        try {
            String id = Integer.toString(note.getId());

            SQLiteDatabase db = openOrCreateDatabase("easyText", MODE_PRIVATE,null);

            db.execSQL("delete from contents where id = "+id);
            Toast.makeText(getApplicationContext(),"Successfully Deleted", Toast.LENGTH_SHORT).show();
            finish();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    //This is method I need to call many times to get the Typeface based on the name of the font user select
    public  Typeface getTypeFace(String value){
        Typeface fontFamily;
        switch (value){
            case "Arial":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.arial);
                return fontFamily;
            case "Aka_Dora":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.aka_dora);
                return fontFamily;
            case "Beyond Wonder Land":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.beyond_wonderland);
                return fontFamily;
            case "Bold Stylish Calligraphy":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.bold_stylish_calligraphy);
                return fontFamily;
            case "Extra Ornamentalno":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.extra_ornamentalno);
                return fontFamily;
            case "Hugs Kisses Xoxo":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.hugs_kisses_xoxo);
                return fontFamily;
            case "Little Lord Font Leroy":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.little_lord_fontleroy);
                return fontFamily;
            case "Nemo Nightmares":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.nemo_nightmares);
                return fontFamily;
            case "Princess Sofia":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.princess_sofia);
                return fontFamily;
            case "Snipper":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.sniper);
                return fontFamily;
            case "Underground":
                fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.underground);
                return fontFamily;
        }
        fontFamily = ResourcesCompat.getFont(getApplicationContext(),R.font.arial);
        return fontFamily;
    }

    //This is called when user press the share as text in the custom dialog.
    public void shareTextAsText() {
        try {
            Intent sharer = new Intent(Intent.ACTION_SEND);
            String htmlText = Html.toHtml(html);
            //htmlText is a string that has the html code of the spannable TextView
            sharer.putExtra(Intent.EXTRA_HTML_TEXT, htmlText);
            sharer.putExtra(Intent.EXTRA_TEXT, note.getText());
            sharer.setType("text/html");
            startActivity(Intent.createChooser(sharer, "Check this wonderful Text created using Easy Writer App"));
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_LONG).show();
        }
    }

    //This is called when user press the share as image in the custom dialog.
    public void shareTextAsImage() {
        //get the textView and create bitmap and get the jpeg from the bitmap and share that jpeg file
        try {
            showText.setDrawingCacheEnabled(true);
            Bitmap pic = Bitmap.createBitmap(showText.getDrawingCache());
            showText.destroyDrawingCache();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            pic.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), pic,"EasyWriter" + Calendar.getInstance().getTime(),"This image it created by Easy Write, download Easy Write from PlayStore");
            Uri imagePath = Uri.parse(path);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, imagePath);
            share.setType("image/*");
            startActivity(share);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    //Function that will call when user press copy in custom share dialog
    public void copyTextToClipBoard() {
        //Copy the text in both plain text and html text
        //If html supported then html text will be pasted else plain text
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newHtmlText("meta", showText.getText(), Html.toHtml(html));
            clipboard.setPrimaryClip(data);
            Toast.makeText(getApplicationContext(),"Copied",Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error: "+e, Toast.LENGTH_LONG).show();
        }
    }
}