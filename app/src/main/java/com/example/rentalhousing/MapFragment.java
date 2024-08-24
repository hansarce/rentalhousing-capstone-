package com.example.rentalhousing;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private WebView webView;
    private SearchView searchView;
    private Spinner filterSpinner;
    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private List<SearchResult> allSearchResults = new ArrayList<>(); // Store all results

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        webView = view.findViewById(R.id.mapView);
        searchView = view.findViewById(R.id.searchView);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        recyclerView = view.findViewById(R.id.recyclerView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/map.html");

        setupSearch();
        setupFilters();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchResultsAdapter(new ArrayList<>(), new SearchResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResult searchResult) {
                displayLocationOnMap(searchResult);
            }
        });
        recyclerView.setAdapter(adapter);

        customizeSearchView();

        return view;
    }

    private void customizeSearchView() {
        int searchViewTextColor = Color.BLACK;
        int searchViewHintColor = Color.GRAY;

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(searchViewTextColor);
            searchEditText.setHintTextColor(searchViewHintColor);
        }
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchLocation(String location) {
        NominatimApiService apiService = RetrofitClient.getClient().create(NominatimApiService.class);
        Call<List<SearchResult>> call = apiService.search(location, "json", 1, 10);

        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call, Response<List<SearchResult>> response) {
                if (response.isSuccessful()) {
                    List<SearchResult> results = response.body();
                    if (results != null && !results.isEmpty()) {
                        allSearchResults.clear();
                        allSearchResults.addAll(results);
                        adapter.updateSearchResults(results);
                        recyclerView.setVisibility(View.VISIBLE);
                        applyFilter(filterSpinner.getSelectedItem().toString()); // Apply current filter
                    } else {
                        Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }private void setupFilters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = (String) parent.getItemAtPosition(position);
                applyFilter(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void applyFilter(String filter) {
        webView.evaluateJavascript("clearMarkers();", null); // Clear existing markers

        List<SearchResult> filteredResults = new ArrayList<>();
        if (filter.equals("All")) {
            filteredResults.addAll(allSearchResults);
        } else {
            // Implement your filtering logic here based on 'filter'
            // For example, if you have a 'type' property in SearchResult:
            for (SearchResult result : allSearchResults) {
                // if (result.getType().equals(filter)) {
                //     filteredResults.add(result);
                // }
            }
        }

        for (SearchResult result : filteredResults) {
            displayLocationOnMap(result);
        }
    }

    private void displayLocationOnMap(SearchResult searchResult) {
        double latitude = Double.parseDouble(searchResult.getLatitude());
        double longitude = Double.parseDouble(searchResult.getLongitude());

        webView.evaluateJavascript("addMarker(" + latitude + ", " + longitude + ", '" +
                searchResult.getDisplayName() + "');", null);
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
