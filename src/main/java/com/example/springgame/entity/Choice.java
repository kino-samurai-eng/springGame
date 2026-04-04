package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * シナリオ内での選択肢を表すエンティティ
 */
@Entity
@Data
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    private String label;

    private Long nextScenarioId;

    private Long traitId;

    private int traitPoint;
}
