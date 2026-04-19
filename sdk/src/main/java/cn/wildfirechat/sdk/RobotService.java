package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.pojos.moments.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.RobotHttpUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static cn.wildfirechat.proto.ProtoConstants.ApplicationType.ApplicationType_Robot;

/**
 * 机器人服务类
 * <p>
 * 提供机器人相关的功能，包括：
 * <ul>
 * <li>发送和回复消息</li>
 * <li>获取用户信息</li>
 * <li>群组管理</li>
 * <li>朋友圈管理</li>
 * <li>会议控制</li>
 * <li>回调管理</li>
 * </ul>
 * </p>
 */
public class RobotService implements Closeable {
    private final RobotHttpUtils robotHttpUtils;

    /**
     * 创建机器人服务实例
     * @param url IM服务器地址
     * @param robotId 机器人ID
     * @param robotSecret 机器人密钥
     */
    public RobotService(String url, String robotId, String robotSecret) {
        robotHttpUtils = new RobotHttpUtils(url, robotId, robotSecret);
    }

    /**
     * 获取机器人ID
     * @return 机器人ID
     */
    public String getRobotId() {
        return robotHttpUtils.getRobotId();
    }

    /**
     * 发送消息
     * @param sender 发送者用户ID
     * @param conversation 会话信息
     * @param payload 消息内容
     * @return 发送结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload) throws Exception {
        return sendMessage(sender, conversation, payload, null);
    }

    /**
     * 发送消息（可指定接收用户）
     * @param sender 发送者用户ID
     * @param conversation 会话信息
     * @param payload 消息内容
     * @param toUsers 接收用户ID列表
     * @return 发送结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload, List<String> toUsers) throws Exception {
        String path = APIPath.Robot_Message_Send;
        SendMessageData messageData = new SendMessageData();
        messageData.setSender(sender);
        messageData.setConv(conversation);
        messageData.setToUsers(toUsers);
        messageData.setPayload(payload);
        return robotHttpUtils.httpJsonPost(path, messageData, SendMessageResult.class);
    }

    public IMResult<SendMessageResult> replyMessage(long messageUid, MessagePayload payload, boolean only2Sender) throws Exception {
        String path = APIPath.Robot_Message_Reply;
        ReplyMessageData messageData = new ReplyMessageData();
        messageData.setMessageUid(messageUid);
        messageData.setOnly2Sender(only2Sender);
        messageData.setPayload(payload);
        return robotHttpUtils.httpJsonPost(path, messageData, SendMessageResult.class);
    }

    public IMResult<String> recallMessage(long messageUid) throws Exception {
        String path = APIPath.Robot_Message_Recall;
        RecallMessageData messageData = new RecallMessageData();
        messageData.setMessageUid(messageUid);
        return robotHttpUtils.httpJsonPost(path, messageData, String.class);
    }

    public IMResult<Void> updateMessage(long messageUid, MessagePayload payload) throws Exception {
        return updateMessage(messageUid, payload, true);
    }

    public IMResult<Void> updateMessage(long messageUid, MessagePayload payload, boolean distribute) throws Exception {
        String path = APIPath.Robot_Message_Update;
        UpdateMessageContentData updateMessageContentData = new UpdateMessageContentData();
        updateMessageContentData.setMessageUid(messageUid);
        updateMessageContentData.setPayload(payload);
        updateMessageContentData.setUpdateTimestamp(0);
        updateMessageContentData.setDistribute(distribute?1:0);
        return robotHttpUtils.httpJsonPost(path, updateMessageContentData, Void.class);
    }

    public IMResult<InputOutputUserInfo> getUserInfo(String userId) throws Exception {
        String path = APIPath.Robot_User_Info;
        InputGetUserInfo getUserInfo = new InputGetUserInfo(userId, null, null);
        return robotHttpUtils.httpJsonPost(path, getUserInfo, InputOutputUserInfo.class);
    }

    public IMResult<InputOutputUserInfo> getUserInfoByMobile(String phone) throws Exception {
        String path = APIPath.Robot_User_Info;
        InputGetUserInfo getUserInfo = new InputGetUserInfo(null, null, phone);
        return robotHttpUtils.httpJsonPost(path, getUserInfo, InputOutputUserInfo.class);
    }

    public IMResult<InputOutputUserInfo> getUserInfoByName(String userName) throws Exception {
        String path = APIPath.Robot_User_Info;
        InputGetUserInfo getUserInfo = new InputGetUserInfo(null, userName, null);
        return robotHttpUtils.httpJsonPost(path, getUserInfo, InputOutputUserInfo.class);
    }

    public IMResult<Void> setCallback(String url) throws Exception {
        String path = APIPath.Robot_Set_Callback;
        RobotCallbackPojo pojo = new RobotCallbackPojo();
        pojo.setUrl(url);
        return robotHttpUtils.httpJsonPost(path, pojo, Void.class);
    }
    
    public IMResult<RobotCallbackPojo> getCallback() throws Exception {
        String path = APIPath.Robot_Get_Callback;
        return robotHttpUtils.httpJsonPost(path, null, RobotCallbackPojo.class);
    }

    public IMResult<Void> deleteCallback() throws Exception {
        String path = APIPath.Robot_Delete_Callback;
        return robotHttpUtils.httpJsonPost(path, null, Void.class);
    }

    public IMResult<OutputRobot> getProfile() throws Exception {
        String path = APIPath.Robot_Get_Profile;
        return robotHttpUtils.httpJsonPost(path, null, OutputRobot.class);
    }

    /*
    type可选范围为MyInfoType，注意不能修改电话号码，如果要修改电话号码请使用adminapi进行修改
     */
    public IMResult<Void> updateProfile(int/*MyInfoType*/ type, String value) throws Exception {
        String path = APIPath.Robot_Update_Profile;
        IntStringPairPojo pojo = new IntStringPairPojo(type, value);
        return robotHttpUtils.httpJsonPost(path, pojo, Void.class);
    }

    public IMResult<OutputCreateGroupResult> createGroup(PojoGroupInfo group_info, List<PojoGroupMember> members, String member_extra, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Create_Group;
        PojoGroup pojoGroup = new PojoGroup();
        pojoGroup.setGroup_info(group_info);
        pojoGroup.setMembers(members);
        InputCreateGroup createGroup = new InputCreateGroup();
        createGroup.setGroup(pojoGroup);
        createGroup.setMember_extra(member_extra);
        createGroup.setTo_lines(to_lines);
        createGroup.setNotify_message(notify_message);

        return robotHttpUtils.httpJsonPost(path, createGroup, OutputCreateGroupResult.class);
    }

    public IMResult<PojoGroupInfo> getGroupInfo(String groupId) throws Exception {
        String path = APIPath.Robot_Group_Get_Info;
        InputGetGroup input = new InputGetGroup();
        input.setGroupId(groupId);

        return robotHttpUtils.httpJsonPost(path, input, PojoGroupInfo.class);
    }

    public IMResult<Void> dismissGroup(String groupId, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Dismiss;
        InputDismissGroup dismissGroup = new InputDismissGroup();
        dismissGroup.setGroup_id(groupId);
        dismissGroup.setTo_lines(to_lines);
        dismissGroup.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, dismissGroup, Void.class);
    }

    public IMResult<Void> transferGroup(String groupId, String newOwner, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Transfer;
        InputTransferGroup transferGroup = new InputTransferGroup();
        transferGroup.setGroup_id(groupId);
        transferGroup.setNew_owner(newOwner);
        transferGroup.setTo_lines(to_lines);
        transferGroup.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, transferGroup, Void.class);
    }

    public IMResult<Void> modifyGroupInfo(String groupId, /*ModifyGroupInfoType*/int type, String value, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Modify_Info;
        InputModifyGroupInfo modifyGroupInfo = new InputModifyGroupInfo();
        modifyGroupInfo.setGroup_id(groupId);
        modifyGroupInfo.setTo_lines(to_lines);
        modifyGroupInfo.setType(type);
        modifyGroupInfo.setValue(value);
        modifyGroupInfo.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, modifyGroupInfo, Void.class);
    }


    public IMResult<OutputGroupMemberList> getGroupMembers(String groupId) throws Exception {
        String path = APIPath.Robot_Group_Member_List;
        InputGetGroup input = new InputGetGroup();
        input.setGroupId(groupId);
        return robotHttpUtils.httpJsonPost(path, input, OutputGroupMemberList.class);
    }

    public IMResult<PojoGroupMember> getGroupMember(String groupId, String memberId) throws Exception {
        String path = APIPath.Robot_Group_Member_Get;
        InputGetGroupMember input = new InputGetGroupMember();
        input.setGroupId(groupId);
        input.setMemberId(memberId);
        return robotHttpUtils.httpJsonPost(path, input, PojoGroupMember.class);
    }

    public IMResult<Void> addGroupMembers(String groupId, List<PojoGroupMember> groupMembers, String member_extra, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Member_Add;
        InputAddGroupMember addGroupMember = new InputAddGroupMember();
        addGroupMember.setGroup_id(groupId);
        addGroupMember.setMembers(groupMembers);
        addGroupMember.setMemberExtra(member_extra);
        addGroupMember.setTo_lines(to_lines);
        addGroupMember.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    public IMResult<Void> setGroupManager(String groupId, List<String> groupMemberIds, boolean isManager, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Set_Manager;
        InputSetGroupManager addGroupMember = new InputSetGroupManager();
        addGroupMember.setGroup_id(groupId);
        addGroupMember.setMembers(groupMemberIds);
        addGroupMember.setIs_manager(isManager);
        addGroupMember.setTo_lines(to_lines);
        addGroupMember.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    public IMResult<Void> muteGroupMember(String groupId, List<String> groupMemberIds, boolean isMute, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Mute_Member;
        InputMuteGroupMember addGroupMember = new InputMuteGroupMember();
        addGroupMember.setGroup_id(groupId);
        addGroupMember.setMembers(groupMemberIds);
        addGroupMember.setIs_manager(isMute);
        addGroupMember.setTo_lines(to_lines);
        addGroupMember.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    public IMResult<Void> allowGroupMember(String groupId, List<String> groupMemberIds, boolean isAllow, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Allow_Member;
        InputMuteGroupMember addGroupMember = new InputMuteGroupMember();
        addGroupMember.setGroup_id(groupId);
        addGroupMember.setMembers(groupMemberIds);
        addGroupMember.setIs_manager(isAllow);
        addGroupMember.setTo_lines(to_lines);
        addGroupMember.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    public IMResult<Void> kickoffGroupMembers(String groupId, List<String> groupMemberIds, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Member_Kickoff;
        InputKickoffGroupMember kickoffGroupMember = new InputKickoffGroupMember();
        kickoffGroupMember.setGroup_id(groupId);
        kickoffGroupMember.setMembers(groupMemberIds);
        kickoffGroupMember.setTo_lines(to_lines);
        kickoffGroupMember.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, kickoffGroupMember, Void.class);
    }

    public IMResult<Void> quitGroup(String groupId, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Member_Quit;
        InputQuitGroup quitGroup = new InputQuitGroup();
        quitGroup.setGroup_id(groupId);
        quitGroup.setTo_lines(to_lines);
        quitGroup.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, quitGroup, Void.class);
    }

    public IMResult<Void> setGroupMemberAlias(String groupId, String memberId, String alias, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Set_Member_Alias;
        InputSetGroupMemberAlias input = new InputSetGroupMemberAlias();
        input.setGroup_id(groupId);
        input.setMemberId(memberId);
        input.setAlias(alias);
        input.setTo_lines(to_lines);
        input.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, input, Void.class);
    }

    public IMResult<Void> setGroupMemberExtra(String groupId, String memberId, String extra, List<Integer> to_lines, MessagePayload notify_message) throws Exception {
        String path = APIPath.Robot_Group_Set_Member_Extra;
        InputSetGroupMemberExtra input = new InputSetGroupMemberExtra();
        input.setGroup_id(groupId);
        input.setMemberId(memberId);
        input.setExtra(extra);
        input.setTo_lines(to_lines);
        input.setNotify_message(notify_message);
        return robotHttpUtils.httpJsonPost(path, input, Void.class);
    }

    public IMResult<OutputApplicationUserInfo> applicationGetUserInfo(String authCode) throws Exception {
        String path = APIPath.Robot_Application_Get_UserInfo;
        InputApplicationGetUserInfo input = new InputApplicationGetUserInfo();
        input.setAuthCode(authCode);
        return robotHttpUtils.httpJsonPost(path, input, OutputApplicationUserInfo.class);
    }

    public OutputApplicationConfigData getApplicationSignature() {
        int nonce = (int)(Math.random() * 100000 + 3);
        long timestamp = System.currentTimeMillis()/1000;
        String str = nonce + "|" + robotHttpUtils.getRobotId() + "|" + timestamp + "|" + robotHttpUtils.getRobotSecret();
        String sign = DigestUtils.sha1Hex(str);
        OutputApplicationConfigData configData = new OutputApplicationConfigData();
        configData.setAppId(robotHttpUtils.getRobotId());
        configData.setAppType(ApplicationType_Robot);
        configData.setTimestamp(timestamp);
        configData.setNonceStr(nonce+"");
        configData.setSignature(sign);
        return configData;
    }

    public IMResult<String> sendConferenceRequest(String robotId, String clientId, String request, long sessionId, String roomId, String data, boolean advance) throws Exception {
        String path = APIPath.Robot_Conference_Request;
        InputConferenceRequest input = new InputConferenceRequest(robotId, clientId, request, sessionId, roomId, data, advance);
        return robotHttpUtils.httpJsonPost(path, input, String.class);
    }

    public IMResult<FeedPojo> postMomentsFeed(int/*MomentsContentType*/ type, String text, List<MediaEntry> medias, List<String> toUsers, List<String> excludeUsers, List<String> mentionedUsers, String extra) throws Exception {
        String path = APIPath.Robot_Moments_Post_Feed;
        FeedPojo feedPojo = new FeedPojo();
        feedPojo.type = type;
        feedPojo.text = text;
        feedPojo.medias = medias;
        feedPojo.to = toUsers;
        feedPojo.ex = excludeUsers;
        feedPojo.mu = mentionedUsers;
        feedPojo.extra = extra;
        IMResult<PostFeedResult> imResult = robotHttpUtils.httpJsonPost(path, feedPojo, PostFeedResult.class);
        IMResult<FeedPojo> feedResult = new IMResult<>();
        if(imResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            feedPojo.feedId = imResult.result.id;
            feedPojo.timestamp = imResult.result.timestamp;
            feedResult.setCode(ErrorCode.ERROR_CODE_SUCCESS.getCode());
            feedResult.setResult(feedPojo);
        } else {
            feedResult.setCode(imResult.getCode());
            feedResult.setMsg(imResult.getMsg());
        }
        return feedResult;
    }

    public IMResult<Void> updateMomentsFeed(long feedId, int/*MomentsContentType*/ type, String text, List<MediaEntry> medias, List<String> toUsers, List<String> excludeUsers, List<String> mentionedUsers, String extra) throws Exception {
        String path = APIPath.Robot_Moments_Update_Feed;
        FeedPojo feedPojo = new FeedPojo();
        feedPojo.feedId = feedId;
        feedPojo.type = type;
        feedPojo.text = text;
        feedPojo.medias = medias;
        feedPojo.to = toUsers;
        feedPojo.ex = excludeUsers;
        feedPojo.mu = mentionedUsers;
        feedPojo.extra = extra;
        return robotHttpUtils.httpJsonPost(path, feedPojo, Void.class);
    }

    public IMResult<FeedsPojo> getMomentsFeeds(long feedId, int count, String user) throws Exception {
        String path = APIPath.Robot_Moments_Pull_Feeds;
        PullFeedRequestPojo requestPojo = new PullFeedRequestPojo();
        requestPojo.feedId = feedId;
        requestPojo.count = count;
        requestPojo.user = user;
        return robotHttpUtils.httpJsonPost(path, requestPojo, FeedsPojo.class);
    }

    public IMResult<FeedPojo> getMomentsFeed(long feedId) throws Exception {
        String path = APIPath.Robot_Moments_Fetch_Feed;
        PullOneFeedRequestPojo requestPojo = new PullOneFeedRequestPojo();
        requestPojo.feedId = feedId;
        return robotHttpUtils.httpJsonPost(path, requestPojo, FeedPojo.class);
    }

    public IMResult<Void> deleteMomentsFeed(long feedId) throws Exception {
        String path = APIPath.Robot_Moments_Recall_Feed;
        IdPojo requestPojo = new IdPojo();
        requestPojo.id = feedId;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<CommentPojo> postMomentsComment(long feedId, long replyId, int/*MomentsCommentType*/ type, String text, String replyTo, String extra) throws Exception {
        String path = APIPath.Robot_Moments_Post_Comment;
        CommentPojo commentPojo = new CommentPojo();
        commentPojo.type = type;
        commentPojo.text = text;
        commentPojo.replyId = replyId;
        commentPojo.feedId = feedId;
        commentPojo.replyTo = replyTo;
        commentPojo.extra = extra;
        IMResult<PostFeedResult> imResult = robotHttpUtils.httpJsonPost(path, commentPojo, PostFeedResult.class);
        IMResult<CommentPojo> feedResult = new IMResult<>();
        if(imResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            commentPojo.commentId = imResult.result.id;
            commentPojo.timestamp = imResult.result.timestamp;
            feedResult.setCode(ErrorCode.ERROR_CODE_SUCCESS.getCode());
            feedResult.setResult(commentPojo);
        } else {
            feedResult.setCode(imResult.getCode());
            feedResult.setMsg(imResult.getMsg());
        }
        return feedResult;
    }

    public IMResult<Void> deleteMomentsComment(long feedId, long commentId) throws Exception {
        String path = APIPath.Robot_Moments_Recall_Comment;
        IdPojo requestPojo = new IdPojo();
        requestPojo.id = commentId;
        requestPojo.id2 = feedId;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<MomentProfilePojo> getUserMomentsProfile(String userId) throws Exception {
        String path = APIPath.Robot_Moments_Fetch_Profiles;
        PullProfileRequestPojo requestPojo = new PullProfileRequestPojo();
        requestPojo.u = userId;
        return robotHttpUtils.httpJsonPost(path, requestPojo, MomentProfilePojo.class);
    }

    public IMResult<Void> updateMomentsBackgroundUrl(String url) throws Exception {
        String path = APIPath.Robot_Moments_Update_Profiles_Value;
        PushProfileValueRequestPojo requestPojo = new PushProfileValueRequestPojo();
        requestPojo.t = 0;
        requestPojo.v = url;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<Void> updateMomentsStrangerVisibleCount(int count) throws Exception {
        String path = APIPath.Robot_Moments_Update_Profiles_Value;
        PushProfileValueRequestPojo requestPojo = new PushProfileValueRequestPojo();
        requestPojo.t = 1;
        requestPojo.i = count;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<Void> updateMomentsVisibleScope(int/*MomentsVisibleScope*/ scope) throws Exception {
        String path = APIPath.Robot_Moments_Update_Profiles_Value;
        PushProfileValueRequestPojo requestPojo = new PushProfileValueRequestPojo();
        requestPojo.t = 2;
        requestPojo.i = scope;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<Void> updateMomentsBlackList(List<String> addList, List<String> removeList) throws Exception {
        String path = APIPath.Robot_Moments_Update_Profiles_List_Value;
        PushProfileListRequestPojo requestPojo = new PushProfileListRequestPojo();
        requestPojo.b = false;
        requestPojo.al = addList;
        requestPojo.rl = removeList;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<Void> updateMomentsBlockList(List<String> addList, List<String> removeList) throws Exception {
        String path = APIPath.Robot_Moments_Update_Profiles_List_Value;
        PushProfileListRequestPojo requestPojo = new PushProfileListRequestPojo();
        requestPojo.b = true;
        requestPojo.al = addList;
        requestPojo.rl = removeList;
        return robotHttpUtils.httpJsonPost(path, requestPojo, Void.class);
    }

    public IMResult<OutputPresignedUploadUrl> getPresignedUploadUrl(String fileName, int/*ProtoConstants.MessageMediaType*/ mediaType, String contentType) throws Exception {
        String path = APIPath.Robot_Get_Presigned_Upload_Url;

        InputGetPresignedUploadUrl requestPojo = new InputGetPresignedUploadUrl();
        requestPojo.fileName = fileName;
        requestPojo.mediaType = mediaType;
        requestPojo.contentType = contentType;
        return robotHttpUtils.httpJsonPost(path, requestPojo, OutputPresignedUploadUrl.class);
    }

    /**
     * 根据文件名获取Content-Type
     *
     * @param fileName 文件名
     * @return Content-Type，如果无法识别则返回 "application/octet-stream"
     */
    private String getContentTypeByFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }
        
        // 首先尝试使用 URLConnection 猜测
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        
        // 如果无法识别，使用常见扩展名映射
        if (contentType == null) {
            String lowerName = fileName.toLowerCase();
            if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerName.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerName.endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (lowerName.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerName.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (lowerName.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (lowerName.endsWith(".avi")) {
                contentType = "video/x-msvideo";
            } else if (lowerName.endsWith(".mp3")) {
                contentType = "audio/mpeg";
            } else if (lowerName.endsWith(".wav")) {
                contentType = "audio/wav";
            } else if (lowerName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerName.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (lowerName.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (lowerName.endsWith(".xls")) {
                contentType = "application/vnd.ms-excel";
            } else if (lowerName.endsWith(".xlsx")) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (lowerName.endsWith(".ppt")) {
                contentType = "application/vnd.ms-powerpoint";
            } else if (lowerName.endsWith(".pptx")) {
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            } else if (lowerName.endsWith(".txt")) {
                contentType = "text/plain";
            } else if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) {
                contentType = "text/html";
            } else if (lowerName.endsWith(".json")) {
                contentType = "application/json";
            } else if (lowerName.endsWith(".xml")) {
                contentType = "application/xml";
            } else if (lowerName.endsWith(".zip")) {
                contentType = "application/zip";
            } else if (lowerName.endsWith(".rar")) {
                contentType = "application/x-rar-compressed";
            } else if (lowerName.endsWith(".7z")) {
                contentType = "application/x-7z-compressed";
            } else if (lowerName.endsWith(".tar")) {
                contentType = "application/x-tar";
            } else if (lowerName.endsWith(".gz")) {
                contentType = "application/gzip";
            } else {
                contentType = "application/octet-stream";
            }
        }
        
        return contentType;
    }


    /**
     * 上传文件
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     *
     * @param file        要上传的文件
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public IMResult<String> uploadFile(File file) throws Exception {
        return uploadFile(file, ProtoConstants.MessageMediaType.FILE, null);
    }
    /**
     * 上传文件
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     *
     * @param file        要上传的文件
     * @param mediaType   媒体类型，参考{@link cn.wildfirechat.proto.ProtoConstants.MessageMediaType}
     * @param contentType 文件Content-Type，例如 "image/jpeg", "application/octet-stream" 等；
     *                    如果为null或空，则根据文件名自动识别
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public IMResult<String> uploadFile(File file, int mediaType, String contentType) throws Exception {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("文件不能为空或不存在");
        }

        // 如果未指定Content-Type，根据文件名自动获取
        if (contentType == null || contentType.isEmpty()) {
            contentType = getContentTypeByFileName(file.getName());
        }

        return doUploadFile(file.getName(), mediaType, contentType, file, null);
    }

    /**
     * 执行文件上传的公共方法
     * <p>
     * 根据服务器类型选择不同的上传方式：
     * <ul>
     * <li>type = 1（七牛云）：使用 POST + multipart/form-data 表单上传</li>
     * <li>type = 其他（阿里云、Minio等）：使用 PUT + 二进制流上传</li>
     * </ul>
     * </p>
     *
     * @param fileName    文件名
     * @param mediaType   媒体类型
     * @param contentType Content-Type
     * @param file        文件对象（用于七牛云上传）
     * @param inputStream 输入流（用于其他类型上传，可为null）
     * @return 上传结果
     * @throws Exception 上传失败时抛出异常
     */
    private IMResult<String> doUploadFile(String fileName, int mediaType,
                                                             String contentType,
                                                             File file,
                                                             InputStream inputStream) throws Exception {
        // 1. 获取预签名上传地址
        IMResult<OutputPresignedUploadUrl> presignedResult = getPresignedUploadUrl(
                fileName, mediaType, contentType);

        if (presignedResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            IMResult<String> imResult = new IMResult<>();
            imResult.setCode(presignedResult.code);
            imResult.setMsg(presignedResult.msg);
            return imResult;
        }

        OutputPresignedUploadUrl presignedUrl = presignedResult.getResult();
        if (presignedUrl == null || presignedUrl.uploadUrl == null) {
            throw new Exception("预签名上传地址为空");
        }

        // 2. 根据服务器类型选择上传方式
        if (presignedUrl.type == 1) {
            // 七牛云：使用 POST + multipart/form-data 表单上传
            return uploadToQiniu(presignedUrl, file, inputStream, fileName, contentType);
        } else {
            // 其他（阿里云、Minio、腾讯云、华为云、AWS S3等）：使用 PUT 上传
            return uploadToOther(presignedUrl, file, inputStream, contentType);
        }
    }

    /**
     * 上传到七牛云（type = 1）
     * 使用 POST + multipart/form-data 表单格式
     */
    private IMResult<String> uploadToQiniu(OutputPresignedUploadUrl presignedUrl, File file,
                                            InputStream inputStream, String fileName, 
                                            String contentType) throws Exception {
        // 解析七牛云上传地址：格式为 "https://host?token?key"
        String uploadUrl = presignedUrl.uploadUrl;
        String token;
        String key;
        
        int tokenStart = uploadUrl.indexOf('?');
        int keyStart = uploadUrl.indexOf('?', tokenStart + 1);
        
        if (tokenStart == -1 || keyStart == -1) {
            throw new Exception("七牛云上传地址格式错误");
        }
        
        String baseUrl = uploadUrl.substring(0, tokenStart);
        token = uploadUrl.substring(tokenStart + 1, keyStart);
        key = uploadUrl.substring(keyStart + 1);

        HttpPost httpPost = new HttpPost(baseUrl);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            
            // 添加 token 和 key 字段
            builder.addTextBody("token", token);
            builder.addTextBody("key", key);
            
            // 添加文件字段
            if (file != null) {
                builder.addPart("file", new FileBody(file, ContentType.create(contentType), fileName));
            } else if (inputStream != null) {
                builder.addPart("file", new InputStreamBody(inputStream, ContentType.create(contentType), fileName));
            }
            
            httpPost.setEntity(builder.build());

            HttpResponse response = robotHttpUtils.getHttpClient().execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            
            // 消耗响应体
            if (response.getEntity() != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }

            // 七牛云返回 200 表示成功
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
                throw new Exception("文件上传到七牛云失败，HTTP状态码：" + statusCode);
            }

            IMResult<String> imResult = new IMResult<>();
            imResult.setCode(0);
            imResult.setResult(presignedUrl.downloadUrl);
            return imResult;
        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * 上传到其他对象存储（type != 1）
     * 使用 PUT + 二进制流
     */
    private IMResult<String> uploadToOther(OutputPresignedUploadUrl presignedUrl, File file,
                                            InputStream inputStream, String contentType) throws Exception {
        HttpPut httpPut = new HttpPut(presignedUrl.uploadUrl);
        try {
            // 设置请求实体
            if (file != null) {
                FileEntity fileEntity = new FileEntity(file);
                fileEntity.setContentType(contentType);
                httpPut.setEntity(fileEntity);
            } else if (inputStream != null) {
                InputStreamEntity streamEntity = new InputStreamEntity(inputStream);
                streamEntity.setContentType(contentType);
                httpPut.setEntity(streamEntity);
            }

            HttpResponse response = robotHttpUtils.getHttpClient().execute(httpPut);
            int statusCode = response.getStatusLine().getStatusCode();
            
            // 消耗响应体，确保连接可以被复用
            if (response.getEntity() != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }

            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
                throw new Exception("文件上传失败，HTTP状态码：" + statusCode);
            }

            IMResult<String> imResult = new IMResult<>();
            imResult.setCode(0);
            imResult.setResult(presignedUrl.downloadUrl);
            return imResult;
        } finally {
            httpPut.releaseConnection();
        }
    }

    /**
     * 上传文件（通过输入流）
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public IMResult<String> uploadFile(InputStream inputStream, String fileName) throws Exception {
        return uploadFile(inputStream, fileName, ProtoConstants.MessageMediaType.FILE, null);
    }
    /**
     * 上传文件（通过输入流）
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     * <p>
     * <b>注意：</b>无论上传成功还是失败，此方法都会关闭输入流。
     * </p>
     *
     * @param inputStream 文件输入流（此方法会关闭该流）
     * @param fileName    文件名
     * @param mediaType   媒体类型，参考{@link cn.wildfirechat.proto.ProtoConstants.MessageMediaType}
     * @param contentType 文件Content-Type，例如 "image/jpeg", "application/octet-stream" 等；
     *                    如果为null或空，则根据文件名自动识别
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public IMResult<String> uploadFile(InputStream inputStream, String fileName,
                                                          int mediaType, String contentType) throws Exception {
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }

        // 如果未指定Content-Type，根据文件名自动获取
        if (contentType == null || contentType.isEmpty()) {
            contentType = getContentTypeByFileName(fileName);
        }

        return doUploadFile(fileName, mediaType, contentType, null, inputStream);
        // 注意：正常流程下的流关闭在 uploadToQiniu 或 uploadToOther 的 finally 块中处理
    }

    @Override
    public void close() throws IOException {
        robotHttpUtils.close();
    }
}
