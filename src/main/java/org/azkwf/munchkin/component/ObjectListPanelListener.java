package org.azkwf.munchkin.component;

import org.azkwf.munchkin.entity.ObjectEntity;

/**
 *
 * @author Kawakicchi
 */
public interface ObjectListPanelListener {

	public void objectListPanelOnChange(final ObjectEntity object);

	public void objectListPanelOnDoubleClick(final ObjectEntity object);
}
