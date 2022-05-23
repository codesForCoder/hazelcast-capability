package com.aniket.movie.response;

import com.aniket.movie.dto.Customer;
import lombok.Data;

import java.util.List;

@Data
public class CustomerSearchListResponse {
  private EnvironmentDetails environmentDetails;
  private List<Customer> searchedDataList;
  private Long currentPage;
  private Long pageSize;
  private Long totalElements;
  private String searchQuery;
  private String context;
  private String searchSpace;


}
