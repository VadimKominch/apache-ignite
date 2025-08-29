+---------------------+
|  Government Service |
|  (slow source)      |
+---------------------+
|
[ETL / ingest job]
v
+-------------------------+
|     Ignite Cluster      |
|  (Server nodes, RAM +  |
|   Native Persistence)  |
+-------------------------+
^           ^           ^
|           |           |
+---------------+ +---------------+ +---------------+
| Camunda App A | | Camunda App B | | Other App |
| (Thin Client) | | (Thin Client) | | (Thin Client) |
+---------------+ +---------------+ +---------------+
| |
Optional near cache |
(local fast reads) |
v
+------------------+
| Production DB / |
| Postgres backup |
+------------------+