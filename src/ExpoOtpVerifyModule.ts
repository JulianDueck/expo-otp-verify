import { NativeModule, requireNativeModule } from "expo";

declare class ExpoOtpVerifyModule extends NativeModule {
  getHash: () => string[];
  getOtp: () => Promise<boolean>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoOtpVerifyModule>("ExpoOtpVerify");
