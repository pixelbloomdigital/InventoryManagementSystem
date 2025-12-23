package com.pixelbloom.auth_server.repository;


import com.pixelbloom.auth_server.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface MyUserRepository  extends JpaRepository<MyUser, Long> {

   Optional<MyUser> findUserByUserName(String userName);
}
