
#import <React/RCTUtils.h>
#import "RNContacts.h"

@implementation RNContacts

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(authorization:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
  self.resolveCallback = resolve;
  self.rejectCallback = reject;
  CNAuthorizationStatus status = [CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts];
  if (status == CNAuthorizationStatusNotDetermined) {
    CNContactStore *store = [[CNContactStore alloc] init];
    [store requestAccessForEntityType:CNEntityTypeContacts completionHandler:^(BOOL granted, NSError * _Nullable error) {
      if (granted) {
        self.resolveCallback(@YES);
      } else {
        self.rejectCallback(@"", @"Unauthorized Access to Address Book.", error);
      }
    }];
  } else if (status == CNAuthorizationStatusRestricted) {
      self.rejectCallback(@"CNAuthorizationStatusRestricted", @"Users cannot change the status of this application.", nil);
  } else if (status == CNAuthorizationStatusDenied) {
      self.rejectCallback(@"CNAuthorizationStatusDenied", @"Users explicitly refuse access to the application's contact data.", nil);
  } else if (status == CNAuthorizationStatusAuthorized) {
    self.resolveCallback(@YES);
  } else {
    self.rejectCallback(@"unknown error", @"unknown error", nil);
  }
}

RCT_EXPORT_METHOD(openSettings:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    self.resolveCallback = resolve;
    self.rejectCallback = reject;
    NSURL *settingUrl = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
    BOOL canOpen = [[UIApplication sharedApplication] canOpenURL:settingUrl];
    CGFloat systemVersion = [[[UIDevice currentDevice] systemVersion] floatValue];
    if (canOpen) {
        if (systemVersion >= 8.0 && systemVersion < 10.0) {
            [[UIApplication sharedApplication] openURL:settingUrl];
            self.resolveCallback(@YES);
        } else if (systemVersion >= 10.0) {
            [[UIApplication sharedApplication] openURL:settingUrl options:@{} completionHandler:^(BOOL success) {
                if (success) {
                    self.resolveCallback(@YES);
                } else {
                    self.rejectCallback(@"can not open url", @"can not open url", nil);
                }
            }];
        }
    } else {
        self.rejectCallback(@"can not open url", @"can not open url", nil);
    }
}


RCT_EXPORT_METHOD(launchContact:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
  self.resolveCallback = resolve;
  self.rejectCallback = reject;
  dispatch_async(dispatch_get_main_queue(), ^{
    UIViewController *root = RCTPresentedViewController();
    self.contactController = [[CNContactPickerViewController alloc] init];
    self.contactController.delegate = self;
    self.contactController.displayedPropertyKeys = @[CNContactPhoneNumbersKey];
    [root presentViewController:self.contactController animated:YES completion:nil];
  });
}

- (void)contactPicker:(CNContactPickerViewController *)picker didSelectContactProperty:(CNContactProperty *)contactProperty {
  CNContact *contact = contactProperty.contact;
  CNPhoneNumber *phoneNumber = contactProperty.value;
  NSDictionary *userInfo = @{@"givenName":contact.givenName,@"familyName": contact.familyName,@"phoneNumber":phoneNumber.stringValue};
  self.resolveCallback(userInfo);
}

@end
  
