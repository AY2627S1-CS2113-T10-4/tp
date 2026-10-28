<!-- @@author Oliver-koh -->

# Koh Li Wei Oliver - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information. My contributions focus on keeping activity state persistent and allowing users to complete, revise, and find existing commitments.

### Summary of contributions

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Oliver-koh&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Completion tracking:** Implemented `mark` and `unmark` with chronological index resolution and completion state that survives restart ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). Commands preserve names, schedules, demand, and ratings. Repeating an already satisfied request produces a warning without unnecessarily saving the activity file.
- **Persistence and recovery:** Developed activity storage and its console integration ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). The current flow supports UTF-8 records, legacy six-field records, independently unset ratings, and line-numbered warnings for malformed rows. Recovery saves preserve the original bytes before replacement, while failed saves keep changes visible in memory and support an explicit `save` retry. This requires coordinating model changes, command-result flags, and file-write failures.
- **Editing and name search:** Implemented `edit` and `find` ([PR #31](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/31)). Edits replace only supplied fields, preserve completion and omitted ratings, and validate the resulting activity before replacement. Search matches a literal case-insensitive name substring and retains full-list chronological indexes. Rejected Unicode-whitespace names before saving to prevent records disappearing after restart ([PR #42](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/42)).

#### Documentation contributions

- **User Guide:** [Data storage](../UserGuide.md#data-storage), [completion commands](../UserGuide.md#changing-completion-mark-and-unmark), [editing](../UserGuide.md#editing-an-activity-edit), [name search](../UserGuide.md#searching-activity-names-find), [saving](../UserGuide.md#saving-changes-save), and persistence/recovery guidance in [error handling](../UserGuide.md#error-handling).
- **Developer Guide:** [Completion, storage, editing, and search](../DeveloperGuide.md#completion-storage-editing-and-search), including partial edits, chronological indexes, conditional saves, record formats, and backup/retry behavior.
- **UML diagrams:** Contributed five class/sequence pairs for editing, name search, marking, unmarking, and persistence ([diagram index](../diagrams/README.md#oliver)). They show how commands use the activity list and how the console decides whether saving is needed.

#### Team-based tasks

Integrated completion and persistence with the shared activity model, parser, and command-result contract so activity-changing commands can request saving consistently ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). Extended editing/search to preserve the team's optional ratings and existing completion state ([PR #31](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/31)). Added storage, completion, parser, and edit/search tests to check restart behavior and ensure rejected changes do not partially update activities.

<!-- @@author -->
