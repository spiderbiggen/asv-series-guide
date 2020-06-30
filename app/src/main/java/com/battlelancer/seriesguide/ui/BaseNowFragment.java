package com.battlelancer.seriesguide.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.traktapi.TraktCredentials;
import com.battlelancer.seriesguide.ui.shows.NowAdapter;
import com.battlelancer.seriesguide.ui.shows.TraktRecentEpisodeHistoryLoader;
import com.battlelancer.seriesguide.util.ViewTools;
import com.uwetrottmann.seriesguide.widgets.EmptyViewSwipeRefreshLayout;
import java.util.List;

public abstract class BaseNowFragment extends Fragment {
    protected @BindView(R.id.swipeRefreshLayoutNow) EmptyViewSwipeRefreshLayout swipeRefreshLayout;

    protected @BindView(R.id.recyclerViewNow) RecyclerView recyclerView;
    protected @BindView(R.id.emptyViewNow) TextView emptyView;
    protected @BindView(R.id.containerSnackbar) View snackbar;
    protected @BindView(R.id.textViewSnackbar) TextView snackbarText;
    protected @BindView(R.id.buttonSnackbar) Button snackbarButton;

    protected Unbinder unbinder;
    protected NowAdapter adapter;
    protected boolean isLoadingRecentlyWatched;
    protected boolean isLoadingFriends;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setSwipeableChildren(R.id.scrollViewNow, R.id.recyclerViewNow);
        swipeRefreshLayout.setOnRefreshListener(this::refreshStream);
        swipeRefreshLayout.setProgressViewOffset(false,
                getResources().getDimensionPixelSize(
                        R.dimen.swipe_refresh_progress_bar_start_margin),
                getResources().getDimensionPixelSize(
                        R.dimen.swipe_refresh_progress_bar_end_margin));

        showError(null);
        snackbarButton.setText(R.string.refresh);
        snackbarButton.setOnClickListener(v -> refreshStream());

        // recycler view layout manager
        final int spanCount = getResources().getInteger(R.integer.grid_column_count);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter == null) {
                    return 1;
                }
                if (position >= adapter.getItemCount()) {
                    return 1;
                }
                // make headers and more links span all columns
                int type = adapter.getItem(position).type;
                return (type == NowAdapter.ItemType.HEADER || type == NowAdapter.ItemType.MORE_LINK)
                        ? spanCount : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        return view;
    }

    protected abstract void refreshStream();

    protected void showError(@Nullable String errorText) {
        boolean show = errorText != null;
        if (show) {
            snackbarText.setText(errorText);
        }
        if (snackbar.getVisibility() == (show ? View.VISIBLE : View.GONE)) {
            // already in desired state, avoid replaying animation
            return;
        }
        snackbar.startAnimation(AnimationUtils.loadAnimation(snackbar.getContext(),
                show ? R.anim.fade_in : R.anim.fade_out));
        snackbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onActivityCreated(
            NowAdapter adapter,
            LoaderManager.LoaderCallbacks<TraktRecentEpisodeHistoryLoader.Result> recentlyTraktCallbacks,
            LoaderManager.LoaderCallbacks<List<NowAdapter.NowItem>> traktFriendsHistoryCallbacks) {

        ViewTools.setSwipeRefreshLayoutColors(requireActivity().getTheme(), swipeRefreshLayout);

        // define dataset
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateEmptyState();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateEmptyState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateEmptyState();
            }
        });
        recyclerView.setAdapter(adapter);

        // if connected to trakt, replace local history with trakt history, show friends history
        if (TraktCredentials.get(getActivity()).hasCredentials()) {
            isLoadingRecentlyWatched = true;
            isLoadingFriends = true;
            showProgressBar(true);
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(ShowsActivity.NOW_TRAKT_USER_LOADER_ID, null,
                    recentlyTraktCallbacks);
            loaderManager.initLoader(ShowsActivity.NOW_TRAKT_FRIENDS_LOADER_ID, null,
                    traktFriendsHistoryCallbacks);
        }

        setHasOptionsMenu(true);
    }

    /**
     * Show or hide the progress bar of the {@link SwipeRefreshLayout}
     * wrapping view.
     */
    protected void showProgressBar(boolean show) {
        // only hide if everybody has finished loading
        if (!show) {
            if (isLoadingRecentlyWatched || isLoadingFriends) {
                return;
            }
        }
        swipeRefreshLayout.setRefreshing(show);
    }

    protected void updateEmptyState() {
        boolean isEmpty = adapter.getItemCount() == 0;
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    protected void destroyLoaderIfExists(int loaderId) {
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if (loaderManager.getLoader(loaderId) != null) {
            loaderManager.destroyLoader(loaderId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}
