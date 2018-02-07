package org.azkwf.swing.dialog.configuration;

/**
 * このインターフェースは、コンフィグレーションダイアログのリスナーインターフェースです。
 *
 * @author N.Kawakita
 *
 * @param <CONFIGURATION>
 */
public interface ConfigurationDialogListener<CONFIGURATION> {

	boolean configurationDialogValidate(ConfigurationDialogEvent<CONFIGURATION> event);

	void configurationDialogOk(ConfigurationDialogEvent<CONFIGURATION> event);

	void configurationDialogCancel(ConfigurationDialogEvent<CONFIGURATION> event);
}
