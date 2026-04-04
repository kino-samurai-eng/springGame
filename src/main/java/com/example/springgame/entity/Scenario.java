package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * シナリオ（各場面）を表すエンティティクラスです。
 * データベースの中の `scenario` テーブルと対応します。
 */
@Entity
@Data
public class Scenario {

    // この場面データの絶対的な主キー（背番号）です
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // シナリオの「タイトル（例：暗い森の入り口）」を保存するカラムです
    private String title;

    /**
     * @Column(columnDefinition = "TEXT") という指定は重要です。
     * String型は通常、データベース側では VARCHAR（最大255文字程度の短いテキスト）として作られがちです。
     * シナリオの本文のように「長い文章」を入れたい場合は、このように "TEXT型"（とても長い文章が入る型）に
     * 指定しておかないと、文字を保存する時にあふれてエラーになってしまうことがあります。
     */
    @Column(columnDefinition = "TEXT")
    private String body;

    // 「この場面にたどり着いたらゲームクリア（エンディング画面）だよ」という判定をするフラグ（目印）です。
    // trueならエンディング、falseなら通常の冒険中となります。
    private boolean isEnding;

    /**
     * @OneToMany はとても重要なデータベースの【リレーション（関係性）】を表すアノテーションです。
     * 「1つのシナリオ（One）に対して、複数の選択肢（Many）が対応（紐づいて）いますよ」と教えてあげています。
     * 
     * - mappedBy = "scenario" : Choice（選択肢）クラスの中にある `scenario` という変数によって紐づけられていますよ、という意味です。
     * - cascade = CascadeType.ALL : 親（シナリオ）が操作されたら、子（選択肢）も連動して同じように操作（保存や削除）してねという親切な設定です。
     * 
     * List<Choice>（リスト）として持っているのは、選択肢が2個や3個など複数あるためです！
     */
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private List<Choice> choices;
}
