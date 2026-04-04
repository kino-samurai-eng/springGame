package com.example.springgame.service;

import com.example.springgame.entity.ActionLog;
import com.example.springgame.entity.Choice;
import com.example.springgame.entity.Scenario;
import com.example.springgame.entity.Trait;
import com.example.springgame.entity.User;
import com.example.springgame.repository.ActionLogRepository;
import com.example.springgame.repository.ChoiceRepository;
import com.example.springgame.repository.ScenarioRepository;
import com.example.springgame.repository.TraitRepository;
import com.example.springgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service（サービス）層のクラスです。
 * ここでは「データベースの更新」や「複雑なポイント集計」など、『ゲームのルール（ビジネスロジック）』に
 * 関するすべての処理を集中して行います。
 * コントローラーは画面からの依頼をここに渡し、サービスが裏側の力仕事をすべて行います。
 */
@Service // Spring Bootに対して「このクラスはビジネスロジックを担当するサービスです」と教えるためのアノテーションです。
@RequiredArgsConstructor // Lombokのアノテーション。finalがついたRepository（データベース操作係）をすべて自動で読み込んで使えるようにしてくれます。
public class GameService {

    // ---------- ここから下は、データベースを操作するための「Repository」の宣言です ----------
    // 全て final をつけることで、起動時に自動でSpringが本物の操作クラスを作って割り当ててくれます。
    private final ScenarioRepository scenarioRepository; // シナリオ（場面）のテーブルを操作する係
    private final ChoiceRepository choiceRepository;     // 選択肢のテーブルを操作する係
    private final TraitRepository traitRepository;       // 特性（勇気など）のテーブルを操作する係
    private final ActionLogRepository actionLogRepository; // ポイント履歴のテーブルを操作する係
    private final UserRepository userRepository;         // プレイヤー（ユーザー）のテーブルを操作する係
    // --------------------------------------------------------------------------------------

    /**
     * エンディングの計算結果（一番高かった特性と、ポイント）をまとめて
     * コントローラーに渡すための「記録用のデータ入れ物（record）」です。
     * Java 14から導入された record を使うと、わざわざクラスを宣言せずとも一行でデータ格納用の型が作れて便利です。
     */
    public record EndingResult(Trait topTrait, Long totalPoint) {}

    /**
     * MVP版（今のテスト段階）のために、常に固定のID=1のユーザーを取得するメソッドです。（まだログイン機能がないため）
     * もしデータベースにID=1のユーザーが存在しなければ、新しく作って保存（Save）してから返します。
     * 
     * @Transactional アノテーションは、「このメソッドの中のデータベース操作は、ひとまとまり（トランザクション）として行うよ」
     * という宣言です。もし途中でエラーが起きたら、ここで行った保存などの変更をすべて「無かったこと（ロールバック）」にしてくれます。
     * データベースを書き換える（INSERT/UPDATE/DELETE）メソッドには原則つけるようにしましょう。
     */
    @Transactional
    public User getOrCreateFixedUser() {
        return userRepository.findById(1L).orElseGet(() -> {
            // DBに見つからなかった場合の処理
            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setCurrentScenarioId(1L); // 初回はシナリオ1からスタート
            return userRepository.save(newUser); // セーブ（DBにINSERT）して、その結果を返す
        });
    }

    /**
     * 指定されたシナリオID（現在位置）を使って、シナリオの情報を取り出すメソッドです。
     * @Transactional(readOnly = true) と書くことで、「このメソッドはデータベースを見る（検索・SELECT）だけだよ。
     * 書き換えは絶対にしないよ」という宣言になり、処理速度（パフォーマンス）が少し向上します。
     */
    @Transactional(readOnly = true)
    public Optional<Scenario> getScenario(Long scenarioId) {
        // findById はJPAがあらかじめ用意してくれている「主キー（ID）で1件検索する」便利機能です。
        return scenarioRepository.findById(scenarioId);
    }
    
    /**
     * エンディングの時に、ユーザーがこれまでどんな特性を選んでどれくらいポイントを稼いだかを推計し、
     * 一番高かった特性のデータを作って返す処理です。
     */
    @Transactional(readOnly = true)
    public EndingResult getEndingResult(Long userId) {
        // actionLogRepository に独自に定義した「SQL」を使って、一番高かったポイントの集計結果を取り出します。
        List<Object[]> topTraitData = actionLogRepository.findTopTraitByUserId(userId);
        
        // もしデータが入っていれば（何か選択肢を選んだ履歴があれば）
        if (!topTraitData.isEmpty() && topTraitData.get(0) != null) {
            Object[] result = topTraitData.get(0);
            // Javaの文法で、取り出した数字を「Long型」に変換しています。
            Long traitId = ((Number) result[0]).longValue();
            Long totalPoint = ((Number) result[1]).longValue();
            
            // 一番高かった特性のIDがわかったので、今度は traitRepository を使ってその特性の詳細（名前や説明文）を取得します。
            Trait trait = traitRepository.findById(traitId).orElse(null);
            
            // エンディング結果の入れ物（EndingResult）に詰めて、コントローラーに返却します。
            return new EndingResult(trait, totalPoint);
        }
        
        // 万が一、一切選択肢を選ばずに終わった（履歴ゼロ）場合は、空の結果を返します。
        return new EndingResult(null, 0L);
    }

    /**
     * コンティニュー（2周目プレイ）などを想定し、ユーザーの現在地だけを「最初の場所(1)」にリセットするメソッドです。
     * このメソッドでは ActionLog（過去のポイント履歴）は消さない設計にしているため、
     * 2周目・3周目と遊ぶほどポイントがどんどん加算され続ける「強くてニューゲーム」のような動きになります！
     * もちろんデータベースに書き込む（UPDATEする）ので @Transactional がついています。
     */
    @Transactional
    public void resetScenarioForNextPlay(User user) {
        user.setCurrentScenarioId(1L); // 次のプレイのために初期位置の1番にもどす
        userRepository.save(user); // データベースに変更をセーブする
    }

    /**
     * プレイヤーが「選択肢」を選んだときの具体的な裏側の処理です。
     * ポイントをActionLogテーブルに保存し、ユーザーの現在地を次に進んだ場所に更新します。
     * 色々なテーブルに書き込む複雑な処理なので @Transactional が大きな力を発揮します（もし途中で落ちたら全てセーフティに巻き戻ります）
     */
    @Transactional
    public void processChoice(User user, Long choiceId) {
        // 1. ユーザーが選んだ選択肢（Choice）の情報をDBから持ってきます。
        Optional<Choice> choiceOpt = choiceRepository.findById(choiceId);
        if (choiceOpt.isEmpty()) {
            return; // 存在しない選択肢を選ばれた場合は、何もしません（不正対策など）
        }

        Choice choice = choiceOpt.get(); // 選択肢情報を展開します

        // 2. 選択肢に「特性ポイント」が付与されているか確認し、あれば ActionLog（履歴テーブル）に新規登録します。
        Long traitId = choice.getTraitId();
        if (traitId != null) {
            ActionLog log = new ActionLog(); // 履歴レコードを新しくメモリ上に準備
            log.setUserId(user.getId()); // 「誰が？」
            log.setTraitId(traitId); // 「どの特性で？」
            log.setPoints(choice.getTraitPoint()); // 「何ポイント？」
            actionLogRepository.save(log); // 履歴テーブルに保存（INSERT）する命令！
        }

        // 3. ユーザーのセーブデータ（いまどこにいるか）を、「選択肢によって進む次のシナリオID」に書き換えます。
        user.setCurrentScenarioId(choice.getNextScenarioId());
        userRepository.save(user); // ユーザーテーブルの保存（UPDATE）する命令！
    }
}
