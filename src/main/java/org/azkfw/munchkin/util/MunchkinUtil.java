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

import java.util.List;

/**
 * このクラスは、Munchkinユーティリティクラスです。
 * 
 * @author Kawakicchi
 */
public class MunchkinUtil {

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
		return (!isNullAll(objects));
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
}
