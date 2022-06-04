package com.example.lens_search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class searchRVAdapter extends RecyclerView.Adapter<searchRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<searchRVModal> searchRVModals;

    public searchRVAdapter(Context context, ArrayList<searchRVModal> searchRVModals) {
        this.context = context;
        this.searchRVModals = searchRVModals;
    }

    @NonNull
    @Override
    public searchRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_results,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchRVAdapter.ViewHolder holder, int position) {
        searchRVModal searchRVModal = searchRVModals.get(position);
        holder.title.setText(searchRVModal.getTitle());
        holder.snippet.setText(searchRVModal.getLink());
        holder.desc.setText(searchRVModal.getSnippet());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(searchRVModal.getLink()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchRVModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title,snippet,desc;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cv_title);
            snippet = itemView.findViewById(R.id.cv_snippet);
            desc = itemView.findViewById(R.id.cv_snippet);
        }
    }
}
