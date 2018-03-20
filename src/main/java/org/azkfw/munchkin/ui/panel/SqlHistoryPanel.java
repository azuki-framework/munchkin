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
import java.awt.Component;
import java.awt.FontMetrics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Caret;

import org.azkfw.munchkin.entity.SQLHistoryEntity;
import org.azkfw.munchkin.ui.VisibleCaret;
import org.azkfw.munchkin.ui.component.SQLTextPanel;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 * このクラスは、SQL実行履歴パネルクラスです。
 * 
 * @author kawakicchi
 */
public class SqlHistoryPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 5855859659397329758L;

	private final List<SqlHistoryPanelListener> listeners;

	private final DefaultTableModel model;

	private final JTable table;
	private final SQLTextPanel text;

	private long maxHistory = 100;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private long thresholdTime = 2000;

	/**
	 * コンストラクタ
	 */
	public SqlHistoryPanel() {
		setLayout(new BorderLayout());

		listeners = new ArrayList<SqlHistoryPanelListener>();

		model = new DefaultTableModel() {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		};

		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				SQLHistoryEntity history = null;
				if (0 < table.getSelectedRowCount()) {
					int row = table.getSelectedRow();
					int rowReal = table.convertRowIndexToModel(row);
					history = (SQLHistoryEntity) model.getValueAt(rowReal, 0);
				}
				doChangedHistory(history);
			}
		});

		text = new SQLTextPanel();
		text.setEditable(false);
		text.setKeywordPattern(Pattern.compile("^(select|as)$", Pattern.CASE_INSENSITIVE));
		text.setParameterPattern(Pattern.compile("^:(.+)$", Pattern.CASE_INSENSITIVE));

		final Caret orgCaret = text.getCaret();
		final Caret newCaret = new VisibleCaret(orgCaret.getBlinkRate());
		text.setCaret(newCaret);

		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(table));
		split.setRightComponent(new JScrollPane(text));

		model.addColumn("");
		model.addColumn("実行日時");
		model.addColumn("処理時間");
		model.addColumn("実行SQL");

		final TableColumnModel tcm = table.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));

		final FontMetrics fm = table.getFontMetrics(table.getFont());
		tcm.getColumn(0).setPreferredWidth(fm.stringWidth("9999/99/99 00:00:00") + 4 + 2);
		tcm.getColumn(1).setPreferredWidth(fm.stringWidth("9999999 ms") + 4 + 2);
		tcm.getColumn(2).setPreferredWidth(200);

		tcm.getColumn(1).setCellRenderer(new TimeTableCellRenderer(thresholdTime));

		add(BorderLayout.CENTER, split);
	}

	/**
	 * SQL実行履歴パネルリスナーを追加する。
	 *
	 * @param listener SQL実行履歴パネルリスナー
	 */
	public synchronized void addSqlHistoryPanelListener(final SqlHistoryPanelListener listener) {
		listeners.add(listener);
	}

	/**
	 * SQL実行履歴情報リストを設定する。
	 *
	 * @param historys SQL実行履歴情報リスト
	 */
	public void setSqlHistorys(final List<SQLHistoryEntity> historys) {
		model.setRowCount(0);
		for (SQLHistoryEntity history : historys) {
			final Object[] objs = toArray(history);
			model.addRow(objs);
		}
	}

	/**
	 * SQL実行履歴情報リストを取得する。
	 *
	 * @return SQL実行履歴情報リスト
	 */
	public List<SQLHistoryEntity> getSqlHistorys() {
		final List<SQLHistoryEntity> historys = new ArrayList<SQLHistoryEntity>();
		for (int row = 0; row < model.getRowCount(); row++) {
			final SQLHistoryEntity history = (SQLHistoryEntity) model.getValueAt(row, 0);
			historys.add(history);
		}
		return historys;
	}

	/**
	 * SQL実行履歴情報を追加する。
	 *
	 * @param history SQL実行履歴情報
	 */
	public void addSqlHistory(final SQLHistoryEntity history) {
		final Object[] objs = toArray(history);

		model.insertRow(0, objs);
		while (maxHistory < model.getRowCount()) {
			model.removeRow(model.getRowCount() - 1);
		}
	}

	private void doChangedHistory(final SQLHistoryEntity history) {
		if (MunchkinUtil.isNull(history)) {
			text.setText("");
		} else {
			text.setText(history.getSql());
		}
		text.setCaretPosition(0);
	}

	private Object[] toArray(final SQLHistoryEntity history) {
		final Object[] objs = new Object[4];

		objs[0] = history;

		final Date date = history.getDate();
		if (MunchkinUtil.isNull(date)) {
			objs[1] = "";
		} else {
			if (MunchkinUtil.isNull(format)) {
				objs[1] = date.toString();
			} else {
				objs[1] = format.format(date);
			}
		}

		objs[2] = Long.valueOf(history.getTime());
		objs[3] = history.getSql();

		return objs;
	}

	private static class TimeTableCellRenderer extends DefaultTableCellRenderer {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private final long threshold;

		public TimeTableCellRenderer(final long threshold) {
			this.threshold = threshold;
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (c instanceof JLabel) {
				final Long l = (Long) value;

				final JLabel label = (JLabel) c;

				final String str = String.format("%d ms", l);
				label.setText(str);

				if (l >= threshold) {
					setForeground(Color.red);
				} else {
					setForeground(table.getForeground());
				}

				label.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			return c;
		}
	}
}
