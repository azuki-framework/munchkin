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

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.azkfw.munchkin.entity.DatabaseDriver;

/**
 *
 * @author Kawakicchi
 *
 */
public class DatabaseDriverComboBox extends JComboBox<DatabaseDriver> {

	/** serialVersionUID */
	private static final long serialVersionUID = 6277860866380344985L;

	public DatabaseDriverComboBox(final DefaultComboBoxModel<DatabaseDriver> model) {
		super(model);

		setRenderer(new DatabaseDriverCellRenderer());
	}

	public void setSelectDatabaseDriver(final DatabaseDriver driver) {
		int index = 0;
		for (int i = 0; i < getItemCount(); i++) {
			DatabaseDriver dd = getItemAt(i);
			if (dd == driver) {
				index = i;
				break;
			}
		}
		setSelectedIndex(index);
	}

	public DatabaseDriver getSelectDatabaseDriver() {
		return (DatabaseDriver) getSelectedItem();
	}

	private class DatabaseDriverCellRenderer extends JLabel implements ListCellRenderer<DatabaseDriver> {

		/** serialVersionUID */
		private static final long serialVersionUID = -5880225421107553959L;

		public DatabaseDriverCellRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(final JList<? extends DatabaseDriver> list,
				final DatabaseDriver value, final int index, final boolean isSelected, final boolean cellHasFocus) {

			setText(String.format("%s [%s]", value.getLabel(), value.getDriver()));

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
