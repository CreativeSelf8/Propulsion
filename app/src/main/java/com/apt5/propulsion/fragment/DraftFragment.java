package com.apt5.propulsion.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apt5.propulsion.Keys;
import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.DraftListViewAdapter;
import com.apt5.propulsion.object.Idea;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;


public class DraftFragment extends Fragment {
    private RealmConfiguration realmConfiguration;
    private Realm realm;
    int position = 0;
    private ListView lv_viewdraft;
    private ArrayList<Idea> ideas;
    private ArrayList<Idea> draftItems;
    private DraftListViewAdapter draftAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                position = getArguments().getInt("position", 0);
            }
        }

        ideas = new ArrayList<Idea>();
        draftItems = new ArrayList<Idea>();
        draftAdapter = new DraftListViewAdapter(draftItems, getActivity());
        Realm.init(getContext());
        realmConfiguration = new RealmConfiguration.Builder().build();
        //change schema when realm have new class
        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(realmConfiguration);
            realm = Realm.getInstance(realmConfiguration);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocalDatabase();
        loadDataforListDrafts();

        //BroadCast reload data from realm
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadLocalDatabase();
            }
        }, new IntentFilter(Keys.RELOAD_DATA_FROM_REALM));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_draft, container, false);
        draftAdapter.notifyDataSetChanged();
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv_viewdraft = (ListView) view.findViewById(R.id.lv_fg_draft);
    }

    public void loadDataforListDrafts() {
        draftAdapter = new DraftListViewAdapter(draftItems, getActivity());
        lv_viewdraft.setAdapter(draftAdapter);
        //set event click item of listview Draft
        lv_viewdraft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //delete draft
                view.findViewById(R.id.iv_draft_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realm.beginTransaction();
                        String strremove = draftItems.get(position).getTitletime();
                        int numberremove = -1;
                        for (int i = 0; i < ideas.size(); i++) {
                            if (ideas.get(i).getTitletime().equals(strremove)) {
                                numberremove = i;
                            }
                        }
                        ideas.get(numberremove).deleteFromRealm();
                        realm.commitTransaction();
                        loadLocalDatabase();
                        draftAdapter.notifyDataSetChanged();
                    }
                });

                view.findViewById(R.id.iv_draft_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });
    }
    public void loadLocalDatabase() {
        draftItems.clear();
        ideas.clear();
        RealmResults<Idea> realmResults = realm.where(Idea.class).findAll();
        if (realmResults != null) {
            for (int i = 0; i < realmResults.size(); i++) {
                Idea idea1 = realmResults.get(i);
                ideas.add(idea1);
                draftItems.add(new Idea(ideas.get(i).getTitle(),
                        ideas.get(i).getTime(), ideas.get(i).getTitletime(),
                        ideas.get(i).getDate()));
            }
            Collections.sort(draftItems);
        }
        draftAdapter.notifyDataSetChanged();
    }
}