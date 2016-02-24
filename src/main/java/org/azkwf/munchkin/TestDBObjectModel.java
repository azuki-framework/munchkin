/**
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
package org.azkwf.munchkin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kawakicchi
 *
 */
public class TestDBObjectModel implements DBObjectModel{

	@Override
	public List<String> getUserList() {
		List<String> users = new ArrayList<String>();
		users.add("TESTER");
		users.add("DEVELOPER");
		return users;
	}

	@Override
	public List<String> getTypeList(final String user) {
		return getTypeList();
	}
	public List<String> getTypeList() {
		List<String> types = new ArrayList<String>();
		types.add("TABLE");
		types.add("VIEW");
		types.add("FUNCTION");
		types.add("DATABASE LINK");
		return types;
	}

	/* (non-Javadoc)
	 * @see org.azkwf.munchkin.DBObjectModel#getObjectList(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> getObjectList(String user, String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
