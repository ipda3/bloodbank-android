package com.ipd3.tech.bloodBank.code.ui.fragment.article;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.post.posts.PostsData;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.loadUserData;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.onLoadImageFromUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailsFragment extends BaseFragment {

    @BindView(R.id.caring_fragment_iv_image_post)
    ImageView caringFragmentIvImagePost;
    @BindView(R.id.caring_fragment_tv_title)
    TextView caringFragmentTvTitle;
    @BindView(R.id.caring_fragment_iv_add_to_favourite)
    ImageView caringFragmentIvAddToFavourite;
    @BindView(R.id.caring_fragment_tv_content)
    TextView caringFragmentTvContent;
    Unbinder unbinder;

    private ApiServices apiServices;
    private UserData user;
    public PostsData postsData;

    public ArticleDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_caring, container, false);
        unbinder = ButterKnife.bind(this, view);
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        user = loadUserData(getActivity());
        setUpHomeActivity();
        homeActivity.setTitle(postsData.getTitle());
        getPostDetails();

        return view;
    }

    private void getPostDetails() {
        try {
            onLoadImageFromUrl(caringFragmentIvImagePost, postsData.getThumbnailFullPath(), getActivity());
            caringFragmentTvTitle.setText(postsData.getTitle());
            caringFragmentTvContent.setText(postsData.getContent());

            if (postsData.getIsFavourite()) {
                caringFragmentIvAddToFavourite.setImageResource(R.drawable.aa);

            } else {
                caringFragmentIvAddToFavourite.setImageResource(R.drawable.lightheart);

            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
