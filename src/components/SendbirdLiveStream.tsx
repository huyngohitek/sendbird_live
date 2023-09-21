/* eslint-disable @typescript-eslint/no-unused-vars */
// SendbirdLiveStream.tsx

import React, {useCallback, useEffect, useRef, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Pressable,
  requireNativeComponent,
  FlatList,
  SafeAreaView,
  Modal,
  TextInput,
  RefreshControl,
  DeviceEventEmitter,
  NativeEventEmitter,
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
const LIVE_EVENT_STATE = {
  CREATED: 'created',
  READY: 'ready',
  ONGOING: 'ongoing',
  ENDED: 'ended',
};

const apiGetLiveEvents = `https://api-${APP_ID}.calls.sendbird.com/v1/live-events?limit=100&state[]=ready&state[]=ongoing&state[]=created`;
const SendbirdLiveStream: React.FC = ({userId}) => {
  const apiGetToken = `https://api-${APP_ID}.sendbird.com/v3/users/${userId}/token`;
  const [liveEventId, setLiveEventId] = useState<string>('');
  const [liveEvents, setLiveEvents] = useState([]);
  const [liveEventTitle, setLiveEventTitle] = useState('Live Sample');
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const toggleModal = () => setModalVisible(!modalVisible);
  const liveEventsRef = useRef();
  liveEventsRef.current = liveEvents;
  const getLiveEvents = () => {
    setLoading(true);
    return fetch(apiGetLiveEvents, {
      method: 'GET',
      headers,
    })
      .then(response => response.json())
      .then(json => {
        const events = json.live_events.filter(item => {
          if (!item.user_ids_for_host.includes(userId)) {
            return item.state !== 'created' && item.state !== 'ended';
          } else {
            return item.state !== 'ended';
          }
        });
        // console.log('getLiveEvents', events);

        setLiveEvents(events);
      })
      .catch(error => {
        console.error(error);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const updateLiveState = ({id, state}) => {
    console.log('=====updateLiveState1======', id, state);
    const eventIndex = liveEventsRef.current.findIndex(
      event => event.live_event_id === id,
    );
    let updatedEvent = null;
    switch (state) {
      case LIVE_EVENT_STATE.READY:
        updatedEvent = {
          ...liveEventsRef.current[eventIndex],
          state: LIVE_EVENT_STATE.READY,
        };
        break;
      case LIVE_EVENT_STATE.ONGOING:
        updatedEvent = {
          ...liveEventsRef.current[eventIndex],
          state: LIVE_EVENT_STATE.ONGOING,
        };
        break;
      case LIVE_EVENT_STATE.ENDED:
        updatedEvent = {
          ...liveEventsRef.current[eventIndex],
          state: LIVE_EVENT_STATE.ENDED,
        };
        break;

      default:
        break;
    }
    const newLiveEvents = [...liveEventsRef.current];
    newLiveEvents[eventIndex] = updatedEvent;
    console.log('=====updateLiveState2======', newLiveEvents);
    setLiveEvents(newLiveEvents);
  };

  const updateLiveParticipantCount = ({id, participantCount}) => {
    console.log('=====updateLiveParticipantCount1======', id, participantCount);
    const eventIndex = liveEventsRef.current.findIndex(
      event => event.live_event_id === id,
    );

    const updatedEvent = {
      ...liveEventsRef.current[eventIndex],
      participant_count: participantCount,
    };
    const newLiveEvents = [...liveEventsRef.current];
    newLiveEvents[eventIndex] = updatedEvent;
    console.log('=====updateLiveParticipantCount2======', newLiveEvents);
    setLiveEvents(newLiveEvents);
  };

  useEffect(() => {
    const SendbirdLiveModuleEmitter = new NativeEventEmitter(
      SendbirdLiveModule,
    );
    let updateLiveStateSubscription = SendbirdLiveModuleEmitter.addListener(
      'updateLiveState',
      updateLiveState,
    );
    let updateLiveParticipantCountSubscription =
      SendbirdLiveModuleEmitter.addListener(
        'updateLiveParticipantCount',
        updateLiveParticipantCount,
      );
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
    return () => {
      updateLiveParticipantCountSubscription.remove();
      updateLiveStateSubscription.remove();
    };
  }, []);

  const createLiveEvents = () => {
    const userIds: string[] = [userId];
    SendbirdLiveModule.createLiveEvent(
      userIds,
      liveEventTitle,
      'https://subiz.com.vn/blog/wp-content/uploads/2023/01/subiz-livestream-thuc-day-su-phat-trien-cua-cac-san-thuong-mai-dien-tu.jpg',
      liveEventRes => {
        console.log('!!!============ live event created   ', liveEventRes);
        setLiveEventId(liveEventRes);
      },
      error => {
        console.log('!!!============ live event error    ', error);
      },
    );
    toggleModal();
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
        refreshControl={
          <RefreshControl refreshing={loading} onRefresh={getLiveEvents} />
        }
        data={liveEvents}
        extraData={liveEvents}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        {...{renderItem, keyExtractor}}
      />
      <Pressable hitSlop={20} style={styles.btnStart} onPress={toggleModal}>
        <Text style={styles.txtBtnStart}>Create</Text>
      </Pressable>
      <Modal
        visible={modalVisible}
        onRequestClose={toggleModal}
        transparent
        style={styles.modalContainer}>
        <View style={styles.centeredView}>
          <View style={styles.modalView}>
            <Text style={styles.modalText}>Please input Live Event title!</Text>
            {/* <View style={styles.txtLiveEventTitle}> */}
            <TextInput
              style={styles.inputLiveName}
              onChangeText={setLiveEventTitle}
            />
            {/* </View> */}
            <Pressable
              style={[styles.button, styles.buttonClose2]}
              onPress={createLiveEvents}>
              <Text style={styles.textStyle}>Create Live Event</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  inputLiveName: {
    width: 200,
    color: 'white',
    backgroundColor: 'blue',
    borderRadius: 10,
  },
  modalContainer: {flex: 1, padding: 40},
  txtLiveEventTitle: {
    borderRadius: 30,
    height: 60,
    paddingVertical: 20,
    backgroundColor: 'blue',
    width: '100%',
  },
  liveHostView: {...StyleSheet.absoluteFillObject},
  content: {flex: 1},
  btnStart: {
    position: 'absolute',
    minWidth: 80,
    height: 40,
    borderRadius: 20,
    backgroundColor: 'rgba(50, 168, 82, 0.5)',
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

  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalView: {
    // margin: 20,
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 50,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  button: {
    borderRadius: 20,
    padding: 10,
    elevation: 2,
  },
  buttonOpen: {
    backgroundColor: '#F194FF',
  },
  buttonClose: {
    backgroundColor: '#2196F3',
  },
  buttonClose2: {
    marginTop: 20,
    backgroundColor: '#2196F3',
  },
  textStyle: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  modalText: {
    marginBottom: 15,
    textAlign: 'center',
  },
});

export default SendbirdLiveStream;
