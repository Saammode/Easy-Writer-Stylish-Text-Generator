package com.easywriter.saam;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/*This class is responsible for showing the popup when share button is clicked. It is just extending Dialog class
and customizing dialog to the need of this application*/
public abstract class CustomShareDialog extends Dialog implements View.OnClickListener {
    public Activity activity;
    private Button copy;
    private Button shareText;
    private Button shareImage;

    //Constructor to initialize this activity in the passed activity.
    public CustomShareDialog(Activity a) {
        super(a);
        this.activity = a;
    }

    //Assign the id to variable.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //This will ensure that share layout(I created) will be displayed instead of defaults ones.
        setContentView(R.layout.share);
        //Assign id(layout) to variable created at top.
        setCopy(findViewById(R.id.copy));
        setShareText(findViewById(R.id.shareText));
        setShareImage(findViewById(R.id.shareImage));

        /*Set the onclick listener for each button, Please note that I have implement methods to handle
        this in the show_writing( where I call this Class ).*/
        getCopy().setOnClickListener(this);
        getShareImage().setOnClickListener(this);
        getShareText().setOnClickListener(this);

    }

    //getters and setters to get and set data
    public Button getCopy() {
        return copy;
    }

    public void setCopy(Button copy) {
        this.copy = copy;
    }

    public Button getShareText() {
        return shareText;
    }

    public void setShareText(Button shareText) {
        this.shareText = shareText;
    }

    public Button getShareImage() {
        return shareImage;
    }

    public void setShareImage(Button shareImage) {
        this.shareImage = shareImage;
    }
}
