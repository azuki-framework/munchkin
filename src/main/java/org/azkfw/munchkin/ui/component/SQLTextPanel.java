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
package org.azkfw.munchkin.ui.component;

import java.awt.Color;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import org.azkfw.munchkin.sql.parser.ANSISQLParser;
import org.azkfw.munchkin.sql.parser.SQLParser;
import org.azkfw.munchkin.sql.parser.token.Token;
import org.azkfw.munchkin.sql.parser.token.TokenType;

/**
 *
 * @author Kawakicchi
 */
public class SQLTextPanel extends JTextPane {

	/** serialVersionUID */
	private static final long serialVersionUID = -6907569671824215526L;

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

	public SQLTextPanel() {
		undoManager = new UndoManager();

		parsing = false;
		parser = new ANSISQLParser();

		asDefault = new SimpleAttributeSet();
		StyleConstants.setForeground(asDefault, Color.black);
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
}
