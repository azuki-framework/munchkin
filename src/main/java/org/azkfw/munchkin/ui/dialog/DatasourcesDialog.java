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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
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
public class DatasourcesDialog extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = -3202118104324325403L;

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(DatasourcesDialog.class);

	public static void main(final String[] args) {
		List<DatasourceEntity> datasources = new ArrayList<DatasourceEntity>();
		for (int i = 0; i < 5; i++) {
			final DatasourceEntity datasource = new DatasourceEntity();
			datasource.setName(String.format("データソース-%02d", (i + 1)));
			datasource.setUser(String.format("ユーザ-%02d", (i + 1)));
			datasources.add(datasource);
		}

		DatasourcesDialog dlg = new DatasourcesDialog(datasources);
		dlg.setVisible(true);
	}

	private final DefaultListModel<DatasourceEntity> model;

	private final JList<DatasourceEntity> lstDatasource;
	private final DatasourcePanel pnlDatasource;

	private final JButton btnCreate;
	private final JButton btnDelete;
	private final JButton btnTest;
	private final JButton btnOK;
	private final JButton btnCancel;

	public DatasourcesDialog(final List<DatasourceEntity> datasources) {
		setTitle("データソース一覧");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // XXX

		model = new DefaultListModel<DatasourceEntity>();

		lstDatasource = new JList<DatasourceEntity>(model);
		lstDatasource.setCellRenderer(new DatasourceCellRenderer());

		pnlDatasource = new DatasourcePanel();

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
			model.addElement(datasource);
		}
		lstDatasource.setSelectedIndex(0);

		setSize(800, 600);
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
		lstDatasource.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				doChangeDatasource();
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
