package com.easywriter.saam;

import android.content.Context;
import android.content.Intent;
import android.view.View;

//Class to execute if user press edit when snackbar in view_writing occurs.
public class shareConfirmListener implements View.OnClickListener {
    private Notes note;
    private Context context;
    //Constructor, so that I can refer to this context to start the Intend to navigate to (show / edit) screen
    public shareConfirmListener(Notes note, Context context) {
        this.note = note;
        this.context = context;
    }

    //When user click edit in snackbar, A new intent will. This method will handle that.
    @Override
    public void onClick(View v) {
        //just navigating to showScreen.
        Intent show = new Intent(context, show_writing.class);
        show.putExtra("note", note);
        ((view_writing) context).startActivityForResult(show, 100);
    }
}
