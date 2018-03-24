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
import java.text.SimpleDateFormat;
import java.util.Date;

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

	/** Date format */
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("HH:mm:ss");

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

	private final JTextPane txtConsole;

	private final ImageButton btnDelete;

	/**
	 * コンストラクタ
	 */
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

		final Caret orgCaret = txtConsole.getCaret();
		final Caret newCaret = new VisibleCaret(orgCaret.getBlinkRate());
		txtConsole.setCaret(newCaret);

		final JPanel pnlControl = new JPanel();
		pnlControl.setPreferredSize(new Dimension(0, 24));
		pnlControl.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 2));

		btnDelete = new ImageButton(loadImage("delete_ena.png"));
		btnDelete.setPreferredSize(new Dimension(16, 16));
		pnlControl.add(btnDelete);

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

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				clear();
			}
		});
	}

	/**
	 * コンソールログをクリアする。
	 */
	public void clear() {
		synchronized (this) {
			btnDelete.setEnabled(false);
			txtConsole.setText("");
			btnDelete.setEnabled(true);
		}
	}

	/**
	 * ログ[FATAL]を出力する。
	 *
	 * @param message メッセージ
	 */
	public void fatal(final String message) {
		insert(LEVEL_FATAL, message, asFatal);
	}

	/**
	 * ログ[FATAL]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	public void fatal(final String format, final Object... objs) {
		insert(LEVEL_FATAL, String.format(format, objs), asFatal);
	}

	/**
	 * ログ[WARN]を出力する。
	 *
	 * @param message メッセージ
	 */
	public void warn(final String message) {
		insert(LEVEL_WARN, message, asWarn);
	}

	/**
	 * ログ[WARN]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	public void warn(final String format, final Object... objs) {
		insert(LEVEL_WARN, String.format(format, objs), asWarn);
	}

	/**
	 * ログ[INFO]を出力する。
	 *
	 * @param message メッセージ
	 */
	public void info(final String message) {
		insert(LEVEL_INFO, message, asInfo);
	}

	/**
	 * ログ[INFO]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	public void info(final String format, final Object... objs) {
		insert(LEVEL_INFO, String.format(format, objs), asInfo);
	}

	/**
	 * ログ[DEBUG]を出力する。
	 *
	 * @param message メッセージ
	 */
	public void debug(final String message) {
		insert(LEVEL_DEBUG, message, asDebug);
	}

	/**
	 * ログ[DEBUG]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	public void debug(final String format, final Object... objs) {
		insert(LEVEL_DEBUG, String.format(format, objs), asDebug);
	}

	/**
	 * ログ[TRACE]を出力する。
	 *
	 * @param message メッセージ
	 */
	public void trace(final String message) {
		insert(LEVEL_TRACE, message, asTrace);
	}

	/**
	 * ログ[TRACE]を出力する。
	 *
	 * @param format フォーマット
	 * @param objs パラメータ
	 */
	public void trace(final String format, final Object... objs) {
		insert(LEVEL_TRACE, String.format(format, objs), asTrace);
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
		final String date = SDF_DATE.format(new Date());
		final String msg = String.format("%s [%-5s] %s\n", date, level, log);

		final Document doc = txtConsole.getDocument();
		try {
			synchronized (this) {
				final int offset = doc.getLength();
				doc.insertString(offset, msg, as);
				txtConsole.setCaretPosition(doc.getLength() - 1);
			}
		} catch (BadLocationException ex) {
			LOGGER.error("TextPanel insert error.", ex);
		}
	}
}
