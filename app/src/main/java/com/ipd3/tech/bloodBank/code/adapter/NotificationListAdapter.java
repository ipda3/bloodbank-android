package com.ipd3.tech.bloodBank.code.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationList.NotificationData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.getDonation;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private UserData userData;
    private Context context;
    private Activity activity;
    private ApiServices apiServices;
    private List<NotificationData> notificationsData = new ArrayList<>();

    public NotificationListAdapter(Context context, Activity activity, List<NotificationData> notificationsData) {
        this.context = context;
        this.activity = activity;
        this.notificationsData = notificationsData;
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_list_adapter,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {
        try {
            holder.notificationListAdapterTvNotificationText.setText(notificationsData.get(position).getTitle());

            holder.notificationListAdapterTvNotificationTime.setText(notificationsData.get(position).getCreatedAt());

            if (notificationsData.get(position).getPivot().getIsRead().equals("1")) {
                holder.notificationListAdapterIvNotificationImage.setImageResource(R.drawable.notilight);

            } else {
                holder.notificationListAdapterIvNotificationImage.setImageResource(R.drawable.noti2);
            }

        } catch (Exception e) {

        }
    }

    private void setAction(ViewHolder holder, int position) {

        try {

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDonation(activity, apiServices, notificationsData.get(position).getDonationRequestId(), userData.getApiToken(), false);
                    notificationsData.get(position).getPivot().setIsRead("1");
                }
            });

        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return notificationsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notification_list_adapter_iv_notification_image)
        ImageView notificationListAdapterIvNotificationImage;
        @BindView(R.id.notification_list_adapter_tv_notification_text)
        TextView notificationListAdapterTvNotificationText;
        @BindView(R.id.notification_list_adapter_tv_notification_Time)
        TextView notificationListAdapterTvNotificationTime;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}
