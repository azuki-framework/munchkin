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
package org.azkfw.munchkin.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import org.azkfw.munchkin.sql.parser.ANSISQLParser;
import org.azkfw.munchkin.sql.parser.SQLParser;
import org.azkfw.munchkin.sql.parser.token.Token;
import org.azkfw.munchkin.sql.parser.token.TokenType;

/**
 * このクラスは、SQLテキストエディタークラスです。
 *
 * @author Kawakicchi
 */
public class SQLTextEditor extends JTextPane {

	/** serialVersionUID */
	private static final long serialVersionUID = 7284998526769136420L;

	private static final Color lineColor1 = new Color(255, 255, 255);
	private static final Color lineColor2 = new Color(255, 255, 255);
	private static final Color caretLineColor = new Color(255, 230, 210);

	private final UndoManager undoManager;

	private boolean parsing;
	private final SQLParser parser;

	private final SimpleAttributeSet asDefault;
	private final SimpleAttributeSet asComment;
	private final SimpleAttributeSet asString;
	private final SimpleAttributeSet asNumber;
	private final SimpleAttributeSet asSymbol;
	private final SimpleAttributeSet asKeyword;
	private final SimpleAttributeSet asParameter;

	private final DefaultCaret caret;

	public SQLTextEditor(final Font font) {
		setOpaque(false);
		setFont(font);

		undoManager = new UndoManager();

		parsing = false;
		parser = new ANSISQLParser();

		asDefault = new SimpleAttributeSet();
		StyleConstants.setForeground(asDefault, new Color(10, 10, 10));
		asComment = new SimpleAttributeSet();
		StyleConstants.setForeground(asComment, new Color(63, 127, 95));
		asString = new SimpleAttributeSet();
		StyleConstants.setForeground(asString, new Color(200, 0, 0));
		asNumber = new SimpleAttributeSet();
		StyleConstants.setForeground(asNumber, new Color(200, 0, 0));
		asSymbol = new SimpleAttributeSet();
		StyleConstants.setForeground(asSymbol, new Color(140, 0, 0));
		asKeyword = new SimpleAttributeSet();
		StyleConstants.setForeground(asKeyword, new Color(0, 0, 200));
		asParameter = new SimpleAttributeSet();
		StyleConstants.setForeground(asParameter, new Color(100, 100, 100));

		setEditorKit(new SmartEditorKit());

		// 行ハイライト
		caret = new MyCaret(getCaret().getBlinkRate());
		setCaret(caret);

		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(final DocumentEvent e) {
				doChanged();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				doChanged();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});
		getDocument().addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(final UndoableEditEvent e) {
				final DefaultDocumentEvent event = (DefaultDocumentEvent) e.getEdit();

				if (EventType.CHANGE.equals(event.getType())) {
					// Redoが効かなくなる
					// undoManager.addEdit(new IgnoreUndoableEdit(e.getEdit()));
				} else {
					undoManager.addEdit(e.getEdit());
				}
			}
		});
	}

	@Override
	public void setText(final String text) {
		super.setText(text);
	}

	public boolean undo() {
		if (undoManager.canUndo()) {
			undoManager.undo();
			return true;
		}
		return false;
	}

	public boolean redo() {
		if (undoManager.canRedo()) {
			undoManager.redo();
			return true;
		}
		return false;
	}

	private void doChanged() {
		synchronized (this) {
			if (parsing) {
				return;
			}
			parsing = true;
		}

		final Runnable r = new Runnable() {
			@Override
			public void run() {
				while (true) {
					final String targetSql = getText();
					parser.parse(targetSql);
					final String latestSql = getText();
					if (targetSql.equals(latestSql)) {
						setAttribute(parser.getPlainTokens());
						break;
					}
				}
				parsing = false;
			}
		};
		SwingUtilities.invokeLater(r);
	}

	private void setAttribute(final List<Token> tokens) {
		final StyledDocument doc = getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), asDefault, true);

		for (Token token : tokens) {
			if (TokenType.Comment == token.type()) {
				doc.setCharacterAttributes(token.position(), token.word().length(), asComment, true);
			} else if (TokenType.String == token.type()) {
				doc.setCharacterAttributes(token.position(), token.word().length(), asString, true);
			} else if (TokenType.Symbol == token.type()) {
				doc.setCharacterAttributes(token.position(), token.word().length(), asSymbol, true);
			} else if (TokenType.Keyword == token.type()) {
				doc.setCharacterAttributes(token.position(), token.word().length(), asKeyword, true);
			} else if (TokenType.Parameter == token.type()) {
				doc.setCharacterAttributes(token.position(), token.word().length(), asParameter, true);
			}
		}
	}

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

		y = caret.y;
		g2.setPaint(caretLineColor);
		g2.fillRect(0, y, w, h);

		super.paintComponent(g);
	}

	private int getLineAtPoint(final int y) {
		final Element root = getDocument().getDefaultRootElement();
		final int pos = viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}

	private class MyCaret extends DefaultCaret {

		/** serialVersionUID */
		private static final long serialVersionUID = -5227694834003006790L;

		public MyCaret(final int blinkRate) {
			setBlinkRate(blinkRate);
		}

		@Override
		protected synchronized void damage(final Rectangle rect) {
			if (null != rect) {
				final JTextComponent c = getComponent();
				x = 0;
				y = rect.y;
				width = c.getSize().width;
				height = rect.height;
				c.repaint();
			}
		}
	}
}
