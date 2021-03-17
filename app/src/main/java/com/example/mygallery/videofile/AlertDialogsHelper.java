package com.example.mygallery.videofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;

import com.example.mygallery.R;

import java.lang.reflect.Field;

public class AlertDialogsHelper {
    public static AlertDialog getInsertTextDialog(Activity context, Builder dialogBuilder, EditText editText, @StringRes int title) {
        View dialogLayout = context.getLayoutInflater().inflate(R.layout.dialog_insert_text, null);
        TextView textViewTitle = (TextView) dialogLayout.findViewById(R.id.rename_title);
        ((CardView) dialogLayout.findViewById(R.id.dialog_chose_provider_title)).setCardBackgroundColor(Color.WHITE);
       textViewTitle.setBackgroundColor(Color.GRAY);
        textViewTitle.setText(title);
       // ThemeHelper.setCursorDrawableColor(editText, activity.getTextColor());
        editText.setLayoutParams(new LayoutParams(-1, -2));
        editText.setSingleLine(true);
      //  editText.getBackground().mutate().setColorFilter(context.getTextColor(), Mode.SRC_IN);
       editText.setTextColor(Color.BLACK);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, null);
        } catch (Exception e) {
        }
        ((RelativeLayout) dialogLayout.findViewById(R.id.container_edit_text)).addView(editText);
        dialogBuilder.setView(dialogLayout);
        return dialogBuilder.create();
    }

    public static AlertDialog getTextDialog(DashboardActivity activity, Builder textDialogBuilder, @StringRes int title, @StringRes int Message) {
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_text, null);
        TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.text_dialog_title);
        TextView dialogMessage = (TextView) dialogLayout.findViewById(R.id.text_dialog_message);
        ((CardView) dialogLayout.findViewById(R.id.message_card)).setCardBackgroundColor(Color.WHITE);
        dialogTitle.setBackgroundColor(Color.GRAY);
        dialogTitle.setText(title);
        dialogMessage.setText(Message);
        textDialogBuilder.setView(dialogLayout);
        return textDialogBuilder.create();
    }

    public static AlertDialog getProgressDialog(Activity activity, Builder progressDialog, String title, String message) {
        View dialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_progress, null);
        TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.progress_dialog_title);
        TextView dialogMessage = (TextView) dialogLayout.findViewById(R.id.progress_dialog_text);
        dialogTitle.setBackgroundColor(Color.WHITE);
        ((CardView) dialogLayout.findViewById(R.id.progress_dialog_card)).setCardBackgroundColor(Color.WHITE);
        ((ProgressBar) dialogLayout.findViewById(R.id.progress_dialog_loading)).getIndeterminateDrawable().setColorFilter(Color.BLACK, Mode.SRC_ATOP);
        dialogTitle.setText(title);
        dialogMessage.setText(message);
       dialogMessage.setTextColor(Color.BLACK);
        progressDialog.setCancelable(false);
        progressDialog.setView(dialogLayout);
        return progressDialog.create();
    }

}
