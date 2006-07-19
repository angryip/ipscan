/* 
 * $Id: IPPacket.java,v 1.1 2005/09/13 20:15:53 angryziber Exp $
 *
 * Copyright 2004-2005 Daniel F. Savarese
 * Contact Information: http://www.savarese.org/contact.html
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.savarese.org/software/ApacheLicense-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.savarese.vserv.tcpip;

import java.net.*;

/**
 * IPPacket wraps the raw bytes comprising an IPv4 packet and exposes
 * its content via setter and getter methods.  After you alter the
 * header of an IP packet you have to recompute the checksum with
 * {@link #computeIPChecksum computeIPChecksum()}.  The structure of
 * IP packets is described in
 * <a href="http://www.ietf.org/rfc/rfc0760.txt?number=760">RFC 760</a>.
 *
 * @author <a href="http://www.savarese.org/">Daniel F. Savarese</a>
 */

public class IPPacket {

  /** Offset into byte array of total packet length header value. */
  public static final int OFFSET_TOTAL_LENGTH = 2;

  /** Offset into byte array of source address header value. */
  public static final int OFFSET_SOURCE_ADDRESS      = 12;

  /** Number of bytes in source address. */
  public static final int LENGTH_SOURCE_ADDRESS      = 4;

  /** Offset into byte array of destination address header value. */
  public static final int OFFSET_DESTINATION_ADDRESS = 16;

  /** Number of bytes in destination address. */
  public static final int LENGTH_DESTINATION_ADDRESS = 4;

  /** Offset into byte array of time to live header value. */
  public static final int OFFSET_TTL      = 8;

  /** Offset into byte array of protocol number header value. */
  public static final int OFFSET_PROTOCOL = 9;

  /** Offset into byte array of header checksum header value. */
  public static final int OFFSET_IP_CHECKSUM = 10;

  /** Protocol constant for IPv4. */
  public static final int PROTOCOL_IP   = 0;

  /** Protocol constant for ICMP. */
  public static final int PROTOCOL_ICMP = 1;

  /** Protocol constant for TCP. */
  public static final int PROTOCOL_TCP  = 6;

  /** Protocol constant for UDP. */
  public static final int PROTOCOL_UDP  = 17;

  /** Raw packet data. */
  protected byte[] _data_;


  /**
   * Creates a new IPPacket of a given size.
   *
   * @param size The number of bytes in the packet.
   */
  public IPPacket(int size) {
    setData(new byte[size]);
  }


  /**
   * @return The size of the packet.
   */
  public int size() {
    return _data_.length;
  }


  /**
   * Sets the raw packet byte array.  Although this method would
   * appear to violate object-oriented principles, it is necessary to
   * implement efficient packet processing.  You don't necessarily
   * want to allocate a new IPPacket and data buffer every time a
   * packet arrives and you need to be able to wrap packets from
   * APIs that supply them as byte arrays.
   *
   * @param data The raw packet byte array to wrap.
   */
  public void setData(byte[] data) {
    _data_ = data;
  }


  /**
   * Copies the raw packet data into a byte array.  If the array
   * is too small to hold the data, the data is truncated.
   *
   * @param data The raw packet byte array to wrap.
   */
  public void getData(byte[] data) {
    System.arraycopy(_data_, 0, data, 0, data.length);
  }


  /**
   * Copies the contents of an IPPacket to the calling instance.  If
   * the two packets are of different lengths, a new byte array is
   * allocated equal to the length of the packet parameter.
   *
   * @param packet  The packet to copy from.
   */
  public final void copy(IPPacket packet) {
    if(_data_.length != packet.size())
      setData(new byte[packet.size()]);
    System.arraycopy(packet._data_, 0, _data_, 0, _data_.length);
  }


  /**
   * Sets the IP packet total length header value.
   *
   * @param length The total IP packet length in bytes.
   */
  public final void setIPPacketLength(int length) {
    _data_[OFFSET_TOTAL_LENGTH]     = (byte)((length >> 8) & 0xff);
    _data_[OFFSET_TOTAL_LENGTH + 1] = (byte)(length & 0xff);
  }


  /**
   * @return The IP packet total length header value.
   */
  public final int getIPPacketLength() {
    return (((_data_[OFFSET_TOTAL_LENGTH] & 0xff) << 8) |
            (_data_[OFFSET_TOTAL_LENGTH + 1] & 0xff)); 
  }


  /**
   * Sets the IP header length field.  At most, this can be a 
   * four-bit value.  The high order bits beyond the fourth bit
   * will be ignored.
   *
   * @param length The length of the IP header in 32-bit words.
   */
  public void setIPHeaderLength(int length) {
    // Clear low order bits and then set
    _data_[0] &= 0xf0;
    _data_[0] |= (length & 0xf);
  }


  /**
   * @return The length of the IP header in 32-bit words.
   */
  public final int getIPHeaderLength() {
    return (_data_[0] & 0xf);
  }


  /**
   * @return The length of the IP header in bytes.
   */
  public final int getIPHeaderByteLength() {
    return getIPHeaderLength() << 2;
  }


  /**
   * Sets the protocol number.
   *
   * @param protocol The protocol number.
   */
  public final void setProtocol(int protocol) {
    _data_[OFFSET_PROTOCOL] = (byte)protocol;
  }


  /**
   * @return The protocol number.
   */
  public final int getProtocol() {
    return _data_[OFFSET_PROTOCOL];
  }


  /**
   * @return The time to live value in seconds.
   */
  public final int getTTL() {
    return _data_[OFFSET_TTL];
  }


  /**
   * Computes the IP checksum, optionally updating the IP checksum header.
   *
   * @param update Specifies whether or not to update the IP checksum
   * header after computing the checksum.  A value of true indicates
   * the header should be updated, a value of false indicates it
   * should not be updated.
   * @return The computed IP checksum.
   */
  public final int computeIPChecksum(boolean update) {
    int i     = 0;
    int len   = getIPHeaderByteLength();
    int total = 0;

    while(i < OFFSET_IP_CHECKSUM)
      total+=(((_data_[i++] & 0xff) << 8) | (_data_[i++] & 0xff));

    // Skip existing checksum.
    i = OFFSET_IP_CHECKSUM + 2;

    while(i < len)
      total+=(((_data_[i++] & 0xff) << 8) | (_data_[i++] & 0xff));

    // Fold to 16 bits
    while((total & 0xffff0000) != 0)
      total = (total & 0xffff) + (total >>> 16);

    total = (~total & 0xffff);

    if(update) {
      _data_[OFFSET_IP_CHECKSUM]     = (byte)(total >> 8);
      _data_[OFFSET_IP_CHECKSUM + 1] = (byte)(total & 0xff);
    }

    return total;
  }


  /**
   * Same as <code>computeIPChecksum(true);</code>
   *
   * @return The computed IP checksum value.
   */
  public final int computeIPChecksum() {
    return computeIPChecksum(true);
  }


  /**
   * @return The IP checksum header value.
   */
  public final int getIPChecksum() {
    return (((_data_[OFFSET_IP_CHECKSUM] & 0xff) << 8) |
            (_data_[OFFSET_IP_CHECKSUM + 1] & 0xff)); 
  }


  /**
   * Retrieves the source IP address into a byte array.  The array
   * should be {@link #LENGTH_SOURCE_ADDRESS} bytes long.
   *
   * @param address The array in which to store the address.
   */
  public final void getSource(byte[] address) {
    System.arraycopy(_data_, OFFSET_SOURCE_ADDRESS, address,
                     0, (address.length < LENGTH_SOURCE_ADDRESS ?
                         address.length : LENGTH_SOURCE_ADDRESS));
  }


  /**
   * Retrieves the destionation IP address into a byte array.  The array
   * should be {@link #LENGTH_DESTINATION_ADDRESS} bytes long.
   *
   * @param address The array in which to store the address.
   */
  public final void getDestination(byte[] address) {
    System.arraycopy(_data_, OFFSET_DESTINATION_ADDRESS, address,
                     0, (address.length < LENGTH_DESTINATION_ADDRESS ?
                         address.length : LENGTH_DESTINATION_ADDRESS));
  }


  /**
   * Retrieves the source IP address as a string into a StringBuffer.
   *
   * @param buffer The StringBuffer in which to store the address.
   */
  public final void getSource(StringBuffer buffer) {
    OctetConverter.octetsToString(buffer, _data_, OFFSET_SOURCE_ADDRESS);
  }


  /**
   * Retrieves the destination IP address as a string into a StringBuffer.
   *
   * @param buffer The StringBuffer in which to store the address.
   */
  public final void getDestination(StringBuffer buffer) {
    OctetConverter.octetsToString(buffer, _data_, OFFSET_DESTINATION_ADDRESS);
  }


  /**
   * Sets the source IP address using a word representation.
   *
   * @param src The source IP address as a 32-bit word.
   */
  public final void setSourceAsWord(int src) {
    OctetConverter.intToOctets(src, _data_, OFFSET_SOURCE_ADDRESS);
  }


  /**
   * Sets the destination IP address using a word representation.
   *
   * @param dest The source IP address as a 32-bit word.
   */
  public final void setDestinationAsWord(int dest) {
    OctetConverter.intToOctets(dest, _data_, OFFSET_DESTINATION_ADDRESS);
  }


  /**
   * @return The source IP address as a 32-bit word.
   */
  public final int getSourceAsWord() {
    return OctetConverter.octetsToInt(_data_, OFFSET_SOURCE_ADDRESS);
  }


  /**
   * @return The destination IP address as a 32-bit word.
   */
  public final int getDestinationAsWord() {
    return OctetConverter.octetsToInt(_data_, OFFSET_DESTINATION_ADDRESS);
  }


  /**
   * @return The source IP address as a java.net.InetAddress instance.
   */
  public final InetAddress getSourceAsInetAddress()
    throws UnknownHostException
  {
    byte[] octets = new byte[4];
    getSource(octets);
    return InetAddress.getByAddress(octets);
  }



  /**
   * @return The destination IP address as a java.net.InetAddress instance.
   */
  public final InetAddress getDestinationAsInetAddress()
    throws UnknownHostException
  {
    byte[] octets = new byte[4];
    getDestination(octets);
    return InetAddress.getByAddress(octets);
  }
}
