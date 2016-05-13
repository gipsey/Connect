package org.davidd.connect.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.davidd.connect.R;
import org.davidd.connect.component.adapter.RoomsArrayAdapter;
import org.davidd.connect.component.event.RoomsUpdatedEvent;
import org.davidd.connect.manager.MyMultiUserChatManager;
import org.davidd.connect.model.Room;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.muc.HostedRoom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlRoomsFragment extends ControlTabFragment {

    @Bind(R.id.rooms_listView)
    ListView roomsListView;

    @Bind(R.id.addRoom_floatingActionButton)
    FloatingActionButton addRoomFloatingActionButton;

    private RoomsArrayAdapter roomsArrayAdapter;

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

        roomsArrayAdapter = new RoomsArrayAdapter(getActivity());

        roomsListView.setAdapter(roomsArrayAdapter);
        roomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        addRoomFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Hi", Toast.LENGTH_SHORT).show();
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

        List<HostedRoom> hostedRooms = MyMultiUserChatManager.instance().getAllHostedRoomsOnServer();
        List<Room> rooms = new ArrayList<>();

        for (HostedRoom hostedRoom : hostedRooms) {
            rooms.add(new Room(hostedRoom.getJid().toString() + " " + hostedRoom.getName()));
        }

        roomsUpdated(rooms);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPagesSelected() {
    }

    // TODO maybe no need for events
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void roomsUpdated(RoomsUpdatedEvent roomsUpdatedEvent) {
        roomsUpdated(roomsUpdatedEvent.getRooms());
    }

    private void roomsUpdated(List<Room> roomList) {
        roomsArrayAdapter.clear();
        roomsArrayAdapter.addAll(roomList);
        roomsArrayAdapter.notifyDataSetChanged();
    }
}
