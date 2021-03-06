package com.battlelancer.seriesguide.ui.shows;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.jobs.episodes.EpisodeWatchedJob;
import com.battlelancer.seriesguide.provider.SeriesGuideContract;
import com.battlelancer.seriesguide.traktapi.TraktCredentials;
import com.battlelancer.seriesguide.ui.BaseMessageActivity;
import com.battlelancer.seriesguide.ui.BaseNowFragment;
import com.battlelancer.seriesguide.ui.ShowsActivity;
import com.battlelancer.seriesguide.ui.episodes.EpisodesActivity;
import com.battlelancer.seriesguide.ui.search.AddShowDialogFragment;
import com.battlelancer.seriesguide.ui.streams.HistoryActivity;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Displays recently watched episodes and recent episodes from friends (if connected to trakt).
 */
public class ShowsNowFragment extends BaseNowFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        emptyView.setText(R.string.now_empty);
        new ViewModelProvider(requireActivity()).get(ShowsActivityViewModel.class)
                .getScrollTabToTopLiveData()
                .observe(getViewLifecycleOwner(), tabPosition -> {
                    if (tabPosition != null
                            && tabPosition == ShowsActivity.InitBundle.INDEX_TAB_NOW) {
                        recyclerView.smoothScrollToPosition(0);
                    }
                });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // define dataset
        adapter = new NowAdapter(getActivity(), itemClickListener);

        super.onActivityCreated(adapter, recentlyTraktCallbacks, traktFriendsHistoryCallbacks);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        /*
          Init recently watched loader here the earliest.
          So we can restart them if they already exist to ensure up to date data (the loaders do
          not react to database changes themselves) and avoid loading data twice in a row.
         */
        if (!TraktCredentials.get(getActivity()).hasCredentials()) {
            isLoadingRecentlyWatched = true;
            initAndMaybeRestartLoader(ShowsActivity.NOW_RECENTLY_LOADER_ID, recentlyLocalCallbacks);
        }
    }

    /**
     * Init the loader. If the loader already exists, will restart it (the default behavior of init
     * would be to get the last loaded data).
     */
    private <D> void initAndMaybeRestartLoader(int loaderId,
            LoaderManager.LoaderCallbacks<D> callbacks) {
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        boolean isLoaderExists = loaderManager.getLoader(loaderId) != null;
        loaderManager.initLoader(loaderId, null, callbacks);
        if (isLoaderExists) {
            loaderManager.restartLoader(loaderId, null, callbacks);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // guard against not attached to activity
        if (!isAdded()) {
            return;
        }

        inflater.inflate(R.menu.shows_now_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_action_shows_now_refresh) {
            refreshStream();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void refreshStream() {
        showProgressBar(true);
        showError(null);

        // if connected to trakt, replace local history with trakt history, show friends history
        // user might get disconnected during our life-time,
        // so properly clean up old loaders so they won't interfere
        isLoadingRecentlyWatched = true;
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if (TraktCredentials.get(getActivity()).hasCredentials()) {
            destroyLoaderIfExists(ShowsActivity.NOW_RECENTLY_LOADER_ID);

            loaderManager.restartLoader(ShowsActivity.NOW_TRAKT_USER_LOADER_ID, null,
                    recentlyTraktCallbacks);
            isLoadingFriends = true;
            loaderManager.restartLoader(ShowsActivity.NOW_TRAKT_FRIENDS_LOADER_ID, null,
                    traktFriendsHistoryCallbacks);
        } else {
            // destroy trakt loaders and remove any shown error message
            destroyLoaderIfExists(ShowsActivity.NOW_TRAKT_USER_LOADER_ID);
            destroyLoaderIfExists(ShowsActivity.NOW_TRAKT_FRIENDS_LOADER_ID);
            showError(null);

            loaderManager.restartLoader(ShowsActivity.NOW_RECENTLY_LOADER_ID, null,
                    recentlyLocalCallbacks);
        }
    }



    /**
     * Starts an activity to display the given episode.
     */
    private void showDetails(View view, int episodeId) {
        Intent intent = new Intent();
        intent.setClass(requireContext(), EpisodesActivity.class);
        intent.putExtra(EpisodesActivity.EXTRA_EPISODE_TVDBID, episodeId);

        ActivityCompat.startActivity(requireContext(), intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle()
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventEpisodeTask(BaseMessageActivity.ServiceCompletedEvent event) {
        if (event.flagJob == null || !event.isSuccessful) {
            return; // no changes applied
        }
        if (!isAdded()) {
            return; // no longer added to activity
        }
        // reload recently watched if user set or unset an episode watched
        // however, if connected to trakt do not show local history
        if (event.flagJob instanceof EpisodeWatchedJob
                && !TraktCredentials.get(getActivity()).hasCredentials()) {
            isLoadingRecentlyWatched = true;
            LoaderManager.getInstance(this)
                    .restartLoader(ShowsActivity.NOW_RECENTLY_LOADER_ID, null,
                            recentlyLocalCallbacks);
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
                        HistoryActivity.DISPLAY_EPISODE_HISTORY));
                return;
            }

            // other actions need at least an episode TVDB id
            if (item.episodeTvdbId == null) {
                return;
            }

            // check if episode is in database
            Cursor query = requireActivity().getContentResolver()
                    .query(SeriesGuideContract.Episodes.buildEpisodeUri(item.episodeTvdbId),
                            new String[]{SeriesGuideContract.Episodes._ID}, null, null, null);
            if (query == null) {
                // query failed
                return;
            }
            if (query.getCount() == 1) {
                // episode in database: display details
                showDetails(view, item.episodeTvdbId);
            } else if (item.showTvdbId != null) {
                // episode missing: show likely not in database, suggest adding it
                AddShowDialogFragment
                        .show(requireContext(), getParentFragmentManager(), item.showTvdbId);
            }
            query.close();
        }
    };

    private LoaderManager.LoaderCallbacks<List<NowAdapter.NowItem>> recentlyLocalCallbacks
            = new LoaderManager.LoaderCallbacks<List<NowAdapter.NowItem>>() {
        @Override
        public Loader<List<NowAdapter.NowItem>> onCreateLoader(int id, Bundle args) {
            return new RecentlyWatchedLoader(getActivity());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<NowAdapter.NowItem>> loader,
                List<NowAdapter.NowItem> data) {
            if (!isAdded()) {
                return;
            }
            adapter.setRecentlyWatched(data);
            isLoadingRecentlyWatched = false;
            showProgressBar(false);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<NowAdapter.NowItem>> loader) {
            if (!isVisible()) {
                return;
            }
            // clear existing data
            adapter.setRecentlyWatched(null);
        }
    };

    private LoaderManager.LoaderCallbacks<TraktRecentEpisodeHistoryLoader.Result>
            recentlyTraktCallbacks
            = new LoaderManager.LoaderCallbacks<TraktRecentEpisodeHistoryLoader.Result>() {
        @Override
        public Loader<TraktRecentEpisodeHistoryLoader.Result> onCreateLoader(int id, Bundle args) {
            return new TraktRecentEpisodeHistoryLoader(getActivity());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<TraktRecentEpisodeHistoryLoader.Result> loader,
                TraktRecentEpisodeHistoryLoader.Result data) {
            if (!isAdded()) {
                return;
            }
            adapter.setRecentlyWatched(data.items);
            isLoadingRecentlyWatched = false;
            showProgressBar(false);
            showError(data.errorText);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<TraktRecentEpisodeHistoryLoader.Result> loader) {
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
            return new TraktFriendsEpisodeHistoryLoader(getActivity());
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
