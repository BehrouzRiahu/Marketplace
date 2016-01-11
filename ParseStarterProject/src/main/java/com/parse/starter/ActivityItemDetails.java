package com.parse.starter;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

public class ActivityItemDetails extends ActionBarActivity {

    int position;
    TextView price, date, title, phone, description;
    ImageView imageView;
    Button btnDelete;
    boolean deleted = false;


    private void showDialog(final String objectId){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Confirm");
        alert.setMessage("Enter Password");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String value = input.getText().toString();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
                query.whereEqualTo("objectId", objectId);
                query. getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if(e==null){
                            if(parseObject.getString("password").equals(value)){
                                deleteItem(parseObject);
                            }else{
                                Toast.makeText(ActivityItemDetails.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", deleted);
        returnIntent.putExtra("position", position);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_layout);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        price = (TextView) findViewById(R.id.txtPrice);
        date = (TextView) findViewById(R.id.txtDate);
        title = (TextView) findViewById(R.id.txtTitle);
        phone = (TextView) findViewById(R.id.txtPhone);
        description = (TextView) findViewById(R.id.txtDesc);

        btnDelete = (Button) findViewById(R.id.btnDelete);
        position= getIntent().getIntExtra("position", 0);
        final String objectId = getIntent().getStringExtra("objectId");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(objectId);
            }
        });

        imageView = (ImageView) findViewById(R.id.imageViewdetail);
        price.setText(getIntent().getStringExtra(("price")));
        date.setText(getIntent().getStringExtra(("date")));
        title.setText(getIntent().getStringExtra(("title")));
        phone.setText(getIntent().getStringExtra(("phone")));
        description.setText(getIntent().getStringExtra(("desc")));
        String url = getIntent().getStringExtra("url");

        Picasso.with(this).load(url).into(imageView);
    }

    private void deleteItem(ParseObject parseObject) {
        parseObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(ActivityItemDetails.this, "Post Deleted!!", Toast.LENGTH_LONG).show();
                    deleted = true;
                    onBackPressed();
                }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home:
                onBackPressed();
                break;
            case R.id.homeAsUp:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
