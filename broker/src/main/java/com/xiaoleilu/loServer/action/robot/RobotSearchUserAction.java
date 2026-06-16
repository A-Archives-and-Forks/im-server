/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.xiaoleilu.loServer.action.robot;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import cn.wildfirechat.pojos.InputOutputUserInfo;
import cn.wildfirechat.pojos.mesh.PojoSearchUserReq;
import cn.wildfirechat.pojos.mesh.PojoSearchUserRes;
import cn.wildfirechat.proto.WFCMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Route(APIPath.Robot_Search_User)
@HttpMethod("POST")
public class RobotSearchUserAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            PojoSearchUserReq input = getRequestBody(request.getNettyRequest(), PojoSearchUserReq.class);
            if (input != null && !StringUtil.isNullOrEmpty(input.getKeyword())) {
                List<WFCMessage.User> users = new ArrayList<>();
                messagesStore.searchUser(robot.getUid(), input.keyword, input.searchType, input.userType, input.page, users);
                PojoSearchUserRes res = new PojoSearchUserRes();
                res.keyword = input.keyword;
                res.userInfos = new ArrayList<>();
                for (WFCMessage.User user : users) {
                    InputOutputUserInfo inputOutputUserInfo = InputOutputUserInfo.fromPbUser(user);
                    res.userInfos.add(inputOutputUserInfo);
                }
                setResponseContent(RestResult.ok(res), response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }
}
