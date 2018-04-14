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
package org.azkfw.munchkin.sql.parser;

import java.util.ArrayList;
import java.util.List;

import org.azkfw.munchkin.sql.parser.token.Token;

/**
 * このクラスは、SQL解析機能を実装するための基底クラスです。
 * 
 * @author Kawakicchi
 */
public abstract class AbstractSQLParser implements SQLParser {

	/** トークン一覧 */
	private final List<Token> tokens;

	/**
	 * コンストラクタ
	 */
	public AbstractSQLParser() {
		tokens = new ArrayList<Token>();
	}

	@Override
	public final List<Token> getPlainTokens() {
		return new ArrayList<Token>(tokens);
	}

	@Override
	public final void parse(final String sql) {
		tokens.clear();

		doParse(sql);
	}

	/**
	 * パース処理を行う。
	 *
	 * @param sql SQL
	 */
	protected abstract void doParse(final String sql);

	/**
	 * トークンを追加する。
	 *
	 * @param token トークン
	 */
	protected final void addToken(final Token token) {
		tokens.add(token);
	}
}
