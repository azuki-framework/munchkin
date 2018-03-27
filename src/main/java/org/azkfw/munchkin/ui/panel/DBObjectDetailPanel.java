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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.azkfw.munchkin.database.model.entity.ObjectDetailEntity;
import org.azkfw.munchkin.ui.ColumnWidths;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、データベースオブジェクト詳細パネルクラスです。
 * 
 * @author Kawakicchi
 */
public class DBObjectDetailPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 7484142852594382091L;

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(DBObjectDetailPanel.class);

	/** listener */
	private final List<DBObjectDetailPanelListener> listeners;

	private final DefaultTableModel model;
	private final TableRowSorter<DefaultTableModel> sorter;

	private final DetailTableCellRenderer renderer;

	private final JTextField txtFilter;
	private final JTable table;

	public DBObjectDetailPanel() {
		listeners = new ArrayList<DBObjectDetailPanelListener>();

		model = new DefaultTableModel() {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		};

		sorter = new TableRowSorter<DefaultTableModel>(model);

		txtFilter = new JTextField();

		renderer = new DetailTableCellRenderer();

		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(true);
		table.setSelectionBackground(new Color(255, 204, 153));
		table.setSelectionForeground(table.getForeground());

		initLayout();
		initEvent();
	}

	public synchronized void addDBObjectDetailListener(final DBObjectDetailPanelListener listener) {
		listeners.add(listener);
	}

	public void setObjectDetail(final ObjectDetailEntity detail) {
		model.setRowCount(0);
		model.setColumnCount(0);

		if (MunchkinUtil.isNotNull(detail)) {
			final List<String> columnNames = detail.getColumnNames();
			for (String name : columnNames) {
				model.addColumn(name);
			}

			final ColumnWidths widths = new ColumnWidths(columnNames.size(), 60);
			final FontMetrics fm = table.getFontMetrics(table.getFont());

			for (List<Object> record : detail.getRecords()) {
				model.addRow(record.toArray());

				for (int i = 0; i < columnNames.size(); i++) {
					final Object obj = record.get(i);
					widths.setWidth(i, (null != obj) ? fm.stringWidth(obj.toString()) : 0);
				}
			}

			final TableColumnModel mdlColumn = table.getColumnModel();
			for (int i = 0; i < columnNames.size(); i++) {
				final TableColumn column = mdlColumn.getColumn(i);
				column.setCellRenderer(renderer);
				column.setPreferredWidth(widths.getWidth(i) + 4 + 2);
			}
		}
	}

	private void doFiltering(final String text) {
		try {
			final RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter(text, 0);
			sorter.setRowFilter(filter);
		} catch (Exception ex) {
			LOGGER.error("Object detail filtering error.", ex);
		}
	}

	private void initLayout() {
		setBorder(new EmptyBorder(4, 4, 4, 4));

		setLayout(new BorderLayout(0, 4));
		add(BorderLayout.NORTH, txtFilter);
		add(BorderLayout.CENTER, new JScrollPane(table));

		txtFilter.setPreferredSize(new Dimension(0, 24));
	}

	private void initEvent() {
		txtFilter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				doFiltering(txtFilter.getText());
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				doFiltering(txtFilter.getText());
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (MouseEvent.BUTTON1 == e.getButton() && 2 == e.getClickCount()) {
					final Point pt = e.getPoint();
					final int row = table.rowAtPoint(pt);
					if (0 <= row) {
						final int rowReal = table.convertRowIndexToModel(row);

						// TODO:
					}
				}
			}
		});
	}

	private static class DetailTableCellRenderer extends DefaultTableCellRenderer {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private DetailTableCellRenderer() {
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}

			setFont(table.getFont());
			if (MunchkinUtil.isNull(value)) {
				setText("");
			} else {
				setText(value.toString());
			}

			return this;
		}
	}
}
