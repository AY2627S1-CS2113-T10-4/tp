# Lou Yu Luke - Project Portfolio Page

GitHub: [@lukelouyu](https://github.com/lukelouyu)

## Overview

UniEnable is a Java command-line application for managing dated activities and
checking campus accessibility reference information. Luke's responsibilities
cover optional add ratings, today's high-demand count, and recorded campus
accessibility queries.

## Summary of responsibilities

| Version | Feature | Current status |
| --- | --- | --- |
| V1.0 | Exit the application using `bye`. | Implemented. |
| V1.0 | List, look up and filter facilities using `facility`. | Implemented. |
| V2.0 | Add independently optional importance, urgency and commitment ratings. | Implemented. |
| V2.0 | Count today's HIGH-demand activities using `demand today` (US16). | Implemented. |
| V2.0 | Find shortest recorded accessible routes using `route`. | Implemented. |

Optional ratings use integers from 1 to 5, with omitted values stored as unset.
They extend Atharva's add flow and feed Asyraf's priority calculation; Oliver's
editing and storage flows preserve them.

`demand today` counts all HIGH-demand activities scheduled on the current local
date, including completed activities and those that have already ended. It reads
the current date and activity list on each execution, reports zero when nothing
matches, and leaves activity data and its saved file unchanged.

Facility queries use bundled records for AS1-AS8 and CLB. Routes minimize
recorded distance over bidirectional connections marked YES for accessibility.
Output includes segment details and the sample-data disclaimer. This is recorded
reference information rather than live campus conditions.

## Developer Guide sections

- [Luke's implementation responsibilities](../DeveloperGuide.md#luke-ratings-and-accessibility).
- [Optional ratings integration](../DeveloperGuide.md#optional-ratings-integration).
- [Today's high-demand count (US16)](../DeveloperGuide.md#todays-high-demand-count-us16).
- [Facility data](../DeveloperGuide.md#facility-accessibility-data-facilitiestxt) and
  [facility lookup](../DeveloperGuide.md#facility-lookup).
- [Connection data](../DeveloperGuide.md#accessibility-route-connections-connectionstxt) and
  [accessible route search](../DeveloperGuide.md#accessible-route-search).
- [Exiting the application](../DeveloperGuide.md#exiting-the-application).

## PlantUML sources and images

| Diagram | Source | Image |
| --- | --- | --- |
| Facility class model | [PlantUML](../diagrams/luke/FacilityClassDiagram.puml) | [PNG](../images/luke/FacilityClassDiagram.png) |
| Facility lookup sequence | [PlantUML](../diagrams/luke/FacilityLookupSequence.puml) | [PNG](../images/luke/FacilityLookupSequence.png) |
| Campus connectivity graph | [PlantUML](../diagrams/luke/fass-accessibility-network.puml) | [SVG](../images/luke/fass-accessibility-network.svg) |
| Routing class model | [PlantUML](../diagrams/luke/RoutingClassDiagram.puml) | [PNG](../images/luke/RoutingClassDiagram.png) |
| Route search sequence | [PlantUML](../diagrams/luke/RouteSearchSequence.puml) | [PNG](../images/luke/RouteSearchSequence.png) |
| Shared activity creation and rating model | [PlantUML](../diagrams/shared/ActivityCreationClassDiagram.puml) | [PNG](../images/shared/ActivityCreationClassDiagram.png) |

The combined creation class diagram is shared with Atharva. Exit currently has
no separate diagram. The shared architecture is indexed in the
[diagram ownership page](../diagrams/README.md#shared).

## Related code and tests

- Reference data: [facilities.txt](../../src/main/resources/facilities.txt),
  [connections.txt](../../src/main/resources/connections.txt).
- Code: [FacilityManager](../../src/main/java/seedu/unienable/logic/FacilityManager.java),
  [ConnectionManager](../../src/main/java/seedu/unienable/logic/ConnectionManager.java),
  [DijkstraShortestPath](../../src/main/java/seedu/unienable/logic/DijkstraShortestPath.java),
  [DemandTodayCommand](../../src/main/java/seedu/unienable/command/activity/DemandTodayCommand.java),
  [ActivityList](../../src/main/java/seedu/unienable/model/ActivityList.java),
  [ByeCommand](../../src/main/java/seedu/unienable/command/ByeCommand.java).
- Verification: [FacilityManagerTest](../../src/test/java/seedu/unienable/logic/FacilityManagerTest.java),
  [DijkstraShortestPathTest](../../src/test/java/seedu/unienable/logic/DijkstraShortestPathTest.java),
  [RouteCommandTest](../../src/test/java/seedu/unienable/command/accessibility/connection/RouteCommandTest.java),
  [DemandTodayCommandTest](../../src/test/java/seedu/unienable/command/activity/DemandTodayCommandTest.java),
  [UniEnableTest](../../src/test/java/seedu/unienable/UniEnableTest.java),
  [ActivityTest](../../src/test/java/seedu/unienable/model/ActivityTest.java).

[Team portfolios](README.md) · [About Us](../AboutUs.md) · [Developer Guide](../DeveloperGuide.md)
