package org.pojava.persistence.norm;

import java.util.ArrayList;
import java.util.List;

/*
 * This is intended to hold a list of schema specific to a database.
 */
public class NormDatabase {

    String name;
    List<NormSchema> schemas = new ArrayList<NormSchema>();

    public void addSchema(NormSchema schema) {
        if (schema != null) {
            schemas.add(schema);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NormSchema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<NormSchema> schemas) {
        this.schemas = schemas;
    }

}
