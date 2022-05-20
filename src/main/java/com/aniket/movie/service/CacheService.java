package com.aniket.movie.service;

import com.aniket.movie.response.SearchListResponse;

public interface CacheService {

	SearchListResponse findSearchedResults(int limit, int page, String context, String query);

	void createSrachTree(String context);

 
}
