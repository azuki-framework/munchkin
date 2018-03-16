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

/**
 * このクラスは、文字列生成を行うビルダークラスです。
 * 
 * @author Kawakicchi
 */
public class StringBuilderEx {

	private final StringBuilder s;

	/**
	 * コンストラクタ
	 */
	public StringBuilderEx() {
		s = new StringBuilder();
	}

	/**
	 * 文字列を追加する。
	 *
	 * @param str 文字列
	 */
	public void append(final String str) {
		s.append(str);
	}

	/**
	 * 文字列を追加する。
	 *
	 * @param format フォーマット
	 * @param args 引数
	 */
	public void append(final String format, final Object... args) {
		s.append(String.format(format, args));
	}

	/**
	 * 文字列を追加する。
	 * <p>
	 * 文字列の末尾にシステム改行コードを追加する。
	 * </p>
	 *
	 * @param str 文字列
	 */
	public void appendln(final String str) {
		s.append(str).append(System.lineSeparator());
	}

	/**
	 * 文字列を追加する。
	 * <p>
	 * 文字列の末尾にシステム改行コードを追加する。
	 * </p>
	 *
	 * @param format フォーマット
	 * @param args 引数
	 */
	public void appendln(final String format, final Object... args) {
		s.append(String.format(format, args)).append(System.lineSeparator());
	}

	/**
	 * 文字列の長さを取得する。
	 *
	 * @return 文字列数
	 */
	public int length() {
		return s.length();
	}

	@Override
	public String toString() {
		return s.toString();
	}
}
