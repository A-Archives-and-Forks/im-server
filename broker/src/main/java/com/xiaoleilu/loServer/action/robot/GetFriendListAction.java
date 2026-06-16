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
import com.xiaoleilu.loServer.model.FriendData;
import cn.wildfirechat.pojos.OutputGetFriendList;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route(APIPath.Robot_Friend_Get_List)
@HttpMethod("POST")
public class GetFriendListAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            RestResult result;

            String owner = robot.getOwner();
            if (StringUtil.isNullOrEmpty(owner)) {
                result = RestResult.resultOf(ErrorCode.ERROR_CODE_NOT_EXIST);
            } else {
                List<FriendData> dataList = messagesStore.getFriendList(owner, null, 0);
                List<String> friendIds = new ArrayList<>();
                dataList.sort(Comparator.comparingLong(FriendData::getTimestamp));
                for (FriendData data : dataList) {
                    if (data.getState() == 0) {
                        friendIds.add(data.getFriendUid());
                    }
                }
                OutputGetFriendList output = new OutputGetFriendList();
                output.setFriends(friendIds);
                result = RestResult.ok(output);
            }

            setResponseContent(result, response);
        }
        return true;
    }
}
