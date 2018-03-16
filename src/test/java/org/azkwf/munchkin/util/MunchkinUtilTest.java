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
package org.azkwf.munchkin.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.azkfw.munchkin.util.MunchkinUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * このクラスは、{@link MunchkinUtil} クラスを評価するテストクラスです。
 * 
 * @author Kawakicchi
 */
@RunWith(JUnit4.class)
public class MunchkinUtilTest {

	@Test
	public void isNull() {
		Object obj = null;
		assertTrue(MunchkinUtil.isNull(obj));

		obj = new Object();
		assertFalse(MunchkinUtil.isNull(obj));
	}

	@Test
	public void isNotNull() {
		Object obj = null;
		assertFalse(MunchkinUtil.isNotNull(obj));

		obj = new Object();
		assertTrue(MunchkinUtil.isNotNull(obj));
	}
}
