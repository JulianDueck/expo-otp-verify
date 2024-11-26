// Reexport the native module. On web, it will be resolved to ExpoOtpVerifyModule.web.ts
// and on native platforms to ExpoOtpVerifyModule.ts
import { EventEmitter } from "expo";
import { EventSubscription } from "expo-modules-core";
import { Platform } from "react-native";

import ExpoOtpVerifyModule from "./ExpoOtpVerifyModule";

export async function getOtp(): Promise<boolean> {
  if (Platform.OS === "ios") {
    console.warn("Not Supported on iOS");
    return Promise.resolve(false);
  }
  return await ExpoOtpVerifyModule.getOtp();
}

export function getHash(): string[] {
  if (Platform.OS === "ios") {
    console.warn("Not Supported on iOS");
    return [];
  }
  return ExpoOtpVerifyModule.getHash();
}

export function addOtpListener(
  listener: (value: string) => any,
): EventSubscription {
  return ExpoOtpVerifyModule.addListener("onOtpReceived", listener);
}
