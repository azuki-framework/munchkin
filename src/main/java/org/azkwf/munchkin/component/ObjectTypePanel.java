package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.azkwf.munchkin.entity.ObjectTypeEntity;
import org.azkwf.munchkin.entity.SchemaEntity;
import org.azkwf.munchkin.model.DatabaseModel;

/**
 * このクラスは、オブジェクトタイプパネルクラスです。
 * 
 * @author Kawakicchi
 */
public class ObjectTypePanel extends JPanel {

	private DatabaseModel model;

	private JComboBox<SchemaEntity> cmbSchema;

	private JComboBox<ObjectTypeEntity> cmbObject;

	private List<ObjectTypePanelListener> listeners;

	public ObjectTypePanel(final DatabaseModel model) {
		this.model = model;
		this.listeners = new ArrayList<ObjectTypePanelListener>();

		setLayout(new BorderLayout());

		cmbSchema = new JComboBox<SchemaEntity>();
		add(cmbSchema, BorderLayout.NORTH);

		cmbObject = new JComboBox<ObjectTypeEntity>();
		add(cmbObject, BorderLayout.SOUTH);

		for (SchemaEntity schema : model.getSchemas()) {
			cmbSchema.addItem(schema);
		}
		for (ObjectTypeEntity type : model.getObjectTypes()) {
			cmbObject.addItem(type);
		}

		int index = -1;
		for (int i = 0; i < cmbSchema.getItemCount(); i++) {
			if (cmbSchema.getItemAt(i).toString().equals(model.getUser())) {
				index = i;
				break;
			}
		}
		if (-1 != index) {
			cmbSchema.setSelectedIndex(index);
		}

		cmbSchema.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onChange();
			}
		});
		cmbObject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onChange();
			}
		});
	}

	private void onChange() {
		SchemaEntity schema = (SchemaEntity) cmbSchema.getSelectedItem();
		ObjectTypeEntity objectType = (ObjectTypeEntity) cmbObject.getSelectedItem();

		synchronized (listeners) {
			for (ObjectTypePanelListener l : listeners) {
				l.objectTypePanelOnChangeObject(schema, objectType);
			}
		}
	}

	public synchronized void addObjectTypePanelListener(final ObjectTypePanelListener listener) {
		listeners.add(listener);
	}
}
