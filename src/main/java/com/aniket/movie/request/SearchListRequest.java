package com.aniket.movie.request;

import com.aniket.movie.response.EnvironmentDetails;
import lombok.Data;

import java.util.List;

@Data
public class SearchListRequest {

  private Long currentPage;
  private Long pageSize;
  private String searchQuery;
  private String context;
  private String searchSpace;


}
