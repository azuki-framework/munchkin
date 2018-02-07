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
package org.azkfw.munchkin.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.azkfw.munchkin.message.Label;

/**
 * このクラスは、設定ダイアログの基底クラスです。
 * 
 * @author Kawakicchi
 */
public abstract class ConfigurationListDialog<ENTITY> extends BaseDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = 7275592143128616426L;

	/** 設定ダイアログイベント */
	private BaseConfigurationListDialogEvent<ENTITY> event;

	/** 設定ダイアログリスナー */
	private List<ConfigurationListDialogListener<ENTITY>> listeners;

	/**
	 * コンストラクタ
	 *
	 * @param frame
	 */
	public ConfigurationListDialog(final Frame frame) {
		super(frame);
	}

	/**
	 * コンストラクタ
	 *
	 * @param dialog
	 */
	public ConfigurationListDialog(final Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInit() {
		super.doInit();

		event = new BaseConfigurationListDialogEvent<ENTITY>(this);
		listeners = new ArrayList<ConfigurationListDialogListener<ENTITY>>();
		listeners.add(new ConfigurationListDialogAdapter<ENTITY>() {
			@Override
			public void configurationListDialogCheck(final ConfigurationListDialogEvent<ENTITY> event,
					final List<ENTITY> entites) {
				if (!check(entites)) {
					event.cancel();
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				cancel();
			}
		});

		initComponent();
	}

	/**
	 * 設定ダイアログリスナーを追加する。
	 *
	 * @param listener リスナー
	 */
	public final synchronized void addConfigurationListDialogListener(
			final ConfigurationListDialogListener<ENTITY> listener) {
		listeners.add(listener);
	}

	public final void setConfiguration(final List<ENTITY> entites) {
		setEntites(entites);
	}

	protected abstract void initContainer(final Container container);

	protected abstract boolean check(final List<ENTITY> entites);

	protected abstract void setEntites(final List<ENTITY> entites);

	protected abstract List<ENTITY> getEntites();

	private void cancel() {
		synchronized (listeners) {
			event.setCancel(false);
			for (ConfigurationListDialogListener<ENTITY> listener : listeners) {
				listener.configurationListDialogCancel(event);
			}
		}
		close();
	}

	private void ok() {
		List<ENTITY> entites = getEntites();
		synchronized (listeners) {
			event.setCancel(false);
			for (ConfigurationListDialogListener<ENTITY> listener : listeners) {
				listener.configurationListDialogCheck(event, entites);
			}
		}
		if (event.isCancel()) {
			return;
		}
		synchronized (listeners) {
			event.setCancel(false);
			for (ConfigurationListDialogListener<ENTITY> listener : listeners) {
				listener.configurationListDialogOk(event, entites);
			}
		}
		if (event.isCancel()) {
			return;
		}
		close();
	}

	private void close() {
		dispose();
	}

	private void initComponent() {
		setLayout(new BorderLayout());

		Container c = new Container();

		JPanel controller = new JPanel();
		controller.setLayout(new BorderLayout());
		controller.setPreferredSize(new Dimension(0, 32));

		JPanel pnl = new JPanel();
		pnl.setLayout(new GridLayout(1, 2));
		JButton btnOK = new JButton(Label.BUTTON_OK.toString());
		JButton btnCancel = new JButton(Label.BUTTON_CANCEL.toString());
		btnOK.setPreferredSize(new Dimension(120, 0));
		pnl.add(btnCancel);
		pnl.add(btnOK);

		controller.add(BorderLayout.EAST, pnl);

		add(BorderLayout.CENTER, c);
		add(BorderLayout.SOUTH, controller);

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				onClickOK();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				onClickCancel();
			}
		});

		initContainer(c);
	}

	private void onClickOK() {
		ok();
	}

	private void onClickCancel() {
		cancel();
	}

	private static class BaseConfigurationListDialogEvent<ENTITY> implements ConfigurationListDialogEvent<ENTITY> {

		private ConfigurationListDialog<ENTITY> source;

		private boolean cancel;

		public BaseConfigurationListDialogEvent(final ConfigurationListDialog<ENTITY> source) {
			this.source = source;
		}

		@Override
		public ConfigurationListDialog<ENTITY> getSource() {
			return source;
		}

		@Override
		public void cancel() {
			cancel = true;
		}

		public void setCancel(final boolean cancel) {
			this.cancel = cancel;
		}

		@Override
		public boolean isCancel() {
			return cancel;
		}
	}
}
