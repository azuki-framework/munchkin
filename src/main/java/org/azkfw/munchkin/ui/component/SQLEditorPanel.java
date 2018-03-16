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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.UndoManager;

import org.azkfw.munchkin.ui.SQLTextEditor;

/**
 * このクラスは、SQLエディターパネルクラスです。
 * 
 * @author Kawakicchi
 */
public class SQLEditorPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -6756071304148806058L;

	public static final String ACTION_KEY_UNDO = "undo";
	public static final String ACTION_KEY_REDO = "redo";

	private final List<SQLEditorPanelListener> listeners;

	private final SQLTextEditor txtEditor;

	private final UndoManager undoManager;

	/**
	 * コンストラクタ
	 */
	public SQLEditorPanel() {
		listeners = new ArrayList<SQLEditorPanelListener>();
		undoManager = new UndoManager();

		setLayout(new BorderLayout());

		Font font = null;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			font = new Font("ＭＳ ゴシック", Font.PLAIN, 14);
		} else if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			font = new Font("Osaka-Mono", Font.PLAIN, 16);
		} else {
			font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
		}
		txtEditor = new SQLTextEditor(font);

		TextLineNumberView numberView = new TextLineNumberView(txtEditor, font);
		JScrollPane scroll = new JScrollPane(txtEditor);
		scroll.setRowHeaderView(numberView);

		final JPanel pnlControl = new JPanel();
		pnlControl.setPreferredSize(new Dimension(0, 32));

		add(BorderLayout.NORTH, pnlControl);
		add(BorderLayout.CENTER, scroll);

		txtEditor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_ENTER) {
					String text = txtEditor.getSelectedText();
					if (null == text) {
						text = txtEditor.getText();
					}
					if (0 < text.length()) {
						doExecSQL(text);
					}
					e.consume();
				}
			}
		});

		final ActionMap amap = txtEditor.getActionMap();
		final InputMap imap = txtEditor.getInputMap();
		if (amap.get(ACTION_KEY_UNDO) == null) {
			UndoAction undoAction = new UndoAction();
			amap.put(ACTION_KEY_UNDO, undoAction);
			imap.put((KeyStroke) undoAction.getValue(Action.ACCELERATOR_KEY), ACTION_KEY_UNDO);
		}
		if (amap.get(ACTION_KEY_REDO) == null) {
			RedoAction redoAction = new RedoAction();
			amap.put(ACTION_KEY_REDO, redoAction);
			imap.put((KeyStroke) redoAction.getValue(Action.ACCELERATOR_KEY), ACTION_KEY_REDO);
		}
		txtEditor.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				if (e instanceof DefaultDocumentEvent) {
					DefaultDocumentEvent de = (DefaultDocumentEvent) e;
					undoManager.addEdit(de);
				}
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				if (e instanceof DefaultDocumentEvent) {
					DefaultDocumentEvent de = (DefaultDocumentEvent) e;
					undoManager.addEdit(de);
				}
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});
	}

	public void undo() {

	}

	public void redo() {

	}

	public String getText() {
		return txtEditor.getText();
	}

	public void setText(final String text) {
		txtEditor.setText(text);
	}

	public synchronized void addSQLEditorPanelListener(final SQLEditorPanelListener listener) {
		listeners.add(listener);
	}

	private void doExecSQL(final String sql) {
		for (SQLEditorPanelListener l : listeners) {
			l.sqlEditorPanelExecSQL(sql);
		}
	}

	/**
	 * Undoアクション
	 */
	private class UndoAction extends AbstractAction {
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private UndoAction() {
			super("元に戻す(U)");
			putValue(MNEMONIC_KEY, new Integer('U'));
			putValue(SHORT_DESCRIPTION, "元に戻す");
			putValue(LONG_DESCRIPTION, "元に戻す");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Z', Event.CTRL_MASK));
			//putValue(SMALL_ICON, SwingUtilities.getImageIcon("/resources/EditUndo.png"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		}
	}

	/**
	 * Redoアクション
	 */
	private class RedoAction extends AbstractAction {
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private RedoAction() {
			super("やり直し(R)");
			putValue(MNEMONIC_KEY, new Integer('R'));
			putValue(SHORT_DESCRIPTION, "やり直し");
			putValue(LONG_DESCRIPTION, "やり直し");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Y', Event.CTRL_MASK));
			//putValue(SMALL_ICON, SwingUtil.getImageIcon("/resources/EditRedo.png"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
	}
}
