package org.azkwf.munchkin.entity;

import java.util.List;

/**
 *
 * @author Kawakicchi
 */
public class ObjectDetailEntity {

	private List<String> columns;

	private List<List<Object>> datas;

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<List<Object>> getDatas() {
		return datas;
	}

	public void setDatas(List<List<Object>> datas) {
		this.datas = datas;
	}

}
