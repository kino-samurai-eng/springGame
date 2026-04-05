# SpringGame プロジェクト

## データベース設計とテーブル自動生成についての方針

本プロジェクトでは、開発初期段階におけるテーブル構成の頻繁な変更に柔軟に対応するため、現在は **JPAによるテーブル自動生成（`ddl-auto=create`）** を採用しています。

### 現在の設定（開発フェーズ）

`application.properties` にて以下の設定が有効になっています。
```properties
spring.jpa.hibernate.ddl-auto=create
```
起動のたびに既存テーブルが削除（DROP）され再構築（CREATE）されるため、Javaコードを書き換えるだけでDBが追従し、直後に `data.sql` でクリーンなテストデータが投入される快適な開発環境になっています。

**【データベースへの時間自動記録について】**
現在、`ActionLog` エンティティなどでDBに時刻記録を任せるため、以下のアノテーションを使用しています。
```java
@Column(name = "logged_at", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
private LocalDateTime loggedAt;
```
`ddl-auto=create` 環境下では、この `columnDefinition` があるおかげで、自動生成されるテーブルに無事デフォルト時刻が付与され、安全に動いています。

---

### 将来の移行計画（開発終盤・リリースフェーズ）

テーブル構成（スキーマ）が完全に固まった段階で、**JPAの自動生成機能をオフにし、手動のSQL管理（`schema.sql`）へと移行**します。

**【移行手順】**
1. `application.properties` から `spring.jpa.hibernate.ddl-auto=create` の行を**削除**する（または `none` を指定する）。
2. `src/main/resources/schema.sql` を手動で作成し、**存在するすべてのエンティティ**のテーブル定義を記述する。
3. Java側の `@Column` から `columnDefinition` パラメータを削除する（以後のテーブル作成は `schema.sql` が行うため、Java側に書く必要がなくなります）。

**【schema.sql作成時の注意点とサンプル】**

データの永続化（本番運用）を見据えた最終的な構成においては、起動のたびにデータが消えないように `CREATE TABLE IF NOT EXISTS` を使用します。

1. **初期構築時のみテーブルを作成するように `IF NOT EXISTS` をつける**
2. **Java側で `insertable = false` にしている変数（時間記録など）には、忘れず `DEFAULT CURRENT_TIMESTAMP` をつける**
3. **（対応済）データの重複エラーを防ぐため、`data.sql` は `INSERT IGNORE` を使用する**

```sql
-- ▼本番運用・データ永続化時の schema.sql サンプル

-- ▼注意点1：既存のデータを消さず、テーブルが無い時（初回）だけ作成する
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    -- ▼注意点2：Java側で insertable=false としているカラムは、ここで必ず DEFAULT を設定する事！
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- その他カラム...
);

CREATE TABLE IF NOT EXISTS action_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    trait_id BIGINT,
    points INT,
    logged_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- （Scenario, Choice, Traitテーブルも同様に作成する）
```

**✅ `data.sql` について（すでに対応済み）**
`IF NOT EXISTS` を使ってテーブルとデータを残し続ける（永続化する）仕様になった場合、「毎回の起動時に `data.sql` が何度も走り、同じテストデータを二重登録しようとしてエラーが起きる」という問題が発生します。
そのため、本プロジェクトではすでに `data.sql` 内の全INSERT文を **`INSERT IGNORE`** 構文に書き換えてあります。これによって、すでにデータが有る場合はスキップされ、安全にアプリが立ち上がる設計になっています。

---

### 補足：なぜ `ddl-auto=update` を使わないのか？

`update` 設定は「データも残るしテーブルも更新される」ように見えますが、以下の致命的なデメリットがあるため本プロジェクトでは不採用としています。

1. **ゴミデータの蓄積:** カラム名を変更したり削除したりしても、古いカラムがDB上に永遠に残り続けてしまう。
2. **`data.sql` との競合:** 前回起動時のデータが残るため、毎回 `data.sql` が実行されると（主キー重複などで）2回目の起動時にクラッシュしてしまう。
3. **型変更の失敗:** 文字列から数値への変更などを行った際、変換に失敗して起動エラーになるリスクが高い。
