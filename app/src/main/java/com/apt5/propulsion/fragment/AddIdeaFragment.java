package com.apt5.propulsion.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apt5.propulsion.CommonMethod;
import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.GridViewPhotoAdapter;
import com.apt5.propulsion.object.Idea;
import com.apt5.propulsion.object.IdeaFb;
import com.apt5.propulsion.object.Picture;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

import static android.app.Activity.RESULT_OK;
import static com.apt5.propulsion.ConstantVar.CHILD_IDEA;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class AddIdeaFragment extends Fragment implements View.OnClickListener {
    private EditText edtTitle;
    private EditText edtTag;
    private EditText edtDescription;
    private Button btnAttach;
    private RecyclerView gvPhoto;
    private Button btnSubmit;
    private Button btnSave;
    private Button btnDelete;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> listBitmaps;
    private GridViewPhotoAdapter adapter;
    private ArrayList<String> photoUrlList;
    private FirebaseAuth firebaseAuth;
    private Realm realm;
    private LinearLayoutManager layoutManager;
    //idea đang duoc thao tac;
    private Idea ideaedit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addidea, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Realm.init(getActivity());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(realmConfiguration);
            realm = Realm.getInstance(realmConfiguration);
        }

        initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        edtTitle = (EditText) rootView.findViewById(R.id.edt_addidea_title);
        edtTag = (EditText) rootView.findViewById(R.id.edt_addidea_tag);
        edtDescription = (EditText) rootView.findViewById(R.id.edt_addidea_desciption);
        btnAttach = (Button) rootView.findViewById(R.id.btn_addidea_add_file);
        gvPhoto = (RecyclerView) rootView.findViewById(R.id.gv_addidea);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_addidea_submit_idea);
        btnSave = (Button) rootView.findViewById(R.id.btn_addidea_save_as_draft);
        btnDelete = (Button) rootView.findViewById(R.id.btn_addidea_delete);
        listBitmaps = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        photoUrlList = new ArrayList<>();
        adapter = new GridViewPhotoAdapter(listBitmaps, getActivity(), new GridViewPhotoAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                listBitmaps.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        gvPhoto.setAdapter(adapter);
        gvPhoto.setLayoutManager(layoutManager);
        photoUrlList = new ArrayList<>();

        btnDelete.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnAttach.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnAttach) {
            Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
            //set limit on number of images that can be selected, default is 10
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
            startActivityForResult(intent, Constants.REQUEST_CODE);
        } else if (v == btnDelete) {
            //TODO : delete exist idea in realm
            if (ideaedit != null) {
                realm.beginTransaction();
                ideaedit.deleteFromRealm();
                realm.commitTransaction();
                clearContent();
            } else
                clearContent();

        } else if (v == btnSave) {

            //TODO : save idea to realm
            if (edtTitle.getText().toString().equals("")) {
                Toast toast = Toast.makeText(getContext(), "Title is empty", Toast.LENGTH_SHORT);
                toast.show();
            } else if (edtDescription.getText().toString().equals("")) {
                Toast toast = Toast.makeText(getContext(), "Discription is empty", Toast.LENGTH_SHORT);
                toast.show();
            } else if (edtTag.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Category is empty", Toast.LENGTH_SHORT).show();
            } else if (ideaedit != null) {
                saveEditIdeaToRelm();
            } else {
                saveNewIdeaToRealm();
                clearContent();
            }
        } else if (v == btnSubmit) {
            sendDataToServer();
        }
    }

    private void saveEditIdeaToRelm() {
        realm.beginTransaction();
        ideaedit.setTitle(edtTitle.getText().toString());
        ideaedit.setCategory(edtTag.getText().toString());
        ideaedit.setDescription(edtDescription.getText().toString());
        realm.commitTransaction();
    }

    private void saveNewIdeaToRealm() {
        Idea idea = new Idea();
        Date date = Calendar.getInstance().getTime();
        String datetime = date.toString();
        long now = System.currentTimeMillis();
        String currentTime = CommonMethod.convertToDate(now);

        idea.setTitletime(datetime + edtTitle.getText().toString());
        idea.setTime(currentTime);
        idea.setTitle(edtTitle.getText().toString());
        idea.setDate(date);
        idea.setCategory(edtTag.getText().toString());
        idea.setDescription(edtDescription.getText().toString());

        realm.beginTransaction();
        realm.copyToRealm(idea);
        realm.commitTransaction();
    }

    private void sendDataToServer() {
        DatabaseReference newIdeaPost = firebaseDatabase.getReference().child(CHILD_IDEA).push();
        IdeaFb newIdea = new IdeaFb();
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String tag = edtTag.getText().toString();
        String author = firebaseAuth.getCurrentUser().getDisplayName();
        long time = System.currentTimeMillis();
        String id = newIdeaPost.getKey();

        if (!title.equals("") && !description.equals("") && !tag.equals("")) {
            newIdea.setTitle(title);
            newIdea.setDescription(description);
            newIdea.setTag(tag);
            newIdea.setAuthor(author);
            newIdea.setDate(time);
            newIdea.setPhotoUrl(photoUrlList);
            newIdea.setId(id);
            newIdea.setAuthorId(firebaseAuth.getCurrentUser().getUid());

            firebaseDatabase.getReference().child(CHILD_IDEA).child(id).
                    setValue(newIdea, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                clearContent();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please fill in the blank space", Toast.LENGTH_LONG).show();
        }

    }

    private void saveImageToStorage(Bitmap bitmap) {
        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        String fileName = "" + bitmap.hashCode();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("images" + "/" + fileName);
        //compress image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = reference.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")

                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                photoUrlList.add(downloadUrl.toString());
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && imageReturnedIntent != null) {
            ArrayList<Image> imageList = imageReturnedIntent.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//                        listBitmaps.add(bitmap);
//                        adapter.updateItem(bitmap);
//                        if (ideaedit!=null)
//                        {
//                            savePictureOfEditIdeatoRealm(bitmap);
//                        } else {
//                            savePictureOfNewIdeatoRealm(bitmap);
//                        }
//                        adapter.notifyDataSetChanged();
            for (Image child : imageList) {
                listBitmaps.add(child.path);

                //push image to server
                File newFile = new File(child.path);
                Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                saveImageToStorage(myBitmap);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void savePictureOfNewIdeatoRealm(Bitmap bitmap) {
        byte[] btm = CommonMethod.BitmaptoByteArray(bitmap);
        Picture images = new Picture();
//        images.setTitletime("");
        Date date = Calendar.getInstance().getTime();
        String time = date.toString();
//        images.setCreateTitletime(time + ideaedit.getTitletime());
        images.setKey("0");
        images.setPicture(btm);
    }

    private void savePictureOfEditIdeatoRealm(Bitmap bitmap) {
        byte[] btm = CommonMethod.BitmaptoByteArray(bitmap);
        Picture images = new Picture();
        images.setTitletime(ideaedit.getTitletime());
        Date date = Calendar.getInstance().getTime();
        String time = date.toString();
        images.setCreateTitletime(time + ideaedit.getTitletime());
        images.setKey("1");
        images.setPicture(btm);
    }

    private void clearContent() {
        edtDescription.setText("");
        edtTag.setText("");
        edtTitle.setText("");
        ideaedit = null;
        listBitmaps.clear();
        adapter.notifyDataSetChanged();
    }
}
