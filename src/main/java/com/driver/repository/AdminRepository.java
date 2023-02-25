package com.driver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.driver.model.Admin;
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>{
    @Query(value = "DELETE * FROM admins a WHERE a.adminId == adminId",nativeQuery = true)
    void deleteAdmin(int adminId);
}
