package org.davidd.connect.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.davidd.connect.R;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserPresence;
import org.davidd.connect.ui.adapter.PresenceStatusAdapter;
import org.davidd.connect.util.BitmapUtil;
import org.davidd.connect.util.DataUtils;
import org.davidd.connect.util.DisplayUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class UserActivity extends AppCompatActivity {

    public static final String USER_BUNDLE_TAG = "UserBundleTag";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar_imageView)
    ImageView toolbarImageView;

    @Bind(R.id.toolbar_JID_textView)
    TextView toolbarTextView;

    @Bind(R.id.frameLayout_name_textView)
    TextView nameTextView;

    @Bind(R.id.frameLayout_JID_textView)
    TextView JIDTextView;

    @Bind(R.id.frameLayout_presence_spinner)
    AppCompatSpinner presenceSpinner;

    @Bind(R.id.frameLayout_status_editText)
    EditText statusEditText;

    @Bind(R.id.frameLayout_logOut_button)
    Button logOutButton;

    private User user;
    private boolean itIsTheCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        user = createGsonWithExcludedFields().fromJson(getIntent().getStringExtra(USER_BUNDLE_TAG), User.class);
        itIsTheCurrentUser = user.equals(UserManager.instance().getCurrentUser());

        UserPresence userPresence = RosterManager.instance().getUserPresenceForUser(user.getUserJIDProperties());

        toolbarImageView.setImageBitmap(BitmapUtil.drawTextToBitmap(user.getUserJIDProperties().getName().substring(0, 1)));
        toolbarTextView.setText(user.getUserJIDProperties().getNameAndDomain());
        nameTextView.setText(user.getUserJIDProperties().getName());
        JIDTextView.setText(user.getUserJIDProperties().getJID());

        if (DataUtils.isEmpty(userPresence.getPresence().getStatus())) {
            statusEditText.setVisibility(View.VISIBLE);
            statusEditText.setText(userPresence.getPresence().getStatus());
        } else {
            statusEditText.setVisibility(View.GONE);
        }

        if (itIsTheCurrentUser) {
            collapsingToolbarLayout.setTitle("It's me!");

            PresenceStatusAdapter adapter = new PresenceStatusAdapter(this);
            presenceSpinner.setAdapter(adapter);

            presenceSpinner.setEnabled(true);
            statusEditText.setEnabled(true);

            logOutButton.setVisibility(View.VISIBLE);
            logOutButton.setText("Log out");
        } else {
            collapsingToolbarLayout.setTitle(user.getUserJIDProperties().getName());

            presenceSpinner.setEnabled(false);
            statusEditText.setEnabled(false);

            logOutButton.setVisibility(View.GONE);
        }

        presenceSpinner.setSelection(userPresence.getUserPresenceType().ordinal());
        statusEditText.setCursorVisible(false);
    }

    @OnClick(R.id.frameLayout_status_editText)
    void onEditTextClick(View view) {
        statusEditText.setCursorVisible(true);
    }

    @OnEditorAction(R.id.frameLayout_status_editText)
    boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if ((keyEvent != null && keyEvent.getAction() == R.id.frameLayout_status_editText_imeId) || id == EditorInfo.IME_ACTION_GO) {
            Toast.makeText(this, statusEditText.getText(), Toast.LENGTH_SHORT).show();
            statusEditText.setCursorVisible(false);
            DisplayUtils.hideSoftKeyboard(this, statusEditText);
            return true;
        }
        return false;
    }

    @OnClick(R.id.frameLayout_logOut_button)
    void onLogOut(View view) {
        Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
    }
}
