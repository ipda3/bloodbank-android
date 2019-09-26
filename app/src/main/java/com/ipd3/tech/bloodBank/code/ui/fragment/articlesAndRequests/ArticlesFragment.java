package com.ipd3.tech.bloodBank.code.ui.fragment.articlesAndRequests;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.adapter.homeAdapter.ArticlesAdapter;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.post.posts.Posts;
import com.ipd3.tech.bloodBank.code.data.model.post.posts.PostsData;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.categeories.Categeories;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.OnEndLess;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.disappearKeypad;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.setSpinner;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesFragment extends BaseFragment {

    @BindView(R.id.articles_fragment_rv_list_articles)
    RecyclerView articlesFragmentRvListArticles;
    @BindView(R.id.articles_fragment_sp_categories)
    Spinner articlesFragmentSpCategories;
    @BindView(R.id.articles_fragment_et_keyword)
    EditText articlesFragmentEtKeyword;
    @BindView(R.id.articles_fragment_iv_search)
    ImageView articlesFragmentIvSearch;
    @BindView(R.id.articles_fragment_tv_no_results)
    TextView articlesFragmentTvNoResults;
    @BindView(R.id.articles_fragment_ll_filter)
    LinearLayout articlesFragmentLlFilter;
    @BindView(R.id.articles_fragment_srl_articles_list_refresh)
    SwipeRefreshLayout articlesFragmentSrlArticlesListRefresh;
    @BindView(R.id.articles_fragment_tv_no_items)
    TextView articlesFragmentTvNoItems;
    Unbinder unbinder;

    private List<String> categorisesNames = new ArrayList<>();
    private List<Integer> categoriesIds = new ArrayList<>();
    private List<PostsData> postsList = new ArrayList<>();

    private ApiServices apiServices;
    private ArticlesAdapter articlesAdapter;
    private OnEndLess onEndLess;
    private LinearLayoutManager linearLayoutManager;
    private UserData user;

    private boolean Filter = false;
    private int categoryId = 0;
    private int max = 0;
    private String keyword = "";
    public boolean favourites = false;
    public boolean backFromFavourites = false;

    public ArticlesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HelperMethod.changeLang(getActivity(), "ar");
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        unbinder = ButterKnife.bind(this, view);
        user = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        setUpHomeActivity();

        if (favourites) {
            articlesFragmentLlFilter.setVisibility(View.GONE);
            homeActivity.setTitle(getString(R.string.fav));
        }

        initRecyclerView();

        if (!favourites) {
            if (categoriesIds.size() == 0) {
                getCategories();
            } else {
                setSpinner(getActivity(), articlesFragmentSpCategories, categorisesNames);
            }
        }

        articlesFragmentSrlArticlesListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onEndLess.current_page = 1;
                onEndLess.previousTotal = 0;
                onEndLess.previous_page = 1;

                max = 0;

                postsList = new ArrayList<>();

                articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList, favourites, articlesFragmentTvNoItems);
                articlesFragmentRvListArticles.setAdapter(articlesAdapter);

                if (Filter) {
                    getPostsFilter(1);
                } else {
                    getPosts(1);
                }
            }
        });

        if (postsList.size() == 0 || backFromFavourites == true) {
            backFromFavourites = false;
            getPosts(1);
        }

        return view;
    }

    private void initRecyclerView() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        articlesFragmentRvListArticles.setLayoutManager(linearLayoutManager);

        onEndLess = new OnEndLess(linearLayoutManager, 1) {
            @Override
            public void onLoadMore(int current_page) {

                if (current_page <= max) {
                    if (max != 0 && current_page != 1) {
                        onEndLess.previous_page = current_page;

                        if (Filter) {
                            getPostsFilter(current_page);
                        } else {
                            getPosts(current_page);
                        }

                    } else {
                        onEndLess.current_page = onEndLess.previous_page;
                    }
                } else {
                    onEndLess.current_page = onEndLess.previous_page;
                }

            }
        };
        articlesFragmentRvListArticles.addOnScrollListener(onEndLess);
        articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList, favourites, articlesFragmentTvNoItems);
        articlesFragmentRvListArticles.setAdapter(articlesAdapter);

    }

    private void getCategories() {
        apiServices.getCategeories().enqueue(new Callback<Categeories>() {
            @Override
            public void onResponse(Call<Categeories> call, Response<Categeories> response) {
                try {

                    if (response.body().getStatus() == 1) {

                        categorisesNames = new ArrayList<>();
                        categoriesIds = new ArrayList<>();

                        categorisesNames.add(getString(R.string.select_categories));
                        categoriesIds.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            categorisesNames.add(response.body().getData().get(i).getName());
                            categoriesIds.add(response.body().getData().get(i).getId());
                        }

                        setSpinner(getActivity(), articlesFragmentSpCategories, categorisesNames);

                        articlesFragmentSpCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                categoryId = categoriesIds.get(i);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } else {
                        customToast(getActivity(), response.body().getMsg(), true);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Categeories> call, Throwable t) {
                customToast(getActivity(), getResources().getString(R.string.error), true);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getPosts(int page) {

        Call<Posts> call;

        if (!favourites) {
            call = apiServices.getPosts(user.getApiToken(), page);
        } else {
            call = apiServices.getFavouritesList(user.getApiToken(), page);
        }

        callData(page, call);

    }

    @OnClick({R.id.articles_fragment_iv_search, R.id.articles_fragment_rl_sub_view})
    public void onViewClicked(View view) {
        disappearKeypad(getActivity(), view);
        switch (view.getId()) {
            case R.id.articles_fragment_iv_search:

                keyword = articlesFragmentEtKeyword.getText().toString().trim();
                if (articlesFragmentSpCategories.getSelectedItemPosition() == 0
                        && keyword.equals("")) {
                    if (Filter) {
                        Filter = false;

                        onEndLess.current_page = 1;
                        onEndLess.previous_page = 1;
                        onEndLess.previousTotal = 0;
                        onEndLess.totalItemCount = 0;
                        max = 0;


                        postsList = new ArrayList<>();
                        articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList, favourites, articlesFragmentTvNoItems);
                        articlesFragmentRvListArticles.setAdapter(articlesAdapter);
                        getPosts(1);
                    }

                } else {
                    Filter = true;

                    onEndLess.current_page = 1;
                    onEndLess.previous_page = 1;
                    onEndLess.previousTotal = 0;
                    onEndLess.totalItemCount = 0;
                    max = 0;
                    postsList = new ArrayList<>();
                    articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList, favourites, articlesFragmentTvNoItems);
                    articlesFragmentRvListArticles.setAdapter(articlesAdapter);

                    getPostsFilter(1);
                }

                break;
        }
    }

    private void getPostsFilter(int page) {

//        if (page == 1) {
//            showProgressDialog(getActivity(), getString(R.string.waiit));
//        }

        Call<Posts> call = apiServices.getPostFilter(user.getApiToken(), keyword, page, categoryId);

        callData(page, call);

    }

    private void callData(int page, Call<Posts> call) {
        if (InternetState.isConnected(getActivity())) {
            showProgressDialog(getActivity(), getString(R.string.waiit));
            call.enqueue(new Callback<Posts>() {
                @Override
                public void onResponse(Call<Posts> call, Response<Posts> response) {

                    try {
                        dismissProgressDialog();
                        articlesFragmentSrlArticlesListRefresh.setRefreshing(false);
                        if (response.body().getStatus() == 1) {

                            if (page == 1) {
                                if (response.body().getData().getTotal() > 0) {

                                    articlesFragmentTvNoResults.setVisibility(View.GONE);

                                } else {
                                    if (favourites) {
                                        articlesFragmentTvNoItems.setVisibility(View.VISIBLE);
                                    } else {
                                        articlesFragmentTvNoResults.setVisibility(View.VISIBLE);

                                    }
                                }

                                onEndLess.current_page = 1;
                                onEndLess.previousTotal = 0;
                                onEndLess.previous_page = 1;

                                max = 0;

                                postsList = new ArrayList<>();
                                articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList, favourites, articlesFragmentTvNoItems);
                                articlesFragmentRvListArticles.setAdapter(articlesAdapter);

                            }

                            max = response.body().getData().getLastPage();
                            postsList.addAll(response.body().getData().getData());
                            articlesAdapter.notifyDataSetChanged();


                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Posts> call, Throwable t) {
                    dismissProgressDialog();
                    customToast(getActivity(), getResources().getString(R.string.error), true);

                    try {
                        articlesFragmentSrlArticlesListRefresh.setRefreshing(false);
                    } catch (Exception e) {

                    }
                }
            });

        } else {
            dismissProgressDialog();

            customToast(getActivity(), getResources().getString(R.string.offline), true);

            try {
                articlesFragmentSrlArticlesListRefresh.setRefreshing(false);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onBack() {
        if (favourites) {
            setUpHomeActivity();
            homeActivity.articlesAndDonations.backFromFavourites = true;
            HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, homeActivity.articlesAndDonations);
        } else {
            getActivity().finish();
        }
    }

}