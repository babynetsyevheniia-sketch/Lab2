package com.example.smartlibrary.repository;

import com.example.smartlibrary.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    boolean existsByEmail(String email);
}