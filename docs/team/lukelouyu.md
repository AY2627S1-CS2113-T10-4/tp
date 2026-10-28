<!-- @@author lukelouyu -->

# Lou Yu - Project Portfolio Page

## Project: UniEnable

### Overview

UniEnable is a Java 25 command-line application that helps students plan dated activities and check recorded campus accessibility information. My work connects campus accessibility queries with shared validation, optional activity ratings, and integration across feature areas.

### Summary of contributions

**Code contributed:** [My code and tests on the tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs%7Efunctional-code%7Etest-code%7Eother&since=2026-09-18T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=lukelouyu&tabRepo=AY2627S1-CS2113-T10-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=functional-code%7Etest-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented

- **Facilities and accessible routes:** Implemented facility models, data loading, lookup, feature/status filtering, and route queries ([PR #6](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/6), [PR #17](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/17)). Dijkstra's algorithm searches bidirectional connections marked accessible and reports distance, shelter, and recorded barriers. Separating loading, graph search, and formatting supports recoverable failures and independent repeated queries.
- **Optional activity ratings:** Integrated independent importance, urgency, and commitment ratings into creation and the shared model ([PR #30](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/30)). Ratings may be omitted; supplied values share a 1-5 rule. Integration preserves unset ratings and legacy records while supporting teammates' priority and calendar features.
- **Shared validation and integration:** Implemented command-word checks and shared activity-name, parameter-spelling, duplicate, and schedule-clash validation. These reject invalid changes before mutation, support valid Unicode names, and allow explicitly confirmed overlaps while still rejecting duplicates. Also maintained exit handling and shared command/package integration.

#### Documentation contributions

- **User Guide:** [Optional add ratings](../UserGuide.md#trying-optional-add-ratings), [facility queries](../UserGuide.md#listing-and-viewing-facilities-facility), [feature search](../UserGuide.md#finding-facilities-by-feature-facility-find), [routes](../UserGuide.md#finding-an-accessible-route-route), and [exit](../UserGuide.md#exiting-the-application-bye).
- **Developer Guide:** [Architecture](../DeveloperGuide.md#architecture), shared validation, [ratings/accessibility](../DeveloperGuide.md#ratings-and-accessibility), data-loading contracts, and routing design. Consolidated guide sections and accessibility diagrams in [PR #35](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/35).
- **Diagrams:** Contributed the architecture diagram, facility and routing class/sequence pairs, and the campus connectivity graph - six diagrams in total ([index](../diagrams/README.md#luke)). The six shared validation diagrams are credited to Atharva.

#### Team-based tasks and reviews

Gave specific feedback on add parsing, model insertion, package placement, and tests in [PR #11](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/11#issuecomment-6075041154). Reviewed and approved the [planning views](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/36#pullrequestreview-5601662187) and [calendar command](https://github.com/AY2627S1-CS2113-T10-4/tp/pull/47#pullrequestreview-5643188730). Reported a reproducible Unicode-whitespace edit/restart defect with regression suggestions in [course tracker issue #41](https://github.com/NUS-CS2113-AY2627-S1/tp/issues/41).

**Credits:** Campus data references the NUS FASS accessibility map; inherited tooling comes from the course starter ([acknowledgements](../DeveloperGuide.md#acknowledgements)).

<!-- @@author -->
