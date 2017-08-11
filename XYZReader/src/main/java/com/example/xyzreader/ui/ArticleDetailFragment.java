package com.example.xyzreader.ui;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.Item;
import com.example.xyzreader.util.EventBus;
import com.example.xyzreader.util.FragmentReadyStatus;
import com.example.xyzreader.util.Util;
import com.example.xyzreader.viewmodel.ArticleViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Article detail screen.
 */
public class ArticleDetailFragment extends LifecycleFragment
        implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "ArticleDetailFragment";

    private ArticleViewModel viewModel;
    private View rootView;
    @BindView(R.id.photo)
    ImageView photoView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.share_fab)
    FloatingActionButton shareFab;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.article_byline)
    TextView bylineView;
    @BindView(R.id.article_body)
    TextView bodyView;
    long lastItemId = -1;
    boolean isExpanded = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
        viewModel = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ArticleViewModel.getViewModel();
        // keep this fragment
        setRetainInstance(true);
    }

    public void scrollToTop() {
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, 0);
                    scrollView.setScrollY(0);
                    appBarLayout.setExpanded(true, false);
                }
            });
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, rootView);
        // listen on the fab
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(viewModel.getItem().title)
                        .getIntent(), getString(R.string.action_share)));
            }
        });
        // get current item
        // show values & image
        Item item = viewModel.getItem();
        if (item != null) {
            toolbar.setTitle(item.title);
            GlideApp.with(ArticleDetailFragment.this)
                    .load(item.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            EventBus.post(FragmentReadyStatus.FRAGMENT_DETAIL);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            EventBus.post(FragmentReadyStatus.FRAGMENT_DETAIL);
                            rootView.post(new Runnable() {
                                @Override
                                public void run() {
                                    appBarLayout.setExpanded(isExpanded);
                                }
                            });
                            return false;
                        }
                    })
                    .into(photoView);
            bodyView.setText(Html.fromHtml(
                    item.body.replaceAll("(\r\n|\n)", "<br />")));
            bylineView.setText(Util.getSubtitle(getContext(), item));
            if (lastItemId != -1 && lastItemId != item.id) {
                scrollToTop();
            }
            lastItemId = item.id;
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // notify
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        isExpanded = (verticalOffset == 0);
    }
}
