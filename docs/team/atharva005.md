<!-- @@author Atharva005 -->

# Atharva Rege - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information.

### Summary of contributions

I implemented activity creation, daily/weekly calendar dashboards, and the initial help command. My work covered input parsing, activity insertion, schedule summaries, related tests, and feature documentation and diagrams.

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Atharva005&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Activity creation:** Implemented `add` and its named-field parser so students can record multiword activities without remembering a fixed field order ([PR #11](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/11)). Corrected date/time validation: at 15:00 today, an activity ending at 10:00 tomorrow must remain valid ([PR #23](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/23)). This requires comparing the date and time together. Optional ratings and shared clash/name validation were later team extensions.
- **Calendar dashboards:** Implemented `view today`, `view tomorrow`, and `view week` so students can inspect commitments and workload together ([PR #47](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/47)). Combined date filtering, daily grouping, completion summaries, and rating charts without changing stored activities. An injectable clock makes date selection reproducible; later team changes refined demand counts, formatting, and displayed indexes.
- **Command discovery:** Added the initial help command so users can discover supported commands inside the application ([PR #21](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/21)). This established the help entry point subsequently expanded by the team into categorized and topic-specific guidance.

#### Testing contributions

Added JUnit tests for successful activity creation, parser field handling, invalid inputs, and help output in [PR #21](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/21). These exercise both parsing and command execution, helping distinguish malformed input from insertion failures. The current [add tests](https://github.com/AY2627S1-CS2113-T10-4/tp/blob/master/src/test/java/seedu/unienable/command/activity/AddCommandTest.java) also contain later team regressions.

#### Documentation contributions

- **User Guide:** [Adding activities](../UserGuide.md#adding-an-activity-add), [calendar dashboards](../UserGuide.md#viewing-calendar-dashboards-view), and [command help](../UserGuide.md#viewing-command-help-help), including formats, examples, validation rules, and empty-period behavior.
- **Developer Guide:** [Adding activities](../DeveloperGuide.md#adding-activities), [calendar dashboards](../DeveloperGuide.md#calendar-dashboards), and [categorized/topic help](../DeveloperGuide.md#categorized-and-topic-help), covering the parser-command-model flow and read-only views.
- **UML diagrams:** Contributed the add sequence, three class/sequence pairs for calendar dashboards, topic help, and activity-schedule validation, and an illustrative activity-state object diagram. These eight sources and rendered images are listed in [my diagram index](../diagrams/README.md#atharva). The activity-creation class diagram and name/parameter validation pairs are credited to Luke.

#### Team-based tasks

Connected `add` parsing to activity insertion and registered calendar commands in the shared dispatcher ([PR #11](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/11), [PR #47](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/47)). Updated my diagram assets in [PR #58](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/58) and corrected them in [PR #71](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/71), keeping feature explanations aligned with the integrated application.

<!-- @@author -->
