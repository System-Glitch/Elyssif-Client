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

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Text formatter for Bitcoin amounts.
 * @author Jérémy LAMBERT
 *
 */
public final class BitcoinFormatter {

	public static final int MODE_AUTO     = 0;
	public static final int MODE_SATOSHIS = 1;
	public static final int MODE_BTC      = 2;

	private static final String UNIT_SAT = "Satoshi";
	private static final String UNIT_BTC = "BTC";

	private double amount;

	/**
	 * Create a new instance of formatter with a value of 0.
	 */
	public BitcoinFormatter() {
		this(0);
	}

	/**
	 * Create a new instance of formatter.
	 * @param amount the amount to format, in BTC, up to 8 decimals
	 */
	public BitcoinFormatter(double amount) {
		checkAmount(amount);
		this.amount = amount;
	}

	/**
	 * Format the amount, automatically selecting
	 * the most appropriated unit (Sat or BTC).
	 * @return formatted amount
	 */
	public String format() {
		return format(MODE_AUTO);
	}

	/**
	 * Format the amount, automatically selecting
	 * the most appropriated unit (Sat or BTC).
	 * @param mode the format mode, defines the unit (MODE_SATOSHIS : MODE_BTC)
	 * @return formatted amount
	 */
	public String format(int mode) {
		double absAmount = Math.abs(amount);
		switch(mode) {
		case MODE_AUTO:
			return format(absAmount < 1e-4 ? MODE_SATOSHIS : MODE_BTC);
		case MODE_SATOSHIS:
			String unit = UNIT_SAT;

			if(absAmount > 1e-8) {
				unit += 's';
			}

			return Double.valueOf(amount * 1e8).intValue() + " " + unit;
		case MODE_BTC:
			DecimalFormat df = new DecimalFormat("#.########");
			df.setRoundingMode(RoundingMode.HALF_DOWN);
			return df.format(amount) + " " + UNIT_BTC;
		default: throw new IllegalArgumentException("Invalid format mode.");
		}
	}

	/**
	 * 
	 * @return the amount to format
	 */
	public final double getAmount() {
		return amount;
	}

	/**
	 * Set the amount to format, in BTC, up to 8 decimals.
	 * @param amount
	 */
	public final void setAmount(double amount) {
		checkAmount(amount);
		this.amount = amount;
	}

	private void checkAmount(double amount) {
		if(amount < 1e-8 && amount > 0) throw new IllegalArgumentException("Bitcoin amount must be at least one Satoshi, " + amount + " BTC given.");
	}	

}
