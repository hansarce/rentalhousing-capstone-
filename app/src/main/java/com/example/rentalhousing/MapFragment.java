package com.example.rentalhousing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private MapView mapView;
    private SearchView searchView;
    private Spinner filterSpinner;
    private final List<SearchResult> allSearchResults = new ArrayList<>();

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMultiTouchControls(true);
        mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(14.5995, 120.9842); // Default coordinates
        mapController.setCenter(startPoint);

        filterSpinner = view.findViewById(R.id.filterSpinner); // Initialize filterSpinner
        searchView = view.findViewById(R.id.searchView); // Initialize searchView

        setupFilters(); // Setup filter spinner
        setupSearch(); // Setup search view

        return view;
    }



    private void displayLocationOnMap(SearchResult searchResult) {
        double latitude = Double.parseDouble(searchResult.getLatitude());
        double longitude = Double.parseDouble(searchResult.getLongitude());

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setTitle(searchResult.getDisplayName());
        mapView.getOverlays().add(marker);

        mapView.invalidate();  // Refresh the map to show the marker
    }

    private void applyFilter(String filter) {
        mapView.getOverlays().clear();  // Clear existing markers

        List<SearchResult> filteredResults = new ArrayList<>();
        if (filter.equals("All")) {
            filteredResults.addAll(allSearchResults);
        } else {
            // Add your filtering logic here
        }

        for (SearchResult result : filteredResults) {
            displayLocationOnMap(result);
        }
    }

    private void setupFilters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
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

    private void setupSearch() {
        if (searchView != null) {
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
    }

    private void searchLocation(String location) {
        NominatimApiService apiService = RetrofitClient.getClient().create(NominatimApiService.class);
        Call<List<SearchResult>> call = apiService.search(location, "json", 1, 10);

        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchResult>> call, @NonNull Response<List<SearchResult>> response) {
                if (response.isSuccessful()) {
                    List<SearchResult> results = response.body();
                    if (results != null && !results.isEmpty()) {
                        allSearchResults.clear();
                        allSearchResults.addAll(results);
                        applyFilter(filterSpinner.getSelectedItem().toString()); // Apply current filter
                    } else {
                        Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchResult>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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
