package org.azkwf.munchkin.entity;

/**
 *
 * @author Kawakicchi
 */
public class ObjectTypeEntity {

	private String name;

	private String label;

	public ObjectTypeEntity(final String name, final String label) {
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
