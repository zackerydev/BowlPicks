import React from 'react';
import { StyleSheet, View, ScrollView, KeyboardAvoidingView, Text, FlatList, ToastAndroid } from 'react-native';
import { Spinner, List, ListItem } from 'native-base';
import { ButtonGroup, Button } from 'react-native-elements'
import * as firebase from 'firebase';


export default class Home extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      loaded: false,
      selected: [],
      picks: []
    }
  }

  componentWillMount() {
    var self = this;
    let gamesRef = firebase.database().ref('games');
    gamesRef.on('value', (snapshot) => {
      let games = snapshot.val();
      let playersRef = firebase.database().ref('picks');
      playersRef.on('value', (snapshot) => {
        let players = snapshot.val();
        let picks = []
        let userPicks = []
        for(var i = 0; i < games.length; i++) {
          picks[i] = 0;
          userPicks[i] = games[i].favorite;
        }
        self.setState({games: games, loaded: true, selected: picks, picks: userPicks, players: players});
      })
    })
  }
  updateIndex = (idx, selected) => {
    let team;
    // console.log(idx)
    // console.log(selected)
    if(selected === 0) team = this.state.games[idx].favorite
    if(selected === 1) team = this.state.games[idx].underdog
    let user = this.state.picks;
    let select = this.state.selected;
    select[idx] = selected;
    user[idx] = team;
    this.setState({picks: user, selected: select});

    //console.log("Selected ", team)

  }
  submitPicks = () => {
    var self = this;
    let valid = true;
    if(this.state.picks.length !== this.state.games.length) {
      valid = false
      ToastAndroid.show("Make sure you have picked all games!", ToastAndroid.SHORT);
    } else {
      for(var i = 0; i < this.state.picks.length; i++) {
        if(typeof this.state.picks[i] === "undefined") {
          valid = false;
          console.log("At least one game is undefined!")
          ToastAndroid.show("At least one game is undefined!", ToastAndroid.SHORT);
        }
      }
    }
    if(valid) {
      var user = firebase.auth().currentUser;
      if(user) {
        var newPicks = this.state.players;
        newPicks.push({
          name: user.displayName,
          picks: this.state.picks,
          points: 0
        })
        firebase.database().ref('picks').set(newPicks, function(err) {
          if(err) {
            ToastAndroid.show("An error occurred updating the database", ToastAndroid.SHORT);
          } else {
            self.props.picked()
          }
        })
      } else {
        ToastAndroid.show("There is not a user currently logged in!", ToastAndroid.SHORT);
      }
    }

  }
  render() {
    let content = <View>
      <Spinner color="navy" />
    </View>;
    let loaded = this.state.loaded
    if(!!loaded) {
      const { games } = this.state;
      const pickList = games.map((val, idx) => {
        let buttons = []
        buttons.push(val.favorite + " " + val.favPoints)
        buttons.push(val.underdog + " " + val.undPoints)
        return(
          <ButtonGroup
            key={idx}
            onPress={(selected) => this.updateIndex(idx, selected)}
            selectedIndex={this.state.selected[idx]}
            buttons={buttons}
            containerStyle={{height: 50}}/>
        )
      })
      content = pickList
    }
    return (
      <ScrollView>
        {content}
        <Button
          large
          onPress={this.submitPicks}
          backgroundColor={"navy"}
          icon={{name: 'forward'}}
          title="Submit"/>
      </ScrollView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
});
