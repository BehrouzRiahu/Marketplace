package com.parse.starter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BrowseFragment extends Fragment implements RecyclerViewAdapter.ClickListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    ArrayList<ItemsModel> data = new ArrayList<>();
    ProgressDialog dialog;
    final private int PICK_IMAGE = 50;
    final private int CAPTURE_IMAGE = 51;

    private static final String IMAGE_DIRECTORY_NAME = "My Camera";

    private Uri fileUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.browse_fragment_layout, container, false);
        setupFloatingActionMenu(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnScrollListener(new EndlessRecyclerView(layoutManager) {
            @Override
            public void onLoadMire(int current_page) {
                int limit = current_page * 5;
                LoadMoreItems(limit);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            Uri path;
            int type;
            if(requestCode == PICK_IMAGE){
                path = data.getData();
                type = 50;
            }else {
                path = fileUri;
                type = 51;
            }
            Intent i = new Intent(getActivity(), PostActivity.class);
            i.putExtra("extras", path);
            i.putExtra("image_from", type);
            startActivity(i);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setOnScrollListener(new EndlessRecyclerView(layoutManager) {
            @Override
            public void onLoadMire(int current_page) {
                int limit = current_page * 5;
                LoadMoreItems(limit);
            }
        });
        LoadData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putParcelable("file_uri", fileUri);
        }
    }

    private  void onRestoreInstanceState(Bundle savedInstanceState){
        if(savedInstanceState != null){
            fileUri = savedInstanceState.getParcelable("file_uri");
        }
    }

    public BrowseFragment() {

    }

    @Override
    public void OnItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), ActivityItemDetails.class);
        ItemsModel model = data.get(position);
        i.putExtra("price", model.getPrice());
        i.putExtra("date", model.getDate());
        i.putExtra("title", model.getTitle());
        i.putExtra("desc", model.getDesc());
        i.putExtra("phone", model.getPhone());
        i.putExtra("url", model.getImagefile());
        i.putExtra("objectId", model.getObjectId());
        startActivity(i);

    }

    private void setupFloatingActionMenu(Context context) {
        ImageView imgv = new ImageView(context);
        imgv.setImageResource(R.drawable.plus_icon);

        FloatingActionButton actionButton = new FloatingActionButton.Builder((Activity) context).setContentView(imgv)
                .setBackgroundDrawable(R.drawable.selector_button_action_blue).build();

        ImageView capture = new ImageView(context);
        capture.setImageResource(R.drawable.camera_icon);

        ImageView upload = new ImageView(context);
        upload.setImageResource(R.drawable.share_icon);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder((Activity) context).setBackgroundDrawable(getResources()
                .getDrawable(R.drawable.selector_button_action_blue));

        SubActionButton buttonSortCap = itemBuilder.setContentView(capture).build();
        SubActionButton buttonSortUpd = itemBuilder.setContentView(upload).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder((Activity) context)
                .addSubActionView(buttonSortCap)
                .addSubActionView(buttonSortUpd)
                .enableAnimations()
                .attachTo(actionButton)
                .build();

        buttonSortCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        buttonSortUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
            }
        });
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.d(IMAGE_DIRECTORY_NAME, "OopS! Filed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHMMSS", Locale.getDefault()).format(new Date());

        File mediaFile = null;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    private void LoadMoreItems(int limit) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        query.setLimit(limit).orderByAscending("createdAt");
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading");
        dialog.setMessage("Please Wait...");
        dialog.show();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    data.clear();
                    for (int a = 0; a < list.size(); a++) {
                        ParseObject object = list.get(a);
                        ItemsModel model = new ItemsModel();
                        model.setDate(String.valueOf(object.getCreatedAt().toLocaleString()));
                        model.setPrice(object.getNumber("price").toString());
                        model.setPhone(object.getNumber("phone").toString());
                        model.setDesc(object.getString("desc"));
                        model.setTitle(object.getString("title"));
                        model.setImagefile(object.getParseFile("image").getUrl());
                        model.setObjectId(object.getObjectId());

                        data.add(model);
                    }
                    adapter.UpdateData(data);
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(getActivity(), "No internet Connection, please check your connection!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void LoadData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        query.setLimit(5).orderByAscending("createdAt");
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading");
        dialog.setMessage("Please Wait...");
        dialog.show();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    data.clear();
                    for (int a = 0; a < list.size(); a++) {
                        ParseObject object = list.get(a);
                        ItemsModel model = new ItemsModel();
                        model.setDate(String.valueOf(object.getCreatedAt().toLocaleString()));
                        model.setPrice(object.getNumber("price").toString());
                        model.setPhone(object.getNumber("phone").toString());
                        model.setDesc(object.getString("desc"));
                        model.setTitle(object.getString("title"));
                        model.setImagefile(object.getParseFile("image").getUrl());
                        model.setObjectId(object.getObjectId());

                        data.add(model);
                    }
                    adapter = new RecyclerViewAdapter(data, getActivity());
                    adapter.setClickListener(BrowseFragment.this);
                    recyclerView.setAdapter(adapter);
                }else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(getActivity(), "No internet Connection, please check your connection!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

