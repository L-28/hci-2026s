import { View, Text, FlatList, StyleSheet } from 'react-native'

export default function HistoryScreen({ route }) {
  const { entries } = route.params

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Verlauf</Text>
      <FlatList
        data={entries}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={styles.entry}>
            <Text style={styles.entryMood}>{item.mood}</Text>
            <Text style={styles.entryTime}>{item.time}</Text>
          </View>
        )}
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F0FFF4',
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#1A3C2E',
    marginVertical: 20,
  },
  entry: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 10,
    marginBottom: 12,
  },
  entryMood: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#2E7D5E',
  },
  entryTime: {
    fontSize: 13,
    color: '#888',
    marginTop: 4,
  },
})
