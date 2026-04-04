package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 性格特性（Trait）を表すエンティティです。
 * 勇気、優しさ、慎重さなど、ゲームをクリアした際のエンディングを分ける重要な要素になります。
 */
@Entity
@Data
public class Trait {

    // 特性の主キー（ID）です。
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 特性の名前（例：「勇気」）が文字列として記録されます。
    private String name;

    // 特性の説明（例：「危険を恐れずに立ち向かう性格です」）が記録されます。
    // エンディング画面でプレイヤーに表示してあげるための情報です。
    private String description;
}
