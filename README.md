# 🦉 OwlDB

> Building a relational database from scratch, one layer at a time.

A ground-up implementation of a database engine in Java, starting from metadata and progressing towards full SQL support, indexing, transactions, and AI-powered query interfaces.

---

## 🎯 The Vision

Imagine this workflow in 1 year:

```sql
create table users (
  id int,
  name string,
  age int
)

insert into users values (1, 'Niranjan', 22)

select * from users
-- Output: 1 Niranjan 22

show users older than 20
-- AI converts to: SELECT * FROM users WHERE age > 20
-- Output: 1 Niranjan 22
```

**That's OwlDB.**

Current capabilities:
- ✅ Represents tables with schemas, columns, and rows
- ✅ Stores schemas and rows in files
- ✅ Loads schemas, rows, and tables from disk
- ✅ Uses in-memory hash indexes for fast lookups on indexed columns
- ✅ Saves and loads index metadata to disk
- ✅ Provides a basic storage-backed database API for create, insert, select, update, delete, and load

Planned capabilities:
- ⏳ SQL query parsing and execution
- ⏳ Indexes and query optimization
- ⏳ Transactions and crash recovery
- ⏳ Natural language queries via AI

---

## 🏗️ Architecture

```
┌─────────────────────┐
│   AI Layer          │  Phase 11: Natural language → SQL
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Query Engine       │  Phase 4: Parse & execute SQL (NEXT)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Database API       │  Phase 3: storage-backed CRUD operations (IN PROGRESS)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Table Engine       │  Phase 1: Tables, schemas, rows ✓
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Storage Engine     │  Phase 2: Save/load files ✓
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Disk / Files       │
└─────────────────────┘
```

---

## 📍 Current Status: Phase 3 - Database API (In Progress)

OwlDB now has the core metadata layer, file-based storage, and a small storage-backed database API for creating tables, inserting rows, fetching tables, selecting rows, updating rows, and deleting rows.

### Completed So Far

### `Column`
Represents a field definition:
```java
new Column("id", "INT")
new Column("name", "STRING")
new Column("age", "INT")
```

### `Schema`
Represents a table's blueprint (structure):
```
users
├── id INT
├── name STRING
└── age INT
```

### `Row`
Represents a single record/tuple:
```
[1, "Niranjan", 22]
```

Rows keep a mutable copy of their values so update operations can change matching records safely.

### `Table`
Represents complete table (Schema + Rows):
```
Table: users
Schema:
  id INT
  name STRING
  age INT
Rows:
  [1, "Niranjan", 22]
  [2, "Rahul", 17]
```

Tables can create indexes per column:
```java
usersTable.createIndex("id");
usersTable.createIndex("name");
```

### `StorageEngine`
Persists schemas and rows to disk:
```
schemas/users.schema
data/users.data
indexes/users.index
```

Current storage operations:
- `saveSchema(schema)`
- `saveRows(table)`
- `saveIndexes(table)`
- `loadSchema(tableName)`
- `loadRows(tableName)`
- `loadIndexes(table)`
- `loadTable(tableName)`

### `Index`
Basic in-memory hash index:
```
columnValue -> List<Row>
```

Current index operations:
- `add(key, row)`
- `find(key)`

### `Database`
Provides a simple database-level API:
```java
db.createTable(usersTable);
db.insert("users", new Row(List.of("1", "Niranjan", "22")));
db.selectAll("users");
db.selectWhere("users", "age", "22");
db.updateWhere("users", "name", "Niranjan", "age", "23");
db.deleteWhere("users", "age", "17");
```

Current database operations:
- `createTable(table)`
- `getTable(tableName)`
- `insert(tableName, row)`
- `selectAll(tableName)`
- `selectWhere(tableName, columnName, value)`
- `updateWhere(tableName, whereColumn, whereValue, updateColumn, newValue)`
- `deleteWhere(tableName, columnName, value)`
- `loadTable(tableName)`

Database writes are now connected to storage:
- `createTable()` saves the table schema and current rows
- `insert()` appends the row in memory and rewrites the table data file
- `updateWhere()` changes matching rows and persists the updated file
- `deleteWhere()` removes matching rows and persists the updated file
- `loadTable()` restores a persisted table back into the in-memory database
- `selectWhere()` uses an index when the column is indexed

Example persisted row data:
```txt
1,Niranjan,23
3,Priya,20
```

---

## 🚀 Quick Start

### Build & Run

```bash
cd ~/Desktop/OwlDB/src
javac schema/*.java row/*.java table/*.java storage/*.java database/*.java index/*.java app/*.java
java app.Main
```

### Compile & Run at Once

```bash
cd ~/Desktop/OwlDB/src
javac schema/*.java row/*.java table/*.java storage/*.java database/*.java index/*.java app/*.java && java app.Main
```

### Basic Example

```java
// 1. Define columns
List<Column> columns = new ArrayList<>();
columns.add(new Column("id", "INT"));
columns.add(new Column("name", "STRING"));
columns.add(new Column("age", "INT"));

// 2. Create schema
Schema usersSchema = new Schema("users", columns);

// 3. Create table and database
Table usersTable = new Table(usersSchema);
Database db = new Database();

// 4. Register table
db.createTable(usersTable);

// 4.1 Create indexes
usersTable.createIndex("id");
usersTable.createIndex("name");

// 5. Insert rows
db.insert("users", new Row(List.of("1", "Niranjan", "22")));
db.insert("users", new Row(List.of("2", "Rahul", "17")));

// 6. Select rows
List<Row> rows = db.selectAll("users");

// 7. Select rows by condition
List<Row> adults = db.selectWhere("users", "age", "22");

// 8. Update matching rows
db.updateWhere("users", "name", "Niranjan", "age", "23");

// 9. Delete matching rows
db.deleteWhere("users", "age", "17");

// Changes are persisted to data/users.data
```

---

## 🛣️ The Roadmap

| Phase | Name | Components | Status |
|-------|------|------------|--------|
| 1 | **Metadata Layer** | Column, Schema, Row, Table | ✅ Complete |
| 2 | **Storage Engine** | Save/load schemas and rows from files | ✅ Complete |
| 3 | **Database API** | Storage-backed `createTable()`, `insert()`, `selectWhere()`, `updateWhere()`, `deleteWhere()`, `loadTable()` | 🔄 In Progress |
| 4 | **Query Language** | Tokenizer, Parser, Executor for SQL | ⏭️ Next |
| 5 | **Indexing** | Hash indexes per column → B+ trees | 🔄 In Progress |
| 6 | **Pages** | Buffer management, page-based storage | ⏳ Planned |
| 7 | **Transactions** | BEGIN, COMMIT, ROLLBACK (ACID) | ⏳ Planned |
| 8 | **WAL** | Write Ahead Log for crash recovery | ⏳ Planned |
| 9 | **Joins** | Multi-table queries | ⏳ Planned |
| 10 | **Query Planner** | Optimization, query execution planning | ⏳ Planned |
| 11 | **AI Layer** | Natural language to SQL conversion | ⏳ Planned |

---

## 📁 Project Structure

```
OwlDB/
├── src/
│   ├── app/              Main entry point
│   ├── schema/           Column, Schema classes
│   ├── row/              Row class
│   ├── table/            Table class
│   ├── storage/          Storage engine (Phase 2)
│   ├── database/         Database API (Phase 3)
│   └── index/            Basic hash index (Phase 5)
├── data/                 Persisted table data
├── indexes/              Persisted index metadata
├── schemas/              Persisted schema definitions
└── README.md
```

Planned folders:
- `query/` for SQL parsing and execution
- `transaction/` for transactions
- `ai/` for natural language query support

---

## 🔧 What's Next? (Phase 3 → Phase 4)

### Finish Database API
The database layer now exists, but it can grow into a cleaner user-facing API before SQL parsing begins.

**Current goal:** Make table operations feel like a real database interface.

**Implementation:**
- Keep improving `Database`
- Add safer error handling for missing tables
- Add more query-style operations after `selectWhere`, `updateWhere`, and `deleteWhere`
- Add auto-loading so persisted tables can be restored into `Database` at startup

**Next major phase:** Build the Query Language layer with a tokenizer, parser, and executor.

---

## 💡 Design Principles

1. **Incremental**: Build one layer at a time, in order
2. **Observable**: Each phase produces visible artifacts (files, outputs)
3. **Realistic**: Follow real database design (PostgreSQL, MySQL patterns)
4. **Teachable**: Code is a learning tool for database internals
5. **Extensible**: Each layer builds cleanly on the previous one

---

## 📖 Learning Path

This project teaches:
- Object-oriented design (layered architecture)
- File I/O and serialization
- Query parsing and execution
- B+ tree indexing
- Transaction management
- Query optimization
- Natural language processing basics

---

## 🤝 Contributing

This is a personal learning project. Future contributions welcome!

---

## 📝 License

MIT

---

**Built with 🦉 on a mission to understand databases from the ground up.**
