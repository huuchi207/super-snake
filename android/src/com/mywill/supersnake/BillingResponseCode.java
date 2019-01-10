package com.mywill.supersnake;


public enum BillingResponseCode {
  FEATURE_NOT_SUPPORTED(-2),
  SERVICE_DISCONNECTED(-1),
  OK(0),
  USER_CANCELED(1),
  SERVICE_UNAVAILABLE(2),
  BILLING_UNAVAILABLE(3),
  ITEM_UNAVAILABLE(4),
  DEVELOPER_ERROR(5),
  ERROR(6),
  ITEM_ALREADY_OWNED(7);


  private int value;

  BillingResponseCode(int value) {
    this.value = value;
  }

  public static BillingResponseCode valueOf(int value){
    for (BillingResponseCode code : values()){
      if (code.value== value){
        return code;
      }
    }
    return null;
  }
  @Override
  public String toString() {
    switch (value) {
      case -2:
        return "Requested feature is not supported by Play Store on the current device.";
      case -1:
        return "Play Store service is not connected now - potentially transient state.";
      case 0:
        return "OK";
      case 1:
        return "User pressed back or canceled a dialog.";
      case 2:
        return "Network connection is down";
      case 3:
        return "Billing API version is not supported for the type requested";
      case 4:
        return "Requested product is not available for purchase";
      case 5:
        return "Invalid arguments provided to the API. This error can also indicate that the application was\n" +
            "   * not correctly signed or properly set up for In-app Billing in Google Play, or does not have\n" +
            "   * the necessary permissions in its manifest";
      case 6:
        return "Fatal error during the API action";
      case 7:
        return "Failure to purchase since item is already owned";
      case 8:
        return "Failure to consume since item is not owned";
      default:
        return "";
    }
  }
}
