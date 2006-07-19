/*
 * $Id: ICMPPacket.java,v 1.1 2005/09/13 20:15:53 angryziber Exp $
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
 * ICMPPacket extends {@link IPPacket} to handle ICMP packets.  The ICMP
 * packet structure is described in
 * <a href="http://www.ietf.org/rfc/rfc0792.txt?number=792">RFC 792</a>.
 *
 * @author <a href="http://www.savarese.org/">Daniel F. Savarese</a>
 */

public abstract class ICMPPacket extends IPPacket {

  /** Offset into the ICMP packet of the type header value. */
  public static final int OFFSET_TYPE         = 0;

  /** Offset into the ICMP packet of the code header value. */
  public static final int OFFSET_CODE         = 1;

  /** Offset into the ICMP packet of the ICMP checksum. */
  public static final int OFFSET_ICMP_CHECKSUM = 2;

  /** Offset into the ICMP packet of the identifier header value. */
  public static final int OFFSET_IDENTIFIER   = 4;

  /** Offset into the ICMP packet of the sequence number header value. */
  public static final int OFFSET_SEQUENCE     = 6;

  /** The ICMP type number for an echo request. */
  public static final int TYPE_ECHO_REQUEST   = 8;

  /** The ICMP type number for an echo reply. */
  public static final int TYPE_ECHO_REPLY     = 0;

  /** The byte offset into the IP packet where the ICMP packet begins. */
  int _offset;


  /**
   * Creates a new ICMP packet of a given size.
   *
   * @param size The number of bytes in the packet.
   */
  public ICMPPacket(int size) {
    super(size);
    _offset = 0;
  }


  /**
   * Creates a new ICMP packet that is a copy of a given packet.
   *
   * @param packet The packet to replicate.
   */
  public ICMPPacket(ICMPPacket packet) {
    super(packet.size());
    copy(packet);
    _offset = packet._offset;
  }


  /** @return The number of bytes in the ICMP packet header. */
  public abstract int getICMPHeaderByteLength();


  public void setIPHeaderLength(int length) {
    super.setIPHeaderLength(length);
    _offset = getIPHeaderByteLength();
  }


  /**
   * @return The total number of bytes in the IP and ICMP headers.
   */
  public final int getCombinedHeaderByteLength() {
    return _offset + getICMPHeaderByteLength();
  }


  /**
   * Sets the length of the ICMP data payload.
   *
   * @param length The length of the ICMP data payload in bytes.
   */
  public final void setICMPDataByteLength(int length) {
    if(length < 0)
      length = 0;

    setIPPacketLength(getCombinedHeaderByteLength() + length);
  }


  /**
   * @return The number of bytes in the ICMP data payload.
   */
  public final int getICMPDataByteLength() {
    return getIPPacketLength() - getCombinedHeaderByteLength();
  }


  /**
   * @return The ICMP packet length.  This is the size of the IP packet
   * minus the size of the IP header.
   */
  public final int getICMPPacketByteLength() {
    return getIPPacketLength() - _offset;
  }


  /**
   * Copies the contents of an ICMPPacket.  If the current data array is
   * of insufficient length to store the contents, a new array is
   * allocated.
   *
   * @param packet The TCPPacket to copy.
   */
  public final void copyData(ICMPPacket packet) {
    if(_data_.length < packet._data_.length) {
      byte[] data = new byte[packet._data_.length];
      System.arraycopy(_data_, 0, data, 0, getCombinedHeaderByteLength());
      _data_ = data;
    }
    int length = packet.getICMPDataByteLength();
    System.arraycopy(packet._data_, packet.getCombinedHeaderByteLength(),
                     _data_, getCombinedHeaderByteLength(), length);
    setICMPDataByteLength(length);
  }


  public void setData(byte[] data) {
    super.setData(data);
    _offset = getIPHeaderByteLength();
  }


  /**
   * Sets the ICMP type header field.
   *
   * @param type The new type.
   */
  public final void setType(int type) {
    _data_[_offset + OFFSET_TYPE] = (byte)(type & 0xff);
  }


  /**
   * @return The ICMP type header field.
   */
  public final int getType() {
    return (_data_[_offset + OFFSET_TYPE] & 0xff);
  }


  /**
   * Sets the ICMP code header field.
   *
   * @param code The new type.
   */
  public final void setCode(int code) {
    _data_[_offset + OFFSET_CODE] = (byte)(code & 0xff);
  }


  /**
   * @return The ICMP code header field.
   */
  public final int getCode() {
    return (_data_[_offset + OFFSET_CODE] & 0xff);
  }


  /**
   * @return The ICMP checksum.
   */
  public final int getICMPChecksum() {
    return (((_data_[_offset + OFFSET_ICMP_CHECKSUM] & 0xff) << 8) |
            (_data_[_offset + OFFSET_ICMP_CHECKSUM + 1] & 0xff)); 
  }


  /**
   * Computes the ICMP checksum, optionally updating the ICMP checksum header.
   *
   * @param update Specifies whether or not to update the ICMP checksum
   * header after computing the checksum.  A value of true indicates
   * the header should be updated, a value of false indicates it
   * should not be updated.
   * @return The computed ICMP checksum.
   */
  public final int computeICMPChecksum(boolean update) {
    int total = 0;
    int len   = getIPPacketLength();
    int i     = _offset;
    int imax  = _offset + OFFSET_ICMP_CHECKSUM;

    while(i < imax)
      total+=(((_data_[i++] & 0xff) << 8) | (_data_[i++] & 0xff));

    // Skip existing checksum.
    i = _offset + OFFSET_ICMP_CHECKSUM + 2;

    imax = len - (len % 2);

    while(i < imax)
      total+=(((_data_[i++] & 0xff) << 8) | (_data_[i++] & 0xff));

    if(i < len)
      total+=((_data_[i] & 0xff) << 8);

    // Fold to 16 bits
    while((total & 0xffff0000) != 0)
      total = (total & 0xffff) + (total >>> 16);

    total = (~total & 0xffff);

    if(update) {
      _data_[_offset + OFFSET_ICMP_CHECKSUM]     = (byte)(total >> 8);
      _data_[_offset + OFFSET_ICMP_CHECKSUM + 1] = (byte)(total & 0xff);
    }

    return total;
  }


  /**
   * Same as <code>computeICMPChecksum(true);</code>
   *
   * @return The computed ICMP checksum value.
   */
  public final int computeICMPChecksum() {
    return computeICMPChecksum(true);
  }

}

