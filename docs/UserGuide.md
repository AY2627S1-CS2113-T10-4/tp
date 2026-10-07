# UniEnable User Guide

## Introduction

UniEnable is a command-line project for managing dated activities with start/end
times and demand levels, and looking up hub accessibility information.
This Week 8 baseline starts, accepts input, and exits with `bye`. Other v1.0
commands are not implemented yet.

## Quick Start

1. Install Java 25.
2. Obtain the built `unienable.jar` (developers can run `./gradlew shadowJar`).
3. In its directory, run `java -jar unienable.jar`.
4. Enter `bye` to exit. End of input also ends the application cleanly.

## Planned v1.0 Command Summary

The authoritative team contract is [V1_SPEC.md](V1_SPEC.md).

```text
help
add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]
list
list demand
delete INDEX
mark INDEX
unmark INDEX
facility HUB
bye
```

Command words, prefixes, and supported hub codes are case-insensitive. Names
preserve capitalization. Parameters may appear in any order. Dates use
`YYYY-MM-DD`, times use 24-hour `HH:mm`, and indexes are 1-based.
Demand is `LOW`, `MEDIUM`, or `HIGH` and defaults to `MEDIUM` when omitted.
Detailed feature instructions will be added alongside the feature implementations.
