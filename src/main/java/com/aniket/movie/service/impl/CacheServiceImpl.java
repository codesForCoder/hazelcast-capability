package com.aniket.movie.service.impl;

import com.aniket.movie.request.SearchListRequest;
import com.aniket.movie.util.ApplicationUtils;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import com.hazelcast.org.json.JSONObject;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aniket.movie.response.SearchListResponse;
import com.aniket.movie.service.CacheService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.aniket.movie.constant.ApplicationConstant.LIKE;

@Component
@Slf4j
public class CacheServiceImpl implements CacheService {

	@Autowired
	private ApplicationUtils applicationUtils;

	@Autowired
	@Qualifier("hazelcastInstance")
	private HazelcastInstance hazelcastInstance;
	@Override
	public SearchListResponse findSearchedResults(SearchListRequest searchListRequest) {
		SearchListResponse searchListResponse = new SearchListResponse();
		searchListResponse.setEnvironmentDetails(applicationUtils.getCurrentEnv());
		searchListResponse.setContext(searchListRequest.getContext());
		searchListResponse.setSearchQuery(searchListRequest.getSearchQuery());
		searchListResponse.setCurrentPage(Long.valueOf(searchListRequest.getCurrentPage()));
		searchListResponse.setPageSize(Long.valueOf(searchListRequest.getPageSize()));
		searchListResponse.setSearchSpace(searchListRequest.getSearchSpace());
		IMap<String, HazelcastJsonValue> map = hazelcastInstance.getMap( searchListRequest.getContext() );
		StringBuilder queryBuilder = new StringBuilder();
		String finalQuery = queryBuilder.append(searchListRequest.getSearchQuery()).toString();
		finalQuery = finalQuery.replaceAll("\\*",LIKE);
		log.info("QUERY SEARCH SPACE - {} | QUERY STR - {}",searchListRequest.getSearchSpace(),finalQuery);
		Predicate<Object, Object> searchPredicate = Predicates.ilike(searchListRequest.getSearchSpace(), finalQuery);
		PagingPredicate pagingPredicate = Predicates.pagingPredicate( searchPredicate, Math.toIntExact(searchListRequest.getPageSize()));
		while(searchListRequest.getCurrentPage()>0)
		{
			pagingPredicate.nextPage();
			searchListRequest.setCurrentPage(searchListRequest.getCurrentPage()-1);
		}
		Collection<HazelcastJsonValue> searchedResults =map.values(pagingPredicate);
		List<Object> finalResults = searchedResults.stream().map(item->item.getValue()).collect(Collectors.toList());
		log.info("SEARCHED RESULTS - {}",finalResults);
		searchListResponse.setSearchedDataList(finalResults);
		searchListResponse.setTotalElements(Long.valueOf(finalResults.size()));
		return searchListResponse;
	}

}
