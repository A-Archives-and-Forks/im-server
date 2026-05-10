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
import cn.wildfirechat.pojos.UserSettingPojo;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;

import static win.liyufan.im.IMTopic.PutUserSettingTopic;
import static win.liyufan.im.UserSettingScope.kUserSettingMyChannels;

@Route(APIPath.User_Put_Setting)
@HttpMethod("POST")
public class UserSettingPutAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            UserSettingPojo userSettingPojo = getRequestBody(request.getNettyRequest(), UserSettingPojo.class);
            if (userSettingPojo != null && !StringUtil.isNullOrEmpty(userSettingPojo.getUserId())) {
                WFCMessage.ModifyUserSettingReq modifyUserSettingReq = WFCMessage.ModifyUserSettingReq.newBuilder().setScope(userSettingPojo.getScope()).setKey(userSettingPojo.getKey()).setValue(userSettingPojo.getValue()).build();
                sendApiMessage(response, userSettingPojo.getUserId(), PutUserSettingTopic, modifyUserSettingReq.toByteArray(), result -> {
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeBytes(result);
                    ErrorCode errorCode = ErrorCode.fromCode(byteBuf.readByte());
                    return new Result(errorCode);
                });
                return false;
            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(gson.toJson(result));
            }
        }
        return true;
    }
}
