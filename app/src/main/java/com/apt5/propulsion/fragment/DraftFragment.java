package com.apt5.propulsion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.DraftListViewAdapter;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class DraftFragment extends Fragment {
    private ListView draftListView;
    private DraftListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_draft, container, false);

        draftListView = (ListView) rootView.findViewById(R.id.lv_fg_draft);

        return rootView;
    }
}
