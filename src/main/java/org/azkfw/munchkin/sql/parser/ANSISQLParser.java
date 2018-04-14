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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.azkfw.munchkin.util.MunchkinUtil;

/**
 *
 * @author Kawakicchi
 */
public class ANSISQLParser extends BasicSQLParser {

	private static final Set<String> keywords;
	static {
		final InputStream stream = ANSISQLParser.class.getResourceAsStream("sql99.txt");
		final List<String> lines = MunchkinUtil.readLines(stream, Charset.forName("UTF-8"));

		keywords = new HashSet<String>();

		lines.forEach(line -> {
			final String s = line.trim().toUpperCase();
			if (0 < s.length()) {
				keywords.add(s);
			}
		});
	}

	@Override
	protected final boolean isKeyword(final String word) {
		return keywords.contains(word.toUpperCase());
	}

	@Override
	protected boolean isParameter(final String word) {
		return false;
	}

}
