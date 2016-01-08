package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    EditText search_query;
    Spinner regionSpinner, categorySpinner;
    Button find;

    MyParseAdapter regionAdapter, categoryAdapter;
    ParseObject regionObject, categoryObject;
    ArrayList<ItemsModel> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment_layout, container, false);
        regionSpinner = (Spinner) rootView.findViewById(R.id.spinnerRegion);
        categorySpinner = (Spinner) rootView.findViewById(R.id.spinnerCategory);
        search_query = (EditText) rootView.findViewById(R.id.et_search);
        find = (Button) rootView.findViewById(R.id.btnFind);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery query = ParseQuery.getQuery("Product");
                query.whereEqualTo("cat_object", categoryObject);
                query.whereEqualTo("reg_object", regionObject);
                if(search_query.getText().length() > 0 ){
                    query.whereMatches("title", "(" + search_query.getText().toString() + ")", "i");
                }
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e == null){
                            data.clear();
                            for(int a = 0; a < list.size(); a++){
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
                            Intent i = new Intent(getActivity(), SearchResultActivity.class);
                            i.putExtra("data", data);
                            startActivity(i);
                        } else if (e.getCode() == ParseException.CONNECTION_FAILED){
                            Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        regionSpinnerSetup();
        categorySpinnerSetup();
    }

    public void regionSpinnerSetup(){
        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Region");
                return query;
            }
        };

        regionAdapter = new MyParseAdapter(getActivity(), factory);
        regionAdapter.setTextKey("name");
        regionSpinner.setAdapter(regionAdapter);
        regionSpinner.setSelection(1);
        regionSpinner.setOnItemSelectedListener(new RegionSpinnerListener());
    }


    class RegionSpinnerListener implements Spinner.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ParseObject theSelectedObject = regionAdapter.getItem(i);
            Log.e("ABC", "Name is : " + theSelectedObject.getString("name") + " objectId is " + theSelectedObject.getObjectId());
            regionObject = theSelectedObject;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public void categorySpinnerSetup(){
        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Category");
                return query;
            }
        };

        categoryAdapter = new MyParseAdapter(getActivity(), factory);
        categoryAdapter.setTextKey("name");
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(1);
        categorySpinner.setOnItemSelectedListener(new CategorySpinnerListener());
    }

    class CategorySpinnerListener implements Spinner.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ParseObject theSelectedObject = categoryAdapter.getItem(i);
            Log.e("ABC", "Name is : " + theSelectedObject.getString("name") + " objectId is " + theSelectedObject.getObjectId());
            categoryObject = theSelectedObject;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
