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
package org.azkfw.munchkin;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author Kawakicchi
 */
public class Sample {

	public static void main(final String[] args) {
		//paste();
		copy();
	}

	private static void paste() {
		final Toolkit kit = Toolkit.getDefaultToolkit();
		final Clipboard clip = kit.getSystemClipboard();

		try {
			DataFlavor[] dfs = clip.getAvailableDataFlavors();
			for (DataFlavor df : dfs) {
				//System.out.println(df.getMimeType());
				//System.out.println("  " + df.getHumanPresentableName());

				if ("text/html; document=all; class=java.lang.String; charset=Unicode".equals(df.getMimeType())) {
					if ("text/html".equals(df.getHumanPresentableName())) {
						String html = (String) clip.getData(df);
						System.out.println(html);
						break;
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void copy() {
		final Toolkit kit = Toolkit.getDefaultToolkit();
		final Clipboard clip = kit.getSystemClipboard();

		TestSelection is = new TestSelection();
		clip.setContents(is, is);
	}

	public static class TestSelection implements Transferable, ClipboardOwner {

		private DataFlavor[] flavors;

		public TestSelection() {
			flavors = new DataFlavor[1];
			flavors[0] = new DataFlavor("text/html; document=all; class=java.lang.String; charset=Unicode", "text/html");
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			for (DataFlavor f : flavors) {
				if (f.equals(flavor)) return true;
			}
			return false;
		}

		@Override
		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavors[0].equals(flavor)) {
				return getHtml();
			}
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
		}
	}

	public static class DataGridSelection implements Transferable, ClipboardOwner {

		protected String html;

		public DataGridSelection(final String html) {
			this.html = html;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.allHtmlFlavor, DataFlavor.stringFlavor };
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			System.out.println("suppoert");
			if (DataFlavor.allHtmlFlavor.equals(flavor)) return true;
			if (DataFlavor.stringFlavor.equals(flavor)) return true;
			return false;
		}

		@Override
		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (DataFlavor.allHtmlFlavor.equals(flavor)) {
				System.out.println("html");
				return html;
			} else if (DataFlavor.allHtmlFlavor.equals(flavor)) {
				System.out.println("string");
				return html;
			}
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
			this.html = null;
		}
	}

	public static String getHtml() {
		StringBuilder s = new StringBuilder();
		s.append("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"");
		s.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
		s.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
		s.append(" xmlns=\"http://www.w3.org/TR/REC-html40\">");
		s.append("<head>");
		s.append("<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
		s.append("<meta name=ProgId content=Excel.Sheet>");
		s.append("<meta name=Generator content=\"Microsoft Excel 14\">");
		s.append("<style>");
		s.append("<!--");
		s.append("@page");
		s.append("{");
		s.append("	margin:.75in .7in .75in .7in;");
		s.append("	mso-header-margin:.3in;");
		s.append("	mso-footer-margin:.3in;");
		s.append("}");
		s.append("ruby");
		s.append("{");
		s.append("	ruby-align:left;");
		s.append("}");
		s.append("table");
		s.append("{");
		s.append("	mso-displayed-decimal-separator:\"\\.\";");
		s.append("	mso-displayed-thousand-separator:\"\\,\";");
		s.append("}");
		s.append("rt");
		s.append("{");
		s.append("	color:windowtext;");
		s.append("	font-size:6.0pt;");
		s.append("	font-weight:400;");
		s.append("	font-style:normal;");
		s.append("	text-decoration:none;");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-font-charset:128;");
		s.append("	mso-char-type:katakana;");
		s.append("	display:none;");
		s.append("}");
		s.append("td");
		s.append("{");
		s.append("	padding-top:1px;");
		s.append("	padding-right:1px;");
		s.append("	padding-left:1px;");
		s.append("	mso-ignore:padding;");
		s.append("	color:black;");
		s.append("	font-size:12.0pt;");
		s.append("	font-weight:400;");
		s.append("	font-style:normal;");
		s.append("	text-decoration:none;");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-font-charset:128;");
		s.append("	mso-number-format:General;");
		s.append("	text-align:general;");
		s.append("	vertical-align:bottom;");
		s.append("	border:none;");
		s.append("	mso-background-source:auto;");
		s.append("	mso-pattern:auto;");
		s.append("	mso-protection:locked visible;");
		s.append("	white-space:nowrap;");
		s.append("	mso-rotate:0;");
		s.append("}");
		s.append(".xl63");
		s.append("{");
		s.append("	font-weight:700;");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-generic-font-family:auto;");
		s.append("	mso-font-charset:128;");
		s.append("	mso-number-format:\"\\@\";");
		s.append("	border:.5pt solid windowtext;");
		s.append("	background:#D8E4BC;");
		s.append("	mso-pattern:black none;");
		s.append("}");
		s.append(".xl64");
		s.append("{");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-generic-font-family:auto;");
		s.append("	mso-font-charset:128;");
		s.append("	mso-number-format:\"\\@\";");
		s.append("	border:.5pt solid windowtext;");
		s.append("}");
		s.append(".font6");
		s.append("{");
		s.append("	color:black;");
		s.append("	font-size:12.0pt;");
		s.append("	font-weight:400;");
		s.append("	font-style:normal;");
		s.append("	text-decoration:none;");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-generic-font-family:auto;");
		s.append("	mso-font-charset:128;");
		s.append("}");
		s.append(".font7");
		s.append("{");
		s.append("	color:red;");
		s.append("	font-size:12.0pt;");
		s.append("	font-weight:400;");
		s.append("	font-style:normal;");
		s.append("	text-decoration:none;");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-generic-font-family:auto;");
		s.append("	mso-font-charset:128;");
		s.append("}");
		s.append(".font8");
		s.append("{");
		s.append("	color:blue;");
		s.append("	font-size:12.0pt;");
		s.append("	font-weight:700;");
		s.append("	font-style:normal;");
		s.append("	text-decoration:none;");
		s.append("	font-family:\"ＭＳ ゴシック\";");
		s.append("	mso-generic-font-family:auto;");
		s.append("	mso-font-charset:128;");
		s.append("}");
		s.append("-->");
		s.append("</style>");
		s.append("</head>");
		s.append("<body link=blue vlink=purple>");
		s.append("<table border=0 cellpadding=0 cellspacing=0 width=345 style='border-collapse: collapse;width:345pt'>");
		s.append("<!--StartFragment-->");
		s.append(" <col width=77 span=3 style='width:77pt'>");
		s.append(" <col width=114 style='mso-width-source:userset;mso-width-alt:4864;width:114pt'>");
		s.append(" <tr height=18 style='height:18.0pt'>");
		s.append("  <td class=xl63 width=77 style='height:18.0pt;width:77pt'>ID</td>");
		s.append("  <td class=xl63 width=77 style='border-left:none;width:77pt'>NAME</td>");
		s.append("  <td class=xl63 width=77 style='border-left:none;width:77pt'>AGE</td>");
		s.append("  <td class=xl63 width=114 style='border-left:none;width:114pt'>DATE</td>");
		s.append(" </tr>");
		s.append(" <tr height=18 style='height:18.0pt'>");
		s.append("  <td class=xl64 style='height:18.0pt;border-top:none'>A00001</td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'>AAA<font class=\"font7\">_</font><font class=\"font6\">BBB</font></td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'>1</td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'>2018-11-12 12:00:00</td>");
		s.append(" </tr>");
		s.append(" <tr height=18 style='height:18.0pt'>");
		s.append("  <td class=xl64 style='height:18.0pt;border-top:none'>B00002</td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font7\">_______</font></td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'>1.0</td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'>　</td>");
		s.append(" </tr>");
		s.append(" <tr height=18 style='height:18.0pt'>");
		s.append("  <td class=xl64 style='height:18.0pt;border-top:none'>C00003</td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font7\">̺̺̺̺̺̺̺</font></td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font8\">(NULL)</font></td>");
		s.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font8\">(NULL)</font></td>");
		s.append(" </tr>");
		s.append("<!--EndFragment-->");
		s.append("</table>");
		s.append("</body>");
		s.append("</html>");
		s.append("");
		s.append("");
		return s.toString();
	}
}
