package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * アクションログ（ユーザーの選択と、それによって獲得した特性ポイントの履歴）を
 * 裏側でひっそり記録し続けるためのエンティティ（テーブル）です。
 */
@Entity
@Data
public class ActionLog {

    // ログ1行1行に対する自動採番の主キーです。
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 「誰が選んだか」を記録するユーザーIDです
    private Long userId;

    // 「上がったのは何の特性ポイントか」を記録する特性IDです
    private Long traitId;

    // 「具体的に何ポイントもらったのか」を記録します
    private int points;

    // 「いつ（何時何分何秒に）このログが発生したか」タイムスタンプ（時間記録）の役目を与えます。
    private LocalDateTime loggedAt;

    /**
     * @PrePersist ライフサイクルメソッドです。
     * この ActionLog がデータベースに新しく保存された（INSERTされた）瞬間に、
     * Javaが自動的に `LocalDateTime.now()` を呼び出して、保存した現在時刻をこの `loggedAt` 変数に格納してくれます。
     * この機能のおかげで、保存前に手動で時間をセットし忘れて時間が空っぽになっちゃう！というエラー（事故）を防げます。
     */
    @PrePersist
    public void prePersist() {
        this.loggedAt = LocalDateTime.now();
    }
}
