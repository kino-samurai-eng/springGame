package com.example.springgame.repository;

import com.example.springgame.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    @Query("SELECT a.traitId, SUM(a.points) as total FROM ActionLog a WHERE a.userId = :userId GROUP BY a.traitId ORDER BY total DESC LIMIT 1")
    List<Object[]> findTopTraitByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ActionLog a WHERE a.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
