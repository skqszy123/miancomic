package com.hiroshi.cimoc.ui.activity;

import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hiroshi.cimoc.CimocApplication;
import com.hiroshi.cimoc.R;
import com.hiroshi.cimoc.core.PreferenceMaster;
import com.hiroshi.cimoc.ui.custom.PreCacheLayoutManager;
import com.hiroshi.cimoc.ui.custom.ReverseSeekBar;
import com.hiroshi.cimoc.ui.custom.photo.PhotoDraweeView;
import com.hiroshi.cimoc.ui.custom.photo.PhotoDraweeViewController;
import com.hiroshi.cimoc.ui.custom.rvp.RecyclerViewPager;
import com.hiroshi.cimoc.utils.ControllerBuilderFactory;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

/**
 * Project：Cimoc-master
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/8/26 11:51
 * Modification  History:
 * Why & What is modified:
 */
public class AYReaderActivity extends BaseActivity implements RecyclerViewPager.OnPageChangedListener,PhotoDraweeViewController.OnSingleTapListener, DiscreteSeekBar.OnProgressChangeListener {

    @BindView(R.id.reader_recycler_view)
    RecyclerViewPager mRecyclerView;
    @BindView(R.id.reader_chapter_title)
    TextView mChapterTitle;
    @BindView(R.id.reader_chapter_page) TextView mChapterPage;
    @BindView(R.id.reader_battery) TextView mBatteryText;
    @BindView(R.id.reader_progress_layout) View mProgressLayout;
    @BindView(R.id.reader_back_layout) View mBackLayout;
    @BindView(R.id.reader_loading_layout) View mLoadingLayout;
    @BindView(R.id.reader_seek_bar)
    ReverseSeekBar mSeekBar;
    @BindView(R.id.reader_mask) View mNightMask;

    protected PreCacheLayoutManager mLayoutManager;
    protected AYViewPagerAdapter mReaderAdapter;


    protected int progress;
    protected int max;

    private int source;
    private boolean volume;
    private boolean reverse;


    @Override
    protected void initView() {
        super.initView();
        if (CimocApplication.getPreferences().getBoolean(PreferenceMaster.PREF_BRIGHT, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (CimocApplication.getPreferences().getBoolean(PreferenceMaster.PREF_NIGHT, false)) {
            mNightMask.setVisibility(View.VISIBLE);
        }
        mSeekBar.setOnProgressChangeListener(this);
        mReaderAdapter = new AYViewPagerAdapter(this, new LinkedList<String>());
        mReaderAdapter.setSingleTapListener(this);
        mReaderAdapter.setControllerBuilder(ControllerBuilderFactory.getControllerBuilder(source, this));
        mLayoutManager = new PreCacheLayoutManager(this);

        reverse = CimocApplication.getPreferences().getBoolean(PreferenceMaster.PREF_REVERSE, false);
        volume = CimocApplication.getPreferences().getBoolean(PreferenceMaster.PREF_VOLUME, false);
        mSeekBar.setReverse(reverse);
        mLayoutManager.setExtraSpace(4);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLayoutManager.setReverseLayout(reverse);
        mReaderAdapter.setAutoSplit(false);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mReaderAdapter);
        mRecyclerView.addOnPageChangedListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        hideToolLayout();
                        break;
                }
            }
        });
        setNextImage(null);
    }
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_page_reader;
    }
    @Override
    protected void initTheme() {}

    @Override
    protected void initToolbar() {}

    @Override
    public void OnPageChanged(int oldPosition, int newPosition) {
//        if (newPosition > oldPosition && progress == max) {
//            mPresenter.toNextChapter();
//        } else if (newPosition < oldPosition && progress == 1) {
//            mPresenter.toPrevChapter();
//        } else {
//            setReadProgress(progress + newPosition - oldPosition);
//        }
//        if (newPosition == 0) {
//            mPresenter.loadPrev();
//        } else if (newPosition == mReaderAdapter.getItemCount() - 1) {
//            mPresenter.loadNext();
//        }
    }
    public void setReadProgress(int progress) {
        this.progress = progress;
        String text = progress + "/" + max;
        mChapterPage.setText(text);
    }

    public void hideToolLayout() {
        if (mProgressLayout.isShown()) {
            mProgressLayout.setVisibility(View.INVISIBLE);
            mBackLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSingleTap(PhotoDraweeView draweeView, float x, float y) {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        float limitX = point.x / 3.0f;
        float limitY = point.y / 3.0f;
        if (x < limitX) {
            hideToolLayout();
            if (mRecyclerView.getCurrentPosition() == 0) {

            } else if (reverse) {
                mRecyclerView.scrollToPosition(mRecyclerView.getCurrentPosition() + 1);
            } else {
                mRecyclerView.scrollToPosition(mRecyclerView.getCurrentPosition() - 1);
            }
        } else if (x > 2 * limitX) {
            hideToolLayout();
            if (mRecyclerView.getCurrentPosition() == mReaderAdapter.getItemCount() - 1) {

            } else if (reverse) {
                mRecyclerView.scrollToPosition(mRecyclerView.getCurrentPosition() - 1);
            } else {
                mRecyclerView.scrollToPosition(mRecyclerView.getCurrentPosition() + 1);
            }
        } else if (y >= 2 * limitY) {
            switchToolLayout();
        } else if (y >= limitY) {
            draweeView.retry();
        }
    }
    public void switchToolLayout() {
        if (mProgressLayout.isShown()) {
            mProgressLayout.setVisibility(View.INVISIBLE);
            mBackLayout.setVisibility(View.INVISIBLE);
        } else {
            mSeekBar.setProgress(progress);
            mSeekBar.setMax(max);
            mProgressLayout.setVisibility(View.VISIBLE);
            mBackLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
            mRecyclerView.scrollToPosition(mRecyclerView.getCurrentPosition() + value - progress);
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    public void setNextImage(List<String> list) {
        list = new LinkedList<>();
        for(String path: getIntent().getStringArrayListExtra("pathlist")){
            list.add("file://"+path);
        }

        mReaderAdapter.addAll(list);
    }
}
