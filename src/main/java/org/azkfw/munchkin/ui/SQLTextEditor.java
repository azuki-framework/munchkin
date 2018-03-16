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
package org.azkfw.munchkin.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.azkfw.munchkin.style.DocumentStyle;
import org.azkfw.munchkin.style.SQLDocumentStyle;

public class SQLTextEditor extends JTextPane {

	/** serialVersionUID */
	private static final long serialVersionUID = 7284998526769136420L;

	private final DocumentStyle style;
	private final SimpleAttributeSet defaultAttributeSet;

	//
	private static final Color lineColor1 = new Color(255, 255, 255);
	//private static final Color lineColor2 = new Color(255, 250, 245);
	private static final Color lineColor2 = new Color(255, 255, 255);

	// 行ハイライト
	private static final Color lineCaretColor = new Color(255, 230, 210);
	private final DefaultCaret caret;

	public SQLTextEditor(final Font font) {
		setOpaque(false);

		style = new SQLDocumentStyle();
		defaultAttributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(defaultAttributeSet, new Color(10, 10, 10));

		setFont(font);

		setEditorKit(new SmartEditorKit());

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
				final StyledDocument doc = getStyledDocument();
				doc.setCharacterAttributes(0, doc.getLength(), defaultAttributeSet, true);

				style.apply(doc);
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
		final Graphics2D g2 = (Graphics2D) g;

		final Insets insets = getInsets();

		g2.setColor(getBackground());
		final Rectangle clip = g2.getClipBounds();
		g2.fillRect(clip.x, clip.y, clip.width, clip.height);

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

		super.paintComponent(g);
	}
}
