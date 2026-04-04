package com.example.springgame.repository;

import com.example.springgame.entity.Trait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraitRepository extends JpaRepository<Trait, Long> {
}
