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
import com.apt5.propulsion.WrapContentLinearLayoutManager;
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

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;
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
    private WrapContentLinearLayoutManager layoutManager;
    private Idea ideaedit;
    private ArrayList<Picture> pictures;
    public AddIdeaFragment(Idea ideaedit){
        this.ideaedit = ideaedit;
    }
    public AddIdeaFragment()
    {
        this.ideaedit = null;
    }

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
        if (ideaedit != null)
        {
            edtTitle.setText(ideaedit.getTitle());
            edtTag.setText(ideaedit.getCategory());
            edtDescription.setText(ideaedit.getDescription());
        }

        btnAttach = (Button) rootView.findViewById(R.id.btn_addidea_add_file);
        gvPhoto = (RecyclerView) rootView.findViewById(R.id.gv_addidea);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_addidea_submit_idea);
        btnSave = (Button) rootView.findViewById(R.id.btn_addidea_save_as_draft);
        btnDelete = (Button) rootView.findViewById(R.id.btn_addidea_delete);
        listBitmaps = new ArrayList<>();
        layoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        photoUrlList = new ArrayList<>();

        while (realm.where(Picture.class).equalTo("Key", "0").findFirst() != null) {
            Picture picture = realm.where(Picture.class).equalTo("Key", "0").findFirst();
            realm.beginTransaction();
            picture.deleteFromRealm();
            realm.commitTransaction();
        }

        pictures = new ArrayList<>();
        if (ideaedit == null)
        {
            RealmResults<Picture> realmResults = realm.where(Picture.class).equalTo("Key","0").findAll();
            for (int i = 0; i< realmResults.size(); i++)
            {
                pictures.add(realmResults.get(i));
            }

        } else
        {
            RealmResults<Picture> realmResults = realm.where(Picture.class).equalTo("titletime",ideaedit.getTitletime()).findAll();
            for (int i = 0; i< realmResults.size(); i++)
            {
                pictures.add(realmResults.get(i));
            }
        }
        adapter = new GridViewPhotoAdapter(pictures, getActivity());

        gvPhoto.setAdapter(adapter);
        gvPhoto.setLayoutManager(layoutManager);

        photoUrlList = new ArrayList<>();

        btnDelete.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnAttach.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if (v == btnAttach) {
            Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
            //set limit on number of images that can be selected, default is 10
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
            startActivityForResult(intent, Constants.REQUEST_CODE);
        } else if (v == btnDelete) {
            if (ideaedit != null) {
                realm.beginTransaction();
                ideaedit.deleteFromRealm();
                realm.commitTransaction();
                clearContent();
            } else
                clearContent();
        } else if (v == btnSave) {
            if (edtTitle.getText().toString().equals("")) {
                Toasty.warning(getActivity(), "Title is empty", LENGTH_SHORT, true).show();
            } else if (edtDescription.getText().toString().equals("")) {
                Toasty.warning(getActivity(), "Discription is empty", LENGTH_SHORT, true).show();
            } else if (edtTag.getText().toString().equals("")) {
                Toasty.warning(getActivity(), "Category is empty", LENGTH_SHORT, true).show();
            } else if (ideaedit != null) {
                saveEditIdeaToRelm();
                Toasty.success(getActivity(), "Saved", LENGTH_SHORT, true).show();
            } else {
                saveNewIdeaToRealm();
                clearContent();
                Toasty.success(getActivity(), "Saved", LENGTH_SHORT, true).show();
            }
        } else if (v == btnSubmit) {
            sendDataToServer();
            while (realm.where(Picture.class).equalTo("Key", "0").findFirst() != null) {
                Picture picture = realm.where(Picture.class).equalTo("Key", "0").findFirst();
                realm.beginTransaction();
                picture.deleteFromRealm();
                realm.commitTransaction();
            }
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

        //save picture with primary key idea

        while (realm.where(Picture.class).equalTo("Key","0").findFirst() != null)
        {
            Picture picture = realm.where(Picture.class).equalTo("Key","0").findFirst();
            realm.beginTransaction();
            picture.setTitletime(idea.getTitletime());
            picture.setKey("1");
            realm.commitTransaction();
        }

        pictures.clear();
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
                                Toasty.error(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG, true).show();
                            } else {
                                Toasty.success(getActivity(), "Success", Toast.LENGTH_LONG, true).show();
                                clearContent();
                            }
                        }
                    });
        } else {
            Toasty.error(getContext(), "Please fill in the blank space", Toast.LENGTH_LONG, true).show();
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
                Toasty.error(getActivity(), e.toString(), Toast.LENGTH_LONG, true).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploading...");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && imageReturnedIntent != null) {
            ArrayList<Image> imageList = imageReturnedIntent.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            for (Image child : imageList) {
                listBitmaps.add(child.path);

                //push image to server
                File newFile = new File(child.path);
                Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                saveImageToStorage(myBitmap);

                //save image to realm
                if (ideaedit!=null)
                {
                    savePictureOfEditIdeatoRealm(myBitmap);
                } else {
                    savePictureOfNewIdeatoRealm(myBitmap);
                }

            }
            adapter.notifyDataSetChanged();
        }
    }

    private void savePictureOfNewIdeatoRealm(Bitmap bitmap) {
        byte[] btm = CommonMethod.BitmaptoByteArray(bitmap);
        Picture images = new Picture();
        Date date = Calendar.getInstance().getTime();
        String time = date.toString();
        images.setCreateTitletime(System.currentTimeMillis() + "");
        images.setKey("0");
        images.setPicture(btm);

        realm.beginTransaction();
        realm.copyToRealm(images);
        realm.commitTransaction();

        pictures.add(images);
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

        realm.beginTransaction();
        realm.copyToRealm(images);
        realm.commitTransaction();
        pictures.add(images);
    }

    private void clearContent() {
        edtDescription.setText("");
        edtTag.setText("");
        edtTitle.setText("");
        ideaedit = null;
        listBitmaps.clear();
        pictures.clear();
        adapter.notifyDataSetChanged();
    }
}
