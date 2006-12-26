/*
 * $Id: TCPPacket.java 6023 2005-12-10 20:42:15Z dfs $
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

/**
 * TCPPacket extends {@link IPPacket} to handle TCP packets.  The TCP
 * packet structure is described in
 * <a href="http://www.ietf.org/rfc/rfc0761.txt?number=761">RFC 761</a>.
 *
 * @author <a href="http://www.savarese.org/">Daniel F. Savarese</a>
 */

public class TCPPacket extends IPPacket {

  /** Offset into the TCP packet of the source port header value. */
  public static final int OFFSET_SOURCE_PORT       = 0;

  /** Offset into the TCP packet of the destination port header value. */
  public static final int OFFSET_DESTINATION_PORT  = 2;

  /** Offset into the TCP packet of the sequence number header value. */
  public static final int OFFSET_SEQUENCE          = 4;

  /** Offset into the TCP packet of the acknowledgement number header value. */
  public static final int OFFSET_ACK               = 8;

  /** Offset into the TCP packet of the TCP header length. */
  public static final int OFFSET_HEADER_LENGTH     = 12;

  /** Offset into the TCP packet of the control header. */
  public static final int OFFSET_CONTROL           = 13;

  /** Offset into the TCP packet of the window size. */
  public static final int OFFSET_WINDOW_SIZE       = 14;

  /** Offset into the TCP packet of the TCP checksum. */
  public static final int OFFSET_TCP_CHECKSUM      = 16;

  /** Offset into the TCP packet of the URG pointer. */
  public static final int OFFSET_URG_POINTER       = 18;

  /** A mask for extracting the FIN bit from the control header. */
  public static final int MASK_FIN = 0x01;

  /** A mask for extracting the SYN bit from the control header. */
  public static final int MASK_SYN = 0x02;

  /** A mask for extracting the reset bit from the control header. */
  public static final int MASK_RST = 0x04;

  /** A mask for extracting the push bit from the control header. */
  public static final int MASK_PSH = 0x08;

  /** A mask for extracting the ACK bit from the control header. */
  public static final int MASK_ACK = 0x10;

  /** A mask for extracting the urgent bit from the control header. */
  public static final int MASK_URG = 0x20;

  /** A byte value for TCP options indicating end of option list. */
  public static final byte KIND_EOL  = 0;

  /** A byte value for TCP options indicating no operation. */
  public static final byte KIND_NOP  = 1;

  /**
   * A byte value for TCP options identifying a selective
   * acknowledgement option.
   */
  public static final byte KIND_SACK = 4;

  /** The byte offset into the IP packet where the TCP packet begins. */
  private int __offset;

  /**
   * Creates a new TCP packet of a given size.
   *
   * @param size The number of bytes in the packet.
   */
  public TCPPacket(int size) {
    super(size);
    __offset = 0;
  }


  /**
   * Creates a new TCP packet that is a copy of a given packet.
   *
   * @param packet The packet to replicate.
   */
  public TCPPacket(TCPPacket packet) {
    super(packet.size());
    copy(packet);
    __offset = packet.__offset;
  }


  /**
   * Clears all selective acknowledgement options.  This is a
   * temporary kluge and will be removed from the final API.  Do not
   * use it.  The final API will have proper methods for adjusting
   * selective acknowledgement options.
   */
  public void clearSACK() {
    int headerLength = getTCPHeaderByteLength();
    int offset = OFFSET_URG_POINTER + 2;

    if(headerLength > offset) {
      offset+=__offset;
      headerLength+=__offset;

      loop:
      do {
        byte kind = _data_[offset];

        switch(kind) {
          case KIND_NOP:
            ++offset;
            break;
          case KIND_EOL:
            break loop;
          case KIND_SACK:
            _data_[offset]     = KIND_NOP;
            _data_[offset + 1] = KIND_NOP;
            break loop;
            //break;
          default:
            offset+=_data_[offset + 1];
            /*
            int length = _data_[offset + 1];
            while(length-- > 0)
              _data_[offset++] = KIND_NOP;
            */
            break;
        }

      } while(offset < headerLength);

    }
  }


  /**
   * Copies the contents of a TCPPacket.  If the current data array is
   * of insufficient length to store the contents, a new array is
   * allocated.
   *
   * @param packet The TCPPacket to copy.
   */
  public final void copyData(TCPPacket packet) {
    if(_data_.length < packet._data_.length) {
      byte[] data = new byte[packet._data_.length];
      System.arraycopy(_data_, 0, data, 0, getCombinedHeaderByteLength());
      _data_ = data;
    }
    int length = packet.getTCPDataByteLength();
    System.arraycopy(packet._data_, packet.getCombinedHeaderByteLength(),
                     _data_, getCombinedHeaderByteLength(), length);
    setTCPDataByteLength(length);
  }

  /**
   * @param mask The bit mask to check.
   * @return True only if all of the bits in the mask are set.
   */
  public boolean isSet(int mask) {
    return ((_data_[__offset + OFFSET_CONTROL] & mask) == mask);
  }


  /**
   * @param mask The bit mask to check.
   * @return True if any of the bits in the mask are set.
   */
  public boolean isSetAny(int mask) {
    return ((_data_[__offset + OFFSET_CONTROL] & mask) != 0);
  }


  /**
   * @param mask The bit mask to check.
   * @return True only if all of the bits in the mask are set
   * and ONLY the bits in the mask are set.
   */
  public boolean isSetOnly(int mask) {
    int flags = _data_[__offset + OFFSET_CONTROL] & 0xff;
    return ((flags & mask) == flags);
  }


  /**
   * Sets the specified control bits without altering any other bits
   * in the control header.
   *
   * @param mask The bits to set.
   */
  public void addControlFlags(int mask) {
    int flags = _data_[__offset + OFFSET_CONTROL] & 0xff;
    flags |= mask;
    _data_[__offset + OFFSET_CONTROL] = (byte)(flags & 0xff);
  }


  /**
   * Unsets the specified control bits.
   *
   * @param mask The bits to unset.
   */
  public void removeControlFlags(int mask) {
    int flags = _data_[__offset + OFFSET_CONTROL] & 0xff;
    flags |= mask;
    flags ^= mask;
    _data_[__offset + OFFSET_CONTROL] = (byte)(flags & 0xff);
  }


  /**
   * Sets the control header to the sepecified value.
   *
   * @param mask The new control header bit mask.
   */
  public void setControlFlags(int mask) {
    _data_[__offset + OFFSET_CONTROL] = (byte)(mask & 0xff);
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


  /**
   * Sets the sequence number.
   *
   * @param seq The new sequence number.
   */
  public final void setSequenceNumber(long seq) {
    OctetConverter.intToOctets((int)(seq & 0xffffffff), _data_,
                               __offset + OFFSET_SEQUENCE);
  }


  /**
   * @return The sequence number.
   */
  public final long getSequenceNumber() {
    return (((_data_[__offset + OFFSET_SEQUENCE] & 0xffL) << 24) |
            ((_data_[__offset + OFFSET_SEQUENCE + 1] & 0xffL) << 16) |
            ((_data_[__offset + OFFSET_SEQUENCE + 2] & 0xffL) << 8) |
             (_data_[__offset + OFFSET_SEQUENCE + 3] & 0xffL));
  }


  /**
   * Sets the acknowledgement number.
   *
   * @param seq The new acknowledgement number.
   */
  public final void setAckNumber(long seq) {
    OctetConverter.intToOctets((int)(seq & 0xffffffff), _data_,
                               __offset + OFFSET_ACK);
  }


  /**
   * @return The acknowledgement number.
   */
  public final long getAckNumber() {
    return (((_data_[__offset + OFFSET_ACK] & 0xffL) << 24) |
            ((_data_[__offset + OFFSET_ACK + 1] & 0xffL) << 16) |
            ((_data_[__offset + OFFSET_ACK + 2] & 0xffL) << 8) |
             (_data_[__offset + OFFSET_ACK + 3] & 0xffL));
  }


  public void setIPHeaderLength(int length) {
    super.setIPHeaderLength(length);
    __offset = getIPHeaderByteLength();
  }


  /**
   * Sets te TCP header length (i.e., the data offset field) in 32-bit words.
   *
   * @param length The TCP header length in 32-bit words.
   */
  public final void setTCPHeaderLength(int length) {
    _data_[__offset + OFFSET_HEADER_LENGTH] &= 0x0f;
    _data_[__offset + OFFSET_HEADER_LENGTH] |= ((length << 4) & 0xf0);
  }


  /**
   * @return The TCP header length in 32-bit words.
   */
  public final int getTCPHeaderLength() {
    return (_data_[__offset + OFFSET_HEADER_LENGTH] & 0xf0) >> 4;
  }


  /**
   * @return The TCP header length in bytes.
   */
  public final int getTCPHeaderByteLength() {
    return getTCPHeaderLength() << 2;
  }


  /**
   * Sets the TCP window size.
   *
   * @param window The TCP window size.
   */
  public final void setWindowSize(int window) {
    _data_[__offset + OFFSET_WINDOW_SIZE]  = (byte)((window >> 8) & 0xff);
    _data_[__offset + OFFSET_WINDOW_SIZE + 1] = (byte)(window & 0xff);
  }


  /**
   * @return The TCP window size.
   */
  public final int getWindowSize() {
    return (((_data_[__offset + OFFSET_WINDOW_SIZE] & 0xff) << 8) |
            (_data_[__offset + OFFSET_WINDOW_SIZE + 1] & 0xff)); 
  }


  /**
   * Sets the urgent pointer.
   *
   * @param pointer The urgent pointer value.
   */
  public final void setUrgentPointer(int pointer) {
    _data_[__offset + OFFSET_URG_POINTER]  = (byte)((pointer >> 8) & 0xff);
    _data_[__offset + OFFSET_URG_POINTER + 1] = (byte)(pointer & 0xff);
  }


  /**
   * @return The urgent pointer value.
   */
  public final int getUrgentPointer() {
    return (((_data_[__offset + OFFSET_URG_POINTER] & 0xff) << 8) |
            (_data_[__offset + OFFSET_URG_POINTER + 1] & 0xff)); 
  }


  /**
   * @return The TCP checksum.
   */
  public final int getTCPChecksum() {
    return (((_data_[__offset + OFFSET_TCP_CHECKSUM] & 0xff) << 8) |
            (_data_[__offset + OFFSET_TCP_CHECKSUM + 1] & 0xff)); 
  }


  /**
   * @return The TCP packet length in bytes.  This is the size of the
   * IP packet minus the size of the IP header.
   */
  public final int getTCPPacketByteLength() {
    return getIPPacketLength() - __offset;
  }


  /**
   * @return The IP header length plus the TCP header length in bytes.
   */
  public final int getCombinedHeaderByteLength() {
    return __offset + getTCPHeaderByteLength();
  }


  /**
   * Sets the length of the TCP data payload.
   *
   * @param length The length of the TCP data payload in bytes.
   */
  public final void setTCPDataByteLength(int length) {
    if(length < 0)
      length = 0;

    setIPPacketLength(getCombinedHeaderByteLength() + length);
  }


  public final int getTCPDataByteLength() {
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
    return s1 + s2 + d1 + d2 + getProtocol() + getTCPPacketByteLength();
  }


  /**
   * Computes the TCP checksum, optionally updating the TCP checksum header.
   *
   * @param update Specifies whether or not to update the TCP checksum
   * header after computing the checksum.  A value of true indicates
   * the header should be updated, a value of false indicates it
   * should not be updated.
   * @return The computed TCP checksum.
   */
  public final int computeTCPChecksum(boolean update) {
    return _computeChecksum_(__offset, __offset + OFFSET_TCP_CHECKSUM,
                             getIPPacketLength(), __getVirtualHeaderTotal(),
                             update);
  }


  /**
   * Same as <code>computeTCPChecksum(true);</code>
   *
   * @return The computed TCP checksum value.
   */
  public final int computeTCPChecksum() {
    return computeTCPChecksum(true);
  }
}
