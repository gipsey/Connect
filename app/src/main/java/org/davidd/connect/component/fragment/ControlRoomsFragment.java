package org.davidd.connect.component.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.davidd.connect.R;
import org.davidd.connect.component.activity.NavigateToAddOccupantsListener;
import org.davidd.connect.component.adapter.RoomsArrayAdapter;
import org.davidd.connect.component.event.RoomsUpdatedEvent;
import org.davidd.connect.component.exception.RoomNameExistsException;
import org.davidd.connect.manager.MyMultiUserChatManager;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.Room;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlRoomsFragment extends ControlTabFragment {

    @Bind(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.rooms_listView)
    ListView roomsListView;

    @Bind(R.id.addRoom_floatingActionButton)
    FloatingActionButton addRoomFloatingActionButton;

    private RoomsArrayAdapter roomsArrayAdapter;

    private NavigateToAddOccupantsListener navigateToAddOccupantsListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigateToAddOccupantsListener = (NavigateToAddOccupantsListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        TextView emptyView = (TextView) view.findViewById(R.id.rooms_empty_view);
        emptyView.setText(R.string.no_rooms);
        roomsListView.setEmptyView(emptyView);

        roomsArrayAdapter = new RoomsArrayAdapter(getActivity(), new RoomsArrayAdapter.AddOccupantsListener() {
            @Override
            public void addOccupants(Room room) {
                navigateToAddOccupantsListener.navigateToAddOccupants(room);
            }
        });

        roomsListView.setAdapter(roomsArrayAdapter);
        roomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToChatListener.navigateToChat(roomsArrayAdapter.getItem(position).getMuc());
            }
        });

        addRoomFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewRoomAlert();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRefreshLayout(true);
                roomsArrayAdapter.clear();

                MyMultiUserChatManager.instance().refreshUserRoomWithOwnerAffiliationAsync();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        startLoadingData();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPagesSelected() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void roomsUpdated(RoomsUpdatedEvent roomsUpdatedEvent) {
        roomsUpdated(roomsUpdatedEvent.getRooms());
        endLoadingData();
    }

    private void startLoadingData() {
        ((TextView) roomsListView.getEmptyView()).setText(getResources().getString(R.string.rooms_loading));

        roomsArrayAdapter.clear();
        roomsArrayAdapter.notifyDataSetChanged();

        MyMultiUserChatManager.instance().getUserRoomWithOwnerAffiliationAsync();
    }

    private void endLoadingData() {
        ((TextView) roomsListView.getEmptyView()).setText(getString(R.string.no_rooms));
    }

    private void roomsUpdated(List<MultiUserChat> chats) {
        roomsArrayAdapter.clear();

        List<Room> roomList = new ArrayList<>();
        for (MultiUserChat multiUserChat : chats) {
            roomList.add(new Room(multiUserChat));
        }

        roomsArrayAdapter.addAll(roomList);
        roomsArrayAdapter.notifyDataSetChanged();

        showRefreshLayout(false);
    }

    private void showNewRoomAlert() {
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setHint("Enter room name");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(layoutParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomCreationAttempt(editText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setView(editText);

        builder.create().show();
    }

    private void roomCreationAttempt(String roomName) {
        try {
            boolean success = false;

            if (!TextUtils.isEmpty(roomName)) {
                success = MyMultiUserChatManager.instance().createRoom(roomName);
            }

            if (success) {
                Toast.makeText(getActivity(), "Room '" + roomName + "' successfully created", Toast.LENGTH_LONG).show();
                startLoadingData();
            } else {
                Toast.makeText(getActivity(), "Room '" + roomName + "' creation failed", Toast.LENGTH_LONG).show();
            }
        } catch (RoomNameExistsException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Room '" + roomName + "' already exists, try another", Toast.LENGTH_LONG).show();
        }
    }

    private void showRefreshLayout(final boolean isRefreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }
}
