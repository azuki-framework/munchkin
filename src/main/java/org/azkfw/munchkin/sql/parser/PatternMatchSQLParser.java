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

import java.util.regex.Pattern;

/**
 *
 * @author Kawakicchi
 */
public class PatternMatchSQLParser extends BasicSQLParser {

	private Pattern ptnKeyword;
	private Pattern ptnParameter;

	public void setKeywordPattern(final Pattern pattern) {
		ptnKeyword = pattern;
	}

	public void setParameterPattern(final Pattern pattern) {
		ptnParameter = pattern;
	}

	@Override
	protected boolean isKeyword(final String word) {
		if (null != ptnKeyword) {
			if (ptnKeyword.matcher(word).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isParameter(final String word) {
		if (null != ptnParameter) {
			if (ptnParameter.matcher(word).matches()) {
				return true;
			}
		}
		return false;
	}
}
