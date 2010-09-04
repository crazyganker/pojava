package org.pojava.persistence.norm;

import java.util.ArrayList;
import java.util.List;

/**
 * NormIndex is a collection of field names representing a sort index.
 * Each field name is assumed to be ascending unless " DESC" is specified.
 * 
 * @author John
 *
 */
public class NormIndex {
    
    /**
     * Each fieldName followed by optional " ASC" or " DESC".
     */
    List<String> fieldNames=new ArrayList<String>();
    /**
     * True if index also represents a unique constraint.
     */
    boolean unique;

    public void addField(String fieldName) {
        fieldNames.add(fieldName);
    }
    
    public void addField(String fieldName, SortOrder sortOrder) {
        if (SortOrder.Ascending.equals(sortOrder)) {
            fieldNames.add(fieldName);
        } else {
            fieldNames.add(fieldName + " DESC");
        }
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

}
