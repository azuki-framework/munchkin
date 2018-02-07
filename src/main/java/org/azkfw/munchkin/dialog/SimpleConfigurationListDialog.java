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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.azkfw.munchkin.message.Label;

/**
 *
 * @author Kawakicchi
 */
public abstract class SimpleConfigurationListDialog<ENTITY> extends ConfigurationListDialog<ENTITY> {

	/** serialVersionUID */
	private static final long serialVersionUID = -3002576952023863816L;

	public SimpleConfigurationListDialog(final Frame frame) {
		super(frame);
	}

	public SimpleConfigurationListDialog(final Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInit() {
		super.doInit();

	}

	protected abstract void initComponent(final DefaultTableModel model);

	protected abstract ConfigurationDialog<ENTITY> createConfigurationDialog();

	@Override
	protected final void initContainer(final Container container) {
		BorderLayout layout = new BorderLayout();
		container.setLayout(layout);

		DefaultTableModel model = new DefaultTableModel();
		JTable table = new JTable(model) {

			/** serialVersionUID */
			private static final long serialVersionUID = 2568381435024632430L;

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initComponent(model);

		JScrollPane scroll = new JScrollPane(table);

		JPanel pnlController = new JPanel();
		pnlController.setLayout(new GridLayout(3, 1));
		JButton btnCreate = new JButton(Label.BUTTON_CREATE.toString());
		JButton btnModify = new JButton(Label.BUTTON_MODIFY.toString());
		JButton btnDelete = new JButton(Label.BUTTON_DELETE.toString());
		pnlController.add(btnCreate);
		pnlController.add(btnModify);
		pnlController.add(btnDelete);

		JPanel pnl = new JPanel();
		pnl.setLayout(new BorderLayout());
		pnl.add(BorderLayout.NORTH, pnlController);

		add(BorderLayout.EAST, pnl);
		add(BorderLayout.CENTER, scroll);

		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ConfigurationDialog<ENTITY> dlg = createConfigurationDialog();
				dlg.setVisible(true);
			}
		});
	}

}
