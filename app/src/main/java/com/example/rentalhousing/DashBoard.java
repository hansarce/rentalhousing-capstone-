package com.example.rentalhousing;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalhousing.NominatimApiService;
import com.example.rentalhousing.RetrofitClient;
import com.example.rentalhousing.SearchResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoard extends AppCompatActivity {

    private MapView mapView;
    private SearchView searchView;
    private Spinner filterSpinner;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottomnavigationview);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        // Set the user agent value to avoid getting blocked by the OSM servers
        Configuration.getInstance().setUserAgentValue(getPackageName());

        mapView = findViewById(R.id.mapView);
        searchView = findViewById(R.id.searchView);
        filterSpinner = findViewById(R.id.filterSpinner);
        recyclerView = findViewById(R.id.recyclerView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(14.5186, 121.0182);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setTitle("Marker in Pasay");
        mapView.getOverlays().add(startMarker);

        setupSearch();
        setupFilters();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsAdapter(new ArrayList<>(), new SearchResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResult searchResult) {
                displayLocationOnMap(searchResult);
            }
        });
        recyclerView.setAdapter(adapter);

        customizeSearchView();
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
                        adapter.updateSearchResults(results);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(DashBoard.this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Toast.makeText(DashBoard.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilters() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
        mapView.getOverlays().clear();

        if (filter.equals("All")) {
            // Add all markers
        } else if (filter.equals("Restaurants")) {
            // Add restaurant markers
        } else if (filter.equals("Parks")) {
            // Add park markers
        }

        mapView.invalidate();
    }

    private void displayLocationOnMap(SearchResult searchResult) {
        double latitude = Double.parseDouble(searchResult.getLatitude());
        double longitude = Double.parseDouble(searchResult.getLongitude());
        GeoPoint point = new GeoPoint(latitude, longitude);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(searchResult.getDisplayName());
        mapView.getOverlays().add(marker);

        mapView.getController().setCenter(point);
        mapView.getController().setZoom(15.0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // Detach the mapView
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
