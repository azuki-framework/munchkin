package org.azkwf.munchkin.component;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Kawakicchi
 */
public class StatusBar extends JComponent {

	private JLabel label;

	private JProgressBar progressBar;

	public StatusBar() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setLayout(null);

		label = new JLabel();
		label.setLocation(4, 2);
		add(label);

		progressBar = new JProgressBar(0, 100);
		add(progressBar);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				doResize();
			}

			@Override
			public void componentResized(final ComponentEvent e) {
				doResize();
			}

			private void doResize() {
				Insets insets = getInsets();
				int width = getWidth() - (insets.left + insets.right);
				int height = getHeight() - (insets.top + insets.bottom);

				label.setSize(width - (160 + 8), height - 4);
				progressBar.setBounds(width - (160), insets.top + 2, 160, height - 2);
			}
		});

		Dimension dim = new Dimension(-1, 24);
		setPreferredSize(dim);
	}

	public void setMessage(final String message) {
		label.setText(message);
	}

	public void setProgressValue(final double percent) {
		progressBar.setValue((int) percent);
	}
}
