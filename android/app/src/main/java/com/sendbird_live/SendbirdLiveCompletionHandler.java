
package com.sendbird_live;

import com.sendbird.android.exception.SendbirdException;
import com.sendbird.android.handler.CompletionHandler;

public interface SendbirdLiveCompletionHandler extends CompletionHandler {
    void onComplete(SendbirdException e);
}
