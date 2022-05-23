package com.aniket.movie.service;

import com.aniket.movie.request.SearchListRequest;
import com.aniket.movie.response.SearchListResponse;

public interface CacheService {

	SearchListResponse findSearchedResults(SearchListRequest searchListRequest);

 
}
