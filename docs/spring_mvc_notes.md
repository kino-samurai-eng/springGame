# Spring MVC & 実装ノウハウ 学習ノート

本プロジェクトのコントローラー、サービス、リポジトリ層で使われている重要な概念とアノテーションについての解説です。

## 1. Controller（コントローラー）層について
`GameController.java`
画面（ブラウザ）からのリクエストを受け付け、サービスを呼び出して次に表示する画面を決定する司令塔。
* **`@Controller`**: このクラスがWEBリクエストを処理するコントローラーであることをSpringに指定。
* **`@GetMapping("...") / @PostMapping("...")`**: URLへのアクセス（GET/POST）とメソッドを紐付ける。
* **`Model model`**: 画面（Thymeleaf HTML）に変数を渡すための箱（`model.addAttribute`で値をセットする）。
* **リダイレクト**: `return "redirect:/path"` で、指定したURLへ自動的に遷移（リダイレクト）させる。

## 2. Service（サービス）層について
`GameService.java`
データベースの更新やポイント集計など、ゲームのビジネスロジックを集中管理する場所。
* **`@Service`**: ビジネスロジックを担当するサービスであることを指定。
* **`@Transactional`**: データベースの実データを書き換える際（追加・更新・削除）に必須となる宣言。処理をひとまとまりとして扱い、途中でエラーが起きた場合はすべてロールバック（無かったことに）する。
  * **※補足:** データの「検索（SELECT）」だけを行う処理には本来不要ですが、`@Transactional(readOnly=true)` と明記しておくことで「この処理は検索だけだよ」と保証され、動作が最適化（高速化）するメリットがあります。
* **`record` の活用**: Java14以降の文法。`public record EndingResult(...) {}` のようにクラスをデータ運搬用として1行で定義できる。

## 3. Repository（リポジトリ）層について
`ActionLogRepository.java`, `ScenarioRepository.java` 等
データベースへの命令（SQL）を自動生成、または手動定義する層。
* **`@Repository` & `JpaRepository`**: `extends JpaRepository` とするだけで、基本的なCRUD（保存・更新・削除・全件取得・ID検索）のメソッドが自動で構築される。
* **`@Query`**: 複雑な集計やカスタムSQL（JPQL）を自身で定義して実行したい場合に使用する。検索（SELECT）の場合はこれ単体でOK。
* **`@Param`**: カスタムクエリ内の`:変数名` に引数をバインド（代入）する。
* **`@Modifying`**: `@Query` を使って、検索ではなく**「追加・更新・削除」**の命令文を自作した場合に、必ずセットで必要になるアノテーション。これがないとSpringが「検索処理だと思い込んでいるのに中身が更新文」となりパニック（実行時エラー）を起こします。

## 4. DI（依存性の注入）について
* **`@RequiredArgsConstructor` と `final`**: Lombokの機能。ControllerやServiceに記述し、各フィールドに `final` を付けることで、Spring起動時に必要なクラス（ServiceやRepositoryのインスタンス）が自動的にセット（注入）される仕組み。大量のコンストラクタを手書きする手間を省く。
