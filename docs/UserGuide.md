# UniEnable User Guide

## Introduction

UniEnable is a command-line application for managing dated activities and checking
campus accessibility information. Add activities with optional importance, urgency
and commitment ratings, edit activities, search their names, track completion,
rank activities by priority, count today's high-demand activities, identify
overdue and upcoming activities, search facilities, and find shortest recorded
accessible routes. Activity changes save automatically.

Calendar dashboards (`view today`, `view tomorrow`, and `view week`) from the
V2.0 proposal remain planned and are unavailable in this build. The commands
documented under [Features](#features) are implemented.

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
- For `add` and `edit`, fields can appear in any order. Names can contain spaces.
  For `edit`, put the activity index before the fields.
- Separate fields with whitespace (for example, `n/Meeting d/2026-12-01`).
  Do not put text before the first field.
- Enter each command on one line, including all optional fields.
- Dates use `YYYY-MM-DD`, such as `2026-12-01`; times use 24-hour `HH:mm`.
- Activity indexes start at 1. Change commands (`delete`, `edit`, `mark`, and `unmark`)
  use indexes from chronological `list` output. `list demand` numbers can differ,
  while `find`, `list priority`, `overdue`, and `next` retain full chronological
  indexes, even when some indexes are missing from the results. Run `list` before
  changing an activity; adding, deleting, or rescheduling can change indexes.
- Sample outputs below omit the application's horizontal borders.
  The activity walkthrough, from `add` through `delete`, starts with an empty list
  and runs in order. Additional rating examples follow the walkthrough.
  If you try them after the example dates, replace the dates with future dates.


<!-- @@author Oliver-koh -->

## Data Storage

Activities load at startup from `data/activities.txt`, relative to the folder
where you launch the application. A missing file starts an empty list. Successful
add, edit, delete, and completion changes save the entire activity list automatically.
Every successful `edit` requests a save, even if the supplied values match the
existing values. Read-only commands (`find`, `list`, `list demand`, `demand today`,
`list priority`, `overdue`, `next`, facility queries, routes, and `help`) and `bye`
do not save or retry a failed save. Repeated `mark`/`unmark` commands that leave
completion unchanged also do not save.

Saved activity rows use nine fields:
```text
NAME|DATE|START|END|DEMAND|DONE|IMP|URG|COM
```
`DONE` is `true` or `false`. Each rating is a whole number from 1 to 5, or `-`
when unset. Ratings and completion state survive saving and restarting.
Existing six-field V1.0 rows still load, with all three ratings unset. Historical
activities can load; the end-after-now rule applies to adding an activity and
to edits that supply a date, start time, or end time. Metadata-only edits can
update historical activities without changing their date or times.
Loading alone does not rewrite the file. The next successful activity-changing
save writes every row in the nine-field format. V1.0 cannot load nine-field rows;
keep a separate copy of a V1.0 file if you need to return to that version.
The loader does not check whether a stored end follows its start. Do not treat
loading without warnings as validation of manually edited activity times.

Malformed rows are skipped with line-numbered warnings. If the file cannot be
read, the app reports `Activity data unavailable:` and starts with an empty list.
In either case, before a later save, the original file must be copied and verified
at `data/activities.txt.corrupt.bak`. If the backup fails, the save fails without
replacing the activity file. Copy this backup elsewhere before recovery; a later
recovery backup can overwrite it.

After a save failure, keep the app open and follow the
[recovery steps](#error-handling) before exiting.
Facility and connection reference data are bundled and read-only.


<!-- @@author -->

## Features


<!-- @@author Atharva005 -->

### Adding an activity: `add`

Add an incomplete activity with a date, time, and optional demand and ratings.

Format:

```text
add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH] [imp/IMP] [urg/URG] [com/COM]
```

`n/`, `d/`, `s/`, and `e/` are required and must each appear once with a value.
Every supplied known prefix must appear once and contain a value.


<!-- @@author -->

| Optional field | Meaning | Accepted values | When omitted |
| --- | --- | --- | --- |
| `dem/` | Activity demand, separate from ratings | LOW, MEDIUM, HIGH | MEDIUM |
| `imp/` | Importance | Whole number from 1 to 5 | Unset (`-`) |
| `urg/` | Urgency | Whole number from 1 to 5 | Unset (`-`) |
| `com/` | Commitment | Whole number from 1 to 5 | Unset (`-`) |

<!-- @@author Atharva005 -->



<!-- @@author -->


<!-- @@author lukelouyu -->

Supply all, some, or none of the ratings. To leave one unset, omit its prefix;
do not enter a literal `-`. Empty values, duplicate prefixes, ratings outside
1 to 5, decimals, and text are rejected.

<!-- @@author -->


<!-- @@author Atharva005 -->


The end time must be strictly after the start time on
the same day; overnight activities are not supported. The activity's date and end
time together must also be strictly after the current date/time in your computer's
local time zone. An activity that has already started can still be added if it
has not ended; an activity ending exactly now is rejected. New activities start
incomplete.

Names must contain at least one non-whitespace character. Chinese, accented and
emoji names are supported. Names cannot contain `|` or line breaks.
Slash-containing text such as `and/or`
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


<!-- @@author -->


<!-- @@author officialasyrafrahim -->

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

An empty list shows `[WARNING] No activities yet.` beneath the heading
(and demand-view notice).


<!-- @@author -->


<!-- @@author lukelouyu -->

### Counting today's high-demand activities: `demand today`

See how many activities with demand `HIGH` are scheduled today, so you can
anticipate a demanding day.

Format: `demand today`

- Today is the computer's current local date, checked each time the command runs.
  The result includes the date so you can see which day was counted.
- The count includes completed and incomplete activities across the whole day,
  including activities that have already ended. Marking or unmarking an activity
  does not change the count.
- Activities on other dates and those with `LOW` or `MEDIUM` demand are excluded.
  Importance, urgency and commitment ratings do not affect the count.
- An empty schedule or no matching activities produces a count of `0` with
  `[WARNING]` before the count message.
- The command reads the current activity list without changing or saving it.

With the two activities in the listing example above, assume the current local
date is 2026-12-02. Input: `demand today`

Output:

```text
High-demand activities scheduled today (2026-12-02): 1
```

Project meeting is counted even if it has already ended or is marked done.
On 2026-12-01, the same two activities instead produce:

```text
[WARNING] High-demand activities scheduled today (2026-12-01): 0
```

`DEMAND TODAY` also works. Missing or unsupported subcommands and extra arguments,
such as `demand`, `demand tomorrow`, or `demand today extra`, produce a warning
ending with `Usage: demand today`.


<!-- @@author -->


<!-- @@author Oliver-koh -->

### Changing completion: `mark` and `unmark`

Formats: `mark INDEX` and `unmark INDEX`

Input: `mark 1`

Output: `Marked activity as done: CS2113 tutorial`

Input: `unmark 1`

Output: `Unmarked activity: CS2113 tutorial`

Repeating a command reports `[WARNING] Activity is already marked as done: NAME`
or `[WARNING] Activity is already unmarked: NAME`, without changing or saving it.


<!-- @@author -->


<!-- @@author officialasyrafrahim -->

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
When the last activity is deleted, the confirmation ends with
`There is no activity recorded` instead of a remaining count.


<!-- @@author -->


<!-- @@author lukelouyu -->

### Trying optional add ratings

Try these after the activity walkthrough above. They add to your current list
and are not included in its earlier sample outputs. First re-add the Project
meeting removed by `delete 2`, then add the three rating examples. This creates
the five-activity schedule used by the priority, overdue, and next examples.
Enter each command on its own line:

```text
add n/Project meeting d/2026-12-02 s/14:00 e/15:00 dem/HIGH
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


<!-- @@author -->


<!-- @@author Oliver-koh -->

### Editing an activity: `edit`

Change selected fields while preserving omitted fields and completion status.

Format:

```text
edit INDEX [n/NAME] [d/YYYY-MM-DD] [s/HH:mm] [e/HH:mm] [dem/LOW|MEDIUM|HIGH] [imp/IMP] [urg/URG] [com/COM]
```

- Use an index from chronological `list` or activity `find` output and supply
  at least one field. The command updates one activity at a time.
- Each supplied field must have a value and appear once. Names follow the same
  prefix, pipe and line-break restrictions as `add`.
- Omitted fields, including ratings, keep their existing values. An omitted
  demand does not reset to MEDIUM. Ratings accept 1 to 5; `-` cannot clear a rating.
- If any date/time field is supplied, the combined old and new values must form
  a same-day activity with its end strictly after its start and the current local
  time. A change only to name, demand, or ratings can update historical activities.
- Validation failures leave the activity unchanged. Successful edits save
  automatically and display all resulting fields, including ratings.
- Rescheduling can change chronological indexes. Run `list` or `find` again
  before selecting another activity.

Try these editing and searching examples in a **separate empty test folder**;
they are independent of the earlier activity walkthrough:

```text
add n/CCA EXCO meeting d/2026-12-02 s/14:00 e/15:00 dem/HIGH imp/5 urg/4 com/3
mark 1
edit 1 n/CCA EXCO planning meeting com/4
```

The edit output is:

```text
Edited activity: CCA EXCO planning meeting
Date: 2026-12-02
Time: 14:00 - 15:00
Demand: HIGH
Importance: 5
Urgency: 4
Commitment: 4
```

Completion stays `[X]`; omitted date, times, demand, importance and urgency remain
unchanged. For a timing change, supply the date, start or end fields as needed;
the resulting start must still be earlier than the resulting end.

Both `add` and `edit` reject whitespace-only names, including Unicode spaces such
as U+2003 EM SPACE and U+3000 IDEOGRAPHIC SPACE. Rejected input leaves the activity
list and saved data unchanged.

### Searching activity names: `find`

Find activities by a case-insensitive substring of their names.

Format: `find KEYWORD`

- Supply a non-blank name substring. Multiple words form one literal phrase;
  do not add quotation marks unless the quotes are part of the name.
- Searches include completed and incomplete activities. They search names,
  rather than dates, demand levels or ratings, and do not interpret regular expressions.
- Matches appear chronologically with indexes from the **full** chronological
  list. For example, a single match can still be numbered 2.
- Searching does not change activities or save the activity file.

Continuing the editing example, add an earlier tutorial and search:

```text
add n/CS2113 tutorial class d/2026-12-01 s/09:00 e/10:00
find EXCO planning
```

Output:

```text
Found 1 matching activity:
2. CCA EXCO planning meeting (2026-12-02, 14:00-15:00) [HIGH] [X]
```

`find exco PLANNING` returns the same activity. `find exam` reports
`[WARNING] No activities matched keyword "exam".`; a valid search on an empty list has
the same no-match form. `find` without a keyword reports a warning and usage.


<!-- @@author -->


<!-- @@author officialasyrafrahim -->

### Viewing priority: `list priority`

`list priority` includes completed and incomplete activities. An activity receives
a score only when importance, urgency, and commitment are all set:

```text
Priority = URG x URG x (IMP + COM)
```

Available scores range from 2 to 250. Demand and completion do not affect the
formula. Higher scores appear first. Activities with missing ratings show
`Priority NA` and appear after every scored activity. Equal scores keep date and
start-time order, and equal date/time keep add order. Indexes refer to chronological
`list` output.

Return to the original activity-walkthrough folder, after restoring Project
meeting and adding the rating examples. The separate edit/find examples do not
change this five-activity schedule. With those five activities still incomplete:

```text
Activities by priority (highest first):
Indexes refer to the chronological 'list' order.
4. CS2113 project (2026-12-04, 14:00-15:00) [MEDIUM] [ ] | Priority 225 (IMP 5, URG 5, COM 4)
1. CS2113 tutorial (2026-12-01, 10:00-11:00) [MEDIUM] [ ] | Priority NA (IMP -, URG -, COM -)
2. Project meeting (2026-12-02, 14:00-15:00) [HIGH] [ ] | Priority NA (IMP -, URG -, COM -)
3. Planning and/or review (2026-12-03, 10:00-11:00) [LOW] [ ] | Priority NA (IMP 5, URG -, COM 3)
5. Team stand-up (2026-12-04, 16:00-17:00) [MEDIUM] [ ] | Priority NA (IMP 5, URG 4, COM -)
```

### Viewing overdue activities: `overdue`

`overdue` shows incomplete activities whose start is strictly before the current
local time. An activity that has started but not ended is included. Completed
activities and activities starting exactly now or later are excluded.

Each row keeps its chronological `list` index and adds one label:

| Label | Meaning |
| --- | --- |
| `OVERDUE TODAY` | Starts earlier today. |
| `OVERDUE WEEK` | Starts on an earlier day in the current Monday-to-Sunday week. |
| `OVERDUE EARLIER` | Starts before the current week's Monday. |

Using the same five-activity schedule, assume the current local time is
2026-12-04 at 15:00. The containing week starts on Monday, 2026-11-30:

```text
Overdue activities (started before now):
1. [OVERDUE WEEK] CS2113 tutorial (2026-12-01, 10:00-11:00) [MEDIUM] [ ]
2. [OVERDUE WEEK] Project meeting (2026-12-02, 14:00-15:00) [HIGH] [ ]
3. [OVERDUE WEEK] Planning and/or review (2026-12-03, 10:00-11:00) [LOW] [ ]
4. [OVERDUE TODAY] CS2113 project (2026-12-04, 14:00-15:00) [MEDIUM] [ ]
```

If nothing is overdue, the command shows `[WARNING] No overdue activities.`

### Viewing the next activity: `next`

`next` shows the single earliest incomplete activity whose start is strictly after
the current local time. Activities starting exactly now and completed activities
are skipped. Equal starts keep add order, and the row keeps its chronological
`list` index.

At the same assumed local time, 2026-12-04 at 15:00, with the same five activities:

```text
Next activity (earliest upcoming):
5. Team stand-up (2026-12-04, 16:00-17:00) [MEDIUM] [ ]
```

If no incomplete activity starts later, the command shows
`[WARNING] No upcoming incomplete activities.`

For an incomplete activity scheduled 09:00-09:30 on the current date:

| Current local time | In `overdue`? | Eligible for `next`? |
| --- | --- | --- |
| 08:59 | No | Yes, if it is the earliest future incomplete activity. |
| Exactly 09:00 | No | No. |
| 09:15 | Yes, even though it is still in progress. | No. |

A completed activity appears in neither time query. At its exact start, an
incomplete activity appears in neither query; it remains visible in `list`.
These strict boundaries also apply when the current clock includes seconds.


<!-- @@author -->


<!-- @@author lukelouyu -->

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

`facility find type/REST_POINT` returns AS8; an unmatched search shows
`[WARNING] No matching facilities found.`.

### Finding an accessible route: `route`

Format: `route from/START to/END`

Use hub names (AS1-AS8 or CLB), rather than IDs such as F01.
Keep `from/` before `to/`. Routes minimize recorded distance using only connections
marked YES for accessibility. Shelter, barriers, and notes are displayed;
a shortest route may include unsheltered sections or recorded barriers.
If the source and destination are the same hub, including names that differ only
in letter case, the command shows
`[WARNING] Sorry, the source and destination are the same.`

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


<!-- @@author -->

### Viewing help and exiting: `help` and `bye`

Input: `help`

Output begins with `UniEnable commands:` and lists all available commands,
including activity `edit` and `find`, optional ratings, activity index rules and automatic saving
location.

Input: `bye`

Output: `Goodbye from UniEnable!`

## Error Handling

Invalid input usually produces `[WARNING]` and the app waits for another command.
Empty activity views, searches with no matches, a zero daily-demand count, and
unavailable route results also include `[WARNING]` on the no-result message.
Facility lists without records or recorded features use the same marker.
Repeated mark/unmark requests warn that completion is already in the requested
state. These query and no-change warnings do not save or modify activities.
Common causes include missing/duplicate fields, invalid dates or times, unknown
facilities, or an activity index outside the current list.
For `add` and `edit`, use real calendar dates and two-digit hours and minutes (for example,
`09:30`, not `9:30`). An invalid add command also displays the expected format.
Rejected add or edit input, including an invalid rating, leaves the activity list and
stored data unchanged.

`list priority`, `overdue`, and `next` accept no arguments. Extra arguments and
unsupported list variants produce a warning; they do not change activities.

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

**Can I edit an activity or facility?** Use `edit INDEX` with at least one field
to update an activity while keeping omitted values and completion status.
Date/time edits must leave an end after now; metadata-only edits can update
historical activities. Facility records are read-only.

**Why does a search result start at index 2?** Activity `find` preserves indexes
from the full chronological list. Use the displayed index directly for `edit`,
`mark`, `unmark`, or `delete`; indexes can change after editing dates or times.

## Command Summary

| Command | Purpose |
| --- | --- |
| `help` / `bye` | Show built-in help / exit |
| `add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW\|MEDIUM\|HIGH] [imp/IMP] [urg/URG] [com/COM]` | Add an activity with optional ratings |
| `list` / `list demand` | View activities by time / demand |
| `demand today` | Count today's HIGH-demand activities, including completed ones |
| `edit INDEX [n/NAME] [d/YYYY-MM-DD] [s/HH:mm] [e/HH:mm] [dem/LOW\|MEDIUM\|HIGH] [imp/IMP] [urg/URG] [com/COM]` | Update supplied activity fields; preserve omitted fields and completion |
| `find KEYWORD` | Search activity names and retain chronological list indexes |
| `list priority` | View all activities by combined priority score |
| `overdue` / `next` | View incomplete activities that started before now / the earliest upcoming incomplete activity |
| `mark INDEX` / `unmark INDEX` / `delete INDEX` | Change completion / delete |
| `facility list` / `facility LOCATION` | View all facilities / one facility |
| `facility find type/FEATURE [status/YES\|NO\|UNKNOWN]` | Search recorded features |
| `route from/START to/END` | Find a shortest recorded accessible route |
