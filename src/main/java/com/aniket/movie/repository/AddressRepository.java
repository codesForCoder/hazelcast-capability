package com.aniket.movie.repository;

import com.aniket.movie.entity.AddressEntity;
import com.aniket.movie.entity.AddressEntityProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<AddressEntity, Integer> {
	 Page<AddressEntityProjection> findByAddressIdNotNull(Pageable pageConfig);
}
