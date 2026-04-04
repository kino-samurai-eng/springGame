package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 性格特性（Trait）を表すエンティティ
 */
@Entity
@Data
public class Trait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
}
