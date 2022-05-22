package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKUserFull(
    @SerializedName("id")
    val id: Long,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("first_name_nom")
    val firstNameNom: String? = null,
    @SerializedName("first_name_gen")
    val firstNameGen: String? = null,
    @SerializedName("first_name_dat")
    val firstNameDat: String? = null,
    @SerializedName("first_name_acc")
    val firstNameAcc: String? = null,
    @SerializedName("first_name_ins")
    val firstNameIns: String? = null,
    @SerializedName("first_name_abl")
    val firstNameAbl: String? = null,
    @SerializedName("last_name_nom")
    val lastNameNom: String? = null,
    @SerializedName("last_name_gen")
    val lastNameGen: String? = null,
    @SerializedName("last_name_dat")
    val lastNameDat: String? = null,
    @SerializedName("last_name_acc")
    val lastNameAcc: String? = null,
    @SerializedName("last_name_ins")
    val lastNameIns: String? = null,
    @SerializedName("last_name_abl")
    val lastNameAbl: String? = null,
    @SerializedName("bdate")
    val bdate: String? = null,
    @SerializedName("photo_100")
    val photo100: String? = null,
    @SerializedName("photo_200")
    val photo200: String? = null,
//    @SerializedName("photo_max")
//    val photoMax: String? = null,
//    @SerializedName("photo_200_orig")
//    val photo200Orig: String? = null,
//    @SerializedName("photo_400_orig")
//    val photo400Orig: String? = null,
    @SerializedName("photo_max_orig")
    val photoMaxOrig: String? = null,
    @SerializedName("sex")
    val sex: VKSex? = null
)

/*
data class UsersUserFull(
    @SerializedName("id")
    val id: UserId,
    @SerializedName("first_name_nom")
    val firstNameNom: String? = null,
    @SerializedName("first_name_gen")
    val firstNameGen: String? = null,
    @SerializedName("first_name_dat")
    val firstNameDat: String? = null,
    @SerializedName("first_name_acc")
    val firstNameAcc: String? = null,
    @SerializedName("first_name_ins")
    val firstNameIns: String? = null,
    @SerializedName("first_name_abl")
    val firstNameAbl: String? = null,
    @SerializedName("last_name_nom")
    val lastNameNom: String? = null,
    @SerializedName("last_name_gen")
    val lastNameGen: String? = null,
    @SerializedName("last_name_dat")
    val lastNameDat: String? = null,
    @SerializedName("last_name_acc")
    val lastNameAcc: String? = null,
    @SerializedName("last_name_ins")
    val lastNameIns: String? = null,
    @SerializedName("last_name_abl")
    val lastNameAbl: String? = null,
    @SerializedName("nickname")
    val nickname: String? = null,
    @SerializedName("maiden_name")
    val maidenName: String? = null,
    @SerializedName("contact_name")
    val contactName: String? = null,
    @SerializedName("domain")
    val domain: String? = null,
    @SerializedName("bdate")
    val bdate: String? = null,
    @SerializedName("city")
    val city: BaseCity? = null,
    @SerializedName("country")
    val country: BaseCountry? = null,
    @SerializedName("timezone")
    val timezone: Float? = null,
    @SerializedName("owner_state")
    val ownerState: OwnerState? = null,
    @SerializedName("photo_200")
    val photo200: String? = null,
    @SerializedName("photo_max")
    val photoMax: String? = null,
    @SerializedName("photo_200_orig")
    val photo200Orig: String? = null,
    @SerializedName("photo_400_orig")
    val photo400Orig: String? = null,
    @SerializedName("photo_max_orig")
    val photoMaxOrig: String? = null,
    @SerializedName("photo_id")
    val photoId: String? = null,
    @SerializedName("has_photo")
    val hasPhoto: BaseBoolInt? = null,
    @SerializedName("has_mobile")
    val hasMobile: BaseBoolInt? = null,
    @SerializedName("is_friend")
    val isFriend: BaseBoolInt? = null,
    @SerializedName("wall_comments")
    val wallComments: BaseBoolInt? = null,
    @SerializedName("can_post")
    val canPost: BaseBoolInt? = null,
    @SerializedName("can_see_all_posts")
    val canSeeAllPosts: BaseBoolInt? = null,
    @SerializedName("can_see_audio")
    val canSeeAudio: BaseBoolInt? = null,
    @SerializedName("type")
    val type: UsersUserType? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("skype")
    val skype: String? = null,
    @SerializedName("facebook")
    val facebook: String? = null,
    @SerializedName("facebook_name")
    val facebookName: String? = null,
    @SerializedName("twitter")
    val twitter: String? = null,
    @SerializedName("livejournal")
    val livejournal: String? = null,
    @SerializedName("instagram")
    val instagram: String? = null,
    @SerializedName("test")
    val test: BaseBoolInt? = null,
    @SerializedName("video_live")
    val videoLive: VideoLiveInfo? = null,
    @SerializedName("is_video_live_notifications_blocked")
    val isVideoLiveNotificationsBlocked: BaseBoolInt? = null,
    @SerializedName("is_service")
    val isService: Boolean? = null,
    @SerializedName("service_description")
    val serviceDescription: String? = null,
    @SerializedName("photo_rec")
    val photoRec: String? = null,
    @SerializedName("photo_medium")
    val photoMedium: String? = null,
    @SerializedName("photo_medium_rec")
    val photoMediumRec: String? = null,
    @SerializedName("photo")
    val photo: String? = null,
    @SerializedName("photo_big")
    val photoBig: String? = null,
    @SerializedName("photo_400")
    val photo400: String? = null,
    @SerializedName("photo_max_size")
    val photoMaxSize: PhotosPhoto? = null,
    @SerializedName("language")
    val language: String? = null,
    @SerializedName("stories_archive_count")
    val storiesArchiveCount: Int? = null,
    @SerializedName("has_unseen_stories")
    val hasUnseenStories: Boolean? = null,
    @SerializedName("wall_default")
    val wallDefault: UsersUserFull.WallDefault? = null,
    @SerializedName("can_call")
    val canCall: Boolean? = null,
    @SerializedName("can_call_from_group")
    val canCallFromGroup: Boolean? = null,
    @SerializedName("can_see_wishes")
    val canSeeWishes: Boolean? = null,
    @SerializedName("can_see_gifts")
    val canSeeGifts: BaseBoolInt? = null,
    @SerializedName("interests")
    val interests: String? = null,
    @SerializedName("books")
    val books: String? = null,
    @SerializedName("tv")
    val tv: String? = null,
    @SerializedName("quotes")
    val quotes: String? = null,
    @SerializedName("about")
    val about: String? = null,
    @SerializedName("games")
    val games: String? = null,
    @SerializedName("movies")
    val movies: String? = null,
    @SerializedName("activities")
    val activities: String? = null,
    @SerializedName("music")
    val music: String? = null,
    @SerializedName("can_write_private_message")
    val canWritePrivateMessage: BaseBoolInt? = null,
    @SerializedName("can_send_friend_request")
    val canSendFriendRequest: BaseBoolInt? = null,
    @SerializedName("can_be_invited_group")
    val canBeInvitedGroup: Boolean? = null,
    @SerializedName("mobile_phone")
    val mobilePhone: String? = null,
    @SerializedName("home_phone")
    val homePhone: String? = null,
    @SerializedName("site")
    val site: String? = null,
    @SerializedName("status_audio")
    val statusAudio: AudioAudio? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("activity")
    val activity: String? = null,
    @SerializedName("last_seen")
    val lastSeen: UsersLastSeen? = null,
    @SerializedName("exports")
    val exports: UsersExports? = null,
    @SerializedName("crop_photo")
    val cropPhoto: BaseCropPhoto? = null,
    @SerializedName("followers_count")
    val followersCount: Int? = null,
    @SerializedName("video_live_level")
    val videoLiveLevel: Int? = null,
    @SerializedName("video_live_count")
    val videoLiveCount: Int? = null,
    @SerializedName("clips_count")
    val clipsCount: Int? = null,
    @SerializedName("blacklisted")
    val blacklisted: BaseBoolInt? = null,
    @SerializedName("blacklisted_by_me")
    val blacklistedByMe: BaseBoolInt? = null,
    @SerializedName("is_favorite")
    val isFavorite: BaseBoolInt? = null,
    @SerializedName("is_hidden_from_feed")
    val isHiddenFromFeed: BaseBoolInt? = null,
    @SerializedName("common_count")
    val commonCount: Int? = null,
    @SerializedName("occupation")
    val occupation: UsersOccupation? = null,
    @SerializedName("career")
    val career: List<UsersCareer>? = null,
    @SerializedName("military")
    val military: List<UsersMilitary>? = null,
    @SerializedName("university")
    val university: Int? = null,
    @SerializedName("university_name")
    val universityName: String? = null,
    @SerializedName("university_group_id")
    val universityGroupId: Int? = null,
    @SerializedName("faculty")
    val faculty: Int? = null,
    @SerializedName("faculty_name")
    val facultyName: String? = null,
    @SerializedName("graduation")
    val graduation: Int? = null,
    @SerializedName("education_form")
    val educationForm: String? = null,
    @SerializedName("education_status")
    val educationStatus: String? = null,
    @SerializedName("home_town")
    val homeTown: String? = null,
    @SerializedName("relation")
    val relation: UsersUserRelation? = null,
    @SerializedName("relation_partner")
    val relationPartner: UsersUserMin? = null,
    @SerializedName("personal")
    val personal: UsersPersonal? = null,
    @SerializedName("universities")
    val universities: List<UsersUniversity>? = null,
    @SerializedName("schools")
    val schools: List<UsersSchool>? = null,
    @SerializedName("relatives")
    val relatives: List<UsersRelative>? = null,
    @SerializedName("is_subscribed_podcasts")
    val isSubscribedPodcasts: Boolean? = null,
    @SerializedName("can_subscribe_podcasts")
    val canSubscribePodcasts: Boolean? = null,
    @SerializedName("can_subscribe_posts")
    val canSubscribePosts: Boolean? = null,
    @SerializedName("counters")
    val counters: UsersUserCounters? = null,
    @SerializedName("access_key")
    val accessKey: String? = null,
    @SerializedName("can_upload_doc")
    val canUploadDoc: BaseBoolInt? = null,
    @SerializedName("hash")
    val hash: String? = null,
    @SerializedName("is_no_index")
    val isNoIndex: Boolean? = null,
    @SerializedName("contact_id")
    val contactId: Int? = null,
    @SerializedName("is_message_request")
    val isMessageRequest: Boolean? = null,
    @SerializedName("descriptions")
    val descriptions: List<String>? = null,
    @SerializedName("lists")
    val lists: List<Int>? = null,
    @SerializedName("sex")
    val sex: BaseSex? = null,
    @SerializedName("screen_name")
    val screenName: String? = null,
    @SerializedName("photo_50")
    val photo50: String? = null,
    @SerializedName("photo_100")
    val photo100: String? = null,
    @SerializedName("online_info")
    val onlineInfo: UsersOnlineInfo? = null,
    @SerializedName("online")
    val online: BaseBoolInt? = null,
    @SerializedName("online_mobile")
    val onlineMobile: BaseBoolInt? = null,
    @SerializedName("online_app")
    val onlineApp: Int? = null,
    @SerializedName("verified")
    val verified: BaseBoolInt? = null,
    @SerializedName("trending")
    val trending: BaseBoolInt? = null,
    @SerializedName("friend_status")
    val friendStatus: FriendsFriendStatusStatus? = null,
    @SerializedName("mutual")
    val mutual: FriendsRequestsMutual? = null,
    @SerializedName("deactivated")
    val deactivated: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("hidden")
    val hidden: Int? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("can_access_closed")
    val canAccessClosed: Boolean? = null,
    @SerializedName("is_closed")
    val isClosed: Boolean? = null
)
*/
