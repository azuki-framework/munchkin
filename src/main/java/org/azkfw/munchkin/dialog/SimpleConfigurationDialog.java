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
package org.azkfw.munchkin.dialog;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * このクラスは、シンプルな設定を行うためのダイアログクラスです。
 *
 * @author Kawakicchi
 *
 */
public abstract class SimpleConfigurationDialog<ENTITY> extends ConfigurationDialog<ENTITY> {

	/** serialVersionUID */
	private static final long serialVersionUID = 6669023464314825048L;

	public SimpleConfigurationDialog(final Frame frame) {
		super(frame);
	}

	public SimpleConfigurationDialog(final Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInit() {
		super.doInit();

	}

	protected abstract void initComponent(SettingComponent setting);

	@Override
	protected final void initContainer(final Container container) {
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);

		// コンポーネント同士の間隔を空ける設定
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		BaseSettingComponent comps = new BaseSettingComponent();
		initComponent(comps);

		JComponent[][] compos = comps.getComponents();

		int ny = compos.length;
		int nx = compos[0].length;

		// 水平方向のグループ
		{
			SequentialGroup hg = layout.createSequentialGroup();
			for (int x = 0; x < nx; x++) {
				ParallelGroup pg = layout.createParallelGroup();
				for (int y = 0; y < ny; y++) {
					JComponent c = compos[y][x];
					if (c != null) {
						pg.addComponent(c);
					}
				}
				hg.addGroup(pg);
			}
			layout.setHorizontalGroup(hg);
		}

		// 垂直方向のグループ
		{
			SequentialGroup vg = layout.createSequentialGroup();
			for (int y = 0; y < ny; y++) {
				ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
				for (int x = 0; x < nx; x++) {
					JComponent c = compos[y][x];
					if (c != null) {
						pg.addComponent(c);
					}
				}
				vg.addGroup(pg);
			}
			layout.setVerticalGroup(vg);
		}
	}

	public static interface SettingComponent {
		public void add(String label, JComponent component);
	}

	private static class BaseSettingComponent implements SettingComponent {
		List<String> labels;
		List<JComponent> components;

		/**
		 *
		 */
		public BaseSettingComponent() {
			labels = new ArrayList<String>();
			components = new ArrayList<JComponent>();
		}

		@Override
		public void add(final String label, final JComponent component) {
			labels.add(label);
			components.add(component);
		}

		private JComponent[][] getComponents() {

			JComponent[][] cs = new JComponent[labels.size()][2];
			for (int i = 0; i < labels.size(); i++) {
				cs[i][0] = new JLabel(labels.get(i));
				cs[i][1] = components.get(i);
			}

			return cs;
		}
	}
}
