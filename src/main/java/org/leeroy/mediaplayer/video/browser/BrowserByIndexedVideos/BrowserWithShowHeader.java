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


package org.leeroy.mediaplayer.video.browser.BrowserByIndexedVideos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.leeroy.mediaplayer.utils.ActionBarSubmenu;
import org.leeroy.mediaplayer.utils.imageview.ImageProcessor;
import org.leeroy.mediaplayer.utils.imageview.ImageViewSetter;
import org.leeroy.mediaplayer.utils.imageview.ImageViewSetterConfiguration;
import org.leeroy.mediaplayer.video.R;
import org.leeroy.mediaplayer.video.browser.HeaderGridView;
import org.leeroy.mediaplayer.video.browser.MainActivity;
import org.leeroy.mediaplayer.video.browser.adapters.mappers.TvshowCursorMapper;
import org.leeroy.mediaplayer.video.browser.adapters.object.Tvshow;
import org.leeroy.mediaplayer.video.browser.loader.SeasonsLoader;
import org.leeroy.mediaplayer.video.browser.loader.TvshowLoader;
import org.leeroy.mediaplayer.video.info.VideoInfoPosterBackdropActivity;
import org.leeroy.mediaplayer.video.info.VideoInfoScraperActivity;
import org.leeroy.mediaplayer.video.info.VideoInfoShowScraperFragment;
import org.leeroy.mediaplayer.video.utils.DelayedBackgroundLoader;
import org.leeroy.mediaplayer.video.utils.SerialExecutor;
import org.leeroy.mediaplayer.video.utils.VideoUtils;
import org.leeroy.mediaprovider.video.VideoStore;
import org.leeroy.mediascraper.BaseTags;
import org.leeroy.mediascraper.ShowTags;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BrowserWithShowHeader extends CursorBrowserByVideo  {

    private static final Logger log = LoggerFactory.getLogger(BrowserWithShowHeader.class);

    static final private String BROWSER_SHOW = BrowserListOfEpisodes.class.getName();
    public static final String EXTRA_SHOW_ITEM = "show_item";
    private final static int SUBMENU_ITEM_LIST_INDEX = 0;
    private final static int SUBMENU_ITEM_GRID_INDEX = 1;

    protected long mShowId;
    private View mHeaderView;
    protected Tvshow mShow;
    protected int SHOW_LOADER_ID = 1;
    private AsyncTask<Object, Void, TvShowAsyncTask.Result> mTvShowAsyncTask;
    private final static int SCRAPER_REQUEST = 0;
    private int mCurrentPlotLines = 5;

    private ImageViewSetter mBackgroundSetter;
    private ImageProcessor mBackgroundLoader;
    private ImageView mApplicationBackdrop;
    private SerialExecutor mSerialExecutor;
    private int mColor;
    protected View mApplicationFrameLayout;
    private boolean mPlotIsFullyDisplayed;

    public BrowserWithShowHeader() {
        if (log.isDebugEnabled()) log.debug("BrowserBySeason()");
    }
    
    private void setContentInfoVisibility(boolean visible){
        if (visible) {
            mHeaderView.findViewById(R.id.content_rating_container).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.network_container).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.premiered_container).setVisibility(View.VISIBLE);
        } else {
            mHeaderView.findViewById(R.id.content_rating_container).setVisibility(View.GONE);
            mHeaderView.findViewById(R.id.network_container).setVisibility(View.GONE);
            mHeaderView.findViewById(R.id.premiered_container).setVisibility(View.GONE);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHideOption = false;
        mSerialExecutor = new SerialExecutor();
        //be aware that argument can be changed in activityresult
        Bundle bundle = getArguments();
        if (bundle != null) {
            mShowId = getArguments().getLong(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID, 0);
            mShow = (Tvshow) getArguments().getSerializable(EXTRA_SHOW_ITEM);
        } else {
            mShowId = 0;
            mShow = null;
        }
        mHideWatched = false;

        mBackgroundLoader = new DelayedBackgroundLoader(getActivity(), 800, 0.2f);

    }
    public void onViewCreated(View v, Bundle save){
        super.onViewCreated(v, save);
        ImageViewSetterConfiguration config = ImageViewSetterConfiguration.Builder.createNew()
                .setUseCache(false)
                .build();
        mApplicationFrameLayout =  ((MainActivity) getActivity()).getGlobalBackdropView();
        mApplicationBackdrop = (ImageView) (((MainActivity) getActivity()).getGlobalBackdropView().findViewById(R.id.backdrop));
        mApplicationBackdrop.setAlpha(0f);
        mBackgroundSetter = new ImageViewSetter(getActivity(), config);
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.browser_item_header_show, null);
        mHeaderView.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        mHeaderView.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.video_details_item_height_new));
        setContentInfoVisibility(false);
        addHeaderView();
    }

    @Override
    public int getEmptyMessage() {
        return R.string.scraper_no_episode_found;
    }

    protected void applySelectedViewMode(int newMode) {
        if(mHeaderView!=null) { //removing headerview
            if (mLeeroyFlixGridView instanceof ListView) {
                ((ListView) mLeeroyFlixGridView).removeHeaderView(mHeaderView);
                ((AdapterView)mHeaderView.getParent()).removeViewInLayout(mHeaderView);
            }
            if(mLeeroyFlixGridView instanceof HeaderGridView)
                ((HeaderGridView) mLeeroyFlixGridView).removeHeaderView(mHeaderView);

        }
        // Save the current position variables before changing the view mode
        setPosition();
        mSelectedPosition = mLeeroyFlixGridView.getFirstVisiblePosition();
        setViewMode(newMode);
        //replacing headerView

            addHeaderView();
        bindAdapter();
    }

    private void addHeaderView() {
            if (mHeaderView != null) {

                if (mLeeroyFlixGridView instanceof ListView) {
                    //to avoid cast exception
                    mHeaderView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    ((ListView) mLeeroyFlixGridView).addHeaderView(mHeaderView);
                }
                if (mLeeroyFlixGridView instanceof HeaderGridView) {
                    //to avoid cast exception
                    mHeaderView.setLayoutParams(new HeaderGridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    ((HeaderGridView) mLeeroyFlixGridView).addHeaderView(mHeaderView);
                }
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mShow!=null){
            menu.add(0,R.string.scrap_series_change, 0, R.string.scrap_series_change);
            menu.add(0,R.string.info_menu_series_backdrop_select, 0, R.string.info_menu_series_backdrop_select);
            menu.add(0,R.string.info_menu_series_poster_select, 0, R.string.info_menu_series_poster_select);
        }
        super.onCreateOptionsMenu(menu, inflater);
        if (mBrowserAdapter != null
                && !mBrowserAdapter.isEmpty()) {
            mDisplayModeSubmenu.clear();
            mDisplayModeSubmenu.addSubmenuItem(R.drawable.ic_menu_list_mode2, R.string.view_mode_list, 0);
            mDisplayModeSubmenu.addSubmenuItem(R.drawable.ic_menu_grid_mode, R.string.view_mode_grid, 0);
            mDisplayModeSubmenu.selectSubmenuItem(mViewMode == VideoUtils.VIEW_MODE_GRID
                    ? SUBMENU_ITEM_GRID_INDEX : SUBMENU_ITEM_LIST_INDEX);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            long newShowID = data.getIntExtra(VideoInfoShowScraperFragment.SHOW_ID, -1);
            if(newShowID!=-1&&newShowID!=mShowId){
                mShowId = newShowID;
                getArguments().putLong(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID, mShowId);
                LoaderManager.getInstance(this).restartLoader(0, null, this);
                LoaderManager.getInstance(this).restartLoader(SHOW_LOADER_ID, null, this);
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.string.scrap_series_change){
            Intent intent = new Intent(getActivity(), VideoInfoScraperActivity.class);
            intent.putExtra(VideoInfoScraperActivity.EXTRA_SHOW, mShow);
            startActivityForResult(intent, SCRAPER_REQUEST);
            return true;
        }else if(item.getItemId()==R.string.info_menu_series_backdrop_select){
            Intent intent = new Intent(getActivity(), VideoInfoPosterBackdropActivity.class);
            intent.putExtra(VideoInfoPosterBackdropActivity.EXTRA_VIDEO, mShow);
            intent.putExtra(VideoInfoPosterBackdropActivity.EXTRA_CHOOSE_BACKDROP, true);
            startActivity(intent);
            return  true;
        }else if(item.getItemId()==R.string.info_menu_series_poster_select){
            selectSeriesPoster();
            return  true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    protected void selectSeriesPoster(){
        Intent intent = new Intent(getActivity(), VideoInfoPosterBackdropActivity.class);
        intent.putExtra(VideoInfoPosterBackdropActivity.EXTRA_VIDEO, mShow);
        intent.putExtra(VideoInfoPosterBackdropActivity.EXTRA_CHOOSE_BACKDROP, false);
        startActivity(intent);
    }
    @Override
    public void onSubmenuItemSelected(ActionBarSubmenu submenu, int position, long itemId) {
        switch (position) {
            case SUBMENU_ITEM_LIST_INDEX:
                if (mViewMode != VideoUtils.VIEW_MODE_LIST) {
                    applySelectedViewMode(VideoUtils.VIEW_MODE_LIST);
                }
                break;

            case SUBMENU_ITEM_GRID_INDEX:
                if (mViewMode != VideoUtils.VIEW_MODE_GRID) {
                    applySelectedViewMode(VideoUtils.VIEW_MODE_GRID);
                }
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (log.isDebugEnabled()) log.debug("onLoadFinished");
        super.onLoadFinished(loader, cursor);
        if (getActivity() == null) return;
        if(loader.getId()==SHOW_LOADER_ID) {
            cursor.moveToFirst();
            TvshowCursorMapper mTvShowMapper = new TvshowCursorMapper();
            mTvShowMapper.bindColumns(cursor);
            Tvshow newShow = (Tvshow) mTvShowMapper.bind(cursor);
            if(needToReload(mShow, newShow)) {
                mShow = newShow;
                getArguments().putSerializable(EXTRA_SHOW_ITEM, mShow); //saving in arguments
                if (mTvShowAsyncTask != null)
                    mTvShowAsyncTask.cancel(true);
                // FIXME: for some unexplained reason when doing BrowserAllTvShows->BrowserListOfSeasons->BrowserAllMovies->open movie
                // calls at the end BrowserWithShowHeaders for no apparent reason (tried to determine code path)
                // since this happens when getActivity() == null avoid doing UI stuff hides the issue that still remains to be fixed properly
                if (getActivity() != null) {
                    if (log.isDebugEnabled()) log.debug("onLoadFinished: activity not null");
                    mTvShowAsyncTask = new TvShowAsyncTask().executeOnExecutor(mSerialExecutor,getPosterUri(),mShow);
                    getActivity().invalidateOptionsMenu();
                } else {
                    if (log.isDebugEnabled()) log.debug("onLoadFinished: FIXME onLoadFinished getActivity is null");
                }
            }

        }
    }

    private boolean needToReload(Tvshow oldShow, Tvshow newShow) {
        if (oldShow==null || newShow==null) { if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate null"); return true;}
        if (oldShow.getClass() != newShow.getClass()) { if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate class"); return true;}
        if (oldShow.getTvshowId() != newShow.getTvshowId()) { if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate getTvshowId"); return true;}
        if (oldShow.isTraktSeen() != newShow.isTraktSeen()) { if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate isTraktSeen"); return true;}
        if (oldShow.getPosterUri()!=null&&!oldShow.getPosterUri().equals(newShow.getPosterUri())||newShow.getPosterUri()!=null&&newShow.getPosterUri().equals(oldShow.getPosterUri())) { if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate getPosterUri"); return true;}
        return false;
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mTvShowAsyncTask!=null)
            mTvShowAsyncTask.cancel(true);
        mBackgroundSetter.stopLoading(mApplicationBackdrop);
        mApplicationBackdrop.animate().cancel();
        mApplicationBackdrop.setAlpha(0f);
    }

    private  class TvShowAsyncTask extends AsyncTask<Object, Void,TvShowAsyncTask.Result>{
        public class Result{
            public Bitmap bitmap;
            public Tvshow show;
            public ShowTags tags;
            public Result(Tvshow show, Bitmap bitmap, ShowTags tags){
                this.show = show;
                this.bitmap = bitmap;
                this.tags = tags;
            }
        }
        @Override
        protected TvShowAsyncTask.Result doInBackground(Object... postersUri) {
            Tvshow show = (Tvshow) postersUri[1];
            Uri posterUri = (Uri) postersUri[0];
            Bitmap bitmap = null;
            try {
                if (log.isDebugEnabled()) log.debug("TvShowAsyncTask.Result show {} postersUri {}", show.getName(), posterUri);
                if (posterUri != null) {
                    bitmap = Picasso.get()
                            .load(posterUri)
                            .resizeDimen(R.dimen.video_details_poster_width,R.dimen.video_details_poster_height)
                            .noFade() // no fade since we are using activity transition anyway
                            .get();
                    if (log.isDebugEnabled()) log.debug("TvShowAsyncTask.Result: {}x{} ---- {}", bitmap.getWidth(), bitmap.getHeight(), posterUri);
                    if(bitmap!=null) {
                        Palette palette = Palette.from(bitmap).generate();
                        mColor = palette.getDarkVibrantColor(ContextCompat.getColor(getActivity(), R.color.leanback_details_background));
                    }
                }
            }
            catch (IOException e) {
                log.error("DetailsOverviewRow: caught IOException, Picasso load exception", e);
            }
            catch (NullPointerException e) { // getDefaultPoster() may return null (seen once at least)
                log.error("DetailsOverviewRow: caught NullPointerException doInBackground exception", e);
            }

            return new TvShowAsyncTask.Result(show, bitmap,(ShowTags) show.getFullScraperTags(getActivity()));
        }
        protected void onPostExecute(TvShowAsyncTask.Result result) {
            Tvshow show = result.show;
            BaseTags tags = result.tags;
            ShowTags showTags = result.tags;
            final TextView plotTv = (TextView) mHeaderView.findViewById(R.id.plot);
            mHeaderView.findViewById(R.id.loading).setVisibility(View.GONE);

            TextView tvpg = (TextView) mHeaderView.findViewById(R.id.content_rating);
            View tvpgContainer = (View) mHeaderView.findViewById(R.id.content_rating_container);
            if (tags == null || tags.getContentRating() == null || tags.getContentRating().isEmpty()) {
                tvpg.setVisibility(View.GONE);
                tvpgContainer.setVisibility(View.GONE);
            } else {
                tvpg.setText(tags.getContentRating());
            }

            TextView network = (TextView) mHeaderView.findViewById(R.id.network);
            network.setText(show.getStudio());

            TextView Premiered = (TextView) mHeaderView.findViewById(R.id.premiered);
            String pattern = "yyyy-MM-dd";
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = "";
            Date today = null;
            if (showTags != null) {
                today = showTags.getPremiered();
                todayAsString = df.format(today);
            } else {
                Premiered.setVisibility(View.INVISIBLE);
            }
            Premiered.setText(todayAsString);

            ImageView posterView = ((ImageView)mHeaderView.findViewById(R.id.thumbnail));
            posterView.setImageBitmap(result.bitmap);
            posterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPosterClick();
                }
            });

            setColor(mColor);
            ((TextView)mHeaderView.findViewById(R.id.name)).setText(show.getName());
            plotTv.setText(show.getPlot());
            plotTv.setMaxLines(Integer.MAX_VALUE);

            setSeason((TextView)mHeaderView.findViewById(R.id.season));
            plotTv.setVisibility(View.VISIBLE);
                if(!mPlotIsFullyDisplayed)
                mHeaderView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.video_details_item_height);
            else
                mHeaderView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mHeaderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewMore = mHeaderView.findViewById(R.id.view_more);
                    if (viewMore.getVisibility() == View.VISIBLE) {
                        plotTv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        viewMore.setVisibility(View.GONE);
                        mHeaderView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        setContentInfoVisibility(true);
                        mPlotIsFullyDisplayed = true;
                    } else {
                        plotTv.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                        viewMore.setVisibility(View.VISIBLE);
                        mHeaderView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.video_details_item_height);
                        setContentInfoVisibility(false);
                        mPlotIsFullyDisplayed = false;
                    }
                    mBrowserAdapter.notifyDataSetChanged();
                }
            });

            if(result.tags!=null&&result.tags.getDefaultBackdrop()!=null)
                mBackgroundSetter.set(mApplicationBackdrop, mBackgroundLoader, result.tags.getDefaultBackdrop());

        }
    }

    protected abstract void setSeason(TextView seasonView);

    protected abstract void setColor(int color);

    protected abstract void onPosterClick();

    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            log.error("onCreateContextMenu: bad menuInfo", e);
            return;
        }
        // This can be null sometimes, don't crash...
        if (info == null) {
            log.error("onCreateContextMenu: bad menuInfo");
            return;
        }

        info.position = correctedPosition(info.position);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    protected abstract Uri getPosterUri();

    @Override
    protected void postBindAdapter() {
        super.postBindAdapter();
        if(mCursor.getCount()==0) {
            //cannot be done in onLoadFinished
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().onBackPressed();
                }
            });
        }
        else {
            if (mShow != null) {
                if (mTvShowAsyncTask != null)
                    mTvShowAsyncTask.cancel(true);

                mTvShowAsyncTask = new TvShowAsyncTask().executeOnExecutor(mSerialExecutor, getPosterUri(), mShow);
            }
            LoaderManager.getInstance(this).restartLoader(SHOW_LOADER_ID, null, this);
        }
    }

    protected int correctedPosition(int position) {
        if(mLeeroyFlixGridView instanceof HeaderGridView){
            if(position<((HeaderGridView)mLeeroyFlixGridView).getOffset())
                return -1;
            position = position - ((HeaderGridView)mLeeroyFlixGridView).getOffset();
        }
        else if(mLeeroyFlixGridView instanceof ListView){
            if(position<((ListView)mLeeroyFlixGridView).getHeaderViewsCount())
                return -1;
            position = position - ((ListView)mLeeroyFlixGridView).getHeaderViewsCount();
        }
        return position;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==0) {
            return new SeasonsLoader(getContext(), mShowId).getV4CursorLoader(true, false);
        }
        else if(id== SHOW_LOADER_ID){
            return  new TvshowLoader(getContext(), mShowId).getV4CursorLoader(true, false);
        }
        return null;
    }

}
