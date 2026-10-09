# UniEnable v1.0 shared contract

This internal specification is authoritative for the four feature branches.
The Week 8 baseline supplies shared APIs and a runnable bootstrap only. Except
for `bye`, the commands below are planned v1.0 behavior, not implemented features.

## Commands

```text
help
add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]
list
list demand
delete INDEX
mark INDEX
unmark INDEX
facility LOCATION
bye
```

- Command words, prefixes, and supported hub codes are case-insensitive.
- Activity names preserve entered capitalization.
- Parameters may be supplied in any order.
- Dates use `YYYY-MM-DD`; times use 24-hour `HH:mm`.
- Demand is `LOW`, `MEDIUM`, or `HIGH`; omitted `dem/` defaults to `MEDIUM`.
- User-visible indexes are 1-based.
- Start and end times use `s/START_TIME` and `e/END_TIME`.
- `list demand` is part of v1.0.

## Activity data

An Activity conceptually contains:

```java
String name;
LocalDate date;
LocalTime startTime;
LocalTime endTime;
DemandLevel demand;
boolean isDone;
```

`DemandLevel` has exactly `LOW`, `MEDIUM`, and `HIGH`.
The baseline constructor accepts all six values explicitly; feature owners
implement input defaults, validation, and completion changes in their PRs.

## Persistence and reference data

The agreed conceptual persistence representation is:

```text
NAME|YYYY-MM-DD|START|END|DEMAND|DONE
```

`START` and `END` use `HH:mm`. Runtime activity data will use
`data/activities.txt`. No runtime data file is created by this baseline.
Accessibility reference data will be bundled as
`src/main/resources/facilities.txt` once the actual dataset is supplied.
No facility dataset is invented here.

## Shared API boundary

- `Command.execute(ActivityList, Storage)` returns `CommandResult` and may throw
  `UniEnableException`.
- `CommandResult` carries `message` and `isExit`.
- `Parser.parse(String)` returns a `Command` or throws `ParseException`.
  Only case-insensitive `bye` is currently recognized; all other input receives
  a clear unimplemented-command message.
- `Activity` has the six agreed fields, a full constructor, and getters.
- `ActivityList` owns a copied collection and returns an immutable snapshot
  through `getActivities()`. Feature PRs add mutation and ordering APIs as needed.
- `Storage.load()` returns `ActivityList`; `save(ActivityList)` returns void.
  Both declare `UniEnableException` and currently throw a clear unimplemented
  error. The bootstrap does not call them.
- `Ui` reads console lines and prints messages. End of input exits cleanly.
- `ParseException` and `InvalidIndexException` extend `UniEnableException`.

Feature owners extend these contracts incrementally. No feature commands,
feature parsers, facility model, sorting, completion mutation, or persistence
implementation is included in this baseline.

## Decisions still requiring team agreement

The supplied contract contains no conflicting command syntax. It does not yet
specify the supported hub codes/dataset, chronological and demand sort direction
or tie-breaking, which displayed order indexes refer to, start/end validation
(including overnight activities), duplicate or missing prefix handling, demand
value case handling, the `DONE` encoding, delimiter/newline escaping in names,
or malformed-file handling. Agree on these before the affected feature PRs;
do not infer final behavior from this skeleton.

## Out of scope

Later-version features such as find, edit, view today/tomorrow/week, route,
priority scoring, recommendation, Pomodoro, and notifications are excluded.
