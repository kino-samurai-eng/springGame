package com.example.springgame.repository;

import com.example.springgame.entity.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Scenario（場面）テーブルにアクセスするためのRepositoryです。
 * 中身が空っぽに見えますが、 extends JpaRepository<扱う対象のクラス, 主キー(ID)の型> 
 * と書くだけで、シナリオの「保存」「更新」「削除」「全件取得」などがすべて出来るようになっています。
 * 
 * 自分で特別なSQL（@Queryなど）を書く必要がない場合は、これだけで十分機能します。とても便利ですね！
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}
