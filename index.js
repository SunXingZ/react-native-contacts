
import { NativeModules, Platform } from 'react-native';

const { RNContacts } = NativeModules;

const Contacts = RNContacts;

Contacts.authorization = async () => {
  if (Platform.OS == "ios") {
    return new Promise((resolve, reject) => {
      RNContacts.authorization(resolve);
    });
  } else {
    return await RNContacts.authorization();
  }
}

Contacts.launchContact = async (options = {}) => {
  if (Platform.OS == "ios") {
    return new Promise((resolve, reject) => {
      RNContacts.launchContact(options, resolve);
    });
  } else {
    return await RNContacts.launchContact(options);
  }
}

export default Contacts;
