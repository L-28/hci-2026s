import { View, Text, StyleSheet } from 'react-native'

export default function DetailsScreen({ route }) {
  const { mood, time } = route.params

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Deine Stimmung</Text>
      <Text style={styles.mood}>{mood}</Text>
      <Text style={styles.time}>Gespeichert um: {time}</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#F0FFF4',
    padding: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#1A3C2E',
    marginBottom: 30,
  },
  mood: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#2E7D5E',
    marginBottom: 16,
  },
  time: {
    fontSize: 15,
    color: '#555',
  },
})
