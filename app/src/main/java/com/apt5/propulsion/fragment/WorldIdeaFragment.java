package com.apt5.propulsion.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.GridViewPhotoAdapter;
import com.apt5.propulsion.adapter.WorldIdeaRecyclerViewAdapter;
import com.apt5.propulsion.object.IdeaFb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.apt5.propulsion.ConstantVar.CHILD_IDEA;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class WorldIdeaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private WorldIdeaRecyclerViewAdapter adapter;
    private RecyclerView recyclerViewIdea;
    private List<IdeaFb> ideaList;
    private FirebaseDatabase database;
    private LinearLayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private PopupWindow popupWindow;
    private GridViewPhotoAdapter gridViewPhotoAdapter;
    private List<Bitmap> bitmapList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_worldidea, container, false);

        initView(rootView);

        swipeRefreshLayout.setOnRefreshListener(this);

        getData();

        return rootView;
    }

    private void initView(View rootView) {
        recyclerViewIdea = (RecyclerView) rootView.findViewById(R.id.rv_world);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ideaList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_worldidea);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerViewIdea.setHasFixedSize(true);
        recyclerViewIdea.setLayoutManager(layoutManager);

        adapter = new WorldIdeaRecyclerViewAdapter(new WorldIdeaRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(IdeaFb content, View view, int position) {
                showDetail(view, content);
            }
        }, ideaList, getActivity(), database, firebaseAuth.getCurrentUser().getUid());

        recyclerViewIdea.setAdapter(adapter);
    }

    private void getData() {
        ideaList.clear();
        database.getReference().child(CHILD_IDEA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    IdeaFb idea = new IdeaFb();
                    if (child.hasChild("title")) {
                        idea.setTitle(child.child("title").getValue().toString());
                    }
                    if (child.hasChild("id")) {
                        idea.setId(child.child("id").getValue().toString());
                    }
                    if (child.hasChild("description")) {
                        idea.setDescription(child.child("description").getValue().toString());
                    }
                    if (child.hasChild("author")) {
                        idea.setAuthor(child.child("author").getValue().toString());
                    }
                    if (child.hasChild("tag")) {
                        idea.setTag(child.child("tag").getValue().toString());
                    }
                    if (child.hasChild("date")) {
                        idea.setDate(child.child("date").getValue(long.class));
                    }
                    if (child.hasChild("id")) {
                        idea.setId(child.child("id").getValue().toString());
                    }
                    if (child.hasChild("authorId")) {
                        idea.setAuthorId(child.child("authorId").getValue().toString());
                    }
                    if (child.hasChild("likelist")) {
                        List<String> listLike = new ArrayList<String>();
                        for (DataSnapshot snapshot : child.child("likelist").getChildren()) {
                            listLike.add(snapshot.getKey());
                        }
                        idea.setLikeList(listLike);
                    }
                    if (child.hasChild("encodedImageList")) {
                        List<String> listImage = new ArrayList<String>();
                        for (DataSnapshot snapshot : child.child("encodedImageList").getChildren()) {
                            listImage.add(snapshot.getKey());
                        }
                        idea.setPhotoUrl(listImage);
                    }
                    ideaList.add(idea);
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getData();
    }


    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private void showDetail(View v, final IdeaFb idea) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = layoutInflater.inflate(R.layout.layout_idea_detail, null, false);
        bitmapList = new ArrayList<>();

        if (idea.getPhotoUrl() != null) {
            for (String encodedImage : idea.getPhotoUrl()) {
                try {
                    bitmapList.add(decodeFromFirebaseBase64(encodedImage));
                } catch (IOException e) {
                }
            }
        }

        //gridViewPhotoAdapter = new GridViewPhotoAdapter(bitmapList, getActivity());

        //init view
        GridView gridViewPhoto = (GridView) rootView.findViewById(R.id.gv_idea_detail);
        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_idea_detail_title);
        TextView tvDescription = (TextView) rootView.findViewById(R.id.tv_idea_detail_description);
        TextView tvLikeCount = (TextView) rootView.findViewById(R.id.tv_idea_detail_likecount);
        TextView tvDate = (TextView) rootView.findViewById(R.id.tv_idea_detail_date);
        TextView tvTag = (TextView) rootView.findViewById(R.id.tv_idea_detail_tag);

        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        gridViewPhoto.setAdapter(gridViewPhotoAdapter);

        //set data
        tvTitle.setText("Title: " + idea.getTitle());
        tvTag.setText("Tag: " + idea.getTag());
        tvDescription.setText("Description: " + idea.getDescription());
        tvDate.setText(CommonMethod.convertToDate(idea.getDate()) + "");
        if (idea.getLikeList() == null) {
            tvLikeCount.setText("0 people like this");
        } else {
            tvLikeCount.setText(idea.getLikeList().size() + " people like this");
        }

        // set height depends on the device size
        popupWindow = new PopupWindow(rootView, size.x - 50, size.y - 400, true);
        // set a background drawable with rounders corners
        popupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.comment_popup_bg));
        // make it focusable to show the keyboard to enter in `EditText`
        popupWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popupWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);
    }

}
