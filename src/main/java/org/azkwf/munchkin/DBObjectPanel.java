/**
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
package org.azkwf.munchkin;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * @author Kawakicchi
 *
 */
public class DBObjectPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -4509124507335179464L;

	private static final int LABEL_WIDTH = 52;
	
	private DBObjectModel model;
	
	private DefaultTableModel modelObjectTable;
	private DefaultTableModel modelDetailTable;
	
	private JLabel lblUser;
	private JComboBox<String> lstUser;
	private JLabel lblType;
	private JComboBox<String> lstType;
	
	private JSplitPane split;
	
	private JPanel pnlObject;
	private JTextField txtObjectFilter;	
	private JScrollPane sclObject;
	private JTable tblObject;
	
	private JPanel pnlDetail;
	private JTextField txtDetailFilter;
	private JScrollPane sclDetail;
	private JTable tblDetail;
	
	public DBObjectPanel(final DBObjectModel model) {
		setLayout(null);
		
		this.model = model;
		modelObjectTable = new DefaultTableModel();
		modelDetailTable = new DefaultTableModel();
		
		lblUser = new JLabel("User");
		lblUser.setBounds(4, 4, LABEL_WIDTH, 24);
		add(lblUser);
		lstUser = new JComboBox<String>();
		lstUser.setLocation(4 + LABEL_WIDTH, 4);
		add(lstUser);
		
		lblType = new JLabel("Type");
		lblType.setBounds(4, 30, LABEL_WIDTH, 24);
		add(lblType);
		lstType = new JComboBox<String>();
		lstType.setLocation(4 + LABEL_WIDTH, 30);
		add(lstType);

		
		pnlObject = new JPanel();
		pnlObject.setLayout(null);
		txtObjectFilter = new JTextField();
		txtObjectFilter.setLocation(4, 4);
		pnlObject.add(txtObjectFilter);
		tblObject = new JTable(modelObjectTable);
		sclObject = new JScrollPane(tblObject);
		sclObject.setLocation(0, 30);
		pnlObject.add(sclObject);
		
		pnlDetail = new JPanel();
		pnlDetail.setLayout(null);
		txtDetailFilter = new JTextField();
		txtDetailFilter.setLocation(4, 4);
		pnlDetail.add(txtDetailFilter);
		tblDetail = new JTable(modelDetailTable);
		sclDetail = new JScrollPane(tblDetail);
		sclDetail.setLocation(0, 30);
		pnlDetail.add(sclDetail);
		
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setLocation(0, 56);
		split.setTopComponent(pnlObject);
		split.setBottomComponent(pnlDetail);
		split.setDividerLocation(160);
		add(split);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				doResize();
			}
			@Override
			public void componentResized(ComponentEvent e) {
				doResize();
			}			
		});
		pnlObject.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Insets insets = pnlObject.getInsets();
				int width = pnlObject.getWidth() - (insets.left + insets.right);
				int height = pnlObject.getHeight() - (insets.top + insets.bottom);

				txtObjectFilter.setSize(width-4, 24);
				sclObject.setSize(width, height-30);
			}
		});
		pnlDetail.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Insets insets = pnlDetail.getInsets();
				int width = pnlDetail.getWidth() - (insets.left + insets.right);
				int height = pnlDetail.getHeight() - (insets.top + insets.bottom);

				txtDetailFilter.setSize(width-4, 24);
				sclDetail.setSize(width, height-30);
			}
		});
		
		refresh();

	}
	
	private void doResize() {
		Insets insets = getInsets();
		int width = getWidth() - (insets.left + insets.right);
		int height = getHeight() - (insets.top + insets.bottom);

		Dimension dime = new Dimension(width-(LABEL_WIDTH+4+4), 24);
		
		lstUser.setSize(dime);
		lstType.setSize(dime);
		split.setSize(width, height - (56));
	}
	
	private void refresh() {
		lstUser.removeAllItems();
		for (String user : model.getUserList()) {
			lstUser.addItem(user);
		}
		lstType.removeAllItems();
		for (String type : model.getTypeList()) {
			lstType.addItem(type);
		}
	}
}
