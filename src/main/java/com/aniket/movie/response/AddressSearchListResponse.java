package com.aniket.movie.response;

import com.aniket.movie.dto.Address;
import lombok.Data;

import java.util.List;

@Data
public class AddressSearchListResponse {
  private EnvironmentDetails environmentDetails;
  private List<Address> searchedDataList;
  private Long currentPage;
  private Long pageSize;
  private Long totalElements;
  private String searchQuery;
  private String context;
  private String searchSpace;


}
