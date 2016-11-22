package org.azkwf.swing.dialog.configuration;

/**
 * このインターフェースは、コンフィグレーションダイアログのリスナーインターフェースです。
 *
 * @author N.Kawakita
 *
 * @param <CONFIGURATION>
 */
public class ConfigurationDialogAdapter<CONFIGURATION> implements ConfigurationDialogListener<CONFIGURATION> {

	public boolean configurationDialogValidate(ConfigurationDialogEvent<CONFIGURATION> event) {
		return true;
	}

	public void configurationDialogOk(ConfigurationDialogEvent<CONFIGURATION> event) {

	}

	public void configurationDialogCancel(ConfigurationDialogEvent<CONFIGURATION> event) {

	}
}
