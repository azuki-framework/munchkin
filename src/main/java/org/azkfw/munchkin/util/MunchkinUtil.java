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
package org.azkfw.munchkin.util;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、Munchkinユーティリティクラスです。
 * 
 * @author Kawakicchi
 */
public class MunchkinUtil {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MunchkinUtil.class);

	/**  */
	private static final Pattern PTN_HEX_COLOR = Pattern.compile("^#([0-9A-Z]{2})([0-9A-Z]{2})([0-9A-Z]{2})$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * オブジェクトが、<code>null</code>か判断する。
	 *
	 * @param object オブジェクト
	 * @return オブジェクトが、<code>null</code>の場合、<code>true</code>を返す。
	 */
	public static boolean isNull(final Object object) {
		return (null == object);
	}

	/**
	 * オブジェクトが、<code>null</code>でないか判断する。
	 *
	 * @param object オブジェクト
	 * @return オブジェクトが、<code>null</code>でない場合、<code>true</code>を返す。
	 */
	public static boolean isNotNull(final Object object) {
		return (!isNull(object));
	}

	public static boolean isNullAll(final Object... objects) {
		for (Object object : objects) {
			if (isNotNull(object)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotNullAll(final Object... objects) {
		for (Object object : objects) {
			if (isNull(object)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotNullAny(final Object... objects) {
		for (Object object : objects) {
			if (isNotNull(object)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmpty(final String string) {
		return (null == string || 0 == string.length());
	}

	public static boolean isNotEmpty(final String string) {
		return (!isEmpty(string));
	}

	public static boolean isEmpty(final List<?> list) {
		return (null == list || 0 == list.size());
	}

	public static boolean isNotEmpty(final List<?> list) {
		return (!isEmpty(list));
	}

	public static boolean isEquals(final String str1, final String str2) {
		if (null == str1 && null == str2) {
			return true;
		} else if (null != str1 && null != str2) {
			return str1.equals(str2);
		}
		return false;
	}

	public static boolean isEqualsAny(final String str1, final String... strs) {
		for (String str : strs) {
			if (isEquals(str1, str)) {
				return true;
			}
		}
		return false;
	}

	public static Color getColorFormHexadecimal(final String hex) {
		Color color = Color.white;
		if (MunchkinUtil.isNotEmpty(hex)) {
			final Matcher m = PTN_HEX_COLOR.matcher(hex);
			if (m.matches()) {
				final int r = Integer.parseInt(m.group(1), 16);
				final int g = Integer.parseInt(m.group(2), 16);
				final int b = Integer.parseInt(m.group(3), 16);
				color = new Color(r, g, b);
			}
		}
		return color;
	}

	public static String getHexadecimalFromColor(final Color color) {
		String str = "#FFFFFF";
		if (MunchkinUtil.isNotNull(color)) {
			str = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
		}
		return str;
	}

	public static void release(final Connection connection) {
		if (null != connection) {
			try {
				connection.close();
			} catch (SQLException ex) {
				LOGGER.warn("Connection close error.", ex);
			}
		}
	}
}
