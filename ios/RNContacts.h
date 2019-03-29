
#import <React/RCTBridgeModule.h>
#import <Foundation/Foundation.h>
#import <ContactsUI/ContactsUI.h>
#import <UIKit/UIKit.h>

@interface RNContacts : NSObject <RCTBridgeModule, CNContactViewControllerDelegate>

@property (nonatomic, strong) CNContactPickerViewController *contactController;
@property (nonatomic, strong) RCTPromiseResolveBlock resolveCallback;
@property (nonatomic, strong) RCTPromiseRejectBlock rejectCallback;

@end
  
