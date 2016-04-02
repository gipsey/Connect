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
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.ActiveChat;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.ui.adapter.ActiveChatsAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActiveChatsFragment extends ControlActivityFragment {

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
                navigateToChatListener.navigateToChat(chatsAdapter.getItem(position).getSender());
            }
        });
    }

    public void onStart() {
        super.onStart();

        // TODO mock data
        ActiveChat chat1 = new ActiveChat(
                new User(new UserJIDProperties("qwe@is-a-furry.org"),
                        RosterManager.instance().getRosterEntryForUser(new UserJIDProperties("qwe@is-a-furry.org")),
                        RosterManager.instance().getUserPresenceForUser(new UserJIDProperties("qwe@is-a-furry.org"))),
                "malacka");

        ActiveChat chat2 = new ActiveChat(
                new User(new UserJIDProperties("malacka@jancsika.com"),
                        RosterManager.instance().getRosterEntryForUser(new UserJIDProperties("malacka@jancsika.com")),
                        RosterManager.instance().getUserPresenceForUser(new UserJIDProperties("malacka@jancsika.com"))),
                null);
        chatsAdapter.add(chat1);
        chatsAdapter.add(chat2);
        chatsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPagesSelected() {
    }
}
