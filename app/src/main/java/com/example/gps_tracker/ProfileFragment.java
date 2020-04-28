package com.example.gps_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class ProfileFragment extends Fragment {

    private String name;
    private String email;
    private Uri selectedImage;
    private ImageView avatar;
    private EditText editName;

    private View rootView;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private SharedPreferences preferences;

    private static final int GALLERY_REQUEST = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        myRef = database.getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        preferences = getDefaultSharedPreferences(getContext());

        email = preferences.getString("emailForBD","");
        avatar = (ImageView) rootView.findViewById(R.id.avatar);
        editName = ((EditText) rootView.findViewById(R.id.editName));

        //отображение информации о пользователе
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated

                editName.setText(dataSnapshot.child(email).child("name").getValue(String.class));
                String image = preferences.getString(email, "");
                if (!image.equals("")) {
                    Uri uri = Uri.parse(preferences.getString(email,""));
                    Picasso.get().load(uri).transform(new CircleTransform()).into(avatar);
                }
                else{
                    String imagePath = dataSnapshot.child(email).child("photo").getValue(String.class);
                    StorageReference riversRef = mStorageRef.child(imagePath);
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).transform(new CircleTransform()).into(avatar);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(email, String.valueOf(uri));
                            editor.apply();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(rootView.getContext(),"error",Toast.LENGTH_LONG).show();
            }
        });

        //кнопка для сохранения изменений
        final Button save = rootView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = ((EditText) rootView.findViewById(R.id.editName)).getText().toString();
                myRef.child(email).child("name").setValue(name);
                final String imagePath = "gs://gps-tracker-275108.appspot.com"+email; // путь до обложки

                if (selectedImage != null) {

                    uploadFile(imagePath, selectedImage);
                }

                Toast.makeText(rootView.getContext(),"Изменения успешно сохранены",Toast.LENGTH_LONG).show();
            }
        });

        final Button changePassword = rootView.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });
        final Button exit = rootView.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.putExtra("options",false);
                startActivity(intent);
            }
        });

        //открытие галереи по нажаттию на аватар
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);

            }
        });
        return rootView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        super.onActivityResult(requestCode, resultCode, resultIntent);

        avatar = (ImageView) rootView.findViewById(R.id.avatar);

        if (resultCode == -1) {

            switch (requestCode) {
                //меняем изображение в профиле на изображение, которое выбрали в галерее
                case GALLERY_REQUEST:
                    selectedImage = resultIntent.getData();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(email, String.valueOf(selectedImage));
                    editor.apply();
                    Picasso.get().load(selectedImage).transform(new CircleTransform()).into(avatar);


            }

        }

    }

    //загрузка файла на firebase
    private void uploadFile(String path, Uri pathOfFile) {
        //if there is a file to upload
        StorageReference riversRef = mStorageRef.child(path);
        myRef.child(email).child("photo").setValue(path);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        riversRef.putFile(pathOfFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();

                        //and displaying error message

                        //* заменить на активити
                        //Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }
}