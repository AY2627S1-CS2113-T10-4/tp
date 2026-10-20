# UniEnable User Guide

## Introduction

UniEnable is a command-line application for managing dated activities and checking
campus accessibility information. Add activities with optional importance, urgency
and commitment ratings, track completion, search facilities, and find shortest
recorded accessible routes. Activity changes save automatically.

Activity editing, activity-name searching, priority ordering, overdue/next queries,
and calendar dashboards are planned features, unavailable in this build.

[Quick Start](#quick-start) · [Command Format](#command-format) ·
[Data Storage](#data-storage) · [Features](#features) ·
[Error Handling](#error-handling) · [FAQ](#faq) · [Command Summary](#command-summary)

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
- Separate fields with whitespace (for example, `n/Meeting d/2026-12-01`).
  Do not put text before the first field.
- Enter each command on one line, including all optional fields.
- Dates use `YYYY-MM-DD`, such as `2026-12-01`; times use 24-hour `HH:mm`.
- Activity indexes start at 1. Change commands (`delete`, `mark`, and `unmark`)
  use indexes from chronological `list` output; `list demand` numbers can differ.
  Run `list` before changing an activity.
- Sample outputs below omit the application's horizontal borders.
  The activity walkthrough, from `add` through `delete`, starts with an empty list
  and runs in order. Additional rating examples follow the walkthrough.
  If you try them after the example dates, replace the dates with future dates.

## Data Storage

Activities load at startup from `data/activities.txt`, relative to the folder
where you launch the application. A missing file starts an empty list. Successful
add, delete, and completion changes save the entire activity list automatically.
Commands that leave activities unchanged, including `list`, `help`, repeated
`mark`/`unmark`, and `bye`, do not save or retry a failed save.

Saved activity rows use nine fields:
```text
NAME|DATE|START|END|DEMAND|DONE|IMP|URG|COM
```
`DONE` is `true` or `false`. Each rating is a whole number from 1 to 5, or `-`
when unset. Ratings and completion state survive saving and restarting.
Existing six-field V1.0 rows still load, with all three ratings unset. Historical
activities can load; the end-after-now rule applies to adding an activity.
Loading alone does not rewrite the file. The next successful activity-changing
save writes every row in the nine-field format. V1.0 cannot load nine-field rows;
keep a separate copy of a V1.0 file if you need to return to that version.

Malformed rows are skipped with line-numbered warnings. If the file cannot be
read, the app reports `Activity data unavailable:` and starts with an empty list.
In either case, before a later save, the original file must be copied and verified
at `data/activities.txt.corrupt.bak`. If the backup fails, the save fails without
replacing the activity file. Copy this backup elsewhere before recovery; a later
recovery backup can overwrite it.

After a save failure, keep the app open and follow the
[recovery steps](#error-handling) before exiting.
Facility and connection reference data are bundled and read-only.

## Features

### Adding an activity: `add`

Add an incomplete activity with a date, time, and optional demand and ratings.

Format:

```text
add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH] [imp/IMP] [urg/URG] [com/COM]
```

`n/`, `d/`, `s/`, and `e/` are required and must each appear once with a value.
Every supplied known prefix must appear once and contain a value.

| Optional field | Meaning | Accepted values | When omitted |
| --- | --- | --- | --- |
| `dem/` | Activity demand, separate from ratings | LOW, MEDIUM, HIGH | MEDIUM |
| `imp/` | Importance | Whole number from 1 to 5 | Unset (`-`) |
| `urg/` | Urgency | Whole number from 1 to 5 | Unset (`-`) |
| `com/` | Commitment | Whole number from 1 to 5 | Unset (`-`) |

Supply all, some, or none of the ratings. To leave one unset, omit its prefix;
do not enter a literal `-`. Empty values, duplicate prefixes, ratings outside
1 to 5, decimals, and text are rejected.

The end time must be strictly after the start time on
the same day; overnight activities are not supported. The activity's date and end
time together must also be strictly after the current date/time in your computer's
local time zone. An activity that has already started can still be added if it
has not ended; an activity ending exactly now is rejected. New activities start
incomplete.

Names cannot contain `|` or line breaks. Slash-containing text such as `and/or`
and `w/o` is allowed. However, a known prefix (`n/`, `d/`, `s/`, `e/`, `dem/`,
`imp/`, `urg/`, or `com/`)
at the start of a whitespace-separated part of the name is treated as another
field, so avoid these prefixes in names. Duplicate or overlapping activities are
currently allowed.

Input:
```text
add n/CS2113 tutorial d/2026-12-01 s/10:00 e/11:00
```
Output:
```text
Added activity: CS2113 tutorial
Date: 2026-12-01
Time: 10:00 - 11:00
Demand: MEDIUM
Importance: -
Urgency: -
Commitment: -
Activities in this session: 1
```

For the following examples, also enter:
```text
add n/Project meeting d/2026-12-02 s/14:00 e/15:00 dem/HIGH
```
The app confirms Project meeting, its date/time, HIGH demand, three unset ratings,
and a total of 2 activities.

### Listing activities: `list` and `list demand`

`list` sorts by date, then start time. `list demand` sorts HIGH, MEDIUM, LOW,
then by date and start time for ties. Completion appears as `[X]` or `[ ]`.

Input: `list`

Output:
```text
Activities (by date, start time):
1. CS2113 tutorial (2026-12-01, 10:00-11:00) [MEDIUM] [ ]
2. Project meeting (2026-12-02, 14:00-15:00) [HIGH] [ ]
```

Input: `list demand`

Output:
```text
Activities by demand (HIGH to LOW):
Note: Do not use indexes from 'list demand' for deletion.
Run 'list' to find the correct index for 'delete INDEX'.
1. Project meeting (2026-12-02, 14:00-15:00) [HIGH] [ ]
2. CS2113 tutorial (2026-12-01, 10:00-11:00) [MEDIUM] [ ]
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
Date: 2026-12-02
Time: 14:00 - 15:00
Demand: HIGH
Activities in this session: 1
```

The activity is removed immediately; subsequent indexes may change.

### Trying optional add ratings

Try these after the activity walkthrough above. They add to your current list
and are not included in its sample outputs. Enter each command on its own line:

```text
add CoM/3 dem/low e/11:00 n/Planning and/or review d/2026-12-03 s/10:00 iMp/5
add n/CS2113 project d/2026-12-04 s/14:00 e/15:00 imp/5 urg/5 com/4
add n/Team stand-up d/2026-12-04 s/16:00 e/17:00 imp/5 urg/4
```

The first command also demonstrates reordered fields, mixed-case prefixes, and
ordinary slashes in a name. Its demand is LOW; the others default to MEDIUM.

| Activity | Importance | Urgency | Commitment |
| --- | --- | --- | --- |
| Planning and/or review | 5 | - | 3 |
| CS2113 project | 5 | 5 | 4 |
| Team stand-up | 5 | 4 | - |

The add confirmation displays the ratings, and saving preserves them across
restarts. `list` and `list demand` show date, time, demand and completion, without
ratings.

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

Output begins with `UniEnable commands:` and lists all available commands,
including the optional add ratings, activity index rules and automatic saving
location.

Input: `bye`

Output: `Goodbye from UniEnable!`

## Error Handling

Invalid input usually produces `[WARNING]` and the app waits for another command.
Common causes include missing/duplicate fields, invalid dates or times, unknown
facilities, or an activity index outside the current list.
For `add`, use real calendar dates and two-digit hours and minutes (for example,
`09:30`, not `9:30`). An invalid add command also displays the expected format.
Invalid add input, including an invalid rating, leaves the activity list and
stored data unchanged.

Example input: `help extra`

Output: `[WARNING] help does not take arguments.`

If saving fails, the change may already be in memory. Repeating the same `add`
can create a duplicate; exiting with `bye` does not retry the save.

To recover before exiting:

1. Run `list` and record the activities you need to keep.
2. Fix the reported file or backup-path problem, keeping a separate copy of any
   existing data or recovery backup.
3. Run `list` again and choose an activity. If it is incomplete, run `mark INDEX`
   followed by `unmark INDEX`; if completed, reverse the commands. Each actual
   change retries saving the entire list. If the list is empty, add a temporary
   activity with a future end time, then delete it.
4. Check that both changes succeed without a save error before exiting. Restart
   and run `list` to confirm the saved result.

## FAQ

**Where is my data?** In `data/activities.txt` under the launch folder; use the same
folder on each run. To transfer activities, copy this file before starting the app.

**Can I edit an activity or facility?** This build has no activity edit command
or facility editing commands. To replace an activity, delete it and add the
replacement; its end date/time must still be in the future. Keep historical
activities if they cannot be re-added under this rule.

## Command Summary

| Command | Purpose |
| --- | --- |
| `help` / `bye` | Show built-in help / exit |
| `add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW\|MEDIUM\|HIGH] [imp/IMP] [urg/URG] [com/COM]` | Add an activity with optional ratings |
| `list` / `list demand` | View activities by time / demand |
| `mark INDEX` / `unmark INDEX` / `delete INDEX` | Change completion / delete |
| `facility list` / `facility LOCATION` | View all facilities / one facility |
| `facility find type/FEATURE [status/YES\|NO\|UNKNOWN]` | Search recorded features |
| `route from/START to/END` | Find a shortest recorded accessible route |
