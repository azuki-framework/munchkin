package org.azkwf.munchkin.component;

import java.awt.BorderLayout;

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

	private JMenu menuHelp;

	private JMenuItem menuFileExit;

	public MunchkinFrame() {
		setTitle("DBツール");
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

		menuFileExit = new JMenuItem("終了");
		menuFile.add(menuFileExit);

		pnlObject.addObjectPanelListener(new ObjectPanelListener() {
			@Override
			public void objectPanelOnDoubleClick(ObjectEntity object) {
				pnlQuery.insertText(object.getName());
			}
		});

		setBounds(0, 0, 1000, 600);
	}
}
