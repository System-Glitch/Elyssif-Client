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
 package fr.elyssif.client.callback;

import fr.elyssif.client.gui.model.PaymentState;
import fr.elyssif.client.http.RestResponse;

/**
 * Callback data for the payment state request.
 * @author Jérémy LAMBERT
 *
 */
public final class PaymentStateCallbackData extends JsonCallbackData {

	private PaymentState state;

	public PaymentStateCallbackData(RestResponse response) {
		super(response);
	}

	/**
	 * Get the payment state.
	 * @return state
	 */
	public final PaymentState getState() {
		return state;
	}

	/**
	 * Set the payment state.
	 * @param state
	 */
	public final void setState(PaymentState state) {
		this.state = state;
	}

}
