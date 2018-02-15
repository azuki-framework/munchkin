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
				System.out.println(df.getMimeType());
				System.out.println("  " + df.getHumanPresentableName());

				// all
				// fragment
				// selection
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
		StringBuilder sPreHeader = new StringBuilder();
		sPreHeader.append("Version:0.9\r\n"); // 11
		sPreHeader.append("StartHTML:0000000182\r\n"); // 20
		sPreHeader.append("EndHTML:0000002080\r\n"); // 18
		sPreHeader.append("StartFragment:0000000218\r\n"); // 24
		sPreHeader.append("EndFragment:0000002044\r\n"); // 22
		sPreHeader.append("SourceURL:test\r\n"); // 14
		// 109 + 12

		StringBuilder sHtmlStart = new StringBuilder();
		sHtmlStart.append("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"");
		sHtmlStart.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
		sHtmlStart.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
		sHtmlStart.append(" xmlns=\"http://www.w3.org/TR/REC-html40\">");
		sHtmlStart.append("<head>");
		sHtmlStart.append("<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
		sHtmlStart.append("<meta name=ProgId content=Excel.Sheet>");
		sHtmlStart.append("<meta name=Generator content=\"Microsoft Excel 14\">");
		sHtmlStart.append("<style>");
		sHtmlStart.append("<!--");
		sHtmlStart.append("@page");
		sHtmlStart.append("{");
		sHtmlStart.append("	margin:.75in .7in .75in .7in;");
		sHtmlStart.append("	mso-header-margin:.3in;");
		sHtmlStart.append("	mso-footer-margin:.3in;");
		sHtmlStart.append("}");
		sHtmlStart.append("ruby");
		sHtmlStart.append("{");
		sHtmlStart.append("	ruby-align:left;");
		sHtmlStart.append("}");
		sHtmlStart.append("table");
		sHtmlStart.append("{");
		sHtmlStart.append("	mso-displayed-decimal-separator:\"\\.\";");
		sHtmlStart.append("	mso-displayed-thousand-separator:\"\\,\";");
		sHtmlStart.append("}");
		sHtmlStart.append("rt");
		sHtmlStart.append("{");
		sHtmlStart.append("	color:windowtext;");
		sHtmlStart.append("	font-size:6.0pt;");
		sHtmlStart.append("	font-weight:400;");
		sHtmlStart.append("	font-style:normal;");
		sHtmlStart.append("	text-decoration:none;");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("	mso-char-type:katakana;");
		sHtmlStart.append("	display:none;");
		sHtmlStart.append("}");
		sHtmlStart.append("td");
		sHtmlStart.append("{");
		sHtmlStart.append("	padding-top:1px;");
		sHtmlStart.append("	padding-right:1px;");
		sHtmlStart.append("	padding-left:1px;");
		sHtmlStart.append("	mso-ignore:padding;");
		sHtmlStart.append("	color:black;");
		sHtmlStart.append("	font-size:12.0pt;");
		sHtmlStart.append("	font-weight:400;");
		sHtmlStart.append("	font-style:normal;");
		sHtmlStart.append("	text-decoration:none;");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("	mso-number-format:General;");
		sHtmlStart.append("	text-align:general;");
		sHtmlStart.append("	vertical-align:bottom;");
		sHtmlStart.append("	border:none;");
		sHtmlStart.append("	mso-background-source:auto;");
		sHtmlStart.append("	mso-pattern:auto;");
		sHtmlStart.append("	mso-protection:locked visible;");
		sHtmlStart.append("	white-space:nowrap;");
		sHtmlStart.append("	mso-rotate:0;");
		sHtmlStart.append("}");
		sHtmlStart.append(".xl63");
		sHtmlStart.append("{");
		sHtmlStart.append("	font-weight:700;");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-generic-font-family:auto;");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("	mso-number-format:\"\\@\";");
		sHtmlStart.append("	border:.5pt solid windowtext;");
		sHtmlStart.append("	background:#D8E4BC;");
		sHtmlStart.append("	mso-pattern:black none;");
		sHtmlStart.append("}");
		sHtmlStart.append(".xl64");
		sHtmlStart.append("{");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-generic-font-family:auto;");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("	mso-number-format:\"\\@\";");
		sHtmlStart.append("	border:.5pt solid windowtext;");
		sHtmlStart.append("}");
		sHtmlStart.append(".font6");
		sHtmlStart.append("{");
		sHtmlStart.append("	color:black;");
		sHtmlStart.append("	font-size:12.0pt;");
		sHtmlStart.append("	font-weight:400;");
		sHtmlStart.append("	font-style:normal;");
		sHtmlStart.append("	text-decoration:none;");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-generic-font-family:auto;");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("}");
		sHtmlStart.append(".font7");
		sHtmlStart.append("{");
		sHtmlStart.append("	color:red;");
		sHtmlStart.append("	font-size:12.0pt;");
		sHtmlStart.append("	font-weight:400;");
		sHtmlStart.append("	font-style:normal;");
		sHtmlStart.append("	text-decoration:none;");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-generic-font-family:auto;");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("}");
		sHtmlStart.append(".font8");
		sHtmlStart.append("{");
		sHtmlStart.append("	color:blue;");
		sHtmlStart.append("	font-size:12.0pt;");
		sHtmlStart.append("	font-weight:700;");
		sHtmlStart.append("	font-style:normal;");
		sHtmlStart.append("	text-decoration:none;");
		sHtmlStart.append("	font-family:\"ＭＳ ゴシック\";");
		sHtmlStart.append("	mso-generic-font-family:auto;");
		sHtmlStart.append("	mso-font-charset:128;");
		sHtmlStart.append("}");
		sHtmlStart.append("-->");
		sHtmlStart.append("</style>");
		sHtmlStart.append("</head>");
		sHtmlStart.append("<body link=blue vlink=purple>");
		sHtmlStart.append("<table border=0 cellpadding=0 cellspacing=0 width=345 style='border-collapse: collapse;width:345pt'>");

		StringBuilder sFrag = new StringBuilder();
		sFrag.append("<!--StartFragment-->");
		sFrag.append(" <col width=77 span=3 style='width:77pt'>");
		sFrag.append(" <col width=114 style='mso-width-source:userset;mso-width-alt:4864;width:114pt'>");
		sFrag.append(" <tr height=18 style='height:18.0pt'>");
		sFrag.append("  <td class=xl63 width=77 style='height:18.0pt;width:77pt'>ID</td>");
		sFrag.append("  <td class=xl63 width=77 style='border-left:none;width:77pt'>NAME</td>");
		sFrag.append("  <td class=xl63 width=77 style='border-left:none;width:77pt'>AGE</td>");
		sFrag.append("  <td class=xl63 width=114 style='border-left:none;width:114pt'>DATE</td>");
		sFrag.append(" </tr>");
		sFrag.append(" <tr height=18 style='height:18.0pt'>");
		sFrag.append("  <td class=xl64 style='height:18.0pt;border-top:none'>A00001</td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'>AAA<font class=\"font7\">_</font><font class=\"font6\">BBB</font></td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'>1</td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'>2018-11-12 12:00:00</td>");
		sFrag.append(" </tr>");
		sFrag.append(" <tr height=18 style='height:18.0pt'>");
		sFrag.append("  <td class=xl64 style='height:18.0pt;border-top:none'>B00002</td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font7\">_______</font></td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'>1.0</td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'>　</td>");
		sFrag.append(" </tr>");
		sFrag.append(" <tr height=18 style='height:18.0pt'>");
		sFrag.append("  <td class=xl64 style='height:18.0pt;border-top:none'>C00003</td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font7\">_______</font></td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font8\">(NULL)</font></td>");
		sFrag.append("  <td class=xl64 style='border-top:none;border-left:none'><font class=\"font8\">(NULL)</font></td>");
		sFrag.append(" </tr>");
		sFrag.append("<!--EndFragment-->");

		StringBuilder sHtmlEnd = new StringBuilder();
		sHtmlEnd.append("</table>");
		sHtmlEnd.append("</body>");
		sHtmlEnd.append("</html>");

		int startHTML = sPreHeader.length();
		int startFragment = startHTML + sHtmlStart.length();
		int endFragment = startFragment + sFrag.length();
		int endHTML = endFragment + sHtmlEnd.length();

		StringBuilder sHeader = new StringBuilder();
		sHeader.append("Version:0.9\r\n"); // 11
		sHeader.append(String.format("StartHTML:%010d\r\n", startHTML)); // 20
		sHeader.append(String.format("EndHTML:%010d\r\n", endHTML)); // 18
		sHeader.append(String.format("StartFragment:%010d\r\n", startFragment)); // 24
		sHeader.append(String.format("EndFragment:%010d\r\n", endFragment)); // 22
		sHeader.append("SourceURL:test\r\n"); // 14
		// 109 + 12
		
		StringBuffer s = new StringBuffer();
		s.append(sHeader.toString());
		s.append(sHtmlStart.toString());
		s.append(sFrag.toString());
		s.append(sHtmlEnd.toString());
		return s.toString();
	}
}
