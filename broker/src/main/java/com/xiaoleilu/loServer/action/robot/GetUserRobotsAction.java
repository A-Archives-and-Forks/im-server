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
import cn.wildfirechat.pojos.InputUserId;
import cn.wildfirechat.pojos.OutputGetRobotList;
import cn.wildfirechat.pojos.OutputRobot;
import cn.wildfirechat.proto.WFCMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Route(APIPath.Robot_Get_User_Robots)
@HttpMethod("POST")
public class GetUserRobotsAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputUserId input = getRequestBody(request.getNettyRequest(), InputUserId.class);
            if (input != null && !StringUtil.isNullOrEmpty(input.getUserId())) {
                List<String> robotIds = messagesStore.getUserRobotIds(input.getUserId());
                OutputGetRobotList output = new OutputGetRobotList();
                output.robotInfoList = new ArrayList<>();

                for (String robotId : robotIds) {
                    WFCMessage.User user = messagesStore.getUserInfo(robotId);
                    WFCMessage.Robot robot = messagesStore.getRobot(robotId);
                    if (user != null && robot != null) {
                        OutputRobot outputRobot = new OutputRobot();
                        outputRobot.fromUser(user);
                        outputRobot.fromRobot(robot, false);
                        output.robotInfoList.add(outputRobot);
                    }
                }

                setResponseContent(RestResult.ok(output), response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }
}
