package com.cheemcheem.projects.energyusage.repository;

import com.cheemcheem.projects.energyusage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
