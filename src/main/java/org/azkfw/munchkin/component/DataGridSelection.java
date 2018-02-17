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
package org.azkfw.munchkin.component;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.azkfw.munchkin.util.StringBuilderEx;

/**
 *
 * @author Kawakicchi
 */
public class DataGridSelection implements Transferable, ClipboardOwner {

	private static final Pattern PTN = Pattern.compile("([ ]+)");

	private final DataFlavor[] flavors;

	private final List<String> columns;
	private final List<List<String>> records;

	public DataGridSelection(final List<String> columns, final List<List<String>> records) {
		this.columns = columns;
		this.records = records;

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

	private String html;

	private synchronized String getHtml() {
		if (null != html) {
			return html;
		}

		final String fontName = "ＭＳ ゴシック";

		final StringBuilderEx sPreHeader = new StringBuilderEx();
		sPreHeader.appendln("Version:0.9");
		sPreHeader.appendln("StartHTML:%010d", 0);
		sPreHeader.appendln("EndHTML:%010d", 0);
		sPreHeader.appendln("StartFragment:%010d", 0);
		sPreHeader.appendln("EndFragment:%010d", 0);
		sPreHeader.appendln("SourceURL:Munchkin");

		final StringBuilderEx sHtmlStart = new StringBuilderEx();
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
		sHtmlStart.append(" margin:.75in .7in .75in .7in;");
		sHtmlStart.append(" mso-header-margin:.3in;");
		sHtmlStart.append(" mso-footer-margin:.3in;");
		sHtmlStart.append("}");
		sHtmlStart.append("ruby");
		sHtmlStart.append("{");
		sHtmlStart.append(" ruby-align:left;");
		sHtmlStart.append("}");
		sHtmlStart.append("table");
		sHtmlStart.append("{");
		sHtmlStart.append(" mso-displayed-decimal-separator:\"\\.\";");
		sHtmlStart.append(" mso-displayed-thousand-separator:\"\\,\";");
		sHtmlStart.append("}");
		sHtmlStart.append("rt");
		sHtmlStart.append("{");
		sHtmlStart.append(" color:windowtext;");
		sHtmlStart.append(" font-size:6.0pt;");
		sHtmlStart.append(" font-weight:400;");
		sHtmlStart.append(" font-style:normal;");
		sHtmlStart.append(" text-decoration:none;");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append(" mso-char-type:katakana;");
		sHtmlStart.append(" display:none;");
		sHtmlStart.append("}");
		sHtmlStart.append("td");
		sHtmlStart.append("{");
		sHtmlStart.append(" padding-top:1px;");
		sHtmlStart.append(" padding-right:1px;");
		sHtmlStart.append(" padding-left:1px;");
		sHtmlStart.append(" mso-ignore:padding;");
		sHtmlStart.append(" color:black;");
		sHtmlStart.append(" font-size:12.0pt;");
		sHtmlStart.append(" font-weight:400;");
		sHtmlStart.append(" font-style:normal;");
		sHtmlStart.append(" text-decoration:none;");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append(" mso-number-format:General;");
		sHtmlStart.append(" text-align:general;");
		sHtmlStart.append(" vertical-align:bottom;");
		sHtmlStart.append(" border:none;");
		sHtmlStart.append(" mso-background-source:auto;");
		sHtmlStart.append(" mso-pattern:auto;");
		sHtmlStart.append(" mso-protection:locked visible;");
		sHtmlStart.append(" white-space:nowrap;");
		sHtmlStart.append(" mso-rotate:0;");
		sHtmlStart.append("}");
		sHtmlStart.append(".xl63");
		sHtmlStart.append("{");
		sHtmlStart.append(" font-weight:700;");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-generic-font-family:auto;");
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append(" mso-number-format:\"\\@\";");
		sHtmlStart.append(" border:.5pt solid windowtext;");
		sHtmlStart.append(" background:#D8E4BC;");
		sHtmlStart.append(" mso-pattern:black none;");
		sHtmlStart.append("}");
		sHtmlStart.append(".xl64");
		sHtmlStart.append("{");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-generic-font-family:auto;");
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append(" mso-number-format:\"\\@\";");
		sHtmlStart.append(" border:.5pt solid windowtext;");
		sHtmlStart.append("}");
		sHtmlStart.append(".font6");
		sHtmlStart.append("{");
		sHtmlStart.append(" color:black;");
		sHtmlStart.append(" font-size:12.0pt;");
		sHtmlStart.append(" font-weight:400;");
		sHtmlStart.append(" font-style:normal;");
		sHtmlStart.append(" text-decoration:none;");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-generic-font-family:auto;");
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append("}");
		sHtmlStart.append(".font7");
		sHtmlStart.append("{");
		sHtmlStart.append(" color:red;");
		sHtmlStart.append(" font-size:12.0pt;");
		sHtmlStart.append(" font-weight:400;");
		sHtmlStart.append(" font-style:normal;");
		sHtmlStart.append(" text-decoration:none;");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-generic-font-family:auto;");
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append("}");
		sHtmlStart.append(".font8");
		sHtmlStart.append("{");
		sHtmlStart.append(" color:blue;");
		sHtmlStart.append(" font-size:12.0pt;");
		sHtmlStart.append(" font-weight:700;");
		sHtmlStart.append(" font-style:normal;");
		sHtmlStart.append(" text-decoration:none;");
		sHtmlStart.append(" font-family:\"%s\";", fontName);
		sHtmlStart.append(" mso-generic-font-family:auto;");
		sHtmlStart.append(" mso-font-charset:128;");
		sHtmlStart.append("}");
		sHtmlStart.append("-->");
		sHtmlStart.append("</style>");
		sHtmlStart.append("</head>");
		sHtmlStart.append("<body link=blue vlink=purple>");
		sHtmlStart.append("<table border=0 cellpadding=0 cellspacing=0 style='border-collapse: collapse;'>");

		final StringBuilderEx sFrag = new StringBuilderEx();
		sFrag.append("<!--StartFragment-->");
		sFrag.append("<tr>");
		for (int col = 0; col < columns.size(); col++) {
			sFrag.append("<td class=xl63 ");
			if (0 == col) {
				sFrag.append("style=''");
			} else {
				sFrag.append("style='border-left:none;'");
			}
			sFrag.append(">");
			sFrag.append(es1(columns.get(col)));
			sFrag.append("</td>");
		}
		sFrag.append("</tr>");
		for (int row = 0; row < records.size(); row++) {
			sFrag.append("<tr>");
			final List<String> record = records.get(row);
			for (int col = 0; col < record.size(); col++) {
				sFrag.append("<td class=xl64 ");
				if (0 == col) {
					sFrag.append("style='border-top:none'");
				} else {
					sFrag.append("style='border-top:none;border-left:none'");
				}
				sFrag.append(">");
				sFrag.append(es2(record.get(col)));
				sFrag.append("</td>");
			}
			sFrag.append("</tr>");
		}
		sFrag.append("<!--EndFragment-->");

		final StringBuilderEx sHtmlEnd = new StringBuilderEx();
		sHtmlEnd.append("</table>");
		sHtmlEnd.append("</body>");
		sHtmlEnd.append("</html>");

		final int startHTML = sPreHeader.length();
		final int startFragment = startHTML + sHtmlStart.length();
		final int endFragment = startFragment + sFrag.length();
		final int endHTML = endFragment + sHtmlEnd.length();

		final StringBuilderEx sHeader = new StringBuilderEx();
		sHeader.appendln("Version:0.9");
		sHeader.appendln("StartHTML:%010d", startHTML);
		sHeader.appendln("EndHTML:%010d", endHTML);
		sHeader.appendln("StartFragment:%010d", startFragment);
		sHeader.appendln("EndFragment:%010d", endFragment);
		sHeader.appendln("SourceURL:Munchkin");

		final StringBuilderEx s = new StringBuilderEx();
		s.append(sHeader.toString());
		s.append(sHtmlStart.toString());
		s.append(sFrag.toString());
		s.append(sHtmlEnd.toString());
		html = s.toString();
		return html;
	}

	private String es1(final String val) {
		return val;
	}

	private String es2(final String val) {
		// &nbsp;
		StringBuffer s = new StringBuffer();
		if (null == val) {
			s.append("<font class=\"font8\">(NULL)</font>");
		} else {
			final Matcher m = PTN.matcher(val);
			int i = 0;
			while (m.find(i)) {
				int start = m.start(1);
				int end = m.end(1);
				if (i < start) {
					s.append("<font class=\"font6\">");
					s.append(val.substring(i, start));
					s.append("</font>");
				}
				s.append("<font class=\"font7\">");
				for (int j = start; j < end; j++) {
					s.append("_");
				}
				s.append("</font>");
				i = end;
			}
			if (i < val.length()) {
				s.append("<font class=\"font6\">");
				s.append(val.substring(i));
				s.append("</font>");
			}
		}
		return s.toString();
	}
}
