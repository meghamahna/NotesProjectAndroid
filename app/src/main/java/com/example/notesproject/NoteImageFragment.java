package com.example.notesproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;

import java.io.File;

import java.io.IOException;

public class NoteImageFragment extends Fragment {

    private static final int IMAGE_REQUEST_CODE = 0;
    private final int CAMERA_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;

    NoteClass notes;
    ImageView image;
    private File directory;

    ListView listView;
    Button save;

    public NoteImageFragment(NoteClass notes) {
        this.notes = notes;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_image_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageButton imageButton = view.findViewById(R.id.saveImageButton);
        listView = view.findViewById(R.id.listView);
        image = view.findViewById(R.id.imageview);
        save = view.findViewById(R.id.saveButton);

        if(!checkCameraPermission()){
            requestCameraPermission();
        }

        if(notes != null){

            File sdCard = Environment.getExternalStorageDirectory();
            directory = new File(sdCard.getAbsolutePath() + "/NotesImages/" + notes.getId());
            if(!directory.exists())
                directory.mkdirs();

        }


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items ={"Camera", "Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Image");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Camera")){


                            if(checkCameraPermission()){
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivityForResult(intent, CAMERA_REQUEST_CODE);

                                    File pictureFile;
                                    try {
                                        pictureFile = getPictureFile();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        return;
                                    }
                                    if (pictureFile != null) {
                                        Uri photoURI = Uri.fromFile(pictureFile);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                                    }

                                }
                            } else{
                                requestCameraPermission();
                            }


                        }
                        else if (items[i].equals("Gallery")){

                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            String[] mimeTypes = {"image/jpeg", "image/png"};
                            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                            startActivityForResult(intent,GALLERY_REQUEST_CODE);


                        }
                        else if (items[i].equals("Cancel")){
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getPictureFile() throws IOException {


        String pictureFile = String.format("image_%d.png", notes.getId());
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDirectory);
        String pictureFilePath = image.getAbsolutePath();
        return image;
    }



    private boolean checkCameraPermission(){
        int permissionState = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int write_permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return (permissionState == PackageManager.PERMISSION_GRANTED) &&
                (read_permission == PackageManager.PERMISSION_GRANTED) &&
                (write_permission == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission(){
        requestPermissions(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == IMAGE_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode){
                case CAMERA_REQUEST_CODE:

                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    image.setImageBitmap(bitmap);
                    save.setVisibility(View.VISIBLE);
                    break;

                case GALLERY_REQUEST_CODE:

                    Uri uri = data.getData();
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        image.setImageBitmap(bitmap);
                        save.setVisibility(View.VISIBLE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;
            }

        }
    }



}
