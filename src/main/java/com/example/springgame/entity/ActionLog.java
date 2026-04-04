package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * アクションログ（ユーザーの選択と獲得特性ポイント）の履歴
 * MVP段階では今回はセッション上で同等のデータを管理します。
 */
@Entity
@Data
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long traitId;

    private int points;

    private LocalDateTime loggedAt;

    @PrePersist
    public void prePersist() {
        this.loggedAt = LocalDateTime.now();
    }
}
