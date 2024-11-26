import { EventSubscription } from "expo-modules-core";
import { useEffect, useState } from "react";

import { ExpoOtpChangeEvent } from "./ExpoOtpVerifyModule.android";

const isDev =
  typeof __DEV__ !== "undefined"
    ? __DEV__
    : typeof process !== "undefined" && process.env.NODE_ENV !== "production";

async function getOtp(): Promise<boolean> {
  if (isDev) {
    console.warn("expo-otp-verify not supported on iOS");
  }
  return false;
}

function getHash(): string[] {
  if (isDev) {
    console.warn("expo-otp-verify not supported on iOS");
  }
  return [];
}

async function startOtpListener(
  listener: (value: ExpoOtpChangeEvent) => void,
): Promise<EventSubscription | null> {
  if (isDev) {
    console.warn("expo-otp-verify not supported on iOS");
  }
  return null;
}

function removeListener(): void {
  if (isDev) {
    console.warn("expo-otp-verify not supported on iOS");
  }
}

const useOtpVerify = ({ numberOfDigits } = { numberOfDigits: 0 }) => {
  const [message, setMessage] = useState<string | null>(null);
  const [otp, setOtp] = useState<string | null>(null);
  const [timeoutError, setTimeoutError] = useState<boolean>(false);
  const [hash, setHash] = useState<string[] | null>([]);

  useEffect(() => {
    console.warn("expo-otp-verify not supported on iOS");
  }, []);

  const startListener = () => {
    if (isDev) {
      console.warn("expo-otp-verify not supported on iOS");
    }
  };

  const stopListener = () => {
    if (isDev) {
      console.warn("expo-otp-verify not supported on iOS");
    }
  };

  return { otp, message, hash, timeoutError, stopListener, startListener };
};

export { getHash, startOtpListener, removeListener, useOtpVerify };
