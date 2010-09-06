package org.pojava.persistence.examples;

import org.pojava.lang.BoundString;
import org.pojava.persistence.query.SqlQuery;

public class TypeTestQuery extends SqlQuery {

    public TypeTestQuery forAll() {
        super.sql.clear();
        return this;
    }

    public TypeTestQuery forIdGreaterThan(int val) {
        BoundString predicate = new BoundString("test_id > ?");
        predicate.addBinding(Integer.class, new Integer(val));
        super.whereAnd(predicate);
        return this;
    }
}
