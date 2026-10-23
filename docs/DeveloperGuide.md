# UniEnable Developer Guide

<!-- SHARED: All members maintain the contents, introduction and setup instructions. -->

## Table of contents

- [Introduction](#introduction)
- [Setting up and getting started](#setting-up-and-getting-started)
- [Design and implementation](#design-and-implementation)
  - [Shared design](#shared-design)
    - [Architecture](#architecture)
  - [Atharva: Activity creation and help](#atharva-activity-creation-and-help)
  - [Asyraf: Activity views and deletion](#asyraf-activity-views-and-deletion)
    - [Ordering and displayed indexes](#ordering-and-displayed-indexes)
    - [Priority, overdue and next views](#priority-overdue-and-next-views)
  - [Oliver: Completion, storage, editing and search](#oliver-completion-storage-editing-and-search)
  - [Ratings and accessibility](#ratings-and-accessibility)
    - [Optional ratings integration](#optional-ratings-integration)
    - [Today's high-demand count](#todays-high-demand-count)
    - [Facility Accessibility Data (`facilities.txt`)](#facility-accessibility-data-facilitiestxt)
    - [Facility lookup](#facility-lookup)
    - [Accessibility Route Connections (`connections.txt`)](#accessibility-route-connections-connectionstxt)
    - [Accessible route search](#accessible-route-search)
    - [Exiting the application](#exiting-the-application)
- [Testing and defensive programming](#testing-and-defensive-programming)
- [Product scope](#product-scope)
  - [Team contributions](#team-contributions)
- [User stories](#user-stories)
  - [Core schedule](#core-schedule)
  - [Views and reminders](#views-and-reminders)
  - [Energy and priority](#energy-and-priority)
  - [Accessible navigation](#accessible-navigation)
  - [Foundation](#foundation)
- [Non-functional requirements](#non-functional-requirements)
- [Glossary](#glossary)
- [Instructions for manual testing](#instructions-for-manual-testing)
- [Acknowledgements](#acknowledgements)

## Introduction

UniEnable is a Java command-line application for managing dated activities and
checking campus accessibility reference information. It combines activity
planning with facility lookup and route search.

The target users are tertiary students with ASD, ADHD, or mobility constraints
who are adapting to university routines and managing commitments independently.

This guide explains the current design for developers maintaining the application.
See the [User Guide](UserGuide.md) for commands and examples. Accessibility records
are bundled sample data; check results against current campus information before use.

## Setting up and getting started

Install **JDK 25** and clone the repository. Check `java -version` before building
or running the application. In IntelliJ IDEA, import the project as a Gradle
project and select JDK 25 for both the project SDK and Gradle JVM. The
[README](../README.md#setting-up-in-intellij) gives the IDE setup steps.

Run these commands from the project root using the included Gradle wrapper.
The first build needs internet access to download Gradle and dependencies.

| Purpose | Windows PowerShell | macOS / Linux |
| --- | --- | --- |
| Compile the application | `.\gradlew.bat classes` | `./gradlew classes` |
| Run automated tests | `.\gradlew.bat test` | `./gradlew test` |
| Run all configured checks | `.\gradlew.bat check` | `./gradlew check` |
| Run the console application | `.\gradlew.bat run --console=plain` | `./gradlew run --console=plain` |
| Build the executable JAR | `.\gradlew.bat shadowJar` | `./gradlew shadowJar` |

`check` runs tests, Checkstyle, and the configured JaCoCo coverage checks. The
application entry point is `seedu.unienable.UniEnable`. Gradle's `run` task enables
assertions; add `-ea` to the VM options when running the main method directly in
an IDE. After `shadowJar`, run the JAR with assertions enabled:

```text
java -ea -jar build/libs/unienable.jar
```

Enter `help` to see the commands and `bye` to exit. Activity data is stored at
`data/activities.txt`, relative to the directory where the application starts.
Use the same working directory between sessions to load the same activities.
Facility and connection data come from `src/main/resources/facilities.txt` and
`src/main/resources/connections.txt`; these resources are also included in the JAR.

## Design and implementation

<!-- SHARED: All members maintain this ownership overview and the diagram index. -->

Asyraf's, Luke's and shared implementation write-ups are populated. The other members'
main headings mark where they should add their documentation.

Implementation sections and diagram folders follow the responsibility assignments
in [Team contributions](#team-contributions). In the Markdown source, `BEGIN OWNER`
and `END OWNER` comments mark each member's editing block. Add implementation
details inside your block; maintain sections marked `SHARED` jointly.

| Maintainer | Implementation sections | PlantUML sources | Rendered images |
| --- | --- | --- | --- |
| Shared | [Shared design](#shared-design) | [shared/](diagrams/shared) | [shared/](images/shared) |
| [Atharva](team/atharva005.md) | [Atharva: Activity creation and help](#atharva-activity-creation-and-help) | Awaiting contribution | Awaiting contribution |
| [Asyraf](team/officialasyrafrahim.md) | [Asyraf: Activity views and deletion](#asyraf-activity-views-and-deletion) | [asyraf/](diagrams/asyraf) | [asyraf/](images/asyraf) |
| [Oliver](team/oliver-koh.md) | [Oliver: Completion, storage, editing and search](#oliver-completion-storage-editing-and-search) | Awaiting contribution | Awaiting contribution |
| [Luke](team/lukelouyu.md) | [Ratings and accessibility](#ratings-and-accessibility) | [luke/](diagrams/luke) | [luke/](images/luke) |

<!-- BEGIN SHARED: All members maintain the architecture and shared models jointly. -->

### Shared design

Architecture and models used across members' features are maintained jointly.

#### Architecture

![Architecture diagram showing the console, parsers, commands, models, accessibility logic, and storage](images/shared/ArchitectureDiagram.png)

`UniEnable` restores activities; `ConsoleHelper` loads facilities before connections
and creates the managers and parser. `Ui` reads input, `Parser` creates a command,
and the command queries or updates a model or manager. Its `CommandResult` carries
a message, an exit flag and an activity-change flag. `ConsoleHelper` saves only
when activities changed, then displays the message; the exit flag ends the loop.

| Area | Main classes and responsibilities |
| --- | --- |
| Startup and console loop | `UniEnable` runs until `bye` or end of input. `ConsoleHelper` loads reference data, creates the parser, executes commands and saves changes. |
| User interface | `Ui` reads and displays text. `ActivityListFormatter`, `FacilityOutputFormatter` and `FacilityDetailsFormatter` build messages; `RouteCommand` formats its own result. |
| Parsing | `Parser` dispatches to `AddCommandParser`, `EditCommandParser`, `ListDeleteCommandParser`, `MarkCommandParser`, `FacilityCommandParser` or `RouteParser`. It checks `find`, `demand today`, `overdue` and `next` directly. A shared injected `Clock` makes time checks repeatable. |
| Command execution | Subclasses implement `Command.execute(ActivityList, Storage)` and return a `CommandResult`. |
| Activity model | `Activity` stores details, completion and ratings and calculates priority. `ActivityList` owns the collection, supplies views and demand counts, and resolves displayed indexes against chronological order. |
| Accessibility logic | `FacilityManager` handles facility queries. `ConnectionManager` delegates routes to `DijkstraShortestPath`, with a fresh `DijkstraSolver` per search. |
| Reference models | Immutable `Facility` and `FacilityFeature` objects describe hubs. Immutable `Connection` objects hold endpoints, distance, statuses, traversal type, barriers and notes. |
| Storage | `Storage` loads six- or nine-field activity rows and saves nine-field rows, with recovery backups when needed. `FacilityStorage` and `ConnectionStorage` load bundled reference records and return records plus warnings in `LoadResult<T>`. |
| Exceptions | `UniEnableException` is the checked error contract for recoverable failures. `ParseException` reports invalid commands, while `InvalidIndexException` reports indexes outside the current activity view. |

Recoverable failures use `UniEnableException`; the console displays the problem
and accepts another command. Failed facility loading leaves activity commands,
help and exit available. Failed connection loading disables routes but keeps
facility queries available. Activity-load warnings and failures appear at startup;
a fatal read failure starts an empty list with `Storage` recovery protection.

Diagram source: [ArchitectureDiagram.puml](diagrams/shared/ArchitectureDiagram.puml).
Update diagram sources and their images together. Use Java 25 and PlantUML:
`-tpng` renders PNG diagrams; `-tsvg` renders the connectivity graph.

<!-- END SHARED -->

<!-- BEGIN OWNER: Atharva Rege (@Atharva005). Add activity-creation and help documentation here. -->

### Activity creation and help

Documentation to be completed by Atharva.

<!-- END OWNER: Atharva Rege (@Atharva005) -->

<!-- BEGIN OWNER: Muhammad Asyraf Bin Abdul Rahim (@officialasyrafrahim). Add listing, deletion, priority, overdue and next documentation here. -->

### Activity views and deletion

Asyraf maintains chronological and demand listing, deletion, priority ordering,
and the overdue/next queries, including their displayed-index rules.

#### Ordering and displayed indexes

##### Overview

`list` displays activities by date and start time. `list demand` groups them from
`HIGH` to `LOW`, then by date and start time. Delete, edit, mark and unmark always
use the one-based indexes from chronological `list`, even after a demand-ordered
list has been displayed.

##### High-level design

`ActivityList` keeps its backing list in insertion order and creates separate,
unmodifiable sorted lists for display. The activity objects in those lists are
shared with the backing list. Commands select a view, while
`ActivityListFormatter` renders its supplied order without sorting or printing.

![Class diagram showing activity views, list formatting and deletion](images/asyraf/ActivityOrderingClassDiagram.png)

Source: [ActivityOrderingClassDiagram.puml](diagrams/asyraf/ActivityOrderingClassDiagram.puml).
The [shared activity-creation diagram](diagrams/shared/ActivityCreationClassDiagram.puml)
shows the activity fields omitted here.

##### Implementation and sequence diagram

1. `Parser` delegates `list` and `delete` syntax to `ListDeleteCommandParser`.
   It creates `ListCommand`, `ListDemandCommand` or `DeleteCommand` as appropriate.
   The same parser creates `ListPriorityCommand` for `list priority`.
2. Listing calls `getCanonicalView()` or `getDemandView()`. Sorting is stable:
   activities tied on all ordering fields retain their insertion order.
3. `ActivityListFormatter` numbers the selected view from 1. The demand view
   includes a notice telling users to run `list` before choosing a deletion index.
4. For `delete 1`, `deleteByDisplayedIndex(1)` builds the canonical view, checks
   the index and selects element 0. It removes that activity object from the
   backing list rather than removing backing-list element 0.
5. `DeleteCommand` returns the removed activity's details and the new count with
   the activity-change flag set. If no activities remain, it shows
   `There is no activity recorded` instead of a count. `ConsoleHelper` then saves
   the updated activities before displaying the result, as described in
   [Architecture](#architecture).

The sequence starts after `DeleteCommand(1)` has been parsed. It highlights why
deletion follows chronological display order even when insertion order differs.
It assumes a valid index and omits the subsequent save and display calls.

![Sequence diagram showing deletion through the canonical displayed index](images/asyraf/DeleteActivitySequence.png)

Source: [DeleteActivitySequence.puml](diagrams/asyraf/DeleteActivitySequence.puml).

##### Design considerations

Sorting copies keeps listing read-only and preserves insertion order for storage.
Sorting the backing list in place would make a display command change that order.
Using one canonical index rule also avoids maintaining a separate "last displayed
list" state, though users must check `list` when switching from the demand view.
Priority and time views instead display full chronological indexes directly,
as explained below.

##### Error handling

The parser rejects missing or extra arguments, signed or non-numeric indexes,
and integers too large for `int`. Zero passes syntax checking but is rejected
by `ActivityList`, which also handles empty lists and out-of-range indexes through
`InvalidIndexException`. Invalid deletion leaves activities unchanged.
Assertions check view ordering and deletion invariants; `FINE` log messages record
list and delete operations. These support debugging, while checked exceptions
handle invalid user input. Empty list views show `[WARNING] No activities yet.`.

#### Priority, overdue and next views

##### Overview

`list priority` ranks every activity, including completed ones, by its combined
priority score. `overdue` and `next` query incomplete activities relative to the
current local time. All three views are read-only and display the full
chronological index used by `edit`, `mark`, `unmark` and `delete`.

`Activity.getPriorityScore()` calculates:

```text
URG * URG * (IMP + COM)
```

Each rating is an integer from 1 to 5. The score ranges from 2 to 250 and is
available only when importance, urgency and commitment are all set. Missing
ratings produce `null`, displayed as `NA`; demand and completion do not affect
the score. Squaring urgency gives it greater weight, while importance and
commitment contribute equally within the sum.

##### High-level design

`Activity` owns the score formula so every view uses one definition. `ActivityList`
owns ordering and filtering. `ListPriorityCommand`, `OverdueCommand` and
`NextCommand` select views, while `ActivityListFormatter` renders their results.
`Parser` supplies the session's injected `Clock` to the time-query commands.

![Class diagram for priority, overdue and next views](images/asyraf/PriorityAndTimeViewsClassDiagram.png)

Source: [PriorityAndTimeViewsClassDiagram.puml](diagrams/asyraf/PriorityAndTimeViewsClassDiagram.puml).

##### Implementation and sequence diagram

1. `ListDeleteCommandParser` recognises `list priority`; `Parser` recognises
   `overdue` and `next` and creates their commands with the session's clock.
2. `OverdueCommand` and `NextCommand` capture `LocalDateTime.now(clock)` once per
   execution, so filtering, labels and logging use the same instant.
3. `getPriorityView()` sorts available scores in descending order, with missing
   scores last. Ties use date and start time, then retain insertion order.
   Completed activities remain included.
4. `getOverdueView(now)` keeps incomplete activities whose start is strictly
   before `now`, in chronological order. `getNext(now)` returns the earliest
   incomplete start strictly after `now`, with equal starts retaining add order.
5. Each command also obtains `getCanonicalView()`. The formatter maps selected
   activities to their indexes in that full view instead of renumbering them.
   Priority rows show scores and ratings; overdue rows show time labels.
6. Each command returns a `CommandResult` with the activity-change flag unset,
   so `ConsoleHelper` displays its message without calling `Storage.save()`.

The sequence follows a successful `overdue` query from input to output. `next`
uses the same clock and index flow; `list priority` omits the time boundary.

![Sequence diagram for an overdue query with chronological indexes and no save](images/asyraf/PriorityAndTimeViewsSequence.png)

Source: [PriorityAndTimeViewsSequence.puml](diagrams/asyraf/PriorityAndTimeViewsSequence.puml).

##### Time-boundary rules

Production uses `Clock.systemDefaultZone()`. Comparisons are strict: an activity
starting exactly at `now` appears in neither `overdue` nor `next`. An activity
that has started but not yet ended is overdue because the boundary uses its start.

| Label or view | Rule for an incomplete activity |
| --- | --- |
| `OVERDUE TODAY` | Start date equals today and start is before now. |
| `OVERDUE WEEK` | Start date is before today but not before the current week's Monday. |
| `OVERDUE EARLIER` | Start date is before the current week's Monday. |
| `next` | Earliest start strictly after now. |

The formatter calculates Monday with `TemporalAdjusters.previousOrSame(MONDAY)`,
which also handles week and year rollovers.

##### Design considerations

Keeping completion out of scoring allows completed and incomplete activities to
appear together in priority order. Marking an activity changes its eligibility
for time queries without changing its score.

Using `Comparator.nullsLast(Comparator.reverseOrder())` puts missing scores after
all available scores. Canonical tie-breaking and stable sorting make results
predictable. Read-only views preserve backing-list order, while full chronological
indexes let users act on a result without translating a filtered-view index.
Adding, deleting or rescheduling activities can change those indexes.

Injecting `Clock` allows command and integration tests to use `Clock.fixed()`
with an explicit zone. Passing the captured `now` into model methods keeps them
independent of the system clock and makes exact-boundary cases repeatable.

##### Error handling

The parsers reject extra arguments after `list priority`, `overdue` and `next`.
The model rejects a null `now` and asserts priority and overdue ordering.
Commands write `FINE` logs with view sizes and, for time queries, the captured time.

The formatter shows unset ratings as `-` and an unavailable score as `NA`.
Empty priority output includes `[WARNING] No activities yet.`; time queries use
`[WARNING] No overdue activities.` and
`[WARNING] No upcoming incomplete activities.` respectively. Empty results do
not change or save activity data.

<!-- END OWNER: Muhammad Asyraf Bin Abdul Rahim (@officialasyrafrahim) -->

<!-- BEGIN OWNER: Oliver (@Oliver-koh). Add completion, storage/recovery, editing and search documentation here. -->

### Completion, storage, editing and search

Documentation to be completed by Oliver.

<!-- END OWNER: Oliver (@Oliver-koh) -->

<!-- BEGIN OWNER: Lou Yu Luke (@lukelouyu). Maintain optional add ratings, today's high-demand count, facilities, routes and exit documentation here. -->

<a id="luke-ratings-and-accessibility"></a>

### Ratings and accessibility

This section covers optional add ratings, today's high-demand count (US16), facility
reference data and queries, accessible route search, and the exit command.
These features use the shared activity models and command/result flow.

#### Optional ratings integration

`add` accepts optional `imp/`, `urg/`, and `com/` ratings.
`AddCommandParser` parses each rating independently into a nullable `Integer`;
omitting a field leaves it unset. `Activity.validateRating()` accepts `null` or
an integer from 1 to 5, so add, edit and storage use the same validation rule.
The [shared class diagram](diagrams/shared/ActivityCreationClassDiagram.puml)
shows how ratings fit into activity creation. The priority view uses these ratings;
editing and persistence update and preserve them.

<a id="todays-high-demand-count-us16"></a>

#### Today's high-demand count

`demand today` displays the number of HIGH-demand activities
scheduled on the current local date, including completed and already-ended
activities. Other dates, LOW/MEDIUM demand and numeric ratings do not contribute.
A zero count carries `[WARNING]`.

Execution follows the existing command/result path:

1. `Parser` accepts exactly `demand today`, ignoring case and extra whitespace,
   and passes its shared `Clock` to `DemandTodayCommand`. Invalid syntax raises
   `ParseException` with a warning and `Usage: demand today`.
2. Each `execute()` call reads `LocalDate.now(clock)` and calls
   `ActivityList.countHighDemandOn(today)`, so the date stays current after midnight.
3. The model counts matching dates and `DemandLevel.HIGH` in one pass, returning
   a `long` without sorting or changing activities: O(n) time and O(1) extra space.
4. The command returns `High-demand activities scheduled today (YYYY-MM-DD): N`,
   prefixed with `[WARNING]` when N is zero. Both result flags are false, so the
   console continues without saving.

The default clock is `Clock.systemDefaultZone()`; tests inject a clock to control
the instant and zone. The count uses existing activity fields and needs no
saved-file changes.

`DemandTodayCommandTest` covers counting rules, syntax, zero results and local
midnight/New Year transitions. `UniEnableTest` checks the console path and
unchanged file bytes and modification time; `HelpCommandTest` checks the help entry.

#### Facility Accessibility Data (`facilities.txt`)

The UTF-8 [facility dataset](../src/main/resources/facilities.txt) stores nine
facilities: AS1 to AS8 and Central Library (CLB), identified by F01 to F09.
It is bundled with the application and loaded from the classpath, including
when running the packaged JAR.

The facility and connection records were adapted with reference to the
[NUS FASS accessibility map](https://osa.nus.edu.sg/wp-content/uploads/2025/03/250224-FASS-Access-Route-Faculty-Map.pdf),
updated 24 February 2025. UniEnable maintains these text datasets separately;
the source credits are listed in [Acknowledgements](#acknowledgements).

Each pipe-separated facility row has one of two formats:

```text
FACILITY|ID|NAME|DESCRIPTION
FEATURE|FACILITY_ID|TYPE|STATUS|NOTES
```

For `FACILITY`, `NAME` is the short hub code used by `Facility.getName()`, such
as AS1; `DESCRIPTION` supplies the longer building description. The row requires
four fields, although the description may be empty. A `FEATURE` row references
the stable facility ID and records a feature type, a `YES`, `NO`, or `UNKNOWN`
status, and optional notes. The notes field may be omitted or left empty.

##### Facilities covered

All nine facilities record `STEP_FREE_ENTRANCE: YES`. The table summarises their
other recorded features and notes. An omitted feature means no record is present;
it does not imply `NO` or `UNKNOWN`. Full entrance notes remain in the dataset.

| ID | Hub | Dataset description | Other recorded features and notes |
| --- | --- | --- | --- |
| F01 | AS1 | Faculty of Arts and Social Sciences, Block 1 | `LIFT: NO`; floors 4-5 are not accessible. |
| F02 | AS2 | Faculty of Arts and Social Sciences, Block 2 | `LIFT: NO`; building has no lift. |
| F03 | AS3 | Faculty of Arts and Social Sciences, Block 3 | `LIFT: YES`; serves the main floors. |
| F04 | AS4 | Faculty of Arts and Social Sciences, Block 4 | `LIFT: YES` (floors 1-6); `ACCESSIBLE_WASHROOM: YES` (floors 3-6). |
| F05 | AS5 | Faculty of Arts and Social Sciences, Block 5 | `LIFT: YES` (floor 6 is not accessible); `ACCESSIBLE_WASHROOM: YES` (near the lift lobby). |
| F06 | AS6 | Computing Drive link building (School of Computing) | `LIFT: YES` (floors 1, B1, and 6 are not accessible); `ACCESSIBLE_WASHROOM: YES` (upper floor, shared with SoC). |
| F07 | AS7 | The Shaw Foundation Building | `LIFT: YES` (basement B1 is not accessible); `ACCESSIBLE_WASHROOM: YES` (ground floor). |
| F08 | AS8 | Faculty of Arts and Social Sciences, Block 8 | `LIFT: YES` (floors 1-4); `ACCESSIBLE_WASHROOM: YES` (near the lift lobby); `REST_POINT: YES` (PitStop@FASS). |
| F09 | CLB | Central Library | `LIFT: YES` (shared upper floors linked to School of Computing); `ACCESSIBLE_WASHROOM: YES` (4th-floor toilet requires staff card access). |

`FacilityStorage` associates features with facilities by exact ID after reading
all rows, so a feature can precede its facility declaration. It constructs
immutable `Facility` objects containing their `FacilityFeature` lists and skips
unknown facility references with warnings. [Facility lookup](#facility-lookup)
explains how commands query these records.

#### Facility lookup

##### Overview

The `facility` commands list reference facilities, show one hub's recorded
features, or search by feature and accessibility status. They read the
[bundled dataset](#facility-accessibility-data-facilitiestxt) without changing
activities or facility records.

##### High-level design

`FacilityStorage` parses the bundled resource into immutable `Facility` and
`FacilityFeature` objects. `FacilityManager` takes a snapshot of the loaded list
and handles queries without file I/O. `LoadResult<Facility>` carries accepted
records and load warnings.

![Class diagram showing facility loading, query logic, and reference models](images/luke/FacilityClassDiagram.png)

Source: [FacilityClassDiagram.puml](diagrams/luke/FacilityClassDiagram.puml).

##### Implementation and sequence diagram

1. At startup, `ConsoleHelper` loads facilities, displays warnings and passes a
   `FacilityManager` to `Parser` and its `FacilityCommandParser`.
2. The feature parser creates `FacilityListCommand`, `FacilityViewCommand`, or
   `FacilityFindCommand`. For searches, `type/` must precede the optional
   `status/`; the omitted status defaults to `YES`.
3. The manager uses `getFacilities()`, `findFacility()` or `findByFeature(type, status)`.
   Lookup checks IDs before names, ignoring case and surrounding spaces. Searches
   match statuses exactly, exclude absent features from `UNKNOWN`, and return each
   matching facility once in dataset order.
4. `FacilityOutputFormatter` builds responses and adds the accessibility disclaimer,
   using `FacilityDetailsFormatter` for details in lists and single-hub lookups.
   The command returns a `CommandResult` with no activity change, so no save occurs.

The sequence shows `facility find type/LIFT status/NO`, which returns AS1 and AS2
in the bundled dataset.

![Sequence diagram for finding facilities with a recorded feature and status](images/luke/FacilityLookupSequence.png)

Source: [FacilityLookupSequence.puml](diagrams/luke/FacilityLookupSequence.puml).

##### Design considerations

Separating the loader from the manager lets query tests supply small lists
without creating files. Defensive copies keep callers from changing the loaded
collection or a facility's feature list. Explicit `YES`, `NO`, and `UNKNOWN`
values preserve the difference between confirmed absence and unconfirmed data;
treating missing features as `UNKNOWN` would lose that distinction.

##### Error handling

The loader skips malformed rows, duplicate IDs/names, unknown facility references
and invalid enums with line-numbered warnings. For conflicting statuses of the
same feature, it keeps the first valid status. Loading matches tags, enums and
facility IDs exactly; command input ignores case.

A missing or unreadable resource raises `UniEnableException` and uses the
fallback described in [Architecture](#architecture). Invalid command filters
raise `ParseException`. An unknown hub produces a warning with supported names;
a valid search with no matches returns `[WARNING] No matching facilities found.`

#### Accessibility Route Connections (`connections.txt`)

The UTF-8 [connection dataset](../src/main/resources/connections.txt) stores ten
bidirectional links between the nine facilities. Hub names form graph vertices;
links form edges weighted by the [Connection model's](../src/main/java/seedu/unienable/model/Connection.java)
recorded `distanceInMetres` values.

Each pipe-separated row follows this format:

```text
CONNECTION|ID|FROM|TO|DISTANCE|ACCESSIBILITY|TYPE|SHELTER|KNOWN_BARRIER|NOTES
```

| Column | Field | Meaning and validation |
| --- | --- | --- |
| 1 | `CONNECTION` | Required record tag, matched exactly. |
| 2 | `ID` | Unique connection identifier, parsed as a Java `int`. |
| 3 | `FROM` | One endpoint's facility name, such as AS6; must exactly match a loaded facility name. |
| 4 | `TO` | The other endpoint's facility name; must be known and different from `FROM`. Endpoint order does not restrict travel direction. |
| 5 | `DISTANCE` | Positive integer distance in metres, used as the routing weight. |
| 6 | `ACCESSIBILITY` | `YES`, `NO`, or `UNKNOWN`. Only `YES` connections are eligible for route search. |
| 7 | `TYPE` | Traversal category: `PATH`, `RAMP`, `SHELTERED_RAMP`, `LIFT`, or `OTHER`. |
| 8 | `SHELTER` | Independent `YES`, `NO`, or `UNKNOWN` shelter status; `UNKNOWN` means unconfirmed. |
| 9 | `KNOWN_BARRIER` | Optional description of a recorded barrier. |
| 10 | `NOTES` | Optional additional route information. |

`ConnectionStorage` accepts eight to ten fields. Omitted or empty optional
fields become `null`; to supply notes without a barrier, keep an empty ninth
field. Tags and enum values are case-sensitive when loading.

For example, this actual record describes a bidirectional, 90-metre accessible
path between AS6 and AS1, recorded as sheltered, with no barrier text:

```text
CONNECTION|3|AS6|AS1|90|YES|PATH|YES||Main covered walkway
```

Connection 7, between **AS1 and AS2**, is a 130-metre `RAMP` with accessibility
`YES` and shelter `NO`. Its note identifies the route via the unsheltered
pick-up/drop-off points (PUDOs) at CP14/Arts Link. Shelter and accessibility are
separate properties, so this connection remains eligible for routing.

At startup, `ConnectionStorage` validates endpoints against facilities already
loaded from `facilities.txt`. [Accessible route search](#accessible-route-search)
explains how the graph is built and how routes are found and displayed.

##### Connectivity graph

This schematic connectivity graph shows the ten recorded connections; it is
not a geographical map.

![FASS accessibility connection graph](images/luke/fass-accessibility-network.svg)

Source: [fass-accessibility-network.puml](diagrams/luke/fass-accessibility-network.puml).
Regenerate its SVG when the source changes.

#### Accessible route search

##### Overview

`route from/START to/END` finds a shortest route by recorded distance between
two hub names. Connections are bidirectional, and only those marked `YES` for
accessibility can be traversed. The result includes ordered stops, segment
details, total metres, and the sample-data disclaimer.

##### High-level design

`ConnectionManager` keeps a connection snapshot and constructs one reusable
`DijkstraShortestPath` graph. Each query creates a fresh package-private
`DijkstraSolver` to track distances and predecessors. The nested
`DijkstraShortestPath.Route` record copies its stop and segment lists to protect
the result from later changes.

![Class diagram showing route commands, the reusable graph, per-query solver, and route result](images/luke/RoutingClassDiagram.png)

Source: [RoutingClassDiagram.puml](diagrams/luke/RoutingClassDiagram.puml).

##### Implementation and sequence diagram

1. `ConsoleHelper` loads facilities before connections so `ConnectionStorage`
   can validate endpoint names, then passes valid connections to `ConnectionManager`.
2. `DijkstraShortestPath` assigns a vertex index to each distinct, normalised
   endpoint name. Endpoints from `NO` or `UNKNOWN` connections are still known
   vertices. It then adds two adjacency entries for each `YES` connection, using
   its positive distance and retaining the original `Connection` object.
3. `Parser` delegates route input to `RouteParser`, which requires `from/` before
   `to/` and creates a `RouteCommand`. Executing the command calls
   `ConnectionManager.findRoute()`, which delegates to the reusable graph.
4. The graph resolves names ignoring case and surrounding spaces and creates a
   fresh solver. `dijkstra()` selects the nearest unsettled vertex with a priority
   queue and updates neighbours only for shorter distances, recording predecessor
   vertices and connections. Totals use `long` to exceed individual segments' `int` range.
5. An unreachable destination retains `Long.MAX_VALUE`, producing `Optional.empty()`.
   Otherwise, the graph follows predecessors back to the source and reverses the
   stops and connections into a `Route`. `RouteCommand` formats ordered stops,
   segment types, shelter, barriers and notes, adds the disclaimer and returns
   without changing activities or saving.

The sequence shows a successful query between different hubs after parsing.
Endpoints are names in the connection dataset; facility IDs such as F01 are not accepted.

![Sequence diagram showing route lookup, Dijkstra search, and path reconstruction](images/luke/RouteSearchSequence.png)

Source: [RouteSearchSequence.puml](diagrams/luke/RouteSearchSequence.puml).
Minor getter calls and console output are omitted to keep the search flow readable.

##### Design considerations

Positive edge weights make Dijkstra suitable for finding the shortest recorded
distance. Reusing the graph avoids rebuilding adjacency lists; a fresh solver
keeps each query's search state independent. Computing paths from connections
also avoids maintaining a separate route for every hub pair.

The cost is distance alone. Shelter, traversal type, and recorded barriers do
not add routing restrictions or preferences, so a selected route may include
an unsheltered section or a narrow passage. The result describes recorded links
and needs checking against current campus conditions and the user's needs.

##### Error handling

The connection loader skips invalid rows with line-numbered warnings, including
unknown endpoints, self-connections, duplicate IDs, non-positive distances, and
invalid enum values. Missing or unreadable data makes route commands unavailable
without disabling successfully loaded facility queries.

`RouteParser` reports malformed prefixes or missing endpoints through
`ParseException`. Unknown or blank graph endpoints raise `IllegalArgumentException`,
which `RouteCommand` converts to a checked `UniEnableException` with usage text.
Known but disconnected endpoints return a `[WARNING] No confirmed accessible route
recorded` message; this describes the local dataset rather than proving that
no accessible route exists on campus.

For the same known hub at both ends, the graph returns a zero-distance route
with one stop and no segments. `RouteCommand` rejects that result with
`[WARNING] Sorry, the source and destination are the same.`

#### Exiting the application

`ByeCommand.execute()` returns `Goodbye from UniEnable!` with the exit flag
set and the activity-change flag unset. `ConsoleHelper` displays the message,
then `UniEnable` stops the console loop. Exit does not save or retry a failed
save; recovery must finish before the user enters `bye`.

<!-- END OWNER: Lou Yu Luke (@lukelouyu) -->

<!-- SHARED: All members maintain testing guidance and cross-feature validation notes. -->

## Testing and defensive programming

### Automated testing

JUnit Jupiter tests are under `src/test/java/seedu/unienable`. Run `test` for
the test suite, or `check` for tests, Checkstyle, and the configured coverage
checks, using the wrapper commands in [Setting up](#setting-up-and-getting-started).
To focus on one class on Windows, for example:

```text
.\gradlew.bat test --tests "seedu.unienable.parser.AddCommandParserTest"
```

On macOS or Linux, replace `.\gradlew.bat` with `./gradlew`. Test results are
written to `build/reports/tests/test/index.html`; the JaCoCo HTML report is at
`build/reports/jacoco/test/html/index.html` after `check` or `jacocoTestReport`.

| Area | Representative tests and purpose |
| --- | --- |
| Activity input and commands | Parser tests cover valid and rejected syntax. `EditCommandTest` checks partial changes and atomic rejection; `FindCommandTest` checks name matching and chronological indexes. |
| Priority and time views | `ActivityTest`, `ListPriorityCommandTest`, `OverdueCommandTest`, `NextCommandTest` and `ActivityListFormatterTest` cover ratings, completion, ordering, ties, indexes, empty results and time boundaries. |
| Daily demand (US16) | `DemandTodayCommandTest` covers counting rules, syntax and local midnight/New Year transitions; `UniEnableTest` checks loaded data and read-only behaviour. |
| Activity persistence | `StorageTest` covers round trips, legacy data, malformed rows, backups and failed writes. `UniEnableTest` covers sessions, warnings, restarts and saved-byte preservation. |
| Facility reference data | `FacilityStorageTest`, `FacilityManagerTest`, `FacilityCommandParserTest` and facility command tests cover loading, exact status matching, formatting and unavailable data. |
| Routing | `DijkstraSolverTest` checks distances and predecessors. `DijkstraShortestPathTest` checks all 81 hub pairs against a fixed distance matrix and rejects inaccessible shortcuts. `ConnectionManagerTest`, `RouteParserTest` and `RouteCommandTest` cover endpoint errors, disconnected/same-hub routes and output. |

Use `Clock.fixed()` with a non-UTC zone to test local midnight independently of
the instant. `@TempDir` and explicit storage paths protect personal data; loaders
accept `Reader` inputs for testing malformed records without editing resources.

The [build configuration](../build.gradle) requires **100% line and branch
coverage for its selected accessibility classes**. This is a repository rule,
not a measurement of the whole application: the selection excludes the routing
algorithm, route parser, route command, and all activity classes. Coverage does
not replace checking expected results and failure behaviour. CI runs `check` with
Java 25 on Windows, macOS, and Ubuntu, as configured in
[the workflow](../.github/workflows/gradle.yml).

### Defensive programming

Parsers reject malformed command syntax; commands and models validate resulting
values, displayed-index bounds and rating ranges. Recoverable failures use checked
exceptions. Defensive collection copies protect activity views and reference
data. Assertions check internal ordering and deletion invariants; enable them
as described in the setup section. They support debugging rather than input validation.

`Storage` distinguishes invalid records from read failures, using backups for
recovery writes and temporary files for saves. Recoverable errors let the console
continue; failed saves may leave unsaved changes in memory, so follow the
[recovery steps](UserGuide.md#error-handling) before exiting. Reference-data failures
use the [architecture fallback](#architecture). Add and edit
[reject blank or non-ASCII names](UserGuide.md#editing-an-activity-edit) with
`[WARNING]` and usage without changing data; loaders retain their handling of
historical names. Parser tests and `UniEnableTest` check validation, unchanged
file bytes and restarts.

<!-- SHARED: All members maintain product scope and the team responsibility table. -->

## Product scope

### Target users and value proposition

UniEnable combines commitment planning and campus reference queries in a
keyboard-operated terminal application, with activities stored locally.

### Current boundaries

The current build supports dated, same-day activities, optional ratings,
chronological, demand, and priority views, today's high-demand count, overdue and
next queries, activity editing and name search, completion, deletion, persistence,
facility queries, and shortest recorded accessible routes.
Reference information covers the nine bundled hubs; it is sample data without
live campus updates.

The [user stories](#user-stories) distinguish available features from proposals.
One supplied Must-Have story remains unimplemented: today's summary (US10).
Today's high-demand count (US16) is supported by `demand today`. Keyword search
(US03), editing (US05), and manual rescheduling (US09) are supported by `find` and `edit`.
Workbook priority therefore does not imply inclusion in the current build.
The team still needs to confirm release scope for the remaining stories.

### Team contributions

The team supplied the ownership and version allocations below. Each member
maintains their descriptions and diagrams; [portfolio pages](team/README.md)
link their responsibilities, design, code and tests.

| Team member | V1.0 work | V2.0 work |
| --- | --- | --- |
| [Lou Yu Luke (@lukelouyu)](https://github.com/lukelouyu) | Exit: `bye`. [Facility listing, lookup, and feature searches](#facility-lookup): `facility list`, `facility AS4` (or `facility F04`), and `facility find type/LIFT status/NO`. | [Today's high-demand count](#todays-high-demand-count): `demand today`. [Accessible routes](#accessible-route-search): `route from/AS6 to/AS7`. [Optional importance, urgency, and commitment ratings on add](#optional-ratings-integration): `add n/Project d/2026-12-01 s/09:00 e/10:00 imp/5 urg/4 com/3`. |
| [Atharva Rege (@Atharva005)](https://github.com/Atharva005) | Help: `help`. [Adding an activity with name, date, times, and optional demand](#atharva-activity-creation-and-help): `add n/Tutorial d/2026-12-01 s/09:00 e/10:00 dem/HIGH`. | Planned: `view today`, `view tomorrow`, and `view week`. |
| [Muhammad Asyraf Bin Abdul Rahim (@officialasyrafrahim)](https://github.com/officialasyrafrahim) | [Chronological listing, demand ordering, and deletion](#asyraf-activity-views-and-deletion): `list`, `list demand`, and `delete 1`. | [Priority, overdue, and next views](#asyraf-activity-views-and-deletion): `list priority`, `overdue`, and `next`. |
| [Oliver (@Oliver-koh)](https://github.com/Oliver-koh) | [Saving, backups, and recovery](#oliver-completion-storage-editing-and-search), triggered automatically by activity changes; completion commands: `mark 1` and `unmark 1`. | [Activity editing and name search](#oliver-completion-storage-editing-and-search): `edit 1 n/CS2113 tutorial class` and `find tutorial`. |

## User stories

### Core schedule

| ID | Priority | As a/an ... | I can ... | So that ... | Current status |
| --- | --- | --- | --- | --- | --- |
| US01 | Must-Have | student managing multiple commitments | add an activity with a name, date, and time | I can record what I need to attend | Implemented |
| US02 | Must-Have | student planning my routine | list all my activities in chronological order | I can review my commitments and anticipate what comes next | Implemented |
| US03 | Must-Have | student with a long schedule | find activities by keyword | I can locate a specific commitment quickly | Implemented |
| US04 | Must-Have | student whose plans change | delete an activity | obsolete commitments do not clutter my schedule | Implemented |
| US05 | Must-Have | student whose details change | edit an existing activity's name, date, or time | I can correct my plan without deleting and re-adding it | Implemented |
| US06 | Must-Have | student who finds it difficult to track completion | mark and unmark an activity as done | I know what is finished and what remains | Implemented |
| US07 | Nice-to-Have | student balancing responsibilities | assign a category (academic, CCA, daily) to an activity | I can distinguish types of commitments | Not implemented |
| US08 | Nice-to-Have | student who finds timetable entry tedious | add a recurring activity by specifying its day, time, and week numbers | I do not re-enter the same class repeatedly | Not implemented |
| US09 | Nice-to-Have | student whose day changes unexpectedly | reschedule an activity to a new date or time | my plan stays realistic when disruptions occur | Implemented |

### Views and reminders

| ID | Priority | As a/an ... | I can ... | So that ... | Current status |
| --- | --- | --- | --- | --- | --- |
| US10 | Must-Have | student who benefits from predictable routines | view all of today's activities in one summary | I can prepare for the day ahead | Not implemented |
| US11 | Nice-to-Have | student preparing in advance | view tomorrow's activity summary | I can plan before the next morning | Not implemented |
| US12 | Nice-to-Have | student who struggles to track a full timetable | view a week's activities grouped by day | I have an overview of my upcoming week | Not implemented |
| US13 | Nice-to-Have | student who forgets tasks easily | see my overdue and next-upcoming activities highlighted | I know what needs attention first | Implemented |
| US14 | Future | student who finds it difficult to keep track of time | get an alert 10 minutes before an activity starts | I can mentally prepare for the transition | Not implemented |

### Energy and priority

| ID | Priority | As a/an ... | I can ... | So that ... | Current status |
| --- | --- | --- | --- | --- | --- |
| US15 | Must-Have | student with sensory or attention limits | assign a demand level to an activity | I can record how demanding I expect it to be | Implemented |
| US16 | Must-Have | student with sensory limits | see the count of high-demand activities scheduled today | I can anticipate a potentially stressful day | Implemented (Luke: `demand today`) |
| US17 | Nice-to-Have | student managing my energy | set importance, commitment, and urgency levels on an activity | I can capture how much each one matters | Implemented |
| US18 | Nice-to-Have | student unsure what to do first | view my activities, including completed ones, ordered by combined priority score | I can decide what to tackle next | Implemented |
| US19 | Nice-to-Have | student aware of my workload | view the commitment level of a specific activity | I can decide whether to take it on | Implemented |
| US20 | Nice-to-Have | student aware of my sensory limits | get a warning when the total demand of a day exceeds my personal daily limit | I can avoid overloading myself | Not implemented |
| US21 | Future | student with a short gap between classes | see pending activities whose estimated duration fits a given gap | I can use the available time realistically | Not implemented |
| US22 | Future | student managing my energy | get reminders to take a short break after a sustained work interval | I keep my energy up instead of burning out | Not implemented |

### Accessible navigation

| ID | Priority | As a/an ... | I can ... | So that ... | Current status |
| --- | --- | --- | --- | --- | --- |
| US23 | Must-Have | student with mobility constraints | list accessible facilities at a supported hub | I can identify useful facilities before travelling there | Implemented |
| US24 | Future | student who avoids stairs | get textual directions to a lift at a supported hub | I can move between levels more easily | Not implemented |
| US25 | Future | student using a mobility aid | get textual directions to a ramp at a supported hub | I can avoid step-only entrances | Not implemented |
| US26 | Must-Have | student navigating an unfamiliar campus | get the shortest recorded accessible route between two supported hubs | I can follow a suitable path without interpreting a map | Implemented |
| US27 | Nice-to-Have | student assessing a stored route | see which lifts, ramps, or accessible entrances occur along it | I can judge whether the route suits my needs | Partial |
| US28 | Future | student whose mobility needs affect travel time | record my own travel duration between two locations | my planning reflects the journey I can actually make | Not implemented |
| US29 | Future | student overwhelmed by ambient noise | view which supported locations are flagged as high-noise | I can avoid visiting them | Not implemented |
| US30 | Future | student overwhelmed by ambient noise | request a route between hubs that avoids locations flagged as high-noise | my emotional wellbeing is protected while travelling | Not implemented |

### Foundation

| ID | Priority | As a/an ... | I can ... | So that ... | Current status |
| --- | --- | --- | --- | --- | --- |
| US31 | Must-Have | returning user | have my activities saved automatically and reloaded when I reopen the app | I can continue from my saved schedule between sessions | Implemented |
| US32 | Future | student estimating my time | record an estimated duration on an activity | gap-fitting and planning features have data to work with | Not implemented |
| US33 | Must-Have | new user | see a list of available commands and their formats | I can learn to use the product without external documentation | Implemented |

## Non-functional requirements

These constraints describe the current design and how to check it. They do not
claim that every supported platform or failure condition has been tested manually.

| ID | Requirement | Verification and limits |
| --- | --- | --- |
| NFR01 | Build and run with Java 25 using the included Gradle wrapper. | Compile and run the JAR; the CI matrix checks Windows, macOS, and Ubuntu. |
| NFR02 | Make supported operations available through text commands, with built-in help and recoverable input errors. | Exercise `help`, invalid inputs, and a subsequent valid command. |
| NFR03 | Preserve all activity fields across successful saves and restarts. | Compare stored fields and reloaded state; exercise six-field migration and nine-field round trips. A failed save requires recovery. |
| NFR04 | Report malformed stored rows and protect the original file before a recovery save replaces it. | Check line-numbered warnings, backup bytes, and failed-backup behaviour. The normal-move fallback is not atomic. |
| NFR05 | Query bundled reference data without network access during normal operation. | Test facilities and routes with networking disabled after building. Initial Gradle dependency downloads need internet access. |
| NFR06 | Keep changes maintainable through automated tests, Checkstyle, and reproducible diagram sources. | Run `check`; update related tests and regenerate each changed diagram from its `.puml` source. |

Response-time targets, maximum activity counts, and compatibility with specific
assistive technologies have not been agreed or measured. Set concrete targets
with the team before making those guarantees.

<!-- SHARED: All members maintain terminology used throughout the guides. -->

## Glossary

| Term | Meaning in UniEnable |
| --- | --- |
| Activity | A named commitment with a date, start/end times, demand, completion state, and optional ratings. |
| Demand | The activity's `LOW`, `MEDIUM`, or `HIGH` level; separate from its numeric ratings. |
| Ratings | Independent importance, urgency, and commitment values from 1 to 5. An omitted value is unset, stored as `-`. |
| Priority score | `URG * URG * (IMP + COM)`, available only when all three ratings are set. |
| Canonical view | Activities sorted by date and start time, retaining insertion order for ties. |
| Displayed index | A one-based position in chronological `list`, used by edit, delete, mark, and unmark and retained by find, priority, overdue, and next. Demand output numbers its own rows. |
| Hub | A supported facility name: AS1 to AS8 or CLB in the bundled dataset. |
| Facility ID | A stable lookup identifier, F01 to F09. Route endpoints use hub names instead. |
| Connection | A bidirectional recorded link with distance, accessibility, traversal type, shelter, and optional barrier information or notes. |
| Accessibility status | A recorded `YES`, `NO`, or `UNKNOWN` value. A missing feature is distinct from an explicit `UNKNOWN`. |
| Route | Ordered stops and their connecting segments, with total recorded distance. |
| Recovery backup | The original activity-file bytes copied to `.corrupt.bak` before a save following detected corruption; not a backup history. |

<!-- SHARED: All members maintain manual regression scenarios and expected results. -->

## Instructions for manual testing

### Prepare a separate test folder

1. Use Java 25 and the [setup commands](#setting-up-and-getting-started) to run
   `check` and build `shadowJar`.
2. Copy `build/libs/unienable.jar` into a new, empty test folder. Open a terminal
   there and run `java -ea -jar unienable.jar`. Its `data/activities.txt` will
   belong to this test folder. Keep the folder for restart checks.
3. Enter commands one at a time. Expected results below omit console borders.
   Examples use 2026 dates; for future-date scenarios, choose dates and times
   later than the computer's current local time.

### Activities and restart persistence

Run these steps in order, starting with no activity file.

| Step | Input or action | Expected result |
| --- | --- | --- |
| 1 | `help`, then `list` | Help lists supported commands; the list is empty. |
| 2 | `add n/CCA EXCO meeting d/2026-12-02 s/14:00 e/15:00 dem/HIGH imp/5 urg/4 com/3` | CCA EXCO meeting is added with all three ratings and a count of 1. |
| 3 | `add n/CS2113 tutorial class d/2026-12-01 s/09:00 e/10:00 imp/2` | CS2113 tutorial class is added with default MEDIUM demand, importance 2, and unset urgency/commitment; count is 2. |
| 4 | `list`, then `list demand` | Chronological order is CS2113 tutorial class, CCA EXCO meeting; demand order is CCA EXCO meeting, CS2113 tutorial class. Both are incomplete. |
| 5 | `mark 1`, then `mark 1` again | CS2113 tutorial class becomes done even though demand order was shown last. The second command reports it is already done. |
| 6 | `bye`, relaunch from the same folder, then `list` | The app exits cleanly; CS2113 tutorial class reloads as `[X]` and CCA EXCO meeting as `[ ]`. |
| 7 | `unmark 1`, then `list` | CS2113 tutorial class is incomplete again. |
| 8 | `delete 1`, then `list` | CS2113 tutorial class is removed; CCA EXCO meeting is now chronological index 1. |
| 9 | `list priority` | The remaining CCA EXCO meeting is index 1 with `Priority 128 (IMP 5, URG 4, COM 3)`. |
| 10 | `overdue`, then `next` | Before the meeting starts, `[WARNING] No overdue activities.`; `next` shows CCA EXCO meeting at chronological index 1. |
| 11 | `list priority extra`, `overdue extra`, and `next extra` | Each command produces a warning and leaves activities unchanged. |

At step 6, inspect `data/activities.txt`. It should retain insertion order and
all ratings, even though `list` shows chronological order:

```text
CCA EXCO meeting|2026-12-02|14:00|15:00|HIGH|false|5|4|3
CS2113 tutorial class|2026-12-01|09:00|10:00|MEDIUM|true|2|-|-
```

Compare the file contents before and after the repeated mark at step 5 and the
read-only commands below: these commands should not change the saved bytes.

### Priority and strict local-time boundaries

For deterministic time checks, use the fixed-clock integration entry point
`UniEnable.run(input, output, activityFile, clock)` from tests in the
`seedu.unienable` package. Supply `Clock.fixed()` with `ZoneId.of("Asia/Singapore")`
and disposable storage. Do not change the computer's clock or personal activity file.

Use these rows in the disposable `data/activities.txt` before starting the app:

```text
Planning meeting|2026-11-21|10:00|11:00|MEDIUM|false|3|2|4
Team stand-up|2026-11-22|09:00|09:30|HIGH|false|5|4|-
Submit project report|2026-11-22|14:00|15:00|HIGH|false|4|5|4
Dentist appointment|2026-11-24|15:00|16:00|LOW|false|4|3|2
```

At 2026-11-22 12:00 local time, priority indexes are 3, 4, 1, 2 with scores
200, 54, 28, NA. Overdue returns index 1 as `OVERDUE WEEK` and index 2 as
`OVERDUE TODAY`; next selects index 3. Marking
index 3 removes it from next but leaves it in priority at score 200.

For one incomplete activity on the same date at 09:00-09:30, check these clocks:

| Local now | Overdue | Next |
| --- | --- | --- |
| 08:59 | Empty | That activity. |
| Exactly 09:00 | Empty | Empty. |
| 09:15 | That activity with `OVERDUE TODAY`. | Empty. |

Repeat with the activity completed: both time queries are empty. Check
Sunday/Monday, December/January and local-midnight boundaries with a non-UTC clock.
Queries must preserve file bytes and modification times. `ActivityTest` and
`ListPriorityCommandTest` cover score bounds 2/250, missing ratings, demand and
completion combinations, and stable ties.

### Daily high-demand count

Use a disposable test folder and activity file. Reuse the fixed-clock integration
entry point above with `Asia/Singapore`, initially at 2026-10-22 12:00 local time.
Preload these rows before launching so already-ended activities can be checked:

```text
Yesterday high|2026-10-21|09:00|10:00|HIGH|false|-|-|-
Today done|2026-10-22|08:00|09:00|HIGH|true|-|-|-
Today later|2026-10-22|23:00|23:30|HIGH|false|-|-|-
Today medium|2026-10-22|12:00|13:00|MEDIUM|false|5|5|5
Today low|2026-10-22|14:00|15:00|LOW|false|-|-|-
Tomorrow high|2026-10-23|09:00|10:00|HIGH|false|-|-|-
```

| Input or action | Expected result |
| --- | --- |
| `demand today` | `High-demand activities scheduled today (2026-10-22): 2`. |
| `DEMAND TODAY` or extra whitespace between the words | The same date and count. |
| `mark 5`, then `demand today` | Count remains 2 even though Today later is completed. |
| `unmark 2`, then `demand today` | Count remains 2; completion does not affect scheduled demand. |
| `edit 5 dem/LOW`, then `demand today` | Count becomes 1, using the current activity data. |
| `demand`, `demand tomorrow`, or `demand today extra` | Warning ending with `Usage: demand today`; the next valid command still works. |
| Run with the local clock just after midnight on 2026-10-23 | Date is 2026-10-23 and count is 1, while the UTC date is still 2026-10-22. |
| Run with the local clock on 2026-10-24, or with an empty file | Count is 0 for the displayed date, with `[WARNING]` before the count message. |

Compare file bytes and modification times immediately before and after the
query itself: neither changes. Mark, unmark and edit are separate saving actions.
For midnight and December/January boundaries, the automated test advances an
injected clock and executes the same command again; no computer clock changes
are needed.

### Invalid activity and command input

After the walkthrough, try each input below. Each should produce a warning,
leave CCA EXCO meeting unchanged in memory and on disk, and allow the next command.

| Input | Expected reason for rejection |
| --- | --- |
| `add n/Bad d/2026-12-01 s/09:00` | Missing end time. |
| `add n/Bad d/2026-12-01 s/09:00 e/10:00 imp/6` | Rating outside 1 to 5. |
| `add n/Bad d/2026-02-30 s/09:00 e/10:00` | Invalid calendar date. |
| `add n/Bad d/2026-12-01 s/10:00 e/09:00` | End time does not follow start time. |
| `add n/Ended d/2000-01-01 s/09:00 e/10:00` | Activity has already ended. |
| `delete 0` and `mark 99` | Index outside the current chronological list. |
| `edit 1` or `edit 1 n/` | At least one field is required; a supplied name cannot be empty. |
| `edit 1 s/16:00` | Existing end 15:00 is not after the new start. |
| `edit 1 com/6` | Rating outside 1 to 5. |
| `edit 1 n/One n/Two` | Duplicate name prefix. |
| `find` | Missing non-blank name keyword. |
| `help extra` | Help accepts no arguments. |

### Editing, searching and updated empty-state messages

Use another empty test folder so this sequence does not change the earlier
walkthrough's activity. Enter each command separately. Compare saved bytes
before and after searches and rejected edits; they must remain unchanged.

| Step | Input or action | Expected result |
| --- | --- | --- |
| 1 | `add n/CCA EXCO meeting d/2026-12-02 s/14:00 e/15:00 dem/HIGH imp/5 urg/4 com/3` | Meeting is chronological index 1, initially incomplete. |
| 2 | `add n/CS2113 tutorial class d/2026-12-01 s/09:00 e/10:00 imp/2` | Tutorial becomes index 1; meeting becomes index 2. |
| 3 | `mark 2`, then `find eXcO` | One matching activity, numbered 2, with the meeting marked `[X]`. |
| 4 | `edit 2 n/CCA EXCO planning meeting com/4` | Only name and commitment change. Date, times, HIGH demand, importance 5, urgency 4 and completion remain unchanged. |
| 5 | `edit 1 d/2026-12-03 s/11:00 e/12:00` | Tutorial moves after the meeting; refreshed chronological indexes are meeting 1 and tutorial 2. |
| 6 | `find tutorial`, then `edit 2 imp/5` | Find retains tutorial index 2. Only its importance changes; its demand stays MEDIUM, completion stays incomplete and urgency/commitment stay unset. |
| 7 | `bye`, relaunch from the same folder, then `list` | Meeting reloads as index 1 and `[X]`; tutorial reloads as index 2 and `[ ]`, with the edited dates and names. The file retains all edited ratings. |
| 8 | `edit 2 s/13:00`, then `list` | Warning: existing end 12:00 is before the proposed start. Tutorial stays at 11:00–12:00 in memory and in the file. |
| 9 | `find exam` | `[WARNING] No activities matched keyword "exam".`; no saved-file changes. |
| 10 | `delete 2` | Tutorial is removed; confirmation ends with `Activities in this session: 1`. |
| 11 | `delete 1`, then `list` | Final meeting is removed; confirmation ends with `There is no activity recorded`. List shows `[WARNING] No activities yet.`. |
| 12 | `delete 1`, then `find tutorial` | Empty-list deletion warning, followed by the `[WARNING]` no-match search message. |
| 13 | `bye`, relaunch, then `list` | Empty activity file reloads successfully with no remaining activities. |

In a disposable test folder, also try `add` and `edit` with a name containing only
U+2003 EM SPACE: both must show the blank-name warning. Chinese, accented or emoji
names must show the English-only warning. Confirm that rejected edits leave the
original activity and saved file unchanged, including after restarting.

### Facility queries and routes

Use the bundled resources without editing them. These commands should leave the
activity file unchanged. Every successful facility or route response includes
the sample-data disclaimer.

| Input | Expected result |
| --- | --- |
| `facility list` | Nine facilities, F01 to F09, with recorded features and notes. |
| `facility F04` | AS4 details, including LIFT and ACCESSIBLE_WASHROOM marked YES. |
| `facility find type/LIFT status/NO` | AS1 and AS2. |
| `facility find type/REST_POINT` | AS8; omitted status defaults to YES. |
| `facility find type/RAMP status/UNKNOWN` | No matches; absent ramp records do not count as UNKNOWN. |
| `facility XYZ` | Unknown-facility warning with supported names. |
| `facility find type/LIFT status/MAYBE` | Invalid-status warning. |
| `route from/as6 to/as7` | AS6 -> AS1 -> AS4 -> AS7, total 200 metres, with segment details and the recorded narrow passage. |
| `route from/as7 to/as6` | The reverse stop order, still 200 metres. |
| `route from/AS6 to/AS6` | `[WARNING] Sorry, the source and destination are the same.` |
| `route from/F06 to/AS7` | Unknown-endpoint warning: facility IDs are not route endpoints. |
| `route to/AS7 from/AS6` | Invalid-command warning because the prefixes are reversed. |

The bundled graph connects all nine hubs through confirmed accessible links.
Disconnected graphs and unavailable resources are covered by automated fixtures;
do not infer a no-route case from this connected dataset.

### Legacy data, corruption warnings, and save failures

Use a fresh test folder with a copy of the JAR for these cases. With the app
closed, create `data/activities.txt` in UTF-8 containing exactly these two rows:

```text
Legacy|2000-01-01|09:00|10:00|LOW|true
Broken row
```

1. Launch the app. Expect a warning for line 2; `list` shows Legacy as completed
   despite its past date. Loading alone leaves the file unchanged.
2. Enter `add n/Recovery d/2026-12-01 s/10:00 e/11:00`. Expect a successful save.
   `data/activities.txt.corrupt.bak` must match the original two-row file byte for
   byte, including the malformed row. The replacement activity file has two
   valid nine-field rows, with unset ratings represented by `-`.
3. Exit and relaunch. Both activities should load without the malformed-row
   warning. Keep a separate copy of the backup before testing further corruption.

For a failed-backup case, start from a fresh copy of the two-row fixture and
create a **non-empty directory** named `data/activities.txt.corrupt.bak` before
launching. Adding Recovery should report a save failure and preserve the original
activity file. The added activity remains in memory; follow the
[recovery steps](UserGuide.md#error-handling) before exiting.

## Acknowledgements

UniEnable's facility and connection datasets were compiled with reference to
the [NUS FASS *Access Routes / Faculty Map*](https://osa.nus.edu.sg/wp-content/uploads/2025/03/250224-FASS-Access-Route-Faculty-Map.pdf),
updated 24 February 2025. The map is an effort led by the Student Accessibility
Unit (SAU), Office of Student Affairs, National University of Singapore.

The PlantUML connectivity graph is UniEnable's own schematic representation of
its text datasets. The linked NUS PDF provides the original campus map.
