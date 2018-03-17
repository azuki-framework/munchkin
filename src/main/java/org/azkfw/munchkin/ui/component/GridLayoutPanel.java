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
package org.azkfw.munchkin.ui.component;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Kawakicchi
 */
public class GridLayoutPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -6196208341809825551L;

	protected final void setComponents(final JComponent[][] components) {
		final GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		final int ny = components.length;
		final int nx = components[0].length;

		final SequentialGroup hg = layout.createSequentialGroup();
		for (int x = 0; x < nx; x++) {
			final ParallelGroup pg = layout.createParallelGroup();
			for (int y = 0; y < ny; y++) {
				final JComponent c = components[y][x];
				if (c != null) {
					pg.addComponent(c);
				}
			}
			hg.addGroup(pg);
		}
		layout.setHorizontalGroup(hg);

		final SequentialGroup vg = layout.createSequentialGroup();
		for (int y = 0; y < ny; y++) {
			final ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			for (int x = 0; x < nx; x++) {
				final JComponent c = components[y][x];
				if (c != null) {
					pg.addComponent(c);
				}
			}
			vg.addGroup(pg);
		}
		layout.setVerticalGroup(vg);
	}
}
