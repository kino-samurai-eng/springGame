-- トレイト（性格特性）の初期データ
INSERT IGNORE INTO trait (name, description) VALUES ('勇気', '危険なことに挑戦する性格');
INSERT IGNORE INTO trait (name, description) VALUES ('慎重', '物事をよく考えてから動く性格');
INSERT IGNORE INTO trait (name, description) VALUES ('優しさ', '他人を思いやる性格');

-- シナリオの初期データ
INSERT IGNORE INTO scenario (id, title, body, is_ending) VALUES (1, '暗い森の入り口', 'あなたは暗く深い森の入り口に立っています。奥から獣の鳴き声が聞こえます。', false);
INSERT IGNORE INTO scenario (id, title, body, is_ending) VALUES (2, '森の奥深く', '森の奥に進むと、道が二手に分かれています。片方は開けた道、もう一方は茨に覆われた道です。', false);
INSERT IGNORE INTO scenario (id, title, body, is_ending) VALUES (3, '森の小屋', '森の中で小さな小屋を見つけました。中にはおばあさんがいて、スープを差し出してきました。', false);

-- エンディングシナリオ
INSERT IGNORE INTO scenario (id, title, body, is_ending) VALUES (4, 'エンディング', 'あなたの冒険はここで終わります……。', true);

-- シナリオ1の選択肢 (id=1, 暗い森の入り口)
INSERT IGNORE INTO choice (scenario_id, label, next_scenario_id, trait_id, trait_point) VALUES (1, '勇気を出して進む', 2, 1, 10);
INSERT IGNORE INTO choice (scenario_id, label, next_scenario_id, trait_id, trait_point) VALUES (1, '誰か来るまで待つ', 3, 2, 10);

-- シナリオ2の選択肢 (id=2, 森の奥深く)
INSERT IGNORE INTO choice (scenario_id, label, next_scenario_id, trait_id, trait_point) VALUES (2, '茨に覆われた道を進む', 4, 1, 15);
INSERT IGNORE INTO choice (scenario_id, label, next_scenario_id, trait_id, trait_point) VALUES (2, '開けた道を安全に進む', 4, 2, 15);

-- シナリオ3の選択肢 (id=3, 森の小屋)
INSERT IGNORE INTO choice (scenario_id, label, next_scenario_id, trait_id, trait_point) VALUES (3, 'ありがたくスープをいただく', 4, 3, 20);
INSERT IGNORE INTO choice (scenario_id, label, next_scenario_id, trait_id, trait_point) VALUES (3, '怪しいので断る', 4, 2, 10);
