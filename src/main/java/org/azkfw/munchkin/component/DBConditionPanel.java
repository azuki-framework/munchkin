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
package org.azkfw.munchkin.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.azkfw.munchkin.database.model.SchemaEntity;
import org.azkfw.munchkin.database.model.TypeEntity;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 *
 */
public class DBConditionPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -771651304561133739L;

	private final List<DBConditionPanelListener> listeners;

	private final JLabel lblSchema;
	private final JComboBox<SchemaEntity> lstSchema;
	private final JLabel lblType;
	private final JComboBox<TypeEntity> lstType;

	public DBConditionPanel() {
		listeners = new ArrayList<DBConditionPanelListener>();

		setLayout(new GridLayout(2, 1));
		setPreferredSize(new Dimension(0, 60 + 12));
		setBorder(new EmptyBorder(4, 4, 16, 4));

		lblSchema = new JLabel("Schema");
		lblSchema.setPreferredSize(new Dimension(60, 0));
		lstSchema = new JComboBox<SchemaEntity>();
		lstSchema.setRenderer(new DefaultListCellRenderer() {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (MunchkinUtil.isNull(value)) {
					setText("(NULL)");
				} else if (value instanceof SchemaEntity) {
					setText(((SchemaEntity) value).getName());
				} else {
					setText("-- Unknown --");
				}

				return this;
			}
		});
		final JPanel pnlUser = new JPanel();
		pnlUser.setLayout(new BorderLayout());
		pnlUser.add(BorderLayout.WEST, lblSchema);
		pnlUser.add(BorderLayout.CENTER, lstSchema);
		add(pnlUser);

		lblType = new JLabel("Type");
		lblType.setPreferredSize(new Dimension(60, 0));
		lstType = new JComboBox<TypeEntity>();
		lstType.setRenderer(new DefaultListCellRenderer() {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (MunchkinUtil.isNull(value)) {
					setText("(NULL)");
				} else if (value instanceof TypeEntity) {
					setText(((TypeEntity) value).getName());
				} else {
					setText("-- Unknown --");
				}

				return this;
			}
		});
		final JPanel pnlType = new JPanel();
		pnlType.setLayout(new BorderLayout());
		pnlType.add(BorderLayout.WEST, lblType);
		pnlType.add(BorderLayout.CENTER, lstType);
		add(pnlType);

		lstSchema.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					listeners.forEach(l -> l.dbConditionPanelSchemaChenged((SchemaEntity) lstSchema.getSelectedItem()));
				}
			}
		});
		lstType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					listeners.forEach(l -> l.dbConditionPanelTypeChenged((TypeEntity) lstType.getSelectedItem()));
				}
			}
		});
	}

	public synchronized void addDBConditionPanelListener(final DBConditionPanelListener listener) {
		listeners.add(listener);
	}

	public void refresh(final List<SchemaEntity> schemaList, final List<TypeEntity> typeList) {
		lstSchema.removeAllItems();
		schemaList.forEach(schema -> lstSchema.addItem(schema));
		lstSchema.setSelectedIndex(0);

		lstType.removeAllItems();
		typeList.forEach(type -> lstType.addItem(type));
		lstType.setSelectedIndex(0);
	}

	public void setSelectSchema(final SchemaEntity schema) {
		lstSchema.setSelectedIndex(0);
		for (int i = 0; i < lstSchema.getItemCount(); i++) {
			final SchemaEntity target = lstSchema.getItemAt(i);
			if (MunchkinUtil.isEquals(schema.getName(), target.getName())) {
				lstSchema.setSelectedIndex(i);
				return;
			}
		}
	}

	public SchemaEntity getSelectSchema() {
		return (SchemaEntity) lstSchema.getSelectedItem();
	}

	public TypeEntity getSelectType() {
		return (TypeEntity) lstType.getSelectedItem();
	}
}
