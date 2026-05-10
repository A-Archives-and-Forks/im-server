package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.pojos.mesh.PojoStringList;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

import java.util.List;

/**
 * 频道管理类
 * <p>
 * 提供频道管理相关的功能，包括：
 * <ul>
 * <li>创建和销毁频道</li>
 * <li>获取频道信息</li>
 * <li>用户订阅/取消订阅频道</li>
 * <li>检查用户是否订阅频道</li>
 * </ul>
 * </p>
 */
public class ChannelAdmin {
    /**
     * 创建频道
     * @param inputCreateChannel 频道创建信息
     * @return 创建结果，包含频道ID
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputCreateChannel> createChannel(InputCreateChannel inputCreateChannel) throws Exception {
        String path = APIPath.Create_Channel;
        return AdminHttpUtils.httpJsonPost(path, inputCreateChannel, OutputCreateChannel.class);
    }

    /**
     * 销毁频道
     * @param channelId 频道ID
     * @return 销毁结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> destroyChannel(String channelId) throws Exception {
        String path = APIPath.Destroy_Channel;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, Void.class);
    }

    /**
     * 获取频道信息
     * @param channelId 频道ID
     * @return 频道信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputGetChannelInfo> getChannelInfo(String channelId) throws Exception {
        String path = APIPath.Get_Channel_Info;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, OutputGetChannelInfo.class);
    }

    /**
     * 获取频道信息列表
     * @param count 数量
     * @param offset offset
     * @return 频道信息列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputChannelInfoList> getChannelInfoList(int count, int offset) throws Exception {
        String path = APIPath.List_Channel_Info;
        InputCountOffset inputCountOffset = new InputCountOffset();
        inputCountOffset.count = count;
        inputCountOffset.offset = offset;
        return AdminHttpUtils.httpJsonPost(path, inputCountOffset, OutputChannelInfoList.class);
    }

    /**
     * 订阅频道
     * @param channelId 频道ID
     * @param userId 用户ID
     * @return 订阅结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> subscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 批量用户订阅频道
     * @param channelId 频道ID
     * @param userIds 用户ID列表
     * @return 订阅结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> batchSubscribeChannel(String channelId, List<String> userIds) throws Exception {
        String path = APIPath.Batch_Subscribe_Channel;
        InputSubscribeChannelBatch input = new InputSubscribeChannelBatch(channelId, userIds, 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 取消订阅频道
     * @param channelId 频道ID
     * @param userId 用户ID
     * @return 取消订阅结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> unsubscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 批量用户取消订阅频道
     * @param channelId 频道ID
     * @param userIds 用户ID列表
     * @return 取消订阅结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> batchUnsubscribeChannel(String channelId, List<String> userIds) throws Exception {
        String path = APIPath.Batch_Subscribe_Channel;
        InputSubscribeChannelBatch input = new InputSubscribeChannelBatch(channelId, userIds, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 检查用户是否订阅了频道
     * @param userId 用户ID
     * @param channelId 频道ID
     * @return true-已订阅，false-未订阅
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputBooleanValue> isUserSubscribedChannel(String userId, String channelId) throws Exception {
        String path = APIPath.Check_User_Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, OutputBooleanValue.class);
    }

    /**
     * 获取频道订阅用户列表
     * @param channelId 频道ID
     * @param count
     * @param offset
     * @return 订阅用户id列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoTotalList> getChannelSubscribers(String channelId, int count, int offset) throws Exception {
        String path = APIPath.List_Channel_Subscriber;
        InputStringCountOffset input = new InputStringCountOffset(channelId, count, offset);
        return AdminHttpUtils.httpJsonPost(path, input, PojoTotalList.class);
    }
}
