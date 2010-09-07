package org.pojava.persistence.examples;

import java.sql.SQLException;
import java.util.List;

import org.pojava.lang.Processor;
import org.pojava.persistence.query.SqlQuery;
import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.ConnectionSource;
import org.pojava.persistence.sql.TableMap;
import org.pojava.persistence.util.DaoTool;

public class PrimitiveTestDao {

	private static final Class JAVA_CLASS = PrimitiveTest.class;
	private static final String TABLE_NAME = "type_test";
	private static final String DS_NAME = "pojava_test";

	private static final TableMap MAP = newTableMap();

	public static TableMap newTableMap() {
		TableMap tableMap = DatabaseCache.getTableMap(JAVA_CLASS, TABLE_NAME,
				DS_NAME);
		return tableMap;
	}

	public static int insert(ConnectionSource connector, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.insert(connector.getConnection(DS_NAME), MAP, obj);
	}

	public static int update(ConnectionSource connector, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.update(connector.getConnection(DS_NAME), MAP, obj);
	}

	public static int updateInsert(ConnectionSource connector, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.updateInsert(connector.getConnection(DS_NAME), MAP, obj);
	}

	public static int passiveInsert(ConnectionSource connector,
			PrimitiveTest obj) throws SQLException {
		return DaoTool
				.passiveInsert(connector.getConnection(DS_NAME), MAP, obj);
	}

	public static int delete(ConnectionSource connector, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.delete(connector.getConnection(DS_NAME), MAP, obj);
	}

	public static PrimitiveTest find(ConnectionSource connector,
			PrimitiveTest obj) throws SQLException {
		return (PrimitiveTest) DaoTool.find(connector.getConnection(DS_NAME),
				MAP, obj);
	}

	public static List listByQuery(ConnectionSource connector, SqlQuery query)
			throws SQLException {
		return DaoTool
				.listByQuery(connector.getConnection(DS_NAME), MAP, query);
	}

	public static int deleteByQuery(ConnectionSource connector, SqlQuery query)
			throws SQLException {
		return DaoTool.deleteByQuery(connector.getConnection(DS_NAME), MAP,
				query);
	}

	public static int countByQuery(ConnectionSource connector, SqlQuery query)
			throws SQLException {
		return DaoTool.countByQuery(connector.getConnection(DS_NAME), MAP,
				query);
	}

	public static int processByQuery(ConnectionSource connector,
			SqlQuery query, Processor objProcessor) throws SQLException {
		return DaoTool.processByQuery(connector.getConnection(DS_NAME), MAP,
				query, objProcessor);
	}

}
