/*
 * Licensed to the Apache Softwarimport java.awt.BorderLayout;
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
s required by applicable law or agreed to in writing, software
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
 * @param <ENTITY> 設定情報
 */
public abstract class ConfigurationDialog<ENTITY> extends BaseDialog {

	/** serialVersionUID */
	private static final long serialVersionUID = 3271337200066073948L;

	/** 設定ダイアログイベント */
	private BaseConfigurationDialogEvent<ENTITY> event;

	/** 設定ダイアログリスナー */
	private List<ConfigurationDialogListener<ENTITY>> listeners;

	/**
	 * コンストラクタ
	 *
	 * @param frame
	 */
	public ConfigurationDialog(final Frame frame) {
		super(frame);
	}

	/**
	 * コンストラクタ
	 *
	 * @param dialog
	 */
	public ConfigurationDialog(final Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInit() {
		super.doInit();

		event = new BaseConfigurationDialogEvent<ENTITY>(this);
		listeners = new ArrayList<ConfigurationDialogListener<ENTITY>>();
		listeners.add(new ConfigurationDialogAdapter<ENTITY>() {
			@Override
			public void configurationDialogCheck(final ConfigurationDialogEvent<ENTITY> event, final ENTITY entity) {
				if (!check(entity)) {
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
	public final synchronized void addConfigurationDialogListener(final ConfigurationDialogListener<ENTITY> listener) {
		listeners.add(listener);
	}

	public final void setConfiguration(final ENTITY entity) {
		setEntity(entity);
	}

	protected abstract void initContainer(final Container container);

	protected abstract boolean check(final ENTITY entity);

	protected abstract void setEntity(final ENTITY entity);

	protected abstract ENTITY getEntity();

	private void cancel() {
		synchronized (listeners) {
			event.setCancel(false);
			for (ConfigurationDialogListener<ENTITY> listener : listeners) {
				listener.configurationDialogCancel(event);
			}
		}
		close();
	}

	private void ok() {
		ENTITY entity = getEntity();
		synchronized (listeners) {
			event.setCancel(false);
			for (ConfigurationDialogListener<ENTITY> listener : listeners) {
				listener.configurationDialogCheck(event, entity);
			}
		}
		if (event.isCancel()) {
			return;
		}
		synchronized (listeners) {
			event.setCancel(false);
			for (ConfigurationDialogListener<ENTITY> listener : listeners) {
				listener.configurationDialogOk(event, entity);
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

	private static class BaseConfigurationDialogEvent<ENTITY> implements ConfigurationDialogEvent<ENTITY> {

		private ConfigurationDialog<ENTITY> source;

		private boolean cancel;

		public BaseConfigurationDialogEvent(final ConfigurationDialog<ENTITY> source) {
			this.source = source;
		}

		@Override
		public ConfigurationDialog<ENTITY> getSource() {
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
