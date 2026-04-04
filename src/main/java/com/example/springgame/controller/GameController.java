package com.example.springgame.controller;

import com.example.springgame.entity.Scenario;
import com.example.springgame.entity.User;
import com.example.springgame.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller（コントローラー）層のクラスです。
 * 画面（ブラウザ）からのリクエストを受け取り、適切なサービス（GameService）を呼び出し、
 * 次にどの画面（HTML）を表示するかを決定する「司令塔」のような役割を持ちます。
 */
@Controller // Spring Bootに対して「このクラスはWEBリクエストを処理するコントローラーですよ」と教えるためのアノテーション（目印）です。
@RequiredArgsConstructor // Lombokというライブラリの機能で、finalがついた変数（GameServiceなど）を自動的に読み込んでくれる（DI：依存性の注入）便利な記述です。これによりコンストラクタを書く手間が省けます。
public class GameController {

    // ビジネスロジック（データベースの操作や、複雑な計算など）を担当するGameServiceを宣言します。
    // finalをつけることで、「このサービスは書き換えられず、必ず用意される」ことを保証します。
    private final GameService gameService;

    /**
     * @GetMapping("/") は、ブラウザで一番最初のURL（http://localhost:8080/ など）に
     * 「GETリクエスト（ページを見たいというお願い）」が来た時にこのメソッドが動くこと指定します。
     */
    @GetMapping("/")
    public String index() {
        // "redirect:/play" と返すことで、「/play というURLに自動的に移動してね」とブラウザに指示（リダイレクト）しています。
        return "redirect:/play";
    }

    /**
     * @GetMapping("/play") は、「/play というURL」にアクセスした際に動くメソッドです。
     * ここでゲームの画面（現在のシナリオ）を表示する準備をします。
     * 
     * @param model 画面（Thymeleafを用いたHTML）にデータを渡すための「箱」のような役割をする引数です。
     * @return 次に表示するHTMLテンプレートの名前（拡張子 .html は不要）を文字列で返します。
     */
    @GetMapping("/play")
    public String play(Model model) {
        // 1. 今回遊んでいるユーザーのデータ（今回は練習用なので固定ユーザー）を取得します。
        User user = gameService.getOrCreateFixedUser();
        
        // 2. そのユーザーが「今どこまで進んでいるか（現在のシナリオID）」を取得します。
        // もしまだ何も保存されていなければ（nullならば）、最初のシナリオである「1」を代入します。
        Long currentScenarioId = user.getCurrentScenarioId() != null ? user.getCurrentScenarioId() : 1L;
        
        // 3. GameServiceにお願いして、データベースからシナリオの情報を取ってきてもらいます。
        // Optional<Scenario> は、「もしかしたら値が入っていない（シナリオが見つからない）かもしれない」ことを安全に扱うための型です。
        Optional<Scenario> scenarioOpt = gameService.getScenario(currentScenarioId);
        
        // 4. 万が一、シナリオがデータベースに見つからなかった場合の安全対策（エラーにならずにトップページに戻す）
        if (scenarioOpt.isEmpty()) {
            return "redirect:/";
        }

        // 5. 無事にシナリオが見つかったので、中身を取り出します。
        Scenario scenario = scenarioOpt.get();

        // 6. もし取得したシナリオが「エンディング（シナリオの終わり）」だった場合の特別な処理です。
        if (scenario.isEnding()) {
            // エンディング用の集計処理（これまでどんなポイントを取ったか）をGameServiceにお願いします。
            GameService.EndingResult result = gameService.getEndingResult(user.getId());

            // model.addAttribute("キー名", 中身) を使うことで、画面（HTML側）にデータを渡すことができます。
            // 画面側では th:text="${キー名}" のようにして取り出します。
            model.addAttribute("scenario", scenario);          // 今回のシナリオの文章
            model.addAttribute("topTrait", result.topTrait()); // 一番ポイントが高かった特性（優しさ、勇気など）
            model.addAttribute("totalPoint", result.totalPoint()); // 獲得した合計ポイント
            
            // エンディングを見終わったので、次に遊ぶときのために「現在地を最初の場所(1)に戻す」処理を行います。
            gameService.resetScenarioForNextPlay(user);

            // "ending" と返すことで、 resources/templates/ending.html という画面ファイルを表示するよう指示します。
            return "ending";
        }

        // 7. エンディングではない（通常の冒険中）の場合
        // シナリオの情報（本文や、次に選べる選択肢のリスト）を画面に渡します。
        model.addAttribute("scenario", scenario);
        
        // "game" と返すことで、 resources/templates/game.html という画面ファイルを表示します。
        return "game";
    }

    /**
     * @PostMapping("/play/choose") は、画面から「フォーム送信（POSTリクエスト）」で
     * 選択肢が選ばれた時に動くメソッドです。ユーザーの「決断」を処理します。
     * 
     * @param choiceId @RequestParam が付いているため、HTML画面のフォーム（name="choiceId"）から送られてきた値を受け取ります。
     */
    @PostMapping("/play/choose")
    public String choose(@RequestParam Long choiceId) {
        // 1. 今回遊んでいるユーザー（今回は固定）を取得します。
        User user = gameService.getOrCreateFixedUser();
        
        // 2. 「ユーザーがこの選択肢（choiceId）を選びましたよ」という情報の処理（ポイント計算や次の場面への移動）を
        // すべてGameService（ビジネスロジック）にお願いします。コントローラーのコードがこれでとてもスッキリします！
        gameService.processChoice(user, choiceId);
        
        // 3. 処理が終わったら、次の場面の表示をお願いするために "/play" というURLへリダイレクト（再アクセス）させます。
        return "redirect:/play";
    }
}
