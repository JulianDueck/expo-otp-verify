import { NativeModule, requireNativeModule } from "expo";
import { EventSubscription } from "expo-modules-core";
import { useEffect, useState } from "react";

export type ExpoOtpChangeEvent = {
  message: string;
};

export type ExpoOtpVerifyModuleEvents = {
  onOtpReceived: (value: ExpoOtpChangeEvent) => void;
};

declare class ExpoOtpVerifyModuleClass extends NativeModule<ExpoOtpVerifyModuleEvents> {
  getHash: () => string[];
  getOtp: () => Promise<boolean>;
}

// This call loads the native module object from the JSI.
const ExpoOtpVerifyModule =
  requireNativeModule<ExpoOtpVerifyModuleClass>("ExpoOtpVerify");

async function getOtp(): Promise<boolean> {
  return await ExpoOtpVerifyModule.getOtp();
}

function getHash(): string[] {
  return ExpoOtpVerifyModule.getHash();
}

async function startOtpListener(
  listener: (value: ExpoOtpChangeEvent) => void,
): Promise<EventSubscription | null> {
  return getOtp().then(() =>
    ExpoOtpVerifyModule.addListener("onOtpReceived", listener),
  );
}

function removeListener(): void {
  return ExpoOtpVerifyModule.removeAllListeners("onOtpReceived");
}

const useOtpVerify = ({ numberOfDigits } = { numberOfDigits: 0 }) => {
  const [message, setMessage] = useState<string | null>(null);
  const [otp, setOtp] = useState<string | null>(null);
  const [timeoutError, setTimeoutError] = useState<boolean>(false);
  const [hash, setHash] = useState<string[] | null>([]);

  const handleMessage = (response: ExpoOtpChangeEvent) => {
    console.log(response.message);
    if (response.message === "Timeout Error.") {
      setTimeoutError(true);
    } else {
      setMessage(response.message);
      if (numberOfDigits && response.message) {
        const otpDigits = new RegExp(`(\\d{${numberOfDigits}})`, "g").exec(
          response.message,
        );
        if (otpDigits && otpDigits[1]) setOtp(otpDigits[1]);
      }
    }
  };

  useEffect(() => {
    setHash(getHash());
    startOtpListener(handleMessage);
    return () => {
      removeListener();
    };
  }, []);

  const startListener = () => {
    setOtp("");
    setMessage("");
    startOtpListener(handleMessage);
  };

  const stopListener = () => {
    removeListener();
  };

  return { otp, message, hash, timeoutError, stopListener, startListener };
};

export { getHash, startOtpListener, removeListener, useOtpVerify };
