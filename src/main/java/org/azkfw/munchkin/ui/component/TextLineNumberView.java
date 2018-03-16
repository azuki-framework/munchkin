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
package org.azkfw.munchkin.ui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class TextLineNumberView extends JComponent {

	/** serialVersionUID */
	private static final long serialVersionUID = 5766991295487339962L;

	private static final int MARGIN = 5;

	private final JTextPane textArea;

	private final FontMetrics fontMetrics;
	private final Insets textInsets;

	// private final int topInset;
	private final int fontAscent;
	private final int fontHeight;
	private final int fontDescent;
	private final int fontLeading;

	private final int fontWidth;

	public TextLineNumberView(final JTextPane textArea) {
		this(textArea, null);
	}

	public TextLineNumberView(final JTextPane textArea, final Font font) {
		this.textArea = textArea;
		textInsets = textArea.getInsets();

		Font wkFont = null;
		if (null != font) {
			wkFont = font;
		} else {
			wkFont = textArea.getFont();
		}

		fontMetrics = getFontMetrics(wkFont);
		fontHeight = fontMetrics.getHeight();
		fontAscent = fontMetrics.getAscent();
		fontDescent = fontMetrics.getDescent();
		fontLeading = fontMetrics.getLeading();
		// topInset = textArea.getInsets().top;

		fontWidth = fontMetrics.charWidth('m');

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				repaint();
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				repaint();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});
		textArea.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				revalidate();
				repaint();
			}
		});

		Insets i = textArea.getInsets();
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
				BorderFactory.createEmptyBorder(i.top, MARGIN, i.bottom, MARGIN - 1)));
		setOpaque(true);
		setBackground(new Color(250, 250, 250));
		setFont(wkFont);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getComponentWidth(), textArea.getHeight());
	}

	@Override
	public void paintComponent(final Graphics g) {
		g.setColor(getBackground());
		Rectangle clip = g.getClipBounds();
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		// System.out.println(String.format("clip %d %d %d %d", clip.x, clip.y, clip.width, clip.height));

		g.setColor(getForeground());
		int base = clip.y;
		int start = getLineAtPoint(base);
		int end = getLineAtPoint(base + clip.height);
		int y = start * fontHeight + textInsets.top;
		int rmg = getBorder().getBorderInsets(this).right;
		for (int i = start; i <= end; i++) {
			String text = String.valueOf(i + 1);
			int x = getComponentWidth() - rmg - fontMetrics.stringWidth(text);
			y += fontAscent;
			g.drawString(text, x, y);
			y += fontDescent + fontLeading;
		}
	}

	private int getComponentWidth() {
		Document doc = textArea.getDocument();
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementIndex(doc.getLength()) + 1;
		int maxDigits = Math.max(2, String.valueOf(lineCount).length());
		Insets i = getBorder().getBorderInsets(this);
		int width = maxDigits * fontWidth + i.left + i.right;
		return width;
	}

	private int getLineAtPoint(final int y) {
		Element root = textArea.getDocument().getDefaultRootElement();
		int pos = textArea.viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}
}