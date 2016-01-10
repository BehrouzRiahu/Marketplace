package com.parse.starter;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerView extends RecyclerView.OnScrollListener{

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mlinearLayoutManager;

    EndlessRecyclerView(LinearLayoutManager mlinearLayoutManager){
        this.mlinearLayoutManager = mlinearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mlinearLayoutManager.getItemCount();
        firstVisibleItem = mlinearLayoutManager.findFirstVisibleItemPosition();

        if(loading){
            if(totalItemCount > previousTotal){
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)){
            current_page++;
            onLoadMire(current_page);
            loading = true;
        }
    }

    public abstract void onLoadMire(int current_page);
}
