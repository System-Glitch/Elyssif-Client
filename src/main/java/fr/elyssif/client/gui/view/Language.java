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
