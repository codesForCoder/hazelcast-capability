package com.aniket.movie.service.impl;

import org.springframework.stereotype.Component;

import com.aniket.movie.response.SearchListResponse;
import com.aniket.movie.service.CacheService;

@Component
public class CacheServiceImpl implements CacheService {

	@Override
	public SearchListResponse findSearchedResults(int limit, int page, String context, String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createSrachTree(String context) {
		// TODO Auto-generated method stub
	}

}
