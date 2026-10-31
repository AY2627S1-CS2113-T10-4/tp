<!-- @@author lukelouyu -->

# Lou Yu - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information.

### Summary of contributions

I implemented facility queries and accessible-route search, added optional activity ratings, and strengthened shared input validation and save recovery. I also contributed routing/regression tests, architecture and feature documentation, UML diagrams, and reviews of teammates' changes.

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=lukelouyu&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Facilities and accessible routes:** Implemented facility lookup, feature/status filtering, data loading, and route queries to help students inspect recorded accessibility information ([PR #6](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/6), [PR #17](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/17)). Dijkstra's algorithm considers accessible bidirectional connections. Separating graph search, loading, and formatting keeps route queries independently testable and lets the console report recoverable data errors.
- **Optional activity ratings:** Added independently optional importance, urgency, and commitment ratings for more useful workload planning ([PR #30](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/30)). Shared 1-5 validation preserves the distinction between an unset rating and a score. Extending storage while retaining legacy six-field loading let teammates build priority and calendar features on a compatible model.
- **Shared validation and recovery:** Added name/parameter checks and duplicate/clash validation before activity mutation ([PR #52](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/52), [PR #54](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/54)). Students can explicitly approve overlaps while duplicates remain rejected. Later work aligned input rules with historical storage compatibility and made `bye` retry unsaved changes ([PR #55](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/55)), extending the team's original persistence implementation.

#### Testing contributions

Replaced library-generated routing expectations with a fixed distance matrix for all 81 bundled campus pairs, independently checking shortest distances and that returned routes use valid links ([PR #45](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/45), [tests](https://github.com/AY2627S1-CS2113-T10-4/tp/blob/master/src/test/java/seedu/unienable/command/accessibility/connection/logic/DijkstraShortestPathTest.java)). This includes a zero-distance route from a facility to itself. Added optional-rating/legacy-storage regressions ([PR #30](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/30)) and failed-save/exit-retry tests ([PR #55](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/55)).

#### Documentation contributions

- **User Guide:** [Optional add ratings](../UserGuide.md#trying-optional-add-ratings), [facility queries](../UserGuide.md#listing-and-viewing-facilities-facility), [feature search](../UserGuide.md#finding-facilities-by-feature-facility-find), [routes](../UserGuide.md#finding-an-accessible-route-route), and [exit](../UserGuide.md#exiting-the-application-bye).
- **Developer Guide:** [Architecture](../DeveloperGuide.md#architecture), shared validation, [ratings/accessibility](../DeveloperGuide.md#ratings-and-accessibility), data-loading contracts, and routing design. Consolidated guide sections and accessibility diagrams in [PR #35](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/35).
- **Diagrams:** Contributed the activity-creation class diagram, name and parameter validation class/sequence pairs, architecture diagram, facility and routing class/sequence pairs, and campus connectivity graph - eleven diagrams in total ([index](../diagrams/README.md#luke)). The add sequence and schedule validation pair remain credited to Atharva.

#### Team-based tasks and reviews

Gave specific feedback on add parsing, model insertion, package placement, and tests in [PR #11](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/11#issuecomment-6075041154). Reviewed and approved the [planning views](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/36#pullrequestreview-5601662187) and [calendar command](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/47#pullrequestreview-5643188730). Reported a reproducible Unicode-whitespace edit/restart defect with regression suggestions in [course tracker issue #41](https://github.com/NUS-CS2113-AY2627-S1/tp/issues/41).

**Credits:** Campus data references the NUS FASS accessibility map; inherited tooling comes from the course starter ([acknowledgements](../DeveloperGuide.md#acknowledgements)).

<!-- @@author -->
