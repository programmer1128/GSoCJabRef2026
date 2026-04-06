# JabRef PostgreSQL Backend Prototype (GSoC 2026)

This repository contains a standalone, pure Java Proof of Concept (PoC) demonstrating a high-performance, constant-memory $O(1)$ database backend for [JabRef](https://github.com/JabRef/jabref). 

This prototype was built as part of the exploration for **GSoC 2026 Issue #12708: PostgreSQL as Full GUI Data Backend**.

## 🚀 The Bottleneck & The Solution
Currently, JabRef parses entire `.bib` files into JVM memory. For massive libraries, this linear memory growth causes UI stuttering and high heap consumption. 

This PoC completely bypasses the in-memory parsing bottleneck by offloading the data layer to PostgreSQL and utilizing **Keyset Pagination** combined with a **Hybrid JSONB Schema**.

### Architectural Highlights
* **Zero Web Frameworks:** Built in pure Java using Gradle to mirror JabRef's lightweight desktop environment.
* **Connection Pooling:** Utilizes `HikariCP` for robust, thread-safe database connection management.
* **Hybrid Schema:** Standard BibTeX fields (author, title, year) are mapped to relational columns, while all dynamic/custom fields are stored in a `JSONB` column.
* **GIN Indexing:** A Generalized Inverted Index (GIN) is applied to the JSONB column, allowing ultra-fast text searches across arbitrary dynamic fields.
* **Keyset Pagination:** Data is streamed to the application via `WHERE id > ? LIMIT 50`, guaranteeing constant heap usage regardless of total library size.

## 📊 Performance Results
To prove the architecture, the database was seeded with **50,000 realistic dummy entries**. The Java application simulates a user continuously scrolling through the entire library.

**Metrics:**
* **Fetch Speed:** Stabilized at **2–3 milliseconds** per 50-row page.
* **Memory Footprint:** Maintained a flat $O(1)$ "sawtooth" pattern in VisualVM.
    * **Peak Heap:** ~46 MB
    * **Idle/Cleared Heap:** ~15 MB

### VisualVM Profiler Proof
*(The flat blue line indicates constant memory usage while streaming 50,000 rows)*

![VisualVM Proof 1]<img width="1366" height="768" alt="GSoCJabrefPrototypeTest" src="https://github.com/user-attachments/assets/249163c7-acef-4549-9ba8-9e3e0e7856d1" />

<img width="1366" height="768" alt="GSoCJabrefPrototypeTest" src="https://github.com/user-attachments/assets/e36bbb50-578c-46bb-97ff-eefc36cf0338" />


> *Garbage Collector successfully clearing old pages, idling at ~15MB.*

## 🛠️ How to Reproduce

### 1. Prerequisites
* Java 17 or higher
* PostgreSQL 15 or higher
* VisualVM (for profiling)

### 2. Database Setup
Connect to your local PostgreSQL instance and create a database named `jabref`. Run the following SQL script to generate the schema, the GIN index, and 50,000 rows of dummy data:

```sql
DROP TABLE IF EXISTS bib_entries;

CREATE TABLE bib_entries (
    id SERIAL PRIMARY KEY,
    citation_key VARCHAR(255) UNIQUE NOT NULL,
    entry_type VARCHAR(50) NOT NULL,
    author TEXT NOT NULL,
    title TEXT NOT NULL,
    publication_year DATE,
    dynamic_fields JSONB DEFAULT '{}'::jsonb
);

CREATE INDEX idx_dynamic_fields ON bib_entries USING GIN (dynamic_fields);

INSERT INTO bib_entries (citation_key, entry_type, author, title, publication_year, dynamic_fields)
SELECT 
    'cite_' || md5(random()::text) || '_' || i,                                  
    CASE WHEN i % 3 = 0 THEN 'Book' ELSE 'Article' END,                          
    'Author Number ' || (i % 500),                                               
    'Optimization of System Architecture Vol. ' || i,                            
    DATE '1990-01-01' + (random() * 12775)::integer,                             
    (
        '{"journal": "Journal of Science ' || (i % 10) || '", ' ||
        '"pages": "' || (i % 100) || '-' || ((i % 100) + 15) || '", ' ||
        '"read_status": "' || CASE WHEN i % 2 = 0 THEN 'done' ELSE 'pending' END || '"}'
    )::jsonb                                                                     
FROM generate_series(1, 50000) AS i;
```

Configure the Application
Open src/main/java/org/example/DatabaseConnectionManager.java and ensure the credentials match your local PostgreSQL setup:

```java
config.setUsername("postgres");
config.setPassword("your_password_here");
```

Run the Simulation
Execute the Gradle wrapper from your terminal. The application will pause and wait for you to attach a profiler before it begins streaming data.

```bash
./gradlew clean run
```

Open VisualVM.

Attach to the org.example.Main process.

Open the Monitor tab to view the Heap graph.

Press ENTER in your terminal to start the scrolling simulation.

📂 Project Structure
Main.java: The simulation loop and execution entry point.

DatabaseConnectionManager.java: HikariCP configuration and initialization.

BibEntry.java: The primary Data Transfer Object (DTO).

BibEntryRepository.java: The Data Access Object (DAO) interface.

JsonbBibEntryRepository.java: The PostgreSQL implementation handling Keyset Pagination and JSONB parsing.

Developed by Aritra Banerjee for the JabRef Open Source Community.
