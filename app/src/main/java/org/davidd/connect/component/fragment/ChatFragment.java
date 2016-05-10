package org.davidd.connect.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.component.activity.ChatActivity;
import org.davidd.connect.component.activity.MapsActivity;
import org.davidd.connect.component.activity.UserActivity;
import org.davidd.connect.component.adapter.ChatAdapter;
import org.davidd.connect.component.adapter.ContactsHelper;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.MyChatManager;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.manager.UserPresenceChangedMessage;
import org.davidd.connect.model.MyMessage;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserPresenceType;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ChatFragment extends Fragment implements
        MyChatManager.MessageReceivedListener {

    public static final String TAG = ChatFragment.class.getName();

    @Bind(R.id.chat_toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.toolbar_userName_textView)
    protected TextView userNameTextView;
    @Bind(R.id.toolbar_status_textView)
    protected TextView statusTextView;
    @Bind(R.id.toolbar_availability_imageView)
    protected ImageView availabilityImageView;

    @Bind(R.id.chat_list_view)
    protected ListView chatListView;
    @Bind(R.id.chat_empty_view)
    protected TextView chatListViewEmptyTextView;
    @Bind(R.id.chat_action_and_send_layout)
    protected View footerView;
    @Bind(R.id.footer_chat_message_edit_text)
    protected EditText messageEditText;
    @Bind(R.id.footer_chat_send_button)
    protected ImageButton sendButton;

    private ChatAdapter chatAdapter;
    private User userToChatWith;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        userToChatWith = createGsonWithExcludedFields().fromJson(
                getArguments().getString(ChatActivity.USER_TO_CHAT_WITH), User.class);
        userToChatWith.setRosterEntry(
                RosterManager.instance().getRosterEntryForUser(userToChatWith.getUserJIDProperties()));
        userToChatWith.setUserPresence(
                RosterManager.instance().getUserPresenceForUser(userToChatWith.getUserJIDProperties()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle bundle = new Bundle();
                switch (item.getItemId()) {
                    case R.id.user_profile:
                        bundle.putString(UserActivity.USER_BUNDLE_TAG, createGsonWithExcludedFields().toJson(userToChatWith));
                        ActivityUtils.navigate(getActivity(), UserActivity.class, bundle, false);
                        return true;
                    case R.id.user_location:
                        bundle.putString(MapsActivity.USER_BUNDLE_TAG, createGsonWithExcludedFields().toJson(userToChatWith));
                        ActivityUtils.navigate(getActivity(), MapsActivity.class, bundle, false);
                        return true;
                    default:
                        return false;
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        userNameTextView.setText(userToChatWith.getUserJIDProperties().getNameAndDomain());

        chatAdapter = new ChatAdapter(getActivity(), R.layout.chat_row, new ArrayList<MyMessage>());
        chatListView.setAdapter(chatAdapter);

        chatListViewEmptyTextView.setText("No conversation to show");
        chatListView.setEmptyView(chatListViewEmptyTextView);
        chatListView.getEmptyView().setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        MyChatManager.instance().addMessageReceivedListener(userToChatWith, this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserPresenceOnUi();
    }

    @Override
    public void onStop() {
        super.onStop();
        MyChatManager.instance().removeMessageReceivedListener(userToChatWith, this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userPresenceChanged(UserPresenceChangedMessage message) {
        if (message.getUser().equals(userToChatWith)) {
            userToChatWith.setUserPresence(message.getUser().getUserPresence());
            updateUserPresenceOnUi();
        }
    }

    @OnEditorAction(R.id.footer_chat_message_edit_text)
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.footer_chat_message_edit_text_action_id || id == EditorInfo.IME_NULL) {
            sendMessage();
            return true;
        }
        return false;
    }

    @OnClick(R.id.footer_chat_send_button)
    protected void sendButtonPressed(View view) {
        sendMessage();
    }

    private void updateUserPresenceOnUi() {
        UserPresenceType userPresenceType;

        if (userToChatWith.getUserPresence() != null) {
            userPresenceType = userToChatWith.getUserPresence().getUserPresenceType();
        } else {
            userPresenceType = UserPresenceType.OFFLINE;
        }

        availabilityImageView.setImageResource(ContactsHelper.getImageResourceFromUserPresence(userPresenceType));

        if (userToChatWith.getUserPresence() != null && !DataUtils.isEmpty(userToChatWith.getUserPresence().getPresence().getStatus())) {
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(userToChatWith.getUserPresence().getPresence().getStatus());
        } else {
            statusTextView.setVisibility(View.GONE);
            statusTextView.setText(null);
        }
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        messageEditText.setText(null);

        if (DataUtils.isEmpty(message)) {
            return;
        }

        // send it
        MyMessage myMessage = MyChatManager.instance().sendMessage(userToChatWith, message);

        if (myMessage != null) {
            // show locally
            chatAdapter.add(myMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void messageReceived(MyMessage myMessage) {
        L.d(new Object() {});
        if (!isMessageValid(myMessage.getMessage())) {
            L.d(new Object() {}, "Message is empty");
            return;
        }

        chatAdapter.add(myMessage);
        chatAdapter.notifyDataSetChanged();
    }

    private boolean isMessageValid(Message message) {
        return (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal)
                && !DataUtils.isEmpty(message.getBody());
    }
}
