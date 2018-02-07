package org.azkwf.munchkin.entity;

/**
 *
 * @author Kawakicchi
 */
public class SchemaEntity {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
