package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 通用管理类
 * <p>
 * 提供通用的系统管理功能，包括：
 * <ul>
 * <li>系统设置管理</li>
 * <li>文件管理（会话文件、用户文件）</li>
 * <li>用户设置管理（会话置顶等）</li>
 * <li>健康检查</li>
 * <li>频道管理（已废弃，请使用ChannelAdmin）</li>
 * </ul>
 * </p>
 */
public class GeneralAdmin {
    /**
     * 获取系统设置
     * @param id 设置项ID
     * @return 系统设置信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SystemSettingPojo> getSystemSetting(int id) throws Exception {
        String path = APIPath.Get_System_Setting;
        SystemSettingPojo input = new SystemSettingPojo();
        input.id = id;
        return AdminHttpUtils.httpJsonPost(path, input, SystemSettingPojo.class);
    }

    /**
     * 设置系统设置
     * @param id 设置项ID
     * @param value 设置值
     * @param desc 设置描述
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setSystemSetting(int id, String value, String desc) throws Exception {
        String path = APIPath.Put_System_Setting;
        SystemSettingPojo input = new SystemSettingPojo();
        input.id = id;
        input.value = value;
        input.desc = desc;
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#createChannel(InputCreateChannel inputCreateChannel)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<OutputCreateChannel> createChannel(InputCreateChannel inputCreateChannel) throws Exception {
        String path = APIPath.Create_Channel;
        return AdminHttpUtils.httpJsonPost(path, inputCreateChannel, OutputCreateChannel.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#destroyChannel(String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<Void> destroyChannel(String channelId) throws Exception {
        String path = APIPath.Destroy_Channel;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#getChannelInfo(String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<OutputGetChannelInfo> getChannelInfo(String channelId) throws Exception {
        String path = APIPath.Get_Channel_Info;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, OutputGetChannelInfo.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#subscribeChannel(String userId, String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<Void> subscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#unsubscribeChannel(String userId, String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<Void> unsubscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#isUserSubscribedChannel(String userId, String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<OutputBooleanValue> isUserSubscribedChannel(String userId, String channelId) throws Exception {
        String path = APIPath.Check_User_Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, OutputBooleanValue.class);
    }

    /**
     * 获取会话文件列表（仅专业版支持）
     * <p>
     * 如果是单聊会话，target和userId代表会话的2个用户；如果是其他会话userId无意义。
     * </p>
     * @param conversationType 会话类型
     * @param target 会话目标ID
     * @param line 线路
     * @param userId 用户ID
     * @param offset 偏移量
     * @param desc 是否降序
     * @param count 每页数量
     * @return 文件列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<FilesPojo> getConversationFiles(int conversationType, String target, int line, String userId, int offset, boolean desc, int count) throws Exception {
        String path = APIPath.Get_Conversation_Files;
        GetConversationFilesPojo input = new GetConversationFilesPojo();
        input.conversationType = conversationType;
        input.target = target;
        input.line = line;
        input.userId = userId;
        input.offset = offset;
        input.desc = desc;
        input.count = count;
        return AdminHttpUtils.httpJsonPost(path, input, FilesPojo.class);
    }

    /**
     * 获取用户文件列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param desc 是否降序
     * @param count 每页数量
     * @return 文件列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<FilesPojo> getUserFiles(String userId, int offset, boolean desc, int count) throws Exception {
        String path = APIPath.Get_User_Files;
        GetUserFilesPojo input = new GetUserFilesPojo();
        input.userId = userId;
        input.offset = offset;
        input.desc = desc;
        input.count = count;
        return AdminHttpUtils.httpJsonPost(path, input, FilesPojo.class);
    }

    /**
     * 根据消息ID获取文件信息
     * @param messageId 消息ID
     * @return 文件信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<FilesPojo.FilePojo> getFile(long messageId) throws Exception {
        String path = APIPath.Get_Message_File;
        LongPojo input = new LongPojo();
        input.value = messageId;
        return AdminHttpUtils.httpJsonPost(path, input, FilesPojo.FilePojo.class);
    }

    /**
     * 设置会话置顶
     * @param userId 用户ID
     * @param conversationType 会话类型
     * @param target 会话目标ID
     * @param line 线路
     * @param isTop true-置顶，false-取消置顶
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setConversationTop(String userId, int conversationType, String target, int line, boolean isTop) throws Exception {
        String key = conversationType + "-" + line + "-" + target;
        String value = isTop?"1":"0";
        return setUserSetting(userId, 3, key, value);
    }

    /**
     * 获取会话置顶状态
     * @param userId 用户ID
     * @param conversationType 会话类型
     * @param target 会话目标ID
     * @param line 线路
     * @return true-已置顶，false-未置顶
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Boolean> getConversationTop(String userId, int conversationType, String target, int line) throws Exception {
        String key = conversationType + "-" + line + "-" + target;
        IMResult<UserSettingPojo> result = getUserSetting(userId, 3, key);
        IMResult<Boolean> out = new IMResult<Boolean>();
        out.code = result.code;
        out.msg = result.msg;
        out.result = result.result != null && "1".equals(result.result.getValue());
        return out;
    }

    /**
     * 获取用户设置
     * @param userId 用户ID
     * @param scope 设置范围
     * @param key 设置键
     * @return 用户设置信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<UserSettingPojo> getUserSetting(String userId, int scope, String key) throws Exception {
        String path = APIPath.User_Get_Setting;
        UserSettingPojo pojo = new UserSettingPojo();
        pojo.setUserId(userId);
        pojo.setScope(scope);
        pojo.setKey(key);
        return AdminHttpUtils.httpJsonPost(path, pojo, UserSettingPojo.class);
    }

    /**
     * 设置用户设置
     * @param userId 用户ID
     * @param scope 设置范围
     * @param key 设置键
     * @param value 设置值
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setUserSetting(String userId, int scope, String key, String value) throws Exception {
        String path = APIPath.User_Put_Setting;
        UserSettingPojo pojo = new UserSettingPojo();
        pojo.setUserId(userId);
        pojo.setScope(scope);
        pojo.setKey(key);
        pojo.setValue(value);
        return AdminHttpUtils.httpJsonPost(path, pojo, Void.class);
    }

    /**
     * 健康检查
     * @return 健康检查结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<HealthCheckResult> healthCheck() throws Exception {
        return AdminHttpUtils.httpGet(APIPath.Health, HealthCheckResult.class);
    }

    /**
     * 获取客户信息
     * @return 客户信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<String> getCustomer() throws Exception {
        return AdminHttpUtils.httpJsonPost(APIPath.GET_CUSTOMER, null, String.class);
    }
}
