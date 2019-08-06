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
 package fr.elyssif.client;

/**
 * Various utilities for String.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class StringUtils {

	/**
	 * Convert a snake case string to camel case.
	 * @param str the string to convert
	 * @return a camel case version of the given string
	 */
	public static String toCamelCase(String str) {
		StringBuilder builder = new StringBuilder(str);
		int index = builder.indexOf("_");
		while(index != -1) {
			if(index < str.length() - 1)
				builder.setCharAt(index + 1, Character.toUpperCase(builder.charAt(index + 1)));
			builder.deleteCharAt(index);
			index = builder.indexOf("_");
		}
		return builder.toString();
	}

	/**
	 * Convert a camel case string to snake case.
	 * @param str the string to convert
	 * @return a snake case version of the given string
	 */
	public static String toSnakeCase(String str) {
		StringBuilder builder = new StringBuilder(str);
		for(int i = 0 ; i < builder.length() ; i++) {
			char c = builder.charAt(i);
			if(Character.isUpperCase(c)) {
				builder.setCharAt(i, Character.toLowerCase(c));
				builder.insert(i, '_');
			}
		}

		return builder.toString();
	}

}
