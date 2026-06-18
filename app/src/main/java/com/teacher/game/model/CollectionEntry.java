package com.teacher.game.model;

import com.teacher.game.state.L10n;

/**
 * A single entry in the collection encyclopedia.
 */
public class CollectionEntry {

	public enum Category {
		FISH,
		POWERUP,
		OTHER
	}

	public final String id;         // unique ID used for preference keys
	public final String nameKey;    // L10n key for display name
	public final String descKey;    // L10n key for description
	public final Category category;

	public CollectionEntry(String id, String nameKey, String descKey, Category category) {
		this.id = id;
		this.nameKey = nameKey;
		this.descKey = descKey;
		this.category = category;
	}

	public String getName() {
		return L10n.get(nameKey);
	}

	public String getDesc() {
		return L10n.get(descKey);
	}
}
