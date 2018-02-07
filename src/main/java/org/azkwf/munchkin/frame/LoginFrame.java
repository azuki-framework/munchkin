package org.azkwf.munchkin.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.azkwf.munchkin.DatasourceManager;
import org.azkwf.munchkin.entity.DatasourceEntity;

public class LoginFrame extends JFrame {

	public static void main(final String[] args) {
		LoginFrame frm = new LoginFrame();
		frm.setVisible(true);
	}

	private DatasourceListPanel pnlDatasourceList;

	public LoginFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setTitle("ログイン");

		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		pnlDatasourceList = new DatasourceListPanel();

		add(BorderLayout.CENTER, pnlDatasourceList);
		add(BorderLayout.SOUTH, new DatasourcePanel());

		setBounds(10, 10, 400, 500);

		try {
			File file = new File("datasources.properties");
			if (file.isFile()) {
				DatasourceManager.getInstance().load(file);
			}

			pnlDatasourceList.refresh(DatasourceManager.getInstance().getDatasouces());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private class DatasourceListPanel extends JPanel {

		private JTable table;
		private DefaultTableModel model;

		public DatasourceListPanel() {

			setLayout(new BorderLayout());

			model = new DefaultTableModel();
			table = new JTable(model);
			JScrollPane scroll = new JScrollPane(table);

			add(scroll);

			model.addColumn("名前");
			model.addColumn("ユーザ名");
		}

		public void refresh(final List<DatasourceEntity> datasources) {
			model.setRowCount(0);

			for (int i = 0; i < datasources.size(); i++) {
				DatasourceEntity datasource = datasources.get(i);

				Object[] cells = new Object[2];
				cells[0] = datasource.getName();
				cells[1] = datasource.getUser();

				model.addRow(cells);
			}
		}
	}

	private class DatasourcePanel extends JPanel {

		private JTextField txtName;
		private JTextField txtUser;
		private JTextField txtPass;
		private JTextArea txtURL;
		private JComboBox<String> cmbDriver;

		public DatasourcePanel() {
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);

			// コンポーネント同士の間隔を空ける設定
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			txtName = new JTextField();
			txtUser = new JTextField();
			txtPass = new JTextField();
			txtURL = new JTextArea();
			cmbDriver = new JComboBox<String>();

			JComponent[][] compos = { { new JLabel("名前"), txtName }, { new JLabel("ユーザ名"), txtUser }, { new JLabel("パスワード"), txtPass },
					{ new JLabel("接続文字列"), new JScrollPane(txtURL) }, { new JLabel("ドライバ"), cmbDriver }, };

			int ny = compos.length;
			int nx = compos[0].length;

			{
				SequentialGroup hg = layout.createSequentialGroup();
				for (int x = 0; x < nx; x++) {
					ParallelGroup pg = layout.createParallelGroup();
					for (int y = 0; y < ny; y++) {
						JComponent c = compos[y][x];
						if (c != null) {
							pg.addComponent(c);
						}
					}
					hg.addGroup(pg);
				}
				layout.setHorizontalGroup(hg);
			}

			{
				SequentialGroup vg = layout.createSequentialGroup();
				for (int y = 0; y < ny; y++) {
					ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
					for (int x = 0; x < nx; x++) {
						JComponent c = compos[y][x];
						if (c != null) {
							pg.addComponent(c);
						}
					}
					vg.addGroup(pg);
				}
				layout.setVerticalGroup(vg);
			}

			setPreferredSize(new Dimension(1, 200));
		}
	}
}
