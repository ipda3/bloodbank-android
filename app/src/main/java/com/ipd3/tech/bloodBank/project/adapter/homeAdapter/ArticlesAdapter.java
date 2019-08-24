package com.ipd3.tech.bloodBank.project.adapter.homeAdapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.post.postToggleFavourite.PostToggleFavourite;
import com.ipd3.tech.bloodBank.project.data.model.post.posts.PostsData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.data.api.RetrofitClient.getClient;
import static com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger.loadUserData;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.onLoadImageFromUrl;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private UserData user;
    private ApiServices apiServices;
    private Context Context;
    private Activity activity;
    private List<PostsData> PostsData = new ArrayList<>();

    public ArticlesAdapter(Activity activity, Context context, List<PostsData> PostsData) {
        Context = context;
        this.activity = activity;
        this.PostsData = PostsData;
        apiServices = getClient().create(ApiServices.class);
        user = loadUserData(activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Context).inflate(R.layout.item_articles, parent, false);
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

            onLoadImageFromUrl(holder.articlesAdapterIvPostImage, PostsData.get(position).getThumbnailFullPath(), Context);
            holder.articlesAdapterTvTitle.setText(PostsData.get(position).getTitle());
            if (PostsData.get(position).getIsFavourite()) {
                holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
            } else {
                holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);
            }

        } catch (Exception e) {

        }

    }

    private void setAction(ViewHolder holder, int position) {
        try {

            holder.articlesAdapterRlFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PostsData.get(position).setIsFavourite(!PostsData.get(position).getIsFavourite());

                    if (PostsData.get(position).getIsFavourite()) {
                        holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
                        Toast.makeText(Context, "تمت الإضافة", Toast.LENGTH_SHORT).show();

                    } else {
                        holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);
                        Toast.makeText(Context, "تمت الإزالة", Toast.LENGTH_SHORT).show();

                    }

                    apiServices.getPostToggleFavourite(PostsData.get(position).getId(), user.getApiToken()).enqueue(new Callback<PostToggleFavourite>() {
                        @Override
                        public void onResponse(Call<PostToggleFavourite> call, Response<PostToggleFavourite> response) {
                            try {
                                if (response.body().getStatus() == 1) {
                                    PostsData.get(position).setIsFavourite(!PostsData.get(position).getIsFavourite());

                                    if (PostsData.get(position).getIsFavourite()) {
                                        holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
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
                                PostsData.get(position).setIsFavourite(!PostsData.get(position).getIsFavourite());

                                if (PostsData.get(position).getIsFavourite()) {
                                    holder.articlesAdapterIvFavourites.setImageResource(R.drawable.aa);
                                } else {
                                    holder.articlesAdapterIvFavourites.setImageResource(R.drawable.lightheart);
                                }
                            } catch (Exception e) {

                            }

                        }
                    });

                }
            });


            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    HomeNavigationActivity navigationActivity = (HomeNavigationActivity) activity;
//                    navigationActivity.changeUi();
//                    CaringFragment caringFragment = new CaringFragment();
//                    caringFragment.postsData = PostsData.get(position);
//                    HelperMethod.replaceFragment(navigationActivity.getSupportFragmentManager(), R.id.Content_Frame_Replace, caringFragment);

                }
            });

        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return PostsData.size();
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

