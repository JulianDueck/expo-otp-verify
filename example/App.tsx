import * as Clipboard from "expo-clipboard";
import { useOtpVerify } from "expo-otp-verify";
import { Button, SafeAreaView, ScrollView, Text, View } from "react-native";

export default function App() {
  const { hash, otp, message, timeoutError } = useOtpVerify({
    numberOfDigits: 6,
  });

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.container}>
        <Text style={styles.header}>Module API Example</Text>
        <Group name="Hash">
          <Text>{hash}</Text>
          <Button
            title="Copy to clipboard"
            onPress={() => Clipboard.setStringAsync(hash?.toString() ?? "")}
          />
        </Group>
        <Group name="OTP">
          <Text>{otp}</Text>
        </Group>
        <Group name="Message">
          <Text>{message}</Text>
        </Group>
        <Group name="Timeout">
          <Text>{timeoutError ? "true" : "false"}</Text>
        </Group>
      </ScrollView>
    </SafeAreaView>
  );
}

function Group(props: { name: string; children: React.ReactNode }) {
  return (
    <View style={styles.group}>
      <Text style={styles.groupHeader}>{props.name}</Text>
      {props.children}
    </View>
  );
}

const styles = {
  header: {
    fontSize: 30,
    margin: 20,
  },
  groupHeader: {
    fontSize: 20,
    marginBottom: 20,
  },
  group: {
    margin: 20,
    backgroundColor: "#fff",
    borderRadius: 10,
    padding: 20,
  },
  container: {
    flex: 1,
    backgroundColor: "#eee",
  },
  view: {
    flex: 1,
    height: 200,
  },
};
