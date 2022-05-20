package com.aniket.movie.response;

import lombok.Data;

import java.util.List;

@Data
public class SearchListResponse {

  private List<Object> searchedDataList;
  private Long totalPages;
  private Long currentPage;
  private Long pageSize;
  private Long totalElements;
  private String searchQuery;
  private String context;


}
