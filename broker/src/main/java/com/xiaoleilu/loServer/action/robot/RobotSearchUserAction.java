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
                int mask = messagesStore.getRobotGetUserInfoMask();
                
                for (WFCMessage.User user : users) {
                    InputOutputUserInfo outputUserInfo = InputOutputUserInfo.fromPbUser(user);
                    if((mask & 1) == 0) {
                        outputUserInfo.setName(null);
                    }
                    if((mask & 2) == 0) {
                        outputUserInfo.setMobile(null);
                    }
                    if((mask & 4) == 0) {
                        outputUserInfo.setEmail(null);
                    }
                    if((mask & 8) == 0) {
                        outputUserInfo.setAddress(null);
                    }
                    if((mask & 16) == 0) {
                        outputUserInfo.setCompany(null);
                    }
                    if((mask & 32) == 0) {
                        outputUserInfo.setExtra(null);
                    }
                    if((mask & 64) == 0) {
                        outputUserInfo.setUpdateDt(0);
                    }
                    if((mask & 128) == 0) {
                        outputUserInfo.setGender(0);
                    }
                    if((mask & 256) == 0) {
                        outputUserInfo.setSocial(null);
                    }
                    if((mask & 512) == 0) {
                        outputUserInfo.setType(0);
                    }
                    res.userInfos.add(outputUserInfo);
                }
                setResponseContent(RestResult.ok(res), response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }
}
