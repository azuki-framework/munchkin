package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.azkwf.munchkin.entity.ObjectDetailEntity;
import org.azkwf.munchkin.entity.ObjectEntity;
import org.azkwf.munchkin.entity.ObjectTypeEntity;
import org.azkwf.munchkin.entity.SchemaEntity;
import org.azkwf.munchkin.model.DatabaseModel;

/**
 *
 * @author Kawakicchi
 */
public class ObjectPanel extends JPanel {

	private DatabaseModel model;

	private ObjectTypePanel pnlObjectType;

	private JSplitPane split;

	private ObjectListPanel pnlObjectList;

	private ObjectDetailPanel pnlObjectDetail;

	private List<ObjectPanelListener> listeners;

	public ObjectPanel(final DatabaseModel model) {
		this.model = model;
		setLayout(new BorderLayout());

		listeners = new ArrayList<ObjectPanelListener>();

		pnlObjectType = new ObjectTypePanel(model);
		add(pnlObjectType, BorderLayout.NORTH);

		pnlObjectList = new ObjectListPanel(model);
		pnlObjectDetail = new ObjectDetailPanel(model);

		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(pnlObjectList);
		split.setBottomComponent(pnlObjectDetail);
		split.setDividerLocation(300);
		add(split, BorderLayout.CENTER);

		pnlObjectType.addObjectTypePanelListener(new ObjectTypePanelListener() {
			@Override
			public void objectTypePanelOnChangeObject(SchemaEntity schema, ObjectTypeEntity objectType) {

				List<ObjectEntity> objects = model.getObjects(schema, objectType);
				pnlObjectList.setObjects(objects);
			}
		});
		pnlObjectList.addObjectListPanelListener(new ObjectListPanelListener() {
			@Override
			public void objectListPanelOnChange(ObjectEntity object) {

				ObjectDetailEntity detail = model.getObjectDetail(object);
				pnlObjectDetail.setDetail(detail);
			}

			@Override
			public void objectListPanelOnDoubleClick(ObjectEntity object) {

				synchronized (listeners) {
					for (ObjectPanelListener l : listeners) {
						l.objectPanelOnDoubleClick(object);
					}
				}
			}
		});
	}

	public synchronized void addObjectPanelListener(final ObjectPanelListener l) {
		listeners.add(l);
	}
}
