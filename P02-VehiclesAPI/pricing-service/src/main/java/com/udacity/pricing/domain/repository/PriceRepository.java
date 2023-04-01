package com.udacity.pricing.domain.repository;

import com.udacity.pricing.domain.price.Price;
import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<Price,Long> {

}
