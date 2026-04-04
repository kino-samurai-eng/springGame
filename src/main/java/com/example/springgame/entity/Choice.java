package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * シナリオ内での「選択肢」を表すエンティティ（データベースのテーブルと対応するクラス）です。
 */
@Entity
@Data
public class Choice {

    // この選択肢一つ一つの固有番号（主キー）です。自動採番の1、2、3...が入ります。
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne これもデータベースの【リレーション（関係性）】を表す非常に大事な設定です！
     * 先ほどの Scenario に書いた @OneToMany の逆側です。
     * 「たくさんある選択肢（Many）は、特定の1つのシナリオ（One）に属しているよ」と宣言しています。
     * 
     * @JoinColumn(name = "scenario_id") は「どうやって紐づけるか」の指定です。
     * データベースの中に `scenario_id` という名前のカラム（列）を作り、そこに紐づけ先のシナリオのID番号（背番号）を入れて繋ぐんだよ、と教えています。
     * この変数があるおかげで、Javaのコード上で choice.getScenario() と書くだけで「親のシナリオデータ」をごっそり手に入れることができます！
     */
    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    // 「右に曲がる」「立ち向かう」といった、選択肢のボタンに表示されるテキストです。
    private String label;

    // 「この選択肢を選んだら、次はどのシナリオIDに進むか」という移動先の指定です。
    private Long nextScenarioId;

    // 「この選択肢を選んだら、プレイヤーのどの性格（特性ID）に影響を与えるか」の指定です。（例: 1＝勇気）
    private Long traitId;

    // 「その性格（特性）が、具体的に何点上がるか（ポイント）」です。
    private int traitPoint;
}
