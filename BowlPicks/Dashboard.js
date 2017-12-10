import React from 'react';
import { StyleSheet, View, ScrollView, KeyboardAvoidingView, FlatList } from 'react-native';
import { Spinner, List, ListItem, Text, Left, Body, Right, Content, Container } from 'native-base';
import { ButtonGroup, Button } from 'react-native-elements'
import * as firebase from 'firebase';

function compare(a,b) {
  if(a.points > b.points) {
    return -1
  } else if(a.points < b.points) {
    return 1
  } else {
    return 0
  }
}

export default class Home extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      loaded: false,
      selected: [],
      picks: [],
      players: [{name: "Loading...", points: 0}]
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
        let winnersRef = firebase.database().ref('winners');
        winnersRef.on('value', (snapshot) => {
          let winners = snapshot.val();
          for(var i = 0; i < winners.length; i++) {
            for(var j = 0; j < players.length; j++) {
              if(players[j].picks[i] === winners[i]) {
                if(winners[i] === games[i].favorite) {
                  //console.log(players[j].points)
                  players[j].points = parseInt(players[j].points) + parseInt(games[i].favPoints);
                } else {
                  players[j].points = parseInt(players[j].points) + parseInt(games[i].undPoints);
                }

              }
            }
          }
          players = players.sort(compare);
          self.setState({games: games, loaded: true, players: players});
        })

      })
    })
  }
  render() {
    let content = <ListItem>
      <Text> Loading... </Text>
    </ListItem>;
    let loaded = this.state.loaded
    //console.log(this.state.players)
    if(loaded) {
      const players = this.state.players.map((val, idx) => {
        //console.log(val.name)
        return(
          <ListItem key={idx}>
            <Left><Text>{idx + 1}.</Text></Left>
            <Body><Text>{val.name}</Text></Body>
            <Right><Text>{val.points}</Text></Right>
          </ListItem>
        )
      })
      content = players
    }
    return (
      <ScrollView>
        <Content>
          <List>
            <ListItem itemHeader first>
              <Left><Text>Place</Text></Left>
              <Body><Text>Name</Text></Body>
              <Right><Text>Points</Text></Right>
            </ListItem>
            {content}
          </List>
        </Content>

      </ScrollView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
});
