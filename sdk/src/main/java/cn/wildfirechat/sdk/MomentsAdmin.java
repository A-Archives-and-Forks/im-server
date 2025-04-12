package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.pojos.moments.FeedPojo;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;


public class MomentsAdmin {
    public static IMResult<SendMessageResult> postFeeds(FeedPojo feedPojo) throws Exception {
        String path = APIPath.Admin_Moments_Post_Feed;
        return AdminHttpUtils.httpJsonPost(path, feedPojo, SendMessageResult.class);
    }
}
