package com.example.xyzreader.ui;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.util.EventBus;
import com.example.xyzreader.util.RefreshStatus;
import com.example.xyzreader.util.Util;
import com.example.xyzreader.viewmodel.ArticleViewModel;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends LifecycleFragment {

    private View rootView;
    private ArticleViewModel viewModel;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Adapter adapter;

    public ArticleListFragment() {
        super();
        EventBus.register(this);
        adapter = null;
    }

    ViewHolder getSelectedViewHolder() {
        if (adapter != null) {
            ViewHolder viewHolder = adapter.getViewForPosition(viewModel.getPosition());
            return viewHolder;
        }
        return null;
    }

    public ImageView getSelectedImage() {
        ViewHolder viewHolder = getSelectedViewHolder();
        if (viewHolder != null) {
            return viewHolder.thumbnailImageView;
        }
        return null;
    }

    public TextView getSelectedTitle() {
        ViewHolder viewHolder = getSelectedViewHolder();
        if (viewHolder != null) {
            return viewHolder.titleTextView;
        }
        return null;
    }

    public TextView getSelectedSubtitle() {
        ViewHolder viewHolder = getSelectedViewHolder();
        if (viewHolder != null) {
            return viewHolder.subtitleTextView;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ArticleViewModel.getViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_article_list, container, false);
        ButterKnife.bind(this, rootView);
        // bind to the refresh-layout
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // initiate reload from network
                callUpdater();
            }
        });
        // set parent's ActionToolbar if possible
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity)getActivity();
            toolbar.setTitle("");
            activity.setSupportActionBar(toolbar);
        }
        // listen to the cursor
        viewModel.getItemsLiveData().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                if (items == null || items.size() <= 0) {
                    adapter = null;
                } else {
                    adapter = new Adapter(items, getContext());
                }
                recyclerView.setAdapter(adapter);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // set parent's ActionToolbar if possible
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity)getActivity();
            activity.setSupportActionBar(toolbar);
        }
    }

    void callUpdater() {
        Intent reloadIntent = new Intent(getActivity(), UpdaterService.class);
        getActivity().startService(reloadIntent);
    }

    @Subscribe
    public void onRefresh(RefreshStatus status) {
        // show / hide refresh status
        boolean newRefreshing = (status == RefreshStatus.STATE_LOADING);
        if (newRefreshing != refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(newRefreshing);
        }
        if (status == RefreshStatus.STATE_ERROR) {
            // show error toast
            Snackbar snackbar = Snackbar
                    .make(
                            getView(),
                            R.string.error_loading,
                            Snackbar.LENGTH_LONG)
                    .setAction(R.string.load_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callUpdater();
                        }
                    });
            snackbar.show();
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Item> items;
        private GlideRequests glide;
        private HashMap<Integer, ViewHolder> viewHolderHashMap;

        public Adapter(List<Item> items, Context ctx) {
            this.items = items;
            setHasStableIds(true);
            glide = GlideApp.with(ctx);
            viewHolderHashMap = new HashMap<>();
        }

        public ViewHolder getViewForPosition(int position) {
            return viewHolderHashMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).id;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.list_item_article, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view, glide);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.setPosition(viewHolder.position);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Item item = items.get(position);
            viewHolder.bind(
                    ArticleListFragment.this,
                    position,
                    item);
            viewHolderHashMap.put(position, viewHolder);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.placeholder)
        AspectRatioImageView placeholderImageView;
        @BindView(R.id.thumbnail)
        ImageView thumbnailImageView;
        @BindView(R.id.article_title)
        TextView titleTextView;
        @BindView(R.id.article_subtitle)
        TextView subtitleTextView;
        int position;
        GlideRequests glide;

        public ViewHolder(View view, GlideRequests glide) {
            super(view);
            ButterKnife.bind(this, view);
            this.glide = glide;
        }

        public void bind(ArticleListFragment fragment, int position, Item item) {
            this.position = position;
            titleTextView.setText(item.title);
            placeholderImageView.setVisibility(View.VISIBLE);
            placeholderImageView.setAspectRatio(item.aspectRatio);
            // load the real image
            /* */
            glide
                    .load(item.thumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.empty_detail)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            placeholderImageView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            placeholderImageView.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(thumbnailImageView);
            /* */
            /*
            Picasso.with(fragment.getContext())
                    .load(item.thumbnailUrl)
                    .error(R.drawable.empty_detail)
                    .into(thumbnailImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            placeholderImageView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            placeholderImageView.setVisibility(View.GONE);
                        }
                    });
            */
            subtitleTextView.setText(Util.getSubtitle(fragment.getContext(), item));
        }
    }
}
