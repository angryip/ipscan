/*
 * $Id: ICMPEchoPacket.java 5260 2005-05-10 21:01:16Z dfs $
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
 * ICMPEchoPacket extends {@link ICMPPacket} to implement ICMP echo request
 * and reply packets.
 *
 * @author <a href="http://www.savarese.org/">Daniel F. Savarese</a>
 */

public class ICMPEchoPacket extends ICMPPacket {

  /** Offset into the ICMP packet of the identifier header value. */
  public static final int OFFSET_IDENTIFIER   = 4;

  /** Offset into the ICMP packet of the sequence number header value. */
  public static final int OFFSET_SEQUENCE     = 6;


  /**
   * Creates a new ICMP echo packet of a given size.
   *
   * @param size The number of bytes in the packet.
   */
  public ICMPEchoPacket(int size) {
    super(size);
  }


  /**
   * Creates a new ICMP echo packet that is a copy of a given packet.
   *
   * @param packet The packet to replicate.
   */
  public ICMPEchoPacket(ICMPEchoPacket packet) {
    super(packet);
  }


  public int getICMPHeaderByteLength() {
    return 8;
  }


  /**
   * Sets the identifier header field.
   *
   * @param id The new identifier.
   */
  public final void setIdentifier(int id) {
    _data_[_offset + OFFSET_IDENTIFIER]     = (byte)((id >> 8) & 0xff);
    _data_[_offset + OFFSET_IDENTIFIER + 1] = (byte)(id & 0xff);
  }


  /**
   * @return The identifier header field.
   */
  public final int getIdentifier() {
    return (((_data_[_offset + OFFSET_IDENTIFIER] & 0xff) << 8) |
             (_data_[_offset + OFFSET_IDENTIFIER + 1] & 0xff));
  }


  /**
   * Sets the sequence number.
   *
   * @param seq The new sequence number.
   */
  public final void setSequenceNumber(int seq) {
    _data_[_offset + OFFSET_SEQUENCE]     = (byte)((seq >> 8) & 0xff);
    _data_[_offset + OFFSET_SEQUENCE + 1] = (byte)(seq & 0xff);
  }


  /**
   * @return The sequence number.
   */
  public final int getSequenceNumber() {
    return (((_data_[_offset + OFFSET_SEQUENCE] & 0xff) << 8) |
             (_data_[_offset + OFFSET_SEQUENCE + 1] & 0xff));
  }

}
