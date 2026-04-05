# テーブル定義書 (Table Definitions)

SpringGame アプリケーションで使用される全データベーステーブルの定義です。

---

## 1. user テーブル（ユーザー情報）
ユーザーの基本情報と、ゲームの進行状況（セーブデータ）を管理します。

| 論理名 | 物理名 | 型 | 制約 | デフォルト値 | 備考 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ID | `id` | BIGINT | PK, AUTO_INCREMENT | - | ユーザーの一意な主キー |
| アカウントネーム | `username` | VARCHAR(255) | | - | ログイン用のユーザー名 |
| パスワードハッシュ | `password_hash` | VARCHAR(255) | | - | 暗号化されたパスワード |
| 現在のシナリオID | `current_scenario_id` | BIGINT | | - | 現在進行中のセーブデータ位置 |
| 作成日時 | `created_at` | DATETIME | | `CURRENT_TIMESTAMP` | レコード作成時刻 |
| 更新日時 | `updated_at` | DATETIME | | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | レコード最終更新時刻 |

---

## 2. action_log テーブル（アクション履歴）
ユーザーが各選択肢を選んだ履歴や、獲得したポイントの動きを記録します。

| 論理名 | 物理名 | 型 | 制約 | デフォルト値 | 備考 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ID | `id` | BIGINT | PK, AUTO_INCREMENT | - | ログの主キー |
| ユーザーID | `user_id` | BIGINT | | - | アクションを起こしたユーザー |
| 特性ID | `trait_id` | BIGINT | | - | このアクションで得られた特性ID |
| 獲得ポイント | `points` | INT | | - | 得られたポイント数 |
| 記録日時 | `logged_at` | DATETIME | | `CURRENT_TIMESTAMP` | ログ発生時刻 |

---

## 3. scenario テーブル（シナリオ・場面）
ゲームを構成する各場面・ステージのテキスト情報や判定を持ちます。

| 論理名 | 物理名 | 型 | 制約 | デフォルト値 | 備考 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ID | `id` | BIGINT | PK, AUTO_INCREMENT | - | シナリオの主キー |
| タイトル | `title` | VARCHAR(255) | | - | 場面の題名 |
| 本文 | `body` | TEXT | | - | 場面のテキスト（長文対応のためTEXT型） |
| 終了判定 | `is_ending` | BOOLEAN(BIT) | | - | `true` の場合、エンディング画面へと遷移する |
| 作成日時 | `created_at` | DATETIME | | `CURRENT_TIMESTAMP` | レコード作成時刻 |
| 更新日時 | `updated_at` | DATETIME | | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | レコード最終更新時刻 |

---

## 4. choice テーブル（選択肢）
あるシナリオで提示される「選択肢ボタン」の定義。次のシナリオへの遷移ルートを決定します。

| 論理名 | 物理名 | 型 | 制約 | デフォルト値 | 備考 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ID | `id` | BIGINT | PK, AUTO_INCREMENT | - | 選択肢の主キー |
| シナリオID | `scenario_id` | BIGINT | FK | - | 属しているシナリオのID（`scenario` と N:1 のリレーション） |
| 表示テキスト | `label` | VARCHAR(255) | | - | 画面に表示されるボタン名 |
| 次のシナリオID | `next_scenario_id` | BIGINT | | - | 選択後に遷移する先のシナリオID |
| 特性ID | `trait_id` | BIGINT | | - | 選択結果として影響を与える特性のID |
| 獲得ポイント | `trait_point` | INT | | - | 影響度合い（ポイント数） |
| 作成日時 | `created_at` | DATETIME | | `CURRENT_TIMESTAMP` | レコード作成時刻 |
| 更新日時 | `updated_at` | DATETIME | | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | レコード最終更新時刻 |

---

## 5. trait テーブル（性格特性）
「勇気」「優しさ」など、ゲーム進行で得られる特性のマスタデータです。

| 論理名 | 物理名 | 型 | 制約 | デフォルト値 | 備考 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ID | `id` | BIGINT | PK, AUTO_INCREMENT | - | 特性の主キー |
| 特性名 | `name` | VARCHAR(255) | | - | 例：「勇気」など |
| 説明 | `description` | VARCHAR(255) | | - | エンディング時などで表示される補足テキスト |
| 作成日時 | `created_at` | DATETIME | | `CURRENT_TIMESTAMP` | レコード作成時刻 |
| 更新日時 | `updated_at` | DATETIME | | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | レコード最終更新時刻 |
