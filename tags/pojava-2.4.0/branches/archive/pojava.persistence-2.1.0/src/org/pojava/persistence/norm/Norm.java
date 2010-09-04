package org.pojava.persistence.norm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pojava.persistence.serial.XmlDefs;
import org.pojava.persistence.serial.XmlSerializer;

public class Norm {



    public static NormTable scanTable(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();
        XmlDefs defs=new XmlDefs();
        defs.setIgnoringIllegalAccessException(true);
        defs.setIgnoringInvocationTargetException(true);
        XmlSerializer serializer=new XmlSerializer(defs);
        
        System.out.println(serializer.toXml(dmd));
        ResultSet rs=dmd.getTables(null, "public", null, null);
        ResultSetMetaData rsmd=rs.getMetaData();
        List<String> fieldNames=new ArrayList<String>();
        StringBuffer sb=new StringBuffer();
        for (int i=1; i<=rsmd.getColumnCount(); i++) {
            fieldNames.add(rsmd.getColumnName(i));
            sb.append(rsmd.getColumnName(i));
            sb.append("\t");
        }
        sb.setLength(sb.length()-1);
        System.out.println(sb.toString());
        while (rs.next()) {
            sb.setLength(0);
            for(Iterator<String> it=fieldNames.iterator(); it.hasNext();) {
                String fieldName=it.next();
                sb.append(rs.getObject(fieldName));
                sb.append("\t");
            }
            sb.setLength(sb.length()-1);
            System.out.println(sb.toString());
        }
        /*
        ResultSet rs = dmd.getTables(null,null, "%",null);
        // ResultSet rs = dmd.getIndexInfo(null, null, table, false, true);
        while (rs.next()) {
            if ("information_schema".equals(rs.getString("TABLE_SCHEM"))
                    || "public".equals(rs.getString("TABLE_SCHEM"))) {
        */
        return null;
    }
}
