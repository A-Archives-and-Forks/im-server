package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 会议纪要消息内容类
 * <p>
 * 表示会议纪要类型的消息内容，包含纪要文本、会议标题和会议ID。
 * </p>
 */
public class MeetingMinutesMessageContent extends MessageContent {
    private String text;
    private String title;
    private String meetingId;

    //必须有个空的构造函数
    public MeetingMinutesMessageContent() {
    }

    public MeetingMinutesMessageContent(String text, String title, String meetingId) {
        this.text = text;
        this.title = title;
        this.meetingId = meetingId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Meeting_Minutes;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setContent(text);
        payload.setSearchableContent(title);
        if (!TextUtils.isEmpty(meetingId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("meetingId", meetingId);
            payload.setBase64edData(Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8)));
        }
        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        text = payload.getContent();
        title = payload.getSearchableContent();
        if (!TextUtils.isEmpty(payload.getBase64edData())) {
            String jsonStr = new String(Base64.getDecoder().decode(payload.getBase64edData()), StandardCharsets.UTF_8);
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonStr);
                meetingId = (String) jsonObject.get("meetingId");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
