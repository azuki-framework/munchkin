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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class ConsolePanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -8926247151128087330L;

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePanel.class);

	private static final String LEVEL_FATAL = "FATAL";
	private static final String LEVEL_WARN = "WARN";
	private static final String LEVEL_INFO = "INFO";
	private static final String LEVEL_DEBUG = "DEBUG";
	private static final String LEVEL_TRACE = "TRACE";

	private final SimpleAttributeSet asFatal;
	private final SimpleAttributeSet asWarn;
	private final SimpleAttributeSet asInfo;
	private final SimpleAttributeSet asDebug;
	private final SimpleAttributeSet asTrace;

	private final JTextPane txtConsole;

	public ConsolePanel() {
		setLayout(new BorderLayout());

		txtConsole = new JTextPane() {
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
		txtConsole.setEditable(false);

		final JPanel pnlControl = new JPanel();
		pnlControl.setPreferredSize(new Dimension(0, 32));

		add(BorderLayout.NORTH, pnlControl);
		add(BorderLayout.CENTER, new JScrollPane(txtConsole));

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
	}

	public synchronized void fatal(final String log) {
		insert(LEVEL_FATAL, log, asFatal);
	}

	public synchronized void fatal(final String log, final Object... objs) {
		insert(LEVEL_FATAL, String.format(log, objs), asFatal);
	}

	public synchronized void warn(final String log) {
		insert(LEVEL_WARN, log, asWarn);
	}

	public synchronized void warn(final String log, final Object... objs) {
		insert(LEVEL_WARN, String.format(log, objs), asWarn);
	}

	public synchronized void info(final String log) {
		insert(LEVEL_INFO, log, asInfo);
	}

	public synchronized void info(final String log, final Object... objs) {
		insert(LEVEL_INFO, String.format(log, objs), asInfo);
	}

	public synchronized void debug(final String log) {
		insert(LEVEL_DEBUG, log, asDebug);
	}

	public synchronized void debug(final String log, final Object... objs) {
		insert(LEVEL_DEBUG, String.format(log, objs), asDebug);
	}

	public synchronized void trace(final String log) {
		insert(LEVEL_TRACE, log, asTrace);
	}

	public synchronized void trace(final String log, final Object... objs) {
		insert(LEVEL_TRACE, String.format(log, objs), asTrace);
	}

	private synchronized void insert(final String level, final String log, final AttributeSet as) {
		final String msg = String.format("[%-5s] %s\n", level, log);

		final Document doc = txtConsole.getDocument();
		final int offset = doc.getLength();
		try {
			txtConsole.getDocument().insertString(offset, msg, as);
		} catch (BadLocationException ex) {
			LOGGER.error("TextPanel insert error.", ex);
		}
	}
}
