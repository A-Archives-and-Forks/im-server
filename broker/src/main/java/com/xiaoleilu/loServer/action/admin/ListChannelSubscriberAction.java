package com.xiaoleilu.loServer.action.admin;


import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.InputStringCountOffset;
import cn.wildfirechat.pojos.InputSubscribeChannel;
import cn.wildfirechat.pojos.OutputBooleanValue;
import cn.wildfirechat.pojos.PojoTotalList;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;

import java.util.List;

@Route(APIPath.List_Channel_Subscriber)
@HttpMethod("POST")
public class ListChannelSubscriberAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputStringCountOffset inputSubscribeChannel = getRequestBody(request.getNettyRequest(), InputStringCountOffset.class);
            response.setStatus(HttpResponseStatus.OK);
            if (inputSubscribeChannel != null
                && !StringUtil.isNullOrEmpty(inputSubscribeChannel.target)
                && inputSubscribeChannel.count > 0) {
                int count = messagesStore.getChannelListenerCount(inputSubscribeChannel.target);
                List<String> list = messagesStore.getChannelListenerList(inputSubscribeChannel.target, inputSubscribeChannel.count, inputSubscribeChannel.offset);
                RestResult result = RestResult.ok(new PojoTotalList(count, list));
                response.setContent(gson.toJson(result));
            } else {
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(gson.toJson(result));
            }
        }
        return true;
    }
}
