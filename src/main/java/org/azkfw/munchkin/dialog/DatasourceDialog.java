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
package org.azkfw.munchkin.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.azkfw.munchkin.entity.DatabaseDriver;
import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 */
public class DatasourceDialog extends JDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = 6049445841465203910L;

	private final List<DatasourceDialogListener> listeners;

	private final JLabel lblName;
	private final JTextField txtNmae;
	private final JLabel lblUser;
	private final JTextField txtUser;
	private final JLabel lblPass;
	private final JPasswordField txtPass;
	private final JLabel lblDriver;
	private final JComboBox<DatabaseDriver> cmbDriver;
	private final JLabel lblURL;
	private final JTextField txtURLSample;
	private final JTextPane txtURL;

	private final JButton btnOK;
	private final JButton btnCancel;
	private final JButton btnTest;

	/**
	 * コンストラクタ
	 *
	 * @param owner 親フレーム
	 */
	public DatasourceDialog(final Frame owner) {
		this(owner, null);
	}

	/**
	 * コンストラクタ
	 *
	 * @param owner 親フレーム
	 * @param datasource データソース
	 */
	public DatasourceDialog(final Frame owner, final DatasourceEntity datasource) {
		super(owner, true);

		setTitle("新しいデータベース接続");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		listeners = new ArrayList<DatasourceDialogListener>();

		final JPanel pnlMain = new JPanel();
		final GroupLayout layout = new GroupLayout(pnlMain);
		pnlMain.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		lblName = new JLabel("名前");
		txtNmae = new JTextField();
		lblUser = new JLabel("ユーザID");
		txtUser = new JTextField();
		lblPass = new JLabel("パスワード");
		txtPass = new JPasswordField();
		lblDriver = new JLabel("ドライバ");
		cmbDriver = new JComboBox<DatabaseDriver>();
		lblURL = new JLabel("接続文字列");
		txtURLSample = new JTextField();
		txtURL = new JTextPane();

		txtURLSample.setEditable(false);

		final JComponent[][] compos = { { lblName, txtNmae }, { lblUser, txtUser }, { lblPass, txtPass },
				{ lblDriver, cmbDriver }, { null, txtURLSample }, { lblURL, new JScrollPane(txtURL) } };
		set(layout, compos);

		final JPanel pnlControl = new JPanel();
		pnlControl.setLayout(null);
		pnlControl.setPreferredSize(new Dimension(0, 40));

		btnOK = new JButton("OK");
		btnOK.setSize(120, 30);
		btnCancel = new JButton("キャンセル");
		btnCancel.setSize(120, 30);
		btnTest = new JButton("接続テスト");
		btnTest.setSize(120, 30);
		pnlControl.add(btnOK);
		pnlControl.add(btnCancel);
		pnlControl.add(btnTest);

		add(BorderLayout.CENTER, pnlMain);
		add(BorderLayout.SOUTH, pnlControl);

		for (DatabaseDriver driver : DatabaseDriver.values()) {
			cmbDriver.addItem(driver);
		}
		cmbDriver.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final DatabaseDriver driver = (DatabaseDriver) cmbDriver.getSelectedItem();
					txtURLSample.setText(driver.getTemplate());
				}
			}
		});

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doOK();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doCancel();
			}
		});
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doTest();
			}
		});

		pnlControl.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				final Insets insets = pnlControl.getInsets();
				final int width = pnlControl.getWidth() - (insets.left + insets.right);
				btnCancel.setLocation(width - (btnCancel.getWidth() + 6), 4);
				btnOK.setLocation(width - (btnOK.getWidth() + 6 + btnCancel.getWidth() + 6), 4);
				btnTest.setLocation(width
						- (btnTest.getWidth() + 100 + btnOK.getWidth() + 6 + btnCancel.getWidth() + 6), 4);
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				doCancel();
			}

			@Override
			public void windowClosed(final WindowEvent e) {
			}
		});

		setDatasource(datasource);
		setSize(600, 320);
	}

	/**
	 * データソースダイアログイベントリスナーを追加する。
	 *
	 * @param listener リスナー
	 */
	public synchronized void addDatasourceDialogListener(final DatasourceDialogListener listener) {
		listeners.add(listener);
	}

	/**
	 * データソースを設定する。
	 *
	 * @param datasource データソース
	 */
	private void setDatasource(final DatasourceEntity datasource) {
		if (MunchkinUtil.isNull(datasource)) {
			txtNmae.setText("");
			txtUser.setText("");
			txtPass.setText("");
			cmbDriver.setSelectedIndex(0);
			txtURL.setText("");
		} else {
			txtNmae.setText(datasource.getName());
			txtUser.setText(datasource.getUser());
			txtPass.setText(datasource.getPassword());
			int index = 0;
			for (int i = 0; i < cmbDriver.getItemCount(); i++) {
				final DatabaseDriver driver = cmbDriver.getItemAt(i);
				if (MunchkinUtil.isEquals(driver.getDriver(), datasource.getDriver())) {
					index = i;
					break;
				}
			}
			cmbDriver.setSelectedIndex(index);
			txtURL.setText(datasource.getUrl());
		}
	}

	/**
	 * データソースを取得する。
	 *
	 * @return データソース
	 */
	private DatasourceEntity getDatasource() {
		final String name = txtNmae.getText();
		final String user = txtUser.getText();
		final String password = new String(txtPass.getPassword());
		final String driver = ((DatabaseDriver) cmbDriver.getSelectedItem()).getDriver();
		final String url = txtURL.getText();

		final DatasourceEntity datasource = new DatasourceEntity();
		datasource.setName(name);
		datasource.setUser(user);
		datasource.setPassword(password);
		datasource.setDriver(driver);
		datasource.setUrl(url);

		return datasource;
	}

	private void doOK() {
		final DatasourceEntity datasource = getDatasource();

		listeners.forEach(l -> l.datasourceDialogOK(this, datasource));

		dispose();
	}

	private void doCancel() {

		listeners.forEach(l -> l.datasourceDialogCancel(this));

		dispose();
	}

	private void doTest() {
		final DatasourceEntity datasource = getDatasource();

		try {
			Class.forName(datasource.getDriver());
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "対象のドライバが見つかりません。\n" + datasource.getDriver(), "接続テスト",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean result = false;
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(datasource.getUrl(), datasource.getUser(),
					datasource.getPassword());

			result = true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "接続に失敗しました。\n" + e.getMessage(), "接続テスト", JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException ex) {

				}
			}
		}

		if (result) {
			JOptionPane.showMessageDialog(this, "接続に成功しました。", "接続テスト", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void set(final GroupLayout layout, final JComponent[][] compos) {
		int ny = compos.length;
		int nx = compos[0].length;
		{// 水平方向のグループ
			SequentialGroup hg = layout.createSequentialGroup();
			for (int x = 0; x < nx; x++) {
				ParallelGroup pg = layout.createParallelGroup();
				for (int y = 0; y < ny; y++) {
					JComponent c = compos[y][x];
					if (c != null) {
						pg.addComponent(c);
					}
				}
				hg.addGroup(pg);
			}
			layout.setHorizontalGroup(hg);
		}
		{// 垂直方向のグループ
			SequentialGroup vg = layout.createSequentialGroup();
			for (int y = 0; y < ny; y++) {
				ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
				for (int x = 0; x < nx; x++) {
					JComponent c = compos[y][x];
					if (c != null) {
						pg.addComponent(c);
					}
				}
				vg.addGroup(pg);
			}
			layout.setVerticalGroup(vg);
		}
	}
}
