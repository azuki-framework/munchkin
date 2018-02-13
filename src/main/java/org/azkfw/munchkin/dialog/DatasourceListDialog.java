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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.azkfw.munchkin.entity.DatasourceEntity;
import org.azkfw.munchkin.message.Label;

/**
 *
 * @author Kawakicchi
 */
public class DatasourceListDialog extends AbstractDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = -5986067163302309692L;

	private final DefaultTableModel model;
	private final JTable table;

	private final JLabel lblTitle;

	private final JPanel pnlHead;
	private final JPanel pnlMain;
	private final JPanel pnlSide;
	private final JPanel pnlFoot;

	private final JButton btnCreate;
	private final JButton btnModify;
	private final JButton btnDelete;

	private final JButton btnOK;
	private final JButton btnCancel;

	public DatasourceListDialog(final Frame owner, final List<DatasourceEntity> datasources) {
		super(owner);
		setTitle(Label.TITLE_DATASOURCE_LIST.toString());
		setLayout(new BorderLayout());

		model = new DefaultTableModel();
		model.addColumn(Label.DATASOURCE_NAME.toString());
		model.addColumn(Label.DATASOURCE_USER.toString());
		table = new JTable(model);

		lblTitle = new JLabel(Label.TITLE_DATASOURCE_LIST.toString());
		Font fnt = lblTitle.getFont();
		lblTitle.setFont(new Font(fnt.getName(), fnt.getStyle() | Font.BOLD, fnt.getSize() + 2));

		btnCreate = new JButton(Label.BUTTON_CREATE.toString());
		btnCreate.setSize(120, 32);
		btnCreate.setPreferredSize(new Dimension(120, 32));
		btnModify = new JButton(Label.BUTTON_MODIFY.toString());
		btnModify.setSize(120, 32);
		btnModify.setPreferredSize(new Dimension(120, 32));
		btnDelete = new JButton(Label.BUTTON_DELETE.toString());
		btnDelete.setSize(120, 32);
		btnDelete.setPreferredSize(new Dimension(120, 32));

		btnOK = new JButton(Label.BUTTON_OK.toString());
		btnOK.setSize(120, 32);
		btnOK.setPreferredSize(new Dimension(120, 32));
		btnCancel = new JButton(Label.BUTTON_CANCEL.toString());
		btnCancel.setSize(120, 32);
		btnCancel.setPreferredSize(new Dimension(120, 32));

		pnlHead = new JPanel();
		pnlHead.setBorder(new EmptyBorder(8, 10, 10, 10));
		pnlHead.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlHead.setPreferredSize(new Dimension(0, 34));
		pnlHead.add(lblTitle);

		pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		pnlMain.setBorder(new EmptyBorder(8, 20, 0, 0));
		pnlMain.add(BorderLayout.CENTER, new JScrollPane(table));

		pnlSide = new JPanel();
		pnlSide.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlSide.setPreferredSize(new Dimension(140, 0));
		pnlSide.add(btnCreate);
		pnlSide.add(btnModify);
		pnlSide.add(btnDelete);

		pnlFoot = new JPanel();
		pnlFoot.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlFoot.setPreferredSize(new Dimension(0, 52));
		pnlFoot.setBorder(new EmptyBorder(10, 10, 10, 10));
		pnlFoot.add(btnOK);
		pnlFoot.add(btnCancel);

		add(BorderLayout.NORTH, pnlHead);
		add(BorderLayout.CENTER, pnlMain);
		add(BorderLayout.EAST, pnlSide);
		add(BorderLayout.SOUTH, pnlFoot);

		for (DatasourceEntity datasource : datasources) {
			Object[] objs = new Object[2];
			objs[0] = datasource.getName();
			objs[1] = datasource.getUser();
			model.addRow(objs);
		}

		setSize(800, 520);
		moveParentCenter();
	}
}
