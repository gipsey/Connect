package org.davidd.connect.component.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.davidd.connect.R;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.connection.event.OnRegistrationProcessFinishedEvent;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DisplayUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A registration screen.
 */
public class RegisterActivity extends ConnectionActivity {

    private View progressView;
    private View registerFormView;

    private EditText jidEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerFormView = findViewById(R.id.register_scrollView);
        progressView = findViewById(R.id.progressBar_layout);

        jidEditText = (EditText) findViewById(R.id.name_textView);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        passwordAgainEditText = (EditText) findViewById(R.id.passwordAgain_editText);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
    }

    private void attemptRegistration() {
        // Reset errors.
        jidEditText.setError(null);
        passwordEditText.setError(null);
        passwordAgainEditText.setError(null);

        // Store values at the time of the login attempt.
        String JID = jidEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (!DisplayUtils.isEmailValid(JID)) {
            jidEditText.setError(getString(R.string.error_invalid_jid));
            focusView = jidEditText;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!DisplayUtils.isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        if (!DisplayUtils.doPasswordsMatch(password, passwordAgain)) {
            passwordAgainEditText.setError(getString(R.string.error_passwords_do_not_match));
            focusView = passwordAgainEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            DisplayUtils.hideSoftKeyboard(this, jidEditText);
            DisplayUtils.hideSoftKeyboard(this, passwordEditText);
            DisplayUtils.hideSoftKeyboard(this, passwordAgainEditText);

            // try registration
            UserJIDProperties userJid = new UserJIDProperties(JID);
            User user = new User(userJid, password);
            MyConnectionManager.instance().register(user);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        registerFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegistrationProcessFinished(OnRegistrationProcessFinishedEvent event) {
        showProgress(false);

        if (event.throwable == null) {
            DisplayUtils.showOkAlertDialog(this, "Registration suceeded. Please log in.", new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.navigate(RegisterActivity.this, LoginActivity.class, true);
                }
            });
        } else {
            DisplayUtils.showOkAlertDialog(this, "Registration failed. Please try again.", null);
        }
    }
}