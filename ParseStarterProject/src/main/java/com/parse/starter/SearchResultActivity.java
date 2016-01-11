package com.parse.starter;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class SearchResultActivity extends ActionBarActivity implements RecyclerViewAdapter.ClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<ItemsModel> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_fragment_layout);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        data = getIntent().getParcelableArrayListExtra("data");

        if(data.size() < 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Oops! No Record Found!!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alert.dismiss();
                    finish();
                }
            });
            alert = builder.create();
            alert.show();
        }else{
            adapter = new RecyclerViewAdapter(data, SearchResultActivity.this);
            adapter.setClickListener(SearchResultActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                boolean result = data.getBooleanExtra("result", false);
                if (result){
                    int position=data.getIntExtra("position", 0);
                    this.data.remove((position));
                    adapter.UpdateData(this.data);
                }
            }
            if(resultCode == RESULT_CANCELED){

            }
        }
    }

    AlertDialog alert = null;
    @Override
    public void OnItemClick(View view, int position) {
        Intent i = new Intent(this, ActivityItemDetails.class);
        ItemsModel model = data.get(position);
        i.putExtra("price", model.getPrice());
        i.putExtra("date", model.getDate());
        i.putExtra("title", model.getTitle());
        i.putExtra("desc", model.getDesc());
        i.putExtra("phone", model.getPhone());
        i.putExtra("url", model.getImagefile());
        i.putExtra("objectId", model.getObjectId());
        i.putExtra("position", position);
        startActivityForResult(i, 1);

    }
}
