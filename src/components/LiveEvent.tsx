import {Image, Pressable, StyleSheet, Text, View} from 'react-native';
import React, {memo} from 'react';
interface ILiveEvent {
  item: any;
  isHost: boolean;
  onLiveEventPress: (id) => void;
}
const LiveEvent = (props: ILiveEvent) => {
  const {
    onLiveEventPress,
    isHost,
    item: {
      is_host_streaming: isHostStreaming,
      cumulative_participant_count,
      cover_url: coverUrl,
      participant_count: participantCount,
      user_ids_for_host,
      title,
      duration,
      live_event_id: liveEventId,
    },
  } = props;
  console.log('----Live Event ====', props.item);
  return (
    <Pressable
      onPress={() => onLiveEventPress?.(liveEventId, isHost)}
      style={styles.container}>
      <Image
        source={{
          uri:
            coverUrl ??
            'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSP27DKLmdsHhbBY5hvSsXUOMudfPJpoYjD5g&usqp=CAU',
        }}
        style={styles.coverPhoto}
      />
      <View style={styles.content}>
        <View>
          <Text>{title}</Text>
        </View>
        <Text>{isHostStreaming ? 'Live' : 'Upcoming'}</Text>
        <Text>{participantCount} people joined</Text>
        <Text>
          {user_ids_for_host[0] !== 'none' ? 'Have host' : 'None host'}
        </Text>
        <Text>{isHost ? 'Is host' : 'not host'}</Text>
      </View>
    </Pressable>
  );
};

export default memo(LiveEvent);

const styles = StyleSheet.create({
  content: {marginStart: 5},
  coverPhoto: {height: 100, aspectRatio: 16 / 9},
  container: {alignItems: 'center', flexDirection: 'row', height: 100},
});
