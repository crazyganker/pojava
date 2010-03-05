package org.pojava.persistence.query;

import org.pojava.lang.BoundString;

/**
 * A SqlQuery object is used to build query criteria with a contract to produce a PreparedSql
 * object matching those criteria.
 * 
 * @author John Pile
 */
public class SqlQuery extends AbstractQuery implements PreparedSqlProvider {

    /**
     * SQL fragment (typically everything after the SELECT clause and before the ORDER BY
     * clause) Note that this class will likely grow to support more than just SQL.
     */
    protected final BoundString sql = new BoundString();

    /**
     * Order By clause of the SQL statement.
     */
    protected String sqlOrderBy = null;

    /**
     * Default constructor.
     */
    public SqlQuery() {

    }

    /**
     * Construct a new Sql query from a BoundString.
     * 
     * @param boundSql
     */
    public SqlQuery(BoundString boundSql) {
        if (boundSql == null) {
            return;
        }
        appendSql(boundSql);
    }

    /**
     * Construct a new SQL query from a BoundString specifing a row limit.
     * 
     * @param boundSql
     * @param maxRows
     */
    public SqlQuery(BoundString boundSql, int maxRows) {
        if (boundSql == null) {
            return;
        }
        appendSql(boundSql);
        super.setMaxRows(maxRows);
    }

    /**
     * Append some SQL
     * 
     * @param newSql
     */
    protected void appendSql(BoundString newSql) {
        sql.append(newSql);
    }

    /**
     * Append some SQL
     * 
     * @param newSql
     */
    protected void appendSql(String newSql) {
        sql.append(newSql);
    }

    @SuppressWarnings("unchecked")
    protected void addBinding(Class type, Object value) {
        sql.addBinding(type, value);
    }

    /**
     * Insert some SQL
     * 
     * @param newSql
     */
    protected void insertSql(BoundString newSql) {
        sql.insert(newSql);
    }

    /**
     * Insert some SQL
     * 
     * @param newSql
     */
    protected void insertSql(String newSql) {
        sql.insert(newSql);
    }

    /**
     * Append some SQL with a "WHERE" or "AND" prefix.
     * 
     * @param newSql
     */
    protected void whereAnd(String newSql) {
        sql.append(sql.getString().length() == 0 ? "WHERE " : " AND ");
        sql.append(newSql);
    }

    /**
     * Append some SQL with a "WHERE" or "AND" prefix.
     * 
     * @param predicate
     *            A freeform expression evaluating to true or false
     */
    protected void whereAnd(BoundString predicate) {
        sql.append(sql.getString().length() == 0 ? "WHERE " : " AND ");
        sql.append(predicate);
    }

    /**
     * Generate a PreparedSql statement from your prefix (such as "SELECT * from table") and
     * this query.
     * 
     * @param prefix
     * @return SQL statement ready for execution
     */
    public PreparedSql generatePreparedSql(String prefix) {
        return generatePreparedSql(new BoundString(prefix));
    }

    /**
     * Generate Prepared SQL ready for execution.
     */
    public PreparedSql generatePreparedSql(BoundString prefix) {
        BoundString bs = new BoundString();
        bs.append(prefix);
        bs.append(" ");
        bs.append(sql);
        if (sqlOrderBy != null && sqlOrderBy.trim().length() > 0) {
            String verify = sqlOrderBy.toUpperCase().replace('\t', ' ').trim();
            if (verify.startsWith("ORDER BY ")) {
                bs.append(" ");
            } else {
                bs.append(" ORDER BY ");
            }
            bs.append(sqlOrderBy.trim());
        }
        return new PreparedSql(bs, super.getMaxRows());
    }

}
