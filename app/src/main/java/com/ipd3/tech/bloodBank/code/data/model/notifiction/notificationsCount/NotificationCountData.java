
package com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationsCount;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationCountData {

    @SerializedName("notifications-count")
    @Expose
    private Integer notificationsCount;

    public Integer getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(Integer notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

}
