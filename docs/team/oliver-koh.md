<!-- @@author Oliver-koh -->

# Koh Li Wei Oliver - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information.

### Summary of contributions

I implemented activity completion, file persistence and malformed-record recovery, partial editing, and name search. I connected activity changes to the console's save flow and contributed restart/command tests, storage and editing documentation, and UML diagrams.

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Oliver-koh&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Completion tracking:** Implemented `mark` and `unmark` so students can track progress across sessions ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). Commands resolve chronological indexes and signal whether state actually changed. Repeating an already satisfied request warns without requesting another save, avoiding unnecessary file writes.
- **Persistence and recovery:** Developed UTF-8 activity storage and console integration so commitments survive restart ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). Malformed rows produce line-numbered warnings while valid records remain usable; a backup preserves the corrupt original before a recovery save. Temporary-file replacement required Windows-specific handling. Optional-rating compatibility and the current failed-save/exit-retry flow were later team extensions.
- **Editing and name search:** Implemented partial `edit` and literal, case-insensitive `find` so students can revise and locate commitments without recreating them ([PR #31](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/31)). A name-only edit preserves the schedule and completion state; an invalid replacement leaves the original activity unchanged. Search retains full-list chronological indexes. Added Unicode-whitespace rejection to prevent edited records disappearing after restart ([PR #42](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/42)).

#### Testing contributions

Added storage, mark/unmark, and parser tests for restart persistence, malformed-row recovery, invalid indexes, and repeated completion requests ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). Added [edit tests](https://github.com/AY2627S1-CS2113-T10-4/tp/blob/master/src/test/java/seedu/unienable/command/activity/EditCommandTest.java) and search tests for partial updates, rejected changes, and chronological results ([PR #31](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/31)); later team regressions extend these suites.

#### Documentation contributions

- **User Guide:** [Data storage](../UserGuide.md#data-storage), [completion commands](../UserGuide.md#changing-completion-mark-and-unmark), [editing](../UserGuide.md#editing-an-activity-edit), [name search](../UserGuide.md#searching-activity-names-find), and persistence/recovery guidance in [error handling](../UserGuide.md#error-handling).
- **Developer Guide:** [Completion, storage, editing, and search](../DeveloperGuide.md#completion-storage-editing-and-search), including partial edits, chronological indexes, conditional saves, record formats, and backup/retry behavior.
- **UML diagrams:** Contributed five class/sequence pairs for editing, name search, marking, unmarking, and persistence ([diagram index](../diagrams/README.md#oliver)). They show how commands use the activity list and how the console decides whether saving is needed.

#### Team-based tasks

Connected add, delete, and completion commands to the command-result flag that tells the console when to save ([PR #20](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/20)). Merged the team's ratings foundation while developing edit/search ([PR #31](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/31)). Updated my diagram assets in [PR #59](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/59) and [PR #70](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/70).

<!-- @@author -->
