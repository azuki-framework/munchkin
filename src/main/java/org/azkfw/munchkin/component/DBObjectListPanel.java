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
package org.azkfw.munchkin.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.azkfw.munchkin.database.model.ObjectEntity;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 */
public class DBObjectListPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 84902710794456375L;

	/** listener */
	private final List<DBObjectListPanelListener> listeners;

	private final DefaultTableModel model;
	private final TableRowSorter<DefaultTableModel> sorter;

	private final MyTableCellRenderer renderer;

	private final JTextField txtFilter;
	private final JTable table;

	public DBObjectListPanel() {
		listeners = new ArrayList<DBObjectListPanelListener>();

		setLayout(new BorderLayout(0, 4));
		setBorder(new EmptyBorder(4, 4, 4, 4));

		model = new DefaultTableModel() {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		};
		model.addColumn("ID");
		model.addColumn("名前");
		model.addColumn("コメント");
		model.addColumn("タイプ");

		sorter = new TableRowSorter<DefaultTableModel>(model);

		txtFilter = new JTextField();
		txtFilter.setPreferredSize(new Dimension(0, 24));
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
				doFiltering(txtFilter.getText());
			}
		});

		renderer = new MyTableCellRenderer();

		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setSelectionBackground(new Color(255, 204, 153));
		table.setSelectionForeground(table.getForeground());
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.getColumnModel().getColumn(3).setCellRenderer(renderer);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				ObjectEntity obj = null;
				if (0 < table.getSelectedRowCount()) {
					int i = table.getSelectedRow();
					int index = table.convertRowIndexToModel(i);
					obj = (ObjectEntity) model.getValueAt(index, 0);
				}
				doChangeObject(obj);
			}
		});

		table.removeColumn(table.getColumn("ID"));

		add(BorderLayout.NORTH, txtFilter);
		add(BorderLayout.CENTER, new JScrollPane(table));
	}

	public synchronized void addDBObjectListPanelListener(final DBObjectListPanelListener listener) {
		listeners.add(listener);
	}

	public void setObjectList(final List<ObjectEntity> objects) {
		model.setRowCount(0);

		if (MunchkinUtil.isNotEmpty(objects)) {

			final ColumnWidths widths = new ColumnWidths(3, 60);
			final FontMetrics fm = table.getFontMetrics(table.getFont());

			objects.forEach(obj -> {
				final List<Object> objs = new ArrayList<Object>();
				final String name = obj.getName();
				final String label = obj.getLabel();
				final String type = obj.getType();

				objs.add(obj);
				objs.add(name);
				objs.add(label);
				objs.add(type);
				model.addRow(objs.toArray());

				widths.setWidth(0, (null != name) ? fm.stringWidth(name) : 0);
				widths.setWidth(1, (null != label) ? fm.stringWidth(label) : 0);
				widths.setWidth(2, (null != type) ? fm.stringWidth(type) : 0);
			});

			final TableColumnModel mdlColumn = table.getColumnModel();
			for (int i = 0; i < 3; i++) {
				final TableColumn column = mdlColumn.getColumn(i);
				column.setPreferredWidth(widths.getWidth(i) + 4 + 2);
			}
		}
	}

	private void doFiltering(final String text) {
		RowFilter<DefaultTableModel, Object> filter = null;
		try {
			filter = RowFilter.regexFilter(text, 1);
		} catch (Exception ex) {
		}
		sorter.setRowFilter(filter);
	}

	private void doChangeObject(final ObjectEntity object) {
		listeners.forEach(l -> l.dbObjectListPanelChengedObject(object));
	}

	private class MyTableCellRenderer extends DefaultTableCellRenderer {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private final Color color;

		private MyTableCellRenderer() {
			color = new Color(255, 239, 224);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				if (0 == row % 2) {
					setBackground(table.getBackground());
				} else {
					setBackground(color);
				}
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
