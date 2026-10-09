# Accessibility tests matching the source structure

The test packages and class names mirror their corresponding production classes.
The suite retains the existing scenarios, assertions, and local TSV inputs.
The combined regression classes and TestSupport have been replaced by focused test classes;
there are no regression folders.

The four TSV files in src/test/resources are private local inputs, ignored by Git.
Fresh clones and GitHub CI need those files supplied separately before running this suite.
Missing files fail the tests; no tests are skipped and the coverage requirement is unchanged.

Run `gradlew.bat clean check --console=plain` with Java 25 after supplying the local files.
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
The small private fixture-reading routine lives within each class that uses TSV data.
Console stream capture lives only in UniEnableTest. No shared TestSupport class is needed.
No extra tests of trivial getters or enum constants were added solely to match filenames.

Every Java test file and every resource file stays below 500 lines.
Matching individual production classes increases the total Java line count because imports and
small setup routines repeat; the earlier approximate 500-line total is no longer the layout target.
100% coverage measures execution, not correctness of every possible input or a future route algorithm.

## Editing local test cases

Files under src/test/resources are UTF-8 TSV files.
Each physical line is one case; lines starting with # are comments.
Columns use actual tabs. A single - represents an empty column, including trailing columns.
Literal backslash-n represents a newline inside a value. Tabs in values are not supported.

- console.tsv: case ID, input, expected output fragment, forbidden output fragment.
  Each row is an independent JUnit dynamic test followed by bye.
- session.tsv: the user's exact 27-command sequence and expected fragments.
  A single session also asserts five successful additions and fifteen warnings.
- filters.tsv: case ID, from, to, traversal type, accessibility, shelter, expected comma-separated IDs.
  A - filter means omitted.
- storage.tsv: case ID, F or C, dataset text, record count, warning count,
  complete serialized records, expected warning fragment.
  FacilityStorageTest runs F rows; ConnectionStorageTest runs C rows.
  Records verify all routing fields; invalid rows retain valid neighbors.

Keep these TSV files local. The exact four paths are ignored and must not be staged or published.
