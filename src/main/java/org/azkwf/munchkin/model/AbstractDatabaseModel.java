package org.azkwf.munchkin.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Kawakicchi
 */
public abstract class AbstractDatabaseModel implements DatabaseModel {

	protected final void release(final Connection c, final PreparedStatement p, final ResultSet r) {
		release(r);
		release(p);
		release(c);
	}

	protected final void release(final ResultSet r) {
		if (null != r) {
			try {
				r.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected final void release(final PreparedStatement p) {
		if (null != p) {
			try {
				p.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected final void release(final Connection c) {
		if (null != c) {
			try {
				c.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}
