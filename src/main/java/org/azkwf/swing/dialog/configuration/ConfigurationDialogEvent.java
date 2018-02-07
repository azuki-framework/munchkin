package org.azkwf.swing.dialog.configuration;

/**
 * このクラスは、コンフィグレーションダイアログのイベントクラスです。
 * 
 * @author N.Kawakita
 *
 * @param <CONFIGURATION>
 */
public class ConfigurationDialogEvent<CONFIGURATION> {

	/** source */
	private ConfigurationDialog<CONFIGURATION> source;

	public ConfigurationDialogEvent(final ConfigurationDialog<CONFIGURATION> source) {
		this.source = source;
	}

	public ConfigurationDialog<CONFIGURATION> getSource() {
		return this.source;
	}
}
