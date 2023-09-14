/* eslint-disable @typescript-eslint/no-unused-vars */
// SendbirdLiveStream.tsx

import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import {NativeModules} from 'react-native';

const {SendbirdLiveModule} = NativeModules;

const user_id = '2U6BgR5PKxsyPHmiIwDvOxS2eso';
const appId = '5E720D37-FB7D-41F3-924A-6246070BD1F8';
const SendbirdLiveStream: React.FC = () => {
  const [liveEventId, setLiveEventId] = useState<string>();
  useEffect(() => {
    console.log('!!!===== useEffect triggered  ');
    const get_access_token = async () => {
      console.log('!!!======== get_access_token started    ');
      const expires_at_ts = Date.now();
      const expires_at_ts_7days = expires_at_ts + 7 * 24 * 60 * 60 * 1000;
      const sendBirdResponse = await fetch(
        `https://api-5E720D37-FB7D-41F3-924A-6246070BD1F8.sendbird.com/v3/users/${user_id}/token`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Api-Token': '366ab9eadb2dd21652a74ecc1dded699c26dc8e6',
          },
          body: JSON.stringify({
            expires_at: expires_at_ts_7days,
          }),
        },
      );
      const sendBirdResponseJson = await sendBirdResponse.json();
      console.log('!!!!!=========== tokens_json   ', sendBirdResponseJson);
      // SendbirdLiveModule.initializeSDK('5E720D37-FB7D-41F3-924A-6246070BD1F8');
      // SendbirdLiveModule.authenticate(
      // )

      const onInitSuccess = () => message => {
        console.log('!!!======== SDK init success    ', message);
      };
      const onInitError = error => {
        console.error('!!!==========  SDK init failed   ', error);
      };
      SendbirdLiveModule.initializeSDK(
        '5E720D37-FB7D-41F3-924A-6246070BD1F8',
        user_id,
        sendBirdResponseJson.token,
        onInitSuccess,
        onInitError,
      );
      console.log('!!!=========== before .authenticate  ');
      const onAuthenticateSuccess = message => {
        console.log('!!!======= authentication success   ', message);
        startLiveEvent();
      };
      const onAuthenticateError = error => {
        console.error('!!!========= authentication failed  ', error);
      };
      SendbirdLiveModule.authenticate(
        user_id,
        sendBirdResponseJson.token,
        onAuthenticateSuccess,
        onAuthenticateError,
      );
    };
    get_access_token();
  }, []);

  const startLiveEvent = () => {
    const userIds: string[] = [user_id];
    SendbirdLiveModule.startLiveEvent(
      userIds,
      'LiveStreaming Sample 1',
      'https://subiz.com.vn/blog/wp-content/uploads/2023/01/subiz-livestream-thuc-day-su-phat-trien-cua-cac-san-thuong-mai-dien-tu.jpg',
      liveEventRes => {
        console.log('!!!============ live event created   ', liveEventRes);
        setLiveEventId(liveEventRes);
      },
      error => {
        console.log('!!!============ live event error    ', error);
      },
    );
  };

  const endLiveEvent = () => {
    const onEndLiveEventSuccess = message => {
      console.log('!!!================  live event ended    ', message); // "successfully ended live event"
    };
    const onEndLiveEventError = error => {
      console.error('!!!===============  live event ending failed    ', error);
    };
    SendbirdLiveModule.endLiveEvent(
      liveEventId,
      onEndLiveEventSuccess,
      onEndLiveEventError,
    );
  };
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Sendbird Live Streaming Placeholder</Text>
      <Pressable
        hitSlop={20}
        style={styles.btnContainer}
        onPress={endLiveEvent}>
        <Text>X</Text>
      </Pressable>
    </View>
  );
};

const styles = StyleSheet.create({
  content: {flex: 1},
  btnContainer: {
    position: 'absolute',
    width: 60,
    height: 60,
    top: 20,
    start: 20,
    backgroundColor: 'red',
    padding: 20,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 20,
  },
});

export default SendbirdLiveStream;
