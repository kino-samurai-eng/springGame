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
* **`@Transactional`**: データベースの書き換え（INSERT/UPDATE等）を行う際、ひとまとまりの処理として扱い、途中でエラーが起きた場合にロールバック（無かったことにする）ための重要な宣言。（`readOnly=true` を付けると検索専用となり高速化・最適化される）。
* **`record` の活用**: Java14以降の文法。`public record EndingResult(...) {}` のようにクラスをデータ運搬用として1行で定義できる。

## 3. Repository（リポジトリ）層について
`ActionLogRepository.java`, `ScenarioRepository.java` 等
データベースへの命令（SQL）を自動生成、または手動定義する層。
* **`@Repository` & `JpaRepository`**: `extends JpaRepository` とするだけで、基本的なCRUD（保存・更新・削除・全件取得・ID検索）のメソッドが自動で構築される。
* **`@Query`**: 複雑な集計やカスタムSQL（JPQL）を自身で定義して実行したい場合に使用する。
* **`@Param`**: カスタムクエリ内の`:変数名` に引数をバインド（代入）する。
* **`@Modifying`**: `@Query` を使って、検索（SELECT）ではなく「更新」や「削除」を行う場合、必ずセットで指定しなければならないアノテーション。これがないと実行時に例外が発生する。

## 4. DI（依存性の注入）について
* **`@RequiredArgsConstructor` と `final`**: Lombokの機能。ControllerやServiceに記述し、各フィールドに `final` を付けることで、Spring起動時に必要なクラス（ServiceやRepositoryのインスタンス）が自動的にセット（注入）される仕組み。大量のコンストラクタを手書きする手間を省く。
