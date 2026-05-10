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
import cn.wildfirechat.pojos.InputCountOffset;
import cn.wildfirechat.pojos.OutputChannelInfoList;
import cn.wildfirechat.pojos.OutputGetChannelInfo;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Route(APIPath.List_Channel_Info)
@HttpMethod("POST")
public class GetChannelListAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputCountOffset inputCreateChannel = getRequestBody(request.getNettyRequest(), InputCountOffset.class);
            if (inputCreateChannel != null
                && inputCreateChannel.count > 0 && inputCreateChannel.offset >= 0) {
                int count = messagesStore.getChannelTotalCount(false);
                List<WFCMessage.ChannelInfo> list = messagesStore.getChannelInfoList(inputCreateChannel.count, inputCreateChannel.offset, false);
                OutputChannelInfoList outputChannelInfoList = new OutputChannelInfoList();
                outputChannelInfoList.total = count;
                outputChannelInfoList.list = new ArrayList<>();
                for (WFCMessage.ChannelInfo channelData : list) {
                    outputChannelInfoList.list.add(OutputGetChannelInfo.fromPbInfo(channelData));
                }
                sendResponse(response, ErrorCode.ERROR_CODE_SUCCESS, outputChannelInfoList);
            } else {
                sendResponse(response, ErrorCode.INVALID_PARAMETER, null);
            }
        }
        return true;
    }
}
