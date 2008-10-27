package org.pojava.persistence.examples;

import java.sql.SQLException;
import java.util.List;

import org.pojava.lang.Processor;
import org.pojava.persistence.query.SqlQuery;
import org.pojava.persistence.sql.DatabaseTransaction;
import org.pojava.persistence.sql.TableMap;
import org.pojava.persistence.util.DaoTool;
import org.pojava.persistence.util.SqlTool;

public class TypeTestDao {

	private static final Class JAVA_CLASS = TypeTest.class;
	private static final String TABLE_NAME = "type_test";
	private static final String DS_NAME = "pojava_pg";

	private static final TableMap MAP = newTableMap();

	public static TableMap newTableMap() {
		TableMap tableMap = SqlTool.fetchTableMap(JAVA_CLASS, TABLE_NAME,
				DS_NAME);
		return tableMap;
	}

	public static int insert(DatabaseTransaction trans, TypeTest obj)
			throws SQLException {
		return DaoTool.insert(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int update(DatabaseTransaction trans, TypeTest obj)
			throws SQLException {
		return DaoTool.update(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int updateInsert(DatabaseTransaction trans, TypeTest obj)
			throws SQLException {
		return DaoTool.updateInsert(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int passiveInsert(DatabaseTransaction trans, TypeTest obj)
			throws SQLException {
		return DaoTool.passiveInsert(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static int delete(DatabaseTransaction trans, TypeTest obj)
			throws SQLException {
		return DaoTool.delete(trans.getConnection(DS_NAME), MAP, obj);
	}

	public static TypeTest find(DatabaseTransaction trans, TypeTest obj)
			throws SQLException {
		return (TypeTest) DaoTool.find(trans.getConnection(DS_NAME), MAP, obj);
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
