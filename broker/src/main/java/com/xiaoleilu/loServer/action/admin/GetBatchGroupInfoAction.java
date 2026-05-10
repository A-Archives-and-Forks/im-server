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
import cn.wildfirechat.pojos.PojoGroupInfo;
import cn.wildfirechat.pojos.PojoGroupInfoList;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Route(APIPath.Group_Batch_Info)
@HttpMethod("POST")
public class GetBatchGroupInfoAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            List<String> inputGetGroup = getRequestBody(request.getNettyRequest(), ArrayList.class);
            if (inputGetGroup != null && !inputGetGroup.isEmpty()) {
                List<WFCMessage.UserRequest> requests = new ArrayList<>();
                for (String s : inputGetGroup) {
                    requests.add(WFCMessage.UserRequest.newBuilder().setUid(s).build());
                }
                List<WFCMessage.GroupInfo> groupInfoDataList = messagesStore.getGroupInfos(requests);
                PojoGroupInfoList pojoGroupInfoList = new PojoGroupInfoList();
                pojoGroupInfoList.groupInfoList = new ArrayList<>();

                for (WFCMessage.GroupInfo groupInfo : groupInfoDataList) {
                    pojoGroupInfoList.groupInfoList.add(PojoGroupInfo.fromProto(groupInfo));
                }

                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.ok(pojoGroupInfoList);
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
