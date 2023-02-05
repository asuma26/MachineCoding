package in.wynk.targeting.core.dao.entity.mongo.music;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAttributes implements Serializable  {
    private static final long serialVersionUID = 7594104498971791312L;
    @SerializedName(value = "app_language")
    private List<String> appLanguage;
    @SerializedName(value = "circle")
    private List<String> circle;
    @SerializedName(value = "has_manually_set_language")
    private List<String> hasManuallySetLanguage;
    @SerializedName(value = "registration_date")
    private List<String> registrationDate;
    @SerializedName(value = "selected_languages")
    private List<String> selectedLanguages;
    @SerializedName(value = "core_selected_languages")
    private List<String> coreSelectedLanguages;
    @SerializedName(value = "last_app_open_timestamp")
    private List<String> lastAppOpenTimestamp;
    @SerializedName(value = "top_artists")
    private List<String> topArtists;
    @SerializedName(value = "top_lyricists")
    private List<String> topLyricists;
    @SerializedName(value = "top_composers")
    private List<String> topComposers;
    @SerializedName(value = "top_genre")
    private List<String> topGenre;
    @SerializedName(value = "hcl")
    private List<String> hcl;
    @SerializedName(value = "mcl")
    private List<String> mcl;
    @SerializedName(value = "lcl")
    private List<String> lcl;
    @SerializedName(value = "tcl")
    private List<String> tcl;
    @SerializedName(value = "current_platform")
    private List<String> currentPlatform;
    @SerializedName(value = "current_os")
    private List<String> currentOs;
    @SerializedName(value = "build_no")
    private List<String> buildNo;
    @SerializedName(value = "local_mp3_count")
    private List<String> localMp3count;
    @SerializedName(value = "download_song_count")
    private List<String> downloadSongCount;
    @SerializedName(value = "last_song_played_timestamp")
    private List<String> lastSongPlayedTimestamp;
    @SerializedName(value = "token")
    private List<String> token;
    @SerializedName(value = "selected_play_list")
    private List<String> selectedPlaylist;
    @SerializedName(value = "content_langs")
    private List<String> contentLangs;
    @SerializedName(value = "user_registered")
    private List<String> userRegistered;
    @SerializedName(value = "lifetime_unique_stream")
    private List<String> lifetimeUniqueStreams;
    @SerializedName(value = "operator")
    private List<String> operator;
    @SerializedName(value = "inactive_days_stream")
    private List<String> inactiveDaysStream;
    @SerializedName(value = "inactive_days_app")
    private List<String> inactiveDaysApp;
    @SerializedName(value = "artist_followed")
    private List<String> artistFollowed;
    @SerializedName(value = "age_on_app")
    private List<String> ageOnApp;
    @SerializedName(value = "apps_used")
    private List<String> appsUsed;
    @SerializedName(value = "subscription_id")
    private List<String> subscriptionId;
    @SerializedName(value = "user_subscribed")
    private List<String> userSubscribed;
    @SerializedName(value = "day_of_week")
    private List<String> dayOfWeek;
    @SerializedName(value = "hour_of_day")
    private List<String> hourOfDay;
    @SerializedName(value = "custom_segments")
    private List<String> customSegments;
}
