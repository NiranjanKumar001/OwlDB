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
- ✅ Provides a basic database API for create, insert, select, update, and delete

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
│  Database API       │  Phase 3: create, insert, select, update, delete (IN PROGRESS)
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

OwlDB now has the core metadata layer, file-based storage, and a small database API for creating tables, inserting rows, fetching tables, selecting rows, updating rows, and deleting rows.

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

### `StorageEngine`
Persists schemas and rows to disk:
```
schemas/users.schema
data/users.data
```

Current storage operations:
- `saveSchema(schema)`
- `saveRows(table)`
- `loadSchema(tableName)`
- `loadRows(tableName)`
- `loadTable(tableName)`

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

---

## 🚀 Quick Start

### Build & Run

```bash
cd ~/Desktop/OwlDB/src
javac schema/*.java row/*.java table/*.java storage/*.java database/*.java app/*.java
java app.Main
```

### Compile & Run at Once

```bash
cd ~/Desktop/OwlDB/src
javac schema/*.java row/*.java table/*.java storage/*.java database/*.java app/*.java && java app.Main
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
```

---

## 🛣️ The Roadmap

| Phase | Name | Components | Status |
|-------|------|------------|--------|
| 1 | **Metadata Layer** | Column, Schema, Row, Table | ✅ Complete |
| 2 | **Storage Engine** | Save/load schemas and rows from files | ✅ Complete |
| 3 | **Database API** | `createTable()`, `insert()`, `selectWhere()`, `updateWhere()`, `deleteWhere()` | 🔄 In Progress |
| 4 | **Query Language** | Tokenizer, Parser, Executor for SQL | ⏭️ Next |
| 5 | **Indexing** | Hash indexes, B+ trees | ⏳ Planned |
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
│   └── database/         Database API (Phase 3)
├── data/                 Persisted table data
├── schemas/              Persisted schema definitions
└── README.md
```

Planned folders:
- `query/` for SQL parsing and execution
- `index/` for indexes
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
- Connect database operations more tightly with `StorageEngine`

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
