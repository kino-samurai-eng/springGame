package com.example.springgame.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 【Entity（エンティティ）とは？】
 * データベースのテーブル（表）と、Javaのプログラムを繋ぐ「架空の設計図」です。
 * これを書くことで、Spring Bootが自動的にデータベース上のテーブルをイメージして連携してくれます。
 */
@Entity // 「このクラスはデータベースのテーブルと対応していますよ」というSpring（JPA）に対する宣言です。
@Table(name = "`user`") // データベース上のテーブルの名前を "user" に指定するためのものです。※ user という単語はSQLの予約語（特別な言葉）なので、バッククォート（`）で囲んでエラーになるのを防いでいます。
@Data // Lombokという便利ツールの機能です。裏側で getter（データを取り出すメソッド）や setter（データを入れるメソッド）を自動で作ってくれるため、コードがスッキリします。
public class User {

    /**
     * @Id は、「この変数がこのテーブルの主キー（重複しない絶対的な背番号）だよ」という宣言です。
     * @GeneratedValue(strategy = GenerationType.IDENTITY) は、「この背番号はデータベース側で自動で連番に増やしてね（1, 2, 3...）」という指示です。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 今回はテスト用ですが、ユーザーがログインするための「ID名（アカウント名）」を入れるカラムです。
    // 特に追加の設定（@Columnなど）を書かない場合、変数名そのまま「username」という名前でテーブルのカラムが作られます。
    private String username;

    // ユーザーのパスワードです（データベースに保存されるときは暗号化（ハッシュ化）されるべきものです）
    private String passwordHash;

    // 「今どこまでゲームが進んでいるか」というセーブデータを記憶しておく変数です。
    // シナリオテーブルのID番号が入ります。
    private Long currentScenarioId;

    // このユーザーがいつアカウントを作ったか（レコードが作られたか）の時刻を保存します。
    private LocalDateTime createdAt;

    /**
     * @PrePersist という難しそうなアノテーションは、
     * 「このデータが初めてデータベースに保存（Save）される【直前】に、自動でこのメソッドの中身を実行してね！」
     * という便利な機能（ライフサイクルメソッド）です。
     * 
     * コビトさんが裏でこっそり、保存の瞬間に今の現在時刻を `createdAt` 変数にセットしてくれるイメージです。
     * いちいち自分で時刻をセットするコードを書かなくて良くなります！
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
