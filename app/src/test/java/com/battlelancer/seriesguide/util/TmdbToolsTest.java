package com.battlelancer.seriesguide.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import com.battlelancer.seriesguide.settings.TmdbSettings;
import com.uwetrottmann.tmdb2.entities.Genre;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created on 2020-06-15.
 *
 * @author Stefan Breetveld
 */
@RunWith(PowerMockRunner.class)
public class TmdbToolsTest {

    private static final String DEFAULT_BASE_URL = "https://image.tmdb.org/t/p/";

    @Test
    public void buildMovieUrl() {
        // Arrange
        final int tmdbMovieID = 1;
        final String expectedUrl = "https://www.themoviedb.org/movie/" + tmdbMovieID;
        // Act
        final String result = TmdbTools.buildMovieUrl(tmdbMovieID);
        // Assert
        assertEquals("Movie URLs should be the same", expectedUrl, result);
    }

    @Test
    public void buildPersonUrl() {
        // Arrange
        final int tmdbPersonID = 1;
        final String expectedUrl = "https://www.themoviedb.org/person/" + tmdbPersonID;
        // Act
        final String result = TmdbTools.buildPersonUrl(tmdbPersonID);
        // Assert
        assertEquals("Movie URLs should be the same", expectedUrl, result);
    }

    @Test
    @PrepareOnlyThisForTest(TmdbSettings.class)
    public void buildProfileImageUrlOriginal() throws Exception {
        // Arrange
        final String path = "/test-path";
        TmdbTools.ProfileImageSize size = TmdbTools.ProfileImageSize.ORIGINAL;

        PowerMockito.mockStatic(TmdbSettings.class);
        PowerMockito.doReturn(DEFAULT_BASE_URL).when(TmdbSettings.class, "getImageBaseUrl", any());

        final String expectedUrl = DEFAULT_BASE_URL + size + path;
        // Act
        final String result = TmdbTools
                .buildProfileImageUrl(null, path, size);

        // Assert
        assertEquals("Profile image urls don't match", expectedUrl, result);
    }

    @Test
    @PrepareOnlyThisForTest(TmdbSettings.class)
    public void buildProfileImageUrlW45() throws Exception {
        // Arrange
        final String path = "/test-path";
        TmdbTools.ProfileImageSize size = TmdbTools.ProfileImageSize.W45;

        PowerMockito.mockStatic(TmdbSettings.class);
        PowerMockito.doReturn(DEFAULT_BASE_URL).when(TmdbSettings.class, "getImageBaseUrl", any());

        final String expectedUrl = DEFAULT_BASE_URL + size + path;
        // Act
        final String result = TmdbTools
                .buildProfileImageUrl(null, path, size);

        // Assert
        assertEquals("Profile image urls don't match", expectedUrl, result);
    }

    @Test
    @PrepareOnlyThisForTest(TmdbSettings.class)
    public void buildProfileImageUrlW185() throws Exception {
        // Arrange
        final String path = "/test-path";
        TmdbTools.ProfileImageSize size = TmdbTools.ProfileImageSize.W185;

        PowerMockito.mockStatic(TmdbSettings.class);
        PowerMockito.doReturn(DEFAULT_BASE_URL).when(TmdbSettings.class, "getImageBaseUrl", any());

        final String expectedUrl = DEFAULT_BASE_URL + size + path;
        // Act
        final String result = TmdbTools
                .buildProfileImageUrl(null, path, size);

        // Assert
        assertEquals("Profile image urls don't match", expectedUrl, result);
    }

    @Test
    @PrepareOnlyThisForTest(TmdbSettings.class)
    public void buildProfileImageUrlH632() throws Exception {
        // Arrange
        final String path = "/test-path";
        TmdbTools.ProfileImageSize size = TmdbTools.ProfileImageSize.H632;

        PowerMockito.mockStatic(TmdbSettings.class);
        PowerMockito.doReturn(DEFAULT_BASE_URL).when(TmdbSettings.class, "getImageBaseUrl", any());

        final String expectedUrl = DEFAULT_BASE_URL + size + path;
        // Act
        final String result = TmdbTools
                .buildProfileImageUrl(null, path, size);

        // Assert
        assertEquals("Profile image urls don't match", expectedUrl, result);
    }

    @Test
    public void buildGenresStringNullGenres() {
        // Arrange
        final List<Genre> genres = null;
        // Act
        final String result = TmdbTools.buildGenresString(genres);

        // Assert
        assertNull("Genre list should be null", result);
    }

    @Test
    public void buildGenresStringZeroGenres() {
        // Arrange
        final List<Genre> genres = new ArrayList<>();
        // Act
        final String result = TmdbTools.buildGenresString(genres);

        // Assert
        assertNull("Genre list should be null", result);
    }

    @Test
    public void buildGenresStringOneGenre() {
        // Arrange
        final Genre genre1 = new Genre();
        genre1.name = "test-genre1";
        final List<Genre> genres = Arrays.asList(genre1);
        final String expected = "test-genre1";
        // Act
        final String result = TmdbTools.buildGenresString(genres);

        // Assert
        assertEquals("Genre lists should match", expected, result);
    }

    @Test
    public void buildGenresStringTwoGenres() {
        // Arrange
        final Genre genre1 = new Genre();
        genre1.name = "test-genre1";
        final Genre genre2 = new Genre();
        genre2.name = "test-genre2";
        final List<Genre> genres = Arrays.asList(genre1, genre2);
        final String expected = "test-genre1, test-genre2";
        // Act
        final String result = TmdbTools.buildGenresString(genres);

        // Assert
        assertEquals("Genre lists should match", expected, result);
    }
}