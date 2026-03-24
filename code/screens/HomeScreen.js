import { useState } from 'react'
import { View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native'

export default function HomeScreen({ navigation }) {
  const [mood, setMood] = useState('')
  const [entries, setEntries] = useState([])

  const handleSave = () => {
    if (mood.trim() === '') return
    const newEntry = { mood, time: new Date().toLocaleString(), id: Date.now().toString() }
    const updatedEntries = [newEntry, ...entries]
    setEntries(updatedEntries)
    navigation.navigate('Details', { mood, time: newEntry.time, entries: updatedEntries })
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Mood Tracker</Text>
      <TextInput
        style={styles.input}
        placeholder="Stimmung eingeben..."
        onChangeText={setMood}
        value={mood}
      />
      <TouchableOpacity style={styles.button} onPress={handleSave}>
        <Text style={styles.buttonText}>Speichern</Text>
      </TouchableOpacity>
      <Text style={styles.link} onPress={() => navigation.navigate('History', { entries })}>
        Verlauf ansehen ({entries.length})
      </Text>
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
    fontSize: 36,
    fontWeight: 'bold',
    color: '#1A3C2E',
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#AAA',
    width: '100%',
    padding: 12,
    borderRadius: 10,
    fontSize: 16,
    backgroundColor: '#fff',
    marginBottom: 16,
  },
  button: {
    backgroundColor: '#2E7D5E',
    padding: 16,
    borderRadius: 10,
    alignItems: 'center',
    width: '100%',
    marginBottom: 16,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  link: {
    color: '#2E7D5E',
    fontSize: 15,
  },
})
