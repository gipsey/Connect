package org.davidd.connect.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.davidd.connect.R;
import org.davidd.connect.component.activity.AddOccupantsActivity;
import org.davidd.connect.component.adapter.AddOccupantsAdapter;
import org.davidd.connect.manager.MyMultiUserChatManager;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.User;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddRoomOccupantsFragment extends Fragment {

    public static final String TAG = AddRoomOccupantsFragment.class.getName();

    @Bind(R.id.add_room_occupants_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.add_room_occupants_listview)
    protected ListView addRoomOccupantsListView;

    private MultiUserChat muc;

    private AddOccupantsAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        String roomName = getArguments().getString(AddOccupantsActivity.ROOM_TAG);
        muc = MyMultiUserChatManager.instance().getMucByFullName(roomName);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_room_occupants_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_room_occupants, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_them:
                        boolean success = MyMultiUserChatManager.instance().addUsersToCreatedRoom(muc.getRoom(), adapter.getCheckedUsers());

                        if (success) {
                            Toast.makeText(getActivity(), "Users added successfully", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getActivity(), "Adding users failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
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

        getActivity().setTitle("Add to " + muc.getRoom().getLocalpart().toString());

        adapter = new AddOccupantsAdapter(getActivity(), R.layout.occupant_row);

        addRoomOccupantsListView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateAdapter();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void updateAdapter() {
        adapter.clear();
        adapter.addAll(getOccupants());
        adapter.notifyDataSetChanged();
    }

    private List<User> getOccupants() {
        List<User> users = new ArrayList<>();
        users.addAll(RosterManager.instance().getUserContacts());

        Iterator<User> iterator = users.iterator();

        while (iterator.hasNext()) {
            User user = iterator.next();

            for (EntityFullJid occupant : muc.getOccupants()) {
                try {
                    String jid = occupant.asEntityFullJidIfPossible().getResourcepart().toString();

                    if (jid.equalsIgnoreCase(user.getUserJIDProperties().getName())) {
                        iterator.remove();
                        break;
                    }

                    EntityBareJid occupantBareJid = JidCreate.entityBareFrom(jid);

                    if (user.getUserJIDProperties().getNameAndDomain().equalsIgnoreCase(occupantBareJid.toString())) {
                        iterator.remove();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return users;
    }
}
