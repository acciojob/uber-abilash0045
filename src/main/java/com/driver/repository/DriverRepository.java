package com.driver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.driver.model.Driver;
@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer>{

    @Query(value = "SELECT * FROM drivers d ORDER BY customerId ASC",nativeQuery = true)
    List<Driver> findAllDriversById();

    @Query(value = "DELETE * FROM drivers d WHERE d.driverId == driverId",nativeQuery = true)
    void deleteDriver(int driverId);
}
