package org.example;

import java.util.List;
import java.util.Scanner;

public class Main 
{
     public static void main(String args[]) throws Exception
     {
         System.out.println("Starting JabRef PoC Data Access Test...");
         BibEntryRepository repository = new JsonbBibEntryRepository();

         System.out.println("Press ENTER to start the scrolling simulation...");
         new Scanner(System.in).nextLine();

         int lastSeenId = 0;
         int pageNumber = 1;

         // Simulate a user aggressively scrolling through 10,000 items
         while (lastSeenId < 10000) 
         {
             long startTime = System.currentTimeMillis();
            
             List<BibEntry> page = repository.fetchPage(lastSeenId, 50);
            
             long endTime = System.currentTimeMillis();
             System.out.println("Page " + pageNumber + " fetched " + page.size() + " rows in " + (endTime - startTime) + "ms.");

             if (page.isEmpty()) break;

             // Update the cursor for the next query
             lastSeenId = page.get(page.size() - 1).getId();
             pageNumber++;

             // Pause for 100ms to simulate human scrolling speed and let you watch VisualVM
             Thread.sleep(100); 
         }
        
         System.out.println("Simulation complete.");
     }    
}
