package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.azkwf.munchkin.Munchkin;
import org.azkwf.munchkin.model.DatabaseModel;

/**
 * このクラスは、SQLクエリーパネルクラスです。
 *
 * @author Kawakicchi
 */
public class SQLQueryPanel extends JPanel {

	private DatabaseModel model;

	private JSplitPane split;

	private JScrollPane scrollQuery;

	private JTextPane textQuery;

	private JScrollPane scrollResult;

	private JTable tableResult;

	private DefaultTableModel tmodel;

	public SQLQueryPanel(final DatabaseModel model) {
		this.model = model;
		setLayout(new BorderLayout());

		textQuery = new JTextPane();
		textQuery.setText("select * from tm_ssk where rownum < 10");
		textQuery.setFont(MunchkinStyle.getDefaultFont());
		scrollQuery = new JScrollPane(textQuery);

		tmodel = new DefaultTableModel();
		tableResult = new JTable(tmodel);
		tableResult.setFont(MunchkinStyle.getDefaultFont());
		tableResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollResult = new JScrollPane(tableResult);

		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(scrollQuery);
		split.setBottomComponent(scrollResult);
		split.setDividerLocation(400);
		add(split, BorderLayout.CENTER);

		textQuery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				int mod = e.getModifiersEx();
				if ((mod & InputEvent.CTRL_DOWN_MASK) != 0) {
					if (keycode == KeyEvent.VK_E) {
						doExecute();
						e.consume();
					}
				}
			}
		});

		tableResult.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				int KeyCode = e.getKeyCode();
				if (KeyCode == KeyEvent.VK_C && e.isControlDown()) {
					doCopy();
					e.consume();
				}
			}
		});
	}

	public void insertText(final String string) {
		Document doc = textQuery.getDocument();

		try {
			doc.insertString(textQuery.getCaretPosition(), string, null);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	private void doExecute() {
		String sql = textQuery.getSelectedText();
		if (null == sql || 0 == sql.length()) {
			sql = textQuery.getText();
		}
		if (0 < sql.length()) {
			query(sql);
		}
	}

	private void doCopy() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();

		TableSelection is = new TableSelection(tmodel);
		clip.setContents(is, is);
	}

	private void query(final String sql) {
		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			c = Munchkin.getInstance().getDatasource().getConnection();
			p = c.prepareStatement(sql);
			r = p.executeQuery();
			r.setFetchSize(1000);

			test(r);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (null != r) {
				try {
					r.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (null != p) {
				try {
					p.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (null != c) {
				try {
					c.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void test(final ResultSet r) throws SQLException {
		ResultSetMetaData m = r.getMetaData();

		tmodel.setColumnCount(0);
		tmodel.setRowCount(0);

		for (int col = 1; col <= m.getColumnCount(); col++) {
			tmodel.addColumn(m.getColumnLabel(col));
		}

		FontMetrics fm = getFontMetrics(tableResult.getFont());
		for (int col = 1; col <= m.getColumnCount(); col++) {
			int width = fm.stringWidth(m.getColumnLabel(col));
			tableResult.getColumnModel().getColumn(col - 1).setPreferredWidth(width + 20);
		}

		while (r.next()) {
			Object[] data = new Object[m.getColumnCount()];

			for (int col = 1; col <= m.getColumnCount(); col++) {
				Object obj = r.getObject(col);

				if (null != obj) {
					data[col - 1] = String.valueOf(obj);
				} else {
					data[col - 1] = new NULLObject();
				}
			}

			tmodel.addRow(data);
		}
	}

	public static class NULLObject {
		@Override
		public String toString() {
			return "(NULL)";
		}
	}

	public static class TableSelection implements Transferable, ClipboardOwner {

		private String plain;
		private String html;

		/** コンストラクター */
		public TableSelection(final DefaultTableModel model) {

			StringBuffer plain = new StringBuffer();
			for (int col = 0; col < model.getColumnCount(); col++) {
				if (0 != col) {
					plain.append("\t");
				}
				plain.append(String.format("%s", model.getColumnName(col)));
			}
			plain.append("\n");
			for (int row = 0; row < model.getRowCount(); row++) {
				for (int col = 0; col < model.getColumnCount(); col++) {
					if (0 != col) {
						plain.append("\t");
					}
					plain.append(String.format("%s", model.getValueAt(row, col)));
				}
				plain.append("\n");
			}
			this.plain = plain.toString();

			HtmlStringBuffer html = new HtmlStringBuffer();
			html.append("<html>");
			html.append("<head>");
			html.append("<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
			html.append("<meta name=ProgId content=Excel.Sheet>");
			html.append("<meta name=Generator content=\"Microsoft Excel 14\">");

			html.append("<style type=\"text/css\">");
			html.append("<!--");
			html.append(".grid {");
			html.append(" border-top:.5pt solid windowtext;");
			html.append(" border-bottom:.5pt solid windowtext;");
			html.append(" border-left:.5pt solid windowtext;");
			html.append(" border-right:.5pt solid windowtext;");
			html.append("}");
			html.append(".line0 {");
			html.append(" background:#C6EFCE;");
			html.append(" border-bottom:.5pt solid windowtext;");
			html.append("}");
			html.append(".line1 {");
			html.append(" border-bottom:.5pt hairline windowtext;");
			html.append("}");
			html.append(".line2 {");
			html.append(" background:#EEF0FF;");
			html.append(" border-bottom:.5pt hairline windowtext;");
			html.append("}");
			html.append(".header {");
			html.append(" mso-number-format:\"\\@\";");
			html.append(" white-space: nowrap;");
			html.append(" font-weight:700;");
			html.append(" border-left:.5pt solid windowtext;");
			html.append(" border-right:.5pt solid windowtext;");
			html.append("}");
			html.append(".data {");
			html.append(" mso-number-format:\"\\@\";");
			html.append(" white-space: nowrap;");
			html.append(" border-left:.5pt solid windowtext;");
			html.append(" border-right:.5pt solid windowtext;");
			html.append("}");
			html.append(".null {");
			html.append(" mso-number-format:\"\\@\";");
			html.append(" white-space: nowrap;");
			html.append(" border-left:.5pt solid windowtext;");
			html.append(" border-right:.5pt solid windowtext;");
			html.append(" color:#437DFF;");
			html.append(" font-weight:700;");
			html.append("}");
			html.append("-->");
			html.append("</style>");

			html.append("</head>");
			html.append("<body>");
			html.append("<table class=\"grid\">");

			html.append("<tr class=\"line0\" >");
			for (int col = 0; col < model.getColumnCount(); col++) {
				html.append("<td  class=\"header\">%s</td>", model.getColumnName(col));
			}
			html.append("</tr>");

			for (int row = 0; row < model.getRowCount(); row++) {
				if (0 == row % 2) {
					html.append("<tr class=\"line1\">");
				} else {
					html.append("<tr class=\"line2\">");
				}
				for (int col = 0; col < model.getColumnCount(); col++) {
					Object obj = model.getValueAt(row, col);
					if (obj instanceof NULLObject) {
						html.append("<td class=\"null\">%s</td>", obj);
					} else {
						html.append("<td class=\"data\">%s</td>", obj);
					}
				}
				html.append("</tr>");
			}

			html.append("</table>");
			html.append("</body>");
			html.append("</html>");

			this.html = html.toString();

		}

		/** 対応しているフレーバーを返す */
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor plainFlavor = new DataFlavor("text/plain; class=java.lang.String; charset=Unicode", "text/plain");
			DataFlavor htmlFlavor = new DataFlavor("text/html; class=java.lang.String; charset=Unicode", "text/html");
			return new DataFlavor[] { plainFlavor, htmlFlavor };
		}

		/** フレーバーが対応しているかどうか */
		public boolean isDataFlavorSupported(DataFlavor flavor) {

			if ("text/html".equals(flavor.getHumanPresentableName())) {
				return true;
			} else if ("text/plain".equals(flavor.getHumanPresentableName())) {
				return true;
			}
			return false;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if ("text/html".equals(flavor.getHumanPresentableName())) {
				return html;
			} else if ("text/plain".equals(flavor.getHumanPresentableName())) {
				return plain;
			}
			return "";
		}

		/** クリップボードのデータとして不要になった時に呼ばれる */
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
			plain = null;
			html = null;
		}
	}

	public static class HtmlStringBuffer {
		private StringBuffer s;

		public HtmlStringBuffer() {
			s = new StringBuffer();
		}

		public void append(final String str) {
			s.append(str);
			s.append("\n");
		}

		public void append(final String format, final Object... args) {
			s.append(String.format(format, args));
			s.append("\n");
		}

		public synchronized String toString() {
			return s.toString();
		}
	}
}
