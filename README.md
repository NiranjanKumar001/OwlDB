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

We're building a database that:
- ✅ Stores data persistently to disk
- ✅ Understands SQL queries
- ✅ Optimizes execution with indexes
- ✅ Supports transactions and crash recovery
- ✅ Understands natural language queries via AI

---

## 🏗️ Architecture

```
┌─────────────────────┐
│   AI Layer          │  Phase 11: Natural language → SQL
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Query Engine       │  Phase 4: Parse & execute SQL
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Table Engine       │  Phase 1: Tables, schemas, rows ✓
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Storage Engine     │  Phase 2: Persist to disk (NEXT)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Disk / Files       │
└─────────────────────┘
```

---

## 📍 Current Status: Phase 1 - Metadata Layer

We've built the foundation with four core classes:

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

// 3. Create table
Table usersTable = new Table(usersSchema);

// 4. Add rows
usersTable.addRow(new Row(List.of("1", "Niranjan", "22")));
usersTable.addRow(new Row(List.of("2", "Rahul", "17")));

// 5. Query
System.out.println("Table: " + usersTable.getSchema().getTableName());
```

---

## 🛣️ The Roadmap

| Phase | Name | Components | Status |
|-------|------|------------|--------|
| 1 | **Metadata Layer** | Column, Schema, Row, Table | ✅ Complete |
| 2 | **Storage Engine** | File serialization, persistence | 🔄 Next |
| 3 | **Database API** | `db.createTable()`, `db.insert()`, `db.select()` | ⏳ Planned |
| 4 | **Query Language** | Tokenizer, Parser, Executor for SQL | ⏳ Planned |
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
│   ├── query/            Query engine (Phase 4)
│   ├── index/            Indexing (Phase 5)
│   ├── transaction/      Transactions (Phase 7)
│   └── ai/               AI layer (Phase 11)
├── data/                 Persisted table data
├── schemas/              Persisted schema definitions
└── README.md
```

---

## 🔧 What's Next? (Phase 2)

### Storage Engine
The moment OwlDB becomes a *real* database.

**Goal:** Data persists to disk when the program exits.

**Implementation:**
- Serialize `Schema` objects → `schemas/users.schema`
- Serialize `Row` data → `data/users.data`
- Implement deserialization on startup

**Outcome:** When you restart the program, your data is still there. 🎉

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
