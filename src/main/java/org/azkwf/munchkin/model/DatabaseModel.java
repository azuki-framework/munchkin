package org.azkwf.munchkin.model;

import java.util.List;

import org.azkwf.munchkin.entity.ObjectDetailEntity;
import org.azkwf.munchkin.entity.ObjectEntity;
import org.azkwf.munchkin.entity.ObjectTypeEntity;
import org.azkwf.munchkin.entity.SchemaEntity;

/**
 *
 * @author Kawakicchi
 */
public interface DatabaseModel {

	public String getUser();

	public List<SchemaEntity> getSchemas();

	public List<ObjectTypeEntity> getObjectTypes();

	public List<ObjectEntity> getObjects(final SchemaEntity schema, final ObjectTypeEntity objectType);

	public ObjectDetailEntity getObjectDetail(final ObjectEntity object);
}
