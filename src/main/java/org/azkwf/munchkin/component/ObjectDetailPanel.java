package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.azkwf.munchkin.entity.ObjectDetailEntity;
import org.azkwf.munchkin.model.DatabaseModel;

/**
 *
 * @author Kawakicchi
 */
public class ObjectDetailPanel extends JPanel {

	private DatabaseModel model;

	private JScrollPane scroll;

	private JTable table;

	private DefaultTableModel tmodel;

	public ObjectDetailPanel(final DatabaseModel model) {
		this.model = model;
		setLayout(new BorderLayout());

		tmodel = new DefaultTableModel();
		table = new JTable(tmodel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setFont(MunchkinStyle.getDefaultFont());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scroll = new JScrollPane(table);
		add(scroll, BorderLayout.CENTER);
	}

	public void setDetail(final ObjectDetailEntity detail) {
		tmodel.setColumnCount(0);
		tmodel.setRowCount(0);

		List<String> columns = detail.getColumns();
		for (String column : columns) {
			tmodel.addColumn(column);
		}

		List<List<Object>> records = detail.getDatas();
		tmodel.setRowCount(records.size());

		for (int row = 0; row < records.size(); row++) {
			for (int col = 0; col < columns.size(); col++) {
				tmodel.setValueAt(records.get(row).get(col), row, col);
			}
		}
	}
}
