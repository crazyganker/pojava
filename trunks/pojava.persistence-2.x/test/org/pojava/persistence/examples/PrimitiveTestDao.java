package org.pojava.persistence.examples;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.pojava.lang.Processor;
import org.pojava.persistence.query.SqlQuery;
import org.pojava.persistence.sql.ConnectionSource;
import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.TableMap;
import org.pojava.persistence.util.DaoTool;

public class PrimitiveTestDao {

    private static final Class<PrimitiveTest> JAVA_CLASS = PrimitiveTest.class;
    private static final String TABLE_NAME = "type_test";
    private static final String DS_NAME = "pojava_test";

    private static final TableMap<PrimitiveTest> MAP = newTableMap();

    public static TableMap<PrimitiveTest> newTableMap() {
        TableMap<PrimitiveTest> tableMap = (TableMap<PrimitiveTest>) DatabaseCache.getTableMap(
                JAVA_CLASS, TABLE_NAME, DS_NAME);
        return tableMap;
    }

    public static int insert(ConnectionSource connector, PrimitiveTest obj) throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.insert(conn, MAP, obj);
        } finally {
            conn.close();
        }
    }

    public static int[] batchInsert(ConnectionSource connector, List<PrimitiveTest> list)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.batchInsert(connector.getConnection(DS_NAME), MAP, list);
        } finally {
            conn.close();
        }
    }

    public static int update(ConnectionSource connector, PrimitiveTest obj) throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.update(connector.getConnection(DS_NAME), MAP, obj);
        } finally {
            conn.close();
        }
    }

    public static int updateInsert(ConnectionSource connector, PrimitiveTest obj)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.updateInsert(connector.getConnection(DS_NAME), MAP, obj);
        } finally {
            conn.close();
        }
    }

    public static int passiveInsert(ConnectionSource connector, PrimitiveTest obj)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.passiveInsert(connector.getConnection(DS_NAME), MAP, obj);
        } finally {
            conn.close();
        }
    }

    public static int delete(ConnectionSource connector, PrimitiveTest obj) throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.delete(connector.getConnection(DS_NAME), MAP, obj);
        } finally {
            conn.close();
        }
    }

    public static PrimitiveTest find(ConnectionSource connector, PrimitiveTest obj)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return (PrimitiveTest) DaoTool.find(connector.getConnection(DS_NAME), MAP, obj);
        } finally {
            conn.close();
        }
    }

    public static List<PrimitiveTest> listByQuery(ConnectionSource connector, SqlQuery query)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return (List<PrimitiveTest>) DaoTool.listByQuery(connector.getConnection(DS_NAME),
                    MAP, query);
        } finally {
            conn.close();
        }
    }

    public static int deleteByQuery(ConnectionSource connector, SqlQuery query)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.deleteByQuery(connector.getConnection(DS_NAME), MAP, query);
        } finally {
            conn.close();
        }
    }

    public static int countByQuery(ConnectionSource connector, SqlQuery query)
            throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.countByQuery(connector.getConnection(DS_NAME), MAP, query);
        } finally {
            conn.close();
        }
    }

    public static int processByQuery(ConnectionSource connector, SqlQuery query,
            Processor objProcessor) throws SQLException {
        Connection conn = connector.getConnection(DS_NAME);
        try {
            return DaoTool.processByQuery(connector.getConnection(DS_NAME), MAP, query,
                    objProcessor);
        } finally {
            conn.close();
        }
    }

}
