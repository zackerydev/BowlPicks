import React from 'react';
import { StyleSheet, Text, View, KeyboardAvoidingView } from 'react-native';
import { ButtonGroup, FormLabel, FormInput, Button, FormValidationMessage, Header, Icon } from 'react-native-elements';
import * as firebase from 'firebase';
import Landing from "./Landing.js"
import Home from "./Home.js"
import Dashboard from "./Dashboard.js"
const config = {
    apiKey: "AIzaSyDiqsu5rRJRhMD-6Z1ZyIjCd-uXsvQ_q9M",
    authDomain: "bowlpicks-b3688.firebaseapp.com",
    databaseURL: "https://bowlpicks-b3688.firebaseio.com",
    storageBucket: "bowlpicks-b3688.appspot.com",
  };

if(!firebase.apps.length) {
  const fb = firebase.initializeApp(config);

}


export default class App extends React.Component {
  constructor() {
    super()
    this.state = {
      logged: false,
      name: ""
    }
  }

  async componentWillMount() {
    let playersRef = firebase.database().ref('picks');
    playersRef.on('value', (snapshot) => {
      let players = snapshot.val();
      var user = firebase.auth().currentUser;
      if(user) {
        for(var i = 0; i < players.length; i++) {
          // console.log(user.displayName)
          // console.log(players)
          // console.log(players[i].name === user.diplayName)
          if(user.displayName === players[i].name) {
            this.setState({picked: true})
          }
        }
      }
    })
    firebase.auth().onAuthStateChanged((user) => {
      if(user) {
        if(this.state.name !== "") {
          user.updateProfile({
            displayName: this.state.name
          }).then(this.setState({logged: true}))
        } else {
          this.setState({logged: true});
        }
      }
    })
    await Expo.Font.loadAsync({
    'Roboto': require('native-base/Fonts/Roboto.ttf'),
    'Roboto_medium': require('native-base/Fonts/Roboto_medium.ttf'),
  });
  }
  getName = (name) => {
    this.setState({name: name})
  }
  signOut = () => {
    firebase.auth().signOut().then(() => {
      this.setState({logged: false, picked: false, name: ""})
    })
  }

  picked = () => {
    console.log("KELLY PICKLER")
    this.setState({picked: true})
  }
  render() {
    let content;
    if(this.state.logged && this.state.picked) {
      content = <Dashboard />
    } else if(this.state.logged) {
      content = <Home picked={this.picked.bind(this)}/>
    } else {
      content = <Landing send={this.getName} />
    }

    return (
      <View style={styles.container}>
        <Header
          leftComponent={<Icon name="arrow-back" color="white" onPress={this.signOut}/>}
          centerComponent={{text: 'Bowl Picks', style: {color: '#FFF'}}}
          backgroundColor='navy'
          outerContainerStyles={{height: 100}}
        />
          {content}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  }
});
