/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.munchkin.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.azkfw.munchkin.ui.VisibleCaret;
import org.azkfw.munchkin.ui.component.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、コンソールパネルクラスです。
 * 
 * @author Kawakicchi
 */
public class ConsolePanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -8926247151128087330L;

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePanel.class);

	/** Level [FATAL] */
	private static final String LEVEL_FATAL = "FATAL";
	/** Level [WARN] */
	private static final String LEVEL_WARN = "WARN";
	/** Level [INFO] */
	private static final String LEVEL_INFO = "INFO";
	/** Level [DEBUG] */
	private static final String LEVEL_DEBUG = "DEBUG";
	/** Level [TRACE] */
	private static final String LEVEL_TRACE = "TRACE";

	/** AttributeSet [FATAL] */
	private final SimpleAttributeSet asFatal;
	/** AttributeSet [WARN] */
	private final SimpleAttributeSet asWarn;
	/** AttributeSet [INFO] */
	private final SimpleAttributeSet asInfo;
	/** AttributeSet [DEBUG] */
	private final SimpleAttributeSet asDebug;
	/** AttributeSet [TRACE] */
	private final SimpleAttributeSet asTrace;

	private final JTextPane text;

	private final ImageButton btnDelete;

	public ConsolePanel() {
		setLayout(new BorderLayout());

		text = new JTextPane() {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean getScrollableTracksViewportWidth() {
				final Object parent = getParent();
				if (parent instanceof JViewport) {
					final JViewport port = (JViewport) parent;
					final int width = port.getWidth();
					final TextUI ui = getUI();
					final Dimension sz = ui.getPreferredSize(this);
					if (sz.width < width) {
						return true;
					}
				}
				return false;
			}
		};
		text.setEditable(false);

		final Caret orgCaret = text.getCaret();
		final Caret newCaret = new VisibleCaret(orgCaret.getBlinkRate());
		text.setCaret(newCaret);

		final JPanel pnlControl = new JPanel();
		pnlControl.setPreferredSize(new Dimension(0, 24));
		pnlControl.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 2));

		btnDelete = new ImageButton(loadImage("delete_ena.png"));
		btnDelete.setPreferredSize(new Dimension(16, 16));
		pnlControl.add(btnDelete);

		add(BorderLayout.NORTH, pnlControl);
		add(BorderLayout.CENTER, new JScrollPane(text));

		asFatal = new SimpleAttributeSet();
		StyleConstants.setForeground(asFatal, Color.RED);
		asWarn = new SimpleAttributeSet();
		StyleConstants.setForeground(asWarn, Color.YELLOW);
		asInfo = new SimpleAttributeSet();
		StyleConstants.setForeground(asInfo, Color.BLACK);
		asDebug = new SimpleAttributeSet();
		StyleConstants.setForeground(asDebug, Color.GREEN);
		asTrace = new SimpleAttributeSet();
		StyleConstants.setForeground(asTrace, Color.GRAY);

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				btnDelete.setEnabled(false);
				text.setText("");
				btnDelete.setEnabled(true);
			}
		});
	}

	public void fatal(final String log) {
		insert(LEVEL_FATAL, log, asFatal);
	}

	public void fatal(final String log, final Object... objs) {
		insert(LEVEL_FATAL, String.format(log, objs), asFatal);
	}

	public void warn(final String log) {
		insert(LEVEL_WARN, log, asWarn);
	}

	public void warn(final String log, final Object... objs) {
		insert(LEVEL_WARN, String.format(log, objs), asWarn);
	}

	public void info(final String log) {
		insert(LEVEL_INFO, log, asInfo);
	}

	public void info(final String log, final Object... objs) {
		insert(LEVEL_INFO, String.format(log, objs), asInfo);
	}

	public void debug(final String log) {
		insert(LEVEL_DEBUG, log, asDebug);
	}

	public void debug(final String log, final Object... objs) {
		insert(LEVEL_DEBUG, String.format(log, objs), asDebug);
	}

	public void trace(final String log) {
		insert(LEVEL_TRACE, log, asTrace);
	}

	public void trace(final String log, final Object... objs) {
		insert(LEVEL_TRACE, String.format(log, objs), asTrace);
	}

	private BufferedImage loadImage(final String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
		} catch (IOException ex) {
			LOGGER.error("Image load error.", ex);
		}
		return image;
	}

	private synchronized void insert(final String level, final String log, final AttributeSet as) {
		final String msg = String.format("[%-5s] %s\n", level, log);

		final Document doc = text.getDocument();
		final int offset = doc.getLength();
		try {
			doc.insertString(offset, msg, as);
			text.setCaretPosition(doc.getLength() - 1);
		} catch (BadLocationException ex) {
			LOGGER.error("TextPanel insert error.", ex);
		}
	}
}
