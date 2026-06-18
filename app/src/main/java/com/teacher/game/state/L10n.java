package com.teacher.game.state;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Lightweight localization system.
 * Supports: zh (简体中文), en (English), ja (日本語), fr (Français).
 */
public class L10n {

	public static final String ZH = "zh";
	public static final String EN = "en";
	public static final String JA = "ja";
	public static final String FR = "fr";

	private static final String PREF_KEY = "game_language";
	private static String sLanguage = ZH;

	private static final HashMap<String, String> sMap = new HashMap<>();

	// [key, zh, en, ja, fr]
	private static final String[][] ALL = {

		// ==================== MenuState ====================
		{"menu_title",          "深海大作战", "Deep Sea Battle", "深海バトル", "Bataille des Profondeurs"},
		{"menu_subtitle",       "选择模式开始挑战", "Pick a mode to play", "モードを選んで挑戦", "Choisissez un mode"},
		{"menu_level_mode",     "关卡模式", "Level Mode", "ステージモード", "Mode Niveau"},
		{"menu_endless_mode",   "无尽模式", "Endless Mode", "エンドレスモード", "Mode Infini"},
		{"menu_credits",        "制作成员", "Credits", "スタッフ", "Cr\u00e9dits"},
		{"menu_settings",       "游戏设置", "Settings", "設定", "Param\u00e8tres"},
		{"menu_achievements",   "成就", "Achievements", "実績", "Succ\u00e8s"},
		{"menu_exit",           "退出游戏", "Exit", "終了", "Quitter"},
		{"menu_high_score",     "最高分：%d", "High Score: %d", "最高得点：%d", "Meilleur score : %d"},
		{"menu_control_mode",   "操控模式", "Control Mode", "操作モード", "Mode Contr\u00f4le"},
		{"menu_manual",         "手动", "Manual", "手動", "Manuel"},
		{"menu_auto",           "自动", "Auto", "自動", "Auto"},

		// ==================== HUD ====================
		{"hud_high_score",      "最高分：%d", "High Score: %d", "最高得点：%d", "Meilleur score : %d"},
		{"hud_life",            "生命 %d", "Life %d", "ライフ %d", "Vie %d"},
		{"hud_companion_count", "同伴 %d 只", "Companions: %d", "仲間 %d 匹", "Compagnons : %d"},
		{"hud_speed",           "加速", "Speed", "加速", "Vitesse"},
		{"hud_freeze",          "冰冻", "Freeze", "凍結", "Gel"},
		{"hud_shield",          "护盾", "Shield", "シールド", "Bouclier"},
		{"hud_lure",            "吸引", "Lure", "誘引", "Leurre"},
		{"hud_combo",           "连击 x%d", "Combo x%d", "コンボ x%d", "Combo x%d"},
		{"hud_debug_btn",       "调", "DBG", "デバッグ", "DBG"},

		// ==================== PlayState (debug) ====================
		{"debug_title",         "调试面板", "Debug Panel", "デバッグパネル", "Panneau Debug"},
		{"debug_speed",         "游戏速度", "Game Speed", "ゲーム速度", "Vitesse du jeu"},
		{"debug_spawn_fish",    "生成1条基础鱼", "Spawn 1 Fish", "魚を1匹生成", "G\u00e9n\u00e9rer 1 poisson"},
		{"debug_spawn_powerup", "生成一个泡泡", "Spawn Power-Up", "アイテムを生成", "G\u00e9n\u00e9rer un item"},
		{"debug_spawn_companion", "生成一个同伴", "Spawn Companion", "仲間を生成", "G\u00e9n\u00e9rer un compagnon"},
		{"debug_close",         "关闭", "Close", "閉じる", "Fermer"},

		// ==================== ModeRules ====================
		{"result_restart",      "重新开始", "Restart", "最初から", "Recommencer"},
		{"result_menu",         "返回菜单", "Menu", "メニューに戻る", "Menu"},
		{"result_next",         "下一关", "Next", "次のステージ", "Suivant"},
		{"result_retry",        "重试本关", "Retry", "リトライ", "R\u00e9essayer"},

		{"result_title_endless_over", "无尽结束", "Endless Over", "エンドレス終了", "Fin de l'Infini"},
		{"result_title_clear",  "本关完成", "Level Clear", "ステージクリア", "Niveau termin\u00e9"},
		{"result_title_all_clear", "全部通关", "All Clear", "全ステージクリア", "Tout termin\u00e9"},
		{"result_title_fail",   "挑战失败", "Failed", "失敗", "\u00c9chec"},

		{"result_desc_score",   "本次得分 %d，再来挑战更高分", "Score %d! Try again for higher!", "得点 %d！もっと高得点に挑戦！", "Score %d ! Essayez encore !"},
		{"result_desc_next",    "准备进入第 %d 关", "Entering Level %d", "第%dステージに進みます", "Niveau %d suivant"},
		{"result_desc_all_clear", "恭喜完成全部关卡挑战", "All levels cleared! Well done!", "全ステージクリアおめでとう！", "Tous les niveaux termin\u00e9s !"},
		{"result_desc_retry",   "再试一次，看看能不能拿到更高分", "Try again for a better score!", "もう一度挑戦！", "R\u00e9essayez pour un meilleur score !"},

		{"label_endless",       "无尽模式", "Endless", "エンドレス", "Infini"},
		{"label_level_n",       "第%d关", "Level %d", "第%dステージ", "Niveau %d"},
		{"label_score",         "分数 %d", "Score %d", "得点 %d", "Score %d"},
		{"label_score_target",  "分数 %d / %d", "Score %d / %d", "得点 %d / %d", "Score %d / %d"},

		// ==================== RoundTextFormatter ====================
		{"stat_goal_achieved",  "  达成目标！", "  Goal achieved!", "  目標達成！", "  Objectif atteint !"},
		{"stat_new_record",     "  新纪录！", "  New record!", "  新記録！", "  Nouveau record !"},
		{"stat_score",          "得　分    %d  %s", "Score     %d  %s", "得点      %d  %s", "Score     %d  %s"},
		{"stat_fish_eaten",     "吃掉鱼  %d 条", "Fish Eaten %d", "魚を食べた %d 匹", "Poissons mang\u00e9s %d"},
		{"stat_combo_peak",     "最高连击 x%d", "Peak Combo x%d", "最大コンボ x%d", "Combo max x%d"},
		{"stat_powerups",       "收集道具 %d 个", "Items %d", "アイテム %d 個", "Objets %d"},
		{"stat_companion_assists", "同伴助攻 %d 次", "Companion Kills %d", "仲間アシスト %d 回", "Aides du compagnon %d"},
		{"stat_survival",       "存活时间 %s", "Survival %s", "生存時間 %s", "Survie %s"},
		{"stat_time_limit",     "时间限制 %d 秒", "Time Limit %d s", "制限時間 %d 秒", "Limite de temps %d s"},
		{"time_format_min_sec", "%d分%d秒", "%dm %ds", "%d分%d秒", "%d m %d s"},
		{"time_format_sec",     "%d秒", "%ds", "%d秒", "%d s"},

		// ==================== LevelSelectState ====================
		{"level_title",         "选择关卡", "Select Level", "ステージ選択", "Choisir le niveau"},
		{"level_info",          "当前已扩展为 100 关，难度会持续提升", "100 levels with rising difficulty", "100ステージ、難易度上昇", "100 niveaux, difficult\u00e9 croissante"},
		{"level_page",          "第 %d / %d 页", "Page %d / %d", "%d / %d ページ", "Page %d / %d"},
		{"level_prev_page",     "上一页", "Prev", "前へ", "Pr\u00e9c"},
		{"level_next_page",     "下一页", "Next", "次へ", "Suiv"},
		{"level_back_menu",     "返回主菜单", "Back to Menu", "メニューに戻る", "Retour au menu"},
		{"level_locked",        "\uD83D\uDD12 第 %d 关", "\uD83D\uDD12 Level %d", "\uD83D\uDD12 第%dステージ", "\uD83D\uDD12 Niveau %d"},
		{"level_number",        "第 %d 关", "Level %d", "第%dステージ", "Niveau %d"},
		{"level_target_score",  "目标分数 %d", "Target %d", "目標得点 %d", "Objectif %d"},
		{"level_enemy_count",   "敌鱼 %d 条", "Enemies %d", "敵魚 %d 匹", "Ennemis %d"},

		// ==================== OverlayRenderer (pause) ====================
		{"pause_title",         "游戏已暂停", "Game Paused", "ゲーム一時停止", "Jeu en pause"},
		{"pause_desc",          "可以继续挑战，也可以重新开始", "Resume or restart", "続けるか最初から", "Reprendre ou recommencer"},
		{"pause_resume",        "继续游戏", "Resume", "続ける", "Reprendre"},
		{"pause_restart",       "重新开始", "Restart", "最初から", "Recommencer"},
		{"pause_menu",          "返回菜单", "Menu", "メニューに戻る", "Menu"},

		// ==================== SettingsState ====================
		{"settings_title",      "游戏设置", "Settings", "設定", "Param\u00e8tres"},
		{"settings_sound",      "音效", "Sound", "効果音", "Son"},
		{"settings_music",      "背景音乐", "Music", "BGM", "Musique"},
		{"settings_language",   "语言", "Language", "言語", "Langue"},
		{"settings_reset_score", "重置最高分", "Reset High Score", "最高得点をリセット", "R\u00e9initialiser le score"},
		{"settings_reset_btn",  "重置", "Reset", "リセット", "R\u00e9initialiser"},
		{"settings_reset_done", "已重置", "Reset!", "リセット完了", "Fait !"},
		{"settings_back",       "返回主菜单", "Back to Menu", "メニューに戻る", "Retour au menu"},
		{"toggle_on",           "开", "ON", "ON", "ON"},
		{"toggle_off",          "关", "OFF", "OFF", "OFF"},
		{"lang_zh",             "简体中文", "简体中文", "简体中文", "简体中文"},
		{"lang_en",             "English", "English", "English", "English"},
		{"lang_ja",             "日本語", "日本語", "日本語", "日本語"},
		{"lang_fr",             "Fran\u00e7ais", "Fran\u00e7ais", "Fran\u00e7ais", "Fran\u00e7ais"},

		// ==================== HelpState ====================
		{"help_credits_title",  "制作成员", "Credits", "スタッフ", "Cr\u00e9dits"},
		{"help_goal_title",     "游戏目标", "Objective", "ゲームの目的", "Objectif"},
		{"help_control_title",  "操作方式", "Controls", "操作方法", "Contr\u00f4les"},
		{"help_item_title",     "道具说明", "Items", "アイテム", "Objets"},
		{"help_companion_title","同伴系统", "Companion", "仲間システム", "Compagnon"},
		{"help_combo_title",    "连击系统", "Combo", "コンボシステム", "Combo"},
		{"help_mode_title",     "游戏模式", "Game Modes", "ゲームモード", "Modes de jeu"},

		{"credits_yang",        "杨世杰", "Yang Shijie", "楊世傑", "Yang Shijie"},
		{"credits_zhou",        "周欣琦", "Zhou Xinqi", "周欣琦", "Zhou Xinqi"},
		{"credits_wu",          "吴凯东", "Wu Kaidong", "吳凱東", "Wu Kaidong"},

		{"help_goal_1",         "\u2022 操控小鱼在水中游动，吃掉比自己小的鱼", "\u2022 Swim and eat smaller fish", "\u2022 泳いで小さい魚を食べよう", "\u2022 Nagez et mangez les petits poissons"},
		{"help_goal_2",         "\u2022 躲避比自己大的鱼，避免被吃掉", "\u2022 Dodge bigger fish", "\u2022 大きい魚を避けよう", "\u2022 \u00c9vitez les gros poissons"},
		{"help_goal_3",         "\u2022 达到目标分数即可通关", "\u2022 Reach target score to win", "\u2022 目標得点でクリア", "\u2022 Atteignez le score cible"},
		{"help_goal_4",         "\u2022 生命耗尽则挑战失败", "\u2022 Game over when lives run out", "\u2022 ライフ0でゲームオーバー", "\u2022 Game over si plus de vies"},

		{"help_control_1",      "\u2022 触摸屏幕任意位置出现虚拟摇杆", "\u2022 Touch anywhere for joystick", "\u2022 画面をタッチでジョイスティック表示", "\u2022 Touchez pour afficher le joystick"},
		{"help_control_2",      "\u2022 拖动控制小鱼游动方向", "\u2022 Drag to control direction", "\u2022 ドラッグで方向操作", "\u2022 Glissez pour diriger"},
		{"help_control_3",      "\u2022 点击右上角暂停按钮暂停游戏", "\u2022 Tap pause button to pause", "\u2022 右上の一時停止ボタンで停止", "\u2022 Appuyez sur pause en haut \u00e0 droite"},
		{"help_control_4",      "\u2022 可在主菜单切换\u300C手动\u300D/\u300C自动\u300D模式", "\u2022 Toggle Manual/Auto in menu", "\u2022 メニューで手動/自動切替", "\u2022 Bascule Manuel/Auto dans le menu"},

		{"help_item_1",         "\u2022 加速 \u2014 短时间内提高游泳速度", "\u2022 Speed \u2014 temporary speed boost", "\u2022 加速 \u2014 短時間スピードUP", "\u2022 Vitesse \u2014 boost temporaire"},
		{"help_item_2",         "\u2022 护盾 \u2014 抵挡一次大鱼攻击", "\u2022 Shield \u2014 block one attack", "\u2022 シールド \u2014 攻撃を1回防ぐ", "\u2022 Bouclier \u2014 bloque une attaque"},
		{"help_item_3",         "\u2022 冰冻 \u2014 冻结所有敌鱼，动弹不得", "\u2022 Freeze \u2014 freeze all enemies", "\u2022 凍結 \u2014 敵を凍らせる", "\u2022 Gel \u2014 g\u00e8le tous les ennemis"},
		{"help_item_4",         "\u2022 炸弹 \u2014 消灭屏幕上所有大鱼", "\u2022 Bomb \u2014 eliminate all big fish", "\u2022 爆弾 \u2014 大魚を全滅", "\u2022 Bombe \u2014 \u00e9limine tous les gros poissons"},
		{"help_item_5",         "\u2022 吸引 \u2014 将周围小鱼吸引到身边", "\u2022 Lure \u2014 attract nearby fish", "\u2022 誘引 \u2014 小魚を引き寄せる", "\u2022 Leurre \u2014 attire les petits poissons"},

		{"help_companion_1",    "\u2022 每吃掉一条鱼积攒同伴能量", "\u2022 Eating fish charges companion bar", "\u2022 魚を食べて仲間ゲージをためる", "\u2022 Manger charge la jauge du compagnon"},
		{"help_companion_2",    "\u2022 能量满后召唤同伴鱼跟随", "\u2022 Full bar summons a companion", "\u2022 ゲージMAXで仲間召喚", "\u2022 Jauge pleine invoque un compagnon"},
		{"help_companion_3",    "\u2022 同伴会自动攻击附近的小鱼", "\u2022 Companions auto-attack nearby fish", "\u2022 仲間が近くの小魚を自動攻撃", "\u2022 Les compagnons attaquent automatiquement"},
		{"help_companion_4",    "\u2022 同伴助攻越多，自身等级越高", "\u2022 More kills = higher level", "\u2022 アシスト数でレベルUP", "\u2022 Plus d'aides = niveau sup\u00e9rieur"},

		{"help_combo_1",        "\u2022 连续吃鱼触发连击", "\u2022 Eat consecutively for combo", "\u2022 連続で魚を食べてコンボ", "\u2022 Manger cons\u00e9cutivement pour un combo"},
		{"help_combo_2",        "\u2022 2秒内再次吃鱼维持连击", "\u2022 Eat within 2s to maintain combo", "\u2022 2秒以内に食べてコンボ維持", "\u2022 Manger en 2s pour maintenir le combo"},
		{"help_combo_3",        "\u2022 连击倍率：x1.0 \u2192 x1.5 \u2192 x2.0 \u2192 x2.5 \u2192 x3.0", "\u2022 Multiplier: x1.0 \u2192 x1.5 \u2192 x2.0 \u2192 x2.5 \u2192 x3.0", "\u2022 倍率：x1.0 \u2192 x1.5 \u2192 x2.0 \u2192 x2.5 \u2192 x3.0", "\u2022 Multiplicateur : x1.0 \u2192 x1.5 \u2192 x2.0 \u2192 x2.5 \u2192 x3.0"},
		{"help_combo_4",        "\u2022 被大鱼攻击后连击重置", "\u2022 Getting hit resets combo", "\u2022 大魚に攻撃されるとコンボリセット", "\u2022 Touch\u00e9 = combo r\u00e9initialis\u00e9"},

		{"help_mode_1",         "\u2022 关卡模式：依次挑战3个关卡", "\u2022 Level Mode: clear levels in order", "\u2022 ステージモード：順番にクリア", "\u2022 Mode Niveau : terminer les niveaux"},
		{"help_mode_2",         "\u2022 每关有不同目标分数和难度", "\u2022 Each level has different targets", "\u2022 各ステージに異なる目標", "\u2022 Chaque niveau a des objectifs diff\u00e9rents"},
		{"help_mode_3",         "\u2022 无尽模式：不限目标，挑战最高分", "\u2022 Endless: chase high score", "\u2022 エンドレスモード：限界に挑戦", "\u2022 Mode Infini : visez le meilleur score"},
		{"help_mode_4",         "\u2022 不同关卡背景各异，敌鱼也会变化", "\u2022 Varied backgrounds and enemies", "\u2022 ステージごとに背景と敵が変化", "\u2022 Environnements et ennemis vari\u00e9s"},

		{"help_prev",           "上一页", "Prev", "前へ", "Pr\u00e9c"},
		{"help_back",           "返回菜单", "Menu", "メニューに戻る", "Menu"},
		{"help_next",           "下一页", "Next", "次へ", "Suiv"},

		// ==================== AchievementState ====================
		{"ach_title",           "成就列表", "Achievements", "実績一覧", "Succ\u00e8s"},
		{"ach_back",            "返回主菜单", "Back to Menu", "メニューに戻る", "Retour au menu"},
		{"ach_unlocked",        "已达成", "Unlocked", "解除済み", "D\u00e9bloqu\u00e9"},

		{"ach_eat_fish",        "大鱼吃小鱼", "Big Fish", "大魚小魚", "Grand poisson"},
		{"ach_eat_fish_desc",   "累计吃掉 100 条鱼", "Eat 100 fish total", "合計100匹の魚を食べる", "Mangez 100 poissons au total"},
		{"ach_combo",           "连击大师", "Combo Master", "コンボマスター", "Ma\u00eetre du combo"},
		{"ach_combo_desc",      "单局连击达到 5", "Reach 5 combo in one round", "1ラウンドでコンボ5達成", "Atteignez combo 5 en une partie"},
		{"ach_item",            "道具收藏家", "Item Collector", "アイテムコレクター", "Collectionneur d'objets"},
		{"ach_item_desc",       "累计收集 30 个道具", "Collect 30 items total", "合計30個のアイテムを収集", "Collectez 30 objets au total"},
		{"ach_first_clear",     "初出茅庐", "First Clear", "初クリア", "Premi\u00e8re victoire"},
		{"ach_first_clear_desc","通关第 1 关", "Clear Level 1", "第1ステージをクリア", "Terminez le niveau 1"},
		{"ach_all_clear",       "关卡征服者", "Level Conqueror", "ステージ征服者", "Conqu\u00e9rant des niveaux"},
		{"ach_all_clear_desc",  "解锁全部 100 关", "Unlock all 100 levels", "全100ステージを解放", "D\u00e9bloquez les 100 niveaux"},
		{"ach_high_score",      "高分玩家", "High Scorer", "ハイスコアラー", "Grand Scoreur"},
		{"ach_high_score_desc", "关卡模式得分达到 5000", "Reach 5000 in Level Mode", "ステージモードで5000点達成", "Atteignez 5000 en mode niveau"},
		{"ach_endless",         "无尽挑战者", "Endless Challenger", "エンドレスチャレンジャー", "Challengeur Infini"},
		{"ach_endless_desc",    "无尽模式得分达到 20000", "Reach 20000 in Endless Mode", "エンドレスモードで20000点達成", "Atteignez 20000 en mode infini"},
		{"ach_fish_hunter",     "鱼群猎手", "Fish Hunter", "フィッシュハンター", "Chasseur de Poissons"},
		{"ach_fish_hunter_desc","累计吃掉 500 条鱼", "Eat 500 fish total", "合計500匹の魚を食べる", "Mangez 500 poissons au total"},
		{"ach_combo_expert",    "连击传说", "Combo Legend", "コンボレジェンド", "L\u00e9gende du Combo"},
		{"ach_combo_expert_desc","最高连击达到 10", "Reach 10 combo", "コンボ10達成", "Atteignez combo 10"},
		{"ach_item_tycoon",     "道具大亨", "Item Tycoon", "アイテムタイクーン", "Magnat des Objets"},
		{"ach_item_tycoon_desc","累计收集 100 个道具", "Collect 100 items total", "合計100個のアイテムを収集", "Collectez 100 objets au total"},

		// ==================== LoadState ====================
		{"loading",             "加载中...", "Loading...", "読み込み中...", "Chargement..."},
	};

	static {
		for (String[] row : ALL) {
			String k = row[0];
			sMap.put("zh:" + k, row[1]);
			sMap.put("en:" + k, row[2]);
			sMap.put("ja:" + k, row[3]);
			sMap.put("fr:" + k, row[4]);
		}
	}

	public static void init(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("fish_game_prefs", Context.MODE_PRIVATE);
		sLanguage = prefs.getString(PREF_KEY, ZH);
	}

	public static String getLanguage() {
		return sLanguage;
	}

	/** A human-readable name for the current language (e.g. \"简体中文\", \"English\"). */
	public static String getLanguageDisplayName() {
		if (ZH.equals(sLanguage)) return "简体中文";
		if (EN.equals(sLanguage)) return "English";
		if (JA.equals(sLanguage)) return "日本語";
		if (FR.equals(sLanguage)) return "Fran\u00e7ais";
		return "简体中文";
	}

	public static String[] getLanguageCodes() {
		return new String[]{ZH, EN, JA, FR};
	}

	public static String[] getLanguageNames() {
		return new String[]{"简体中文", "English", "日本語", "Fran\u00e7ais"};
	}

	public static void setLanguage(Context context, String lang) {
		sLanguage = lang;
		context.getSharedPreferences("fish_game_prefs", Context.MODE_PRIVATE)
			.edit()
			.putString(PREF_KEY, lang)
			.apply();
	}

	/** Get a localized string (plain). */
	public static String get(String id) {
		String value = sMap.get(sLanguage + ":" + id);
		if (value == null) value = sMap.get("zh:" + id);
		return value != null ? value : "?" + id;
	}

	/** Get a localized string with one int argument. */
	public static String get(String id, int arg) {
		return String.format(get(id), arg);
	}

	/** Get a localized string with two int arguments. */
	public static String get(String id, int arg1, int arg2) {
		return String.format(get(id), arg1, arg2);
	}

	/** Get a localized string with a String argument. */
	public static String get(String id, String arg) {
		return String.format(get(id), arg);
	}

	/** Get a localized string with arbitrary arguments. */
	public static String get(String id, Object... args) {
		return String.format(get(id), args);
	}
}
