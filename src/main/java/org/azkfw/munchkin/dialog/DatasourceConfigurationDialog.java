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

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.message.Label;
import org.azkfw.munchkin.util.MunchkinUtil;

public class DatasourceConfigurationDialog extends SimpleConfigurationDialog<DatasourceEntity> {

	/** serialVersionUID */
	private static final long serialVersionUID = -2041708888646150082L;

	private JTextField txtName;
	private JTextField txtUser;
	private JTextField txtPassword;
	private JComboBox<String> cmbDriver;
	private JTextArea txtUrl;

	/**
	 * コンストラクタ
	 * 
	 * @param frame sss
	 */
	public DatasourceConfigurationDialog(final Frame frame) {
		super(frame);
	}

	public DatasourceConfigurationDialog(final Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInit() {
		super.doInit();

		setTitle(Label.TITLE_DATASOURCE_CONFIG.toString());

		setSize(600, 400);
		moveCenter(getParent());
	}

	@Override
	protected void initComponent(final SettingComponent setting) {
		txtName = new JTextField();
		txtUser = new JTextField();
		txtPassword = new JTextField();
		cmbDriver = new JComboBox<String>();
		txtUrl = new JTextArea();

		cmbDriver.addItem("oracle.jdbc.driver.OracleDriver");
		cmbDriver.addItem("org.postgresql.Driver");
		cmbDriver.addItem("com.mysql.jdbc.Driver");

		setting.add(Label.DATASOURCE_NAME.toString(), txtName);
		setting.add(Label.DATASOURCE_USER.toString(), txtUser);
		setting.add(Label.DATASOURCE_PASSWORD.toString(), txtPassword);
		setting.add(Label.DATASOURCE_DRIVER.toString(), cmbDriver);
		setting.add(Label.DATASOURCE_URL.toString(), new JScrollPane(txtUrl));
	}

	@Override
	protected void setEntity(final DatasourceEntity entity) {
		txtName.setText(entity.getName());
		txtUser.setText(entity.getUser());
		txtPassword.setText(entity.getPassword());
		cmbDriver.setSelectedItem(entity.getDriver());
		txtUrl.setText(entity.getUrl());
	}

	@Override
	protected DatasourceEntity getEntity() {
		DatasourceEntity entity = new DatasourceEntity();
		entity.setName(txtName.getText());
		entity.setUser(txtUser.getText());
		entity.setPassword(txtPassword.getText());
		entity.setDriver(cmbDriver.getSelectedItem().toString());
		entity.setUrl(txtUrl.getText());
		return entity;
	}

	@Override
	protected boolean check(final DatasourceEntity entity) {
		if (MunchkinUtil.isEmpty(entity.getName())) {
			return false;
		}
		if (MunchkinUtil.isEmpty(entity.getUser())) {
			return false;
		}
		if (MunchkinUtil.isEmpty(entity.getPassword())) {
			return false;
		}
		if (MunchkinUtil.isEmpty(entity.getUrl())) {
			return false;
		}
		return true;
	}
}
