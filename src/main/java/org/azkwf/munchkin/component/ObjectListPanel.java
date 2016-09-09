package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.azkwf.munchkin.entity.ObjectEntity;
import org.azkwf.munchkin.model.DatabaseModel;

/**
 *
 * @author Kawakicchi
 */
public class ObjectListPanel extends JPanel {

	private DatabaseModel model;

	private JTextField keyword;

	private JScrollPane scroll;

	private JTable table;

	private DefaultTableModel tmodel;

	private List<ObjectListPanelListener> listeners;

	private List<ObjectEntity> objects;

	public ObjectListPanel(final DatabaseModel model) {
		this.model = model;
		setLayout(new BorderLayout());

		listeners = new ArrayList<ObjectListPanelListener>();

		keyword = new JTextField();
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

		add(keyword, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);

		tmodel.addColumn("OWNER");
		tmodel.addColumn("NAME");
		tmodel.addColumn("TYPE");
		tmodel.addColumn("UPDATE");
		tmodel.addColumn("STATUS");
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);

		keyword.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				filterTable(table, "NAME", keyword.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				filterTable(table, "NAME", keyword.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterTable(table, "NAME", keyword.getText());
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				int sc = table.getSelectedRowCount();
				if (1 == sc) {
					int row = table.convertRowIndexToModel(table.getSelectedRow());
					ObjectEntity o = objects.get(row);

					synchronized (listeners) {
						for (ObjectListPanelListener l : listeners) {
							l.objectListPanelOnChange(o);
						}
					}
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					Point pt = me.getPoint();
					int idx = table.rowAtPoint(pt);
					if (idx >= 0) {
						int row = table.convertRowIndexToModel(idx);
						ObjectEntity o = objects.get(row);

						synchronized (listeners) {
							for (ObjectListPanelListener l : listeners) {
								l.objectListPanelOnDoubleClick(o);
							}
						}
					}
				}
			}
		});
	}

	public synchronized void addObjectListPanelListener(final ObjectListPanelListener listener) {
		listeners.add(listener);
	}

	public void setObjects(final List<ObjectEntity> objects) {
		tmodel.setRowCount(objects.size());

		for (int i = 0; i < objects.size(); i++) {
			ObjectEntity o = objects.get(i);

			tmodel.setValueAt(o.getOwner(), i, 0);
			tmodel.setValueAt(o.getName(), i, 1);
			tmodel.setValueAt(o.getType(), i, 2);
			tmodel.setValueAt(o.getUpdate(), i, 3);
			tmodel.setValueAt(o.getStatus(), i, 4);
		}

		filterTable(table, "NAME", keyword.getText());

		this.objects = objects;
	}

	private void filterTable(final JTable table, final String column, final String keyword) {

		if (0 == keyword.length()) {
			table.setRowSorter(null);

		} else {

			TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(table.getModel());

			table.setRowSorter(rowSorter);
			List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
			int col = table.getColumn(column).getModelIndex();

			String filterVal = String.format(".*%s.*", keyword);

			filters.add(RowFilter.regexFilter(filterVal, col));

			RowFilter<Object, Object> filter = RowFilter.andFilter(filters);
			rowSorter.setRowFilter(filter);

			table.setRowSorter(rowSorter);
		}
	}
}
