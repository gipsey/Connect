package org.davidd.connect.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.component.adapter.ActiveChatsAdapter;
import org.davidd.connect.manager.MyChatManager;
import org.davidd.connect.manager.UserPresenceChangedMessage;
import org.davidd.connect.model.ActiveBaseChat;
import org.davidd.connect.model.ActiveRoomChat;
import org.davidd.connect.model.ActiveUserChat;
import org.davidd.connect.model.User;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlActiveChatsFragment extends ControlTabFragment implements
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

        chatsAdapter = new ActiveChatsAdapter(getActivity(), R.layout.contact_row);

        listView.setAdapter(chatsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActiveBaseChat baseChat = chatsAdapter.getItem(position);

                if (baseChat instanceof ActiveUserChat) {
                    navigateToChatListener.navigateToChat(((ActiveUserChat) baseChat).getUserToChatWith());
                } else if (baseChat instanceof ActiveRoomChat) {
                    navigateToChatListener.navigateToChat(((ActiveRoomChat) baseChat).getMultiUserChat());
                }
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
        updateActiveChats(MyChatManager.instance().getActiveBaseChats());
    }

    @Override
    public void onStop() {
        super.onStop();
        MyChatManager.instance().removeChatUpdatedListener(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userPresenceChanged(UserPresenceChangedMessage message) {
        for (ActiveUserChat activeUserChat : chatsAdapter.getActiveUserChats()) {
            User partner = activeUserChat.getUserToChatWith();
            if (partner.equals(message.getUser())) {
                partner.setUserPresence(message.getUser().getUserPresence());
                activeUserChat.setUserToChatWith(partner);
                chatsAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void onPagesSelected() {
    }

    @Override
    public void chatsUpdated(List<ActiveBaseChat> activeBaseChats) {
        updateActiveChats(activeBaseChats);
    }

    private void updateActiveChats(List<ActiveBaseChat> activeBaseChats) {
        chatsAdapter.clear();
        chatsAdapter.addAll(activeBaseChats);
    }
}
