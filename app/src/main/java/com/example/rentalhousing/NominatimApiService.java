package com.example.rentalhousing;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimApiService {
    @GET("search")
    Call<List<SearchResult>> search(
            @Query("q") String query,
            @Query("format") String format,
            @Query("addressdetails") int addressDetails,
            @Query("limit") int limit
    );
}
