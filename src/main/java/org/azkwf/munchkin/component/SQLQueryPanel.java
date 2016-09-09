package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
		textQuery.setText("");
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

		textQuery.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				int mod = e.getModifiersEx();

				if ((mod & InputEvent.CTRL_DOWN_MASK) != 0) {
					if (keycode == KeyEvent.VK_E) {
						doExecute();
					}
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
			String[] data = new String[m.getColumnCount()];

			for (int col = 1; col <= m.getColumnCount(); col++) {
				Object obj = r.getObject(col);

				data[col - 1] = String.valueOf(obj);
			}

			tmodel.addRow(data);
		}
	}
}
