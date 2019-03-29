
#import <React/RCTUtils.h>
#import "RNContacts.h"

@implementation RNContacts

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(authorization:(RCTResponseSenderBlock)callback)
{
  self.callback = callback;
  CNAuthorizationStatus status = [CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts];
  if (status == CNAuthorizationStatusNotDetermined) {
    CNContactStore *store = [[CNContactStore alloc] init];
    [store requestAccessForEntityType:CNEntityTypeContacts completionHandler:^(BOOL granted, NSError * _Nullable error) {
      if (error) {
        self.callback(@[@{@"authorization": @NO}]);
      } else {
        self.callback(@[@{@"authorization": @YES}]);
      }
    }];
  } else if (status == CNAuthorizationStatusAuthorized) {
    self.callback(@[@{@"authorization": @YES}]);
  } else {
    self.callback(@[@{@"authorization": @NO}]);
  }
}

RCT_EXPORT_METHOD(launchContact:(NSDictionary *)options callback:(RCTResponseSenderBlock)callback)
{
  self.callback = callback;
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
  NSDictionary *userInfo = @{@"givenName":contact.givenName,@"phoneNumber":phoneNumber.stringValue};
  self.callback(@[userInfo]);
}

@end
  