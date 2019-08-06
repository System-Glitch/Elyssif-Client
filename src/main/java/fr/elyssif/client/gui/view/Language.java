/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client.gui.view;

/**
 * Enum listing available languages and linking them with a
 * resource bundle.
 * @author Jérémy LAMBERT
 *
 */
public enum Language {

	ENGLISH("English", "en"),
	FRENCH("Français", "fr");

	private String title;
	private String shortCode;

	Language(String title, String shortCode) {
		this.title = title;
		this.shortCode = shortCode;
	}

	public String getTitle() {
		return title;
	}

	public String getShortCode() {
		return shortCode;
	}

	@Override
	public String toString() {
		return title;
	}

	/**
	 * Find a language by it's short code.
	 * @param shortCode
	 * @return the language corresponding to the given
	 * short code, or null if not found.
	 */
	public static Language fromShortCode(String shortCode) {
		for(Language lang : values()) {
			if(lang.getShortCode().equals(shortCode)) {
				return lang;
			}
		}
		return null;
	}

}
