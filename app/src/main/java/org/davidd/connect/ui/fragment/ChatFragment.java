package org.davidd.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.davidd.connect.manager.MessageManager;
import org.davidd.connect.model.Message;
import org.davidd.connect.ui.adapter.ConversationAdapter;
import org.davidd.connect.R;
import org.davidd.connect.util.ActivityUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author David Debre
 *         on 2015/12/13
 */
public class ChatFragment extends Fragment {
    public static final String TAG = ChatFragment.class.getName();

    @Bind(R.id.chat_list_view)
    protected ListView mChatListView;
    @Bind(R.id.chat_empty_view)
    protected TextView mChatListViewEmptyTextView;

    @Bind(R.id.chat_action_and_send_layout)
    protected View mFooterView;
    @Bind(R.id.footer_chat_message_edit_text)
    protected EditText mMessageEditText;
    @Bind(R.id.footer_chat_send_button)
    protected Button mSendButton;

    private ConversationAdapter mConversationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mConversationAdapter = new ConversationAdapter(getActivity(), R.layout.chat_row, new ArrayList<Message>());
        mChatListView.setAdapter(mConversationAdapter);

        mChatListViewEmptyTextView.setText("No conversation to show");
        mChatListView.setEmptyView(mChatListViewEmptyTextView);
        mChatListView.getEmptyView().setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MessageManager.instance().getConversation() == null ||
                MessageManager.instance().getConversation().getMessages() == null) {
            // TODO: call the WS
        } else {
            mConversationAdapter.addAll(MessageManager.instance().getConversation().getMessages());
        }

        mConversationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick(R.id.footer_chat_send_button)
    protected void sendButtonPressed(View view) {
        sendPressed();
    }

    private void sendPressed() {
//        ActivityUtils.showToast(getActivity(), "send pressed");


    }

}
