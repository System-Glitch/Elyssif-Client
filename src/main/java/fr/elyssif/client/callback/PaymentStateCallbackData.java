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
