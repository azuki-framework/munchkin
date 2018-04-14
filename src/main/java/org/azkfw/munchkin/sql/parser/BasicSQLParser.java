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

import org.azkfw.munchkin.sql.parser.token.SimpleToken;
import org.azkfw.munchkin.sql.parser.token.Token;
import org.azkfw.munchkin.sql.parser.token.TokenType;
import org.azkfw.munchkin.util.MunchkinUtil;

/**
 * 
 * @author Kawakicchi
 */
public abstract class BasicSQLParser extends AbstractSQLParser {

	public BasicSQLParser() {
	}

	protected abstract boolean isKeyword(String word);

	protected abstract boolean isParameter(String word);

	@Override
	protected final void doParse(final String sql) {
		final char[] chars = sql.toCharArray();
		final int length = chars.length;

		StringBuilder s = new StringBuilder();
		int mode = 0;
		int start = 0;
		int index = 0;
		while (index < length) {
			final char c1 = chars[index];

			if (1 == mode) { // multi comment
				if (MunchkinUtil.isEqualsAny(c1, '*')) {
					if (index + 1 < length) {
						final char c2 = chars[index + 1];
						if (MunchkinUtil.isEqualsAny(c2, '/')) {
							s.append(c1).append(c2);
							addToken(mode, s.toString(), start); // ADD

							mode = 0; // unknown
							start = index + 2;
							s = new StringBuilder();
							index += 2;
							continue;
						}
					}
				}
				s.append(c1);

			} else if (2 == mode) { // single comment
				if (MunchkinUtil.isEqualsAny(c1, '\r', '\n')) {
					addToken(mode, s.toString(), start); // ADD

					mode = 9; // blank
					start = index;
					s = new StringBuilder();
					continue;
				}
				s.append(c1);

			} else if (3 == mode) { // single quotation
				if (MunchkinUtil.isEqualsAny(c1, '\'')) {
					if (index + 1 < length) {
						final char c2 = chars[index + 1];
						if (MunchkinUtil.isEqualsAny(c2, '\'')) {
							s.append(c1).append(c2);

							index += 2;
							continue;
						} else {
							s.append(c1);
							addToken(mode, s.toString(), start);

							mode = 0;
							start = index + 1;
							s = new StringBuilder();
							index++;
							continue;
						}
					}
				}
				s.append(c1);

			} else if (4 == mode) { // double quotation
				if (MunchkinUtil.isEqualsAny(c1, '"')) {
					if (index + 1 < length) {
						final char c2 = chars[index + 1];
						if (MunchkinUtil.isEqualsAny(c2, '"')) {
							s.append(c1).append(c2);

							index += 2;
							continue;
						} else {
							s.append(c1);
							addToken(mode, s.toString(), start);

							mode = 0;
							start = index + 1;
							s = new StringBuilder();
							index++;
							continue;
						}
					}
				}
				s.append(c1);

			} else if (9 == mode) { // blank
				if (!MunchkinUtil.isEqualsAny(c1, '\r', '\n', ' ', '\t')) {
					addToken(mode, s.toString(), start); // ADD

					mode = 0; // unknown
					start = index;
					s = new StringBuilder();
					continue;
				}
				s.append(c1);

			} else {
				if (MunchkinUtil.isEqualsAny(c1, '/')) {
					if (index + 1 < length) {
						final char c2 = chars[index + 1];
						if (MunchkinUtil.isEqualsAny(c2, '*')) {
							addToken(mode, s.toString(), start); // ADD

							mode = 1; // multi comment
							start = index;
							s = new StringBuilder();
							s.append(c1).append(c2);
							index += 2;
							continue;
						}
					}
				}
				if (MunchkinUtil.isEqualsAny(c1, '-')) {
					if (index + 1 < length) {
						final char c2 = chars[index + 1];
						if (MunchkinUtil.isEqualsAny(c2, '-')) {
							addToken(mode, s.toString(), start); // ADD

							mode = 2; // single comment
							start = index;
							s = new StringBuilder();
							s.append(c1).append(c2);
							index += 2;
							continue;
						}
					}
				}
				if (MunchkinUtil.isEqualsAny(c1, '\'')) {
					addToken(mode, s.toString(), start); // ADD

					mode = 3; // single quotation
					start = index;
					s = new StringBuilder();
					s.append(c1);
					index++;
					continue;
				}
				if (MunchkinUtil.isEqualsAny(c1, '"')) {
					addToken(mode, s.toString(), start); // ADD

					mode = 4;// double quotation
					start = index;
					s = new StringBuilder();
					s.append(c1);
					index++;
					continue;
				}
				if (MunchkinUtil.isEqualsAny(c1, '\r', '\n', ' ', '\t')) {
					addToken(mode, s.toString(), start); // ADD

					mode = 9; // blank
					start = index;
					s = new StringBuilder();
					s.append(c1);
					index++;
					continue;
				}
				if (MunchkinUtil.isEqualsAny(c1, '(', ')', ',', '!', '=', '+', '-', '*', '/')) {
					addToken(mode, s.toString(), start); // ADD

					addToken(8, Character.toString(c1), index);

					mode = 0;
					start = index + 1;
					s = new StringBuilder();
					index++;
					continue;
				}

				s.append(c1);
			}

			index++;
		}
		addToken(mode, s.toString(), start);
	}

	protected Token createToken(final int mode, final String word, final int position) {
		TokenType type = TokenType.Unknown;
		if (MunchkinUtil.isEqualsAny(mode, 1, 2)) {
			type = TokenType.Comment;
		} else if (MunchkinUtil.isEqualsAny(mode, 3, 4)) {
			type = TokenType.String;
		} else if (MunchkinUtil.isEqualsAny(mode, 9)) {
			type = TokenType.Blank;
		} else if (MunchkinUtil.isEqualsAny(mode, 8)) {
			type = TokenType.Symbol;
		}

		if (TokenType.Unknown == type) {
			if (isKeyword(word)) {
				type = TokenType.Keyword;
			}
		}
		if (TokenType.Unknown == type) {
			if (isParameter(word)) {
				type = TokenType.Parameter;
			}
		}

		final Token token = new SimpleToken(type, word, position);
		return token;
	}

	private void addToken(final int mode, final String word, final int position) {
		if (MunchkinUtil.isNotEmpty(word)) {
			final Token token = createToken(mode, word, position);
			addToken(token);
		}
	}
}
