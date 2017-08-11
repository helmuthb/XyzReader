package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.util.EventBus;
import com.example.xyzreader.viewmodel.ArticleViewModel;

public class ArticleListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, LifecycleRegistryOwner {

    private ArticleViewModel viewModel;
    private LifecycleRegistry lifecycleRegistry;
    private ArticleListFragment listFragment;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        viewModel.setPosition(-1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry = new LifecycleRegistry(this);
        viewModel = ArticleViewModel.getViewModel();
        setContentView(R.layout.activity_article);
        // Register with Otto
        EventBus.register(this);
        // create or get fragment
        FragmentManager fm = getSupportFragmentManager();
        listFragment = (ArticleListFragment)fm.findFragmentById(R.id.fragment_container);
        if (listFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            listFragment = new ArticleListFragment();
            ft.add(R.id.fragment_container, listFragment);
            ft.commit();
        }
        // start loader
        getLoaderManager().initLoader(0, null, this);
        // listen on changes between list or detail
        viewModel.getIsListLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean newIsList) {
                if (newIsList != null && !newIsList) {
                    // start an activity for the detail view
                    Intent detailIntent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                    ImageView photoView = listFragment.getSelectedImage();
                    if (photoView != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(
                                        ArticleListActivity.this,
                                        photoView,
                                        photoView.getTransitionName())
                                .toBundle();
                        startActivity(detailIntent, bundle);
                    }
                    else {
                        startActivity(detailIntent);
                    }
               }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // is cursor empty?
        if (cursor != null && cursor.getCount() <= 0) {
            // start service to load data
            Intent reloadIntent = new Intent(this, UpdaterService.class);
            startService(reloadIntent);
        } else {
            viewModel.setAllItemsCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        viewModel.setAllItemsCursor(null);
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

}
