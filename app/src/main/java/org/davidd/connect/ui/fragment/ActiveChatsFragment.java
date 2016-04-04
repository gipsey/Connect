package org.davidd.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.manager.MyChatManager;
import org.davidd.connect.manager.UserPresenceChangedMessage;
import org.davidd.connect.model.ActiveChat;
import org.davidd.connect.model.User;
import org.davidd.connect.ui.adapter.ActiveChatsAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActiveChatsFragment extends ControlActivityFragment implements
        MyChatManager.ChatUpdatedListener {

    @Bind(R.id.active_chats_listView)
    ListView listView;

    private ActiveChatsAdapter chatsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_chats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        TextView emptyView = (TextView) view.findViewById(R.id.active_chats_empty_view);
        emptyView.setText(R.string.no_active_chats);

        listView.setEmptyView(emptyView);

        chatsAdapter = new ActiveChatsAdapter(getActivity(), R.layout.contact_row, new ArrayList<ActiveChat>());

        listView.setAdapter(chatsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToChatListener.navigateToChat(chatsAdapter.getItem(position).getUserToChatWith());
            }
        });
    }

    public void onStart() {
        super.onStart();
        MyChatManager.instance().addChatUpdatedListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActiveChats(MyChatManager.instance().getActiveChats());
    }

    @Override
    public void onStop() {
        super.onStop();
        MyChatManager.instance().removeChatUpdatedListener(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userPresenceChanged(UserPresenceChangedMessage message) {
        for (ActiveChat activeChat : chatsAdapter.getActiveChats()) {
            User partner = activeChat.getUserToChatWith();
            if (partner.equals(message.getUser())) {
                partner.setUserPresence(message.getUser().getUserPresence());
                activeChat.setUserToChatWith(partner);
                chatsAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void onPagesSelected() {
    }

    @Override
    public void chatsUpdated(List<ActiveChat> activeChats) {
        updateActiveChats(activeChats);
    }

    private void updateActiveChats(List<ActiveChat> activeChats) {
        chatsAdapter.clear();
        chatsAdapter.addAll(activeChats);
        chatsAdapter.notifyDataSetChanged();
    }
}
