<!-- @@author Atharva005 -->

# Atharva Rege - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information. My contributions focus on creating activities, presenting calendar summaries, and making commands easier to discover.

### Summary of contributions

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Atharva005&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Activity creation:** Implemented `add` and its named-field parser, allowing fields in any order and multiword names. Validation checks required fields, real dates, same-day time ranges, and whether an activity has already ended before changing the list. Comparing the full end date/time avoids rejecting a future activity merely because its clock time is earlier than now. Evidence: [PR #11](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/11) and [PR #23](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/23).
- **Calendar dashboards:** Added `view today`, `view tomorrow`, and `view week`. These select activities by local date, group weekly schedules by day, and display completion summaries, demand counts, and rating charts. The implementation combines filtering, chronological ordering, and aggregation while leaving stored activities unchanged. Evidence: [PR #47](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/47).
- **Command help and testing:** Added the help command and JUnit tests for help, activity creation, and add parsing. The current help feature provides a categorized menu and command-specific syntax, rules, and examples without executing the described command. Evidence: [PR #21](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/21).

#### Documentation contributions

- **User Guide:** [Adding activities](../UserGuide.md#adding-an-activity-add), [calendar dashboards](../UserGuide.md#viewing-calendar-dashboards-view), and [command help](../UserGuide.md#viewing-command-help-help), including formats, examples, validation rules, and empty-period behavior.
- **Developer Guide:** [Adding activities](../DeveloperGuide.md#adding-activities), [calendar dashboards](../DeveloperGuide.md#calendar-dashboards), and [categorized/topic help](../DeveloperGuide.md#categorized-and-topic-help), covering the parser-command-model flow and read-only views.
- **UML diagrams:** Contributed six class/sequence pairs: activity creation, calendar dashboards, topic help, activity-name validation, activity-parameter validation, and activity-schedule validation. All twelve sources and rendered images are listed in [my diagram index](../diagrams/README.md#atharva). The shared validation helper implementation belongs to Luke's feature area; my contribution here includes its diagrams.

#### Team-based tasks

Integrated add, help, and calendar commands with the team's shared parser and activity list. Added parser and command tests so changes to shared input handling could be checked alongside other members' features ([PR #21](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/21)). Reconciled changes from the shared branch while developing activity creation and calendar views, keeping these features compatible with the team's activity model.

<!-- @@author -->
