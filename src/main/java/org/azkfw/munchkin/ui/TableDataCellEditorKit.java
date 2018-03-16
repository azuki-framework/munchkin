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
package org.azkfw.munchkin.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.JEditorPane;
import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class TableDataCellEditorKit extends StyledEditorKit {

	/** serialVersionUID */
	private static final long serialVersionUID = -1135050430308659155L;

	private final SimpleAttributeSet attrs = new SimpleAttributeSet();

	@Override
	public void install(final JEditorPane c) {
		FontMetrics fm = c.getFontMetrics(c.getFont());
		int tabLength = fm.charWidth('m') * 4;
		TabStop[] tabs = new TabStop[100];
		for (int j = 0; j < tabs.length; j++) {
			tabs[j] = new TabStop((j + 1) * tabLength);
		}
		TabSet tabSet = new TabSet(tabs);
		StyleConstants.setTabSet(attrs, tabSet);
		super.install(c);
	}

	@Override
	public Document createDefaultDocument() {
		Document d = super.createDefaultDocument();
		if (d instanceof StyledDocument) {
			((StyledDocument) d).setParagraphAttributes(0, d.getLength(), attrs, false);
		}
		return d;
	}

	@Override
	public ViewFactory getViewFactory() {
		return new NoWrapViewFactory();
	}

	private static class NoWrapViewFactory implements ViewFactory {
		@Override
		public View create(final Element elem) {
			String kind = elem.getName();
			if (null != kind) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new WhitespaceLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new NoWrapParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}

	private static class NoWrapParagraphView extends ParagraphView {
		public NoWrapParagraphView(final Element elem) {
			super(elem);
		}

		@Override
		protected SizeRequirements calculateMinorAxisRequirements(final int axis, final SizeRequirements r) {
			SizeRequirements req = super.calculateMinorAxisRequirements(axis, r);
			req.minimum = req.preferred;
			return req;
		}

		@Override
		public int getFlowSpan(final int index) {
			return Integer.MAX_VALUE;
		}
	}

	private static class WhitespaceLabelView extends LabelView {

		private static final Color COLOR = new Color(65, 105, 225); //new Color(255, 99, 71);
		private static final BasicStroke line = new BasicStroke(1f);
		private static final BasicStroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
				10f, new float[] { 1f }, 0f);

		public WhitespaceLabelView(final Element elem) {
			super(elem);
		}

		@Override
		public void paint(final Graphics g, final Shape a) {
			super.paint(g, a);
			Graphics2D g2 = (Graphics2D) g.create();
			Stroke stroke = g2.getStroke();
			Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
			FontMetrics fontMetrics = g.getFontMetrics();
			int sumOfTabs = 0;
			String text = getText(getStartOffset(), getEndOffset()).toString();
			for (int i = 0; i < text.length(); i++) {
				String s = text.substring(i, i + 1);
				int previousStringWidth = fontMetrics.stringWidth(text.substring(0, i)) + sumOfTabs;
				int sx = alloc.x + previousStringWidth;
				int sy = alloc.y + alloc.height - fontMetrics.getDescent();
				if ("　".equals(s)) {
					int spaceWidth = fontMetrics.stringWidth("　");
					g2.setStroke(dashed);
					g2.setPaint(COLOR);
					g2.drawLine(sx + 1, sy - 1, sx + spaceWidth - 2, sy - 1);
					g2.drawLine(sx + 2, sy, sx + spaceWidth - 2, sy);
				} else if (" ".equals(s)) {
					int spaceWidth = fontMetrics.stringWidth(" ");
					g2.setStroke(line);
					g2.setPaint(COLOR);
					g2.drawLine(sx + 1, sy - 1 - spaceWidth / 2, sx + 1, sy - 1);
					g2.drawLine(sx + 1, sy - 1, sx + spaceWidth - 2, sy - 1);
					g2.drawLine(sx + spaceWidth - 2, sy - 1, sx + spaceWidth - 2, sy - 1 - spaceWidth / 2);
				} else if ("\t".equals(s)) {
					int tabWidth = (int) getTabExpander().nextTabStop(sx, i) - sx;
					g2.setColor(COLOR);
					g2.drawLine(sx + 2, sy - 0, sx + 2 + 2, sy - 0);
					g2.drawLine(sx + 2, sy - 1, sx + 2 + 1, sy - 1);
					g2.drawLine(sx + 2, sy - 2, sx + 2 + 0, sy - 2);
					g2.setStroke(dashed);
					g2.drawLine(sx + 2, sy, sx + tabWidth - 2, sy);
					sumOfTabs += tabWidth;
				}
				g2.setStroke(stroke);
			}
			g2.dispose();
		}
	}

}
