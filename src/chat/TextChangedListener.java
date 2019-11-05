package chat;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * Customized text changed listener from DocumentListener
 */
public abstract class TextChangedListener implements DocumentListener {
	public abstract void update();
	@Override
	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		update();
	}

}
