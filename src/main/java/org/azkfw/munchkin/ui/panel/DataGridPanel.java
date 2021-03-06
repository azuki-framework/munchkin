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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import org.azkfw.munchkin.ui.ColumnWidths;
import org.azkfw.munchkin.ui.DataGridSelection;
import org.azkfw.munchkin.ui.TableDataCellEditorKit;
import org.azkfw.munchkin.ui.component.DataGridStringCell;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 * このクラスは、データグリッドパネルクラスです。
 * 
 * @author Kawakicchi
 */
public class DataGridPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 6307113807028151689L;

	/** listener */
	private final List<DataGridPanelListener> listeners;

	private final DefaultTableModel model;
	private final JTable table;

	private final Font font;
	private final FontMetrics fontMetrics;

	private long maxReadSize = 1000;

	public DataGridPanel() {
		listeners = new ArrayList<DataGridPanelListener>();

		setLayout(new BorderLayout());

		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			font = new Font("ＭＳ ゴシック", Font.PLAIN, 14);
		} else if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			font = new Font("Osaka-Mono", Font.PLAIN, 14);
		} else {
			font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
		}

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
		table.setFont(font);

		fontMetrics = table.getFontMetrics(font);

		table.setRowHeight(fontMetrics.getHeight() + 4 + 2);
		table.setSelectionBackground(new Color(255, 204, 153));

		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				if (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_C) {
					//e.consume();
				}
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_C) {
					//e.consume();
				}
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					doCopy();
					e.consume();
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (MouseEvent.BUTTON1 == e.getButton() && 2 == e.getClickCount()) {
					final Point pt = e.getPoint();
					final int row = table.rowAtPoint(pt);
					final int col = table.columnAtPoint(pt);
					if (0 <= row) {
						final int rowReal = table.convertRowIndexToModel(row);
						final int colReal = table.convertColumnIndexToModel(col);
						final Object object = model.getValueAt(rowReal, colReal);

						listeners.forEach(l -> l.dataGridPanelDoubleClick(object));
					}
				}
			}
		});

		final JPanel pnlControl = new JPanel();
		pnlControl.setPreferredSize(new Dimension(0, 32));

		add(BorderLayout.NORTH, pnlControl);
		add(BorderLayout.CENTER, new JScrollPane(table));
	}

	public synchronized void addDataGridPanelListener(final DataGridPanelListener listener) {
		listeners.add(listener);
	}

	public long getMaxReadSize() {
		return maxReadSize;
	}

	public synchronized void clearData() {
		model.setRowCount(0);
		model.setColumnCount(0);
	}

	public synchronized long setData(final ResultSet rs) throws SQLException {
		model.setRowCount(0);
		model.setColumnCount(0);

		rs.setFetchSize(1000);

		final ResultSetMetaData meta = rs.getMetaData();
		for (int col = 1; col <= meta.getColumnCount(); col++) {
			model.addColumn(meta.getColumnName(col));
		}

		final ColumnWidths widths = new ColumnWidths(meta.getColumnCount(), 60);

		final TableColumnModel mdlColumn = table.getColumnModel();
		for (int i = 0; i < mdlColumn.getColumnCount(); i++) {
			final boolean nullable = (ResultSetMetaData.columnNoNulls != meta.isNullable(i + 1));

			final int type = meta.getColumnType(i + 1);
			if (MunchkinUtil.isEqualsAny(type, Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE)) {
				final MyDateCellRenderer renderer = new MyDateCellRenderer(table, nullable);
				mdlColumn.getColumn(i).setCellRenderer(renderer);

			} else {
				final MyStringCellRenderer renderer = new MyStringCellRenderer(table, nullable);
				mdlColumn.getColumn(i).setCellRenderer(renderer);
			}
		}

		long readSize = 0;
		while (rs.next()) {
			if (readSize < maxReadSize) {
				final List<Object> record = new ArrayList<Object>();
				for (int i = 0; i < meta.getColumnCount(); i++) {
					Object obj = rs.getObject(i + 1);
					record.add(obj);

					final String str = (null != obj) ? obj.toString() : "(NULL)";
					final int width = fontMetrics.stringWidth(str);
					widths.setWidth(i, width);
				}
				model.addRow(record.toArray());
			} else {
				readSize = -1;
				break;
			}
			readSize++;
		}

		for (int i = 0; i < mdlColumn.getColumnCount(); i++) {
			mdlColumn.getColumn(i).setPreferredWidth(widths.getWidth(i) + 4 + 2);
		}

		return readSize;
	}

	private void doCopy() {
		int cnt = table.getSelectedRowCount();
		if (0 < cnt) {
			int rows[] = table.getSelectedRows();

			final List<String> columns = new ArrayList<String>();
			for (int col = 0; col < model.getColumnCount(); col++) {
				columns.add(model.getColumnName(col));
			}

			final List<List<String>> records = new ArrayList<List<String>>();
			for (int row : rows) {
				final List<String> record = new ArrayList<String>();
				int index = table.convertRowIndexToModel(row);
				for (int col = 0; col < model.getColumnCount(); col++) {
					final Object obj = model.getValueAt(index, col);
					if (null == obj) {
						record.add(null);
					} else {
						record.add(obj.toString());
					}
				}
				records.add(record);
			}

			final DataGridSelection selection = new DataGridSelection(columns, records);

			final Toolkit kit = Toolkit.getDefaultToolkit();
			final Clipboard clip = kit.getSystemClipboard();
			clip.setContents(selection, selection);
		}
	}

	private static class MyStringCellRenderer extends AbstractCellRenderer {
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private final SimpleAttributeSet asString;
		private final SimpleAttributeSet asNumber;
		private final SimpleAttributeSet asNull;

		public MyStringCellRenderer(final JTable table, final boolean nullable) {
			super(table, nullable, new TableDataCellEditorKit());

			asString = new SimpleAttributeSet();
			StyleConstants.setForeground(asString, table.getForeground());
			StyleConstants.setAlignment(asString, StyleConstants.ALIGN_LEFT);
			StyleConstants.setFontFamily(asString, getFont().getFamily());
			StyleConstants.setFontSize(asString, getFont().getSize());
			asNumber = new SimpleAttributeSet();
			StyleConstants.setForeground(asNumber, table.getForeground());
			StyleConstants.setAlignment(asNumber, StyleConstants.ALIGN_RIGHT);
			StyleConstants.setFontFamily(asNumber, getFont().getFamily());
			StyleConstants.setFontSize(asNumber, getFont().getSize());

			asNull = new SimpleAttributeSet();
			StyleConstants.setForeground(asNull, new Color(65, 105, 225));
			StyleConstants.setFontFamily(asNull, getFont().getFamily());
			StyleConstants.setBold(asNull, true);
			StyleConstants.setFontSize(asNull, getFont().getSize());
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {

			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(getBackground(row));
			}

			if (null == value) {
				setText("(NULL)");
				setAttri(asNull);
			} else {
				setText(value.toString());
				if (value instanceof Short) {
					setAttri(asNumber);
				} else if (value instanceof Integer) {
					setAttri(asNumber);
				} else if (value instanceof Long) {
					setAttri(asNumber);
				} else if (value instanceof Float) {
					setAttri(asNumber);
				} else if (value instanceof Double) {
					setAttri(asNumber);
				} else if (value instanceof BigDecimal) {
					setAttri(asNumber);
				} else {
					setAttri(asString);
				}
			}
			return this;
		}

		private void setAttri(final SimpleAttributeSet as) {
			getStyledDocument().setParagraphAttributes(0, getDocument().getLength(), as, true);
		}
	}

	private static class MyDateCellRenderer extends AbstractCellRenderer {
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private final SimpleAttributeSet asString;
		private final SimpleAttributeSet asNumber;
		private final SimpleAttributeSet asNull;

		public MyDateCellRenderer(final JTable table, final boolean nullable) {
			super(table, nullable, null);

			asString = new SimpleAttributeSet();
			StyleConstants.setForeground(asString, table.getForeground());
			StyleConstants.setAlignment(asString, StyleConstants.ALIGN_LEFT);
			StyleConstants.setFontFamily(asString, getFont().getFamily());
			StyleConstants.setFontSize(asString, getFont().getSize());
			asNumber = new SimpleAttributeSet();
			StyleConstants.setForeground(asNumber, table.getForeground());
			StyleConstants.setAlignment(asNumber, StyleConstants.ALIGN_RIGHT);
			StyleConstants.setFontFamily(asNumber, getFont().getFamily());
			StyleConstants.setFontSize(asNumber, getFont().getSize());

			asNull = new SimpleAttributeSet();
			StyleConstants.setForeground(asNull, new Color(65, 105, 225));
			StyleConstants.setFontFamily(asNull, getFont().getFamily());
			StyleConstants.setBold(asNull, true);
			StyleConstants.setFontSize(asNull, getFont().getSize());
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {

			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(getBackground(row));
			}

			if (null == value) {
				setText("(NULL)");
				setAttri(asNull);
			} else {
				setText(value.toString());
				if (value instanceof Short) {
					setAttri(asNumber);
				} else if (value instanceof Integer) {
					setAttri(asNumber);
				} else if (value instanceof Long) {
					setAttri(asNumber);
				} else if (value instanceof Float) {
					setAttri(asNumber);
				} else if (value instanceof Double) {
					setAttri(asNumber);
				} else if (value instanceof BigDecimal) {
					setAttri(asNumber);
				} else {
					setAttri(asString);
				}
			}
			return this;
		}

		private void setAttri(final SimpleAttributeSet as) {
			getStyledDocument().setParagraphAttributes(0, getDocument().getLength(), as, true);
		}
	}

	private static abstract class AbstractCellRenderer extends DataGridStringCell implements TableCellRenderer {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private final Color colorLineBackground1;
		private final Color colorLineBackground2;

		public AbstractCellRenderer(final JTable table, final boolean nullable, final StyledEditorKit kit) {
			super(kit);

			setFont(table.getFont());
			setOpaque(true);

			if (nullable) {
				colorLineBackground1 = new Color(255, 255, 255);
				colorLineBackground2 = new Color(255, 239, 224);
			} else {
				colorLineBackground1 = new Color(255, 255, 183);
				colorLineBackground2 = new Color(255, 235, 137);
			}
		}

		protected final Color getBackground(final int row) {
			Color color = null;
			if (0 == row % 2) {
				color = colorLineBackground1;
			} else {
				color = colorLineBackground2;
			}
			return color;
		}
	}
}
