package accountLedgerCli.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Root(
    val title: String,
    val description: String,
    val type: String,
    val properties: Properties,
)

@Serializable
internal data class Properties(
    val forks: Forks,
    val history: History,
    @SerialName("fork_of")
    val forkOf: ForkOf,
    val url: Url8,
    @SerialName("forks_url")
    val forksUrl: ForksUrl2,
    @SerialName("commits_url")
    val commitsUrl: CommitsUrl2,
    val id: Id7,
    @SerialName("node_id")
    val nodeId: NodeId6,
    @SerialName("git_pull_url")
    val gitPullUrl: GitPullUrl2,
    @SerialName("git_push_url")
    val gitPushUrl: GitPushUrl2,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl6,
    val files: Files2,
    val public: Public2,
    @SerialName("created_at")
    val createdAt: CreatedAt4,
    @SerialName("updated_at")
    val updatedAt: UpdatedAt4,
    val description: Description2,
    val comments: Comments2,
    val user: User4,
    @SerialName("comments_url")
    val commentsUrl: CommentsUrl2,
    val owner: Owner2,
    val truncated: Truncated3,
)

@Serializable
internal data class Forks(
    val deprecated: Boolean,
    val type: List<String>,
    val items: Items,
)

@Serializable
internal data class Items(
    val type: String,
    val properties: Properties2,
)

@Serializable
internal data class Properties2(
    val id: Id,
    val url: Url,
    val user: User,
    @SerialName("created_at")
    val createdAt: CreatedAt2,
    @SerialName("updated_at")
    val updatedAt: UpdatedAt2,
)

@Serializable
internal data class Id(
    val type: String,
)

@Serializable
internal data class Url(
    val type: String,
    val format: String,
)

@Serializable
internal data class User(
    val title: String,
    val description: String,
    val type: String,
    val properties: Properties3,
    val required: List<String>,
    val additionalProperties: Boolean,
)

@Serializable
internal data class Properties3(
    val login: Login,
    val id: Id2,
    @SerialName("node_id")
    val nodeId: NodeId,
    @SerialName("avatar_url")
    val avatarUrl: AvatarUrl,
    @SerialName("gravatar_id")
    val gravatarId: GravatarId,
    val url: Url2,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl,
    @SerialName("followers_url")
    val followersUrl: FollowersUrl,
    @SerialName("following_url")
    val followingUrl: FollowingUrl,
    @SerialName("gists_url")
    val gistsUrl: GistsUrl,
    @SerialName("starred_url")
    val starredUrl: StarredUrl,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: SubscriptionsUrl,
    @SerialName("organizations_url")
    val organizationsUrl: OrganizationsUrl,
    @SerialName("repos_url")
    val reposUrl: ReposUrl,
    @SerialName("events_url")
    val eventsUrl: EventsUrl,
    @SerialName("received_events_url")
    val receivedEventsUrl: ReceivedEventsUrl,
    val type: Type,
    @SerialName("site_admin")
    val siteAdmin: SiteAdmin,
    val name: Name,
    val company: Company,
    val blog: Blog,
    val location: Location,
    val email: Email,
    val hireable: Hireable,
    val bio: Bio,
    @SerialName("twitter_username")
    val twitterUsername: TwitterUsername,
    @SerialName("public_repos")
    val publicRepos: PublicRepos,
    @SerialName("public_gists")
    val publicGists: PublicGists,
    val followers: Followers,
    val following: Following,
    @SerialName("created_at")
    val createdAt: CreatedAt,
    @SerialName("updated_at")
    val updatedAt: UpdatedAt,
    val plan: Plan,
    @SerialName("suspended_at")
    val suspendedAt: SuspendedAt,
    @SerialName("private_gists")
    val privateGists: PrivateGists,
    @SerialName("total_private_repos")
    val totalPrivateRepos: TotalPrivateRepos,
    @SerialName("owned_private_repos")
    val ownedPrivateRepos: OwnedPrivateRepos,
    @SerialName("disk_usage")
    val diskUsage: DiskUsage,
    val collaborators: Collaborators2,
)

@Serializable
internal data class Login(
    val type: String,
)

@Serializable
internal data class Id2(
    val type: String,
)

@Serializable
internal data class NodeId(
    val type: String,
)

@Serializable
internal data class AvatarUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class GravatarId(
    val type: List<String>,
)

@Serializable
internal data class Url2(
    val type: String,
    val format: String,
)

@Serializable
internal data class HtmlUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class FollowersUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class FollowingUrl(
    val type: String,
)

@Serializable
internal data class GistsUrl(
    val type: String,
)

@Serializable
internal data class StarredUrl(
    val type: String,
)

@Serializable
internal data class SubscriptionsUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class OrganizationsUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class ReposUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class EventsUrl(
    val type: String,
)

@Serializable
internal data class ReceivedEventsUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class Type(
    val type: String,
)

@Serializable
internal data class SiteAdmin(
    val type: String,
)

@Serializable
internal data class Name(
    val type: List<String>,
)

@Serializable
internal data class Company(
    val type: List<String>,
)

@Serializable
internal data class Blog(
    val type: List<String>,
)

@Serializable
internal data class Location(
    val type: List<String>,
)

@Serializable
internal data class Email(
    val type: List<String>,
    val format: String,
)

@Serializable
internal data class Hireable(
    val type: List<String>,
)

@Serializable
internal data class Bio(
    val type: List<String>,
)

@Serializable
internal data class TwitterUsername(
    val type: List<String>,
)

@Serializable
internal data class PublicRepos(
    val type: String,
)

@Serializable
internal data class PublicGists(
    val type: String,
)

@Serializable
internal data class Followers(
    val type: String,
)

@Serializable
internal data class Following(
    val type: String,
)

@Serializable
internal data class CreatedAt(
    val type: String,
    val format: String,
)

@Serializable
internal data class UpdatedAt(
    val type: String,
    val format: String,
)

@Serializable
internal data class Plan(
    val type: String,
    val properties: Properties4,
    val required: List<String>,
)

@Serializable
internal data class Properties4(
    val collaborators: Collaborators,
    val name: Name2,
    val space: Space,
    @SerialName("private_repos")
    val privateRepos: PrivateRepos,
)

@Serializable
internal data class Collaborators(
    val type: String,
)

@Serializable
internal data class Name2(
    val type: String,
)

@Serializable
internal data class Space(
    val type: String,
)

@Serializable
internal data class PrivateRepos(
    val type: String,
)

@Serializable
internal data class SuspendedAt(
    val type: List<String>,
    val format: String,
)

@Serializable
internal data class PrivateGists(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class TotalPrivateRepos(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class OwnedPrivateRepos(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class DiskUsage(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class Collaborators2(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class CreatedAt2(
    val type: String,
    val format: String,
)

@Serializable
internal data class UpdatedAt2(
    val type: String,
    val format: String,
)

@Serializable
internal data class History(
    val deprecated: Boolean,
    val type: List<String>,
    val items: Items2,
)

@Serializable
internal data class Items2(
    val title: String,
    val description: String,
    val type: String,
    val properties: Properties5,
)

@Serializable
internal data class Properties5(
    val user: User2,
    val version: Version,
    @SerialName("committed_at")
    val committedAt: CommittedAt,
    @SerialName("change_status")
    val changeStatus: ChangeStatus,
    val url: Url4,
)

@Serializable
internal data class User2(
    val anyOf: List<AnyOf>,
)

@Serializable
internal data class AnyOf(
    val type: String,
    val title: String?,
    val description: String?,
    val properties: Properties6?,
    val required: List<String>?,
)

@Serializable
internal data class Properties6(
    val name: Name3,
    val email: Email2,
    val login: Login2,
    val id: Id3,
    @SerialName("node_id")
    val nodeId: NodeId2,
    @SerialName("avatar_url")
    val avatarUrl: AvatarUrl2,
    @SerialName("gravatar_id")
    val gravatarId: GravatarId2,
    val url: Url3,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl2,
    @SerialName("followers_url")
    val followersUrl: FollowersUrl2,
    @SerialName("following_url")
    val followingUrl: FollowingUrl2,
    @SerialName("gists_url")
    val gistsUrl: GistsUrl2,
    @SerialName("starred_url")
    val starredUrl: StarredUrl2,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: SubscriptionsUrl2,
    @SerialName("organizations_url")
    val organizationsUrl: OrganizationsUrl2,
    @SerialName("repos_url")
    val reposUrl: ReposUrl2,
    @SerialName("events_url")
    val eventsUrl: EventsUrl2,
    @SerialName("received_events_url")
    val receivedEventsUrl: ReceivedEventsUrl2,
    val type: Type2,
    @SerialName("site_admin")
    val siteAdmin: SiteAdmin2,
    @SerialName("starred_at")
    val starredAt: StarredAt,
)

@Serializable
internal data class Name3(
    val type: List<String>,
)

@Serializable
internal data class Email2(
    val type: List<String>,
)

@Serializable
internal data class Login2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Id3(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class NodeId2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class AvatarUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class GravatarId2(
    val type: List<String>,
    val examples: List<String>,
)

@Serializable
internal data class Url3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class HtmlUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowersUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowingUrl2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class GistsUrl2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class StarredUrl2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SubscriptionsUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class OrganizationsUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class ReposUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class EventsUrl2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class ReceivedEventsUrl2(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class Type2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SiteAdmin2(
    val type: String,
)

@Serializable
internal data class StarredAt(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Version(
    val type: String,
)

@Serializable
internal data class CommittedAt(
    val type: String,
    val format: String,
)

@Serializable
internal data class ChangeStatus(
    val type: String,
    val properties: Properties7,
)

@Serializable
internal data class Properties7(
    val total: Total,
    val additions: Additions,
    val deletions: Deletions,
)

@Serializable
internal data class Total(
    val type: String,
)

@Serializable
internal data class Additions(
    val type: String,
)

@Serializable
internal data class Deletions(
    val type: String,
)

@Serializable
internal data class Url4(
    val type: String,
    val format: String,
)

@Serializable
internal data class ForkOf(
    val title: String,
    val description: String,
    val type: List<String>,
    val properties: Properties8,
    val required: List<String>,
)

@Serializable
internal data class Properties8(
    val url: Url5,
    @SerialName("forks_url")
    val forksUrl: ForksUrl,
    @SerialName("commits_url")
    val commitsUrl: CommitsUrl,
    val id: Id4,
    @SerialName("node_id")
    val nodeId: NodeId3,
    @SerialName("git_pull_url")
    val gitPullUrl: GitPullUrl,
    @SerialName("git_push_url")
    val gitPushUrl: GitPushUrl,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl3,
    val files: Files,
    val public: Public,
    @SerialName("created_at")
    val createdAt: CreatedAt3,
    @SerialName("updated_at")
    val updatedAt: UpdatedAt3,
    val description: Description,
    val comments: Comments,
    val user: User3,
    @SerialName("comments_url")
    val commentsUrl: CommentsUrl,
    val owner: Owner,
    val truncated: Truncated,
    val forks: Forks,
    val history: History,
)

@Serializable
internal data class Url5(
    val type: String,
    val format: String,
)

@Serializable
internal data class ForksUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class CommitsUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class Id4(
    val type: String,
)

@Serializable
internal data class NodeId3(
    val type: String,
)

@Serializable
internal data class GitPullUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class GitPushUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class HtmlUrl3(
    val type: String,
    val format: String,
)

@Serializable
internal data class Files(
    val type: String,
    val additionalProperties: AdditionalProperties,
)

@Serializable
internal data class AdditionalProperties(
    val type: String,
    val properties: Properties9,
)

@Serializable
internal data class Properties9(
    val filename: Filename,
    val type: Type3,
    val language: Language,
    @SerialName("raw_url")
    val rawUrl: RawUrl,
    val size: Size,
)

@Serializable
internal data class Filename(
    val type: String,
)

@Serializable
internal data class Type3(
    val type: String,
)

@Serializable
internal data class Language(
    val type: String,
)

@Serializable
internal data class RawUrl(
    val type: String,
)

@Serializable
internal data class Size(
    val type: String,
)

@Serializable
internal data class Public(
    val type: String,
)

@Serializable
internal data class CreatedAt3(
    val type: String,
    val format: String,
)

@Serializable
internal data class UpdatedAt3(
    val type: String,
    val format: String,
)

@Serializable
internal data class Description(
    val type: List<String>,
)

@Serializable
internal data class Comments(
    val type: String,
)

@Serializable
internal data class User3(
    val anyOf: List<AnyOf2>,
)

@Serializable
internal data class AnyOf2(
    val type: String,
    val title: String?,
    val description: String?,
    val properties: Properties10?,
    val required: List<String>?,
)

@Serializable
internal data class Properties10(
    val name: Name4,
    val email: Email3,
    val login: Login3,
    val id: Id5,
    @SerialName("node_id")
    val nodeId: NodeId4,
    @SerialName("avatar_url")
    val avatarUrl: AvatarUrl3,
    @SerialName("gravatar_id")
    val gravatarId: GravatarId3,
    val url: Url6,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl4,
    @SerialName("followers_url")
    val followersUrl: FollowersUrl3,
    @SerialName("following_url")
    val followingUrl: FollowingUrl3,
    @SerialName("gists_url")
    val gistsUrl: GistsUrl3,
    @SerialName("starred_url")
    val starredUrl: StarredUrl3,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: SubscriptionsUrl3,
    @SerialName("organizations_url")
    val organizationsUrl: OrganizationsUrl3,
    @SerialName("repos_url")
    val reposUrl: ReposUrl3,
    @SerialName("events_url")
    val eventsUrl: EventsUrl3,
    @SerialName("received_events_url")
    val receivedEventsUrl: ReceivedEventsUrl3,
    val type: Type4,
    @SerialName("site_admin")
    val siteAdmin: SiteAdmin3,
    @SerialName("starred_at")
    val starredAt: StarredAt2,
)

@Serializable
internal data class Name4(
    val type: List<String>,
)

@Serializable
internal data class Email3(
    val type: List<String>,
)

@Serializable
internal data class Login3(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Id5(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class NodeId4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class AvatarUrl3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class GravatarId3(
    val type: List<String>,
    val examples: List<String>,
)

@Serializable
internal data class Url6(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class HtmlUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowersUrl3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowingUrl3(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class GistsUrl3(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class StarredUrl3(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SubscriptionsUrl3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class OrganizationsUrl3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class ReposUrl3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class EventsUrl3(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class ReceivedEventsUrl3(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class Type4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SiteAdmin3(
    val type: String,
)

@Serializable
internal data class StarredAt2(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class CommentsUrl(
    val type: String,
    val format: String,
)

@Serializable
internal data class Owner(
    val anyOf: List<AnyOf3>,
)

@Serializable
internal data class AnyOf3(
    val type: String,
    val title: String?,
    val description: String?,
    val properties: Properties11?,
    val required: List<String>?,
)

@Serializable
internal data class Properties11(
    val name: Name5,
    val email: Email4,
    val login: Login4,
    val id: Id6,
    @SerialName("node_id")
    val nodeId: NodeId5,
    @SerialName("avatar_url")
    val avatarUrl: AvatarUrl4,
    @SerialName("gravatar_id")
    val gravatarId: GravatarId4,
    val url: Url7,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl5,
    @SerialName("followers_url")
    val followersUrl: FollowersUrl4,
    @SerialName("following_url")
    val followingUrl: FollowingUrl4,
    @SerialName("gists_url")
    val gistsUrl: GistsUrl4,
    @SerialName("starred_url")
    val starredUrl: StarredUrl4,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: SubscriptionsUrl4,
    @SerialName("organizations_url")
    val organizationsUrl: OrganizationsUrl4,
    @SerialName("repos_url")
    val reposUrl: ReposUrl4,
    @SerialName("events_url")
    val eventsUrl: EventsUrl4,
    @SerialName("received_events_url")
    val receivedEventsUrl: ReceivedEventsUrl4,
    val type: Type5,
    @SerialName("site_admin")
    val siteAdmin: SiteAdmin4,
    @SerialName("starred_at")
    val starredAt: StarredAt3,
)

@Serializable
internal data class Name5(
    val type: List<String>,
)

@Serializable
internal data class Email4(
    val type: List<String>,
)

@Serializable
internal data class Login4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Id6(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class NodeId5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class AvatarUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class GravatarId4(
    val type: List<String>,
    val examples: List<String>,
)

@Serializable
internal data class Url7(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class HtmlUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowersUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowingUrl4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class GistsUrl4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class StarredUrl4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SubscriptionsUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class OrganizationsUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class ReposUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class EventsUrl4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class ReceivedEventsUrl4(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class Type5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SiteAdmin4(
    val type: String,
)

@Serializable
internal data class StarredAt3(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Truncated(
    val type: String,
)

// @Serializable
// internal data class Forks2(
//     val type: String,
//     val items: Map<String, Any>,
// )

// @Serializable
// internal data class History2(
//     val type: String,
//     val items: Map<String, Any>,
// )

@Serializable
internal data class Url8(
    val type: String,
)

@Serializable
internal data class ForksUrl2(
    val type: String,
)

@Serializable
internal data class CommitsUrl2(
    val type: String,
)

@Serializable
internal data class Id7(
    val type: String,
)

@Serializable
internal data class NodeId6(
    val type: String,
)

@Serializable
internal data class GitPullUrl2(
    val type: String,
)

@Serializable
internal data class GitPushUrl2(
    val type: String,
)

@Serializable
internal data class HtmlUrl6(
    val type: String,
)

@Serializable
internal data class Files2(
    val type: String,
    val additionalProperties: AdditionalProperties2,
)

@Serializable
internal data class AdditionalProperties2(
    val type: List<String>,
    val properties: Properties12,
)

@Serializable
internal data class Properties12(
    val filename: Filename2,
    val type: Type6,
    val language: Language2,
    @SerialName("raw_url")
    val rawUrl: RawUrl2,
    val size: Size2,
    val truncated: Truncated2,
    val content: Content,
)

@Serializable
internal data class Filename2(
    val type: String,
)

@Serializable
internal data class Type6(
    val type: String,
)

@Serializable
internal data class Language2(
    val type: String,
)

@Serializable
internal data class RawUrl2(
    val type: String,
)

@Serializable
internal data class Size2(
    val type: String,
)

@Serializable
internal data class Truncated2(
    val type: String,
)

@Serializable
internal data class Content(
    val type: String,
)

@Serializable
internal data class Public2(
    val type: String,
)

@Serializable
internal data class CreatedAt4(
    val type: String,
)

@Serializable
internal data class UpdatedAt4(
    val type: String,
)

@Serializable
internal data class Description2(
    val type: List<String>,
)

@Serializable
internal data class Comments2(
    val type: String,
)

@Serializable
internal data class User4(
    val type: List<String>,
)

@Serializable
internal data class CommentsUrl2(
    val type: String,
)

@Serializable
internal data class Owner2(
    val title: String,
    val description: String,
    val type: String,
    val properties: Properties13,
    val required: List<String>,
)

@Serializable
internal data class Properties13(
    val name: Name6,
    val email: Email5,
    val login: Login5,
    val id: Id8,
    @SerialName("node_id")
    val nodeId: NodeId7,
    @SerialName("avatar_url")
    val avatarUrl: AvatarUrl5,
    @SerialName("gravatar_id")
    val gravatarId: GravatarId5,
    val url: Url9,
    @SerialName("html_url")
    val htmlUrl: HtmlUrl7,
    @SerialName("followers_url")
    val followersUrl: FollowersUrl5,
    @SerialName("following_url")
    val followingUrl: FollowingUrl5,
    @SerialName("gists_url")
    val gistsUrl: GistsUrl5,
    @SerialName("starred_url")
    val starredUrl: StarredUrl5,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: SubscriptionsUrl5,
    @SerialName("organizations_url")
    val organizationsUrl: OrganizationsUrl5,
    @SerialName("repos_url")
    val reposUrl: ReposUrl5,
    @SerialName("events_url")
    val eventsUrl: EventsUrl5,
    @SerialName("received_events_url")
    val receivedEventsUrl: ReceivedEventsUrl5,
    val type: Type7,
    @SerialName("site_admin")
    val siteAdmin: SiteAdmin5,
    @SerialName("starred_at")
    val starredAt: StarredAt4,
)

@Serializable
internal data class Name6(
    val type: List<String>,
)

@Serializable
internal data class Email5(
    val type: List<String>,
)

@Serializable
internal data class Login5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Id8(
    val type: String,
    val examples: List<Long>,
)

@Serializable
internal data class NodeId7(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class AvatarUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class GravatarId5(
    val type: List<String>,
    val examples: List<String>,
)

@Serializable
internal data class Url9(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class HtmlUrl7(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowersUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class FollowingUrl5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class GistsUrl5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class StarredUrl5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SubscriptionsUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class OrganizationsUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class ReposUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class EventsUrl5(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class ReceivedEventsUrl5(
    val type: String,
    val format: String,
    val examples: List<String>,
)

@Serializable
internal data class Type7(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class SiteAdmin5(
    val type: String,
)

@Serializable
internal data class StarredAt4(
    val type: String,
    val examples: List<String>,
)

@Serializable
internal data class Truncated3(
    val type: String,
)
