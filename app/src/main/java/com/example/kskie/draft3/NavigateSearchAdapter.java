package com.example.kskie.draft3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kskie on 28/03/2018.
 */

public class NavigateSearchAdapter extends RecyclerView.Adapter<NavigateSearchAdapter.SearchViewHolder>{

        ArrayList<Node> foundNodes;
        Context context;

        class SearchViewHolder extends RecyclerView.ViewHolder {

            TextView txtLocation;

            public SearchViewHolder(View itemView) {
                super(itemView);
                txtLocation = itemView.findViewById(R.id.txt_location);
            }
        }

        public NavigateSearchAdapter(Context context, ArrayList<Node> foundNodes) {
            this.context = context;
            this.foundNodes = foundNodes;
        }

        @Override
        public NavigateSearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.navigate_list_items, parent, false);
            return new NavigateSearchAdapter.SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchViewHolder holder, int position) {
            Node currentNode = foundNodes.get(position);
            holder.txtLocation.setText(""+ position+"." +currentNode.getLocation());

        }

        @Override
        public int getItemCount() {
            return foundNodes.size();
        }

}



