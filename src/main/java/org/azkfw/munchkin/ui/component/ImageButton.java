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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 *
 * @author Kawakicchi
 *
 */
public class ImageButton extends JComponent {

	/** serialVersionUID */
	private static final long serialVersionUID = -2387493516303315289L;

	private final List<ActionListener> listeners;

	private BufferedImage image;

	public ImageButton(final BufferedImage image) {
		this.image = image;

		listeners = new ArrayList<ActionListener>();
		setDoubleBuffered(true);

		setPreferredSize(new Dimension(32, 32));
		setMinimumSize(new Dimension(32, 32));

		final ActionEvent event = new ActionEvent(this, 0, null);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (isEnabled()) {
					listeners.forEach(l -> l.actionPerformed(event));
				}
			}
		});
	}

	public synchronized void addActionListener(final ActionListener listener) {
		listeners.add(listener);
	}

	public void setImage(final BufferedImage image) {
		this.image = image;
		repaint();
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		double panelWidth = this.getWidth();
		double panelHeight = this.getHeight();

		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();

		double sx = (panelWidth / imageWidth);
		double sy = (panelHeight / imageHeight);

		final AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
		g2.drawImage(image, af, this);
	}
}
