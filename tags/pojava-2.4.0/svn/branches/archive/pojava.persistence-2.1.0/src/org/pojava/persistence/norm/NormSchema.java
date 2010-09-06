package org.pojava.persistence.norm;

import java.util.ArrayList;
import java.util.List;

public class NormSchema {

    List<NormTable> tables = new ArrayList<NormTable>();
    List<NormSequence> sequences = new ArrayList<NormSequence>();

    public void addTable(NormTable table) {
        if (table != null) {
            tables.add(table);
        }
    }
    
    public void addSequence(NormSequence sequence) {
        if (sequence != null) {
            sequences.add(sequence);
        }
    }

    public List<NormTable> getTables() {
        return tables;
    }

    public void setTables(List<NormTable> tables) {
        this.tables = tables;
    }
    
    
}
