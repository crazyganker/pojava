package org.pojava.persistence.examples;

import org.pojava.lang.Processor;
import org.pojava.persistence.query.SqlQuery;
import org.pojava.persistence.sql.ConnectionSource;
import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.TableMap;
import org.pojava.persistence.util.DaoTool;

import java.sql.SQLException;
import java.util.List;

public class TypeTestDao {

    private static final Class<TypeTest> JAVA_CLASS = TypeTest.class;
    private static final String TABLE_NAME = "type_test";
    private static final String DS_NAME = "pojava_test";

    private static final TableMap<TypeTest> MAP = newTableMap();

    public static TableMap<TypeTest> newTableMap() {
        TableMap<TypeTest> tableMap = DatabaseCache.getTableMap(
                JAVA_CLASS, TABLE_NAME, DS_NAME);
        return tableMap;
    }

    public static int insert(ConnectionSource connector, TypeTest obj) throws SQLException {
        return DaoTool.insert(connector.getConnection(DS_NAME), MAP, obj);
    }

    public static int update(ConnectionSource connector, TypeTest obj) throws SQLException {
        return DaoTool.update(connector.getConnection(DS_NAME), MAP, obj);
    }

    public static int updateInsert(ConnectionSource connector, TypeTest obj)
            throws SQLException {
        return DaoTool.updateInsert(connector.getConnection(DS_NAME), MAP, obj);
    }

    public static int passiveInsert(ConnectionSource connector, TypeTest obj)
            throws SQLException {
        return DaoTool.passiveInsert(connector.getConnection(DS_NAME), MAP, obj);
    }

    public static int delete(ConnectionSource connector, TypeTest obj) throws SQLException {
        return DaoTool.delete(connector.getConnection(DS_NAME), MAP, obj);
    }

    public static TypeTest find(ConnectionSource connector, TypeTest obj) throws SQLException {
        return DaoTool.find(connector.getConnection(DS_NAME), MAP, obj);
    }

    public static List<TypeTest> listByQuery(ConnectionSource connector, SqlQuery query)
            throws SQLException {
        return DaoTool.listByQuery(connector.getConnection(DS_NAME), MAP,
                query);
    }

    public static int deleteByQuery(ConnectionSource connector, SqlQuery query)
            throws SQLException {
        return DaoTool.deleteByQuery(connector.getConnection(DS_NAME), MAP, query);
    }

    public static int countByQuery(ConnectionSource connector, SqlQuery query)
            throws SQLException {
        return DaoTool.countByQuery(connector.getConnection(DS_NAME), MAP, query);
    }

    public static int processByQuery(ConnectionSource connector, SqlQuery query,
                                     Processor<TypeTest> objProcessor) throws SQLException {
        return DaoTool.processByQuery(connector.getConnection(DS_NAME), MAP, query,
                objProcessor);
    }

}
