package com.parse.starter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class PostActivity extends ActionBarActivity {

    private String selectedImagePath = "";
    final private int PICK_IMAGE = 50;
    ImageView postImage;
    Spinner regionSpinner, categorySpinner;
    ParseQueryAdapter<ParseObject> regionAdapter;
    ParseQueryAdapter<ParseObject> categoryAdapter;
    ParseObject regionobject, categoryobject;
    ScrollView scrollView;
    EditText editText_title, editText_desc, editText_price, editText_password, editText_phone;
    Button submit;
    Bitmap bitmappost = null;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activivy_layout);
        scrollView = (ScrollView) findViewById(R.id.scroll_view_post);
        regionSpinner = (Spinner) findViewById(R.id.spinnerRegion);
        categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
        editText_desc = (EditText) findViewById(R.id.et_desc);
        editText_title = (EditText) findViewById(R.id.et_title);
        editText_price = (EditText) findViewById(R.id.et_price);
        editText_password = (EditText) findViewById(R.id.et_password);
        editText_phone = (EditText) findViewById(R.id.et_phone);
        submit = (Button) findViewById(R.id.btn_post);

        setActionBar();

        scrollView.setVerticalScrollBarEnabled(false); //Define whether the vertical scrollbar should be drawn or not
        regionSpinnerSetup();
        categorySpinnerSetup();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_phone.getText().toString().isEmpty() || editText_title.getText().toString().isEmpty() ||
                        editText_price.getText().toString().isEmpty() || editText_password.getText().toString().isEmpty()
                        || editText_desc.getText().toString().isEmpty()) {
                    Toast.makeText(PostActivity.this, "Please fill into all fields!", Toast.LENGTH_LONG).show();
                } else {
                    dialog = new ProgressDialog(PostActivity.this);
                    dialog.setTitle("Posting");
                    dialog.setMessage("Please Wait...");
                    dialog.show();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmappost.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    final ParseFile pFile = new ParseFile(stream.toByteArray());

                    pFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            if (e == null) {
                                ParseObject object = new ParseObject("Product");
                                object.put("title", editText_title.getText().toString());
                                object.put("price", Double.valueOf(editText_price.getText().toString()));
                                object.put("desc", editText_desc.getText().toString());
                                object.put("password", editText_password.getText().toString());
                                object.put("cat_object", categoryobject);
                                object.put("reg_object", regionobject);
                                object.put("phone", Double.valueOf(editText_phone.getText().toString()));
                                object.put("image", pFile);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                        if (e == null) {
                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
                                            query.orderByDescending("updatedAt");
                                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                public void done(ParseObject object, ParseException e) {

                                                    if (object == null) {
                                                        Log.d("product", "The getFirst request failed.");
                                                    } else {
                                                        Intent i = new Intent(PostActivity.this, ActivityItemDetails.class);
                                                        i.putExtra("price", object.getNumber("price").toString());
                                                        i.putExtra("title", object.getString("title"));
                                                        i.putExtra("date", object.getCreatedAt().toString());
                                                        i.putExtra("desc", object.getString("desc"));
                                                        i.putExtra("phone", object.getNumber("phone").toString());
                                                        i.putExtra("objectId", object.getObjectId());
                                                        ParseFile file = object.getParseFile("image");
                                                        i.putExtra("url", file.getUrl());
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                }
                                            });

                                            Toast.makeText(PostActivity.this, "Post Has been Submitted.", Toast.LENGTH_LONG).show();
                                        } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                                            Toast.makeText(PostActivity.this, "No internet Connection please check your connection!",
                                                    Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(PostActivity.this, "Error:" + e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                                Toast.makeText(PostActivity.this, "No internet Connection please check your connection!",
                                        Toast.LENGTH_LONG).show();
                                return;


                            } else {
                                Toast.makeText(PostActivity.this, "Error:" + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                return;

                            }
                        }
                    });


                }
            }
        });

        postImage = (ImageView) findViewById(R.id.imageViewdetail);
        Uri extras = getIntent().getParcelableExtra("extras");
        int image_from = getIntent().getIntExtra("image_from", 0);
        extractImage(extras, image_from);

    }

    private void setActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));

    }

    public void regionSpinnerSetup() {
        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Region");
                return query;
            }
        };

        regionAdapter = new ParseQueryAdapter<ParseObject>(this,
                factory);
        regionAdapter.setTextKey("name");
        regionSpinner.setAdapter(regionAdapter);
        regionSpinner.setSelection(1);
        regionSpinner.setOnItemSelectedListener(new RegionSpinnerListener());
    }

    public void categorySpinnerSetup() {
        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Category");
                return query;
            }
        };

        categoryAdapter = new ParseQueryAdapter<ParseObject>(this,
                factory);
        categoryAdapter.setTextKey("name");
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(1);
        categorySpinner
                .setOnItemSelectedListener(new CategorySpinnerListener());
    }

    class RegionSpinnerListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {
            // TODO Auto-generated method stub

            ParseObject theSelectedObject = regionAdapter.getItem(position);
            Log.e("ABC", "Name is : " + theSelectedObject.getString("name") + " objectId is : " + theSelectedObject.getObjectId());
            regionobject = theSelectedObject;
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

    }

    class CategorySpinnerListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {
            // TODO Auto-generated method stub
            ParseObject theSelectedObject = categoryAdapter.getItem(position);
            Log.e("ABC", theSelectedObject.getString("name") + " objectId is : " + theSelectedObject.getObjectId());
            categoryobject = theSelectedObject;
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }

    }

    private void extractImage(Uri data, int image_from) {
        if (image_from == 50)
            selectedImagePath = getAbsolutePath(data);
        else if (image_from == 51)
            selectedImagePath = data.getPath();

        Bitmap bitmap = decodeFile(selectedImagePath); //we call decodeFile method to resize it
        postImage.setImageBitmap(bitmap); //then we set it as the imageView of the post
        bitmappost = bitmap; //we set the bitmap to our bitmap object
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 160;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2; //the documentation recommends to use to the power of two instead of scale++

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
