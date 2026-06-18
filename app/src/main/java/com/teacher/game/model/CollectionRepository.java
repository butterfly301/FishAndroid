package com.teacher.game.model;

import java.util.ArrayList;

/**
 * Central registry of all collection entries (encyclopedia).
 */
public final class CollectionRepository {

	private static final CollectionEntry[] ENTRIES = {
		// FISH (8)
		new CollectionEntry("coll_fish_SURGEON",      "fish_SURGEON",      "fish_SURGEON_desc",      CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_GLOW_SURGEON", "fish_GLOW_SURGEON", "fish_GLOW_SURGEON_desc", CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_TUNA",         "fish_TUNA",         "fish_TUNA_desc",         CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_SUN_TUNA",     "fish_SUN_TUNA",     "fish_SUN_TUNA_desc",     CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_LION",         "fish_LION",         "fish_LION_desc",         CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_ROYAL_LION",   "fish_ROYAL_LION",   "fish_ROYAL_LION_desc",   CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_SHARK",        "fish_SHARK",        "fish_SHARK_desc",        CollectionEntry.Category.FISH),
		new CollectionEntry("coll_fish_REEF_SHARK",   "fish_REEF_SHARK",   "fish_REEF_SHARK_desc",   CollectionEntry.Category.FISH),

		// POWERUP (5) — name keys reuse existing PowerUpType l10n keys
		new CollectionEntry("coll_power_SPEED",       "powerup_speed",     "powerup_speed_desc",     CollectionEntry.Category.POWERUP),
		new CollectionEntry("coll_power_SHIELD",      "powerup_shield",    "powerup_shield_desc",    CollectionEntry.Category.POWERUP),
		new CollectionEntry("coll_power_FREEZE",      "powerup_freeze",    "powerup_freeze_desc",    CollectionEntry.Category.POWERUP),
		new CollectionEntry("coll_power_BOMB",        "powerup_bomb",      "powerup_bomb_desc",      CollectionEntry.Category.POWERUP),
		new CollectionEntry("coll_power_LURE",        "powerup_lure",      "powerup_lure_desc",      CollectionEntry.Category.POWERUP),

		// OTHER (1)
		new CollectionEntry("coll_companion",         "coll_companion",    "coll_companion_desc",    CollectionEntry.Category.OTHER),
	};

	private CollectionRepository() {}

	public static CollectionEntry[] getAll() {
		return ENTRIES;
	}

	public static int getCount() {
		return ENTRIES.length;
	}

	public static CollectionEntry get(int index) {
		return ENTRIES[index];
	}

	public static ArrayList<CollectionEntry> getByCategory(CollectionEntry.Category category) {
		ArrayList<CollectionEntry> result = new ArrayList<>();
		for (CollectionEntry e : ENTRIES) {
			if (e.category == category) {
				result.add(e);
			}
		}
		return result;
	}
}
