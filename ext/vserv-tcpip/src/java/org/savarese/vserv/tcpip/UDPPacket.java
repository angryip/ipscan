/*
 * $Id: UDPPacket.java 5347 2005-05-25 22:45:54Z dfs $
 *
 * Copyright 2005 Daniel F. Savarese
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

/**
 * UDPPacket extends {@link IPPacket} to handle UDP packets.  The UDP
 * packet structure is described in
 * <a href="http://www.ietf.org/rfc/rfc0768.txt?number=768">RFC 768</a>.
 *
 * @author <a href="http://www.savarese.org/">Daniel F. Savarese</a>
 */

public class UDPPacket extends IPPacket {

  /** Offset into the UDP packet of the source port header value. */
  public static final int OFFSET_SOURCE_PORT       = 0;

  /** Offset into the UDP packet of the destination port header value. */
  public static final int OFFSET_DESTINATION_PORT  = 2;

  /** Offset into the UDP packet of UDP total packet length header value. */
  public static final int OFFSET_UDP_TOTAL_LENGTH  = 4;

  /** Offset into the UDP packet of the UDP checksum. */
  public static final int OFFSET_UDP_CHECKSUM      = 6;

  /** Length of the UDP packet header in bytes. */
  public static final int LENGTH_UDP_HEADER        = 8;

  /** The byte offset into the IP packet where the UDP packet begins. */
  private int __offset;

  /**
   * Creates a new UDP packet of a given size.
   *
   * @param size The number of bytes in the packet.
   */
  public UDPPacket(int size) {
    super(size);
    __offset = 0;
  }


  /**
   * Creates a new UDP packet that is a copy of a given packet.
   *
   * @param packet The packet to replicate.
   */
  public UDPPacket(UDPPacket packet) {
    super(packet.size());
    copy(packet);
    __offset = packet.__offset;
  }


  /**
   * Copies the contents of a UDPPacket.  If the current data array is
   * of insufficient length to store the contents, a new array is
   * allocated.
   *
   * @param packet The UDPPacket to copy.
   */
  public final void copyData(UDPPacket packet) {
    if(_data_.length < packet._data_.length) {
      byte[] data = new byte[packet._data_.length];
      System.arraycopy(_data_, 0, data, 0, getCombinedHeaderByteLength());
      _data_ = data;
    }
    int length = packet.getUDPDataByteLength();
    System.arraycopy(packet._data_, packet.getCombinedHeaderByteLength(),
                     _data_, getCombinedHeaderByteLength(), length);
    setUDPDataByteLength(length);
  }


  public void setData(byte[] data) {
    super.setData(data);
    __offset = getIPHeaderByteLength();
  }


  /**
   * Sets the source port.
   *
   * @param port The new source port.
   */
  public final void setSourcePort(int port) {
    _data_[__offset + OFFSET_SOURCE_PORT]     = (byte)((port >> 8) & 0xff);
    _data_[__offset + OFFSET_SOURCE_PORT + 1] = (byte)(port & 0xff);
  }


  /**
   * Sets the destination port.
   *
   * @param port The new destination port.
   */
  public final void setDestinationPort(int port) {
    _data_[__offset + OFFSET_DESTINATION_PORT]  = (byte)((port >> 8) & 0xff);
    _data_[__offset + OFFSET_DESTINATION_PORT + 1] = (byte)(port & 0xff);
  }


  /**
   * @return The source port.
   */
  public final int getSourcePort() {
    return (((_data_[__offset + OFFSET_SOURCE_PORT] & 0xff) << 8) |
            (_data_[__offset + OFFSET_SOURCE_PORT + 1] & 0xff)); 
  }


  /**
   * @return The destination port.
   */
  public final int getDestinationPort() {
    return (((_data_[__offset + OFFSET_DESTINATION_PORT] & 0xff) << 8) |
            (_data_[__offset + OFFSET_DESTINATION_PORT + 1] & 0xff)); 
  }


  public void setIPHeaderLength(int length) {
    super.setIPHeaderLength(length);
    __offset = getIPHeaderByteLength();
  }


  /**
   * Sets the UDP total length header field.
   *
   * @param length The length of the UDP packet in bytes.
   */
  public void setUDPPacketLength(int length) {
    _data_[__offset + OFFSET_UDP_TOTAL_LENGTH] = (byte)((length >> 8) & 0xff);
    _data_[__offset + OFFSET_UDP_TOTAL_LENGTH + 1] = (byte)(length & 0xff);
  }


  /**
   * @return The value of the UDP total length header field.
   */
  public final int getUDPPacketLength() {
    return  (((_data_[__offset + OFFSET_UDP_TOTAL_LENGTH] & 0xff) << 8) |
            (_data_[__offset + OFFSET_UDP_TOTAL_LENGTH + 1] & 0xff)); 
  }


  /**
   * @return The UDP checksum.
   */
  public final int getUDPChecksum() {
    return (((_data_[__offset + OFFSET_UDP_CHECKSUM] & 0xff) << 8) |
            (_data_[__offset + OFFSET_UDP_CHECKSUM + 1] & 0xff)); 
  }


  /**
   * @return The UDP packet length in bytes.  This is the size of the
   * IP packet minus the size of the IP header.  Normally, you want
   * this to equal the length stored in the UDP header
   * (see {@link #getUDPPacketLength}).
   */
  public final int getUDPPacketByteLength() {
    return getIPPacketLength() - __offset;
  }


  /**
   * @return The IP header length plus the UDP header length in bytes.
   */
  public final int getCombinedHeaderByteLength() {
    return __offset + LENGTH_UDP_HEADER;
  }


  /**
   * Sets the length of the UDP data payload.
   *
   * @param length The length of the UDP data payload in bytes.
   */
  public final void setUDPDataByteLength(int length) {
    if(length < 0)
      length = 0;

    setIPPacketLength(getCombinedHeaderByteLength() + length);
  }


  public final int getUDPDataByteLength() {
    return getIPPacketLength() - getCombinedHeaderByteLength();
  }


  private final int __getVirtualHeaderTotal() {
    int s1 =
      ((_data_[OFFSET_SOURCE_ADDRESS] & 0xff) << 8) |
      (_data_[OFFSET_SOURCE_ADDRESS + 1] & 0xff);
    int s2 =
      ((_data_[OFFSET_SOURCE_ADDRESS + 2] & 0xff) << 8) |
      (_data_[OFFSET_SOURCE_ADDRESS + 3] & 0xff);
    int d1 =
      ((_data_[OFFSET_DESTINATION_ADDRESS] & 0xff) << 8) |
      (_data_[OFFSET_DESTINATION_ADDRESS + 1] & 0xff);
    int d2 =
      ((_data_[OFFSET_DESTINATION_ADDRESS + 2] & 0xff) << 8) |
      (_data_[OFFSET_DESTINATION_ADDRESS + 3] & 0xff);
    return s1 + s2 + d1 + d2 + getProtocol() + getUDPPacketByteLength();
  }


  /**
   * Computes the UDP checksum, optionally updating the UDP checksum header.
   *
   * @param update Specifies whether or not to update the UDP checksum
   * header after computing the checksum.  A value of true indicates
   * the header should be updated, a value of false indicates it
   * should not be updated.
   * @return The computed UDP checksum.
   */
  public final int computeUDPChecksum(boolean update) {
    return _computeChecksum_(__offset, __offset + OFFSET_UDP_CHECKSUM,
                             getIPPacketLength(), __getVirtualHeaderTotal(),
                             update);
  }


  /**
   * Same as <code>computeUDPChecksum(true);</code>
   *
   * @return The computed UDP checksum value.
   */
  public final int computeUDPChecksum() {
    return computeUDPChecksum(true);
  }
}
