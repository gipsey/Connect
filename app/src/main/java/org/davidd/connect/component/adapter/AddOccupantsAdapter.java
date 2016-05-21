package org.davidd.connect.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.model.User;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddOccupantsAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;
    private Context context;
    private int resourceId;

    private List<User> checkedUsers = new LinkedList<>();

    public AddOccupantsAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resourceId = resource;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setupContact(viewHolder, getItem(position));

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedUsers.add(getItem(position));
                } else {
                    checkedUsers.remove(getItem(position));
                }
            }
        });

        return convertView;
    }

    public List<User> getCheckedUsers() {
        return checkedUsers;
    }

    private void setupContact(ViewHolder viewHolder, User user) {
        viewHolder.jidTextView.setText(user.getUserJIDProperties().getNameAndDomain());
        viewHolder.checkBox.setChecked(false);
    }

    class ViewHolder {

        @Bind(R.id.jid)
        TextView jidTextView;

        @Bind(R.id.checkBox)
        CheckBox checkBox;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
