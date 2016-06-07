package org.davidd.connect.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import org.davidd.connect.component.adapter.ChatAdapter;
import org.davidd.connect.component.event.MucMessageEvent;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.MyChatManager;
import org.davidd.connect.manager.MyMultiUserChatManager;
import org.davidd.connect.model.MyMessage;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.EntityFullJid;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MucFragment extends Fragment {

    public static final String TAG = MucFragment.class.getName();

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
    private MultiUserChat muc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        String roomName = getArguments().getString(ChatActivity.ROOM_NAME_TAG);

        muc = MyMultiUserChatManager.instance().getMucByFullName(roomName);
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
                switch (item.getItemId()) {
                    case R.id.user_profile:

                        String users = "";

                        for (EntityFullJid jid : muc.getOccupants()) {
                            users += jid.getResourceOrNull().toString() + "\n";
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setPositiveButton("CLOSE", null)
                                .setTitle("Active occupants of " + muc.getRoom().getLocalpart().toString() + " room")
                                .setMessage(users);

                        builder.create().show();

                        return true;
                    case R.id.user_location:
                        Bundle bundle = new Bundle();
                        bundle.putString(MapsActivity.ROOM_NAME_BUNDLE_TAG, muc.getRoom().toString());
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

        userNameTextView.setText(muc.getRoom().toString());

        chatAdapter = new ChatAdapter(getActivity(), R.layout.chat_row, new ArrayList<MyMessage>());
        chatListView.setAdapter(chatAdapter);

        chatListViewEmptyTextView.setText("No conversation to show");
        chatListView.setEmptyView(chatListViewEmptyTextView);
        chatListView.getEmptyView().setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(MucMessageEvent mucMessageEvent) {
        L.d(new Object() {});
        if (!isMessageValid(mucMessageEvent.getMyMessage().getMessage())) {
            L.d(new Object() {}, "Message is empty");
            return;
        }

        chatAdapter.add(mucMessageEvent.getMyMessage());
        chatAdapter.notifyDataSetChanged();
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        messageEditText.setText(null);

        if (DataUtils.isEmpty(message)) {
            return;
        }

        // send it
        MyMessage myMessage = MyChatManager.instance().sendMessage(muc, message);

        // no need to add to the adapter because will be received as a message
    }

    private boolean isMessageValid(Message message) {
        return (message.getType() == Message.Type.groupchat)
                && !DataUtils.isEmpty(message.getBody());
    }
}
