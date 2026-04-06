package org.example;

import java.util.List;

public interface BibEntryRepository 
{
     List<BibEntry> fetchPage(int lastSeenId, int limit);    
}
