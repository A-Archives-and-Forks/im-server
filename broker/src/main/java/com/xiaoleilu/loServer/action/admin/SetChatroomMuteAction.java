/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.xiaoleilu.loServer.action.admin;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.InputChatroomMute;

import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;

@Route(APIPath.Chatroom_MuteAll)
@HttpMethod("POST")
public class SetChatroomMuteAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputChatroomMute input = getRequestBody(request.getNettyRequest(), InputChatroomMute.class);
            if (input != null
                && !StringUtil.isNullOrEmpty(input.getChatroomId())) {

                ErrorCode code = messagesStore.setChatroomState(input.getChatroomId(), input.getStatus());
                response.setStatus(HttpResponseStatus.OK);
                response.setContent(gson.toJson(RestResult.resultOf(code)));
                sendResponse(response, ErrorCode.ERROR_CODE_SUCCESS, null);
            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(gson.toJson(result));
            }

        }
        return true;
    }
}
