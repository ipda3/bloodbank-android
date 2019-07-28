package com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.adapter.homeAdapter.ArticlesAdapter;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.post.posts.Posts;
import com.ipd3.tech.bloodBank.project.data.model.post.posts.PostsData;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.categeories.Categeories;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.OnEndLess;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.dismissProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesFragment extends Fragment {

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
    Unbinder unbinder;

    private List<String> CategorisesTxt = new ArrayList<>();
    private List<Integer> CategoriesId = new ArrayList<>();
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

    public ArticlesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        unbinder = ButterKnife.bind(this, view);
        user = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        if (favourites) {
            articlesFragmentLlFilter.setVisibility(View.GONE);
        }

        initRecyclerView();

        getCategories();
        getPosts(1);
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

        articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList);
        articlesFragmentRvListArticles.setAdapter(articlesAdapter);
    }

    private void getCategories() {
        apiServices.getCategeories().enqueue(new Callback<Categeories>() {
            @Override
            public void onResponse(Call<Categeories> call, Response<Categeories> response) {
                if (response.body().getStatus() == 1) {

                    CategorisesTxt.add("جميع الفئات");
                    CategoriesId.add(0);

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        CategorisesTxt.add(response.body().getData().get(i).getName());
                        CategoriesId.add(response.body().getData().get(i).getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            R.layout.soinner_item_2, CategorisesTxt);

                    articlesFragmentSpCategories.setAdapter(adapter);

                    articlesFragmentSpCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            categoryId = CategoriesId.get(i);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } else {
                    customToast(getActivity(), response.body().getMsg());
                }

            }

            @Override
            public void onFailure(Call<Categeories> call, Throwable t) {
                customToast(getActivity(), getResources().getString(R.string.error));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getPosts(int page) {
        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));

            Call<Posts> call;

            if (!favourites) {
                call = apiServices.getPosts(user.getApiToken(), page);
            } else {
                call = apiServices.getFavouritesList(user.getApiToken(), page);
            }

            call.enqueue(new Callback<Posts>() {
                @Override
                public void onResponse(Call<Posts> call, Response<Posts> response) {

                    try {
                        if (response.body().getStatus() == 1) {
                            if (response.body().getData().getTotal() > 0) {
                                articlesFragmentTvNoResults.setVisibility(View.GONE);
                            } else {
                                articlesFragmentTvNoResults.setVisibility(View.VISIBLE);

                            }
                            max = response.body().getData().getLastPage();
                            postsList.addAll(response.body().getData().getData());
                            articlesAdapter.notifyDataSetChanged();
                            dismissProgressDialog();

                        } else {
                            Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            customToast(getActivity(), response.body().getMsg());
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Posts> call, Throwable t) {
                    dismissProgressDialog();
                    customToast(getActivity(), getResources().getString(R.string.error));
                }
            });
        } else {
            dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline));
        }

    }

    @OnClick({R.id.articles_fragment_iv_search, R.id.articles_fragment_rl_sub_view})
    public void onViewClicked(View view) {
        HelperMethod.disappearKeypad(getActivity(), view);
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
                        articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList);
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
                    articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList);
                    articlesFragmentRvListArticles.setAdapter(articlesAdapter);

                    getPostsFilter(1);
                }

                HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));

                break;
        }
    }


    private void getPostsFilter(int page) {
        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));

            apiServices.getPostFilter(user.getApiToken(), keyword, page, categoryId).enqueue(new Callback<Posts>() {
                @Override
                public void onResponse(Call<Posts> call, Response<Posts> response) {
                    try {
                        if (response.body().getStatus() == 1) {

                            dismissProgressDialog();

                            max = response.body().getData().getLastPage();

                            if (page == 1) {
                                if (response.body().getData().getTotal() > 0) {
                                    articlesFragmentTvNoResults.setVisibility(View.GONE);

                                } else {
                                    articlesFragmentTvNoResults.setVisibility(View.VISIBLE);

                                }
                                initRecyclerView();
                                postsList = new ArrayList<>();
                                articlesAdapter = new ArticlesAdapter(getActivity(), getActivity(), postsList);
                                articlesFragmentRvListArticles.setAdapter(articlesAdapter);
                            }
                            postsList.addAll(response.body().getData().getData());
                            articlesAdapter.notifyDataSetChanged();

                        } else {
                            dismissProgressDialog();

                            customToast(getActivity(), response.body().getMsg());
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Posts> call, Throwable t) {
                    dismissProgressDialog();

                    customToast(getActivity(), getResources().getString(R.string.error));
                }
            });
        } else {
            dismissProgressDialog();

            customToast(getActivity(), getResources().getString(R.string.offline));
        }


    }
}