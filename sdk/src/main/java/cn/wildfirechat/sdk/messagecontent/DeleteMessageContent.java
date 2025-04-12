package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DeleteMessageContent extends MessageContent {
    private long messageId;
    private String operatorId;

    //必须有个空的构造函数
    public DeleteMessageContent() {
    }

    public long getMessageId() {
        return messageId;
    }

    public String getOperatorId() {
        return operatorId;
    }


    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Delete;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Not_Persist;
    }

    @Override
    public MessagePayload encode() {
        throw new RuntimeException("Delete message cannot encode");
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        operatorId = payload.getContent();
        messageId = Long.parseLong(new String(Base64.getDecoder().decode(payload.getBase64edData()), StandardCharsets.UTF_8));
    }
}
