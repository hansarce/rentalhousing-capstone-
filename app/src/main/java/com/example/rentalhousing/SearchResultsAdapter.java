package com.example.rentalhousing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<SearchResult> searchResults;
    private final OnItemClickListener listener;

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
        if (searchResults != null && position < searchResults.size()) {
            SearchResult searchResult = searchResults.get(position);
            holder.bind(searchResult, listener);
        }
    }

    @Override
    public int getItemCount() {
        return (searchResults != null) ? searchResults.size() : 0;
    }

    public void updateSearchResults(List<SearchResult> newResults) {
        if (newResults != null) {
            searchResults.clear();
            searchResults.addAll(newResults);
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeName;
        private final TextView placeCoordinates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeCoordinates = itemView.findViewById(R.id.placeCoordinates);
        }

        public void bind(final SearchResult searchResult, final OnItemClickListener listener) {
            placeName.setText(searchResult.getDisplayName());

            // Display address details
            String details = "";
            if (searchResult.getAddress() != null) {
                SearchResult.Address address = searchResult.getAddress();
                details = address.getRoad() + ", " +
                        address.getSuburb() + ", " +
                        address.getCity() + ", " +
                        address.getState() + ", " +
                        address.getCountry() + ", " +
                        address.getPostcode();
            }
            placeCoordinates.setText(details);

            itemView.setOnClickListener(v -> listener.onItemClick(searchResult));
        }
    }
}
