# Compact accessibility regression suite

Run `gradlew.bat clean check --console=plain` with Java 25.
The normal check runs JUnit, Checkstyle, and a JaCoCo gate requiring 100% LINE and BRANCH coverage.
Open `build/reports/tests/test/index.html` for named regression failures and
`build/reports/jacoco/test/html/index.html` for coverage.

## Scope and team boundaries

Coverage includes facility commands, FacilityCommandParser, facility/connection managers and models,
AccessibilityStatus/ShelterStatus/TraversalType, reference loaders (including nested builders),
LoadResult, and accessibility formatters/disclaimer. These are listed explicitly in build.gradle.
Branch 1-3 activity code, Parser.java, ConsoleHelper, and the general console are outside the coverage gate.
They are still exercised by the command regressions. No production class within the agreed
facility/connection scope is excluded to inflate the result.
There is no route command or Dijkstra implementation yet; this suite protects its data foundations.

Parser.java and all Branch 1-3 production files and reserved test placeholders are unchanged.
The 11 empty placeholders remain so this cleanup does not remove team-owned files.
Public facility/connection APIs and bundled datasets are unchanged.
The two package-private owned-reader helpers let tests simulate close failures without mocking;
they retain the existing closing and checked-exception behavior.

## Replacement coverage map

| Removed test groups | Replacement checks |
| --- | --- |
| UniEnableTest and UniEnableFacilityFind/List/ViewTest | ConsoleRegressionTest: independent commands, complete session, warning recovery, exit, exact welcome formatting |
| FacilityFind/List/ViewCommandTest | ConsoleRegressionTest: no persistence, unchanged activities, non-exiting results, disclaimer, unknown facilities; ModelRegressionTest: optional-text formatting |
| FacilityManagerTest and facility portions of FacilityConnectionEdgeCasesTest | ManagerRegressionTest: IDs before names, case/space handling, missing IDs, default YES, explicit UNKNOWN, duplicate-feature deduplication, null arguments |
| ConnectionManagerTest and connection portions of FacilityConnectionEdgeCasesTest | ManagerRegressionTest plus filters.tsv: either endpoint, both orientations, AND filters, empty/blank filters, immutability, snapshot ownership |
| FacilityConnectionIntegrationTest | StorageRegressionTest: packaged datasets, valid endpoints, distances, restrictions; console cases verify bundled facility queries |
| FacilityStorageTest, FacilityStorageDuplicateIdTest, FacilityStorageIdentifierTest | storage.tsv: feature-before-facility ordering, optional text, duplicates, Unicode identity, malformed records, warnings; loader I/O tests |
| ConnectionStorageTest | storage.tsv: all status/type/shelter combinations, 8/9/10 fields, preserved endpoints/distances/barriers/notes, invalid numbers, nonpositive distances, unknown endpoints, self edges, duplicate IDs, physical warning lines; snapshot and I/O tests |
| FacilityTest and LoadResultTest | ModelRegressionTest: defensive copies and immutable feature/record/warning lists |
| ConnectionTest, FacilityFeatureTest, FacilityConnectionEnumsTest | StorageRegressionTest serializes all fields and checks actual enum combinations instead of isolated getter/constant tests |
| UiTest | ConsoleRegressionTest: exact welcome/borders and multiline messages during command sessions |

Redundant getter/enum assertions and repeated fixture construction are removed.
Representative exact formatting, read-only behavior, routing fields, and input-boundary cases remain.
100% coverage measures execution; it does not prove correctness of every input or a future route algorithm.

## Editing regression cases

Files under src/test/resources/regression are UTF-8 TSV files.
Each physical line is one case; lines starting with # are comments.
Columns are separated by actual tabs. Use a single - for an empty column, including trailing columns.
Literal backslash-n represents a newline inside a value. Tabs in values are not supported.

- console.tsv: case ID, input, expected output fragment, forbidden output fragment.
  Each row is an independent JUnit dynamic test followed by bye.
- session.tsv: the user's exact 27-command sequence and expected fragments.
  A single session also asserts five successful additions and fifteen warnings.
- filters.tsv: case ID, from, to, traversal type, accessibility, shelter, expected comma-separated IDs.
  A - filter means omitted.
- storage.tsv: case ID, F or C, dataset text, record count, warning count,
  complete serialized records, expected warning fragment.
  Records verify all routing fields; invalid rows retain valid neighbors.

Every Java test file and every resource file stays below 500 lines.
The total Java budget is approximately 500 lines, including shared support and untouched placeholders.
Do not collapse unrelated cases into an unlabelled loop solely to reduce line counts.
