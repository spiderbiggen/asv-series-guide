package com.battlelancer.seriesguide.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

import android.text.TextUtils;
import com.battlelancer.seriesguide.settings.DisplaySettings;
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
public class TextToolsTest {

    @Test
    public void getEpisodeNumberInternalNumberFormatDefault() {
        // Arrange
        final String format = DisplaySettings.NUMBERFORMAT_DEFAULT;
        final int season = 1;
        final int episode = 1;
        final String expected = String.format("%dx%02d", season, episode);

        // Act
        String result = TextTools.getEpisodeNumberInternal(season, episode, format);

        // Assert
        assertEquals("Season strings should match", expected, result);
    }

    @Test
    public void getEpisodeNumberInternalNumberFormatEnglishLower() {
        // Arrange
        final String format = DisplaySettings.NUMBERFORMAT_ENGLISHLOWER;
        final int season = 1;
        final int episode = 1;
        final String expected = String.format("s%02de%02d", season, episode);

        // Act
        String result = TextTools.getEpisodeNumberInternal(season, episode, format);

        // Assert
        assertEquals("Season strings should match", expected, result);
    }

    @Test
    public void getEpisodeNumberInternalNumberFormatNull() {
        // Arrange
        final String format = null;
        final int season = 1;
        final int episode = 1;
        final String expected = String.format("S%02dE%02d", season, episode);

        // Act
        String result = TextTools.getEpisodeNumberInternal(season, episode, format);

        // Assert
        assertEquals("Season strings should match", expected, result);
    }

    @Test
    public void getEpisodeNumberInternalNumberFormatEmpty() {
        // Arrange
        final String format = "";
        final int season = 1;
        final int episode = 1;
        final String expected = String.format("S%02dE%02d", season, episode);

        // Act
        String result = TextTools.getEpisodeNumberInternal(season, episode, format);

        // Assert
        assertEquals("Season strings should match", expected, result);
    }

    @Test
    public void splitAndKitTVDBStringsNull() {
        // Arrange
        final String input = null;
        final String expected = "";

        // Act
        String result = TextTools.splitAndKitTVDBStrings(input);

        // Assert
        assertEquals("Split strings should be equal", expected, result);
    }

    @Test
    public void splitAndKitTVDBStringsEmpty() {
        // Arrange
        final String input = "";
        final String expected = "";

        // Act
        String result = TextTools.splitAndKitTVDBStrings(input);

        // Assert
        assertEquals("Split strings should be equal", expected, result);
    }

    @Test
    public void splitAndKitTVDBStringsNoSplit() {
        // Arrange
        final String input = "test";
        final String expected = "test";

        // Act
        String result = TextTools.splitAndKitTVDBStrings(input);

        // Assert
        assertEquals("Split strings should be equal", expected, result);
    }

    @Test
    public void splitAndKitTVDBStringsSingleSplit() {
        // Arrange
        final String input = "test|tes";
        final String expected = "test, tes";

        // Act
        String result = TextTools.splitAndKitTVDBStrings(input);

        // Assert
        assertEquals("Split strings should be equal", expected, result);
    }

    @Test
    public void splitAndKitTVDBStringsTwoSplit() {
        // Arrange
        final String input = "test|tes|te";
        final String expected = "test, tes, te";

        // Act
        String result = TextTools.splitAndKitTVDBStrings(input);

        // Assert
        assertEquals("Split strings should be equal", expected, result);
    }

    @Test
    public void mendTvdBStringsNull() {
        // Arrange
        final List<String> input = null;
        final String expected = "";

        // Act
        String result = TextTools.mendTvdbStrings(input);

        // Assert
        assertEquals("Mended strings should be equal", expected, result);
    }

    @Test
    public void mendTvdbStringsEmpty() {
        // Arrange
        final List<String> input = new ArrayList<>();
        final String expected = "";

        // Act
        String result = TextTools.mendTvdbStrings(input);

        // Assert
        assertEquals("Mended strings should be equal", expected, result);
    }

    @Test
    public void mendTvdbStringsNoSplit() {
        // Arrange
        final List<String> input = Arrays.asList("test");
        final String expected = "test";

        // Act
        String result = TextTools.mendTvdbStrings(input);

        // Assert
        assertEquals("Mended strings should be equal", expected, result);
    }

    @Test
    public void mendTvdbbtringsSingleMend() {
        // Arrange
        final List<String> input = Arrays.asList("test", "tes");
        final String expected = "test|tes";

        // Act
        String result = TextTools.mendTvdbStrings(input);

        // Assert
        assertEquals("Mended strings should be equal", expected, result);
    }

    @Test
    public void mendTvdbStringsDoubleMend() {
        // Arrange
        final List<String> input = Arrays.asList("test", "tes", "te");
        final String expected = "test|tes|te";

        // Act
        String result = TextTools.mendTvdbStrings(input);

        // Assert
        assertEquals("Split strings should be equal", expected, result);
    }

    @Test
    @PrepareOnlyThisForTest(TextUtils.class)
    public void dotSeparateNullNull() throws Exception {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", isNull());
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", eq(""));
        // Arrange
        final String expected = "";

        // Act
        String result = TextTools.dotSeparate(null, null);

        // Assert
        assertEquals("Dot seperated strings should be equal", expected, result);
    }

    @Test
    @PrepareOnlyThisForTest(TextUtils.class)
    public void dotSeparateRightNull() throws Exception {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", isNull());
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", eq(""));
        // Arrange
        final String left = "testLeft";
        final String expected = "testLeft";

        // Act
        String result = TextTools.dotSeparate(left, null);

        // Assert
        assertEquals("Dot seperated strings should be equal", expected, result);
    }

    @Test
    @PrepareOnlyThisForTest(TextUtils.class)
    public void dotSeparateLeftNull() throws Exception {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", isNull());
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", eq(""));
        // Arrange
        final String right = "testRight";
        final String expected = "testRight";

        // Act
        String result = TextTools.dotSeparate(null, right);

        // Assert
        assertEquals("Dot seperated strings should be equal", expected, result);
    }


    @Test
    @PrepareOnlyThisForTest(TextUtils.class)
    public void dotSeparate() throws Exception {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", isNull());
        PowerMockito.doReturn(true).when(TextUtils.class, "isEmpty", eq(""));
        // Arrange
        final String left = "testLeft";
        final String right = "testRight";
        final String expected = "testLeft · testRight";

        // Act
        String result = TextTools.dotSeparate(left, right);

        // Assert
        assertEquals("Dot seperated strings should be equal", expected, result);
    }
}