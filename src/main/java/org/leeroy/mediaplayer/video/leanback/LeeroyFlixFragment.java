// Copyright 2017 LeeroyFlix
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.leeroy.mediaplayer.video.leanback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.CursorObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.IconItemRowPresenter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import org.leeroy.environment.LeeroyFlixUtils;
import org.leeroy.filecorelibrary.ExtStorageManager;
import org.leeroy.filecorelibrary.ExtStorageReceiver;
import org.leeroy.mediaplayer.video.BuildConfig;
import org.leeroy.mediaplayer.video.LeeroyFlixApp;
import org.leeroy.mediaplayer.video.DensityTweak;
import org.leeroy.mediaplayer.video.R;
import org.leeroy.mediaplayer.video.browser.LeeroyFlixActivity;
import org.leeroy.mediaplayer.video.browser.adapters.mappers.AnimesNShowsMapper;
import org.leeroy.mediaplayer.video.browser.adapters.mappers.TvshowCursorMapper;
import org.leeroy.mediaplayer.video.browser.adapters.mappers.VideoCursorMapper;
import org.leeroy.mediaplayer.video.browser.loader.AllTvshowsLoader;
import org.leeroy.mediaplayer.video.browser.loader.AllTvshowsNoAnimeLoader;
import org.leeroy.mediaplayer.video.browser.loader.AnimesLoader;
import org.leeroy.mediaplayer.video.browser.loader.AnimesNShowsLoader;
import org.leeroy.mediaplayer.video.browser.loader.FilmsLoader;
import org.leeroy.mediaplayer.video.browser.loader.LastAddedLoader;
import org.leeroy.mediaplayer.video.browser.loader.LastPlayedLoader;
import org.leeroy.mediaplayer.video.browser.loader.MoviesLoader;
import org.leeroy.mediaplayer.video.browser.loader.NonScrapedVideosCountLoader;
import org.leeroy.mediaplayer.video.browser.loader.VideoLoader;
import org.leeroy.mediaplayer.video.browser.loader.WatchingUpNextLoader;
import org.leeroy.mediaplayer.video.leanback.adapter.object.Box;
import org.leeroy.mediaplayer.video.leanback.adapter.object.EmptyView;
import org.leeroy.mediaplayer.video.leanback.adapter.object.Icon;
import org.leeroy.mediaplayer.video.leanback.animes.AllAnimesGridActivity;
import org.leeroy.mediaplayer.video.leanback.animes.AllAnimesIconBuilder;
import org.leeroy.mediaplayer.video.leanback.animes.AnimesByGenreActivity;
import org.leeroy.mediaplayer.video.leanback.animes.AnimesByYearActivity;
import org.leeroy.mediaplayer.video.leanback.collections.AllAnimeCollectionsGridActivity;
import org.leeroy.mediaplayer.video.leanback.collections.AllCollectionsGridActivity;
import org.leeroy.mediaplayer.video.leanback.collections.AnimeCollectionsIconBuilder;
import org.leeroy.mediaplayer.video.leanback.collections.CollectionsIconBuilder;
import org.leeroy.mediaplayer.video.leanback.collections.CollectionsMoviesIconBuilder;
import org.leeroy.mediaplayer.video.leanback.filebrowsing.ExtStorageListingActivity;
import org.leeroy.mediaplayer.video.leanback.filebrowsing.LocalListingActivity;
import org.leeroy.mediaplayer.video.leanback.movies.AllFilmsIconBuilder;
import org.leeroy.mediaplayer.video.leanback.movies.AllMoviesGridActivity;
import org.leeroy.mediaplayer.video.leanback.movies.AllMoviesIconBuilder;
import org.leeroy.mediaplayer.video.leanback.movies.MoviesByAlphaActivity;
import org.leeroy.mediaplayer.video.leanback.movies.MoviesByGenreActivity;
import org.leeroy.mediaplayer.video.leanback.movies.MoviesByRatingActivity;
import org.leeroy.mediaplayer.video.leanback.movies.MoviesByYearActivity;
import org.leeroy.mediaplayer.video.leanback.network.NetworkRootActivity;
import org.leeroy.mediaplayer.video.leanback.nonscraped.NonScrapedVideosActivity;
import org.leeroy.mediaplayer.video.leanback.overlay.Overlay;
import org.leeroy.mediaplayer.video.leanback.presenter.BoxItemPresenter;
import org.leeroy.mediaplayer.video.leanback.presenter.IconItemPresenter;
import org.leeroy.mediaplayer.video.leanback.presenter.PosterImageCardPresenter;
import org.leeroy.mediaplayer.video.leanback.search.VideoSearchActivity;
import org.leeroy.mediaplayer.video.leanback.tvshow.AllAnimeShowsGridActivity;
import org.leeroy.mediaplayer.video.leanback.tvshow.AllAnimeShowsIconBuilder;
import org.leeroy.mediaplayer.video.leanback.tvshow.AllTvshowNoAmimeIconBuilder;
import org.leeroy.mediaplayer.video.leanback.tvshow.AllTvshowsGridActivity;
import org.leeroy.mediaplayer.video.leanback.tvshow.AllTvshowsIconBuilder;
import org.leeroy.mediaplayer.video.leanback.tvshow.EpisodesByDateActivity;
import org.leeroy.mediaplayer.video.leanback.tvshow.TvshowsByAlphaActivity;
import org.leeroy.mediaplayer.video.leanback.tvshow.TvshowsByGenreActivity;
import org.leeroy.mediaplayer.video.leanback.tvshow.TvshowsByRatingActivity;
import org.leeroy.mediaplayer.video.player.PrivateMode;
import org.leeroy.mediaplayer.video.tvshow.TvshowSortOrderEntries;
import org.leeroy.mediaplayer.video.utils.VideoPreferencesCommon;
import org.leeroy.mediaplayer.video.utils.WebUtils;
import org.leeroy.mediaprovider.LeeroyFlixMediaIntent;
import org.leeroy.mediaprovider.ImportState;
import org.leeroy.mediaprovider.video.NetworkScannerReceiver;
import org.leeroy.mediaprovider.video.VideoStore;
import org.leeroy.mediascraper.AutoScrapeService;
import org.leeroy.mediaprovider.video.NetworkAutoRefresh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeeroyFlixFragment extends BrowseSupportFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger log = LoggerFactory.getLogger(LeeroyFlixFragment.class);

    // /!\ FIXME cannot be enabled since on large collection of videos viewed, loader takes forever to complete
    // this causes VideoLoader that has only a poolsize of one to not process any other loaders
    public final static boolean FEATURE_WATCH_UP_NEXT = false;

    private static final String PREF_PRIVATE_MODE = "PREF_PRIVATE_MODE";

    final static int LOADER_ID_LAST_ADDED = 42;
    final static int LOADER_ID_LAST_PLAYED = 43;
    final static int LOADER_ID_ALL_TV_SHOWS = 44;
    final static int LOADER_ID_NON_SCRAPED_VIDEOS_COUNT = 45;
    final static int LOADER_ID_ALL_MOVIES = 46;
    final static int LOADER_ID_WATCHING_UP_NEXT = 47;
    final static int LOADER_ID_ALL_ANIMES = 48;

    final static int ROW_ID_LAST_ADDED = 1000;
    final static int ROW_ID_LAST_PLAYED = 1001;
    final static int ROW_ID_MOVIES = 1002;
    final static int ROW_ID_TVSHOW = 1003;
    final static int ROW_ID_ALL_TVSHOWS = 1004;
    final static int ROW_ID_FILES = 1005;
    final static int ROW_ID_PREFERENCES = 1006;
    final static int ROW_ID_ALL_MOVIES = 1007;
    final static int ROW_ID_WATCHING_UP_NEXT = 1008;
    final static int ROW_ID_ANIMES = 1009;
    final static int ROW_ID_ALL_ANIMES = 1010;

    // Need these row indexes to update the full ListRow object
    final static int ROW_INDEX_UNSET = -1;

    private String currentLocale;

    private ArrayObjectAdapter mRowsAdapter;
    private CursorObjectAdapter mMoviesAdapter;
    private CursorObjectAdapter mAnimesAdapter;
    private CursorObjectAdapter mTvshowsAdapter;
    private CursorObjectAdapter mWatchingUpNextAdapter;
    private CursorObjectAdapter mLastAddedAdapter;
    private CursorObjectAdapter mLastPlayedAdapter;
    private ArrayObjectAdapter mFileBrowsingRowAdapter;

    private ListRow mWatchingUpNextRow;
    private ListRow mLastAddedRow;
    private ListRow mLastPlayedRow;
    private ListRow mMoviesRow; // row for all movie posters
    private ListRow mAnimesRow; // row for all anime posters
    private ListRow mTvshowsRow; // row for all tvshow posters
    private ListRow mMovieRow; // row for movie tiles
    private ListRow mAnimeRow; // row for anime tiles
    private ListRow mTvshowRow; // row for tvshow tiles

    private Box mAllMoviesBox;
    private Box mAllAnimesBox;
    private Box mAllTvshowsBox;
    private Box mAllCollectionsBox;
    private Box mAllAnimeCollectionsBox;
    private Box mAllAnimeShowsBox;

    private static boolean mSeparateAnimeFromShowMovie;
    private boolean mShowWatchingUpNextRow;
    private boolean mShowLastAddedRow;
    private boolean mShowLastPlayedRow;
    private boolean mShowMoviesRow;
    private String mMovieSortOrder;
    private boolean mShowTvshowsRow;
    private boolean mShowAnimesRow;
    private String mAnimesSortOrder;
    private String mTvShowSortOrder;
    private boolean mHideExternal;

    private boolean restartLastAddedLoader, restartLastPlayedLoader, restartMoviesLoader, restartTvshowsLoader, restartAnimesLoader, restartWatchingUpNextLoader;

    // Track the video ID at position 0 of last played row to detect when a new video was played
    private long mLastPlayedPosition0VideoId = -1;

    // Track initial load completion for each loader to allow UI population before blocking during scanning
    private boolean mWatchingUpNextInitialLoadComplete = false;
    private boolean mLastAddedInitialLoadComplete = false;
    private boolean mLastPlayedInitialLoadComplete = false;
    private boolean mMoviesInitialLoadComplete = false;
    private boolean mAnimesInitialLoadComplete = false;
    private boolean mTvshowsInitialLoadComplete = false;
    private boolean mNonScrapedVideosInitialLoadComplete = false;

    private Box mNonScrapedVideosItem;

    private SharedPreferences mPrefs;
    private Overlay mOverlay;
    private IntentFilter mUpdateFilter;

    private BackgroundManager bgMngr;

    private AsyncTask mBuildAllMoviesBoxTask;
    private AsyncTask mBuildAllAnimesBoxTask;
    private AsyncTask mBuildAllTvshowsBoxTask;
    private AsyncTask mBuildAllCollectionsBoxTask;
    private AsyncTask mBuildAllAnimeCollectionsBoxTask;
    private AsyncTask mBuildAllAnimeShowsBoxTask;

    private Activity mActivity;

    private static boolean wasInPause = false;

    private boolean firstTimeLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLocale = LeeroyFlixApp.getUiLocale(getContext());
    }

    private Activity updateActivity(String callingMethod) {
        mActivity = getActivity();
        if (mActivity == null) log.warn("updateActivity: {} -> activity is null!", callingMethod);
        return mActivity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        log.debug("onViewCreated");
        LeeroyFlixApp.loadLocale(getResources());
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();

        mOverlay = new Overlay(this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mSeparateAnimeFromShowMovie = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SEPARATE_ANIME_MOVIE_SHOW, VideoPreferencesCommon.SEPARATE_ANIME_MOVIE_SHOW_DEFAULT);
        mShowWatchingUpNextRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_WATCHING_UP_NEXT_ROW, VideoPreferencesCommon.SHOW_WATCHING_UP_NEXT_ROW_DEFAULT);
        if (! FEATURE_WATCH_UP_NEXT) mShowWatchingUpNextRow = false;
        mShowLastAddedRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_LAST_ADDED_ROW, VideoPreferencesCommon.SHOW_LAST_ADDED_ROW_DEFAULT);
        mShowLastPlayedRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_LAST_PLAYED_ROW, VideoPreferencesCommon.SHOW_LAST_PLAYED_ROW_DEFAULT);
        mShowMoviesRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_ALL_MOVIES_ROW, VideoPreferencesCommon.SHOW_ALL_MOVIES_ROW_DEFAULT);
        mMovieSortOrder = mPrefs.getString(VideoPreferencesCommon.KEY_MOVIE_SORT_ORDER, MoviesLoader.DEFAULT_SORT);
        mShowTvshowsRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_ALL_TV_SHOWS_ROW, VideoPreferencesCommon.SHOW_ALL_TV_SHOWS_ROW_DEFAULT);
        mShowAnimesRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_ALL_ANIMES_ROW, VideoPreferencesCommon.SHOW_ALL_ANIMES_ROW_DEFAULT);
        mAnimesSortOrder = mPrefs.getString(VideoPreferencesCommon.KEY_ANIMES_SORT_ORDER, AnimesLoader.DEFAULT_SORT);
        mTvShowSortOrder = mPrefs.getString(VideoPreferencesCommon.KEY_TV_SHOW_SORT_ORDER, TvshowSortOrderEntries.DEFAULT_SORT);
        mHideExternal = mPrefs.getBoolean(VideoPreferencesCommon.KEY_HIDE_EXTERNAL, false);

        //if (mPrefs.getBoolean(PREF_PRIVATE_MODE, false) !=  PrivateMode.isActive()) {
       //     PrivateMode.toggle();
       //     findAndUpdatePrivateModeIcon();
        //}

        updateBackground();

        Resources r = getResources();
        setBadgeDrawable(ContextCompat.getDrawable(mActivity, R.drawable.leanback_title));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(mActivity, R.color.leanback_side));

        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(mActivity, R.color.lightblueA200));

        setupEventListeners();

        loadRows();
        // init the loaders after the rows are loaded to populate
        if (mShowWatchingUpNextRow) {
            log.debug("onViewCreated: watchingUpNext initLoader");
            // /!\ WARNING this loader never ends if FEATURE_WATCH_UP_NEXT is true on large collection of videos watched
            LoaderManager.getInstance(this).initLoader(LOADER_ID_WATCHING_UP_NEXT, null, this);
            log.debug("onViewCreated: init LOADER_ID_WATCHING_UP_NEXT");
        } else {
            log.debug("onViewCreated: NOT init LOADER_ID_WATCHING_UP_NEXT");
        }
        log.debug("onViewCreated: mShowLastAddedRow={}, mShowLastPlayedRow={}", mShowLastAddedRow, mShowLastPlayedRow);
        if (mShowLastAddedRow) {
            log.debug("onViewCreated: lastAdded initLoader");
            LoaderManager.getInstance(this).initLoader(LOADER_ID_LAST_ADDED, null, this);
        }
        if (mShowLastPlayedRow) {
            log.debug("onViewCreated: lastPlayed initLoader");
            LoaderManager.getInstance(this).initLoader(LOADER_ID_LAST_PLAYED, null, this);
        }
        if (mShowMoviesRow) {
            Bundle movieArgs = new Bundle();
            movieArgs.putString("sort", mMovieSortOrder);
            log.debug("onViewCreated: allMovies initLoader");
            LoaderManager.getInstance(this).initLoader(LOADER_ID_ALL_MOVIES, movieArgs, this);
        }
        if (mShowTvshowsRow) {
            Bundle tvshowArgs = new Bundle();
            tvshowArgs.putString("sort", mTvShowSortOrder);
            log.debug("onViewCreated: allTvshows initLoader");
            LoaderManager.getInstance(this).initLoader(LOADER_ID_ALL_TV_SHOWS, tvshowArgs, this);
        }
        log.debug("onViewCreated: nonScrapedVideosCount initLoader");
        LoaderManager.getInstance(this).initLoader(LOADER_ID_NON_SCRAPED_VIDEOS_COUNT, null, this);
        if (mShowAnimesRow && mSeparateAnimeFromShowMovie) {
            Bundle animesArgs = new Bundle();
            animesArgs.putString("sort", mAnimesSortOrder);
            log.debug("onViewCreated: allAnimes initLoader");
            LoaderManager.getInstance(this).initLoader(LOADER_ID_ALL_ANIMES, animesArgs, this);
        }
    }

    @Override
    public void onDestroyView() {
        log.debug("onDestroyView");
        // Cancel all AsyncTasks
        if (mBuildAllMoviesBoxTask != null) {
            mBuildAllMoviesBoxTask.cancel(true);
        }
        if (mBuildAllAnimesBoxTask != null) {
            mBuildAllAnimesBoxTask.cancel(true);
        }
        if (mBuildAllTvshowsBoxTask != null) {
            mBuildAllTvshowsBoxTask.cancel(true);
        }
        if (mBuildAllCollectionsBoxTask != null) {
            mBuildAllCollectionsBoxTask.cancel(true);
        }
        if (mBuildAllAnimeCollectionsBoxTask != null) {
            mBuildAllAnimeCollectionsBoxTask.cancel(true);
        }
        if (mBuildAllAnimeShowsBoxTask != null) {
            mBuildAllAnimeShowsBoxTask.cancel(true);
        }

        if (mOverlay != null) {
            mOverlay.destroy();
        }
        super.onDestroyView();
    }

    private boolean hasLocaleChanged() {
        String newLocale = LeeroyFlixApp.getUiLocale(getContext());
        return !currentLocale.equals(newLocale);
    }

    @Override
    public void onResume() {
        log.debug("onResume");
        super.onResume();
        LeeroyFlixApp.loadLocale(getResources());
        if (hasLocaleChanged()) {
            // Recreate the fragment or activity to apply the new locale
            if (getActivity() != null) getActivity().recreate();
        }
        // be sure activity is not null and static variable does not refer to a destroyed one
        mActivity = getActivity();
        if (mActivity == null) log.warn("onResume: mActivity is null!");
        mOverlay.resume();
        updateBackground();

        // Update storage visibility to reflect current mount state
        // This ensures unmounted drives don't show icons when returning from background
        updateUsbAndSdcardVisibility();

        // register broadcast receivers
        IntentFilter intentFilter = new IntentFilter(ExtStorageReceiver.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(ExtStorageReceiver.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(ExtStorageReceiver.ACTION_MEDIA_CHANGED);
        intentFilter.addDataScheme("file");
        intentFilter.addDataScheme(ExtStorageReceiver.LEEROYFLIX_FILE_SCHEME);//new android nougat send UriExposureException when scheme = file
        if (Build.VERSION.SDK_INT >= 33) {
            mActivity.registerReceiver(mExternalStorageReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            mActivity.registerReceiver(mExternalStorageReceiver, intentFilter);
        }
        mUpdateFilter = new IntentFilter(LeeroyFlixMediaIntent.ACTION_VIDEO_SCANNER_SCAN_FINISHED);
        // VideoStoreImportService sends null scheme thus do not filter for specific scheme
        //for (String scheme : UriUtils.sIndexableSchemes) mUpdateFilter.addDataScheme(scheme);
        if (Build.VERSION.SDK_INT >= 33) {
            mActivity.registerReceiver(mUpdateReceiver, mUpdateFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            mActivity.registerReceiver(mUpdateReceiver, mUpdateFilter);
        }

        // check if resuming we have a change of parameters and update everything accordingly
        restartWatchingUpNextLoader = false;
        restartLastAddedLoader = false;
        restartLastPlayedLoader = false;
        restartMoviesLoader = false;
        restartTvshowsLoader = false;
        restartAnimesLoader = false;
        // needs to be done first to know if movie/tvshow loaders need to be reloaded because
        // need to (inc|ex)clude animation videos. However do not restart updateAnimesRow because done later
        boolean newSeparateAnimeFromShowMovie = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SEPARATE_ANIME_MOVIE_SHOW, VideoPreferencesCommon.SEPARATE_ANIME_MOVIE_SHOW_DEFAULT);
        if (newSeparateAnimeFromShowMovie != mSeparateAnimeFromShowMovie) {
            mSeparateAnimeFromShowMovie = newSeparateAnimeFromShowMovie;
            if (newSeparateAnimeFromShowMovie)
                log.debug("onResume: separate Anime From Show Movie");
            else
                log.debug("onResume: do not separate Anime From Show Movie");
            // need to switch loaders because movies are tvshows do not contain animations anymore
            if (mShowMoviesRow) restartMoviesLoader = true;
            if (mShowTvshowsRow) restartTvshowsLoader = true;
            // in case we disable Anime/Show+Movie separation, there can't be an allAnimesRow
            if (! newSeparateAnimeFromShowMovie) mShowAnimesRow = false;
            if (mShowAnimesRow) restartAnimesLoader = true;
            else // this will add or remove row if no allAnimesRow
                updateAnimesRow(null, false);
        }

        boolean newShowWatchingUpNextRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_WATCHING_UP_NEXT_ROW, VideoPreferencesCommon.SHOW_WATCHING_UP_NEXT_ROW_DEFAULT);
        if (! FEATURE_WATCH_UP_NEXT) newShowWatchingUpNextRow = mShowWatchingUpNextRow;
        if (newShowWatchingUpNextRow != mShowWatchingUpNextRow) {
            log.debug("onResume: preference changed, display watching up next row: {} -> updating", newShowWatchingUpNextRow);
            mShowWatchingUpNextRow = newShowWatchingUpNextRow;
            if (mShowWatchingUpNextRow) restartWatchingUpNextLoader = true;
            else updateWatchingUpNextRow(null);
        }
        if (restartWatchingUpNextLoader) {
            log.debug("onResume: watchingUpNext initLoader");
            restartWatchingUpNextLoader = false;
            LoaderManager.getInstance(this).initLoader(LOADER_ID_WATCHING_UP_NEXT, null, this);
        }

        boolean newShowLastAddedRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_LAST_ADDED_ROW, VideoPreferencesCommon.SHOW_LAST_ADDED_ROW_DEFAULT);
        if (newShowLastAddedRow != mShowLastAddedRow) {
            log.debug("onResume: preference changed, display last added row: {} -> updating", newShowLastAddedRow);
            mShowLastAddedRow = newShowLastAddedRow;
            if (mShowLastAddedRow) restartLastAddedLoader = true;
            else updateLastAddedRow(null);
        }
        if (restartLastAddedLoader) {
            log.debug("onResume: lastAdded initLoader");
            restartLastAddedLoader = false;
            // update lastAdded loader: MUST use initLoader and NOT restartLoader otherwise to avoid update if it exists
            LoaderManager.getInstance(this).initLoader(LOADER_ID_LAST_ADDED, null, this);
        }

        boolean newShowLastPlayedRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_LAST_PLAYED_ROW, VideoPreferencesCommon.SHOW_LAST_PLAYED_ROW_DEFAULT);
        if (newShowLastPlayedRow != mShowLastPlayedRow) {
            log.debug("onResume: preference changed, display last played row: {} -> updating", newShowLastPlayedRow);
            mShowLastPlayedRow = newShowLastPlayedRow;
            if (mShowLastPlayedRow) restartLastPlayedLoader = true;
            else updateLastPlayedRow(null);
        }
        if (restartLastPlayedLoader) {
            log.debug("onResume: lastPlayed initLoader");
            restartLastPlayedLoader = false;
            // update lastAdded loader: MUST use initLoader and NOT restartLoader otherwise to avoid update if it exists
            LoaderManager.getInstance(this).initLoader(LOADER_ID_LAST_PLAYED, null, this);
        }

        boolean newShowMoviesRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_ALL_MOVIES_ROW, VideoPreferencesCommon.SHOW_ALL_MOVIES_ROW_DEFAULT);
        if (newShowMoviesRow != mShowMoviesRow) {
            log.debug("onResume: preference changed, display all movies row: {} -> updating", newShowMoviesRow);
            mShowMoviesRow = newShowMoviesRow;
            if (mShowMoviesRow) restartMoviesLoader = true;
            else updateMoviesRow(null, true);
        } else
            if (! mShowMoviesRow && firstTimeLoad) updateMoviesRow(null, false);
        String newMovieSortOrder = mPrefs.getString(VideoPreferencesCommon.KEY_MOVIE_SORT_ORDER, MoviesLoader.DEFAULT_SORT);
        if (mShowMoviesRow && !newMovieSortOrder.equals(mMovieSortOrder)) {
            log.debug("onResume: preference changed, showing movie row and sort order changed -> updating");
            mMovieSortOrder = newMovieSortOrder;
            restartMoviesLoader = true;
        }
        if (restartMoviesLoader) {
            log.debug("onResume: restart allMovies loader");
            restartMoviesLoader = false;
            Bundle args = new Bundle();
            args.putString("sort", mMovieSortOrder);
            // need to restart the loader since it can change depending on animations / movies shows separation
            LoaderManager.getInstance(this).restartLoader(LOADER_ID_ALL_MOVIES, args, this);
        }

        boolean newShowTvshowsRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_ALL_TV_SHOWS_ROW, VideoPreferencesCommon.SHOW_ALL_TV_SHOWS_ROW_DEFAULT);
        if (newShowTvshowsRow != mShowTvshowsRow) {
            log.debug("onResume: preference changed, display all tv shows row: {} -> updating", newShowTvshowsRow);
            mShowTvshowsRow = newShowTvshowsRow;
            if (mShowTvshowsRow) restartTvshowsLoader = true;
            else updateTvShowsRow(null, true);
        } else
            if (! mShowTvshowsRow && firstTimeLoad) updateTvShowsRow(null, false);
        String newTvShowSortOrder = mPrefs.getString(VideoPreferencesCommon.KEY_TV_SHOW_SORT_ORDER, TvshowSortOrderEntries.DEFAULT_SORT);
        if (mShowTvshowsRow && !newTvShowSortOrder.equals(mTvShowSortOrder)) {
            log.debug("onResume: preference changed, showing tv show row and sort order changed -> updating");
            mTvShowSortOrder = newTvShowSortOrder;
            restartTvshowsLoader = true;
        }
        if (restartTvshowsLoader) {
            log.debug("onResume: restart allTvshows loader");
            restartTvshowsLoader = false;
            Bundle args = new Bundle();
            args.putString("sort", mTvShowSortOrder);
            // need to restart the loader since it can change depending on animations / movies shows separation
            LoaderManager.getInstance(this).restartLoader(LOADER_ID_ALL_TV_SHOWS, args, this);
        }

        boolean newShowAnimesRow = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_ALL_ANIMES_ROW, VideoPreferencesCommon.SHOW_ALL_ANIMES_ROW_DEFAULT);
        if (newShowAnimesRow != mShowAnimesRow && mSeparateAnimeFromShowMovie) {
            log.debug("onResume: preference changed, display all animes row: {} -> updating", newShowAnimesRow);
            mShowAnimesRow = newShowAnimesRow;
            if (mShowAnimesRow) restartAnimesLoader = true;
            else if (mSeparateAnimeFromShowMovie) updateAnimesRow(null, true);
        } else
            if (! mShowAnimesRow && mSeparateAnimeFromShowMovie && firstTimeLoad) updateAnimesRow(null, true);
        String newAnimesSortOrder = mPrefs.getString(VideoPreferencesCommon.KEY_ANIMES_SORT_ORDER, AnimesLoader.DEFAULT_SORT);
        if (mShowAnimesRow && !newAnimesSortOrder.equals(mAnimesSortOrder) && mSeparateAnimeFromShowMovie) {
            log.debug("onResume: preference changed, showing animes row and sort order changed -> updating");
            mAnimesSortOrder = newAnimesSortOrder;
            restartAnimesLoader = true;
        }
        if (restartAnimesLoader) {
            log.debug("onResume: restart allAnimes loader");
            restartAnimesLoader = false;
            Bundle args = new Bundle();
            args.putString("sort", mAnimesSortOrder);
            LoaderManager.getInstance(this).initLoader(LOADER_ID_ALL_ANIMES, args, this);
        }

        firstTimeLoad = false;

        //findAndUpdatePrivateModeIcon();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOverlay.pause();

        // be sure to reload loaders and iconBoxes in onResume after an onPause
        wasInPause = true;
        if (mShowLastAddedRow) restartLastAddedLoader = true;
        if (mShowLastPlayedRow) restartLastPlayedLoader = true;
        if (mShowLastPlayedRow) restartMoviesLoader = true;
        if (mShowTvshowsRow) restartTvshowsLoader = true;
        if (mShowAnimesRow) restartAnimesLoader = true;

        mActivity = getActivity();
        if (mActivity == null) log.warn("onPause: mActivity is null!");
        try {
            log.debug("onPause: unregisterReceiver mUpdateReceiver and mExternalStorageReceiver");
            mActivity.unregisterReceiver(mExternalStorageReceiver);
            mActivity.unregisterReceiver(mUpdateReceiver);
        } catch(IllegalArgumentException | NullPointerException e) { // EntryActivity could have been destroyed
            log.warn("onDetach: trying to unregister mUpdateReceiver or mExternalStorageReceiver which is not registered or EntryActivity destroyed!");
        }
    }

    private void updateBackground() {
        if (updateActivity("updateBackground") == null) return; // do not update background when activity has been destroyed
        Resources r = getResources();
        bgMngr = BackgroundManager.getInstance(mActivity);
        if(!bgMngr.isAttached())
            bgMngr.attach(mActivity.getWindow());
        if (PrivateMode.isActive()) {
            bgMngr.setColor(ContextCompat.getColor(mActivity, R.color.private_mode));
            bgMngr.setDrawable(ContextCompat.getDrawable(mActivity, R.drawable.private_background));
        } else {
            bgMngr.setColor(ContextCompat.getColor(mActivity, R.color.leanback_background));
            bgMngr.setDrawable(new ColorDrawable(ContextCompat.getColor(mActivity, R.color.leanback_background)));
        }
    }

    private void setupEventListeners() {
        if (mActivity == null) return;
        setOnSearchClickedListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mActivity != null) {
                    Intent intent = new Intent(mActivity, VideoSearchActivity.class);
                    intent.putExtra(VideoSearchActivity.EXTRA_SEARCH_MODE, VideoSearchActivity.SEARCH_MODE_ALL);
                    startActivity(intent);
                } else {
                    log.warn("onClick: search clicked but mActivity is null!");
                }
            }
        });
        if (mActivity != null) setOnItemViewClickedListener(new MainViewClickedListener(mActivity));
    }

    private void loadRows() {
        log.debug("loadRows");
        if (updateActivity("loadRows") == null) return;

        // Two different row presenters, one standard for regular cards, one special for the icon items
        ListRowPresenter listRowPresenter = new ListRowPresenter();
        IconItemRowPresenter iconItemRowPresenter = new IconItemRowPresenter();

        // Only way I found to use two different presenter is using a ClassPresenterSelector, hence i needed
        // to create a dummy IconListRow that does nothing more than a regular ListRow
        ClassPresenterSelector rowsPresenterSelector = new ClassPresenterSelector();
        rowsPresenterSelector.addClassPresenter(ListRow.class, listRowPresenter);
        rowsPresenterSelector.addClassPresenter(IconListRow.class, iconItemRowPresenter);

        // Basic header presenter for both row presenters
        listRowPresenter.setHeaderPresenter(new RowHeaderPresenter());
        iconItemRowPresenter.setHeaderPresenter(new RowHeaderPresenter());

        mRowsAdapter = new ArrayObjectAdapter(rowsPresenterSelector);

        mWatchingUpNextAdapter = new CursorObjectAdapter(new PosterImageCardPresenter(mActivity));
        mWatchingUpNextAdapter.setMapper(new CompatibleCursorMapperConverter(new VideoCursorMapper()));
        mWatchingUpNextRow = new ListRow(ROW_ID_WATCHING_UP_NEXT, new HeaderItem(getString(R.string.watching_up_next)), mWatchingUpNextAdapter);

        mLastAddedAdapter = new CursorObjectAdapter(new PosterImageCardPresenter(mActivity));
        mLastAddedAdapter.setMapper(new CompatibleCursorMapperConverter(new VideoCursorMapper()));
        mLastAddedRow = new ListRow(ROW_ID_LAST_ADDED, new HeaderItem(getString(R.string.recently_added)), mLastAddedAdapter);

        mLastPlayedAdapter = new CursorObjectAdapter(new PosterImageCardPresenter(mActivity));
        mLastPlayedAdapter.setMapper(new CompatibleCursorMapperConverter(new VideoCursorMapper()));
        mLastPlayedRow = new ListRow(ROW_ID_LAST_PLAYED, new HeaderItem(getString(R.string.recently_played)), mLastPlayedAdapter);

        boolean showByRating = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SHOW_BY_RATING, VideoPreferencesCommon.SHOW_BY_RATING_DEFAULT);

        ArrayObjectAdapter moviesRowsAdapter = new ArrayObjectAdapter(new BoxItemPresenter());
        buildAllMoviesBox(wasInPause);
        moviesRowsAdapter.add(mAllMoviesBox);
        //mMoviesRowsAdapter.add(new Box(Box.ID.MOVIES_BY_ALPHA, getString(R.string.movies_by_alpha), R.drawable.alpha_banner));
        moviesRowsAdapter.add(new Box(Box.ID.MOVIES_BY_GENRE, getString(R.string.movies_by_genre), R.drawable.genres_banner));
        if (showByRating)
            moviesRowsAdapter.add(new Box(Box.ID.MOVIES_BY_RATING, getString(R.string.movies_by_rating), R.drawable.ratings_banner));
        moviesRowsAdapter.add(new Box(Box.ID.MOVIES_BY_YEAR, getString(R.string.movies_by_year), R.drawable.years_banner_2025));
        mMovieRow = new ListRow(ROW_ID_MOVIES, new HeaderItem(getString(R.string.movies)), moviesRowsAdapter);
        buildAllCollectionsBox(wasInPause);
        moviesRowsAdapter.add(mAllCollectionsBox);

        ArrayObjectAdapter tvshowRowAdapter = new ArrayObjectAdapter(new BoxItemPresenter());
        buildAllTvshowsBox(wasInPause);
        tvshowRowAdapter.add(mAllTvshowsBox);
        //tvshowRowAdapter.add(new Box(Box.ID.TVSHOWS_BY_ALPHA, getString(R.string.tvshows_by_alpha), R.drawable.alpha_banner));
        tvshowRowAdapter.add(new Box(Box.ID.TVSHOWS_BY_GENRE, getString(R.string.tvshows_by_genre), R.drawable.genres_banner));
        if (showByRating)
            tvshowRowAdapter.add(new Box(Box.ID.TVSHOWS_BY_RATING, getString(R.string.tvshows_by_rating), R.drawable.ratings_banner));
        tvshowRowAdapter.add(new Box(Box.ID.EPISODES_BY_DATE, getString(R.string.episodes_by_date), R.drawable.years_banner_2025));
        mTvshowRow = new ListRow(ROW_ID_TVSHOW, new HeaderItem(getString(R.string.all_tv_shows)), tvshowRowAdapter);

        ArrayObjectAdapter animeRowAdapter = new ArrayObjectAdapter(new BoxItemPresenter());
        mAnimeRow = new ListRow(ROW_ID_ANIMES, new HeaderItem(getString(R.string.animes)), animeRowAdapter);
        buildAllAnimesBox(wasInPause);
        animeRowAdapter.add(mAllAnimesBox);
        animeRowAdapter.add(new Box(Box.ID.ANIMES_BY_GENRE, getString(R.string.animes_by_genre), R.drawable.genres_banner));
        animeRowAdapter.add(new Box(Box.ID.ANIMES_BY_YEAR, getString(R.string.animes_by_year), R.drawable.years_banner_2025));
        buildAllAnimeShowsBox(wasInPause);
        animeRowAdapter.add(mAllAnimeShowsBox);

        buildAllAnimeCollectionsBox(wasInPause);
        animeRowAdapter.add(mAllAnimeCollectionsBox);

        wasInPause = false;


        // initialize adapters even the ones not used but do not launch the loaders yet for performance considerations

        // this is for the all movies row not the movie row
        mMoviesAdapter = new CursorObjectAdapter(new PosterImageCardPresenter(mActivity));
        mMoviesAdapter.setMapper(new CompatibleCursorMapperConverter(new VideoCursorMapper()));
        mMoviesRow = new ListRow(ROW_ID_ALL_MOVIES, new HeaderItem(getString(R.string.all_movies)), mMoviesAdapter);

        // this is for the all tv shows row not the tv show row
        mTvshowsAdapter = new CursorObjectAdapter(new PosterImageCardPresenter(mActivity));
        mTvshowsAdapter.setMapper(new CompatibleCursorMapperConverter(new TvshowCursorMapper()));
        mTvshowsRow = new ListRow(ROW_ID_ALL_TVSHOWS, new HeaderItem(getString(R.string.all_tvshows)), mTvshowsAdapter);

        // this is for the all animes row not the animation row
        mAnimesAdapter = new CursorObjectAdapter(new PosterImageCardPresenter(mActivity));
        mAnimesAdapter.setMapper(new CompatibleCursorMapperConverter(new AnimesNShowsMapper()));
        mAnimesRow = new ListRow(ROW_ID_ALL_ANIMES, new HeaderItem(getString(R.string.all_animes_row)), mAnimesAdapter);

        if (!mHideExternal ) {
            mFileBrowsingRowAdapter = new ArrayObjectAdapter(new BoxItemPresenter());
            mFileBrowsingRowAdapter.add(new Box(Box.ID.NETWORK, getString(R.string.network_storage), R.drawable.filetype_new_server));
        
            mFileBrowsingRowAdapter.add(new Box(Box.ID.FOLDERS, getString(R.string.internal_storage), R.drawable.filetype_new_folder));
            //mFileBrowsingRowAdapter.add(new Box(Box.ID.VIDEOS_BY_LISTS, getString(R.string.video_lists), R.drawable.filetype_new_playlist));

            mNonScrapedVideosItem = new Box(Box.ID.NON_SCRAPED_VIDEOS, getString(R.string.non_scraped_videos), R.drawable.filetype_new_unscraped_video);
            
            mRowsAdapter.add(new ListRow(ROW_ID_FILES,
                    new HeaderItem(getString(R.string.leanback_browsing)),
                    mFileBrowsingRowAdapter));

            // Add USB and SDcard items at init ?depending of their availability
            updateUsbAndSdcardVisibility();
        }

        ArrayObjectAdapter preferencesRowAdapter = new ArrayObjectAdapter(new IconItemPresenter());
        preferencesRowAdapter.add(new Icon(Icon.ID.PREFERENCES, getString(R.string.preferences), R.drawable.lollipop_settings));
        preferencesRowAdapter.add(new Icon(Icon.ID.RESCRAPE, getString(R.string.rescrape_title), R.drawable.filetype_new_rescan));

        //mPreferencesRowAdapter.add(new Icon(Icon.ID.PRIVATE_MODE, getString(R.string.private_mode_is_on), getString(R.string.private_mode_is_off),
        //                                    R.drawable.private_mode,  R.drawable.private_mode_off, PrivateMode.isActive()));
        //mPreferencesRowAdapter.add(new Icon(Icon.ID.LEGACY_UI, getString(R.string.leanback_legacy_ui), R.drawable.legacy_ui_icon));
        //mPreferencesRowAdapter.add(new Icon(Icon.ID.HELP_FAQ, getString(R.string.help_faq), R.drawable.lollipop_help));

        // Must use an IconListRow to have the dedicated presenter used (see ClassPresenterSelector above)
        mRowsAdapter.add(new IconListRow(ROW_ID_PREFERENCES,
                new HeaderItem(getString(R.string.preferences)),
                preferencesRowAdapter));

        setAdapter(mRowsAdapter);
    }

    private boolean isCursorCountChanged(Cursor oldCursor, Cursor newCursor) {
        if ((oldCursor == null && newCursor != null) || (oldCursor != null && newCursor == null))
            return true;
        return oldCursor.getCount() != newCursor.getCount();
    }

    private void refreshAllBoxes() {
        log.debug("refreshAllBoxes");
        if (updateActivity("refreshAllBoxes") == null) return;
        refreshAllMoviesBox();
        refreshAllCollectionsBox();
        refreshAllTvshowsBox();
        refreshAllAnimesBox();
        refreshAllAnimeShowsBox();
        refreshAllAnimeCollectionsBox();
    }

    private void buildAllMoviesBox(Boolean buildIcons) {
        log.debug("buildAllMoviesBox: buildIcons {}", buildIcons);
        mAllMoviesBox = new Box(Box.ID.ALL_MOVIES, getString(R.string.all_movies), R.drawable.movies_banner);
        if (buildIcons) refreshAllMoviesBox();
    }

    private void refreshAllMoviesBox() {
        log.debug("refreshAllMoviesBox");
        if (! mShowMoviesRow) {
            if (mBuildAllMoviesBoxTask != null) mBuildAllMoviesBoxTask.cancel(true);
            mBuildAllMoviesBoxTask = new buildAllMoviesBoxTask().execute();
        }
    }

    private class buildAllMoviesBoxTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap iconBitmap;
            if (mSeparateAnimeFromShowMovie) iconBitmap = new AllFilmsIconBuilder(mActivity).buildNewBitmap();
            else iconBitmap = new AllMoviesIconBuilder(mActivity).buildNewBitmap();
            return iconBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || mAllMoviesBox == null || mMovieRow == null) {
                return;
            }
            mAllMoviesBox.setBitmap(bitmap);
            ((ArrayObjectAdapter)mMovieRow.getAdapter()).replace(0, mAllMoviesBox);
        }
    }

    private void buildAllCollectionsBox(Boolean buildIcons) {
        log.debug("buildAllCollectionsBox: buildIcons {}", buildIcons);
        mAllCollectionsBox = new Box(Box.ID.COLLECTIONS, getString(R.string.movie_collections), R.drawable.movies_banner);
        if (buildIcons) refreshAllCollectionsBox();
    }

    private void refreshAllCollectionsBox() {
        log.debug("refreshAllCollectionsBox");
        if (! mShowMoviesRow) {
            if (mBuildAllCollectionsBoxTask != null) mBuildAllCollectionsBoxTask.cancel(true);
            mBuildAllCollectionsBoxTask = new buildAllCollectionsBoxTask().execute();
        }
    }

    private class buildAllCollectionsBoxTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap iconBitmap;
            if (mSeparateAnimeFromShowMovie) iconBitmap = new CollectionsIconBuilder(mActivity).buildNewBitmap();
            else iconBitmap = new CollectionsMoviesIconBuilder(mActivity).buildNewBitmap();
            return iconBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || mAllCollectionsBox == null || mMovieRow == null) {
                return;
            }
            mAllCollectionsBox.setBitmap(bitmap);
            ((ArrayObjectAdapter)mMovieRow.getAdapter()).replace(3, mAllCollectionsBox);
        }
    }

    private void buildAllAnimeCollectionsBox(Boolean buildIcons) {
        log.debug("buildAllAnimeCollectionsBox: buildIcons {}", buildIcons);
        mAllAnimeCollectionsBox = new Box(Box.ID.ANIME_COLLECTIONS, getString(R.string.anime_collections), R.drawable.movies_banner);
        if (buildIcons) refreshAllAnimeCollectionsBox();
    }

    private void refreshAllAnimeCollectionsBox() {
        log.debug("refreshAllAnimeCollectionsBox");
        if (mSeparateAnimeFromShowMovie && ! mShowAnimesRow) {
            if (mBuildAllAnimeCollectionsBoxTask != null)
                mBuildAllAnimeCollectionsBoxTask.cancel(true);
            mBuildAllAnimeCollectionsBoxTask = new buildAllAnimeCollectionsBoxTask().execute();
        }
    }

    private class buildAllAnimeCollectionsBoxTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            return new AnimeCollectionsIconBuilder(mActivity).buildNewBitmap();
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || mAllAnimeCollectionsBox == null || mAnimeRow == null) return;
            mAllAnimeCollectionsBox.setBitmap(bitmap);
            ((ArrayObjectAdapter)mAnimeRow.getAdapter()).replace(4, mAllAnimeCollectionsBox);
        }
    }

    private void buildAllAnimesBox(Boolean buildIcons) {
        log.debug("buildAllAnimesBox: buildIcons {}", buildIcons);
        mAllAnimesBox = new Box(Box.ID.ALL_ANIMES, getString(R.string.all_animes), R.drawable.movies_banner);
        if (buildIcons) refreshAllAnimesBox();
    }

    private void refreshAllAnimesBox() {
        log.debug("refreshAllAnimesBox");
        if (mSeparateAnimeFromShowMovie && ! mShowAnimesRow) {
            if (mBuildAllAnimesBoxTask != null) mBuildAllAnimesBoxTask.cancel(true);
            mBuildAllAnimesBoxTask = new buildAllAnimesBoxTask().execute();
        }
    }

    private class buildAllAnimesBoxTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            return new AllAnimesIconBuilder(mActivity).buildNewBitmap();
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || mAllAnimesBox == null || mAnimeRow == null) return;
            mAllAnimesBox.setBitmap(bitmap);
            ((ArrayObjectAdapter)mAnimeRow.getAdapter()).replace(0, mAllAnimesBox);
        }
    }

    private void buildAllAnimeShowsBox(Boolean buildIcons) {
        log.debug("buildAllAnimeShowsBox: buildIcons {}", buildIcons);
        mAllAnimeShowsBox = new Box(Box.ID.ALL_ANIMESHOWS, getString(R.string.all_animeshows), R.drawable.movies_banner);
        if (buildIcons) refreshAllAnimeShowsBox();
    }

    private void refreshAllAnimeShowsBox() {
        log.debug("refreshAllAnimeShowsBox");
        if (mSeparateAnimeFromShowMovie && ! mShowAnimesRow) {
            if (mBuildAllAnimeShowsBoxTask != null) mBuildAllAnimeShowsBoxTask.cancel(true);
            mBuildAllAnimeShowsBoxTask = new buildAllAnimeShowsBoxTask().execute();
        }
    }

    private class buildAllAnimeShowsBoxTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            return new AllAnimeShowsIconBuilder(mActivity).buildNewBitmap();
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || mAllAnimeShowsBox == null || mAnimeRow == null) return;
            mAllAnimeShowsBox.setBitmap(bitmap);
            ((ArrayObjectAdapter)mAnimeRow.getAdapter()).replace(3, mAllAnimeShowsBox);
        }
    }

    private void buildAllTvshowsBox(Boolean buildIcons) {
        log.debug("buildTvshowsMoviesBox: buildIcons {}", buildIcons);
        mAllTvshowsBox = new Box(Box.ID.ALL_TVSHOWS, getString(R.string.all_tvshows), R.drawable.movies_banner);
        if (buildIcons) refreshAllTvshowsBox();
    }

    private void refreshAllTvshowsBox() {
        log.debug("refreshAllTvshowsBox");
        if (! mShowTvshowsRow) {
            if (mBuildAllTvshowsBoxTask != null) mBuildAllTvshowsBoxTask.cancel(true);
            mBuildAllTvshowsBoxTask = new buildAllTvshowsBoxTask().execute();
        }
    }

    private class buildAllTvshowsBoxTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap iconBitmap;
            if (mSeparateAnimeFromShowMovie) iconBitmap = new AllTvshowNoAmimeIconBuilder(mActivity).buildNewBitmap();
            else iconBitmap = new AllTvshowsIconBuilder(mActivity).buildNewBitmap();
            return iconBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || mAllTvshowsBox == null || mTvshowRow == null) return;
            mAllTvshowsBox.setBitmap(bitmap);
            ((ArrayObjectAdapter)mTvshowRow.getAdapter()).replace(0, mAllTvshowsBox);
        }
    }

    private void debugRows(String function) {
        if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1) log.trace("{}: ROW_ID_WATCHING_UP_NEXT {}", function, getRowPosition(ROW_ID_WATCHING_UP_NEXT));
        if (getRowPosition(ROW_ID_LAST_ADDED) != -1) log.trace("{}: ROW_ID_LAST_ADDED {}", function, getRowPosition(ROW_ID_LAST_ADDED));
        if (getRowPosition(ROW_ID_LAST_PLAYED) != -1) log.trace("{}: ROW_ID_LAST_PLAYED {}", function, getRowPosition(ROW_ID_LAST_PLAYED));
        if (getRowPosition(ROW_ID_MOVIES) != -1) log.trace("{}: ROW_ID_MOVIES {}", function, getRowPosition(ROW_ID_MOVIES));
        if (getRowPosition(ROW_ID_ALL_MOVIES) != -1) log.trace("{}: ROW_ID_ALL_MOVIES {}", function, getRowPosition(ROW_ID_ALL_MOVIES));
        if (getRowPosition(ROW_ID_TVSHOW) != -1) log.trace("{}: ROW_ID_TVSHOW {}", function, getRowPosition(ROW_ID_TVSHOW));
        if (getRowPosition(ROW_ID_ALL_TVSHOWS) != -1) log.trace("{}: ROW_ID_ALL_TVSHOWS {}", function, getRowPosition(ROW_ID_ALL_TVSHOWS));
        if (getRowPosition(ROW_ID_ANIMES) != -1) log.trace("{}: ROW_ID_ANIMES {}", function, getRowPosition(ROW_ID_ANIMES));
        if (getRowPosition(ROW_ID_ALL_ANIMES) != -1) log.trace("{}: ROW_ID_ALL_ANIMES{}", function, getRowPosition(ROW_ID_ALL_ANIMES));
    }

    private void updateWatchingUpNextRow(Cursor cursor) {
        log.debug("updateWatchingUpNextRow");
        if (cursor != null) {
            log.debug("updateWatchingUpNextRow: cursor != null");
            mWatchingUpNextAdapter.changeCursor(cursor);
        } else {
            log.debug("updateWatchingUpNextRow: cursor = null getting old mWatchingUpNextAdapter cursor");
            cursor = mWatchingUpNextAdapter.getCursor();
        }
        int currentPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT);
        if (cursor != null) {
            if (cursor.getCount() == 0 || !mShowWatchingUpNextRow) {
                log.debug("updateWatchingUpNextRow: cursor not null and row not shown thus removing currentPosition={}", currentPosition);
                if (currentPosition != -1)
                    mRowsAdapter.removeItems(currentPosition, 1);
            } else {
                if (currentPosition == -1) {
                    int newPosition = 0;
                    if (getRowPosition(ROW_ID_LAST_ADDED) != -1)
                        newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                    log.debug("updateWatchingUpNextRow: cursor not null and row shown thus adding newPosition={}", newPosition);
                    mRowsAdapter.add(newPosition, mWatchingUpNextRow);
                }
            }
        } else {
            log.warn("updateWatchingUpNextRow: cursor still null!!!");
        }
        debugRows("updateWatchingUpNextRow");
    }

    private void updateLastAddedRow(Cursor cursor) {
        log.debug("updateLastAddedRow");
        if (cursor != null) mLastAddedAdapter.changeCursor(cursor);
        else cursor = mLastAddedAdapter.getCursor();
        int currentPosition = getRowPosition(ROW_ID_LAST_ADDED);
        if (cursor == null || cursor.getCount() == 0 || !mShowLastAddedRow) {
            if (cursor == null) log.debug("updateLastAddedRow: cursor is null");
            else log.debug("updateLastAddedRow: cursor has {} elements", cursor.getCount());
            if (currentPosition != -1) {
                log.debug("updateLastAddedRow: removing currentPosition={}", currentPosition);
                mRowsAdapter.removeItems(currentPosition, 1);
            }
        } else {
            if (currentPosition == -1) {
                int newPosition = 0;
                log.debug("updateLastAddedRow: adding at newPosition={} if {}", newPosition, mShowLastAddedRow);
                if (mShowLastAddedRow) mRowsAdapter.add(newPosition, mLastAddedRow);
            }
        }
        debugRows("updateLastAddedRow");
    }

    private void updateLastPlayedRow(Cursor cursor) {
        log.debug("updateLastPlayedRow");
        if (cursor != null) mLastPlayedAdapter.changeCursor(cursor);
        else cursor = mLastPlayedAdapter.getCursor();
        int currentPosition = getRowPosition(ROW_ID_LAST_PLAYED);
        if (cursor == null || cursor.getCount() == 0 || !mShowLastPlayedRow) {
            if (currentPosition != -1) // it exists thus we remove
                mRowsAdapter.removeItems(currentPosition, 1);
        } else {
            if (currentPosition == -1) {
                int newPosition = 0;
                if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1)
                    newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                else if (getRowPosition(ROW_ID_LAST_ADDED) != -1)
                    newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                if (mShowLastPlayedRow) mRowsAdapter.add(newPosition, mLastPlayedRow);
            }
        }
        debugRows("updateLastPlayedRow");
    }

    private void updateMoviesRow(Cursor cursor, Boolean updateBox) {
        log.debug("updateMoviesRow: updateBox {}", updateBox);
        if (cursor != null) mMoviesAdapter.changeCursor(cursor);
        else cursor = mMoviesAdapter.getCursor();
        int currentPosition = getRowPosition(ROW_ID_ALL_MOVIES);
        log.debug("updateMoviesRow: current position of all movies row {}", currentPosition);
        if (cursor == null || cursor.getCount() == 0 || !mShowMoviesRow) { // NOT ALL MOVIES
            log.debug("updateMoviesRow: not all movies");
            if (currentPosition != -1) { // if ALL MOVIES ROW remove it
                log.debug("updateMoviesRow: remove all movies row at position {}", currentPosition);
                mRowsAdapter.removeItems(currentPosition, 1);
            }
            if (getRowPosition(ROW_ID_MOVIES) == -1) {
                int newPosition = 0; // init at row 0
                if (getRowPosition(ROW_ID_LAST_PLAYED) != -1) // if LAST PLAYED ROW put it after
                    newPosition = getRowPosition(ROW_ID_LAST_PLAYED) + 1;
                else if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1) // otherwise put it after WATCHING UP NEXT
                    newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                else if (getRowPosition(ROW_ID_LAST_ADDED) != -1) // otherwise put it after LAST ADDED ROW
                    newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                log.debug("updateMoviesRow: adding movies row at {}", newPosition);
                mRowsAdapter.add(newPosition, mMovieRow);
            }
            if (! mShowMoviesRow && updateBox) {
                refreshAllMoviesBox();
                refreshAllCollectionsBox();
            }
        } else { // ALL MOVIES CASE
            log.debug("updateMoviesRow: all movies");
            int position = getRowPosition(ROW_ID_MOVIES);
            if (position != -1) { // if MOVIES ROW remove it
                log.debug("updateMoviesRow: remove movies row at position {}", position);
                mRowsAdapter.removeItems(position, 1);
            }
            if (currentPosition == -1) {
                int newPosition = 0; // init at row 0
                if (getRowPosition(ROW_ID_LAST_PLAYED) != -1) // if LAST PLAYED ROW put it after
                    newPosition = getRowPosition(ROW_ID_LAST_PLAYED) + 1;
                else if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1) // otherwise put it after WATCHING UP NEXT
                    newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                else if (getRowPosition(ROW_ID_LAST_ADDED) != -1) // otherwise put it after LAST ADDED ROW
                    newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                log.debug("updateMoviesRow: adding movies row at {}", newPosition);
                mRowsAdapter.add(newPosition, mMoviesRow);
            }
        }
        debugRows("updateMoviesRow");
    }

    private void updateTvShowsRow(Cursor cursor, Boolean updateBox) {
        log.debug("updateTvShowsRow: updateBox {}", updateBox);
        if (cursor != null) mTvshowsAdapter.changeCursor(cursor);
        else cursor = mTvshowsAdapter.getCursor();
        int currentPosition = getRowPosition(ROW_ID_ALL_TVSHOWS);
        log.debug("updateTvShowsRow: current position of all tvshows row {}", currentPosition);
        if (cursor == null || cursor.getCount() == 0 || !mShowTvshowsRow) {
            log.debug("updateTvShowsRow: not all tvshows");
            if (currentPosition != -1) {
                log.debug("updateTvShowsRow: remove all tvshows row at position {}", currentPosition);
                mRowsAdapter.removeItems(currentPosition, 1);
            }
            if (getRowPosition(ROW_ID_TVSHOW) == -1) {
                int newPosition = 0;
                if (getRowPosition(ROW_ID_MOVIES) != -1)
                    newPosition = getRowPosition(ROW_ID_MOVIES) + 1;
                else if (getRowPosition(ROW_ID_ALL_MOVIES) != -1)
                    newPosition = getRowPosition(ROW_ID_ALL_MOVIES) + 1;
                else if (getRowPosition(ROW_ID_LAST_PLAYED) != -1)
                    newPosition = getRowPosition(ROW_ID_LAST_PLAYED) + 1;
                else if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1)
                    newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                else if (getRowPosition(ROW_ID_LAST_ADDED) != -1)
                    newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                log.debug("updateTvShowsRow: adding tvshows row at {}", newPosition);
                mRowsAdapter.add(newPosition, mTvshowRow);
            }
            if (! mShowTvshowsRow && updateBox) refreshAllTvshowsBox();
        } else {
            int position = getRowPosition(ROW_ID_TVSHOW);
            if (position != -1) {
                log.debug("updateTvShowsRow: remove tvshows row at position {}", position);
                mRowsAdapter.removeItems(position, 1);
            }
            if (currentPosition == -1) {
                int newPosition = 0;
                if (getRowPosition(ROW_ID_MOVIES) != -1)
                    newPosition = getRowPosition(ROW_ID_MOVIES) + 1;
                else if (getRowPosition(ROW_ID_ALL_MOVIES) != -1)
                    newPosition = getRowPosition(ROW_ID_ALL_MOVIES) + 1;
                else if (getRowPosition(ROW_ID_LAST_PLAYED) != -1)
                    newPosition = getRowPosition(ROW_ID_LAST_PLAYED) + 1;
                else if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1)
                    newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                else if (getRowPosition(ROW_ID_LAST_ADDED) != -1)
                    newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                log.debug("updateTvShowsRow: adding all tvshows row at {}", newPosition);
                mRowsAdapter.add(newPosition, mTvshowsRow);
            }
        }
        debugRows("updateTvShowsRow");
    }

    private void updateAnimesRow(Cursor cursor, Boolean updateBox) {
        log.debug("updateAnimesRow: updateBox {}", updateBox);
        if (cursor != null) mAnimesAdapter.changeCursor(cursor);
        else cursor = mAnimesAdapter.getCursor();
        int currentPosition = getRowPosition(ROW_ID_ALL_ANIMES);
        log.debug("updateAnimesRow: current position of all animes row {}", currentPosition);
        if (cursor ==null || cursor.getCount() == 0 || !mShowAnimesRow) {
            log.debug("updateAnimesRow: not all animes");
            if (currentPosition != -1) {
                log.debug("updateAnimesRow: remove all animations row at position {}", currentPosition);
                mRowsAdapter.removeItems(currentPosition, 1);
            }
            if (mSeparateAnimeFromShowMovie) {
                if (getRowPosition(ROW_ID_ANIMES) == -1) {
                    int newPosition = 0;
                    if (getRowPosition(ROW_ID_TVSHOW) != -1)
                        newPosition = getRowPosition(ROW_ID_TVSHOW) + 1;
                    else if (getRowPosition(ROW_ID_ALL_TVSHOWS) != -1)
                        newPosition = getRowPosition(ROW_ID_ALL_TVSHOWS) + 1;
                    else if (getRowPosition(ROW_ID_MOVIES) != -1)
                        newPosition = getRowPosition(ROW_ID_MOVIES) + 1;
                    else if (getRowPosition(ROW_ID_ALL_MOVIES) != -1)
                        newPosition = getRowPosition(ROW_ID_ALL_MOVIES) + 1;
                    else if (getRowPosition(ROW_ID_LAST_PLAYED) != -1)
                        newPosition = getRowPosition(ROW_ID_LAST_PLAYED) + 1;
                    else if (getRowPosition(ROW_ID_LAST_ADDED) != -1)
                        newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                    else if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1)
                        newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                    log.debug("updateAnimesRow: adding animations row at {}", newPosition);
                    mRowsAdapter.add(newPosition, mAnimeRow);
                }
                if (! mShowAnimesRow && updateBox) {
                    refreshAllAnimesBox();
                    refreshAllAnimeShowsBox();
                }
            } else {
                // remove row mAnimeRow
                mRowsAdapter.remove(mAnimeRow);
            }
        } else {
            int position = getRowPosition(ROW_ID_ANIMES);
            if (position != -1) {
                log.debug("updateAnimesRow: remove animations row at position {}", position);
                mRowsAdapter.removeItems(position, 1);
            }
            if (mSeparateAnimeFromShowMovie) {
                if (currentPosition == -1) {
                    int newPosition = 0;
                    if (getRowPosition(ROW_ID_TVSHOW) != -1)
                        newPosition = getRowPosition(ROW_ID_TVSHOW) + 1;
                    else if (getRowPosition(ROW_ID_ALL_TVSHOWS) != -1)
                        newPosition = getRowPosition(ROW_ID_ALL_TVSHOWS) + 1;
                    else if (getRowPosition(ROW_ID_MOVIES) != -1)
                        newPosition = getRowPosition(ROW_ID_MOVIES) + 1;
                    else if (getRowPosition(ROW_ID_ALL_MOVIES) != -1)
                        newPosition = getRowPosition(ROW_ID_ALL_MOVIES) + 1;
                    else if (getRowPosition(ROW_ID_LAST_PLAYED) != -1)
                        newPosition = getRowPosition(ROW_ID_LAST_PLAYED) + 1;
                    else if (getRowPosition(ROW_ID_LAST_ADDED) != -1)
                        newPosition = getRowPosition(ROW_ID_LAST_ADDED) + 1;
                    else if (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1)
                        newPosition = getRowPosition(ROW_ID_WATCHING_UP_NEXT) + 1;
                    log.debug("updateAnimesRow: adding all animations row at {}", newPosition);
                    mRowsAdapter.add(newPosition, mAnimesRow);
                }
            } else {
                mRowsAdapter.remove(mAnimesRow);
            }
        }
        debugRows("updateAnimesRow");
    }

    private void updateNonScrapedVideosVisibility(Cursor cursor) {
        int count = NonScrapedVideosCountLoader.getNonScrapedVideoCount(cursor);

        //I have basic mode, so we may not even have mNonScrapedVideosItem!
        if (mNonScrapedVideosItem == null) {
            log.debug("updateNonScrapedVideosVisibility: mNonScrapedVideosItem is null.");
        } else {
            if (mFileBrowsingRowAdapter != null) {
                int currentIndex = mFileBrowsingRowAdapter.indexOf(mNonScrapedVideosItem);
                log.debug("updateNonScrapedVideosVisibility: count={}, currentIndex={}", count, currentIndex);
                if (count > 0) {
                    if (currentIndex < 0) {
                        log.debug("updateNonScrapedVideosVisibility: adding non-scraped box");
                        mFileBrowsingRowAdapter.add(mNonScrapedVideosItem);
                    } else {
                        log.debug("updateNonScrapedVideosVisibility: non-scraped box already present at index {}", currentIndex);
                    }
                } else {
                    log.debug("updateNonScrapedVideosVisibility: removing non-scraped box");
                    mFileBrowsingRowAdapter.remove(mNonScrapedVideosItem);
                }
            }
        }
    }

    /**
     *
     * @param rowId
     * @return -1 if the row is not present
     */
    private int getRowPosition(int rowId) {
        for (int i=0; i<mRowsAdapter.size(); i++) {
            Object o = mRowsAdapter.get(i);
            if (o instanceof ListRow) {
                if (((ListRow)o).getId() == rowId) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @param adapter
     * @return true if the adapter is an ArrayObjectAdapter containing just one EmptyView object
     */
    private boolean isEmptyViewRow(ObjectAdapter adapter) {
        if (adapter instanceof ArrayObjectAdapter) {
            ArrayObjectAdapter arrayObjectAdapter = (ArrayObjectAdapter)adapter;
            if (arrayObjectAdapter.size()==1) {
                if (arrayObjectAdapter.get(0) instanceof EmptyView) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mActivity = getActivity();
        if (mActivity == null) log.warn("onCreateLoader: mActivity is null!");
        switch (id) {
            case LOADER_ID_WATCHING_UP_NEXT -> {
                log.debug("onCreateLoader WATCHING_UP_NEXT");
                return new WatchingUpNextLoader(mActivity);
            }
            case LOADER_ID_LAST_ADDED -> {
                log.debug("onCreateLoader LAST_ADDED");
                return new LastAddedLoader(mActivity);
            }
            case LOADER_ID_LAST_PLAYED -> {
                log.debug("onCreateLoader LAST_PLAYED");
                return new LastPlayedLoader(mActivity);
            }
            case LOADER_ID_ALL_MOVIES -> {
                log.debug("onCreateLoader ALL_MOVIES");
                if (mSeparateAnimeFromShowMovie) {
                    if (args == null)
                        return new FilmsLoader(mActivity, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                    else
                        return new FilmsLoader(mActivity, args.getString("sort"), true, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                } else {
                    if (args == null)
                        return new MoviesLoader(mActivity, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                    else
                        return new MoviesLoader(mActivity, args.getString("sort"), true, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                }
            }
            case LOADER_ID_ALL_TV_SHOWS -> {
                log.debug("onCreateLoader ALL_TV_SHOWS");
                if (mSeparateAnimeFromShowMovie) {
                    if (args == null)
                        return new AllTvshowsNoAnimeLoader(mActivity, TvshowSortOrderEntries.DEFAULT_SORT, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                    else
                        return new AllTvshowsNoAnimeLoader(mActivity, args.getString("sort"), true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                } else {
                    if (args == null)
                        return new AllTvshowsLoader(mActivity, TvshowSortOrderEntries.DEFAULT_SORT, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                    else
                        return new AllTvshowsLoader(mActivity, args.getString("sort"), true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                }
            }
            case LOADER_ID_ALL_ANIMES -> {
                log.debug("onCreateLoader ALL_ANIMES");
                if (mSeparateAnimeFromShowMovie) {
                    if (args == null)
                        return new AnimesNShowsLoader(mActivity, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                    else
                        return new AnimesNShowsLoader(mActivity, args.getString("sort"), true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                } else {
                    if (args == null) return new AnimesLoader(mActivity, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                    else
                        return new AnimesLoader(mActivity, args.getString("sort"), true, true, VideoLoader.ALLVIDEO_THROTTLE, VideoLoader.ALLVIDEO_THROTTLE_DELAY);
                }
            }
            case LOADER_ID_NON_SCRAPED_VIDEOS_COUNT -> {
                log.debug("onCreateLoader NON_SCRAPED");
                return new NonScrapedVideosCountLoader(mActivity);
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (updateActivity("onLoadFinished") == null) return;
        boolean scanningOnGoing = NetworkScannerReceiver.isScannerWorking() || AutoScrapeService.isScraping() || ImportState.VIDEO.isInitialImport();
        log.debug("onLoadFinished: cursor id={}, scanningOnGoing={}", cursorLoader.getId(), scanningOnGoing);

        // Check if this loader has completed its initial load
        boolean isInitialLoadComplete = false;
        switch (cursorLoader.getId()) {
            case LOADER_ID_WATCHING_UP_NEXT -> isInitialLoadComplete = mWatchingUpNextInitialLoadComplete;
            case LOADER_ID_LAST_ADDED -> isInitialLoadComplete = mLastAddedInitialLoadComplete;
            case LOADER_ID_LAST_PLAYED -> isInitialLoadComplete = mLastPlayedInitialLoadComplete;
            case LOADER_ID_ALL_MOVIES -> isInitialLoadComplete = mMoviesInitialLoadComplete;
            case LOADER_ID_ALL_ANIMES -> isInitialLoadComplete = mAnimesInitialLoadComplete;
            case LOADER_ID_ALL_TV_SHOWS -> isInitialLoadComplete = mTvshowsInitialLoadComplete;
            case LOADER_ID_NON_SCRAPED_VIDEOS_COUNT -> isInitialLoadComplete = mNonScrapedVideosInitialLoadComplete;
        }

        if (cursor != null && ! cursor.isClosed()) { // seen on sentry sometimes cursor is already closed
            // Skip UI updates during scanning/scraping ONLY after initial load to prevent SQLite contention
            // But still swap cursors to prevent closed cursor errors
            boolean skipUIUpdate = scanningOnGoing && isInitialLoadComplete;

            switch (cursorLoader.getId()) {
                case LOADER_ID_WATCHING_UP_NEXT -> {
                    if (!skipUIUpdate) {
                        if (mShowWatchingUpNextRow && mWatchingUpNextInitFocus == InitFocus.NOT_FOCUSED)
                            mWatchingUpNextInitFocus = cursor.getCount() > 0 ? InitFocus.NEED_FOCUS : InitFocus.NO_NEED_FOCUS;
                        log.debug("onLoadFinished: WatchingUpNext cursor ready with {} entries and {}, updating row", cursor.getCount(), mWatchingUpNextInitFocus);
                        if (mShowWatchingUpNextRow) updateWatchingUpNextRow(cursor);
                    } else {
                        log.debug("onLoadFinished: WatchingUpNext skipping UI update during scanning, but swapping cursor");
                        if (mWatchingUpNextAdapter != null) mWatchingUpNextAdapter.changeCursor(cursor);
                    }
                    mWatchingUpNextInitialLoadComplete = true;
                }
                case LOADER_ID_LAST_ADDED -> {
                    if (!skipUIUpdate) {
                        if (mShowLastAddedRow && mLastAddedInitFocus == InitFocus.NOT_FOCUSED)
                            mLastAddedInitFocus = cursor.getCount() > 0 ? InitFocus.NEED_FOCUS : InitFocus.NO_NEED_FOCUS;
                        log.debug("onLoadFinished: LastAdded cursor ready with {} entries and {}, updating row", cursor.getCount(), mLastAddedInitFocus);
                        if (mShowLastAddedRow) updateLastAddedRow(cursor);
                    } else {
                        log.debug("onLoadFinished: LastAdded skipping UI update during scanning, but swapping cursor");
                        if (mLastAddedAdapter != null) mLastAddedAdapter.changeCursor(cursor);
                    }
                    mLastAddedInitialLoadComplete = true;
                }
                case LOADER_ID_LAST_PLAYED -> {
                    if (!skipUIUpdate) {
                        if (mShowLastPlayedRow && mLastPlayedInitFocus == InitFocus.NOT_FOCUSED)
                            mLastPlayedInitFocus = cursor.getCount() > 0 ? InitFocus.NEED_FOCUS : InitFocus.NO_NEED_FOCUS;
                        log.debug("onLoadFinished: LastPlayed cursor ready with {} entries and {}, updating row", cursor.getCount(), mLastAddedInitFocus);
                        if (mShowLastPlayedRow) {
                            // Check if the video at position 0 has changed (indicating a new video was played)
                            long newPosition0VideoId = -1;
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                int idColumnIndex = cursor.getColumnIndex(VideoStore.Video.VideoColumns._ID);
                                if (idColumnIndex != -1) {
                                    newPosition0VideoId = cursor.getLong(idColumnIndex);
                                }
                            }
                            boolean videoPosition0Changed = (mLastPlayedPosition0VideoId != -1 && newPosition0VideoId != -1 && mLastPlayedPosition0VideoId != newPosition0VideoId);
                            log.debug("onLoadFinished: LastPlayed position 0 video ID changed from {} to {}, changed={}", mLastPlayedPosition0VideoId, newPosition0VideoId, videoPosition0Changed);
                            mLastPlayedPosition0VideoId = newPosition0VideoId;

                            updateLastPlayedRow(cursor);
                            // Only reset item position to 0 if we're currently on that row AND a new video was played
                            // This ensures that when returning from video playback, the focus goes to position 0
                            // where the just-played video now is, but stays at the current position if no video was played
                            int lastPlayedRowPosition = getRowPosition(ROW_ID_LAST_PLAYED);
                            if (videoPosition0Changed && lastPlayedRowPosition != -1 && lastPlayedRowPosition == getSelectedPosition()) {
                                log.debug("onLoadFinished: LastPlayed row is currently selected and video at position 0 changed, resetting item position to 0");
                                // Use setSelectedPosition with SelectItemViewHolderTask to reset horizontal position
                                setSelectedPosition(lastPlayedRowPosition, false, new ListRowPresenter.SelectItemViewHolderTask(0));
                            }
                        }
                    } else {
                        log.debug("onLoadFinished: LastPlayed skipping UI update during scanning, but swapping cursor");
                        if (mLastPlayedAdapter != null) mLastPlayedAdapter.changeCursor(cursor);
                    }
                    mLastPlayedInitialLoadComplete = true;
                }
                case LOADER_ID_ALL_MOVIES -> {
                    if (!skipUIUpdate) {
                        log.debug("onLoadFinished: AllMovies cursor ready with {} entries, updating row/box", cursor.getCount());
                        // cannot use if (isCursorCountChanged(mLastAddedAdapter.getCursor(), cursor)) because when row is full it is 100 always
                        if (mShowMoviesRow) updateMoviesRow(cursor, false);
                    } else {
                        log.debug("onLoadFinished: AllMovies skipping UI update during scanning, but swapping cursor");
                        if (mMoviesAdapter != null) mMoviesAdapter.changeCursor(cursor);
                    }
                    mMoviesInitialLoadComplete = true;
                }
                case LOADER_ID_ALL_TV_SHOWS -> {
                    if (!skipUIUpdate) {
                        log.debug("onLoadFinished: AllTvShows cursor ready with {} entries, updating row/box", cursor.getCount());
                        if (mShowTvshowsRow) updateTvShowsRow(cursor, false);
                    } else {
                        log.debug("onLoadFinished: AllTvShows skipping UI update during scanning, but swapping cursor");
                        if (mTvshowsAdapter != null) mTvshowsAdapter.changeCursor(cursor);
                    }
                    mTvshowsInitialLoadComplete = true;
                }
                case LOADER_ID_ALL_ANIMES -> {
                    if (!skipUIUpdate) {
                        log.debug("onLoadFinished: AllAnimes cursor ready with {} entries, updating row/box", cursor.getCount());
                        if (mShowAnimesRow && mSeparateAnimeFromShowMovie)
                            updateAnimesRow(cursor, false);
                    } else {
                        log.debug("onLoadFinished: AllAnimes skipping UI update during scanning, but swapping cursor");
                        if (mAnimesAdapter != null) mAnimesAdapter.changeCursor(cursor);
                    }
                    mAnimesInitialLoadComplete = true;
                }
                case LOADER_ID_NON_SCRAPED_VIDEOS_COUNT -> {
                    if (!skipUIUpdate) {
                        log.debug("onLoadFinished: NonScrapedVideos cursor ready, count={}", NonScrapedVideosCountLoader.getNonScrapedVideoCount(cursor));
                        updateNonScrapedVideosVisibility(cursor);
                    } else {
                        log.debug("onLoadFinished: NonScrapedVideos skipping UI update during scanning");
                        // This loader doesn't use a cursor adapter, just skip entirely
                    }
                    mNonScrapedVideosInitialLoadComplete = true;
                }
            }
            checkInitFocus();
        } else {
            log.warn("onLoadFinished: cursor is null or closed!!!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { }

    private enum InitFocus {
        NOT_FOCUSED, NO_NEED_FOCUS, NEED_FOCUS, FOCUSED
    }

    /**
     * When opening, WatchingUpNext, LastAdded and LastPlayed rows are not created yet, hence selection is on Movies.
     * Here we wait for the Loaders to return their results to know if we need to select the first row again (which will be WatchingUpNext, LastAdded or LastPlayed)
     */
    private InitFocus mWatchingUpNextInitFocus = InitFocus.NOT_FOCUSED;
    private InitFocus mLastAddedInitFocus = InitFocus.NOT_FOCUSED;
    private InitFocus mLastPlayedInitFocus = InitFocus.NOT_FOCUSED;

    private void checkInitFocus() { // sets focus on line 0 when there is a line 0 loader update
        // Check if we have WatchingUpNext, LastAdded and LastPlayed loader results
        log.debug("checkInitFocus: mLastAddedInitFocus={}, mLastPlayedInitFocus={}, mWatchingUpNextInitFocus={}, mShowWatchingUpNextRow={}, mShowLastAddedRow={}, mShowLastPlayedRow={}", mLastAddedInitFocus, mLastPlayedInitFocus, mWatchingUpNextInitFocus, mShowWatchingUpNextRow, mShowLastAddedRow, mShowLastPlayedRow);
        if (mWatchingUpNextInitFocus == InitFocus.NEED_FOCUS) {
            log.debug("checkInitFocus: WatchingUpNext loader ready and needs focus and row is {}", (getRowPosition(ROW_ID_WATCHING_UP_NEXT) != -1 ? "present" : "absent"));
            mWatchingUpNextInitFocus = InitFocus.FOCUSED;
            //mLastAddedInitFocus = InitFocus.NO_NEED_FOCUS;
            //mLastPlayedInitFocus = InitFocus.NO_NEED_FOCUS;
            if (FEATURE_WATCH_UP_NEXT && getRowPosition(ROW_ID_WATCHING_UP_NEXT) == -1) return;
        } else if (mLastAddedInitFocus == InitFocus.NEED_FOCUS) {
            log.debug("checkInitFocus: LastAdded loader ready and needs focus and row is {}", (getRowPosition(ROW_ID_LAST_ADDED) != -1 ? "present" : "absent"));
            mLastAddedInitFocus = InitFocus.FOCUSED;
            // removing for now since it causes first row not to be focused since isLastAddedRowVisible=true but in reality it is MoviesRow displayed
            //mLastPlayedInitFocus = InitFocus.NO_NEED_FOCUS;
            if (getRowPosition(ROW_ID_LAST_ADDED) == -1) return;
        } else if (mLastPlayedInitFocus == InitFocus.NEED_FOCUS) { // check if row is visible to avoid selecting MoviesRow in case of slow row display
            log.debug("checkInitFocus: LastPlayed loader ready and needs focus and row is {}", (getRowPosition(ROW_ID_LAST_PLAYED) != -1 ? "present" : "absent"));
            mLastPlayedInitFocus = InitFocus.FOCUSED;
            if (getRowPosition(ROW_ID_LAST_PLAYED) == -1) return;
        } else {
            log.debug("checkInitFocus: there was a cursor update on one that is tagged with FOCUSED OR NO_NEED_FOCUS");
            return; /// if nobody needs focus then exit
        }
        log.debug("checkInitFocus: sets focus on row 0 with animation if above rows were not visible it happens on network first");
        if ((FEATURE_WATCH_UP_NEXT && mShowWatchingUpNextRow) || mShowLastAddedRow || mShowLastPlayedRow) this.setSelectedPosition(0, true);
    }

    /**
     * Update (un)mount sdcard/usb host
     */
    private final BroadcastReceiver mExternalStorageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ExtStorageReceiver.ACTION_MEDIA_MOUNTED)){
                log.debug("mExternalStorageReceiver: ACTION_MEDIA_MOUNTED");
                // Remove "file://"
                String path = null;
                if(intent.getDataString().startsWith("file"))
                    path = intent.getDataString().substring("file".length());
                else if (intent.getDataString().startsWith(ExtStorageReceiver.LEEROYFLIX_FILE_SCHEME))
                    path = intent.getDataString().substring(ExtStorageReceiver.LEEROYFLIX_FILE_SCHEME.length());

                if (path == null || path.isEmpty())
                    return;
                updateUsbAndSdcardVisibility();
            }
            else if (action.equals(ExtStorageReceiver.ACTION_MEDIA_CHANGED)){
                updateUsbAndSdcardVisibility();
            }
            else if (action.equals(ExtStorageReceiver.ACTION_MEDIA_UNMOUNTED)){
                log.debug("mExternalStorageReceiver: ACTION_MEDIA_UNMOUNTED");
                updateUsbAndSdcardVisibility();
            }
        }
    };

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            log.debug("mUpdateReceiver: received intent!!!");
            if (context != null && intent != null && intent.getAction().equals(LeeroyFlixMediaIntent.ACTION_VIDEO_SCANNER_SCAN_FINISHED)) {
                log.debug("mUpdateReceiver: update all boxes");
                refreshAllBoxes();
            }
        }
    };

    private void updateUsbAndSdcardVisibility() {
        log.debug("updateUsbAndSdcardVisibility");
        if (mHideExternal ) return;     //DONT DO ANY OF IT IF WE ARE HIDING EXTERNAL 
        ExtStorageManager storageManager = ExtStorageManager.getExtStorageManager();
        final boolean hasExternal = storageManager.hasExtStorage();

        // Remember if non-scraped box was present before clearing
        boolean nonScrapedWasPresent = mFileBrowsingRowAdapter.indexOf(mNonScrapedVideosItem) >= 0;
        log.debug("updateUsbAndSdcardVisibility: nonScrapedWasPresent={}", nonScrapedWasPresent);

        mFileBrowsingRowAdapter.clear();
        mFileBrowsingRowAdapter.add(new Box(Box.ID.NETWORK, getString(R.string.network_storage), R.drawable.filetype_new_server));
        mFileBrowsingRowAdapter.add(new Box(Box.ID.FOLDERS, getString(R.string.internal_storage), R.drawable.filetype_new_folder));

        if (hasExternal) {
            for(String s : storageManager.getExtSdcards()) {
                Box item = new Box(Box.ID.SDCARD, getString(R.string.sd_card_storage) + getVolumeDescription(s), R.drawable.filetype_new_sdcard, s);
                mFileBrowsingRowAdapter.add(item);
            }
            for(String s : storageManager.getExtUsbStorages()) {
                Box item = new Box(Box.ID.USB, getString(R.string.usb_host_storage) + getVolumeDescription(s), R.drawable.filetype_new_usb, s);
                mFileBrowsingRowAdapter.add(item);
            }
            for(String s : storageManager.getExtOtherStorages()) {
                Box item = new Box(Box.ID.OTHER, getString(R.string.other_storage) + getVolumeDescription(s), R.drawable.filetype_new_folder, s);
                mFileBrowsingRowAdapter.add(item);
            }
        }

        // Re-add non-scraped box if it was present before
        if (nonScrapedWasPresent) {
            log.debug("updateUsbAndSdcardVisibility: re-adding non-scraped box");
            mFileBrowsingRowAdapter.add(mNonScrapedVideosItem);
        }

        mFileBrowsingRowAdapter.add(new Box(Box.ID.VIDEOS_BY_LISTS, getString(R.string.video_lists), R.drawable.filetype_new_playlist));
    }

    private String getVolumeDescription(String s) {
        ExtStorageManager storageManager = ExtStorageManager.getExtStorageManager();
        String volLabel = storageManager.getVolumeLabel(s);
        String volDescr = storageManager.getVolumeDesc(s);
        String descr = "";
        if (volDescr != null && ! volDescr.isEmpty()) descr = volDescr;
        if (volLabel != null && ! volLabel.isEmpty())
            descr = (descr.isEmpty()) ? volLabel : volLabel + " (" + descr + ")"; // volume label is the primary if it exists otherwise volume description is the one
        if (! descr.isEmpty()) descr = ": " + descr;
        return descr;
    }

    //private void updatePrivateMode(Icon icon) {
    //    icon.setActive(PrivateMode.isActive());
    //    mPreferencesRowAdapter.replace(mPreferencesRowAdapter.indexOf(icon), icon);
    //    updateBackground();
    //}

    //private void findAndUpdatePrivateModeIcon() {
    //    if (mPreferencesRowAdapter != null) {
    //        for(int i=0 ; i<mPreferencesRowAdapter.size() ; i++) {
    //            Object item = mPreferencesRowAdapter.get(i);
    //            if (item instanceof Icon) {
    //                Icon icon = (Icon) item;
    //                if (icon.getId() == Icon.ID.PRIVATE_MODE) {
    //                    if (icon.isActive() != PrivateMode.isActive()) {
    //                        updatePrivateMode(icon);
    //                    }
    //                    break;
    //                }
    //            }
    //        }
    //    }
    //}
    
    protected void rescanAvailableShortcuts() {
        NetworkAutoRefresh.forceRescan(getActivity());
    }

    public class MainViewClickedListener extends VideoViewClickedListener {

        final private Activity vActivity;

        public MainViewClickedListener(Activity activity) {
            super(activity);
            vActivity = activity;
        }

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Box) {
                Box box = (Box)item;
                switch (box.getBoxId()) {
                    case ALL_MOVIES ->
                            vActivity.startActivity(new Intent(vActivity, AllMoviesGridActivity.class));
                    case MOVIES_BY_ALPHA ->
                            vActivity.startActivity(new Intent(vActivity, MoviesByAlphaActivity.class));
                    case MOVIES_BY_GENRE ->
                            vActivity.startActivity(new Intent(vActivity, MoviesByGenreActivity.class));
                    case MOVIES_BY_RATING ->
                            vActivity.startActivity(new Intent(vActivity, MoviesByRatingActivity.class));
                    case MOVIES_BY_YEAR ->
                            vActivity.startActivity(new Intent(vActivity, MoviesByYearActivity.class));
                    case ALL_ANIMES ->
                            vActivity.startActivity(new Intent(vActivity, AllAnimesGridActivity.class));
                    case ANIMES_BY_GENRE ->
                            vActivity.startActivity(new Intent(vActivity, AnimesByGenreActivity.class));
                    case ANIMES_BY_YEAR ->
                            vActivity.startActivity(new Intent(vActivity, AnimesByYearActivity.class));
                    case ALL_ANIMESHOWS ->
                            vActivity.startActivity(new Intent(vActivity, AllAnimeShowsGridActivity.class));
                    case VIDEOS_BY_LISTS ->
                            vActivity.startActivity(new Intent(vActivity, VideosByListActivity.class));
                    case FOLDERS ->
                            vActivity.startActivity(new Intent(vActivity, LocalListingActivity.class));
                    case SDCARD, USB, OTHER -> {
                        Intent i = new Intent(vActivity, ExtStorageListingActivity.class);
                        i.putExtra(ExtStorageListingActivity.MOUNT_POINT, box.getPath());
                        i.putExtra(ExtStorageListingActivity.STORAGE_NAME, box.getName());
                        vActivity.startActivity(i);
                    }
                    case NETWORK ->
                            vActivity.startActivity(new Intent(vActivity, NetworkRootActivity.class));
                    case NON_SCRAPED_VIDEOS ->
                            vActivity.startActivity(new Intent(vActivity, NonScrapedVideosActivity.class));
                    case ALL_TVSHOWS ->
                            vActivity.startActivity(new Intent(vActivity, AllTvshowsGridActivity.class));
                    case TVSHOWS_BY_ALPHA ->
                            vActivity.startActivity(new Intent(vActivity, TvshowsByAlphaActivity.class));
                    case TVSHOWS_BY_GENRE ->
                            vActivity.startActivity(new Intent(vActivity, TvshowsByGenreActivity.class));
                    case TVSHOWS_BY_RATING ->
                            vActivity.startActivity(new Intent(vActivity, TvshowsByRatingActivity.class));
                    case EPISODES_BY_DATE ->
                            vActivity.startActivity(new Intent(vActivity, EpisodesByDateActivity.class));
                    case COLLECTIONS ->
                            vActivity.startActivity(new Intent(vActivity, AllCollectionsGridActivity.class));
                    case ANIME_COLLECTIONS ->
                            vActivity.startActivity(new Intent(vActivity, AllAnimeCollectionsGridActivity.class));
                }
            }
            else if (item instanceof Icon) {
                Icon icon = (Icon)item;
                switch (icon.getId()) {
                    case PREFERENCES:
                        if (vActivity instanceof LeeroyFlixActivityLeanback)
                            ((LeeroyFlixActivityLeanback)vActivity).startPreferencesActivity(); // I know this is ugly (and i'm ashamed...)
                        else
                            throw new IllegalStateException("Sorry developer, this ugly code can work with a MainActivityLeanback only for now!");
                        break;
                    case RESCRAPE:
                        rescanAvailableShortcuts();
                    //case PRIVATE_MODE:
                    //    if (!PrivateMode.isActive() && PrivateMode.canShowDialog(vActivity))
                    //        PrivateMode.showDialog(vActivity);
                    //    PrivateMode.toggle();
                    //    mPrefs.edit().putBoolean(PREF_PRIVATE_MODE, PrivateMode.isActive()).apply();
                    //    updatePrivateMode(icon);
                    //    break;
                    //case LEGACY_UI:
                    //    new DensityTweak(vActivity)
                    //            .temporaryRestoreDefaultDensity();
                    //    vActivity.startActivity(new Intent(vActivity, LeeroyFlixActivity.class));
                    //    break;
                    //case HELP_FAQ:
                    //    WebUtils.openWebLink(vActivity,getString(R.string.faq_url));
                    //    break;
                    //case SPONSOR:
                    //    WebUtils.openWebLink(vActivity,getString(R.string.sponsor_url));
                    //    break;
                }
            }
            else {
                super.onItemClicked(itemViewHolder, item, rowViewHolder, row);
            }
        }
    }

}
