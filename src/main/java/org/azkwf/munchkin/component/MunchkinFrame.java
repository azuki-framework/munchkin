package org.azkwf.munchkin.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import org.azkwf.munchkin.Munchkin;
import org.azkwf.munchkin.entity.ObjectEntity;
import org.azkwf.munchkin.model.DatabaseModel;
import org.azkwf.munchkin.model.OracleDatabaseModel;

/**
 *
 * @author Kawakicchi
 */
public class MunchkinFrame extends JFrame {

	private DatabaseModel model;

	private JSplitPane split;

	private ObjectPanel pnlObject;

	private SQLQueryPanel pnlQuery;

	private StatusBar statusbar;

	private JMenuBar menubar;

	private JMenu menuFile;

	private JMenu menuEdit;

	private JMenu menuTool;

	private JMenu menuToolSql;

	private JMenuItem menuToolSqlSessionList;
	private JMenuItem menuToolSqlSessionKill;

	private JMenu menuHelp;

	private JMenuItem menuFileConnection;

	private JMenuItem menuFileExit;

	public MunchkinFrame() {
		setTitle("Munchkin");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		model = new OracleDatabaseModel(Munchkin.getInstance().getDatasource());

		pnlObject = new ObjectPanel(model);
		pnlQuery = new SQLQueryPanel(model);

		statusbar = new StatusBar();
		add(statusbar, BorderLayout.SOUTH);

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(pnlObject);
		split.setRightComponent(pnlQuery);
		split.setDividerLocation(360);
		add(split, BorderLayout.CENTER);

		initMenubar();

		pnlObject.addObjectPanelListener(new ObjectPanelListener() {
			@Override
			public void objectPanelOnDoubleClick(ObjectEntity object) {
				pnlQuery.insertText(object.getName());
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				String msg = String.format("%s : %s", Munchkin.getInstance().getDatasource().getUsername(), Munchkin.getInstance().getDatasource().getJdbcUrl());
				statusbar.setMessage(msg);
			}
		});

		setBounds(0, 0, 1000, 600);
	}

	private void initMenubar() {
		menubar = new JMenuBar();
		setJMenuBar(menubar);

		menuFile = new JMenu("ファイル");
		menubar.add(menuFile);
		menuEdit = new JMenu("編集");
		menubar.add(menuEdit);
		menuTool = new JMenu("ツール");
		menubar.add(menuTool);
		menuHelp = new JMenu("ヘルプ");
		menubar.add(menuHelp);

		menuToolSql = new JMenu("SQL");
		menuTool.add(menuToolSql);

		menuToolSqlSessionList = new JMenuItem("セッション一覧");
		menuToolSql.add(menuToolSqlSessionList);
		menuToolSqlSessionKill = new JMenuItem("セッション切断");
		menuToolSql.add(menuToolSqlSessionKill);

		menuFileConnection = new JMenuItem("接続");
		menuFile.add(menuFileConnection);

		menuFile.addSeparator();

		menuFileExit = new JMenuItem("終了");
		menuFile.add(menuFileExit);

		menuFileConnection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openConnection();
			}
		});

		menuToolSqlSessionList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlQuery.insertText("select * from v$session\n");
			}
		});
		menuToolSqlSessionKill.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlQuery.insertText("alter system kill session 'nnn,nnn' immediate\n");
			}
		});
	}

	private void openConnection() {

	}
}
