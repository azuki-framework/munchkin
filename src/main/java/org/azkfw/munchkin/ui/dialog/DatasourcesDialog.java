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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.ui.component.DatasourcePanel;
import org.azkfw.munchkin.util.MunchkinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kawakicchi
 */
public class DatasourcesDialog extends JDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = -3202118104324325403L;

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(DatasourcesDialog.class);

	private final DefaultListModel<DatasourceEntity> model;

	private final JList<DatasourceEntity> lstDatasource;
	private final DatasourcePanel pnlDatasource;

	/** イベントリスナー */
	private final List<DatasourcesDialogListener> listeners;

	private final JButton btnCreate;
	private final JButton btnDelete;
	private final JButton btnTest;
	private final JButton btnOK;
	private final JButton btnCancel;

	public DatasourcesDialog(final Frame owner, final List<DatasourceEntity> datasources) {
		super(owner, true);
		setTitle("データソース一覧");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		model = new DefaultListModel<DatasourceEntity>();

		lstDatasource = new JList<DatasourceEntity>(model);
		lstDatasource.setCellRenderer(new DatasourceCellRenderer());

		pnlDatasource = new DatasourcePanel(this);

		listeners = new ArrayList<DatasourcesDialogListener>();

		btnCreate = new JButton("+");
		btnDelete = new JButton("-");
		btnTest = new JButton("接続テスト");
		btnTest.setPreferredSize(new Dimension(100, 24));
		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(100, 24));
		btnCancel = new JButton("キャンセル");
		btnCancel.setPreferredSize(new Dimension(100, 24));

		settingLayout();
		settingEvent();

		for (DatasourceEntity datasource : datasources) {
			model.addElement(new DatasourceEntity(datasource));
		}
		if (MunchkinUtil.isNotEmpty(datasources)) {
			lstDatasource.setSelectedIndex(0);
		}

		setSize(800, 600);

		final int offsetX = owner.getX() + (owner.getWidth() - getWidth()) / 2;
		final int offsetY = owner.getY() + (owner.getHeight() - getHeight()) / 2;
		setLocation(offsetX, offsetY);
	}

	public synchronized void addDatasourcesDialogListener(final DatasourcesDialogListener listener) {
		listeners.add(listener);
	}

	private void doCreateDatasource() {
		final DatasourceEntity datasource = new DatasourceEntity();
		datasource.setName("新しいデータソース");

		model.addElement(datasource);
		lstDatasource.setSelectedIndex(model.getSize() - 1);
	}

	private void doDeleteDatasource() {
		final int index = lstDatasource.getSelectedIndex();
		if (-1 != index) {
			model.remove(index);
			if (model.size() > index) {
				lstDatasource.setSelectedIndex(index);
			} else {
				lstDatasource.setSelectedIndex(index - 1);
			}
		}
	}

	private void doChangeDatasource() {
		final DatasourceEntity datasource = lstDatasource.getSelectedValue();
		if (datasource != null) {
			pnlDatasource.setDatasource(datasource);
		}
	}

	private void doConnectionTest() {
		final DatasourceEntity datasource = pnlDatasource.getDatasource();

		Connection c = null;
		try {
			Class.forName(datasource.getDriver());
			c = DriverManager.getConnection(datasource.getUrl(), datasource.getUser(), datasource.getPassword());

			JOptionPane.showMessageDialog(this, "接続に成功しました。", "接続テスト", JOptionPane.INFORMATION_MESSAGE);
		} catch (ClassNotFoundException ex) {
			JOptionPane.showMessageDialog(this, "接続に失敗しました。\nClass not found. [" + ex.getMessage() + "]", "接続テスト",
					JOptionPane.ERROR_MESSAGE);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "接続に失敗しました。\n" + ex.getMessage(), "接続テスト", JOptionPane.ERROR_MESSAGE);
		} finally {
			MunchkinUtil.release(c);
		}
	}

	private void doClosedOk() {
		final List<DatasourceEntity> datasources = new ArrayList<DatasourceEntity>();
		for (int i = 0; i < model.getSize(); i++) {
			datasources.add(model.get(i));
		}

		listeners.forEach(l -> l.datasourcesDialogClosedOk(this, datasources));

		dispose();
	}

	private void doClosedCancel() {

		listeners.forEach(l -> l.datasourcesDialogClosedCancel(this));

		dispose();
	}

	private void settingLayout() {
		setLayout(new BorderLayout());

		final JPanel pnlCommand1 = new JPanel();
		pnlCommand1.setPreferredSize(new Dimension(0, 40));
		pnlCommand1.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pnlCommand1.add(btnCreate);
		pnlCommand1.add(btnDelete);

		final JPanel pnlCommand2 = new JPanel();
		pnlCommand2.setPreferredSize(new Dimension(0, 40));
		pnlCommand2.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pnlCommand2.add(btnTest);

		final JPanel pnlLeft = new JPanel();
		pnlLeft.setBorder(new EmptyBorder(4, 4, 4, 4));
		pnlLeft.setLayout(new BorderLayout());
		pnlLeft.add(BorderLayout.CENTER, new JScrollPane(lstDatasource));
		pnlLeft.add(BorderLayout.SOUTH, pnlCommand1);

		final JPanel pnlRight = new JPanel();
		pnlRight.setLayout(new BorderLayout());
		pnlRight.add(BorderLayout.CENTER, pnlDatasource);
		pnlRight.add(BorderLayout.SOUTH, pnlCommand2);

		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(pnlLeft);
		split.setRightComponent(pnlRight);
		split.setDividerLocation(200);

		final JPanel pnlFooter = new JPanel();
		pnlFooter.setPreferredSize(new Dimension(0, 40));
		pnlFooter.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlFooter.add(btnOK);
		pnlFooter.add(btnCancel);

		add(BorderLayout.CENTER, split);
		add(BorderLayout.SOUTH, pnlFooter);
	}

	private void settingEvent() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent e) {
			}

			@Override
			public void windowClosing(final WindowEvent e) {
				doClosedCancel();
			}

			@Override
			public void windowClosed(final WindowEvent e) {
			}
		});

		lstDatasource.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				doChangeDatasource();
			}
		});

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doClosedOk();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doClosedCancel();
			}
		});

		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doCreateDatasource();
			}
		});
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doDeleteDatasource();
			}
		});
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doConnectionTest();
			}
		});
	}

	private class DatasourceCellRenderer extends JLabel implements ListCellRenderer<DatasourceEntity> {

		/** serialVersionUID */
		private static final long serialVersionUID = -4832998746832847774L;

		public DatasourceCellRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(final JList<? extends DatasourceEntity> list,
				final DatasourceEntity value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			setText(String.format("%s [%s]", value.getName(), value.getUser()));

			if (isSelected) {
				setBackground(Color.orange);
				setForeground(Color.black);
			} else {
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}
}
