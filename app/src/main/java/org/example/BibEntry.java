package org.example;

import java.time.LocalDate;

public class BibEntry 
{
     private int id;
     private String citationKey;
     private String entryType;
     private String author;
     private String title;
     private LocalDate publicationYear;
     private String dynamicFields;

     public void setId(int id)
     {
         this.id=id;
     }

     public int getId()
     {
         return this.id;
     }

     public void setCitationKey(String citationKey)
     {
         this.citationKey=citationKey;
     }

     public String getCitationKey()
     {
         return this.citationKey;
     }

     public void setEntryType(String entryType)
     {
         this.entryType=entryType;
     }

     public String getEntryType()
     {
         return this.entryType;
     }

     public void setAuthor(String author)
     {
         this.author=author;
     }

     public String getAuthor()
     {
         return this.author;
     }

     public void setTitle(String title)
     {
         this.title=title;
     }

     public String getTitle()
     {
         return this.title;
     }

     public void setPublicationYear(LocalDate time)
     {
         this.publicationYear=time;
     }

     public LocalDate getPublicationYear()
     {
         return this.publicationYear;
     }

     public void setDynamicFields(String dynamicFields)
     {
         this.dynamicFields=dynamicFields;
     }

     public String getDynamicFields()
     {
         return this.dynamicFields;
     }
}
