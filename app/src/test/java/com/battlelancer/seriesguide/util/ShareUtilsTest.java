package com.battlelancer.seriesguide.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

import android.app.Activity;
import android.content.Context;
import com.battlelancer.seriesguide.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created on 2020-06-15.
 *
 * @author Stefan Breetveld
 */
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(ShareUtils.class)
public class ShareUtilsTest {

    String tmdbMovieTitle;
    int tmdbMovieID;
    String tvdbShowSlug;
    int tvdbShowID;
    int tvdbSeasonID;
    int tvdbEpisodeID;
    int seasonNumber;
    int episodeNumber;
    String episodeTitle;
    String showTitle;

    @Before
    public void setup() {
        // Arrange
        tmdbMovieTitle = "TestMovieSlug";
        tmdbMovieID = 1;
        tvdbShowSlug = "TestShowSlug";
        tvdbShowID = 1;
        tvdbSeasonID = 1;
        tvdbEpisodeID = 1;
        seasonNumber = 1;
        episodeNumber = 1;
        episodeTitle = "TestEpisode";
        showTitle = "TestShow";
    }

    @Test
    @PrepareOnlyThisForTest({TextTools.class, ShareUtils.class})
    public void testShareEpisode() throws Exception {
        // Arrange
        final String seasonXEpisode = String.format("%dx%02d", seasonNumber, episodeNumber);

        Activity activity = Mockito.mock(Activity.class);

        PowerMockito.spy(TextTools.class);
        PowerMockito.doReturn(seasonXEpisode)
                .when(TextTools.class, "getEpisodeNumber", any(), eq(seasonNumber),
                        eq(episodeNumber));
        PowerMockito.doReturn(episodeTitle)
                .when(TextTools.class, "getEpisodeTitle", any(), eq(episodeTitle),
                        eq(episodeNumber));

        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage = showTitle + " - " + seasonXEpisode + " " + episodeTitle
                + " https://www.thetvdb.com/series/" + tvdbShowSlug + "/episodes/" + tvdbEpisodeID;
        // Act
        ShareUtils
                .shareEpisode(activity, tvdbShowSlug, tvdbShowID, tvdbSeasonID, tvdbEpisodeID,
                        seasonNumber, episodeNumber, showTitle, episodeTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareEpisode(any(Activity.class), any(String.class), any(Integer.class),
                any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class),
                any(String.class), any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_episode));
    }

    @Test
    @PrepareOnlyThisForTest({TextTools.class, ShareUtils.class})
    public void testShareEpisodeNullSlug() throws Exception {
        // Arrange
        final String seasonXEpisode = String.format("%dx%02d", seasonNumber, episodeNumber);

        Activity activity = Mockito.mock(Activity.class);

        PowerMockito.spy(TextTools.class);
        PowerMockito.doReturn(seasonXEpisode)
                .when(TextTools.class, "getEpisodeNumber", any(), eq(seasonNumber),
                        eq(episodeNumber));
        PowerMockito.doReturn(episodeTitle)
                .when(TextTools.class, "getEpisodeTitle", any(), eq(episodeTitle),
                        eq(episodeNumber));

        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage = showTitle + " - " + seasonXEpisode + " " + episodeTitle
                + " https://www.thetvdb.com/?tab=episode&seriesid=" + tvdbShowID + "&seasonid="
                + tvdbSeasonID + "&id=" + tvdbEpisodeID;
        // Act
        ShareUtils
                .shareEpisode(activity, null, tvdbShowID, tvdbSeasonID, tvdbEpisodeID,
                        seasonNumber, episodeNumber, showTitle, episodeTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareEpisode(any(Activity.class), isNull(), any(Integer.class),
                any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class),
                any(String.class), any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_episode));
    }

    @Test
    @PrepareOnlyThisForTest({TextTools.class, ShareUtils.class})
    public void testShareEpisodeEmptySlug() throws Exception {
        // Arrange
        final String seasonXEpisode = String.format("%dx%02d", seasonNumber, episodeNumber);

        Activity activity = Mockito.mock(Activity.class);

        PowerMockito.spy(TextTools.class);
        PowerMockito.doReturn(seasonXEpisode)
                .when(TextTools.class, "getEpisodeNumber", any(), eq(seasonNumber),
                        eq(episodeNumber));
        PowerMockito.doReturn(episodeTitle)
                .when(TextTools.class, "getEpisodeTitle", any(), eq(episodeTitle),
                        eq(episodeNumber));

        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage = showTitle + " - " + seasonXEpisode + " " + episodeTitle
                + " https://www.thetvdb.com/?tab=episode&seriesid=" + tvdbShowID + "&seasonid="
                + tvdbSeasonID + "&id=" + tvdbEpisodeID;
        // Act
        ShareUtils
                .shareEpisode(activity, "", tvdbShowID, tvdbSeasonID, tvdbEpisodeID,
                        seasonNumber, episodeNumber, showTitle, episodeTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareEpisode(any(Activity.class), eq(""), any(Integer.class),
                any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class),
                any(String.class), any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_episode));
    }

    @Test
    public void testShareShow() throws Exception {
        // Arrange
        Activity activity = Mockito.mock(Activity.class);
        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage =
                showTitle + " https://www.thetvdb.com/series/" + tvdbShowSlug;

        // Act
        ShareUtils.shareShow(activity, tvdbShowSlug, tvdbShowID, showTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareShow(any(Activity.class), any(String.class), any(Integer.class),
                any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_show));
    }

    @Test
    public void testShareShowNullSlug() throws Exception {
        // Arrange
        Activity activity = Mockito.mock(Activity.class);
        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage =
                showTitle + " https://www.thetvdb.com/?tab=series&id=" + tvdbShowID;

        // Act
        ShareUtils.shareShow(activity, null, tvdbShowID, showTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareShow(any(Activity.class), isNull(), any(Integer.class),
                any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_show));
    }

    @Test
    public void testShareShowEmptySlug() throws Exception {
        // Arrange
        Activity activity = Mockito.mock(Activity.class);
        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage =
                showTitle + " https://www.thetvdb.com/?tab=series&id=" + tvdbShowID;

        // Act
        ShareUtils.shareShow(activity, "", tvdbShowID, showTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareShow(any(Activity.class), eq(""), any(Integer.class),
                any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_show));
    }

    @Test
    public void testShareMovie() throws Exception {
        // Arrange
        Activity activity = Mockito.mock(Activity.class);
        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage =
                tmdbMovieTitle + " https://www.themoviedb.org/movie/" + tmdbMovieID;
        // Act
        ShareUtils.shareMovie(activity, tmdbMovieID, tmdbMovieTitle);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareMovie(any(Activity.class), any(Integer.class), any(String.class));
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_movie));
    }

    @Test
    public void testShareMovieNullTitle() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        PowerMockito.spy(ShareUtils.class);
        PowerMockito.doNothing()
                .when(ShareUtils.class, "startShareIntentChooser", any(Context.class),
                        any(String.class), any(Integer.class));

        final String expectedMessage = null + " https://www.themoviedb.org/movie/" + tmdbMovieID;

        // Act
        ShareUtils.shareMovie(activity, tmdbMovieID, null);

        // Assert
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.shareMovie(any(Activity.class), eq(tmdbMovieID), isNull());
        PowerMockito.verifyStatic(ShareUtils.class);
        ShareUtils.startShareIntentChooser(any(Activity.class), eq(expectedMessage),
                eq(R.string.share_movie));
    }
}