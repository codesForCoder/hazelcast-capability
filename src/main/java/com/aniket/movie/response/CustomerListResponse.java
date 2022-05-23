package com.aniket.movie.response;

import com.aniket.movie.dto.Customer;
import lombok.Data;

import java.util.List;

@Data
public class CustomerListResponse {
  private EnvironmentDetails environmentDetails;
  private List<Customer> customerList;
  private Long totalPages;
  private Long currentPage;
  private Long pageSize;
  private Long totalElements;


}
