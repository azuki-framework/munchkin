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
package org.azkfw.munchkin.ui.dialog;

import java.awt.Container;
import java.awt.Frame;

import javax.swing.JDialog;

/**
 *
 * @author Kawakicchi
 */
public abstract class AbstractDialog extends JDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = -690810476497547804L;

	/**
	 * コンストラクタ
	 *
	 * @param owner オーナー
	 */
	public AbstractDialog(final Frame owner) {
		super(owner, true);

	}

	/**
	 * 親コンテナーの中央へ移動
	 */
	public void moveParentCenter() {
		final Container parent = getParent();
		final int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
		final int y = parent.getY() + (parent.getHeight() - getHeight()) / 2;
		setLocation(x, y);
	}
}
