package org.azkwf.munchkin.component;

import java.awt.Font;

/**
 *
 * @author Kawakicchi
 */
public class MunchkinStyle {

	private static final MunchkinStyle INSTANCE = new MunchkinStyle();

	private Font font;

	private MunchkinStyle() {
		font = new Font("MS ゴシック", Font.PLAIN, 12);
	}

	public static Font getDefaultFont() {
		return INSTANCE.font;
	}

}
