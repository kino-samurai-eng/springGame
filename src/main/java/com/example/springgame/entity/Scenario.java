package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * シナリオ（各場面）を表すエンティティ
 */
@Entity
@Data
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    private boolean isEnding;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private List<Choice> choices;
}
