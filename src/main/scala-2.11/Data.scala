class Data(keyPassed: String, valuePassed: String) {
  val id = Hasher.hash((keyPassed))
  val key = keyPassed
  val value = valuePassed
}
