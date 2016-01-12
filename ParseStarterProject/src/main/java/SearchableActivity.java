import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.ActivityItemDetails;
import com.parse.starter.EndlessRecyclerView;
import com.parse.starter.ItemsModel;
import com.parse.starter.R;
import com.parse.starter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends ActionBarActivity implements RecyclerViewAdapter.ClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    ArrayList<ItemsModel> data = new ArrayList<>();
    ProgressDialog dialog;
    String query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browse_fragment_layout);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        layoutManager = new LinearLayoutManager(SearchableActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnScrollListener(new EndlessRecyclerView(layoutManager) {
            @Override
            public void onLoadMire(int current_page) {
                int limit = current_page * 5;
                LoadMoreItems(limit);
            }
        });
    }

    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            query = intent.getStringExtra(SearchManager.QUERY);
            LoadData();
        }
    }

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
        startActivity(i);
    }

    private void LoadMoreItems(int limit) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        query.whereContains("title", this.query);
        query.setLimit(limit).orderByAscending("createdAt");
        dialog = new ProgressDialog(this);
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

                        data.add(model);
                    }
                    adapter.UpdateData(data);
                }else {
                    Toast.makeText(SearchableActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void LoadData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        query.setLimit(5).orderByAscending("createdAt");
        query.whereContains("title", this.query);
        dialog = new ProgressDialog(this);
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

                        data.add(model);
                    }
                    adapter = new RecyclerViewAdapter(data, SearchableActivity.this);
                    adapter.setClickListener(SearchableActivity.this);
                    recyclerView.setAdapter(adapter);
                }else {
                    Toast.makeText(SearchableActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
