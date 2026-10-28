# UniEnable systematic review — 28 October 2026

The Java 25 build, regression suite, Checkstyle checks, configured coverage gate,
JAR packaging, and Javadoc generation pass. Concrete behavior and documentation
defects found during this review have been corrected. Changes are uncommitted.

## Verification results

| Check | Result |
| --- | --- |
| Runtime and compiler | JDK 25.0.4 |
| JUnit | 495 cases across 40 test suites; 0 failures, 0 errors, 0 skipped |
| Checkstyle | 0 reported issues in main and test code |
| Java compiler lint | 0 warnings with `-Xlint:all` |
| Configured accessibility coverage gate | Passed its 100% line and branch requirements |
| Whole-application coverage | 1,596/1,650 lines (96.73%); 777/846 branches (91.84%) |
| Executable JAR | Built successfully |
| Javadoc | Generated successfully; 128 completeness warnings remain |
| UG output examples | 13 output blocks matched Java 25 sessions using a fixed clock |
| Documentation navigation | 429 local links/anchors checked across 8 documents |
| Repository links | 10 GitHub link destinations checked against the working tree |
| DG diagrams | All 40 image/source pairs current; ownership and index entries checked |
| Git whitespace checks | Passed |

The final build log is [systematic-check-verified.log](../build/systematic-check-verified.log).
The complete application coverage report is [available here](../build/reports/audit-coverage/html/index.html).
The standard Gradle coverage report continues to cover only the configured
accessibility classes; it must not be interpreted as 100% coverage of the application.

## Corrected findings

| Finding | Effect and correction | Regression evidence |
| --- | --- | --- |
| Invalid saved time ranges were accepted | Six- and nine-field records with equal or reversed start/end times loaded as ordinary activities. Storage now skips them with line-numbered warnings and applies the existing backup protection before a recovery save. Valid historical records still load. | A new storage regression failed before the fix. It checks both row formats, both invalid ranges, valid-row retention, unchanged original bytes before saving, and exact recovery-backup bytes. |
| Delete help contradicted demand-list behavior | `help delete` still told users not to use `list demand` numbering. It now explains that demand views retain chronological indexes and distinguishes calendar dashboard numbering. | A new help regression failed on the old text and passes after correction. Existing demand-view tests verify mark, unmark, edit, and delete select the displayed record. |
| Route help advertised unsupported IDs | `help route` offered facility IDs even though routes accept facility names. It now names AS1–AS8 and CLB and explains that IDs such as F01 are not accepted. | A separate help regression checks the supported endpoints and guards against the old instruction. Existing route tests cover endpoint validation. |
| Switch indentation differed from the current course-linked standard | Case labels used zero additional indentation, enforced by the starter Checkstyle configuration. All seven switches in four source files now indent labels by four spaces, and Checkstyle enforces that setting. | Main/test Checkstyle and the full regression suite pass. Arrow-style switches remain supported by the linked standard. |
| Checked exception classes produced compiler lint warnings | Three exception classes lacked serialization identifiers. Explicit `@Serial` identifiers and constructor documentation remove those warnings; the index exception description now reflects its current use. | Final compilation with `-Xlint:all` produces no warnings. |
| Source links would escape the published documentation folder | DG links to `../src` and the root README would resolve outside a Pages deployment sourced from `/docs`. These now point to the repository's `master` branch on GitHub. | All ten repository destinations and the README anchor exist locally. Publication notes match the link strategy. |
| Documentation details were inconsistent | The guides now describe storage's time-range validation and avoid promising usage text on every invalid add. The persistence class diagram was updated and rendered. The About Us table now uses Koh Li Wei Oliver, consistent with the contribution tables. | Guide navigation checks, example checks, and diagram metadata checks pass. |

## Coding standards and code quality

The current [CS2113 standards page](https://nus-cs2113-ay2627-s1.github.io/website/admin/standardsAndConventions.html)
requires the basic [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/basic.html).
The latter shows both colon and arrow switch forms, with indented case labels.
The review checked switch indentation, terminating branches, defaults and
fall-through, naming, braces, import use, line lengths, and explanatory comments.
No accidental fall-through was found. Exhaustive enum switch expressions retain
their explicit enum alternatives.

Storage now catches the specific date-parsing and invalid-value exceptions
instead of catching every runtime exception while decoding a row. Existing Java
author-marker sequences were preserved in all 14 changed Java files.

Two maintainability opportunities remain; neither caused a failure in this review:

- `EditCommand` has long constructors with paired field values and supplied flags.
  A small immutable edit-fields object would reduce argument-order mistakes.
- Add and edit parsers repeat field collection, date/time parsing, and rating
  parsing. A shared field reader could reduce future validation drift. Such a
  refactor should preserve their different required-field and historical-edit rules.

## JUnit review

The review inspected parser, command, model, storage, formatter, routing, console,
and separate-process tests. Existing coverage includes ordering/index regressions,
strict time boundaries, overlap overrides, duplicate rejection, Unicode output,
80-column rating tables, no-op persistence, corruption backups, and failed-save
recovery. Process tests use disposable directories, bounded waits, and cleanup.

Sixteen cases were added, increasing the suite from 479 to 495. They cover the
storage and help defects plus malformed edit indexes/fields, invalid dates and
times, invalid demand/ratings, and rejected partial edits preserving all existing
fields. Existing mark/unmark parser tests now also exercise integer overflow.
Successful edit-parser tests now assert the parsed command type. Tests that depend
on the current-time boundary use an injected clock; the new storage tests use
JUnit temporary directories.

## Remaining warnings and verification limits

Javadoc reports 128 warnings for missing comments, parameter tags, return tags,
and implicit constructor documentation. These are generated API-documentation
completeness findings, separate from Checkstyle errors. Some tags are optional
under the course standard when their meaning is already clear, but documentation
of the long edit constructors would be especially useful.

This review ran on Windows with Java 25. It did not run the Linux/macOS CI jobs,
deploy GitHub Pages, or confirm that uncommitted changes exist on the remote
`master` branch. Repository URLs were checked against local files. Coverage is
evidence of exercised code, not a guarantee that every possible input is correct.
