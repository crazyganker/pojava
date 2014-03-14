package org.pojava.persistence.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHelper {

    private static Logger logger = Logger.getLogger("org.pojava.persistence.sql.TestHelper");

    /**
     * @return Instructions for setting up a DataSource.
     */
    public static String setupInstructions() {
        String path = new File(".").getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append("INSTRUCTIONS:  Your system must be configured for these");
        sb.append(" unit tests.\nCreate a \"config\" folder under ");
        sb.append(path);
        sb.append("\nand create in it a property file named");
        sb.append(" datasource.properties.\n");
        sb.append("That file must specify where to find a database");
        sb.append(" table for testing.\nIt will provide a");
        sb.append(" driver, url, user, password, and datasource name.\n");
        sb.append("These must match a table and driver you create on");
        sb.append(" your own database.\n");
        sb.append("Here's an example.  Please customize the file to");
        sb.append(" fit your environment\n");
        sb.append("(not the other way around).\n\n");
        sb.append("driver = org.postgresql.Driver\n");
        sb.append("url = jdbc:postgresql://localhost:5432/postgres\n");
        sb.append("user = pojava\n");
        sb.append("password = popojava\n");
        sb.append("name = pojava_test\n");
        return sb.toString();
    }

    public static Properties fetchDataSourceProperties() {
        // default properties
        Properties defaultProps = new Properties();
        Properties dataSourceProps = null;
        // override properties
        try {
            dataSourceProps = new Properties(defaultProps);
            FileInputStream in = new FileInputStream("config/datasource.properties");
            dataSourceProps.load(in);
            in.close();
        } catch (FileNotFoundException ex) {
            System.out.println(TestHelper.setupInstructions());
            System.exit(0);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IOException occurred trying to read config/datastore.properties.", ex);
        }
        return dataSourceProps;
    }

}
