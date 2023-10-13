/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, {useState} from 'react';

import SendbirdLiveStream from './src/components/SendbirdLiveStream';
import {Modal, Pressable, StyleSheet, Text, View} from 'react-native';

const user2 = 'huyngohitek2';
const user1 = 'huyngohitek3';
const App: React.FC = () => {
  const [user, setUser] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const toggleModal = () => setModalVisible(!modalVisible);
  return !user ? (
    <View style={styles.centeredView}>
      <Modal visible={modalVisible || user} onRequestClose={toggleModal}>
        <View style={styles.centeredView}>
          <View style={styles.modalView}>
            <Text style={styles.modalText}>Please choose user to start!</Text>
            <Pressable
              style={[styles.button, styles.buttonClose]}
              onPress={() => {
                setUser(user1);
              }}>
              <Text style={styles.textStyle}>User 1</Text>
            </Pressable>
            <Pressable
              style={[styles.button, styles.buttonClose2]}
              onPress={() => {
                setUser(user2);
              }}>
              <Text style={styles.textStyle}>User 2</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
      <Pressable
        style={[styles.button, styles.buttonOpen]}
        onPress={toggleModal}>
        <Text style={styles.textStyle}>Choose User</Text>
      </Pressable>
    </View>
  ) : (
    <SendbirdLiveStream userId={user} />
  );
};

const styles = StyleSheet.create({
  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalView: {
    margin: 20,
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 35,
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
export default App;
