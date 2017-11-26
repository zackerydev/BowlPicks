import React from 'react';
import firebase from 'react-native-firebase';
import { Container, Header, Content, Button, Text,  Left, Body, 
  Right, Icon, Title, Form, Item, Label, Input, Root, Toast} from 'native-base';
import { StyleSheet } from 'react-native';

/* Main Navy: #252839 Main Yellow #f2b632 */
export default class App extends React.Component {
  constructor() {
    super();
    this.state = {
      display: "landing",
      email: "",
      password: "",
      passwordConfirm: "",
      displayName: "",
      user: {},
      message: "",
      toast: false
    }
    this.unsubscriber = null
    this.validateLogin = this.validateLogin.bind(this);
  }
  componentWillMount() {
    this.unsubscriber = firebase.auth().onAuthStateChanged((user) => {
      console.log("USER", user)
      console.log(typeof user)
      if(user !== null) {
        this.setState({user: user, display: "main"})        
      }
    })
  }
  validateLogin = (event) => {
    var self = this;
    console.log("Validating Login...")
    firebase.auth().signInWithEmailAndPassword(this.state.email, this.state.password).catch(function(error) {
      var code = error.code
      var message = error.message;
      console.log("Error", code, message)
      Toast.show({
        text: message,
        position: 'bottom',
        buttonText: 'Got It'
      })
    })
  }
  validateSignUp = (event) => {
    var self = this;
    console.log("Validating Login...")
    if(this.state.password !== this.state.passwordConfirm) {
      Toast.show({
        text: 'Passwords do not match!',
        position: 'bottom',
        buttonText: 'Got It'
      })
    } else {
      firebase.auth().createUserWithEmailAndPassword(this.state.email, this.state.password).catch(function(error) {
        var code = error.code
        var message = error.message;
        console.log("Error", code, message)
        Toast.show({
          text: message,
          position: 'bottom',
          buttonText: 'Got It'
        })
      })
    }
  }
  render() {
    console.log(this.state)
    var display;
    if(this.state.display === "landing") {
      display = <Content contentContainerStyle={styles.container}>
                  <Button onPress={() => this.setState({display: 'login'})} style={styles.button} large full>
                    <Text>Log In</Text>
                  </Button>
                  <Button onPress={() => this.setState({display: 'signup'})}style={styles.button} large block>
                    <Text>Sign Up</Text>
                  </Button>
                </Content>
    } else if(this.state.display === "login") {
      display = <Content> 
                  <Form>
                  <Item floatingLabel>
                    <Label>Email</Label>
                    <Input onChangeText={(email) => this.setState({email})}/>
                  </Item>
                  <Item floatingLabel last>
                    <Label>Password</Label>
                    <Input secureTextEntry={true} onChangeText={(password) => this.setState({password})} />
                  </Item>
                  <Button onPress={this.validateLogin} style={styles.button} large block>
                          <Text>Log In</Text>
                    </Button>
                </Form>
              </Content>
    } else if(this.state.display === "signup") {
      display = <Content> 
                <Form>
                <Item floatingLabel>
                  <Label>Email</Label>
                  <Input  onChangeText={(email) => this.setState({email})}/>
                </Item>
                <Item floatingLabel last>
                  <Label>Password</Label>
                  <Input secureTextEntry={true} onChangeText={(password) => this.setState({password})} />
                </Item>
                <Item floatingLabel last>
                  <Label>Confirm Password</Label>
                  <Input secureTextEntry={true} onChangeText={(passwordConfirm) => this.setState({passwordConfirm})} />
                </Item>
                <Item floatingLabel last>
                  <Label>Display Name</Label>
                  <Input  onChangeText={(displayName) => this.setState({displayName})} />
                </Item>
                <Button onPress={this.validateSignUp} style={styles.button} large block>
                        <Text>Sign Up</Text>
                  </Button>
              </Form>
            </Content>
    } else if(this.state.display === "main") {
      //display = <Content> <Text> Test </Text> </Content>
    }
    return (
      <Root>
        <Container>
          <Header androidStatusBarColor="#252839" backgroundColor="#252839">
          <Left>
              <Button onPress={() => this.setState({display: 'landing'})} transparent>
                <Icon name='arrow-back' />
              </Button>
            </Left>
            <Body>
              <Title>Bowl Picks</Title>
            </Body>
          </Header>
          
          
          {display}
        </Container>
      </Root>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    padding: 50,
    marginTop: 50,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  }, button: {
    margin: 20
  }
});