package com.henrikacadej.urlshortener.repository;

import com.henrikacadej.urlshortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public boolean existsByEmail(String email);
}
