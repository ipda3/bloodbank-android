package com.ipd3.tech.bloodBank.code.adapter.homeAdapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.post.postToggleFavourite.PostToggleFavourite;
import com.ipd3.tech.bloodBank.code.data.model.post.posts.PostsData;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.ui.activity.homeCycle.HomeActivity;
import com.ipd3.tech.bloodBank.code.ui.fragment.article.ArticleDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.data.api.RetrofitClient.getClient;
import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.loadUserData;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.onLoadImageFromUrl;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private TextView articlesFragmentTvNoItems;
    private boolean favourites;
    private UserData user;
    private ApiServices apiServices;
    private Context context;
    private Activity activity;
    private List<PostsData> postsDataList = new ArrayList<>();

    public ArticlesAdapter(Activity activity, Context context, List<PostsData> PostsData, boolean favourites, TextView articlesFragmentTvNoItems) {
        this.context = context;
        this.activity = activity;
        this.postsDataList = PostsData;
        apiServices = getClient().create(ApiServices.class);
        this.favourites = favourites;
        user = loadUserData(activity);
        this.articlesFragmentTvNoItems = articlesFragmentTvNoItems;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_articles, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {
        try {

            onLoadImageFromUrl(holder.articlesAdapterIvPostImage, postsDataList.get(position).getThumbnailFullPath(), context);
            holder.articlesAdapterTvTitle.setText(postsDataList.get(position).getTitle());
            if (postsDataList.get(position).getIsFavourite()) {
                holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
            } else {
                holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);
            }

        } catch (Exception e) {

        }

    }

    private void setAction(ViewHolder holder, int position) {


        holder.articlesAdapterRlFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavourite(holder, position);
            }
        });


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeActivity navigationActivity = (HomeActivity) activity;
//                    homeActivity.changeUi();
                ArticleDetailsFragment caringFragment = new ArticleDetailsFragment();
                caringFragment.postsData = postsDataList.get(position);
                HelperMethod.replaceFragment(navigationActivity.getSupportFragmentManager(), R.id.Content_Frame_Replace, caringFragment);

            }
        });

    }

    private void toggleFavourite(ViewHolder holder, int position) {
        PostsData postsData = postsDataList.get(position);

        postsDataList.get(position).setIsFavourite(!postsDataList.get(position).getIsFavourite());

        if (postsDataList.get(position).getIsFavourite()) {
            holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);

            customToast(activity, context.getResources().getString(R.string.add_to_favourite), false);

        } else {
            holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);

            customToast(activity, context.getResources().getString(R.string.remove_from_favourite), false);
            if (favourites) {
                postsDataList.remove(position);
                notifyDataSetChanged();
                if (postsDataList.size() == 0) {
                    articlesFragmentTvNoItems.setVisibility(View.VISIBLE);
                }
            }
        }

        apiServices.getPostToggleFavourite(postsData.getId(), user.getApiToken()).enqueue(new Callback<PostToggleFavourite>() {
            @Override
            public void onResponse(Call<PostToggleFavourite> call, Response<PostToggleFavourite> response) {
                try {
                    if (response.body().getStatus() == 1) {

                    } else {
                        postsDataList.get(position).setIsFavourite(!postsDataList.get(position).getIsFavourite());
                        if (postsDataList.get(position).getIsFavourite()) {
                            holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
                            if (favourites) {
                                postsDataList.add(position, postsData);
                                notifyDataSetChanged();
                            }
                        } else {
                            holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);
                        }
                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<PostToggleFavourite> call, Throwable t) {
                try {
                    postsDataList.get(position).setIsFavourite(!postsDataList.get(position).getIsFavourite());
                    if (postsDataList.get(position).getIsFavourite()) {
                        holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
                        if (favourites) {
                            postsDataList.add(position, postsData);
                            notifyDataSetChanged();
                        }
                    } else {
                        holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.articles_adapter_tv_title)
        TextView articlesAdapterTvTitle;
        @BindView(R.id.articles_adapter_iv_favourites)
        ImageView articlesAdapterIvFavourites;
        @BindView(R.id.articles_adapter_iv_post_image)
        ImageView articlesAdapterIvPostImage;
        @BindView(R.id.articles_adapter_rl_favourites)
        RelativeLayout articlesAdapterRlFavourites;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}

