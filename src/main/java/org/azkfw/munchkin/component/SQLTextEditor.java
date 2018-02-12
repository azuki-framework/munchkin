/**
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
package org.azkfw.munchkin.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class SQLTextEditor extends JTextPane {

	/** serialVersionUID */
	private static final long serialVersionUID = 7284998526769136420L;

	private static final Pattern PTN_KEYWORD = Pattern
			.compile(
					"(SELECT|END|GROUP|UNION|ALL|AS|MAX|LIKE|MINUS|SUM|CASE|WHEN|THEN|ELSE|NULL|IS|NOT|SYSDATE|FROM|WHERE|AND|OR|ORDER|BY)",
					Pattern.CASE_INSENSITIVE);
	private static final Pattern PTN_COMMENT = Pattern.compile("(--[^\\r\\n]*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern PTN_STRING = Pattern.compile("('[^']*')", Pattern.CASE_INSENSITIVE);
	private static final Pattern PTN_SYMBOL = Pattern.compile("(,|=|<|>|/|\\.|\\*|\\+|\\(|\\)|\\|)",
			Pattern.CASE_INSENSITIVE);

	private MutableAttributeSet atrDefault;
	private MutableAttributeSet atrKeyword;
	private MutableAttributeSet atrCommnet;
	private MutableAttributeSet atrString;
	private MutableAttributeSet atrSymbol;

	private Color colorKeyword;
	private Color colorComment;
	private Color colorString;
	private Color colorSymbol;

	//
	private static final Color lineColor1 = new Color(255, 255, 255);
	//private static final Color lineColor2 = new Color(255, 250, 245);
	private static final Color lineColor2 = new Color(255, 255, 255);

	// 行ハイライト
	private static final Color lineCaretColor = new Color(255, 230, 210);
	private final DefaultCaret caret;

	public SQLTextEditor(final Font font) {
		setOpaque(false);

		setFont(font);

		setEditorKit(new SmartEditorKit());

		colorKeyword = new Color(0, 0, 205);
		colorComment = new Color(0, 120, 0);
		colorString = new Color(220, 0, 0);
		colorSymbol = new Color(128, 0, 0);

		boolean boldFlag = false;

		atrDefault = new SimpleAttributeSet();
		StyleConstants.setForeground(atrDefault, Color.BLACK);

		atrKeyword = new SimpleAttributeSet();
		StyleConstants.setForeground(atrKeyword, colorKeyword);
		StyleConstants.setBold(atrKeyword, boldFlag);

		atrCommnet = new SimpleAttributeSet();
		StyleConstants.setForeground(atrCommnet, colorComment);
		StyleConstants.setBold(atrCommnet, boldFlag);

		atrString = new SimpleAttributeSet();
		StyleConstants.setForeground(atrString, colorString);
		StyleConstants.setBold(atrString, boldFlag);

		atrSymbol = new SimpleAttributeSet();
		StyleConstants.setForeground(atrSymbol, colorSymbol);
		StyleConstants.setBold(atrSymbol, boldFlag);

		// 行ハイライト
		caret = new DefaultCaret() {
			/** serialVersionUID */
			private static final long serialVersionUID = -5227694834003006790L;

			@Override
			protected synchronized void damage(final Rectangle r) {
				if (r != null) {
					JTextComponent c = getComponent();
					x = 0;
					y = r.y;
					width = c.getSize().width;
					height = r.height;
					c.repaint();
				}
			}
		};
		caret.setBlinkRate(getCaret().getBlinkRate());
		setCaret(caret);

		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				doChangeText();
			}

			@Override
			public void keyPressed(final KeyEvent e) {
			}
		});
	}

	@Override
	public void setText(final String text) {
		super.setText(text);
		doChangeText();
	}

	protected void doChangeText() {
		if (true) {
			try {
				int length = getDocument().getLength();
				String text = getDocument().getText(0, length);

				getStyledDocument().setCharacterAttributes(0, length, atrDefault, true);

				Matcher m = null;
				m = PTN_KEYWORD.matcher(text);
				while (m.find()) {
					getStyledDocument().setCharacterAttributes(m.start(), m.end() - m.start(), atrKeyword, true);
				}

				m = PTN_SYMBOL.matcher(text);
				while (m.find()) {
					getStyledDocument().setCharacterAttributes(m.start(), m.end() - m.start(), atrSymbol, true);
				}

				m = PTN_STRING.matcher(text);
				while (m.find()) {
					getStyledDocument().setCharacterAttributes(m.start(), m.end() - m.start(), atrString, true);
				}

				m = PTN_COMMENT.matcher(text);
				while (m.find()) {
					getStyledDocument().setCharacterAttributes(m.start(), m.end() - m.start(), atrCommnet, true);
				}
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}

	private int getLineAtPoint(final int y) {
		Element root = getDocument().getDefaultRootElement();
		int pos = viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}

	// 行ハイライト
	@Override
	protected void paintComponent(final Graphics g) {
		if (true) {
			Graphics2D g2 = (Graphics2D) g;

			Insets insets = getInsets();

			g2.setColor(getBackground());
			Rectangle clip = g2.getClipBounds();
			g2.fillRect(clip.x, clip.y, clip.width, clip.height);
			// System.out.println(String.format("clip %d %d %d %d", clip.x,
			// clip.y, clip.width, clip.height));

			g2.setColor(getForeground());
			int base = clip.y;
			int start = getLineAtPoint(base);
			int end = getLineAtPoint(base + clip.height);
			int x = 0;
			int y = start * caret.height + insets.top;
			int w = getWidth();
			int h = caret.height;
			for (int i = start; i <= end; i++) {
				if (0 == i % 2) {
					g2.setColor(lineColor1);
				} else {
					g2.setColor(lineColor2);
				}
				g2.fillRect(x, y, w, h);
				y += h;
			}

			// ハイライト行
			y = caret.y;
			g2.setPaint(lineCaretColor);
			g2.fillRect(0, y, w, h);

		} else {
			Graphics2D g2 = (Graphics2D) g;
			Insets insets = getInsets();

			int y = insets.top;
			int h = caret.height;
			int w = getWidth();

			int i = 0;
			if (0 < h) {
				while (y < getHeight()) {
					if (0 == i % 2) {
						g2.setColor(lineColor1);
					} else {
						g2.setColor(lineColor2);
					}
					g2.fillRect(0, y, w, h);
					y += h;
					i++;
				}
			}
			// ハイライト行
			y = caret.y;
			g2.setPaint(lineCaretColor);
			g2.fillRect(0, y, w, h);
		}
		// System.out.println(String.format("x: %d; y: %d; width: %d; height: %d",
		// getX(), getY(), getWidth(), getHeight()));
		super.paintComponent(g);
	}
}
