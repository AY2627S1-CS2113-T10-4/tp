<!-- @@author officialasyrafrahim -->

# Muhammad Asyraf Bin Abdul Rahim - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information. My contributions help users inspect their commitments in useful orders and select the correct activity for deletion or follow-up actions.

### Summary of contributions

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=officialasyrafrahim&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Activity listing and deletion:** Implemented `list`, `list demand`, and `delete` across parsing, commands, model operations, formatting, and tests ([PR #16](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/16)). Chronological and demand orderings are generated views, preserving insertion order for storage. Displayed indexes resolve through a canonical chronological view, so sorting does not change which activity an index identifies.
- **Priority and time-based planning:** Added `list priority`, `overdue`, and `next` ([PR #36](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/36)). Priority uses `URG x URG x (IMP + COM)`, places incomplete rating sets last, and keeps stable chronological ties. Time queries exclude completed activities and distinguish an ended activity from an upcoming one; injected clocks support repeatable boundary tests.
- **Defensive behavior:** Added ordering/deletion invariants and diagnostic logging ([PR #29](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/29)). Tests cover empty lists, invalid indexes, ordering ties, unavailable priority scores, and index preservation. Query commands do not request persistence, while deletion reports its state change for the shared save flow.

#### Documentation contributions

- **User Guide:** [Chronological/demand listing](../UserGuide.md#listing-activities-list-and-list-demand), [deletion](../UserGuide.md#deleting-an-activity-delete), [priority](../UserGuide.md#viewing-priority-list-priority), [overdue](../UserGuide.md#viewing-overdue-activities-overdue), and [next activity](../UserGuide.md#viewing-the-next-activity-next), including index and time-boundary rules.
- **Developer Guide:** [Activity views and deletion](../DeveloperGuide.md#activity-views-and-deletion), explaining comparator-based views, canonical index resolution, query boundaries, and related tests.
- **UML diagrams:** Contributed six class/sequence pairs for chronological listing, demand listing, deletion, priority listing, overdue activities, and next activity ([diagram index](../diagrams/README.md#asyraf)). The feature diagrams and accompanying guide updates were submitted in [PR #43](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/43).

#### Team-based tasks

Fixed shared add-input handling so ordinary slash-containing names remain valid, aligned end-date/time validation with a testable clock, and repaired launcher integration ([PR #25](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/25)). Reconciled concurrent parser/import changes with the shared branch and recorded the resolution and Java 25 verification in the [integration follow-up](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/25#issuecomment-6150154257). These changes supported activity creation and reliable builds beyond my listing/deletion feature area.

<!-- @@author -->
