package com.aniket.movie.repository;

import com.aniket.movie.entity.CustomerEntity;
import com.aniket.movie.entity.CustomerEntityProjection;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<CustomerEntity, Integer> {
	 Page<CustomerEntityProjection> findByCustomerIdNotNull(Pageable pageConfig);
	 @Query(
			  value = "SELECT customer_id FROM CUSTOMER WHERE address_id = :addressId", 
			  countQuery = "SELECT count(customer_id) FROM CUSTOMER WHERE address_id = :addressId", 
			  nativeQuery = true)
	public Page<Integer> findCustomersByAddressId(Pageable pageable , @Param("addressId") Integer addressId);
}
