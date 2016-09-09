package org.azkwf.munchkin.model;

/**
 *
 * @author Kawakicchi
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.azkwf.munchkin.entity.ObjectDetailEntity;
import org.azkwf.munchkin.entity.ObjectEntity;
import org.azkwf.munchkin.entity.ObjectTypeEntity;
import org.azkwf.munchkin.entity.SchemaEntity;

import com.zaxxer.hikari.HikariDataSource;

public class OracleDatabaseModel extends AbstractDatabaseModel {

	private static final List<ObjectTypeEntity> OBJECT_TYPE_LIST;

	static {
		OBJECT_TYPE_LIST = new ArrayList<ObjectTypeEntity>();

		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("TABLE", "テーブル"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("VIEW", "ビュー"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("MATERIALIZED VIEW", "マテリアライズビュー"));

		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("SYNONYM", "シノニム"));

		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("FUNCTION", "ファンクション"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("PROCEDURE", "プロシージャ"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("PACKAGE", "パッケージ"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("PACKAGE BODY", "パッケージ本体"));

		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("SEQUENCE", "シーケンス"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("TRIGGER", "トリガー"));
		OBJECT_TYPE_LIST.add(new ObjectTypeEntity("INDEX", "インデックス"));
	}

	private HikariDataSource ds;

	public OracleDatabaseModel(final HikariDataSource ds) {
		this.ds = ds;
	}

	@Override
	public String getUser() {
		return ds.getUsername();
	}

	@Override
	public List<SchemaEntity> getSchemas() {
		List<SchemaEntity> schemas = new ArrayList<SchemaEntity>();

		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			c = ds.getConnection();
			p = c.prepareStatement("select USERNAME as NAME from dba_users order by USERNAME");
			r = p.executeQuery();

			while (r.next()) {
				SchemaEntity s = new SchemaEntity();
				s.setName(r.getString("NAME"));
				schemas.add(s);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			release(c, p, r);
		}

		return schemas;
	}

	@Override
	public List<ObjectEntity> getObjects(final SchemaEntity schema, final ObjectTypeEntity objectType) {
		List<ObjectEntity> objects = new ArrayList<ObjectEntity>();

		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			c = ds.getConnection();
			p = c.prepareStatement("select OWNER as OWNER, OBJECT_NAME as NAME, OBJECT_TYPE as TYPE, LAST_DDL_TIME as \"UPDATE\", STATUS as \"STATUS\" from DBA_OBJECTS where OWNER = ? and OBJECT_TYPE = ? order by OBJECT_NAME");
			p.setString(1, schema.getName());
			p.setString(2, objectType.getName());
			r = p.executeQuery();

			while (r.next()) {
				ObjectEntity o = new ObjectEntity();
				o.setOwner(r.getString("OWNER"));
				o.setName(r.getString("NAME"));
				o.setType(r.getString("TYPE"));
				o.setUpdate(r.getDate("UPDATE"));
				o.setStatus(r.getString("STATUS"));
				objects.add(o);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			release(c, p, r);
		}

		return objects;
	}

	@Override
	public ObjectDetailEntity getObjectDetail(final ObjectEntity object) {
		ObjectDetailEntity detail = new ObjectDetailEntity();

		List<String> columns = new ArrayList<String>();
		List<List<Object>> records = new ArrayList<List<Object>>();

		//
		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			c = ds.getConnection();

			if ("TABLE".equals(object.getType())) {
				columns.add("NAME");
				columns.add("TYPE");
				columns.add("LENGTH");
				columns.add("NOTNULL");
				p = c.prepareStatement("select COLUMN_NAME as NAME, DATA_TYPE as TYPE, DATA_LENGTH as LENGTH, DECODE(NULLABLE, 'N', '○', '') as NOTNULL from DBA_TAB_COLUMNS where OWNER = ? and TABLE_NAME = ? order by COLUMN_ID");
				p.setString(1, object.getOwner());
				p.setString(2, object.getName());
				r = p.executeQuery();
				while (r.next()) {
					List<Object> record = new ArrayList<Object>();
					record.add(r.getString("NAME"));
					record.add(r.getString("TYPE"));
					record.add(r.getObject("LENGTH"));
					record.add(r.getString("NOTNULL"));
					records.add(record);
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			release(c, p, r);
		}
		//

		detail.setColumns(columns);
		detail.setDatas(records);
		return detail;
	}

	@Override
	public List<ObjectTypeEntity> getObjectTypes() {
		return new ArrayList<ObjectTypeEntity>(OBJECT_TYPE_LIST);
	}
}
