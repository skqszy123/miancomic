package com.hiroshi.cimoc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hiroshi.cimoc.R;
import com.hiroshi.cimoc.model.Chapter;
import com.hiroshi.cimoc.model.Comic;
import com.hiroshi.cimoc.presenter.BasePresenter;
import com.hiroshi.cimoc.presenter.DetailPresenter;
import com.hiroshi.cimoc.ui.adapter.BaseAdapter;
import com.hiroshi.cimoc.ui.adapter.ChapterAdapter;
import com.hiroshi.cimoc.utils.ControllerBuilderFactory;
import com.hiroshi.cimoc.utils.DownLoadManager;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Hiroshi on 2016/7/2.
 */
public class DetailActivity extends BaseActivity {

    @BindView(R.id.detail_chapter_list) RecyclerView mRecyclerView;
    @BindView(R.id.detail_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.detail_star_btn) FloatingActionButton mStarButton;
    @BindView(R.id.detail_progress_bar) ProgressBar mProgressBar;

    private ChapterAdapter mChapterAdapter;
    private DetailPresenter mPresenter;

    @OnClick(R.id.detail_star_btn) void onClick() {
        if (mPresenter.isComicFavorite()) {
            mPresenter.unfavoriteComic();
            mStarButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            showSnackbar(R.string.detail_unfavorite);
        } else {
            mPresenter.favoriteComic();
            mStarButton.setImageResource(R.drawable.ic_favorite_white_24dp);
            showSnackbar(R.string.detail_favorite);
        }
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initPresenter() {
        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        int source = getIntent().getIntExtra(EXTRA_SOURCE, -1);
        String cid = getIntent().getStringExtra(EXTRA_CID);
        if (id == -1) {
            mPresenter = new DetailPresenter(this, null, source, cid);
        } else {
            mPresenter = new DetailPresenter(this, id, source, cid);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_detail;
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.detail);
    }

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected View getLayoutView() {
        return mCoordinatorLayout;
    }

    public void setLastChapter(String last) {
        mChapterAdapter.setLast(last);
    }

    public void setView(final Comic comic, final List<Chapter> list) {
        this.comic = comic;
        if (list == null) {
            mProgressBar.setVisibility(View.GONE);
            mCoordinatorLayout.setVisibility(View.VISIBLE);
            showSnackbar(R.string.common_network_error);
            return;
        }

        mChapterAdapter = new ChapterAdapter(this, list, comic.getSource(), comic.getCover(), comic.getTitle(),
                comic.getAuthor(), comic.getIntro(), comic.getStatus(), comic.getUpdate(), comic.getLast());
        mChapterAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != 0) {
                    Intent intent = ReaderActivity.createIntent(DetailActivity.this, mPresenter.getComic(),
                            mChapterAdapter.getDateSet(), position - 1);
                    startActivity(intent);
                }
            }
        });
        mChapterAdapter.setOnItemLongClickListener(new BaseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if(position!=0){
                    Log.e("chapter",list.get(position-1).getTitle());
                    Log.e("comic name:",comic.getTitle());
                    DetailActivity.this.chapter = list.get(position-1);
                    mPresenter.download(comic.getCid(),list.get(position-1).getPath());
                }
            }
        });
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setAdapter(mChapterAdapter);
        mRecyclerView.addItemDecoration(mChapterAdapter.getItemDecoration());

        if (comic.getFavorite() != null) {
            mStarButton.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mStarButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        mProgressBar.setVisibility(View.GONE);
        mCoordinatorLayout.setVisibility(View.VISIBLE);
        mStarButton.setVisibility(View.VISIBLE);
        if (list.isEmpty()) {
            showSnackbar(R.string.detail_error);
        }
    }

    public static final String EXTRA_ID = "a";
    public static final String EXTRA_SOURCE = "b";
    public static final String EXTRA_CID = "c";

    public static Intent createIntent(Context context, Long id, int source, String cid) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_SOURCE, source);
        intent.putExtra(EXTRA_CID, cid);
        return intent;
    }

    public void download(List<String> list){
        imageurllist = list;
        count = 0;
        DownLoadManager.getInstance().createDir(getExternalFilesDir(null) + "/IMAGE",comic.getTitle(),chapter.getTitle());

        new Task().execute(imageurllist.get(count));
    }

    private int count=0;
    private List<String> imageurllist;
    private Comic comic;
    private Chapter chapter;
    /**
     * 异步线程下载图片
     *
     */
    public class Task extends AsyncTask<String, Integer, Bitmap> {
        private String name;
        protected Bitmap doInBackground(String... params) {
            name = params[0].substring(params[0].lastIndexOf("/"),params[0].length());
            File file = new File(getExternalFilesDir(null) + "/IMAGE/"+comic.getTitle()+"/"+chapter.getTitle()+"/"+name);
            if(file.exists()){
                return null;
            }
            Bitmap bitmap = DownLoadManager.getInstance().GetImageInputStream((String) params[0], ControllerBuilderFactory.getReferer(comic.getSource()));
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if(result == null){
                count++;
                if (count < imageurllist.size()) {
                    new Task().execute(imageurllist.get(count));
                    Log.e("skip ready", count + 1 + "/"+imageurllist.size());
                } else {
                    Toast.makeText(DetailActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                }
                return;
            }
            DownLoadManager.getInstance().SavaImage(result, getExternalFilesDir(null) + "/IMAGE/"+comic.getTitle()+"/"+chapter.getTitle(),name);
            Log.e("path", getExternalFilesDir(null) + "/IMAGE/"+comic.getTitle()+"/"+chapter.getTitle());
//            Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_LONG);
            count++;
            if (count < imageurllist.size()) {
                new Task().execute(imageurllist.get(count));
                Log.e("download ready", count + 1 + "/"+imageurllist.size());
            } else {
                Toast.makeText(DetailActivity.this, "下载完成", Toast.LENGTH_LONG).show();
            }

        }
    }

}
