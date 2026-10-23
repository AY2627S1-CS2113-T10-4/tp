# Code ownership and RepoSense attribution

Ownership follows the [Developer Guide responsibility table](DeveloperGuide.md#team-contributions)
and [portfolio responsibility index](team/README.md). Bug fixes, integration edits and formatting
do not transfer feature responsibility to the latest editor.

| GitHub identity | Responsibility |
| --- | --- |
| `Atharva005` | Activity creation, base add validation and help. Calendar dashboards remain planned. |
| `officialasyrafrahim` | Chronological/demand listing, displayed indexes, deletion, priority, overdue and next. |
| `Oliver-koh` | Completion, activity storage/recovery, editing and activity-name search. |
| `lukelouyu` | Optional add ratings, daily high-demand count, facility reference data/queries, routing and exit. |

The table below records file responsibility. A single-feature class and its focused tests are tagged
for that feature owner. Mixed classes use paired `//@@author ID` and `//@@author` comments around
individual methods, fields or statements; shared constructors, imports, console scaffolding and
cross-feature test fixtures retain normal Git attribution. A file listed as shared is not claimed
exclusively by a teammate. New regression tests follow the feature being tested.

For example, add-name validation belongs to Atharva, even when Luke fixes it; edit-name validation
belongs to Oliver. Rating parsing on add belongs to Luke, while priority-score calculation belongs
to Asyraf. Storage serialization of ratings remains Oliver's persistence responsibility.

| Java file | Feature owners / shared scope |
| --- | --- |
| [src/main/java/seedu/unienable/ConsoleHelper.java](../src/main/java/seedu/unienable/ConsoleHelper.java) | `Oliver-koh`, `lukelouyu`; shared scaffolding remains outside tags |
| [src/main/java/seedu/unienable/UniEnable.java](../src/main/java/seedu/unienable/UniEnable.java) | Shared console integration; `Oliver-koh` owns loading/recovery. |
| [src/main/java/seedu/unienable/command/ByeCommand.java](../src/main/java/seedu/unienable/command/ByeCommand.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/command/Command.java](../src/main/java/seedu/unienable/command/Command.java) | Shared integration/support |
| [src/main/java/seedu/unienable/command/CommandResult.java](../src/main/java/seedu/unienable/command/CommandResult.java) | Shared integration/support |
| [src/main/java/seedu/unienable/command/HelpCommand.java](../src/main/java/seedu/unienable/command/HelpCommand.java) | `Atharva005` |
| [src/main/java/seedu/unienable/command/accessibility/connection/RouteCommand.java](../src/main/java/seedu/unienable/command/accessibility/connection/RouteCommand.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/command/accessibility/facility/FacilityFindCommand.java](../src/main/java/seedu/unienable/command/accessibility/facility/FacilityFindCommand.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/command/accessibility/facility/FacilityListCommand.java](../src/main/java/seedu/unienable/command/accessibility/facility/FacilityListCommand.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/command/accessibility/facility/FacilityViewCommand.java](../src/main/java/seedu/unienable/command/accessibility/facility/FacilityViewCommand.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/command/activity/AddCommand.java](../src/main/java/seedu/unienable/command/activity/AddCommand.java) | `Atharva005`, `lukelouyu`; shared scaffolding remains outside tags |
| [src/main/java/seedu/unienable/command/activity/DeleteCommand.java](../src/main/java/seedu/unienable/command/activity/DeleteCommand.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/command/activity/DemandTodayCommand.java](../src/main/java/seedu/unienable/command/activity/DemandTodayCommand.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/command/activity/EditCommand.java](../src/main/java/seedu/unienable/command/activity/EditCommand.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/command/activity/FindCommand.java](../src/main/java/seedu/unienable/command/activity/FindCommand.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/command/activity/ListCommand.java](../src/main/java/seedu/unienable/command/activity/ListCommand.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/command/activity/ListDemandCommand.java](../src/main/java/seedu/unienable/command/activity/ListDemandCommand.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/command/activity/ListPriorityCommand.java](../src/main/java/seedu/unienable/command/activity/ListPriorityCommand.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/command/activity/MarkCommand.java](../src/main/java/seedu/unienable/command/activity/MarkCommand.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/command/activity/NextCommand.java](../src/main/java/seedu/unienable/command/activity/NextCommand.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/command/activity/OverdueCommand.java](../src/main/java/seedu/unienable/command/activity/OverdueCommand.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/command/activity/UnmarkCommand.java](../src/main/java/seedu/unienable/command/activity/UnmarkCommand.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/exception/InvalidIndexException.java](../src/main/java/seedu/unienable/exception/InvalidIndexException.java) | Shared integration/support |
| [src/main/java/seedu/unienable/exception/ParseException.java](../src/main/java/seedu/unienable/exception/ParseException.java) | Shared integration/support |
| [src/main/java/seedu/unienable/exception/UniEnableException.java](../src/main/java/seedu/unienable/exception/UniEnableException.java) | Shared integration/support |
| [src/main/java/seedu/unienable/logic/ConnectionManager.java](../src/main/java/seedu/unienable/logic/ConnectionManager.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/logic/DijkstraShortestPath.java](../src/main/java/seedu/unienable/logic/DijkstraShortestPath.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/logic/DijkstraSolver.java](../src/main/java/seedu/unienable/logic/DijkstraSolver.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/logic/FacilityManager.java](../src/main/java/seedu/unienable/logic/FacilityManager.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/model/Activity.java](../src/main/java/seedu/unienable/model/Activity.java) | `Atharva005`, `Oliver-koh`, `lukelouyu`, `officialasyrafrahim`; shared scaffolding remains outside tags |
| [src/main/java/seedu/unienable/model/ActivityList.java](../src/main/java/seedu/unienable/model/ActivityList.java) | `Atharva005`, `Oliver-koh`, `lukelouyu`, `officialasyrafrahim`; shared scaffolding remains outside tags |
| [src/main/java/seedu/unienable/model/Connection.java](../src/main/java/seedu/unienable/model/Connection.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/model/Facility.java](../src/main/java/seedu/unienable/model/Facility.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/model/FacilityFeature.java](../src/main/java/seedu/unienable/model/FacilityFeature.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/model/enums/AccessibilityStatus.java](../src/main/java/seedu/unienable/model/enums/AccessibilityStatus.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/model/enums/DemandLevel.java](../src/main/java/seedu/unienable/model/enums/DemandLevel.java) | `Atharva005` |
| [src/main/java/seedu/unienable/model/enums/ShelterStatus.java](../src/main/java/seedu/unienable/model/enums/ShelterStatus.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/model/enums/TraversalType.java](../src/main/java/seedu/unienable/model/enums/TraversalType.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/parser/AddCommandParser.java](../src/main/java/seedu/unienable/parser/AddCommandParser.java) | `Atharva005`, `lukelouyu`; shared scaffolding remains outside tags |
| [src/main/java/seedu/unienable/parser/EditCommandParser.java](../src/main/java/seedu/unienable/parser/EditCommandParser.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/parser/FacilityCommandParser.java](../src/main/java/seedu/unienable/parser/FacilityCommandParser.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/parser/ListDeleteCommandParser.java](../src/main/java/seedu/unienable/parser/ListDeleteCommandParser.java) | `officialasyrafrahim` |
| [src/main/java/seedu/unienable/parser/MarkCommandParser.java](../src/main/java/seedu/unienable/parser/MarkCommandParser.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/parser/Parser.java](../src/main/java/seedu/unienable/parser/Parser.java) | `Atharva005`, `Oliver-koh`, `lukelouyu`, `officialasyrafrahim`; shared scaffolding remains outside tags |
| [src/main/java/seedu/unienable/parser/RouteParser.java](../src/main/java/seedu/unienable/parser/RouteParser.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/storage/ConnectionStorage.java](../src/main/java/seedu/unienable/storage/ConnectionStorage.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/storage/FacilityStorage.java](../src/main/java/seedu/unienable/storage/FacilityStorage.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/storage/LoadResult.java](../src/main/java/seedu/unienable/storage/LoadResult.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/storage/Storage.java](../src/main/java/seedu/unienable/storage/Storage.java) | `Oliver-koh` |
| [src/main/java/seedu/unienable/ui/Ui.java](../src/main/java/seedu/unienable/ui/Ui.java) | Shared integration/support |
| [src/main/java/seedu/unienable/ui/accessibility/AccessibilityDisclaimer.java](../src/main/java/seedu/unienable/ui/accessibility/AccessibilityDisclaimer.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/ui/accessibility/FacilityDetailsFormatter.java](../src/main/java/seedu/unienable/ui/accessibility/FacilityDetailsFormatter.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/ui/accessibility/FacilityOutputFormatter.java](../src/main/java/seedu/unienable/ui/accessibility/FacilityOutputFormatter.java) | `lukelouyu` |
| [src/main/java/seedu/unienable/ui/activity/ActivityListFormatter.java](../src/main/java/seedu/unienable/ui/activity/ActivityListFormatter.java) | `Oliver-koh`, `officialasyrafrahim`; shared scaffolding remains outside tags |
| [src/test/java/seedu/unienable/UniEnableTest.java](../src/test/java/seedu/unienable/UniEnableTest.java) | `Atharva005`, `Oliver-koh`, `lukelouyu`; shared scaffolding remains outside tags |
| [src/test/java/seedu/unienable/command/HelpCommandTest.java](../src/test/java/seedu/unienable/command/HelpCommandTest.java) | `Atharva005` |
| [src/test/java/seedu/unienable/command/accessibility/connection/RouteCommandTest.java](../src/test/java/seedu/unienable/command/accessibility/connection/RouteCommandTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/command/accessibility/facility/FacilityFindCommandTest.java](../src/test/java/seedu/unienable/command/accessibility/facility/FacilityFindCommandTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/command/accessibility/facility/FacilityListCommandTest.java](../src/test/java/seedu/unienable/command/accessibility/facility/FacilityListCommandTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/command/accessibility/facility/FacilityViewCommandTest.java](../src/test/java/seedu/unienable/command/accessibility/facility/FacilityViewCommandTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/command/activity/AddCommandTest.java](../src/test/java/seedu/unienable/command/activity/AddCommandTest.java) | `Atharva005`, `lukelouyu`; shared scaffolding remains outside tags |
| [src/test/java/seedu/unienable/command/activity/DeleteCommandTest.java](../src/test/java/seedu/unienable/command/activity/DeleteCommandTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/command/activity/DemandTodayCommandTest.java](../src/test/java/seedu/unienable/command/activity/DemandTodayCommandTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/command/activity/EditCommandTest.java](../src/test/java/seedu/unienable/command/activity/EditCommandTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/command/activity/FindCommandTest.java](../src/test/java/seedu/unienable/command/activity/FindCommandTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/command/activity/ListCommandTest.java](../src/test/java/seedu/unienable/command/activity/ListCommandTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/command/activity/ListDemandCommandTest.java](../src/test/java/seedu/unienable/command/activity/ListDemandCommandTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/command/activity/ListPriorityCommandTest.java](../src/test/java/seedu/unienable/command/activity/ListPriorityCommandTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/command/activity/MarkCommandTest.java](../src/test/java/seedu/unienable/command/activity/MarkCommandTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/command/activity/NextCommandTest.java](../src/test/java/seedu/unienable/command/activity/NextCommandTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/command/activity/OverdueCommandTest.java](../src/test/java/seedu/unienable/command/activity/OverdueCommandTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/command/activity/UnmarkCommandTest.java](../src/test/java/seedu/unienable/command/activity/UnmarkCommandTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/logic/ConnectionManagerTest.java](../src/test/java/seedu/unienable/logic/ConnectionManagerTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/logic/DijkstraShortestPathTest.java](../src/test/java/seedu/unienable/logic/DijkstraShortestPathTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/logic/DijkstraSolverTest.java](../src/test/java/seedu/unienable/logic/DijkstraSolverTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/logic/FacilityManagerTest.java](../src/test/java/seedu/unienable/logic/FacilityManagerTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/model/ActivityTest.java](../src/test/java/seedu/unienable/model/ActivityTest.java) | `Oliver-koh`, `lukelouyu`, `officialasyrafrahim`; shared scaffolding remains outside tags |
| [src/test/java/seedu/unienable/model/FacilityTest.java](../src/test/java/seedu/unienable/model/FacilityTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/parser/AddCommandParserTest.java](../src/test/java/seedu/unienable/parser/AddCommandParserTest.java) | `Atharva005`, `lukelouyu`; shared scaffolding remains outside tags |
| [src/test/java/seedu/unienable/parser/EditCommandParserTest.java](../src/test/java/seedu/unienable/parser/EditCommandParserTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/parser/FacilityCommandParserTest.java](../src/test/java/seedu/unienable/parser/FacilityCommandParserTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/parser/ListDeleteCommandParserTest.java](../src/test/java/seedu/unienable/parser/ListDeleteCommandParserTest.java) | `officialasyrafrahim` |
| [src/test/java/seedu/unienable/parser/MarkCommandParserTest.java](../src/test/java/seedu/unienable/parser/MarkCommandParserTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/parser/RouteParserTest.java](../src/test/java/seedu/unienable/parser/RouteParserTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/storage/ConnectionStorageTest.java](../src/test/java/seedu/unienable/storage/ConnectionStorageTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/storage/FacilityStorageTest.java](../src/test/java/seedu/unienable/storage/FacilityStorageTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/storage/LoadResultTest.java](../src/test/java/seedu/unienable/storage/LoadResultTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/storage/StorageTest.java](../src/test/java/seedu/unienable/storage/StorageTest.java) | `Oliver-koh` |
| [src/test/java/seedu/unienable/ui/accessibility/FacilityDetailsFormatterTest.java](../src/test/java/seedu/unienable/ui/accessibility/FacilityDetailsFormatterTest.java) | `lukelouyu` |
| [src/test/java/seedu/unienable/ui/activity/ActivityListFormatterTest.java](../src/test/java/seedu/unienable/ui/activity/ActivityListFormatterTest.java) | `officialasyrafrahim` |

Documentation, data and diagrams follow the same responsibility assignment:

| Files | Ownership |
| --- | --- |
| `docs/DeveloperGuide.md` | Existing named owner blocks are tagged; shared setup, architecture, testing and responsibility tables remain shared. |
| `docs/UserGuide.md` | Feature sections are tagged by responsibility. Shared syntax, mixed tables, FAQ and command summary remain shared. |
| `docs/team/*.md` | Each portfolio belongs to its named member. An empty page stays empty. |
| `src/main/resources/facilities.txt`, `connections.txt` | `lukelouyu`; tags use loader-supported `#` comments. |
| `docs/diagrams/luke/`, `docs/images/luke/` | `lukelouyu`. |
| `docs/diagrams/asyraf/`, `docs/images/asyraf/` | `officialasyrafrahim`. |
| `docs/diagrams/shared/`, `docs/images/shared/` | Shared architecture and activity creation/rating model. |
| Build configuration, CI, common guide sections and this index | Shared project infrastructure. |

Markdown ownership tags use HTML comments. Tags are not inserted inside Markdown tables or
code examples. Binary diagrams have no line-level author tags; their responsibility is recorded above.

The four identities match the repository's [About Us](AboutUs.md) GitHub profiles. The course-linked
author-config URL returned HTTP 404 during this review, so recognition by the live course dashboard
has not been verified. No course configuration or history was rewritten. After the normal commit/push
workflow, check each identity and section in the dashboard; correct the alias mapping if necessary.

The [course guidance](https://nus-cs2113-ay2627-s1.github.io/website/admin/tools.html#tool-reposense-for-authorship-tracking)
and [RepoSense tag documentation](https://reposense.org/ug/usingAuthorTags.html) explain attribution
overrides. The [course FAQ](https://nus-cs2113-ay2627-s1.github.io/website/admin/appendixC-faq.html#faq-what-if-someone-took-over-a-feature-from-another-team-member)
requires one author per line and team agreement for feature takeovers. This index applies the
existing responsibility split; it does not invent a takeover or claim shared work for one person.
