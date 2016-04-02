package org.davidd.connect.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.davidd.connect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Class for binding the a User to a view for contacts and active chats screen
 */
class ContactViewHolder {

    @Bind(R.id.name_initial_textView)
    TextView firstLetterTextView;
    @Bind(R.id.name_textView)
    TextView userNameTextView;
    @Bind(R.id.status_textView)
    TextView statusTextView;
    @Bind(R.id.availability_textView)
    TextView availabilityTextView;
    @Bind(R.id.availability_imageView)
    ImageView availabilityImageView;

    ContactViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
