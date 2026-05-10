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
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;

@Route(APIPath.User_Get_Setting)
@HttpMethod("POST")
public class UserSettingGetAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            UserSettingPojo input = getRequestBody(request.getNettyRequest(), UserSettingPojo.class);
            if (input != null && (!StringUtil.isNullOrEmpty(input.getUserId()))) {
                WFCMessage.UserSettingEntry entry = messagesStore.getUserSetting(input.getUserId(), input.getScope(), input.getKey());
                response.setStatus(HttpResponseStatus.OK);
                RestResult result;
                if (entry == null) {
                    result = RestResult.resultOf(ErrorCode.ERROR_CODE_NOT_EXIST);
                } else {
                    input.setValue(entry.getValue());
                    result = RestResult.ok(input);
                }
                response.setContent(gson.toJson(result));
            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(gson.toJson(result));
            }

        }
        return true;
    }
}
