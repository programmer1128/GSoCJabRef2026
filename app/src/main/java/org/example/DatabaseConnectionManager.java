package org.example;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConnectionManager 
{
     private static HikariDataSource dataSource;

     // We use a static block to initialize the pool only once
     static 
     {
         HikariConfig config = new HikariConfig();
         config.setJdbcUrl("jdbc:postgresql://localhost:5432/jabref");
         config.setUsername("postgres"); // Change to your username if you used 'aritra'
         config.setPassword("Chiko2005#");

         // The exact tuning parameters for a desktop app
         config.setMaximumPoolSize(10);
         config.setMinimumIdle(2);
         config.setIdleTimeout(30000);

         dataSource = new HikariDataSource(config);
     }

     // This is how our Repository will get a connection
     public static DataSource getDataSource() 
     {
         return dataSource;
     }     
}
