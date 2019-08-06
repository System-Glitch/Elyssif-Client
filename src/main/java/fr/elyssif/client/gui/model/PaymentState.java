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
 package fr.elyssif.client.gui.model;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * Simple representation of a file payment state.
 */
public class PaymentState {

	private SimpleDoubleProperty pending;
	private SimpleDoubleProperty confirmed;

	/**
	 * @param pending
	 * @param confirmed
	 */
	public PaymentState(double pending, double confirmed) {
		this.pending = new SimpleDoubleProperty(pending);
		this.confirmed = new SimpleDoubleProperty(confirmed);
	}

	/**
	 * Get the unconfirmed amount.
	 * @return pending
	 */
	public final SimpleDoubleProperty getPending() {
		return pending;
	}

	/**
	 * Set the unconfirmed amount.
	 * @param pending
	 */
	public final void setPending(double pending) {
		this.pending.set(pending);
	}

	/**
	 * Get the confirmed amount.
	 * @return confirmed
	 */
	public final SimpleDoubleProperty getConfirmed() {
		return confirmed;
	}

	/**
	 * Set the confirmed amount
	 * @param confirmed
	 */
	public final void setConfirmed(double confirmed) {
		this.confirmed.set(confirmed);
	}

	/**
	 * Update the current instance with the values of another.
	 * @param state
	 */
	public final void update(PaymentState state) {
		setConfirmed(state.getConfirmed().get());
		setPending(state.getPending().get());
	}
}