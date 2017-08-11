package com.example.xyzreader.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.xyzreader.R;
import com.example.xyzreader.util.EventBus;
import com.example.xyzreader.util.FragmentReadyStatus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity
        implements LifecycleRegistryOwner {

    private LifecycleRegistry lifecycleRegistry;
    private ArticleDetailFragment detailFragment;
    static private final String STATE_EXPANDED =
            ArticleDetailFragment.class.getName() + ":STATE_EXPANDED";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (detailFragment != null && detailFragment.appBarLayout != null) {
            outState.putBoolean(STATE_EXPANDED, detailFragment.isExpanded);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry = new LifecycleRegistry(this);
        setContentView(R.layout.activity_article);
        // create or get fragment
        FragmentManager fm = getSupportFragmentManager();
        detailFragment = (ArticleDetailFragment)fm.findFragmentById(R.id.fragment_container);
        if (detailFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            detailFragment = new ArticleDetailFragment();
            ft.add(R.id.fragment_container, detailFragment);
            ft.commit();
        }
        // Register with Otto
        EventBus.register(this);
        // postpone transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportPostponeEnterTransition();
        }
        // check collapsed state
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_EXPANDED)) {
            detailFragment.appBarLayout.setExpanded(
                    savedInstanceState.getBoolean(STATE_EXPANDED));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // set ActionToolbar
        Toolbar toolbar = detailFragment.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onFragmentReady(FragmentReadyStatus fragmentReadyStatus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportStartPostponedEnterTransition();
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
