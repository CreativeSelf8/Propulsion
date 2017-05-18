package com.apt5.propulsion.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.apt5.propulsion.R;
import com.apt5.propulsion.adapter.GridViewPhotoAdapter;
import com.apt5.propulsion.object.IdeaFb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.apt5.propulsion.ConstantVar.CHILD_IDEA;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class AddIdeaFragment extends Fragment implements View.OnClickListener {
    private EditText edtTitle;
    private EditText edtTag;
    private EditText edtDescription;
    private Button btnAttach;
    private GridView gvPhoto;
    private Button btnSubmit;
    private Button btnSave;
    private Button btnDelete;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Bitmap> listBitmaps;
    private GridViewPhotoAdapter adapter;
    private ArrayList<String> listEncodedImage;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addidea, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        edtTitle = (EditText) rootView.findViewById(R.id.edt_addidea_title);
        edtTag = (EditText) rootView.findViewById(R.id.edt_addidea_tag);
        edtDescription = (EditText) rootView.findViewById(R.id.edt_addidea_desciption);
        btnAttach = (Button) rootView.findViewById(R.id.btn_addidea_add_file);
        gvPhoto = (GridView) rootView.findViewById(R.id.gv_addidea);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_addidea_submit_idea);
        btnSave = (Button) rootView.findViewById(R.id.btn_addidea_save_as_draft);
        btnDelete = (Button) rootView.findViewById(R.id.btn_addidea_delete);
        listBitmaps = new ArrayList<>();
        listEncodedImage = new ArrayList<>();
        adapter = new GridViewPhotoAdapter(listBitmaps, getActivity());

        btnDelete.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnAttach.setOnClickListener(this);

        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listBitmaps.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnAttach) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        } else if (v == btnDelete) {
            clearContent();
            //TODO : delete exist idea in realm
        } else if (v == btnSave) {
            //TODO : save idea to realm
        } else if (v == btnSubmit) {
            sendDataToServer();
        }
    }

    private void sendDataToServer() {
        DatabaseReference newIdeaPost = firebaseDatabase.getReference().child(CHILD_IDEA).push();
        listEncodedImage = new ArrayList<>();
        for (Bitmap child : listBitmaps) {
            listEncodedImage.add(encodedBitmap(child, Bitmap.CompressFormat.JPEG, 100));
        }
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
            newIdea.setEncodedImageList(listEncodedImage);
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

    public String encodedBitmap(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        listBitmaps.add(bitmap);

                        adapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private void clearContent() {
        edtDescription.setText("");
        edtTag.setText("");
        edtTitle.setText("");
        listBitmaps.clear();
        adapter.notifyDataSetChanged();
    }
}
