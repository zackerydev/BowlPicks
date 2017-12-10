import React from 'react';
import { StyleSheet, Text, View, KeyboardAvoidingView } from 'react-native';
import { ButtonGroup, FormLabel, FormInput, Button, FormValidationMessage, Header, Icon } from 'react-native-elements';
import * as firebase from 'firebase';


export default class Landing extends React.Component {
  constructor() {
    super()
    this.state = {
      selected: 0,
      password: "",
      confirm: "",
      name: "",
      email: ""
    }
    this.updateIndex = this.updateIndex.bind(this)
    this.handleSignup = this.handleSignup.bind(this)
  }

  updateIndex(selected) {
    this.setState({selected});
  }
  handleSignup = () => {
    var self = this;
    this.props.send(this.state.name)
    if(this.state.password === this.state.confirm) {
      firebase.auth().createUserWithEmailAndPassword(this.state.email, this.state.password).catch((err) => {
        var errorCode = err.code;
        self.email.shake()
        self.setState({emError: err.message});
      })
    } else {
      this.confirm.shake()
      this.password.shake()
      this.setState({confError: "Passwords do not match"})
    }
  }
  handleBack = () => {
    firebase.auth().signOut().then(() => {
      this.setState({logged: false, selected: 0, user: null});
    })
  }
  handleLogin = () => {
    var self = this;
    firebase.auth().signInWithEmailAndPassword(this.state.email, this.state.password).catch((err) => {
      var errorCode = err.code;
      self.email.shake()
      self.setState({emError: err.message});
    })
  }
  render() {
    const buttons = ['Sign Up', 'Login']
    const selected = this.state.selected
    let content;
    let nav;

    if(selected === 0) {
      content = <View>
        <FormLabel>Email</FormLabel>
        <FormInput ref={email => this.email = email} onChangeText={(email) => this.setState({email})}/>
        <FormValidationMessage>{this.state.emError ? this.state.emError : ""}</FormValidationMessage>
        <FormLabel>Password</FormLabel>
        <FormInput secureTextEntry ref={password => this.password = password} onChangeText={(password) => this.setState({password})}/>
        <FormValidationMessage>{this.state.confError ? this.state.confError : ""}</FormValidationMessage>
        <FormLabel>Confirm Password</FormLabel>
        <FormInput secureTextEntry ref={confirm => this.confirm = confirm} onChangeText={(confirm) => this.setState({confirm})}/>
        <FormValidationMessage>{this.state.confError ? this.state.confError : ""}</FormValidationMessage>
        <FormLabel>Display Name</FormLabel>
        <FormInput ref={name => this.name = name} onChangeText={(name) => this.setState({name})}/>
        <Button
          large
          onPress={this.handleSignup.bind(this)}
          backgroundColor={"navy"}
          icon={{name: 'forward'}}
          title="Sign Up"/>
      </View>
    } else if(selected === 1) {
      content = <View>
        <FormLabel>Email</FormLabel>
        <FormInput ref={email => this.email = email} onChangeText={(email) => this.setState({email})}/>
        <FormValidationMessage>{this.state.emError ? this.state.emError : ""}</FormValidationMessage>
        <FormLabel>Password</FormLabel>
        <FormInput secureTextEntry ref={password => this.password = password} onChangeText={(password) => this.setState({password})}/>
        <Button
          large
          onPress={this.handleLogin.bind(this)}
          backgroundColor={"navy"}
          icon={{name: 'forward'}}
          title="Login"/>
      </View>
    }
    return (
      <KeyboardAvoidingView behavior='padding' contentContainerStyle={styles.container}>
        <ButtonGroup
          onPress={this.updateIndex}
          selectedIndex={selected}
          buttons={buttons}
          containerStyle={{height: 100}} />
        {content}
      </KeyboardAvoidingView>

    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
