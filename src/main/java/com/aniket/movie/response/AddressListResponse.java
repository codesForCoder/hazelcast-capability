package com.aniket.movie.response;

import com.aniket.movie.dto.Address;
import lombok.Data;

import java.util.List;

@Data
public class AddressListResponse {
  private EnvironmentDetails environmentDetails;
  private List<Address> addressList;
  private Long totalPages;
  private Long currentPage;
  private Long pageSize;
  private Long totalElements;


}
