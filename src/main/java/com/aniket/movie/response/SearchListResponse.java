package com.aniket.movie.response;

import com.hazelcast.org.json.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class SearchListResponse {
  private EnvironmentDetails environmentDetails;
  private List<Object> searchedDataList;
  private Long currentPage;
  private Long pageSize;
  private Long totalElements;
  private String searchQuery;
  private String context;
  private String searchSpace;


}
