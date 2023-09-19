/* eslint-disable @typescript-eslint/no-unused-vars */
// SendbirdLiveStream.tsx

import React, {useCallback, useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Pressable,
  requireNativeComponent,
  FlatList,
  SafeAreaView,
} from 'react-native';
import {NativeModules} from 'react-native';
import LiveEvent from './LiveEvent';

const {SendbirdLiveModule} = NativeModules;
// const LiveHostView = requireNativeComponent('LiveHostView');
const user2 = 'huyngohitek';
const APP_ID = '5E720D37-FB7D-41F3-924A-6246070BD1F8';
const API_TOKEN = '366ab9eadb2dd21652a74ecc1dded699c26dc8e6';
const headers = {
  'Content-Type': 'application/json',
  'Api-Token': API_TOKEN,
};
const apiGetLiveEvents = `https://api-${APP_ID}.calls.sendbird.com/v1/live-events?limit=100&state[]=ready&state[]=ongoing&state[]=created`;
const SendbirdLiveStream: React.FC = ({userId}) => {
  const apiGetToken = `https://api-${APP_ID}.sendbird.com/v3/users/${userId}/token`;
  const [liveEventId, setLiveEventId] = useState<string>('');
  const [liveEvents, setLiveEvents] = useState([]);

  const getLiveEvents = () => {
    return fetch(apiGetLiveEvents, {
      method: 'GET',
      headers,
    })
      .then(response => response.json())
      .then(json => {
        setLiveEvents(json.live_events);
      })
      .catch(error => {
        console.error(error);
      });
  };
  useEffect(() => {
    console.log('!!!===== useEffect triggered  ');
    const get_access_token = async () => {
      console.log('!!!======== get_access_token started    ');
      const expires_at_ts = Date.now();
      const expires_at_ts_7days = expires_at_ts + 7 * 24 * 60 * 60 * 1000;
      const sendBirdResponse = await fetch(apiGetToken, {
        method: 'POST',
        headers,
        body: JSON.stringify({
          expires_at: expires_at_ts_7days,
        }),
      });
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
      // SendbirdLiveModule.initializeSDK(
      //   '5E720D37-FB7D-41F3-924A-6246070BD1F8',
      //   userId,
      //   sendBirdResponseJson.token,
      //   onInitSuccess,
      //   onInitError,
      // );
      console.log('!!!=========== before .authenticate  ');
      const onAuthenticateSuccess = message => {
        console.log('!!!======= authentication success   ', message);
        getLiveEvents();
      };
      const onAuthenticateError = error => {
        console.error('!!!========= authentication failed  ', error);
      };
      SendbirdLiveModule.authenticate(
        userId,
        sendBirdResponseJson.token,
        onAuthenticateSuccess,
        onAuthenticateError,
      );
    };
    get_access_token();
  }, []);

  const createLiveEvents = () => {
    const userIds: string[] = [userId];
    SendbirdLiveModule.createLiveEvent(
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
  const startLiveEvent = () => {};

  const endLiveEvent = () => {
    console.log('=========on end press========');
    const onEndLiveEventSuccess = message => {
      console.log('!!!================  live event ended    ', message); // "successfully ended live event"
      setLiveEventId('');
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
  const keyExtractor = useCallback(
    (item, index) => `${item.live_event_id} + ${index}`,
    [],
  );

  const onLiveEventPress = (liveId, isHost) => {
    SendbirdLiveModule.enterLiveEvent(liveId, isHost);
  };

  const renderItem = ({item, index}) => {
    return (
      <LiveEvent
        {...{item, onLiveEventPress}}
        isHost={userId === item.user_ids_for_host[0]}
      />
    );
  };
  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        data={liveEvents}
        extraData={liveEvents}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        {...{renderItem, keyExtractor}}
      />
      <Pressable
        hitSlop={20}
        style={styles.btnStart}
        onPress={createLiveEvents}>
        <Text style={styles.txtBtnStart}>Create</Text>
      </Pressable>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  liveHostView: {...StyleSheet.absoluteFillObject},
  content: {flex: 1},
  btnStart: {
    position: 'absolute',
    minWidth: 80,
    height: 40,
    borderRadius: 20,
    backgroundColor: 'green',
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    end: 16,
    top: 16,
  },
  txtBtnStart: {color: 'white'},
  btnContainer: {
    position: 'absolute',
    width: 60,
    height: 60,
    top: 20,
    start: 20,
    backgroundColor: 'red',
    padding: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },
  container: {
    flex: 1,
  },
  title: {
    fontSize: 20,
  },
  separator: {height: 5},
});

export default SendbirdLiveStream;
