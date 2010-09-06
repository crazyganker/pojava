package org.pojava.persistence.norm;

import java.util.ArrayList;
import java.util.List;

public class NormTable {

    private List<NormField> fields=new ArrayList<NormField>();
    private List<NormIndex> indexes=new ArrayList<NormIndex>();
    private String schema=null;
    
    public void addField(NormField field) {
        if (field!=null) {
            if (fields==null) {
                fields=new ArrayList<NormField>();
            }
            fields.add(field);
        }
    }

    public List<NormField> getFields() {
        return fields;
    }

    public void setFields(List<NormField> fields) {
        this.fields = fields;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    public void addIndex(NormIndex index) {
        if (this.indexes==null) {
            this.indexes=new ArrayList<NormIndex>();
        }
        this.indexes.add(index);
    }

    public List<NormIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<NormIndex> indexes) {
        this.indexes = indexes;
    }

    
}
