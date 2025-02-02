package com.example.securityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.securityapp.domain.Item;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


}
