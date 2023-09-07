/* eslint-disable @typescript-eslint/no-unused-vars */
// SendbirdLiveStream.tsx

import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {NativeModules} from 'react-native';

const {SendbirdLiveModule} = NativeModules;

const user_id = '2U6BgR5PKxsyPHmiIwDvOxS2eso';

const SendbirdLiveStream: React.FC = () => {
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
      SendbirdLiveModule.initializeSDK(
        '5E720D37-FB7D-41F3-924A-6246070BD1F8',
        user_id,
        sendBirdResponseJson.token,
        message => {
          console.log('!!!======== SDK init success    ', message);
        },
        error => {
          console.error('!!!==========  SDK init failed   ', error);
        },
      );
      console.log('!!!=========== before .authenticate  ');

      SendbirdLiveModule.authenticate(
        user_id,
        sendBirdResponseJson.token,
        message => {
          console.log('!!!======= authentication success   ', message);
        },
        error => {
          console.error('!!!========= authentication failed  ', error);
        },
      );

      // const userIds: string[] = [user_id];
      // SendbirdLiveModule.startLiveEvent(
      //   userIds,
      //   'liveStreaming1',
      //   '',
      //   message => {
      //     console.log('!!!============ live event created   ', message);
      //   },
      //   error => {
      //     console.log('!!!============ live event error    ', error);
      //   },
      // );

      SendbirdLiveModule.endLiveEvent(
        '33428961-8eaa-4742-b8ac-27bb4cf07e7c',
        message => {
          console.log("!!!================  live event ended    ", message); // "successfully ended live event"
        },
        error => {
          console.error("!!!===============  live event ending failed    ", error);
        },
      );
    };
    get_access_token();
  }, []);
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Sendbird Live Streaming Placeholder</Text>
    </View>
  );
};

const styles = StyleSheet.create({
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
