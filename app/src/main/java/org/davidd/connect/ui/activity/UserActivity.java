package org.davidd.connect.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.model.User;
import org.davidd.connect.ui.adapter.PresenceStatusAdapter;
import org.davidd.connect.util.BitmapUtil;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class UserActivity extends AppCompatActivity {

    public static final String USER_BUNDLE_TAG = "UserBundleTag";

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white);
        setSupportActionBar(toolbar);

        // TODO: investigate how fragment is opened

        user = createGsonWithExcludedFields().fromJson(getIntent().getStringExtra(USER_BUNDLE_TAG), User.class);
    }

    private void setUpUi() {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Marika");

        ImageView toolbarImageView = (ImageView) findViewById(R.id.toolbar_imageView);
        toolbarImageView.setImageBitmap(BitmapUtil.drawTextToBitmap("M"));

        TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_JID_textView);
        toolbarTextView.setText("marika@asdasd.asd");

        TextView nameTextView = (TextView) findViewById(R.id.frameLayout_name_textView);
        nameTextView.setText("Marika");

        TextView JIDTextView = (TextView) findViewById(R.id.frameLayout_JID_textView);
        JIDTextView.setText("marika@asd.asd/PIDGIN_12387126387126387");

        AppCompatSpinner presenceSpinner = (AppCompatSpinner) findViewById(R.id.frameLayout_presence_spinner);

        EditText statusEditText = (EditText) findViewById(R.id.frameLayout_status_editText);
        statusEditText.setText("Voltam tegnap nyaralni");

        Button logOutButton = (Button) findViewById(R.id.frameLayout_logOut_button);
        logOutButton.setText("Log out");

        PresenceStatusAdapter adapter = new PresenceStatusAdapter(this);
        presenceSpinner.setAdapter(adapter);
    }
}
