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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author Kawakicchi
 *
 */
public class SQLHistoryEntity {

	private int order;
	private String sql;

	public SQLHistoryEntity() {
		this.order = -1;
		this.sql = null;
	}

	public SQLHistoryEntity(final String sql) {
		this.order = -1;
		this.sql = sql;
	}

	@XmlAttribute
	public int getOrder() {
		return order;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

	@XmlValue
	public String getSql() {
		return sql;
	}

	public void setSql(final String sql) {
		this.sql = sql;
	}
}
