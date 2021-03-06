package com.battlelancer.seriesguide.ui.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.traktapi.TraktCredentials;
import com.battlelancer.seriesguide.ui.BaseNowFragment;
import com.battlelancer.seriesguide.ui.MoviesActivity;
import com.battlelancer.seriesguide.ui.ShowsActivity;
import com.battlelancer.seriesguide.ui.shows.NowAdapter;
import com.battlelancer.seriesguide.ui.streams.HistoryActivity;
import com.battlelancer.seriesguide.util.Utils;
import java.util.List;

/**
 * Displays recently watched movies, today's releases and recent watches from trakt friends (if
 * connected to trakt).
 */
public class MoviesNowFragment extends BaseNowFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        emptyView.setText(R.string.now_movies_empty);
        new ViewModelProvider(requireActivity()).get(MoviesActivityViewModel.class)
                .getScrollTabToTopLiveData()
                .observe(getViewLifecycleOwner(), event -> {
                    if (event != null
                            && event.getTabPosition() == MoviesActivity.TAB_POSITION_NOW) {
                        recyclerView.smoothScrollToPosition(0);
                    }
                });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // define dataset
        adapter = new MoviesNowAdapter(getContext(), itemClickListener);

        super.onActivityCreated(adapter, recentlyTraktCallbacks, traktFriendsHistoryCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // guard against not attached to activity
        if (!isAdded()) {
            return;
        }

        inflater.inflate(R.menu.movies_now_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_action_movies_now_refresh) {
            refreshStream();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void refreshStream() {
        showProgressBar(true);
        showError(null);

        // user might get disconnected during our life-time,
        // so properly clean up old loaders so they won't interfere
        if (TraktCredentials.get(getActivity()).hasCredentials()) {
            isLoadingRecentlyWatched = true;
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            loaderManager.restartLoader(MoviesActivity.NOW_TRAKT_USER_LOADER_ID, null,
                    recentlyTraktCallbacks);
            isLoadingFriends = true;
            loaderManager.restartLoader(ShowsActivity.NOW_TRAKT_FRIENDS_LOADER_ID, null,
                    traktFriendsHistoryCallbacks);
        } else {
            // destroy trakt loaders and remove any shown error message
            destroyLoaderIfExists(MoviesActivity.NOW_TRAKT_USER_LOADER_ID);
            destroyLoaderIfExists(MoviesActivity.NOW_TRAKT_FRIENDS_LOADER_ID);
            showError(null);
        }
    }

    private NowAdapter.ItemClickListener itemClickListener = new NowAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            NowAdapter.NowItem item = adapter.getItem(position);
            if (item == null) {
                return;
            }

            // more history link?
            if (item.type == NowAdapter.ItemType.MORE_LINK) {
                startActivity(new Intent(getActivity(), HistoryActivity.class).putExtra(
                        HistoryActivity.InitBundle.HISTORY_TYPE,
                        HistoryActivity.DISPLAY_MOVIE_HISTORY));
                return;
            }

            if (item.movieTmdbId == null) {
                return;
            }

            // display movie details
            Intent i = MovieDetailsActivity.intentMovie(getActivity(), item.movieTmdbId);

            // simple scale up animation as there are no images
            Utils.startActivityWithAnimation(getActivity(), i, view);
        }
    };

    private LoaderManager.LoaderCallbacks<TraktRecentMovieHistoryLoader.Result>
            recentlyTraktCallbacks
            = new LoaderManager.LoaderCallbacks<TraktRecentMovieHistoryLoader.Result>() {
        @Override
        public Loader<TraktRecentMovieHistoryLoader.Result> onCreateLoader(int id, Bundle args) {
            return new TraktRecentMovieHistoryLoader(getActivity());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<TraktRecentMovieHistoryLoader.Result> loader,
                TraktRecentMovieHistoryLoader.Result data) {
            if (!isAdded()) {
                return;
            }
            adapter.setRecentlyWatched(data.items);
            isLoadingRecentlyWatched = false;
            showProgressBar(false);
            showError(data.errorText);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<TraktRecentMovieHistoryLoader.Result> loader) {
            if (!isVisible()) {
                return;
            }
            // clear existing data
            adapter.setRecentlyWatched(null);
        }
    };

    private LoaderManager.LoaderCallbacks<List<NowAdapter.NowItem>> traktFriendsHistoryCallbacks
            = new LoaderManager.LoaderCallbacks<List<NowAdapter.NowItem>>() {
        @Override
        public Loader<List<NowAdapter.NowItem>> onCreateLoader(int id, Bundle args) {
            return new TraktFriendsMovieHistoryLoader(getActivity());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<NowAdapter.NowItem>> loader,
                List<NowAdapter.NowItem> data) {
            if (!isAdded()) {
                return;
            }
            adapter.setFriendsRecentlyWatched(data);
            isLoadingFriends = false;
            showProgressBar(false);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<NowAdapter.NowItem>> loader) {
            if (!isVisible()) {
                return;
            }
            // clear existing data
            adapter.setFriendsRecentlyWatched(null);
        }
    };
}
