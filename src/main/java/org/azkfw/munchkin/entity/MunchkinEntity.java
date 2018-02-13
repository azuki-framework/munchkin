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
package org.azkfw.munchkin.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 *
 */
@XmlRootElement(name = "munchkin")
public class MunchkinEntity {

	private final List<DatasourceEntity> datasources;

	private final List<SQLHistoryEntity> historySqls;

	private String sqlTextEditorText;

	public MunchkinEntity() {
		datasources = new ArrayList<DatasourceEntity>();
		historySqls = new ArrayList<SQLHistoryEntity>();
		sqlTextEditorText = "";
	}

	public void addDatasource(final DatasourceEntity datasource) {
		this.datasources.add(datasource);
	}

	public void setDatasources(final List<DatasourceEntity> datasources) {
		this.datasources.clear();
		this.datasources.addAll(datasources);
	}

	@XmlElementWrapper(name = "datasources")
	@XmlElement(name = "datasource")
	public List<DatasourceEntity> getDatasources() {
		return datasources;
	}

	public void addHistorySql(final SQLHistoryEntity historySql) {
		for (int i = historySqls.size() - 1; i >= 0; i--) {
			SQLHistoryEntity sql = historySqls.get(i);
			if (MunchkinUtil.isEquals(historySql.getSql(), sql.getSql())) {
				historySqls.remove(i);
			}
		}
		historySqls.add(0, historySql);
		for (int i = historySqls.size() - 1; i >= 100; i--) {
			historySqls.remove(i);
		}
		for (int i = 0; i < historySqls.size(); i++) {
			historySqls.get(i).setOrder(i + 1);
		}
	}

	@XmlElementWrapper(name = "historySqls")
	@XmlElement(name = "historySql")
	public List<SQLHistoryEntity> getHistorySqls() {
		return historySqls;
	}

	public void setSqlTextEditorText(final String text) {
		sqlTextEditorText = text;
	}

	@XmlElement(name = "SQLTextEditor")
	public String getSqlTextEditorText() {
		return sqlTextEditorText;
	}
}
