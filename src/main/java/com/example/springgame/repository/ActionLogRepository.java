package com.example.springgame.repository;

import com.example.springgame.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * データベースへの命令文（SQL）を人間が手書きしなくて済むように助けてくれる
 * 「Repository（リポジトリ）」と呼ばれるインターフェース（設計図）です。
 * 
 * JpaRepository を継承する（extends JpaRepository）だけで、Spring Bootが裏側で自動的に
 * 「全ての履歴を取ってくる」「IDで1件だけ検索する」「新しく保存する（save）」「消す（delete）」
 * という主要なデータベース操作機能一式を作り上げてくれます！
 */
@Repository // Spring Bootに対して「これはデータベースとやり取りする係だよ」と教えるアノテーションです。
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    /**
     * @Query アノテーションは、Spring Bootが自動で作ってくれないような
     * 「複雑な命令（今回は合計ポイントを計算してランキング1位を取得する処理）」を
     * ピンポイントで自分でSQL（より正確にはJPQLというJava用のSQL）として書くことができる機能です。
     * 
     * コロン（:）がついた ":userId" の部分は、下の @Param("userId") で受け取った
     * メソッドの引数（ユーザーID）が自動的にすっぽり当てはまる仕組みになっています。
     */
    @Query("SELECT a.traitId, SUM(a.points) as total FROM ActionLog a WHERE a.userId = :userId GROUP BY a.traitId ORDER BY total DESC LIMIT 1")
    List<Object[]> findTopTraitByUserId(@Param("userId") Long userId);

    /**
     * 注意：SELECT（検索）以外の、「データの削除（DELETE）」や「書き換え（UPDATE）」を
     * @Query を使って自分で書く場合には、この @Modifying というアノテーションが【必ず】セットで必要になります！
     * これがないとSpring Bootは「えっ、勝手にデータ消そうとしてる！？検索用メソッドじゃないの！？」と驚いてエラーで止まってしまいます。
     * 
     * また、データを消す・書き換える場合は @Transactional（トランザクション：途中で失敗したら無かったことにする）も
     * つけておくのが安全なルールです。
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ActionLog a WHERE a.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
