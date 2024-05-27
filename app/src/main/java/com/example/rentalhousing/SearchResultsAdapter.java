package com.example.rentalhousing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalhousing.SearchResult;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<SearchResult> searchResults;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchResult searchResult);
    }

    public SearchResultsAdapter(List<SearchResult> searchResults, OnItemClickListener listener) {
        this.searchResults = searchResults;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult searchResult = searchResults.get(position);
        holder.bind(searchResult, listener);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateSearchResults(List<SearchResult> newResults) {
        searchResults.clear();
        searchResults.addAll(newResults);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView placeName;
        private TextView placeCoordinates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeCoordinates = itemView.findViewById(R.id.placeCoordinates);
        }

        public void bind(final SearchResult searchResult, final OnItemClickListener listener) {
            placeName.setText(searchResult.getDisplayName());
            placeCoordinates.setText("Lat: " + searchResult.getLatitude() + ", Lon: " + searchResult.getLongitude());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(searchResult);
                }
            });
        }
    }
}
