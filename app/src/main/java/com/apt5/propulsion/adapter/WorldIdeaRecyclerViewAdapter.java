package com.apt5.propulsion.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
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
 * Created by Van Quyen on 5/15/2017.
 */

public class WorldIdeaRecyclerViewAdapter
        extends RecyclerView.Adapter<WorldIdeaRecyclerViewAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private List<IdeaFb> listIdea;
    private Context context;
    private FirebaseDatabase database;
    private String uid;
    private PopupWindow popupWindow;
    private CommentListViewAdapter commentListViewAdapter;
    private List<Comment> commentArrayList;
    private FirebaseAuth firebaseAuth;

    public WorldIdeaRecyclerViewAdapter(OnItemClickListener listener, List<IdeaFb> listIdea, Context context,
                                        FirebaseDatabase database, String uid, FirebaseAuth firebaseAuth) {
        this.listener = listener;
        this.listIdea = listIdea;
        this.context = context;
        this.database = database;
        this.uid = uid;
        this.firebaseAuth = firebaseAuth;
    }


    @Override
    public WorldIdeaRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);

        ViewHolder holder = new ViewHolder(layoutView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final WorldIdeaRecyclerViewAdapter.ViewHolder holder, final int position) {
        if (listIdea.get(position).getLikeList() != null) {
            if (listIdea.get(position).getLikeList().contains(uid)) {
                holder.btnLike.setVisibility(View.GONE);
                holder.btnLiked.setVisibility(View.VISIBLE);
            } else {
                holder.btnLiked.setVisibility(View.GONE);
                holder.btnLike.setVisibility(View.VISIBLE);
            }
        }

        holder.bind(listIdea.get(position), listener);

        final String date = CommonMethod.convertToTime(listIdea.get(position).getDate());

        holder.tvTitle.setText(listIdea.get(position).getTitle() + "");
        holder.tvDescription.setText(listIdea.get(position).getDescription() + "");
        holder.tvAuthor.setText("Posted by " + listIdea.get(position).getAuthor());
        holder.tvDate.setText(date);

        updateLike(holder.tvLikeCount, listIdea.get(position).getId());

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child(CHILD_IDEA).child(listIdea.get(position).getId())
                        .child(CHILD_LIKELIST).child(uid).setValue(System.currentTimeMillis(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toasty.error(context, databaseError.getMessage(), Toast.LENGTH_LONG, true).show();
                        } else {
                        }
                        holder.btnLike.setVisibility(View.GONE);
                        holder.btnLiked.setVisibility(View.VISIBLE);
                        updateLike(holder.tvLikeCount, listIdea.get(position).getId());
                    }
                });
                //set notification
                if (!firebaseAuth.getCurrentUser().getUid().equals(listIdea.get(position).getAuthorId())) {
                    String id = database.getReference().child(CHILD_NOTIFICATION).child(listIdea.get(position).getAuthorId()).push().getKey();
                    database.getReference().child(CHILD_NOTIFICATION).child(listIdea.get(position).getAuthorId())
                            .child(id).setValue(new Message(firebaseAuth.getCurrentUser().getDisplayName() + " like your idea : " + listIdea.get(position).getTitle(),
                            firebaseAuth.getCurrentUser().getUid(), listIdea.get(position).getId()));
                }
            }
        });

        holder.btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child(CHILD_IDEA).child(listIdea.get(position).getId())
                        .child(CHILD_LIKELIST).child(uid).removeValue();
                holder.btnLiked.setVisibility(View.GONE);
                holder.btnLike.setVisibility(View.VISIBLE);
                updateLike(holder.tvLikeCount, listIdea.get(position).getId());
            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(holder.btnComment, listIdea.get(position));
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return listIdea.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvAuthor;
        public LinearLayout btnLike;
        public LinearLayout btnComment;
        public TextView tvLikeCount;
        public TextView tvDescription;
        public LinearLayout btnLiked;

        public ViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tv_item_timeline_title);
            tvAuthor = (TextView) view.findViewById(R.id.tv_item_timeline_author);
            tvDate = (TextView) view.findViewById(R.id.tv_item_timeline_date);
            tvLikeCount = (TextView) view.findViewById(R.id.tv_item_timeline_likecount);
            btnComment = (LinearLayout) view.findViewById(R.id.ll_timeline_comment);
            btnLike = (LinearLayout) view.findViewById(R.id.ll_timeline_like);
            btnLiked = (LinearLayout) view.findViewById(R.id.ll_timeline_liked);
            tvDescription = (TextView) view.findViewById(R.id.tv_item_timeline_description);

            btnComment.setOnClickListener(this);
            btnLike.setOnClickListener(this);
        }

        public void bind(final IdeaFb content, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(content, itemView, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemClickListener {
        void onItemClick(IdeaFb content, View view, int position);
    }

    private void showPopup(View v, final IdeaFb idea) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView = layoutInflater.inflate(R.layout.layout_comment_idea, null, false);
        commentArrayList = new ArrayList<>();
        commentListViewAdapter = new CommentListViewAdapter(commentArrayList, context);
        // find the ListView in the popup layout
        ListView listView = (ListView) rootView.findViewById(R.id.commentsListView);
        //find send button
        Button btnComment = (Button) rootView.findViewById(R.id.btn_comment_send);
        //find comment field
        final EditText edtComment = (EditText) rootView.findViewById(R.id.writeComment);
        // get device size
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        getCommentList(idea);

        listView.setAdapter(commentListViewAdapter);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edtComment.getText().toString();
                if (!comment.equals("")) {
                    DatabaseReference commentRef = database.getReference().child(CHILD_IDEA).child(idea.getId()).child(CHILD_COMMENTLIST).push();
                    String commentId = commentRef.getKey();
                    database.getReference().child(CHILD_IDEA).child(idea.getId()).child(CHILD_COMMENTLIST)
                            .child(commentId).setValue(new Comment(comment, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                            , System.currentTimeMillis(), commentId));
                    edtComment.setText("");
                    getCommentList(idea);

                    //set notification
                    if (!firebaseAuth.getCurrentUser().getUid().equals(idea.getAuthorId())) {
                        String id = database.getReference().child(CHILD_NOTIFICATION).child(idea.getAuthorId()).push().getKey();
                        database.getReference().child(CHILD_NOTIFICATION).child(idea.getAuthorId())
                                .child(id).setValue(new Message(firebaseAuth.getCurrentUser().getDisplayName() + " comment on your idea : " + idea.getTitle(),
                                firebaseAuth.getCurrentUser().getUid(), idea.getId()));
                    }
                }
            }
        });

        // set height depends on the device size
        popupWindow = new PopupWindow(rootView, size.x - 50, size.y - 400, true);
        // set a background drawable with rounders corners
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.comment_popup_bg));
        // make it focusable to show the keyboard to enter in `EditText`
        popupWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popupWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);
    }

    private void getCommentList(IdeaFb idea) {
        commentArrayList.clear();

        database.getReference().child(CHILD_IDEA).child(idea.getId()).child(CHILD_COMMENTLIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentArrayList.add(comment);
                }
                commentListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateLike(final TextView view, String id) {
        database.getReference().child(CHILD_IDEA).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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
}
