package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message,Long> {

     List<Message> findFirst5ByOrderByIdDesc();
     List<Message> findAllByUserId(long id);
    List<Message> findAllByMessage(String message);



}

