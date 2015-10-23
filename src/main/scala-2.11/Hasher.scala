import java.nio.ByteBuffer

object Hasher {

  val md = java.security.MessageDigest.getInstance("SHA-1")

  //Returns a 62-Bit Long from the SHA-1 Hash
  def hash(in: String): Long = {
//  println("IN: " + in)
    val bytes: Array[Byte] = md.digest(in.getBytes) //160 bit SHA-1 Hash

    var longBytes: Array[Byte] = bytes.slice(bytes.length-8, bytes.length)
    longBytes(0) = (longBytes(0) & 63).toByte //Set 64th bit to 0. Always positive.

    //Wrap the bytes into a long.
    val long: Long = ByteBuffer.wrap(longBytes).getLong

    return long

  }

}