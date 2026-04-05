# データベース設計とテーブル自動生成についての方針

本プロジェクトでは、開発初期段階におけるテーブル構成の頻繁な変更に柔軟に対応するため、**JPAによるテーブル自動生成（`ddl-auto=create`）** を経験し、その後 **本番運用を想定した `schema.sql` (IF NOT EXISTS)** に移行する戦略をとっています。

## 開発フェーズの設定（参考情報）
以前は `application.properties` にて `spring.jpa.hibernate.ddl-auto=create` を指定していました。起動のたびに既存テーブルが削除（DROP）され再構築（CREATE）されるため、Javaコードを書き換えるだけでDBが追従する仕組みでしたが、現在はリリースに向けて設定を解除しています。

## 将来の移行・本番運用フェーズの設定（現在採用中）
テーブル構成が固まったため、完全手動のSQL管理（`schema.sql`）へと移行しています。

### schema.sql作成時の注意点と運用
「データ永続化」において、起動のたびにデータが消えないよう `CREATE TABLE IF NOT EXISTS` を使用しています。

1. **初期構築時のみテーブルを作成するように `IF NOT EXISTS` をつける**
2. **Java側で `insertable = false` にしている時刻変数には、`DEFAULT CURRENT_TIMESTAMP` をつける**
3. **（対応済）データの重複エラーを防ぐため、`data.sql` は `INSERT IGNORE` で統一する**

```sql
-- ▼既存のデータを消さず、テーブルが無い時（初回）だけ作成する
CREATE TABLE IF NOT EXISTS action_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    -- ...
    -- ▼Java側で insertable=false としているカラムはDEFAULT設定が必須！
    logged_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### ✅ data.sql における INSERT IGNORE の重要性
`IF NOT EXISTS` でデータが永続化されると、「毎回の起動時に `data.sql` のテストデータが二重登録され、主キー重複エラー等でアプリが落ちる」問題が発生します。
本プロジェクトでは `data.sql` 内の全INSERT文を **`INSERT IGNORE`** 構文に書き換えることで、すでにデータが有る場合はスキップされ安全に起動するよう対策済みです。
