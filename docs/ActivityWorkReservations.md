# Activity work reservations

These files reserve ownership only. Empty classes contain no implementation.
The command placeholders are not wired into the parser. Empty test classes add no tests.
No Git branches or runtime data files are created.

Write your name in the `TODO [Branch N]: Owner` markers.
Replace only your numbered TODOs with implementation and tests.

## Branch 1: Add Activity + Help

Owner: __________

Owned files under `src/main/java/seedu/unienable/`:

- `command/activity/AddCommand.java`
- `command/HelpCommand.java`
- `parser/AddCommandParser.java`

Matching test placeholders are under `src/test/java/seedu/unienable/`.

Implement add end-to-end using n/, d/, s/, e/, and dem/, activity validation and insertion.
Implement help using the current User Guide, including the implemented Facility commands.
Do not advertise planned commands as available before integration.

## Branch 2: List + List Demand + Delete

Owner: __________

Owned files under `src/main/java/seedu/unienable/`:

- `command/activity/ListCommand.java`
- `command/activity/ListDemandCommand.java`
- `command/activity/DeleteCommand.java`
- `parser/ListDeleteCommandParser.java`

Matching test placeholders are under `src/test/java/seedu/unienable/`.

Implement list, list demand, and delete with agreed ordering, displayed indexes and deletion behaviour.
Agree on which displayed order indexes refer to with Branch 3 before implementing index commands.

## Branch 3: Mark + Unmark + Persistence

Owner: __________

Owned files under `src/main/java/seedu/unienable/`:

- `command/activity/MarkCommand.java`
- `command/activity/UnmarkCommand.java`
- `parser/MarkCommandParser.java`
- `storage/Storage.java` (existing shared persistence API)

Matching test placeholders are under `src/test/java/seedu/unienable/`.

Implement mark, unmark, automatic save/load of data/activities.txt, and corrupted-data handling.
Agree on the persistence format and recovery policy. Use temporary files for StorageTest.

## Coordinate the shared files

Separate files reduce overlapping edits. Comments cannot guarantee conflict-free merges.
Choose one team integrator to apply final dispatch changes in Parser.java.
Keep parsing in each branch's own helper, and leave the Facility handlers unchanged.

| Shared file | Numbered TODOs |
| --- | --- |
| parser/Parser.java | Branch 1: add/help; Branch 2: list/delete; Branch 3: mark/unmark dispatch |
| model/Activity.java | Branch 1: validation if needed; Branch 3: completion updates |
| model/ActivityList.java | Branch 1: insertion; Branch 2: ordering/index/deletion; Branch 3: completion updates |
| storage/Storage.java | Branch 3: loading/saving and corrupted-data handling |
| UniEnable.java | Branch 3: startup loading and automatic saving |

Agree on ActivityList APIs before coding in parallel. Activity's isDone field is currently final.
Coordinate model/collection edits together or through the integrator.
Agree on ordering, index meaning, validation and persistence details; the Activity section of
docs/V1_SPEC.md records unresolved decisions. Its Facility section predates the current User Guide.

Use the existing Command.execute(ActivityList, Storage) and CommandResult contracts.
Keep printing in Ui. Preserve the Facility commands, warning recovery, borders and logo.
No feature methods, parser cases, persistence logic or JUnit assertions are supplied here.

## Verification after implementation

Use Java 25. Run focused tests, then gradlew.bat check --no-daemon.
Check that invalid input allows subsequent valid commands.
Keep TEST_AUDIT.md out of commits.
