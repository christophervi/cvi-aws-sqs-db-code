package com.vi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vi.model.SqsMessage;

@Repository
public interface SqsMessageRepository extends JpaRepository<SqsMessage, Long> {

}
