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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
	private final JComboBox<SchemaEntity> cmbSchema;
	private final JLabel lblType;
	private final JComboBox<TypeEntity> cmbType;

	public DBConditionPanel() {
		listeners = new ArrayList<DBConditionPanelListener>();

		//setPreferredSize(new Dimension(0, 60 + 12));
		setBorder(new EmptyBorder(2, 2, 4, 2));

		lblSchema = new JLabel("Schema :");
		cmbSchema = new JComboBox<SchemaEntity>();
		lblType = new JLabel("Type :");
		cmbType = new JComboBox<TypeEntity>();

		cmbSchema.setRenderer(new DefaultListCellRenderer() {
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
		cmbType.setRenderer(new DefaultListCellRenderer() {
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

		final JComponent[][] compos = { { lblSchema, cmbSchema }, { lblType, cmbType } };
		initPane(compos, this);

		cmbSchema.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final SchemaEntity schema = (SchemaEntity) cmbSchema.getSelectedItem();
					listeners.forEach(l -> l.dbConditionPanelChengedSchema(schema));
				}
			}
		});
		cmbType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final TypeEntity type = (TypeEntity) cmbType.getSelectedItem();
					listeners.forEach(l -> l.dbConditionPanelChengedType(type));
				}
			}
		});
	}

	public synchronized void addDBConditionPanelListener(final DBConditionPanelListener listener) {
		listeners.add(listener);
	}

	public void refresh(final List<SchemaEntity> schemaList, final List<TypeEntity> typeList) {
		cmbSchema.removeAllItems();
		schemaList.forEach(schema -> cmbSchema.addItem(schema));
		cmbSchema.setSelectedIndex(0);

		cmbType.removeAllItems();
		typeList.forEach(type -> cmbType.addItem(type));
		cmbType.setSelectedIndex(0);
	}

	public void setSelectSchema(final SchemaEntity schema) {
		cmbSchema.setSelectedIndex(0);
		for (int i = 0; i < cmbSchema.getItemCount(); i++) {
			final SchemaEntity target = cmbSchema.getItemAt(i);
			if (MunchkinUtil.isEquals(schema.getName(), target.getName())) {
				cmbSchema.setSelectedIndex(i);
				return;
			}
		}
	}

	public SchemaEntity getSelectSchema() {
		return (SchemaEntity) cmbSchema.getSelectedItem();
	}

	public TypeEntity getSelectType() {
		return (TypeEntity) cmbType.getSelectedItem();
	}

	private void initPane(final JComponent[][] compos, final Container container) {
		final GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		final int ny = compos.length;
		final int nx = compos[0].length;

		final SequentialGroup hg = layout.createSequentialGroup();
		for (int x = 0; x < nx; x++) {
			final ParallelGroup pg = layout.createParallelGroup();
			for (int y = 0; y < ny; y++) {
				final JComponent c = compos[y][x];
				if (c != null) {
					pg.addComponent(c);
				}
			}
			hg.addGroup(pg);
		}
		layout.setHorizontalGroup(hg);

		final SequentialGroup vg = layout.createSequentialGroup();
		for (int y = 0; y < ny; y++) {
			final ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			for (int x = 0; x < nx; x++) {
				final JComponent c = compos[y][x];
				if (c != null) {
					pg.addComponent(c);
				}
			}
			vg.addGroup(pg);
		}
		layout.setVerticalGroup(vg);
	}
}
