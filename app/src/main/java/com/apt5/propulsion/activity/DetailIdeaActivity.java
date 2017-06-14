package com.apt5.propulsion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.CommentListViewAdapter;
import com.apt5.propulsion.adapter.GlideImageGridViewAdapter;
import com.apt5.propulsion.object.Comment;
import com.apt5.propulsion.object.IdeaFb;
import com.apt5.propulsion.object.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.apt5.propulsion.ConstantVar.CHILD_COMMENTLIST;
import static com.apt5.propulsion.ConstantVar.CHILD_IDEA;
import static com.apt5.propulsion.ConstantVar.CHILD_LIKELIST;
import static com.apt5.propulsion.ConstantVar.CHILD_NOTIFICATION;

/**
 * Created by Van Quyen on 6/14/2017.
 */

public class DetailIdeaActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llComment;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvAuthor;
    private TextView tvTag;
    private TextView tvDescription;
    private ListView lvCommentList;
    private TextView tvLikeCount;
    private LinearLayout llLike;
    private Button btnSend;
    private EditText edtComment;
    private ImageView imgBack;
    private LinearLayout llLiked;
    private String ideaId;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private GlideImageGridViewAdapter imageAdapter;
    private CommentListViewAdapter commentAdapter;
    private ArrayList<String> photoUrlList;
    private RecyclerView rvImages;
    private List<Comment> commentList;
    private IdeaFb idea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_detail_idea);

        ideaId = getIntent().getExtras().getString("IDEA_ID");

        initView();
        getData();
    }

    private void getData() {
        firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("title")) {
                    idea.setTitle(dataSnapshot.child("title").getValue().toString());
                    tvTitle.setText(idea.getTitle());
                }
                if (dataSnapshot.hasChild("id")) {
                    idea.setId(dataSnapshot.child("id").getValue().toString());
                }
                if (dataSnapshot.hasChild("description")) {
                    idea.setDescription(dataSnapshot.child("description").getValue().toString());
                    tvDescription.setText(idea.getDescription());
                }
                if (dataSnapshot.hasChild("author")) {
                    idea.setAuthor(dataSnapshot.child("author").getValue().toString());
                    tvAuthor.setText(idea.getAuthor());
                }
                if (dataSnapshot.hasChild("tag")) {
                    idea.setTag(dataSnapshot.child("tag").getValue().toString());
                    tvTag.setText(idea.getTag());
                }
                if (dataSnapshot.hasChild("date")) {
                    idea.setDate(dataSnapshot.child("date").getValue(long.class));
                    tvDate.setText(CommonMethod.convertToDate(idea.getDate()));
                }
                if (dataSnapshot.hasChild("authorId")) {
                    idea.setAuthorId(dataSnapshot.child("authorId").getValue().toString());
                }
                if (dataSnapshot.hasChild("likelist")) {
                    List<String> listLike = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.child("likelist").getChildren()) {
                        listLike.add(snapshot.getKey());
                    }
                    idea.setLikeList(listLike);
                    tvLikeCount.setText(idea.getLikeList().size() + " like ");
                }
                if (dataSnapshot.hasChild("photoUrl")) {
                    ArrayList<String> listImage = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.child("photoUrl").getChildren()) {
                        listImage.add(snapshot.getValue().toString());
                        photoUrlList.add(snapshot.getValue().toString());
                    }
                    imageAdapter.notifyDataSetChanged();
                    idea.setPhotoUrl(listImage);
                }
                if (dataSnapshot.hasChild("commentList")) {
                    ArrayList<Comment> listComment = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.child("commentList").getChildren()) {
                        listComment.add(snapshot.getValue(Comment.class));
                        commentList.add(snapshot.getValue(Comment.class));
                    }
                    imageAdapter.notifyDataSetChanged();
                    scrollMyListViewToBottom();
                    idea.setCommentList(listComment);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateLike(tvLikeCount);
    }

    private void initView() {
        llComment = (LinearLayout) findViewById(R.id.ll_fullidea_comment);
        tvTag = (TextView) findViewById(R.id.tv_item_fullidea_tag);
        tvTitle = (TextView) findViewById(R.id.tv_item_fullidea_title);
        tvDate = (TextView) findViewById(R.id.tv_item_fullidea_date);
        tvDescription = (TextView) findViewById(R.id.tv_item_fullidea_description);
        tvAuthor = (TextView) findViewById(R.id.tv_item_fullidea_author);
        btnSend = (Button) findViewById(R.id.btn_comment_send);
        edtComment = (EditText) findViewById(R.id.writeComment);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        llLike = (LinearLayout) findViewById(R.id.ll_fullidea_like);
        llLiked = (LinearLayout) findViewById(R.id.ll_fullidea_liked);
        tvLikeCount = (TextView) findViewById(R.id.tv_item_fullidea_likecount);
        lvCommentList = (ListView) findViewById(R.id.commentsListView);
        rvImages = (RecyclerView) findViewById(R.id.rv_listImage);
        commentList = new ArrayList<>();
        photoUrlList = new ArrayList<>();
        idea = new IdeaFb();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        imageAdapter = new GlideImageGridViewAdapter(photoUrlList, this, getSupportFragmentManager());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        commentAdapter = new CommentListViewAdapter(commentList, this);

        rvImages.setLayoutManager(horizontalLayoutManager);
        rvImages.setAdapter(imageAdapter);
        lvCommentList.setAdapter(commentAdapter);

        btnSend.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        llLike.setOnClickListener(this);
        llLiked.setOnClickListener(this);
        llComment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            startActivity(new Intent(DetailIdeaActivity.this, MainActivity.class));
            finish();
        } else if (v == btnSend) {
            String comment = edtComment.getText().toString();
            if (!comment.equals("")) {
                DatabaseReference commentRef = firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId).child(CHILD_COMMENTLIST).push();
                String commentId = commentRef.getKey();
                firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId).child(CHILD_COMMENTLIST)
                        .child(commentId).setValue(new Comment(comment, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                        , System.currentTimeMillis(), commentId));
                edtComment.setText("");
                getCommentList();

                //set notification
                if (!firebaseAuth.getCurrentUser().getUid().equals(idea.getAuthorId())) {
                    String id = firebaseDatabase.getReference().child(CHILD_NOTIFICATION).child(idea.getAuthorId()).push().getKey();
                    firebaseDatabase.getReference().child(CHILD_NOTIFICATION).child(idea.getAuthorId())
                            .child(id).setValue(new Message(firebaseAuth.getCurrentUser().getDisplayName() + " comment on your idea : " + idea.getTitle(),
                            firebaseAuth.getCurrentUser().getUid(), idea.getId()));
                }
            }
        } else if (v == llComment) {
            scrollMyListViewToBottom();
            edtComment.setFocusable(true);
        } else if (v == llLike) {
            firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId)
                    .child(CHILD_LIKELIST).child(firebaseAuth.getCurrentUser().getUid()).setValue(System.currentTimeMillis(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toasty.error(DetailIdeaActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG, true).show();
                    } else {
                    }
                    llLike.setVisibility(View.GONE);
                    llLiked.setVisibility(View.VISIBLE);
                    updateLike(tvLikeCount);
                }
            });
            //set notification
            if (!firebaseAuth.getCurrentUser().getUid().equals(idea.getAuthorId())) {
                String id = firebaseDatabase.getReference().child(CHILD_NOTIFICATION).child(idea.getAuthorId()).push().getKey();
                firebaseDatabase.getReference().child(CHILD_NOTIFICATION).child(idea.getAuthorId())
                        .child(id).setValue(new Message(firebaseAuth.getCurrentUser().getDisplayName() + " like your idea : " + idea.getTitle(),
                        firebaseAuth.getCurrentUser().getUid(), idea.getId()));
            }
        } else if (v == llLiked) {
            firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId)
                    .child(CHILD_LIKELIST).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
            llLiked.setVisibility(View.GONE);
            llLike.setVisibility(View.VISIBLE);
            updateLike(tvLikeCount);
        }
    }

    private void updateLike(final TextView view) {
        firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> listLiked = new ArrayList<>();
                if (dataSnapshot.hasChild("likelist")) {
                    for (DataSnapshot snapshot : dataSnapshot.child("likelist").getChildren()) {
                        listLiked.add(snapshot.getKey());
                    }
                }
                if (listLiked.size() == 0 || listLiked.isEmpty()) {
                    view.setText("0 people like this");
                } else {
                    view.setText(listLiked.size() + " people like this");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void scrollMyListViewToBottom() {
        lvCommentList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvCommentList.setSelection(commentAdapter.getCount() - 1);
            }
        });
    }

    private void getCommentList() {
        commentList.clear();

        firebaseDatabase.getReference().child(CHILD_IDEA).child(ideaId).child(CHILD_COMMENTLIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
