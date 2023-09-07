
package com.sendbird_live;

import com.sendbird.android.exception.SendbirdException;

public interface SendbirdLiveCompletionHandler {
    void onComplete(SendbirdException e);
}
