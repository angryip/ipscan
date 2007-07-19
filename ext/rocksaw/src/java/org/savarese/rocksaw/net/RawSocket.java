/*
 * $Id: RawSocket.java 5979 2005-11-09 18:11:02Z dfs $
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

package org.savarese.rocksaw.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * <p>The RawSocket class provides a strictly utilitarian API for
 * performing I/O with raw sockets.  The API is currently crude, but
 * functional.  It should evolve into something more feature-complete
 * and flexible.</p>
 *
 * <p>We throw java.io.InterruptedIOException when read/write
 * operations time out because java.net.SocketTimeoutException is
 * present only in J2SE 1.4 and up.  By using InterruptedIOException,
 * we allow programmers to use the software with J2SE 1.2 and 1.3.</p>
 *
 * <p>Socket options should not be set until the socket has been
 * opened.</p>
 *
 * <p><em>Important!  On most operating systems, you must have root
 * access or administrative privileges to use raw sockets.</em></p>
 * 
 * @author <a href="http://www.savarese.org/">Daniel F. Savarese</a>
 */

public class RawSocket {

  /**
   * A protocol family constant for {@link #open} indicating IPv4.
   * <em>This should be moved to another class.</em>
   */
  public static final int PF_INET  = 2;

  /**
   * A protocol family constant for {@link #open} indicating IPv6.
   * <em>This should be moved to another class.</em>
   */
  public static final int PF_INET6 = 10 ;

  /**
   * Initializes any system resources used by the RockSaw library.
   * Really, all it does is call WSAStartup on Win32.  It may be
   * called multiple times (only the first call has any effect), but
   * each call must be matched with a corresponding call to
   * RockSawShutdown().
   *
   * @return zero if successful, otherwise some non-zero value.
   */
  private native static int __RockSawStartup();

  /**
   * Deallocates any system resources used by the RockSaw library.
   * Really, all it does is call WSACleanup on Win32.
   */
  native static void __RockSawShutdown();

  static {
	// modified to reuse SWT's loading of jni libs from jar file
    loadLibrary("rocksaw");
    
    if(__RockSawStartup() != 0)
      throw new UnsatisfiedLinkError(__getErrorMessage());

    Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          __RockSawShutdown();
        }
      });
  }

  private static final int __UNDEFINED = -1;

  /**
   * TimeVal is a convenience class for tracking select timeouts.
   * Should we implement select outside of this class, we can make
   * this a public top-level class with proper getters and setters.
   */
  private static final class TimeVal {
    int seconds;
    int microseconds;

    TimeVal() {
      seconds = microseconds = 0;
    }

    void setInMilliseconds(int milliseconds) {
      seconds = milliseconds / 1000;

      if(seconds > 0)
        milliseconds-=(seconds*1000);

      microseconds = milliseconds * 1000;
    }

    int getInMilliseconds() {
      return (seconds * 1000 + microseconds / 1000);
    }

    boolean isZero() {
      return (seconds == 0 && microseconds == 0);
    }
  }

  private int __socket;
  private int __family;
  private TimeVal __stimeout, __rtimeout;
  private boolean __useSelectTimeout;

  /**
   * Creates an uninitialized socket.  If the {@code os.name} system
   * property starts with the string "SunOS",
   * {@link #setUseSelectTimeout} is set to true (because Solaris does not
   * support socket send and receive timeouts), otherwise it is false
   * by default.
   */
  public RawSocket() {
    __socket = __UNDEFINED;
    __family = __UNDEFINED;
    __stimeout = new TimeVal();
    __rtimeout = new TimeVal();

    String os = System.getProperty("os.name");

    if(os != null && os.startsWith("SunOS"))
      setUseSelectTimeout(true);
    else
      setUseSelectTimeout(false);
  }


  private static void loadLibrary(String library) {
	String filename = System.mapLibraryName(library);
	String fullFilename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + filename;
	try {
	  System.load(fullFilename);
	}
	catch (UnsatisfiedLinkError e) {
		try {
			// try to extract
			InputStream is = RawSocket.class.getClassLoader().getResourceAsStream(filename);
			byte[] buffer = new byte[4096];
			OutputStream os = new FileOutputStream(fullFilename);
			int read;
			while ((read = is.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.close();
			is.close();
			if (!net.azib.ipscan.config.Platform.WINDOWS) {
				// TODO: change this to new File(fullFilename).setExecutable(true) in case of Java 1.6
				try {
					Runtime.getRuntime ().exec (new String []{"chmod", "755", fullFilename}).waitFor();
				} catch (Throwable t) {}
			}
			System.load(fullFilename);
		}
		catch (IOException ioe) {
			throw new RuntimeException("Unable to extract native library: " + library, ioe);
		}
	}
	
  }

/**
   * Tests if the socket has been opened.
   *
   * @return True if the socket is open.
   */
  public boolean isOpen() {
    return (__socket > 0);
  }


  /**
   * Writes a system error message into a StringBuffer.
   * The message is appended to the supplied StringBuffer argument.
   * This is not a thread safe call because it relies on the value
   * of errno, which may be altered at any time by another thread.
   *
   * @param buffer The buffer in which to store the error message.
   */
  private native static void __getErrorMessage(StringBuffer buffer);

  private static String __getErrorMessage() {
    StringBuffer buf = new StringBuffer();
    __getErrorMessage(buf);
    return buf.toString();
  }

  private static void __throwIOException() throws IOException {
    throw new IOException(__getErrorMessage());
  }

  private static void __throwSocketException() throws SocketException {
    throw new SocketException(__getErrorMessage());
  }

  private static void __throwInterruptedIOException()
    throws InterruptedIOException
  {
    throw new InterruptedIOException(__getErrorMessage());
  }

  private native static int __socket(int protocolFamily, int protocol);


  /**
   * <p>Returns the protocol number corresponding to the given protocol name.
   * For example, {@code getProtocolByName("icmp");} should return 1 and
   * {@code getProtocolByName("udp");} should return 17.  The native system
   * protocol database is used to look up the protocol numbers.</p>
   *
   * <p><em>This method really belongs in another class, probably in
   * vserv-tcpip, but is currently included here as a convenience.  It
   * may be moved elsewhere in the 1.0 release API.</em></p>
   *
   * @return The protocol number corresponding to the given protocol name.
   *         If the protocol name cannot be found, returns a negative value.
   */
  public native static final int getProtocolByName(String name);


  /**
   * Opens a raw socket.
   *
   * @param protocolFamily The protocol family of the socket (e.g.,
   * {@link #PF_INET} or {@link #PF_INET6}).
   * @param protocol The protocol within the protocol family.  {@link
   * #getProtocolByName} should be used to obtain protocol numbers.
   * @exception IllegalStateException If the object instance is
   * already open.
   * @exception IOException If an error occurs while opening the socket.
   */
  public void open(int protocolFamily, int protocol)
    throws IllegalStateException, IOException
  {
    if(isOpen())
      throw new IllegalStateException();
    __socket = __socket(protocolFamily, protocol);

    if(__socket < 0) {
      __socket = __UNDEFINED;
      __throwIOException();
    }

    __family = protocolFamily;
  }

  private native static int __close(int socket);

  /**
   * Closes the socket.
   *
   * @exception IOException If an I/O error occurs.
   */
  public void close() throws IOException {
    int result = __close(__socket);
    __socket = __UNDEFINED;
    __family = __UNDEFINED;

    if(result != 0)
      __throwIOException();
  }

  /**
   * @return True if errno equals EAGAIN or EWOULDBLOCK.
   */
  private native boolean __isErrorEAGAIN();

  private native static int __setIPHeaderInclude(int socket, boolean on);

  /**
   * @return A negative value if an error occurs, else zero for false and
   *         a positive value for true.
   */
  private native static int __getIPHeaderInclude(int socket);


  /**
   * Sets or unsets the IP_HDRINCL socket option.  Setting this option
   * causes IPv4 packet writes to expect the entire IP packet,
   * starting from the header.  The default behavior is to only expect
   * the data payload.  This option is valid only for IPv4 sockets.
   *
   * @param on True if headers should be included, false if not.
   * @exception SocketException If the option setting could not be altered.
   */
  public void setIPHeaderInclude(boolean on) throws SocketException {
    int result = __setIPHeaderInclude(__socket, on);

    if(result < 0)
      __throwSocketException();
  }


  /**
   * Retrieves the current setting of the IP_HDRINCL option.
   *
   * @return True if the IP_HDRINCL option is set, false if not.
   * @exception SocketException If the option value could not be retrieved.
   */
  public boolean getIPHeaderInclude() throws SocketException {
    int result = __getIPHeaderInclude(__socket);

    if(result < 0)
      __throwSocketException();

    return (result > 0);
  }


  private native static int __setSendBufferSize(int socket, int size);

  /**
   * Sets the send buffer size (SO_SNDBUF).
   *
   * @param size The size of the send buffer.
   * @exception SocketException If the option value could not be set.
   */
  public void setSendBufferSize(int size) throws SocketException {
    int result = __setSendBufferSize(__socket, size);

    if(result < 0)
      __throwSocketException();
  }


  private native static int __getSendBufferSize(int __socket);

  /**
   * Retrieves the send buffer size (SO_SNDBUF).
   *
   * @return The size of the send buffer.
   * @exception SocketException If the option value could not be retrieved.
   */
  public int getSendBufferSize() throws SocketException {
    int result = __getSendBufferSize(__socket);

    if(result < 0)
      __throwSocketException();

    return result;
  }


  private native static int __setReceiveBufferSize(int socket, int size);

  /**
   * Sets the receive buffer size (SO_RCVBUF).
   *
   * @param size The size of the receive buffer.
   * @exception SocketException If the option value could not be set.
   */
  public void setReceiveBufferSize(int size) throws SocketException {
    int result = __setReceiveBufferSize(__socket, size);

    if(result < 0)
      __throwSocketException();
  }


  private native static int __getReceiveBufferSize(int socket);

  /**
   * Retrieves the receive buffer size (SO_RCVBUF).
   *
   * @return The size of the receive buffer.
   * @exception SocketException If the option value could not be retrieved.
   */
  public int getReceiveBufferSize() throws SocketException {
    int result = __getReceiveBufferSize(__socket);

    if(result < 0)
      __throwSocketException();

    return result;
  }

  /**
   * @return Zero if the socket is ready for I/O, negative value if
   * timed out or error occurred.
   */
  private native static
    int __select(int socket, boolean read, int seconds, int microseconds);


  /**
   * <p>Sets whether or not socket send/receive timeouts should be
   * emulated by using the POSIX {@code select} function.  Not all
   * platforms support socket send/receive timeouts and this method
   * provides a means to reproduce the same effect.</p>
   *
   * <p>This method is not guaranteed to be retained in the 1.0 API.  We
   * may find a better way to provide support for read/write timeouts
   * on all platforms.  Technically, it's better to simply use
   * non-blocking I/O rather than rely on socket timeouts, but we have
   * yet to add a non-blocking I/O interface.</p>
   *
   * @param useSelect true if {@code select} should be used to
   * implement timeouts, false if not.
   */
  public void setUseSelectTimeout(boolean useSelect) {
    __useSelectTimeout = useSelect;
  }


  /**
   * <p>Determines whether or not socket send/receive timeouts are
   * emulated by using the POSIX {@code select} system function.
   * Not all platforms support socket send/receive timeouts.  The
   * default value is false except for platforms where the {@code
   * os.name} property starts with the string "SunOS".</p>
   *
   * @return True if send/receive timeouts are emulated with select,
   * false if not.
   */
  public boolean getUseSelectTimeout() {
    return __useSelectTimeout;
  }


  private native static int __setSendTimeout(int socket, int timeout);

  /**
   * Sets the send timeout (SO_SNDTIMEO).  A timeout of zero indicates
   * an infinite timeout.  A negative timeout is undefined.
   *
   * @param timeout The send timeout in milliseconds.
   * @exception SocketException If the option value could not be set.
   */
  public void setSendTimeout(int timeout) throws SocketException {
    __stimeout.setInMilliseconds(timeout);

    if(!getUseSelectTimeout()) {
      int result = 
        __setSendTimeout(__socket, timeout);

      if(result < 0)
        __throwSocketException();
    }
  }


  private native static int __getSendTimeout(int socket);

  /**
   * Retrieves the send timeout (SO_SNDTIMEO).
   *
   * @return The send timeout in milliseconds.
   * @exception SocketException If the option value could not be set.
   */
  public int getSendTimeout() throws SocketException {
    int result;

    if(getUseSelectTimeout())
      result = __stimeout.getInMilliseconds();
    else {
      result = __getSendTimeout(__socket);

      if(result < 0)
        __throwSocketException();
    }

    return result;
  }


  private native static int __setReceiveTimeout(int socket, int timeout);

  /**
   * Sets the receive timeout (SO_RCVTIMEO).  A timeout of zero indicates
   * an infinite timeout.  A negative timeout is undefined.
   *
   * @param timeout The receive timeout in milliseconds.
   * @exception SocketException If the option value could not be set.
   */
  public void setReceiveTimeout(int timeout) throws SocketException {
    __rtimeout.setInMilliseconds(timeout);

    if(!getUseSelectTimeout()) {
      int result =
        __setReceiveTimeout(__socket, timeout);

      if(result < 0)
        __throwSocketException();
    }
  }


  private native static int __getReceiveTimeout(int socket);

  /**
   * Retrieves the receive timeout (SO_RCVTIMEO).
   *
   * @return The receive timeout in milliseconds.
   * @exception SocketException If the option value could not be set.
   */
  public int getReceiveTimeout() throws SocketException {
    int result;

    if(getUseSelectTimeout())
      result = __rtimeout.getInMilliseconds();
    else {
      result = __getReceiveTimeout(__socket);

      if(result < 0)
        __throwSocketException();
    }

    return result;
  }


  private native static int __recvfrom(int socket, byte[] data, int offset,
                                       int length, int family, byte[] address);

  /**
   * Reads packet data from the socket.  IPv4 ({@link #PF_INET})
   * packets will be delivered in their entirety, including the IP
   * header.  IPv6 ({@link #PF_INET6}) packet data will not include
   * the IPV6 header.
   *
   * @param address The source to read from.
   * @param data The buffer in which to store the packet data.
   * @param offset The offset into the buffer where the data should
   *               be stored.
   * @param length The number of bytes to read.
   * @exception IllegalArgumentException If the offset or lengths are invalid.
   * @exception IOException If an I/O error occurs.
   * @exception InterruptedIOException If the read operation times out.
   * @return The number of bytes read.
   */
  public int read(InetAddress address, byte[] data, int offset,
                  int length)
    throws IllegalArgumentException, IOException, InterruptedIOException
  {
    if(offset < 0 || length < 0 || length > data.length - offset)
      throw new IllegalArgumentException("Invalid offset or length.");

    int result = 0;

    if(getUseSelectTimeout() && !__rtimeout.isZero())
      result =
        __select(__socket, true, __rtimeout.seconds, __rtimeout.microseconds);
	//System.out.println("s" + System.currentTimeMillis());
	
	// ??? setsockopt(ssock, IPPROTO_IP, IP_HDRINCL, (char *)&bOpt, sizeof(bOpt));

    if(result == 0)
      result =
        __recvfrom(__socket, data, offset, length, __family,
                   address.getAddress());
	//System.out.println("e" + System.currentTimeMillis());

    if(result < 0) {
      if(__isErrorEAGAIN())
        __throwInterruptedIOException();
      else
        __throwIOException();
    }

    return result;
  }


  /** Same as {@code read(address, data, 0, data.length);} */
  public int read(InetAddress address, byte[] data)
    throws IOException, InterruptedIOException
  {
    return read(address, data, 0, data.length);
  }


  private native static int __sendto(int socket, byte[] data, int offset,
                                     int length, int family, byte[] address);



  /**
   * Writes packet data to the socket.  The data should not include
   * the IP header.  IPv4 ({@link #PF_INET}) sockets may set the
   * IP_HDRINCL option with {@link #setIPHeaderInclude}, in which case the
   * packet data should include the IP header.
   *
   * @param address The destination to write to.
   * @param data The buffer from which to copy the packet data.
   * @param offset The offset into the buffer where the data starts.
   * @param length The number of bytes to write.
   * @exception IllegalArgumentException If the offset or lengths are invalid.
   * @exception IOException If an I/O error occurs.
   * @exception InterruptedIOException If the write operation times out.
   * @return The number of bytes written.
   */
  public int write(InetAddress address, byte[] data, int offset, int length)
    throws IllegalArgumentException, IOException, InterruptedIOException 
  {
    if(offset < 0 || length < 0 || length > data.length - offset)
      throw new IllegalArgumentException("Invalid offset or length.");

    int result = 0;

    if(getUseSelectTimeout() && !__stimeout.isZero())
      result =
        __select(__socket, false, __stimeout.seconds, __stimeout.microseconds);

    if(result == 0)
      result =
        __sendto(__socket, data, offset, length, __family,
                 address.getAddress());

    if(result < 0) {
      if(__isErrorEAGAIN())
        __throwInterruptedIOException();
      else
        __throwIOException();
    }

    return result;
  }


  /** Same as {@code write(address, data, 0, data.length);} */
  public int write(InetAddress address, byte[] data)
    throws IOException, InterruptedIOException
  {
    return write(address, data, 0, data.length);
  }

}
