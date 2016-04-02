package org.davidd.connect.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import org.davidd.connect.ui.activity.NavigateToChatListener;

public abstract class ControlActivityFragment extends Fragment {

    protected NavigateToChatListener navigateToChatListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        navigateToChatListener = (NavigateToChatListener) context;
    }

    public abstract void onPagesSelected();
}
