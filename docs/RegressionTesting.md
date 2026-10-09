# Accessibility tests matching the source structure

The test packages and class names mirror their corresponding production classes.
The suite retains the existing scenarios and assertions, with case values stored directly in the test classes.
The combined regression classes and TestSupport have been replaced by focused test classes;
there are no regression folders.

Tests are self-contained and run in fresh clones and GitHub CI without local resource files.
The four TSV files in src/test/resources remain ignored local reference copies.
No tests are skipped and the coverage requirement is unchanged.

Run `gradlew.bat clean check --console=plain` with Java 25.
This runs JUnit, Checkstyle, and a JaCoCo gate requiring 100% LINE and BRANCH coverage.
Open `build/reports/tests/test/index.html` for named test results and
`build/reports/jacoco/test/html/index.html` for coverage.

## Scope and team boundaries

Coverage includes facility commands, FacilityCommandParser, facility/connection managers and models,
AccessibilityStatus/ShelterStatus/TraversalType, reference loaders (including nested builders),
LoadResult, and accessibility formatters/disclaimer. These are listed explicitly in build.gradle.
Branch 1-3 activity code, Parser.java, ConsoleHelper, and the general console are outside the coverage gate.
They are still exercised by the command tests. No production class within the agreed
facility/connection scope is excluded to inflate the result.
There is no route command or Dijkstra implementation yet; this suite protects its data foundations.

This test reorganization leaves all production files, including Parser.java, unchanged.
The 11 reserved Branch 1-3 test placeholders also remain unchanged.
Public facility/connection APIs and bundled datasets are unchanged.
The previously added package-private owned-reader helpers let tests simulate close failures
without mocking and preserve closing and checked-exception behavior.

## Test layout and retained checks

All paths below are under src/test/java/seedu/unienable.

| Test class | Retained checks |
| --- | --- |
| UniEnableTest.java | Independent commands, the user's complete 27-command session, warning recovery, exit, exact welcome formatting |
| parser/FacilityCommandParserTest.java | Unavailable facility data through the console parser, with the same warning assertions |
| command/accessibility/facility/FacilityFindCommandTest.java | Parsed find command, no persistence, unchanged activities, non-exiting result, disclaimer |
| command/accessibility/facility/FacilityListCommandTest.java | Parsed list command, no persistence, unchanged activities, non-exiting result, disclaimer |
| command/accessibility/facility/FacilityViewCommandTest.java | Parsed lookup, read-only behavior, disclaimer, empty features, unknown facilities |
| logic/FacilityManagerTest.java | IDs before names, case/space handling, missing IDs, default YES, explicit UNKNOWN, duplicate features, null arguments, immutable results |
| logic/ConnectionManagerTest.java | Either endpoint, both orientations, AND filters, empty/blank filters, immutability, snapshot ownership |
| model/FacilityTest.java | Defensive copying and immutable feature lists |
| storage/LoadResultTest.java | Defensive copying and immutable record/warning lists |
| storage/FacilityStorageTest.java | Packaged facilities, feature ordering, optional text, duplicates, Unicode identity, malformed records, warning lines, read/close failures |
| storage/ConnectionStorageTest.java | Packaged graph, endpoint/distance/restriction preservation, enum combinations, invalid records, warning lines, snapshot ownership, read/close failures |
| ui/accessibility/FacilityDetailsFormatterTest.java | Omitted null, empty, and blank optional text with exact output assertions |

Tests sharing a previous loop now live in the relevant class; checks remain in both applicable classes.
Private case methods in each relevant test class return the original inputs and expected values.
Console stream capture lives only in UniEnableTest. No shared TestSupport class is needed.
No extra tests of trivial getters or enum constants were added solely to match filenames.

Every Java test file stays below 500 lines.
Matching individual production classes increases the total Java line count because imports and
small setup routines repeat; the earlier approximate 500-line total is no longer the layout target.
100% coverage measures execution, not correctness of every possible input or a future route algorithm.

## Editing test cases

Case methods return lists of string arrays containing the same values as the original local TSV rows:

- UniEnableTest.consoleCases: case ID, input, expected output fragment, forbidden output fragment.
  Each row is an independent JUnit dynamic test followed by bye.
- UniEnableTest.sessionCases: the user's exact 27-command sequence and expected fragments.
  A single session also asserts five successful additions and fifteen warnings.
- ConnectionManagerTest.filterCases: case ID, from, to, traversal type, accessibility, shelter,
  expected comma-separated IDs. Empty strings represent omitted filters.
- FacilityStorageTest.recordCases and ConnectionStorageTest.recordCases: case ID, F or C, dataset text,
  record count, warning count, complete serialized records, expected warning fragment.
  Each loader's test contains only its own rows. All routing fields and malformed-record cases remain.

Empty values are Java empty strings and newlines use Java newline escapes.
Update these case methods when adding or changing an automated regression case.
The ignored TSV files remain optional local references; editing them does not change the automated tests.
Do not stage or publish those local files.

The CI failure at commit 5ca8e93 occurred because the tests required ignored TSV resources.
Keeping the cases inside their corresponding JUnit classes removes that dependency without reducing coverage.
