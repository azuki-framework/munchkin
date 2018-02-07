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
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.message.Label;

/**
 *
 * @author Kawakicchi
 */
public class DatasourceConfigurationListDialog extends SimpleConfigurationListDialog<DatasourceEntity> {

	/** serialVersionUID */
	private static final long serialVersionUID = -4704920138988160544L;

	private DefaultTableModel model;

	public DatasourceConfigurationListDialog(final Frame frame) {
		super(frame);
	}

	public DatasourceConfigurationListDialog(final Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInit() {
		super.doInit();

		setTitle(Label.TITLE_DATASOURCE_CONFIG.toString());

		setSize(800, 600);
		moveCenter(getParent());
	}

	@Override
	protected ConfigurationDialog<DatasourceEntity> createConfigurationDialog() {
		return new DatasourceConfigurationDialog(this);
	}

	@Override
	protected void initComponent(final DefaultTableModel model) {
		this.model = model;

		model.addColumn(Label.DATASOURCE_NAME.toString());
		model.addColumn(Label.DATASOURCE_USER.toString());
	}

	@Override
	protected void setEntites(final List<DatasourceEntity> entites) {
		model.setRowCount(0);

		for (int i = 0; i < entites.size(); i++) {
			DatasourceEntity ds = entites.get(i);

			Object[] cells = new Object[2];
			cells[0] = ds.getName();
			cells[1] = ds.getUser();

			model.addRow(cells);
		}
	}

	@Override
	protected List<DatasourceEntity> getEntites() {

		return null;
	}

	@Override
	protected boolean check(final List<DatasourceEntity> entites) {
		return true;
	}

}
