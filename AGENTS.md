# Repository Guidelines

## Project Structure & Module Organization

OwlDB is a dependency-free Java database engine. Production code lives in `src/`, organized by responsibility: `schema/`, `row/`, and `table/` model records; `database/` and `query/` expose database operations; `storage/` and `page/` handle persistence; and `index/` and `btree/` implement lookups. `src/app/Main.java` is the main development harness. Manual test programs also live in `src/app/` and end in `Test.java`. Runtime fixtures are stored in `data/`, `schemas/`, `indexes/`, and `pages/`; compiled classes belong in the ignored `out/` directory.

## Build, Test, and Development Commands

Run commands from `src/` because storage paths are resolved relative to that directory.

```bash
cd src
javac -d ../out */*.java
java -cp ../out app.Main
```

The first command compiles every package into `out/`; the second runs the current integration/demo flow. Run standalone harnesses after compiling, for example:

```bash
java -cp ../out app.IndexTest
java -cp ../out app.LoadTest
java -cp ../out app.LoadAllTest
```

There is currently no Maven/Gradle build, formatter, linter, or automated test framework.

## Coding Style & Naming Conventions

Use four spaces for new indentation and never tabs. Keep package names lowercase, classes in PascalCase (`PageManager`), methods and variables in camelCase (`insertAndReturnRID`), and constants in `UPPER_SNAKE_CASE`. Keep each public class in its matching `.java` file. Follow the surrounding code’s brace layout, use explicit package declarations, and avoid unrelated formatting changes while the older files are normalized gradually.

## Testing Guidelines

Add focused manual harnesses under `src/app/` with names such as `PageSerializerTest.java`. A harness may provide `main(String[] args)` or a static `run()` called by `Main`. Exercise success cases, boundary behavior, and persistence round trips. Before submitting, compile all packages and run the harnesses affected by the change. Treat changes under `data/`, `schemas/`, `indexes/`, or `pages/` as fixtures and review them deliberately.

## Commit & Pull Request Guidelines

Recent commits use `OWLET-###: Imperative summary`, for example `OWLET-055: Add page serialization foundation`. Continue the next available number and keep each commit focused. Pull requests should explain the behavior changed, list commands run, link the relevant issue or OWLET task, and include sample console output when CLI-visible behavior changes. Do not commit `.class` files, `out/`, local configuration, or generated runtime data.
