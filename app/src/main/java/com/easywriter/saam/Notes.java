package com.easywriter.saam;

import android.os.Parcel;
import android.os.Parcelable;

//This is the notes class that stores the retried data from database
// I am implementing the parcelable because that way I can pass this Note class object in the Intents without serialization
//It is said that parcel are faster than serializing the objects/Custom class.
public class Notes implements Parcelable {
    //Data types to use
    private int id;
    private String text;
    private String font;
    private float size;
    private int padding;
    private int color;
    private int backgroundcolor;
    private  int alignment;

    //The contructor to initialize the passed data to above variables
    public Notes(String UserId, String text, String font, String size, String padding, String color, String backgroundcolor, String alignment){
        //Assign the passed attributes from database to the data types, when Initialized
        this.id = Integer.parseInt(UserId);
        this.text = text;
        this.font = font;
        this.size = Float.parseFloat(size);
        this.padding = Integer.parseInt(padding);
        this.color = Integer.parseInt(color);
        this.backgroundcolor = Integer.parseInt(backgroundcolor);
        this.alignment = Integer.parseInt(alignment);
    }

    //Used to assign variable when using the Parcel
    protected Notes(Parcel in) {
        id = in.readInt();
        text = in.readString();
        font = in.readString();
        size = in.readFloat();
        padding = in.readInt();
        color = in.readInt();
        backgroundcolor = in.readInt();
        alignment = in.readInt();
    }

    //To create the Parcel
    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    //getter for the variables

    public int getId() { return  id; }

    public String getText() {
        return text;
    }

    public String getFont() {
        return font;
    }

    public float getSize() {
        return size;
    }

    public int getPadding() {
        return padding;
    }

    public int getColor() {
        return color;
    }

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public int getAlignment() { return alignment; }

    @Override
    public int describeContents() {
        return 0;
    }

    //Used to write the data into the parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeString(font);
        dest.writeFloat(size);
        dest.writeInt(padding);
        dest.writeInt(color);
        dest.writeInt(backgroundcolor);
        dest.writeInt(alignment);
    }
}
