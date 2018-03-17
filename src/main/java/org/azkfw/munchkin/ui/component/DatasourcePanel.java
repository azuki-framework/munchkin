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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.azkfw.munchkin.entity.DatabaseDriver;
import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 */
public class DatasourcePanel extends GridLayoutPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -8734888816989779865L;

	private final JLabel lblName;
	private final JLabel lblColor;
	private final JLabel lblMemo;
	private final JTextField txtName;
	private final JTextField txtColor;
	private final JButton btnColor;
	private final JTextPane txtMemo;

	private final JLabel lblUser;
	private final JLabel lblPassword;
	private final JLabel lblDriver;
	private final JLabel lblUrl;
	private final JTextField txtUser;
	private final JPasswordField txtPassword;
	private final DatabaseDriverComboBox cmbDriver;
	private final JTextPane txtUrl;

	private final DefaultComboBoxModel<DatabaseDriver> mdlDriver;

	public DatasourcePanel() {

		mdlDriver = new DefaultComboBoxModel<DatabaseDriver>();

		lblName = new JLabel("名前:");
		lblColor = new JLabel("カラー:");
		lblMemo = new JLabel("メモ:");
		txtName = new JTextField("");
		txtColor = new JTextField();
		btnColor = new JButton("選択");
		txtMemo = new JTextPane();

		lblUser = new JLabel("ユーザ名:");
		lblPassword = new JLabel("パスワード:");
		lblDriver = new JLabel("ドライバ:");
		lblUrl = new JLabel("接続文字列:");
		txtUser = new JTextField("");
		txtPassword = new JPasswordField();
		cmbDriver = new DatabaseDriverComboBox(mdlDriver);
		txtUrl = new JTextPane();

		final JPanel pnlColor = new JPanel();
		pnlColor.setMaximumSize(new Dimension(200, 24));
		pnlColor.setLayout(new BorderLayout());
		pnlColor.add(BorderLayout.CENTER, txtColor);
		pnlColor.add(BorderLayout.EAST, btnColor);

		final JPanel space = new JPanel();
		space.setMaximumSize(new Dimension(16, 16));

		for (DatabaseDriver driver : DatabaseDriver.values()) {
			mdlDriver.addElement(driver);
		}

		final JComponent[][] components = { { lblName, txtName }, { lblColor, pnlColor },
				{ lblMemo, new JScrollPane(txtMemo) }, { space, null }, { lblUser, txtUser },
				{ lblPassword, txtPassword }, { lblDriver, cmbDriver }, { lblUrl, new JScrollPane(txtUrl) } };
		setComponents(components);

		settingEvent();
	}

	public void setDatasource(final DatasourceEntity e) {
		txtName.setText(e.getName());
		txtColor.setText(e.getColorHexString());
		txtMemo.setText(e.getMemo());

		txtUser.setText(e.getUser());
		txtPassword.setText(e.getPassword());
		cmbDriver.setSelectDatabaseDriver(DatabaseDriver.getDriver(e.getDriver()));
		txtUrl.setText(e.getUrl());
	}

	public DatasourceEntity getDatasource() {
		final DatasourceEntity e = new DatasourceEntity();
		e.setName(txtName.getText());
		e.setColorHexString(txtColor.getText());
		e.setMemo(txtMemo.getText());

		e.setUser(txtUser.getText());
		e.setPassword(new String(txtPassword.getPassword()));
		e.setDriver(cmbDriver.getSelectDatabaseDriver().getDriver());
		e.setUrl(txtUrl.getText());
		return e;
	}

	public void setEditable(final boolean b) {
		txtName.setEnabled(b);
		txtColor.setEnabled(b);
		btnColor.setEnabled(b);
		txtMemo.setEnabled(b);

		txtUser.setEnabled(b);
		txtPassword.setEnabled(b);
		cmbDriver.setEnabled(b);
		txtUrl.setEnabled(b);
	}

	private void doSelectColor() {
		final Color color = MunchkinUtil.getColorFormHexadecimal(txtColor.getText());
		final Color selectColor = JColorChooser.showDialog(this, "カラー選択", color);
		if (MunchkinUtil.isNotNull(selectColor)) {
			txtColor.setText(MunchkinUtil.getHexadecimalFromColor(selectColor));
		}
	}

	private void doChangeColor() {
		txtColor.setBackground(MunchkinUtil.getColorFormHexadecimal(txtColor.getText()));
	}

	private void settingEvent() {
		btnColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doSelectColor();
			}
		});
		txtColor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(final DocumentEvent e) {
				doChangeColor();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				doChangeColor();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				doChangeColor();
			}
		});
	}
}
