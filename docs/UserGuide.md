# UniEnable User Guide (v1.0)

## Introduction

UniEnable is a command-line application for managing dated activities and checking
campus accessibility information. v1.0 supports activity demand and completion,
automatic saving, facility searches, and shortest recorded accessible routes.

## Quick Start

1. Install Java 25 and obtain `unienable.jar`.
2. Open a terminal in the folder containing the JAR.
3. Run `java -jar unienable.jar`.
4. Enter a command and press Enter; use `help` to view the built-in command list.
5. Enter `bye` to exit.

To build from source on Windows, run `.\gradlew.bat shadowJar` in the project folder,
then `java -jar build/libs/unienable.jar`. Use Java 25 for both commands.

## Command Format

- Words in UPPER_CASE are placeholders; replace them with your own values.
- Square brackets in command formats indicate optional fields; do not type them.
- Command words, prefixes, demand levels, feature types, statuses, and hub codes
  are case-insensitive. Activity names preserve their capitalization.
- For `add`, fields can appear in any order. Names can contain spaces.
- Dates use `YYYY-MM-DD`, such as `2026-10-20`; times use 24-hour `HH:mm`.
- Activity indexes start at 1 and always refer to the chronological `list` order.
  Run `list` before `delete`, `mark`, or `unmark`; demand-view indexes can differ.
- Sample outputs below omit the application's horizontal borders.
  Activity examples assume an initially empty activity list and run in order.

## Data Storage

Activities load at startup from `data/activities.txt`, relative to the folder
where you launch the application. Successful add, delete, and completion changes
are saved automatically; a missing file starts an empty list.
Malformed rows are skipped with line-numbered warnings. Before a later save,
the original malformed file is copied to `data/activities.txt.corrupt.bak`.
Facility and connection reference data are bundled and read-only.

## Features

### Adding an activity: `add`

Format: `add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]`

`n/`, `d/`, `s/`, and `e/` are required and must each appear once with a value.
Demand defaults to MEDIUM; the end time must be later on the same day.
Names cannot contain `|`. Duplicate or overlapping activities are currently allowed.

Input:
```text
add n/CS2113 tutorial d/2026-10-20 s/10:00 e/11:00
```
Output:
```text
Added activity: CS2113 tutorial
Date: 2026-10-20
Time: 10:00 - 11:00
Demand: MEDIUM
Activities in this session: 1
```

For the following examples, also enter:
```text
add n/Project meeting d/2026-10-21 s/14:00 e/15:00 dem/HIGH
```
The app confirms Project meeting, its date/time, HIGH demand, and a total of 2 activities.

### Listing activities: `list` and `list demand`

`list` sorts by date, then start time. `list demand` sorts HIGH, MEDIUM, LOW,
then by date and start time for ties. Completion appears as `[X]` or `[ ]`.

Input: `list`

Output:
```text
Activities (by date, start time):
1. CS2113 tutorial (2026-10-20, 10:00-11:00) [MEDIUM] [ ]
2. Project meeting (2026-10-21, 14:00-15:00) [HIGH] [ ]
```

Input: `list demand`

Output:
```text
Activities by demand (HIGH to LOW):
Note: Do not use indexes from 'list demand' for deletion.
Run 'list' to find the correct index for 'delete INDEX'.
1. Project meeting (2026-10-21, 14:00-15:00) [HIGH] [ ]
2. CS2113 tutorial (2026-10-20, 10:00-11:00) [MEDIUM] [ ]
```

An empty list shows `No activities yet.` beneath the heading (and demand-view notice).

### Changing completion: `mark` and `unmark`

Formats: `mark INDEX` and `unmark INDEX`

Input: `mark 1`

Output: `Marked activity as done: CS2113 tutorial`

Input: `unmark 1`

Output: `Unmarked activity: CS2113 tutorial`

Repeating a command reports that the activity is already in the requested state.

### Deleting an activity: `delete`

Format: `delete INDEX`

Input: `delete 2` (index from `list`, not `list demand`)

Output:
```text
Deleted activity: Project meeting
Date: 2026-10-21
Time: 14:00 - 15:00
Demand: HIGH
Activities in this session: 1
```

The activity is removed immediately; subsequent indexes may change.

### Listing and viewing facilities: `facility`

Formats: `facility list` and `facility LOCATION`

The nine supported hubs are AS1-AS8 and CLB, with IDs F01-F09 respectively.
`facility list` starts with `Known facilities in the local reference:` and shows
all facility details, recorded feature statuses, and notes.

Input: `facility AS4` (or `facility F04`)

Output:
```text
[F04] AS4 - Faculty of Arts and Social Sciences, Block 4

Accessibility Features:
STEP_FREE_ENTRANCE | YES | Ground floor entrance
LIFT | YES | Lift serves floors 1-6
ACCESSIBLE_WASHROOM | YES | Floors 3-6

Sample reference data.
Verify with current campus information before you rely on it.
```

### Finding facilities by feature: `facility find`

Format: `facility find type/FEATURE [status/YES|NO|UNKNOWN]`

Supported features: LIFT, RAMP, SHELTERED_RAMP, ACCESSIBLE_WASHROOM,
STEP_FREE_ENTRANCE, REST_POINT, AUTOMATIC_DOOR, OTHER.
Status defaults to YES; `status/` must follow `type/`.
UNKNOWN matches explicitly recorded UNKNOWN values, rather than missing features.

Input: `facility find type/LIFT status/NO`

Output:
```text
Facilities with LIFT (status: NO):
[F01] AS1
[F02] AS2

Sample reference data.
Verify with current campus information before you rely on it.
```

`facility find type/REST_POINT` returns AS8; an unmatched search shows `No matching facilities found.`.

### Finding an accessible route: `route`

Format: `route from/START to/END`

Use hub names (AS1-AS8 or CLB), rather than IDs such as F01.
Keep `from/` before `to/`. Routes minimize recorded distance using only connections
marked YES for accessibility. Shelter, barriers, and notes are displayed;
a shortest route may include unsheltered sections or recorded barriers.

Input: `route from/as6 to/as7`

Output:
```text
Shortest recorded accessible route:
AS6 -> AS1 -> AS4 -> AS7
Total distance: 200 metres

Connections:
1. AS6 -> AS1: 90 m (Path, Sheltered)
   Note: Main covered walkway
2. AS1 -> AS4: 60 m (Path, Sheltered)
   Recorded barrier: Narrow passageway (2F link via LT9/LT10)
   Note: Via The Deck
3. AS4 -> AS7: 50 m (Path, Sheltered)
   Note: Via The Deck

Sample reference data.
Verify with current campus information before you rely on it.
```

### Viewing help and exiting: `help` and `bye`

Input: `help`

Output begins with `UniEnable commands (Branch 1 sample):` and lists commands.
The current help text contains older wording about pending activity commands
and saving; these functions are available as described above.

Input: `bye`

Output: `Goodbye from UniEnable!`

## Error Handling

Invalid input usually produces `[WARNING]` and the app waits for another command.
Common causes include missing/duplicate fields, invalid dates or times, unknown
facilities, or an activity index outside the current list.

Example input: `help extra`

Output: `[WARNING] help does not take arguments.`

If loading or saving fails, the app reports the problem. After a save failure,
activity changes may remain in memory without being saved to disk.

## FAQ

**Where is my data?** In `data/activities.txt` under the launch folder; use the same
folder on each run. To transfer activities, copy this file before starting the app.

**Can I edit an activity or facility?** v1.0 has no activity edit command
(delete and add instead) and no facility editing commands.

## Command Summary

| Command | Purpose |
| --- | --- |
| `help` / `bye` | Show built-in help / exit |
| `add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW\|MEDIUM\|HIGH]` | Add an activity |
| `list` / `list demand` | View activities by time / demand |
| `mark INDEX` / `unmark INDEX` / `delete INDEX` | Change completion / delete |
| `facility list` / `facility LOCATION` | View all facilities / one facility |
| `facility find type/FEATURE [status/YES\|NO\|UNKNOWN]` | Search recorded features |
| `route from/START to/END` | Find a shortest recorded accessible route |
