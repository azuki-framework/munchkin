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
package org.azkfw.munchkin.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.azkfw.munchkin.database.model.DatabaseModel;
import org.azkfw.munchkin.database.model.entity.ObjectEntity;
import org.azkfw.munchkin.database.model.entity.TypeEntity;
import org.azkfw.munchkin.ui.ColumnWidths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class ExportDialog extends AbstractDialog {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportDialog.class);

	private final DatabaseModel model;

	private final TableRowSorter<DefaultTableModel> sorter;

	private final DefaultTableModel mdlTable;

	private final JTextField txtFilter;
	private final JTable tblTable;

	private final JTabbedPane tabOption;
	private final JTextField txtOutputFile;
	private final JButton btnOutputFile;

	private final JButton btnExport;

	private Boolean busy;

	public ExportDialog(final Frame owner, final DatabaseModel model) {
		super(owner);
		setTitle("エクスポート");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		busy = Boolean.FALSE;

		this.model = model;

		txtFilter = new JTextField();

		mdlTable = new DefaultTableModel() {
			/** serialVersionUID */
			private static final long serialVersionUID = 3376972715529108216L;

			@Override
			public Class<?> getColumnClass(final int col) {
				return getValueAt(0, col).getClass();
			}

			@Override
			public boolean isCellEditable(final int row, final int column) {
				if (1 == column) {
					return true;
				}
				return false;
			}
		};
		mdlTable.addColumn("");
		mdlTable.addColumn("");
		mdlTable.addColumn("スキーマ");
		mdlTable.addColumn("テーブル");
		mdlTable.addColumn("テーブル");

		sorter = new TableRowSorter<DefaultTableModel>(mdlTable);

		tblTable = new JTable(mdlTable);
		tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblTable.setRowSorter(sorter);

		tabOption = new JTabbedPane();

		txtOutputFile = new JTextField();
		btnOutputFile = new JButton("選択");

		btnExport = new JButton("Export");

		settingLayout();
		settingEvent();

		final TableColumnModel cm = tblTable.getColumnModel();
		cm.removeColumn(cm.getColumn(0));

		setSize(900, 600);
		moveParentCenter();
	}

	private void doInit() {
		synchronized (busy) {
			if (busy) {
				return;
			}
			busy = Boolean.TRUE;
		}

		mdlTable.setRowCount(0);

		final Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					final TypeEntity type = new TypeEntity("TABLE", "TABLE");
					final List<ObjectEntity> objects = model.getObjectList(null, type);

					final ColumnWidths widths = new ColumnWidths(3, 80);
					final FontMetrics fm = tblTable.getFontMetrics(tblTable.getFont());

					for (ObjectEntity obj : objects) {
						final Object[] objs = new Object[5];
						objs[0] = obj;
						objs[1] = Boolean.FALSE;
						objs[2] = s(obj.getSchema());
						objs[3] = s(obj.getName());
						objs[4] = s(obj.getLabel());

						widths.setWidth(0, fm.stringWidth(s(obj.getSchema())) + 4 + 2);
						widths.setWidth(1, fm.stringWidth(s(obj.getName())) + 4 + 2);
						widths.setWidth(2, fm.stringWidth(s(obj.getLabel())) + 4 + 2);

						mdlTable.addRow(objs);
					}

					final TableColumnModel cm = tblTable.getColumnModel();
					cm.getColumn(0).setPreferredWidth(32);
					cm.getColumn(1).setPreferredWidth(widths.getWidth(0));
					cm.getColumn(2).setPreferredWidth(widths.getWidth(1));
					cm.getColumn(3).setPreferredWidth(widths.getWidth(2));

				} catch (SQLException ex) {
					LOGGER.error("", ex);
				}

				busy = Boolean.FALSE;
			}
		};
		SwingUtilities.invokeLater(r);
	}

	private void doFiltering(final String text) {
		RowFilter<DefaultTableModel, Object> filter = null;
		try {
			filter = RowFilter.regexFilter(text, 2, 3, 4);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sorter.setRowFilter(filter);
	}

	private String s(final String string) {
		String s = "";
		if (null != string) {
			s = string;
		}
		return s;
	}

	private void settingLayout() {

		final JPanel pnlFooter = new JPanel();
		pnlFooter.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlFooter.setPreferredSize(new Dimension(0, 40));

		pnlFooter.add(btnExport);

		final JPanel pnlFilter = new JPanel();
		pnlFilter.setLayout(new BorderLayout());
		pnlFilter.setBorder(new EmptyBorder(2, 2, 2, 2));
		pnlFilter.add(BorderLayout.WEST, new JLabel("フィルター : "));
		pnlFilter.add(BorderLayout.CENTER, txtFilter);

		final JPanel pnlTable = new JPanel();
		pnlTable.setLayout(new BorderLayout());
		pnlTable.add(BorderLayout.NORTH, pnlFilter);
		pnlTable.add(BorderLayout.CENTER, new JScrollPane(tblTable));

		final JPanel pnlFormat = new JPanel();
		final JPanel pnlStyle = new JPanel();

		tabOption.add("テーブル", pnlTable);
		tabOption.add("データ書式", pnlFormat);
		tabOption.add("スタイル", pnlStyle);

		final JPanel pnlOutputFile = new JPanel();
		pnlOutputFile.setLayout(new BorderLayout());
		pnlOutputFile.setBorder(new EmptyBorder(2, 6, 2, 6));
		pnlOutputFile.add(BorderLayout.WEST, new JLabel("出力ファイル : "));
		pnlOutputFile.add(BorderLayout.CENTER, txtOutputFile);
		pnlOutputFile.add(BorderLayout.EAST, btnOutputFile);

		final JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(BorderLayout.CENTER, tabOption);
		pnlMain.add(BorderLayout.SOUTH, pnlOutputFile);

		add(BorderLayout.CENTER, pnlMain);
		add(BorderLayout.SOUTH, pnlFooter);
	}

	private void settingEvent() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent e) {
				doInit();
			}

			@Override
			public void windowClosing(final WindowEvent e) {

			}

			@Override
			public void windowClosed(final WindowEvent e) {

			}
		});

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
	}
}
