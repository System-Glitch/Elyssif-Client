package fr.elyssif.client.gui.controller;

/**
 * Functional interface for controllers that need to be locked (such as forms)<br>
 * All controllers implementing this interface should have a SimpleBooleanProperty which defines
 * if the view is locked or not and bind it to the controls through <code>bindControls()</code>.
 * @author Jérémy LAMBERT
 *
 */
public interface Lockable {

	/**
	 * Bind the disable property to all controls in the view.
	 */
	void bindControls();

	/**
	 * Lock the view by disabling all controls or
	 * unlock by enabling all controls.
	 */
	void setLocked(boolean locked);

}
