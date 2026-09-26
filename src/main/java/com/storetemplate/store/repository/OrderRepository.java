package com.storetemplate.store.repository;

import com.storetemplate.store.model.AppUser;
import com.storetemplate.store.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUserOrderByCreatedAtDesc(AppUser user);

    List<CustomerOrder> findAllByOrderByCreatedAtDesc();
}
