# UniEnable User Guide

## Introduction

UniEnable is a command-line project for managing dated activities with start/end
times and demand levels, and looking up hub accessibility information.
The current Facility HUB supports detailed listings, individual facility lookups,
and accessibility-feature searches. Activity commands are stored automatically in
`data/activities.txt` and recover from malformed rows with warnings.

## Quick Start

1. Install Java 25.
2. Obtain the built `unienable.jar` (developers can run `./gradlew shadowJar`).
3. In its directory, run `java -jar unienable.jar`.
4. Enter `facility list` to see the local accessibility reference.
5. Enter `bye` to exit. End of input also ends the application cleanly.

## Available Command Summary

| Command | Purpose |
| --- | --- |
| `facility list` | Display all nine facilities and all their recorded accessibility information |
| `facility LOCATION` | Display the details of one facility by hub code or stable ID |
| `facility find type/FEATURE [status/YES\|NO\|UNKNOWN]` | Find facilities with a recorded accessibility feature and status |
| `add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW\|MEDIUM\|HIGH]` | Add an activity |
| `list` | List activities chronologically |
| `list demand` | List activities by demand |
| `delete INDEX` | Delete an activity by its chronological index |
| `mark INDEX` | Mark an activity as completed |
| `unmark INDEX` | Mark an activity as incomplete |
| `bye` | Exit the application |

Command words, feature types, status values, hub codes, and facility IDs are
case-insensitive. Surrounding spaces and repeated whitespace are accepted.
`LOCATION` and `FEATURE` are placeholders to replace with actual values.

## Facility HUB

The supported hubs are AS1–AS8 and CLB. Their stable IDs are F01–F09 respectively.
Reference data is bundled with the application and is read-only.
Commands do not add, edit, or remove facility records.

### List all facilities

Format:

```text
facility list
```

The output preserves dataset order and shows each facility's ID, name,
description, recorded feature types, YES/NO/UNKNOWN statuses, and notes.
Important restrictions, such as inaccessible floors and staff-card requirements,
remain visible. The shared accessibility disclaimer appears once at the end.

This command accepts no extra arguments.

### Look up one facility

Format:

```text
facility LOCATION
```

Examples:

```text
facility AS4
facility clb
facility F04
facility f09
```

Example output for `facility AS4`:

```text
[F04] AS4 - Faculty of Arts and Social Sciences, Block 4

Accessibility Features:
STEP_FREE_ENTRANCE | YES | Ground floor entrance
LIFT | YES | Lift serves floors 1-6
ACCESSIBLE_WASHROOM | YES | Floors 3-6

Sample local accessibility reference data. Distances are estimates and may be incomplete.
Please verify with current campus information when needed.
```

Features recorded as NO are displayed as well. Missing optional descriptions or
notes are omitted. A facility without feature records explicitly reports that
no accessibility features are recorded.

### Find facilities by feature

Format:

```text
facility find type/FEATURE [status/YES|NO|UNKNOWN]
```

Examples:

```text
facility find type/LIFT
facility find type/LIFT status/NO
facility find type/LIFT status/UNKNOWN
facility find type/REST_POINT
```

Supported features are LIFT, RAMP, SHELTERED_RAMP, ACCESSIBLE_WASHROOM,
STEP_FREE_ENTRANCE, REST_POINT, AUTOMATIC_DOOR, and OTHER.

The status defaults to YES when omitted. When supplied, `status/` must follow
`type/`. Results preserve dataset order and display matching facility IDs and names.

For example, `facility find type/LIFT status/NO` returns AS1 and AS2.
`facility find type/REST_POINT` returns AS8. A valid search with no recorded
matches reports `No matching facilities found.`

UNKNOWN means the local dataset does not confirm the feature. Only explicitly
recorded UNKNOWN statuses match this filter; a missing feature is not treated as
UNKNOWN or NO.

### Warnings and recovery

Invalid input displays one message beginning with `[WARNING]`. The application
continues accepting commands afterward. Unknown hubs list the supported hubs;
invalid feature types or statuses list supported values. Malformed commands show
the relevant usage and an example.

For example:

```text
facility AS99
[WARNING] Unknown facility 'AS99'.
```

The supported facilities and lookup usage follow this warning. Enter
`facility AS4` afterward to continue with a valid lookup.

Accessibility information is sample local reference data. Verify it with current
campus information before relying on it.

## Activity Commands

Activity indexes are one-based and refer to the chronological `list` order.
The same index is used by `delete`, `mark`, and `unmark`. The `list demand`
view changes the display order, so use `list` when choosing an index for a
state-changing command.

```text
help
add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]
list
list demand
delete INDEX
mark INDEX
unmark INDEX
```

Activity names preserve capitalization, and activity parameters may appear in
any order. Dates use `YYYY-MM-DD`, times use 24-hour `HH:mm`, and demand is
LOW, MEDIUM, or HIGH (defaulting to MEDIUM when omitted). Completion is shown
as `[X]` or `[ ]` in activity lists.

Activity records are loaded at startup and saved after successful add, delete,
mark, and unmark changes. If a data row is malformed, UniEnable skips that row,
reports its line number, and preserves the original file in a
`.corrupt.bak` backup before a later save.
