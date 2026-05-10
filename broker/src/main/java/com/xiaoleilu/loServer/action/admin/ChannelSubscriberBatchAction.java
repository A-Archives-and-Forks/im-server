package com.xiaoleilu.loServer.action.admin;


import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.InputSubscribeChannelBatch;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;

import com.hazelcast.util.StringUtil;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.concurrent.Executor;


@Route(APIPath.Batch_Subscribe_Channel)
@HttpMethod("POST")
public class ChannelSubscriberBatchAction extends AdminAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputSubscribeChannelBatch input = getRequestBody(request.getNettyRequest(), InputSubscribeChannelBatch.class);
            if (input != null && !StringUtil.isNullOrEmpty(input.getChannelId()) && input.getUserIds() != null && !input.getUserIds().isEmpty() ) {
                ErrorCode errorCode = messagesStore.batchListenChannel(input.getChannelId(), input.getUserIds(), input.getSubscribe()>0);
                sendResponse(response, errorCode, null);
            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(gson.toJson(result));
            }
        }
        return true;
    }
}
