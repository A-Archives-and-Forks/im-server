package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 转换消息内容类（透传消息）
 * <p>
 * 表示转换类型的透传消息，包含id、会议id、用户id、时间戳、时长和内容。
 * </p>
 */
public class TranscriptionMessageContent extends MessageContent {
    private long transcriptionId;
    private String meetingId;
    private String userId;
    private long timestamp;
    private long duration;
    private String content;

    //必须有个空的构造函数
    public TranscriptionMessageContent() {
    }

    public TranscriptionMessageContent(long transcriptionId, String meetingId, String userId, long timestamp, long duration, String content) {
        this.transcriptionId = transcriptionId;
        this.meetingId = meetingId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.duration = duration;
        this.content = content;
    }

    public long getTranscriptionId() {
        return transcriptionId;
    }

    public void setTranscriptionId(long transcriptionId) {
        this.transcriptionId = transcriptionId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Transcription;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Transparent;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        JSONObject jsonObject = new JSONObject();
        if (transcriptionId > 0) {
            jsonObject.put("id", transcriptionId);
        }
        if (!TextUtils.isEmpty(meetingId)) {
            jsonObject.put("meetingId", meetingId);
        }
        if (!TextUtils.isEmpty(userId)) {
            jsonObject.put("userId", userId);
        }
        if (timestamp > 0) {
            jsonObject.put("timestamp", timestamp);
        }
        if (duration > 0) {
            jsonObject.put("duration", duration);
        }
        if (!TextUtils.isEmpty(content)) {
            jsonObject.put("content", content);
        }
        payload.setContent(jsonObject.toJSONString());
        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        if (!TextUtils.isEmpty(payload.getContent())) {
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(payload.getContent());
                transcriptionId = ((Number) jsonObject.get("id")).longValue();
                meetingId = (String) jsonObject.get("meetingId");
                userId = (String) jsonObject.get("userId");
                Object ts = jsonObject.get("timestamp");
                if (ts != null) {
                    timestamp = ((Number) ts).longValue();
                }
                Object dur = jsonObject.get("duration");
                if (dur != null) {
                    duration = ((Number) dur).longValue();
                }
                content = (String) jsonObject.get("content");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
