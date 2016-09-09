package org.azkwf.munchkin.component;

import org.azkwf.munchkin.entity.ObjectTypeEntity;
import org.azkwf.munchkin.entity.SchemaEntity;

/**
 * このインターフェースは、{@link ObjectTypePanel}のリスナーです。
 *
 * @author Kawakicchi
 */
public interface ObjectTypePanelListener {

	/**
	 * スキーマ、またはオブジェクトタイプに変更があった場合に呼び出される。
	 *
	 * @param schema
	 *            スキーマ
	 * @param objectType
	 *            オブジェクトタイプ
	 */
	public void objectTypePanelOnChangeObject(final SchemaEntity schema, final ObjectTypeEntity objectType);
}
