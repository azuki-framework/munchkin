package org.azkwf.swing.dialog.configuration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * このクラスは、コンフィグレーションダイアログのダイアログクラスです。
 *
 * @author N.Kawakita
 *
 * @param <CONFIGURATION>
 */
public class ConfigurationDialog<CONFIGURATION> extends JDialog {

	public static void main(final String[] args) {
		ConfigurationDialog<String> dlg = new ConfigurationDialog<String>();
		dlg.setVisible(true);
	}

	/** event */
	private ConfigurationDialogEvent<CONFIGURATION> event;

	/** listener list */
	private List<ConfigurationDialogListener<CONFIGURATION>> listeners;

	private JPanel pnlClient;
	private JPanel pnlControl;

	private JButton btnOk;
	private JButton btnCancel;

	public ConfigurationDialog() {
		super();
		init();
	}

	public ConfigurationDialog(final Frame owner) {
		super(owner);
		init();
	}

	public synchronized final void addConfigurationDialogListener(final ConfigurationDialogListener<CONFIGURATION> listener) {
		listeners.add(listener);
	}

	protected void doInitialize() {

	}

	protected boolean doValidate() {
		return true;
	}

	protected final boolean callValidate() {
		synchronized (listeners) {
			for (ConfigurationDialogListener<CONFIGURATION> listener : listeners) {
				if (!listener.configurationDialogValidate(event)) {
					return false;
				}
			}
		}
		return true;
	}

	protected final void callOk() {
		synchronized (listeners) {
			for (ConfigurationDialogListener<CONFIGURATION> listener : listeners) {
				listener.configurationDialogOk(event);
			}
		}
	}

	protected final void callCancel() {
		synchronized (listeners) {
			for (ConfigurationDialogListener<CONFIGURATION> listener : listeners) {
				listener.configurationDialogCancel(event);
			}
		}
	}

	private void init() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		event = new ConfigurationDialogEvent<CONFIGURATION>(this);
		listeners = new ArrayList<ConfigurationDialogListener<CONFIGURATION>>();

		setLayout(new BorderLayout());

		pnlClient = new JPanel();

		pnlControl = new JPanel();
		pnlControl.setPreferredSize(new Dimension(0, 36));

		add(BorderLayout.CENTER, pnlClient);
		add(BorderLayout.SOUTH, pnlControl);

		btnOk = new JButton("OK");
		btnCancel = new JButton("キャンセル");

		pnlControl.setLayout(null);
		pnlControl.add(btnOk);
		pnlControl.add(btnCancel);

		pnlControl.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				JPanel pnl = (JPanel) e.getSource();
				Insets insets = pnl.getInsets();
				int width = pnl.getWidth() - (insets.left + insets.right);
				int height = pnl.getHeight() - (insets.top + insets.bottom);
				btnOk.setLocation(width - (120 + 10 + 120 + 8), 6);
				btnCancel.setLocation(width - (120 + 8), 6);
				btnOk.setSize(120, height - (6 + 6));
				btnCancel.setSize(120, height - (6 + 6));
			}
		});

		listeners.add(new ConfigurationDialogAdapter<CONFIGURATION>() {
			@Override
			public boolean configurationDialogValidate(ConfigurationDialogEvent<CONFIGURATION> event) {
				return doValidate();
			}
		});

		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (btnOk) {
					btnOk.setEnabled(false);
					ok();
					btnOk.setEnabled(true);
				}
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (btnCancel) {
					btnCancel.setEnabled(false);
					cancel();
					btnCancel.setEnabled(true);
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				synchronized (btnCancel) {
					btnCancel.setEnabled(false);
					cancel();
					btnCancel.setEnabled(true);
				}
			}
		});

		// XXX: ここから下はテストコード
		setSize(600, 260);
	}

	private void ok() {
		if (callValidate()) {
			callOk();
			dispose();
		}
	}

	private void cancel() {
		callCancel();
		dispose();
	}

}
