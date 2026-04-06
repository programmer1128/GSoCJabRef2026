package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class JsonbBibEntryRepository implements BibEntryRepository
{
     private final DataSource dataSource=DatabaseConnectionManager.getDataSource(); 

     @Override
     public List<BibEntry> fetchPage(int lastSeenId, int limit)
     {
         String sql = "SELECT id, citation_key, entry_type, author, title, publication_year, dynamic_fields FROM bib_entries WHERE id > ? ORDER BY id ASC LIMIT ?";

         List<BibEntry> page = new ArrayList<>();

         try(Connection connection= dataSource.getConnection())
         {
             PreparedStatement statement= connection.prepareCall(sql);

             statement.setInt(1,lastSeenId);
             statement.setInt(2,limit);

             ResultSet rs = statement.executeQuery();

             while(rs.next())
             {
                 BibEntry entry = new BibEntry();

                 entry.setId(rs.getInt("Id"));
                 entry.setCitationKey(rs.getString("citation_key"));
                 entry.setEntryType(rs.getString("entry_type"));
                 entry.setAuthor(rs.getString("author"));
                 entry.setTitle(rs.getString("title"));

                 rs.getDate("publication_year") ;

                 entry.setDynamicFields(rs.getString("dynamic_fields"));

                 page.add(entry);
             }
         }
         catch(SQLException e)
         {
             System.out.println(e.getMessage());
         }

         return page;
     }
}
