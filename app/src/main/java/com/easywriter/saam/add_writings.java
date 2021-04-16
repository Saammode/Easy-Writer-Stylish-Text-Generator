package com.easywriter.saam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import top.defaults.colorpicker.ColorPickerPopup;

//controller responsible for methods of add writing(note) screen
public class add_writings extends AppCompatActivity {
    //Initializing variable so that I can reference activity views using this variable
    EditText text;
    private View fontColor,backColor;
    private Button save,cancel;
    private String value;
    private Chip alignLeft,alignCenter,alignRight;
    private ImageButton camera;

    //Method that is called when this activity is created(when loaded)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_writings);

        //This is connect the created variables to the design of activity using their id.
        text = findViewById(R.id.userEnteredText);
        fontColor = findViewById(R.id.color);
        backColor = findViewById(R.id.backColor);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        alignLeft = findViewById(R.id.alignLeft);
        alignCenter = findViewById(R.id.alignCenter);
        alignRight = findViewById(R.id.alignRight);
        camera = findViewById(R.id.Camera);

        SeekBar size = findViewById(R.id.size);
        Spinner font = findViewById(R.id.font);
        Spinner spacing = findViewById(R.id.spacing);

        createTheDropdownForFontSizeAndSpacing(font, spacing);

        onClickListenerForFontSize(size);

        onClickListenerForFontDropdown(font);

        onClickListenerForSpacingDropdown(spacing);

        onClickListenerForCameraBtn();

        onCLickListenerForAlignment();

        onClickListenerForTextColorBtn();

        onClickListenerForBackColorBtn();

        onClickListenerForCancelBtn();

        onClickListenerForSaveBtn();

    }

    //Create and set the dropdown list for font size and spacing
    private void createTheDropdownForFontSizeAndSpacing(Spinner font, Spinner spacing) {
        //I am not creating the array adapter using createFromResource.
        //Instead I am initializing a new ArrayAdapter class and overriding the two method I need to set font, and colors.
        //I am using the custom layout (font_spinner) in this array adapter
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
                return fontView;
            }
        };
        //In here I am using createFromResource because I do not need to override anything because default feature and look is fine for app.
        ArrayAdapter<CharSequence> spacingAdapter = ArrayAdapter.createFromResource(this,R.array.spacing, android.R.layout.simple_spinner_dropdown_item);

        //This will set the design of the dropdown list items, I am using pre-defined dropdown, instead of custom one.
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spacingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //It will set the created Adapter, so that string and those designs in adapter will be available for dropdown
        font.setAdapter(fontAdapter);
        spacing.setAdapter(spacingAdapter);
    }

    //Change font size when user increase or decrease the font size seekbar
    private void onClickListenerForFontSize(SeekBar size) {
        //This will be called when user slides the size bar, so that it will change font size based on that.
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String value = Integer.toString(progress);
                Float fontSize = Float.parseFloat(value);
                text.setTextSize(fontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Handles the setting font when user select a font from dropdown
    private void onClickListenerForFontDropdown(Spinner font) {
        //This will get the selected font at the spinner from the values at override adapter we set, and change the font of Edit Text to that font.
        font.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    value = parent.getItemAtPosition(position).toString();
                    text.setTypeface(getTypeFace(value));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"An error: "+ e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Handles setting the space when user select a number from dropdown
    private void onClickListenerForSpacingDropdown(Spinner spacing) {
        //This will get the selected number at the spinner, and change the padding of Edit Text to that number.
        spacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();

                int space = Integer.parseInt(value);
                text.setPadding(space,space,space,space);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Handles the opening camera when user press camera btn
    private void onClickListenerForCameraBtn() {
        //This will open the camera, when user click the camera icon at top of the layout.
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Please not that I have created OnActivity Result method right after onCreate method
                //to handle what to once image is captured
                startActivityForResult(openCamera,0);
            }
        });
    }

    //Handle when user click left,center or right align btn
    private void onCLickListenerForAlignment() {
        //This will be called when align Left icon is pressed, so that it will changed the text to left side.
        alignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alignLeft.isChecked()) {
                    text.setGravity(Gravity.LEFT);
                }
            }
        });

        //This will be called when align Center icon is pressed, so that it will changed the text to center.
        alignCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alignCenter.isChecked()) {
                    text.setGravity(Gravity.CENTER);
                }
            }
        });

        //This will be called when align Right icon is pressed, so that it will changed the text to right side.
        alignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alignRight.isChecked()){
                    text.setGravity(Gravity.RIGHT);
                }
            }
        });
    }

    //Handles changing text color when user press text color btn
    private void onClickListenerForTextColorBtn() {
        //This will set the text color of the Edit Text to users choice.
        //I am using a Color picker I added in build.gradle(app level)
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
                                text.setTextColor(color);
                                fontColor.setBackgroundColor(color);
                            }
                        });
            }
        });
    }

    //Handles setting background color of text when clicked Back Color Btn
    private void onClickListenerForBackColorBtn() {
        //This will set the background color of the Edit Text to users choice.
        //I am using a Color picker I added in build.gradle(app level)
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
                                text.setBackgroundColor(color);
                                backColor.setBackgroundColor(color);
                            }
                        });
            }
        });
    }

    //Handle exiting the screen when user click Close Btn
    private void onClickListenerForCancelBtn() {
        //This will close the current opened activity, so that user will be navigated to previous screen.
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Handle What to do when user click save btn
    private void onClickListenerForSaveBtn() {
        // This will save get the properties of edit text and save it to the database.
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String userText, userFont, userSize, userPadding, userColor, userBackgroundColor, userAlignment;

                    ColorDrawable backFromEditTxt = (ColorDrawable) text.getBackground();
                    userText = text.getText().toString();
                    userFont = value;
                    userSize = Float.toString(text.getTextSize());
                    userPadding = Integer.toString(text.getPaddingLeft());
                    userColor = Integer.toString(text.getCurrentTextColor());
                    Toast.makeText(getApplicationContext(),"BackColor: " + userColor, Toast.LENGTH_LONG).show();
                    userBackgroundColor = Integer.toString(backFromEditTxt.getColor());
                    userAlignment  = Integer.toString(text.getGravity());
                    SQLiteDatabase db = openOrCreateDatabase("easyText", MODE_PRIVATE, null);
                    db.execSQL("create table if not exists contents(id integer primary key autoincrement, "+"text varchar,font varchar,size varchar,padding varchar,color varchar, backgroundcolor varchar, alignment varchar)");

                    String sql = "insert into contents(text,font,size,padding,color,backgroundcolor,alignment) values (?,?,?,?,?,?,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1,userText);
                    statement.bindString(2,userFont);
                    statement.bindString(3,userSize);
                    statement.bindString(4,userPadding);
                    statement.bindString(5,userColor);
                    statement.bindString(6,userBackgroundColor);
                    statement.bindString(7,userAlignment);
                    statement.execute();
                    Toast.makeText(getApplicationContext(), "Writing Created", Toast.LENGTH_SHORT).show();
                    finish();
                    text.setText("");
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error: "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /*This function will receive the image captured, So I have used google ML KIT library to get texts
    in that picture and set those texts in the EditText Box so that you don't need to look to a paper and
    type anything.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            try {
                //Create a bitmap from that data in the photo captured. This is to create a JPEG fill that can be easily viewed
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                //get the bytes of the created bitmap with data
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //Compress image and set quality and image format(JPEG)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                //To save the image to gallery
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap,"EasyWriterTextRecognition",null);
                //convert the path of image file in string to Uri
                Uri imagePath = Uri.parse(path);
                //Inialize the InputImage to use
                InputImage image;
                //set the InputImage to that Uri of the captured photo
                image = InputImage.fromFilePath(this, imagePath);
                //Initialize the google ML-KIT TextRecognition class
                TextRecognizer recognizer = TextRecognition.getClient();
                //Trying to recognize the text from image
                Task<Text> result = recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            //Called when textRecognition process finished
                            @Override
                            public void onSuccess(Text recognizedText) {
                                String RESULT = recognizedText.getText();
                                text.setText(RESULT);
                                /*I am using if statement to get the length of recognized text because,
                                This function is called when processing finished even no text is recognized*/
                                if (RESULT.length() > 0){
                                    Toast.makeText(getApplicationContext(), "All available texts recognized",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "We couldnt find any texts in that image.", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            //When any error happen on text recognition
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed: "+e, Toast.LENGTH_LONG).show();
                            }
                        });

            }catch (Exception e){
                Toast.makeText(this,"An error occurred: "+e,Toast.LENGTH_LONG).show();
            }
        }
    }

    //This is method I called to get the Typeface based on the name of the font user select
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
}