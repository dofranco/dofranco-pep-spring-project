package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{

    @Modifying
    @Query("UPDATE Message m SET m.messageText = :messageText WHERE m.messageId = :messageId")
    void updateMessageTextById(@Param("messageId") Integer messageId, @Param("messageText") String messageText);

    @Query("SELECT m FROM Message m WHERE m.postedBy = :accountId")
    public List<Message> getMessagesByAccountId(@Param("accountId") Integer accountId);
}
