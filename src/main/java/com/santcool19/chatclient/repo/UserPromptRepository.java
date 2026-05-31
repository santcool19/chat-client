package com.santcool19.chatclient.repo;

import com.santcool19.chatclient.model.UserPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPromptRepository extends JpaRepository<UserPrompt, Long> {
}

