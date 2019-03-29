
# react-native-contacts

## Getting started

`$ npm install react-native-contacts --save`

### Mostly automatic installation

`$ react-native link react-native-contacts`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-contacts` and add `RNContacts.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNContacts.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.sunxingzhe.contacts.RNContactsPackage;` to the imports at the top of the file
  - Add `new RNContactsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-contacts'
  	project(':react-native-contacts').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-contacts/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-contacts')
  	```


## Usage
```javascript
import RNContacts from 'react-native-contacts';

// TODO: What to do with the module?
RNContacts;
```
  