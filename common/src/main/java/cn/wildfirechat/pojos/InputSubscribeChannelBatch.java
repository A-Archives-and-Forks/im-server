/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

import java.util.List;

public class InputSubscribeChannelBatch {
    private String channelId;
    private List<String> userIds;
    private int subscribe; //1，订阅；0，取消订阅

    public InputSubscribeChannelBatch() {
    }

    public InputSubscribeChannelBatch(String channelId, List<String> userIds, int subscribe) {
        this.channelId = channelId;
        this.userIds = userIds;
        this.subscribe = subscribe;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public int getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe = subscribe;
    }
}
