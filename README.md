# SpringGame プロジェクト

このプロジェクトは、Spring Bootを使用して構築されたテキストアドベンチャーゲームです。
ユーザーが選択肢を選びながらシナリオを進め、その選択結果に基づいて特性（スコア）が変動し、マルチエンディングに到達する仕組みを備えています。

## ドキュメント一覧

開発の過程で学んだ知識、実装ノウハウ、およびデータベースの運用方針については、`docs/` フォルダ内の各ドキュメントに集約しています。

- 📖 **[学習ノート（エンティティ設計編）](docs/learning_notes.md)**
  - `@Entity`, `@Column`, リレーション (`@OneToMany`等) の基本的な使い方について。
- 📖 **[学習ノート（MVC & Springアーキテクチャ編）](docs/spring_mvc_notes.md)**
  - Controller, Service, Repositoryの役割分担と、`@Transactional` や `@Query` などのアノテーションについて。
- 🗄️ **[データベース運用方針](docs/database_management.md)**
  - 自動生成（`ddl-auto`）から `schema.sql` への移行プロセスと、初期データ投入の仕組み（`INSERT IGNORE` など）について。
- 📊 **[テーブル定義書](docs/table_definition.md)**
  - 本番運用している5つのテーブルのスキーマ詳細。
