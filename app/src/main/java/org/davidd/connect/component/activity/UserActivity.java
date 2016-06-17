package org.davidd.connect.component.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.davidd.connect.R;
import org.davidd.connect.component.adapter.PresenceStatusAdapter;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserPresence;
import org.davidd.connect.model.UserPresenceType;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.BitmapUtil;
import org.davidd.connect.util.DataUtils;
import org.davidd.connect.util.DisplayUtils;
import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class UserActivity extends BaseAppCompatActivity implements AdapterView.OnItemSelectedListener {

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

    @Bind(R.id.user_location_imageButton)
    ImageButton locationImageButton;

    @Bind(R.id.frameLayout_logOut_button)
    Button logOutButton;

    private PresenceStatusAdapter adapter;

    private User user;

    private UserPresenceType previousPresenceType;
    private String previousStatus;

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
        boolean itIsTheCurrentUser = user.equals(UserManager.instance().getCurrentUser());

        UserPresence userPresence = RosterManager.instance().getUserPresenceForUser(user.getUserJIDProperties());

        toolbarImageView.setImageBitmap(BitmapUtil.drawTextToBitmap(user.getUserJIDProperties().getName().substring(0, 1)));
        toolbarTextView.setText(user.getUserJIDProperties().getNameAndDomain());
        nameTextView.setText(user.getUserJIDProperties().getName());
        JIDTextView.setText(user.getUserJIDProperties().getJID());

        if (itIsTheCurrentUser) {
            statusEditText.setHint(Html.fromHtml("<i>Set your status...</i>"));
        } else {
            statusEditText.setHint(Html.fromHtml("<i>No status added...</i>"));
        }

        if (!DataUtils.isEmpty(userPresence.getPresence().getStatus())) {
            statusEditText.setText(userPresence.getPresence().getStatus());
        }

        adapter = new PresenceStatusAdapter(this);
        presenceSpinner.setAdapter(adapter);
        presenceSpinner.setSelection(userPresence.getUserPresenceType().ordinal(), true);

        if (itIsTheCurrentUser) {
            collapsingToolbarLayout.setTitle("It's me!");
            statusEditText.setCursorVisible(false);
            logOutButton.setText("Log out");
        } else {
            collapsingToolbarLayout.setTitle(user.getUserJIDProperties().getName());

            presenceSpinner.setEnabled(false);
            statusEditText.setEnabled(false);

            logOutButton.setVisibility(View.GONE);
        }

        presenceSpinner.setOnItemSelectedListener(this);

        previousPresenceType = (UserPresenceType) presenceSpinner.getSelectedItem();
        previousStatus = statusEditText.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @OnClick(R.id.frameLayout_status_editText)
    void onEditTextClick(View view) {
        statusEditText.selectAll();
        statusEditText.setCursorVisible(true);
    }

    @OnEditorAction(R.id.frameLayout_status_editText)
    boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if ((keyEvent != null && keyEvent.getAction() == R.id.frameLayout_status_editText_imeId) || id == EditorInfo.IME_ACTION_GO) {
            sendPresence((UserPresenceType) presenceSpinner.getSelectedItem(), statusEditText.getText());
            statusEditText.setCursorVisible(false);
            DisplayUtils.hideSoftKeyboard(this, statusEditText);
            return true;
        }
        return false;
    }

    @OnClick(R.id.user_location_imageButton)
    void onLocationClick(View view) {
        if (!user.equals(UserManager.instance().getCurrentUser())) {
            if (LocationEventManager.instance().getGeolocationItemsForUser(user) == null) {
                Toast.makeText(this, "Sorry, but " + user.getUserJIDProperties().getName() +
                        "'s location is not available", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString(MapsActivity.USER_BUNDLE_TAG, createGsonWithExcludedFields().toJson(user));
        ActivityUtils.navigate(this, MapsActivity.class, bundle, false);
    }

    @OnClick(R.id.frameLayout_logOut_button)
    void onLogOut(View view) {
        logOut();
    }

    private void sendPresence(UserPresenceType item, Editable text) {
        try {
            RosterManager.instance().sendPresence(item, text.toString());
            previousPresenceType = item;
            previousStatus = text.toString();
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "Couldn't set your presence. Check your connection!", Toast.LENGTH_LONG).show();

            presenceSpinner.setOnItemSelectedListener(null);

            presenceSpinner.setSelection(previousPresenceType.ordinal(), true);
            statusEditText.setText(previousStatus);

            presenceSpinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sendPresence((UserPresenceType) adapter.getItem(position), statusEditText.getText());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
