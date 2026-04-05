# 開発学習ノート (Learning Notes)

このドキュメントは、Spring Boot および JPA を用いた開発において、コード上に記載していた「学習用・解説用」のコメントを外部化したものです。コードを綺麗に保ちながら学習効果を下げないための備忘録として活用してください。

---

## 1. エンティティの基本設定
エンティティはデータベースのテーブルとJavaプログラムを繋ぐ「設計図」です。

* **`@Entity`**: 「このクラスはデータベースのテーブルと対応している」というSpring（JPA）への宣言。
* **`@Table(name = "\`user\`")`**: データベース上のテーブル名を指定するアノテーション。`user`のようにSQLの予約語を使う場合、エラーを防ぐためにバッククォート（`` ` ``）で囲む必要があります。
* **`@Id` と `@GeneratedValue`**:
  * `@Id`：この変数がテーブルの主キー（絶対的な背番号）であることを示します。
  * `@GeneratedValue(strategy = GenerationType.IDENTITY)`：IDの番号をデータベース側で自動で連番（1, 2, 3...）にするための指示です。
* **`@Data`**: Lombokの機能で、裏側で `getter` / `setter` などを自動生成し、コードをスッキリさせます。

---

## 2. データベースのリレーション（関係性）について
JPAでテーブル同士を繋ぐための重要なアノテーションです。

### 1対多（One to Many） - 例：Scenario
* **`@OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)`**
  * 「1つのシナリオ（One）に対して、複数の選択肢（Many）が対応する」という親側の宣言です。
  * `mappedBy`：子（Choice）クラスにある `scenario` 変数によって紐づけを管理していることを示します。
  * `cascade`：親（Scenario）が保存/削除されたら、子（Choice）も連動して同じように操作する便利な設定です。

### 多対1（Many to One） - 例：Choice
* **`@ManyToOne` と `@JoinColumn(name = "scenario_id")`**
  * 親であるScenarioの `@OneToMany` の逆側（子側）の設定です。「たくさんある選択肢（Many）は１つのシナリオ（One）に属する」ことを宣言します。
  * `@JoinColumn` は、データベース上に実際に `scenario_id` という紐付け用のカラムを作るための指示です。

---

## 3. 作成日時・更新日時の自動保存について
データの保存時間を自動で記録するには、大きく2つのアプローチがあります。

### 過去の学習方針：ライフサイクルメソッド（`@PrePersist`）
以前は以下のような記述をしていました。
```java
@PrePersist
public void prePersist() {
    this.createdAt = LocalDateTime.now();
}
```
これは「データベースに初めて保存（Save）される【直前】に、自動で実行してJava側で時間をセットする」という機能です。

### 現在のスマートな方針：データベース委譲（`@Column` 定義）
現在はよりスマートな手法として、以下のように定義しています。
```java
@Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
private LocalDateTime createdAt;
```
これによるメリット：
1. `insertable=false, updatable=false` とすることで、Javaからの介入を完全に遮断。
2. `columnDefinition` により、DBのDDLに `DEFAULT CURRENT_TIMESTAMP` が付与される。
3. すべてをデータベースの標準機能に任せるため、`@PrePersist` メソッドを書く必要がなくコードが美しくなる。

---

## 4. 長文テキストの保存について
シナリオの本文など、長い文章を扱う場合のアノテーションです。
* **`@Column(columnDefinition = "TEXT")`**
  * `String`型は通常、DB側で `VARCHAR(255)` 等の短いテキストとして作られますが、この指定を入れることで「とても長い文章が入る型（TEXT型）」として生成させ、文字あふれエラーを防止します。
