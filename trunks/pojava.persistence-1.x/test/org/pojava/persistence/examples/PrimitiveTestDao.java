package org.pojava.persistence.examples;

import java.sql.SQLException;
import java.util.List;

import org.pojava.lang.Processor;
import org.pojava.persistence.query.SqlQuery;
import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.DatabaseTransaction;
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

	public static int insert(DatabaseTransaction trans, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.insert(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int update(DatabaseTransaction trans, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.update(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int updateInsert(DatabaseTransaction trans, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.updateInsert(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int passiveInsert(DatabaseTransaction trans, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.passiveInsert(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int delete(DatabaseTransaction trans, PrimitiveTest obj)
			throws SQLException {
		return DaoTool.delete(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static PrimitiveTest find(DatabaseTransaction trans,
			PrimitiveTest obj) throws SQLException {
		return (PrimitiveTest) DaoTool.find(trans.getConnection(DS_NAME), MAP,
				obj);
	}

	public static List listByQuery(DatabaseTransaction trans, SqlQuery query)
			throws SQLException {
		return DaoTool.listByQuery(trans.getConnection(DS_NAME), MAP, query
				.generatePreparedSql(MAP.sqlSelect()));
	}

	public static int deleteByQuery(DatabaseTransaction trans, SqlQuery query)
			throws SQLException {
		return DaoTool.deleteByQuery(trans.getConnection(DS_NAME), query
				.generatePreparedSql("DELETE FROM " + MAP.getTableName()));
	}

	public static int countByQuery(DatabaseTransaction trans, SqlQuery query)
			throws SQLException {
		return DaoTool.intQuery(trans.getConnection(DS_NAME), query
				.generatePreparedSql("SELECT COUNT(*) FROM "
						+ MAP.getTableName()));
	}

	public static int processByQuery(DatabaseTransaction trans, SqlQuery query,
			Processor objProcessor) throws SQLException {
		return DaoTool.processByQuery(trans.getConnection(DS_NAME), MAP, query
				.generatePreparedSql(MAP.sqlSelect()), objProcessor);
	}

}
