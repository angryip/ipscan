---
title: Documentation
layout: default
---

# Introduction #

Scanning of computer networks (searching for addresses with known properties) is a practice that is often used by both network administrators and crackers. Although it is widely accepted that activity of the latter is often illegal, most of the time they depend on exactly the same tools that can be used for perfectly legitimate network administration – just like a kitchen knife that can be used maliciously.
Thanks to the recent activity of mass-media on the subject (that popularized the wrong term for a cracker – a 'hacker'), nowadays every educated person more or less understands the reasons and goals that stand behind malicious cracking: curiosity, stealing of information, making damage, showing self-importance to the world, etc. But why do administrators need to scan their own networks? There are plenty of answers: to check status of computers and various network devices (are they up or down), find spare addresses in statically-addressed networks, monitor the usage of server-type or P2P applications, make inventory of available hardware and software, check for recently discovered holes in order to patch them, and much more things that are even difficult to foresee.

Angry IP Scanner is widely-used open-source and multi-platform network scanner. As a rule, almost all such programs are open-source, because they are developed with the collaboration of many people without having any commercial goals. Secure networks are possible only with the help of open-source systems and tools, possibly reviewed by thousands of independent experts and hackers alike. 

Certainly, there are other network scanners in existence (especially single-host port scanners), however, most of them are not cross-platform, are too simple and do not offer the same level of extensibility and user-friendliness as Angry IP Scanner. The program's target audience are network administrators, consultants, developers, who all use the tool every day and therefore have advanced requirements for usability, configurability, and extensibility. However, Angry IP Scanner aims to be very friendly to novice users as well.

# Theory of network scanning #

## Networking ##

Computer networks, especially large ones, are very heterogeneous – they are composed of many interconnected devices into subnetworks using different topologies, which are in their own turn interconnected into larger networks, etc. The point here is that thanks to bridges between networks, all of them can use different physical (and data link) mediums for communication, with PPP over dial-up, IEEE 802.3 (Ethernet), and 802.11 (Wi-Fi) being the most popular. 

The famous [OSI model](http://en.wikipedia.org/wiki/OSI_model) defines seven layers of networking protocols. While the layers 3 and 4 are the most interesting to scanners – they are guaranteed to exist in any (IP-based) network regardless of physical mediums and provided higher level services, other layers can be interesting as well: local network scans can make use of the 1st and the 2nd layers in order to bypass higher level filtering. Higher level protocols are interesting because they are actually the ones users are most interested in, thus network scanners most often reach these layers too in order to detect the actual running services that make use of scanned network and transport endpoints (addresses and ports).

## The Internet Protocol ##

IP, in “IP address” and “IP scanner”, means nothing more complex than [Internet Protocol](http://en.wikipedia.org/wiki/Internet_protocol_suite). Nowadays, thanks to the Internet, [TCP/IP](http://en.wikipedia.org/wiki/Transmission_Control_Protocol) is the most widely spread network protocol that over the years has replaced many other LAN and WAN protocols – it is now used in the majority of networks not even directly connected to the Internet. An IP address is the unique identifier of a network interface in the network. Most of the world still uses the older [IPv4](http://en.wikipedia.org/wiki/IPv4) version of the protocol, that limits the address space to 32 bits, making the maximum number of directly addressable nodes to be less than 4 billion, which will soon not be enough for current Earth's population of over 6 billion and the increasing usage of computers and mobile devices. In order to fix the problem, [IPv6](http://en.wikipedia.org/wiki/IPv6) was introduced at the end of the previous decade, that among other features provides a much broader address space of 128 bits. However, different tricks were employed in order to prolong IPv4 usage, such as [CIDR](http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing) and [NAT](http://en.wikipedia.org/wiki/Network_address_translation) – it is too expensive to make such a big switch. 

*CIDR* stands for Classless Inter-Domain Routing, that gave us “network prefix” size notation (e.g.  /24), as opposed to the early “classful” Internet, where address ranges were divided into classes of fixed size (A, B, C – with prefixes of 8, 16, 24 bits respectively) and were assigned to organizations only wholly. CIDR then came to help with the introduction of network masks and special prefix notation, meaning how many bits from left to right are the same in all addresses that are on the same network. This allowed for much more flexible address range assignments to networks with varying sizes, fixing the situation when most organizations were too large for class C (254 total addresses), and were assigned class B ranges (65,534 total addresses, but 16,384 total networks). And there were only 128 class A networks with 16,777,214 addresses in each of them.

*NAT* stands for Network Address Translation and is an artful idea of making addresses in private subnets (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16, etc) access the outer Internet as if they had public addresses. NAT is usually implemented by routers by translating addresses of outgoing packets to router's own address and then doing the opposite translation for incoming packets from router's own address to the private address of the host that initiated the connection. 
Both these tricks and some others allow connecting more hosts to the IPv4 Internet than it was initially planned, slowing down the adoption of IPv6. Until now, there are only a few ISPs worldwide supporting IPv6 and a relatively small number of early adopters, bridging their IPv6 networks to IPv4. Angry IP Scanner was designed with IPv6 in mind, but the present user interface supports IPv4 only, as it is currently more useful.

## Transport layer ##

While only IP protocol is fine for sending of packets between hosts, there is a need to differentiate multiple senders and receivers on each host (sockets). This possibilities are provided by transport protocols [UDP (User Datagram Protocol)](http://en.wikipedia.org/wiki/User_datagram_protocol), [TCP (Transmission Control Protocol)](http://en.wikipedia.org/wiki/Transmission_Control_Protocol), and their companion, [ICMP (Internet Control Message Protocol)](http://en.wikipedia.org/wiki/Internet_Control_Message_Protocol). All these protocols are independent of whether IPv4 or IPv6 is used underneath.

Both UDP and TCP define 'ports' – endpoints on each host that are differentiated using their numbers (16 bits, 0 to 65535). The notion of 'port' is analogous to the external ports every computer has (that are used for communicating with printers, mice, keyboards and any sort of external devices) – both provide endpoints of communication with the outside world, so physical and 'virtual' ports have the same name. 
UDP is the simplest addition to IP possible, providing unreliable point-to-point packet transmission, while TCP encapsulates 'streams' of data, providing internal handshaking and acknowledgment sending mechanisms in order to provide reliable data transmission. While majority of services rely on TCP, there are some that do not require the overhead of handshakes, automatic retransmissions, etc – they use UDP, e.g. DNS (Domain Name Service), real-time audio and video streaming, multiplayer games, etc. ICMP is for various control messages interchanged by hosts and other network devices, used for TCP, UDP, and general IP packet transmission.

## Scanning ##

The word _scan_ is derived from the Latin word _scandere_, which means to climb and later came to mean "to scan a verse of poetry," because one could beat the rhythm by lifting and putting down one's foot. The Middle English verb _scannen_, derived from _scandere_, came into Middle English in this sense (first recorded in a text composed before 1398). In the 16th century this highly specialized sense having to do with the close analysis of verse developed other senses, such as "to criticize, examine minutely, interpret, perceive." From these senses having to do with examination and perception, it was an easy step to the sense "to look at searchingly" (first recorded in 1798). In modern language, it usually may mean: “to examine closely”, “to look over quickly and systematically”, “to analyze”. In electronics, it usually means: “to move a finely focused beam \[of light, electrons, radar\] in a systematic pattern”. All these definitions can also be applied to the meaning used in computer technology that we are going to discuss below.

What does a network scanner able to do? There are usually two types of network scanners: port scanners and IP scanners. Port scanners usually scan TCP and sometimes UDP ports of a single host by sequentially probing each of them. This is similar to walking around a shopping mall and writing down the list of all the shops you see there along with their status (open or closed). Another type are IP scanners that scan many hosts and then gather additional information about those of them that are available (alive). According to the shopping mall analogy, that would be walking around the city looking for all shopping malls and then discovering all kinds of shops that exist in each of the malls. As Angry IP Scanner is an IP scanner, designed for scanning of multiple hosts, this will be the type of network scanner reviewed in the following text.

As a rule, user provides a list of IP addresses to the scanner with the goal of sequentially probing all of them and gathering interesting information about each address as well as overall statistics. The gathered information may include the following:

* whether the host is up (alive, responding) or down (dead, not responding)
* average roundtrip time (of IP packets to the destination address and back) – the same value as shown by the ping program
* TTL (time to live) field value from the IP packet header, which can be used to find out the rough distance to the destination address (in number of routers the packet has traveled) 
* host and domain name (by using a DNS reverse lookup)
* versions of particular services running on the host (e.g., “Apache 2.0.32 (Linux 2.6.9)” in case of a web server)
* open (responding) and filtered TCP and UDP port numbers
* ... and much more

The list of addresses for scanning is most often provided as a range, with specified starting and ending addresses, or as a network, with specified  network address and corresponding netmask. Other options are also possible, e.g. loading from a file or generation of random addresses according to some particular rules. Angry IP Scanner has several different modules for generation of IP addresses called _feeders_. Additional feeders can be added with the help of plugins.

## Safety and Security ##

The question of safety is always asked about security tools, like network scanners. So, how safe it is to use such programs?

Fortunately, the short response is that it is both legal and safe, however with some exceptions.
Even though nowadays legal laws do not catch up with the fast development of the IT world, network scanning has existed for almost as long as the networks themselves, meaning that there was probably enough time to update the laws. Nevertheless, scanning itself remains perfectly legal, because in most cases it neither harms the scanned systems in any way nor provides any direct possibilities of breaking into them. Network scanning is even used by some popular network applications for automatic discovery of peers and similar functionality. 

Most countries' laws forbid getting illegal access to data, destroying, spoiling, modifying it, or reducing its usefulness or value in some other way . As a rule, the scanning results just provide the publicly available and freely obtainable information, collected and grouped together. However, this legality may not apply in case some more advanced stealth scanning techniques are used against a network you do not have any affiliation with.

As the topic of user's personal safety is covered: scanning in most cases is legal, then how about the more general safety – the safety of all the people? As was already mentioned before, nothing can be one hundred percent safe. On the other hand, the best tools for maintaining the security are the same ones that are used by those who are needed to be defended from. Only that way it is possible to understand how do crackers think and how do they work. Using the same tools as they do, it is possible to check the network until it is too late because they have already managed to do it themselves.
Every serious network administrator knows that regular probing of own networks is a very good way for keeping it secure.

# Prerequisites #

## Licensing reasons ##

Angry IP Scanner is an open-source software, that is free to use, redistribute, and modify. Nowadays, free software has gained so much popularity, so even large software companies are starting to release their products that way, unthinkable a couple of years ago. The main driving force behind this is the transparency of both the code and data formats: they are reviewed by third party people and organizations, resulting in improved understanding, trust, and the resulting quality, that proprietary software and systems cannot provide. As Linus' law states: “Given enough eyeballs, all bugs are shallow” [The Cathedral and the Bazaar](http://www.catb.org/esr/writings/cathedral-bazaar/). Many governments around the World are now switching to free software and free standards not just because of cost savings, but in order to guarantee the integrity of their important data and documents, that need to last and not be dependent on any particular software vendor.

The license chosen for Angry IP Scanner is the famous GPL (GNU General Public License), which provides the users with as much freedom as possible, while restricts stealing of open-source code for usage in proprietary software. In other words, the goal of GPL comparing to other open-source licenses is to keep free software forever free. This also prevents the possibility that the author or any of the contributors will later revoke any of the freedoms granted by the license. 

Angry IP Scanner is expected to benefit from all the usual advantages of free software: reviews, contributions, community. On the other hand, it is the authors contribution to the growing open-source world, a way of saying thanks for all the free software made by other people. 

## Cross-platformness ##

Cross-platformness can be thought as of another freedom that users must have – the ability to choose their platform without sacrificing their favorite software and having their decision depend on whether some particular program will work on another platform or not. Users may be forced to use one platform at work, however prefer another one at home, but they still need the software they need and like in both cases.

Nowadays, a new great wave of platform switching is coming: Apple is being said to be reborn due to much increased sales and popularity of their computers, Linux is gaining more and more popularity in desktop market (in addition to dominance in the world of servers). The world will be a lot more diverse in terms of different software and hardware platforms in the next years that it used to be. And this is a good thing: competition drives innovation; more choices mean more freedom.

However, cross-platformness poses many challenges to the developers of software. No matter what technology is chosen, there will always be some platform-specific work left in order to make users on each platform happy by following standards and conventions of each of them. This is especially true for graphical desktop applications like Angry IP Scanner.

There are many reasons why particularly Angry IP Scanner needs to be cross-platform. One of them is that most of the users still use Microsoft Windows, however it is the worst platform for network scanning, so Angry IP Scanner needs to support both popular platforms and the more useful, but less popular ones. 

## Technological choice ##

When considering the possibilities of creating cross-platform applications, there are not many. Writing cross-platform code in low level languages such as C++ in very difficult. High level scripting languages often do not perform good enough and are too high level for such networking applications as Angry IP Scanner.

It turned out that Java is the best choice, being marketed for years as “write once, run anywhere” language. Although this is not 100% true if a rich and conforming user experience is required, it is still the best possible alternative. Java has the best development tools of any language, good enough productivity, wide platform support, very large developer community, several independent vendors (including the GNU open-source implementation), strict standardization. In addition to that, Sun Microsystems, the primary developer of Java, have declared their implementation of Java as open-source during the JavaOne 2007 conference; in the past few years they have started pushing the adoption of Java to desktops very aggressively to complement the dominance in server-side enterprise systems market. In other words, Java has been the biggest success of any software technology ever.

For Angry IP Scanner, Java provides solid platform for cross-platform development, making more than 95% of code platform-independent. Only some GUI tweaks and low-level networking need special attention on different operating systems. This is what JNI (Java Native Interface – the way to bind native code with Java) is intended for and thus makes this low-level networking possible.

As a GUI toolkit, it was chosen to use [SWT (Standard Widget Toolkit)](http://www.eclipse.org/swt/), provided by Eclipse project. Its benefits include the usage of native GUI controls and widgets on every supported platform, making Java programs indistinguishable from the native ones. And this is important to users – they want their system-wide settings, themes, and operating system standards to be respected.

## Usage scenarios ##

Network scanners can have very wide range of uses, but they can be generally divided into three major categories: attacking, defense, and maintenance.

Attacking usually cannot be performed using a scanner alone: scanning can only retrieve information that can be further used with malicious purposed for an attack, unless a scanner does not flood the network, which can be considered a DoS (Denial of Service) attack. In all other cases, while it should be noted that scanning itself is not attacking, it still can be used as a part of an attack, that is, with malicious purpose. Other malicious uses besides attacking or breaking into can include searching for hosts, providing anonymous services for, e.g. spammers looking for “blind relay” SMTP hosts, web surfers willing to stay anonymous for any reason  looking for anonymous HTTP proxies, etc. All these uses are not desirable, but if we face the facts, the attackers will have their tools for malicious use anyway, so the goal many security tools such as network scanners is to provide these tools to the people, who need to defend from these attacks and malicious usage of network services. 

This brings us to the second category – defense. This is the desired usage of security tools with the goal of finding the same vulnerabilities or misconfigurations as malicious user would do. But again, scanner is only one of the tools that must be used in order to implement a successful defense strategy and successfully secure a network, however it is one of the most important one – it allows finding of the problems that need to be dealt with. 

Besides searching for problems, scanning can be used for monitoring with the same goal of keeping the network secure. Monitoring is especially important in the networks where there are many users who control their computers themselves, like ISP and public Wi-Fi networks. These users are often either not experienced enough to protect their computers from threats or they are willingly trying to make something undesirable to the network administrator.

The third category – maintenance – means performing routine tasks such as monitoring or inspecting the network with the goal of keeping it running and/or extending it. Examples of such scanning would be: monitoring uptime and availability of hosts/services, finding spare addresses for introduction of new hardware, mapping of networks, collecting various statistics about the network for reporting, making inventory of available hardware and software, planning of upgrade schedules, and so forth. 

All these different usages require the same basic functionality from the scanner, but all of them also have slightly different usability requirements, which are going to be addressed by Angry IP Scanner.

# Technical description #

## Desktop Java availability ##

Since the times when Java applets started loosing their popularity (due to a number of reasons, including the buggy JVM (Java Virtual Machine) included in early versions of Netscape Navigator), Java was mostly used on server-side, for generation of Web pages and other enterprise functionality. Java on desktop systems has long been considered to be slow and “ugly” (not conforming to the system look and feel). This was the price to pay for cross-platformness and security provided by the JVM. Then consumers started to familiarize themselves with Java with the help of their mobile phones and other consumer devices, such as Blu-ray disks. In the meantime, Microsoft has decided to discontinue its Java implementation in favor of their own single-platform clone – .NET, but, counter intuitively, this led to better support of Java on Windows: Microsoft's JVM was not standard enough and was lagging behind the official one. Desktop Java (most notably, the official implementation of it by Sun) has been evolving all the time, too, becoming faster and imitating native applications better and better. On the other hand, no Microsoft support means that Java is no longer shipped with Windows, and many people need to install it themselves. 

However, my own trial has shown that majority of even Windows users already have Java. Some computer vendors ship Windows with preinstalled Java, some users download it themselves (there is a growing number of Java applications, and its very easy to do so), some websites install Java automatically in order to show applets, etc. Apple distributes their own versions of Sun's Java bundled with the Mac OS. All major Linux distributions have GNU Java preinstalled and now thanks to the special licenses from Sun, some even include the official Java as well (Ubuntu). So, in the real world, most of the users will either already have Java on their computers or will be able to easily install it in a matter of minutes. Sun is even planning to release the special 'consumer' version of JRE (Java Runtime Environment) that will be smaller and even easier to install.

## Modularity and extensibility ##

Angry IP Scanner is an open-source program. At first sight it may seem that open-source software can be monolithic – users will be able to extend its functionality anyway, by editing the source code. However, in most cases this is not true. Very often, in order to make a significant change in the code, developer must spend quite a lot of time reading and debugging the code to understand how it works and where to make the exact modification. And it is widely known that reading of code is often more difficult than writing, especially if the original author has not put any effort to make the code extensible. 

The Linux kernel has gone this path in the past: it started with 100% monolithic code, however, as it grew and attracted more and more developers, a more modular approach was taken. Now, Linux kernel has modules, which can be either integrated into the base kernel binary, or can be loaded separately on demand. This and some other improvements resulted in much quicker development that can be easily noticed by the increased pace of kernel releases.

Thus, if a modular extensible system is in place, any individual is able to add additional functionality to the software with much less effort, because extensibility points are likely to be well documented and have simple interface. On the other hand, that allows to reduce the bloat of the original application, making the code simpler and possibly the  application itself faster, because some “optional” plugins are not loaded at all if they are not used.

A plugin is usually an external software component that can be loaded dynamically in order to add or extend functionality of the base program. The internal design of Angry IP Scanner aims to be as modular as possible in order to be able to introduce even more either internal extensions or external plugins later. 

### Feeders ###

User selects one _feeder_ prior to scanning and configures appropriately in order to provide the desired sequence of IP addresses to the Scanner. Built-in _feeders_ include: 

* IP Range – iterates IP addresses beginning and ending with the two provided addresses, e.g. from 192.168.0.1 to 192.168.0.255
* Random – generates the requested number of random IP addresses according to the provided bit mask (in order to define some portions of each generated address), e.g. 100 addresses starting with 192. and ending with .125.
* IP List File – extracts IP addresses from any text file provided by the user. The file may be in any format – the feeder looks for all tokens similar to IP addresses in it, so output of any exporter can be used later as an input for a new scan.
* Advanced – provides the ability to specify more complex ruler for generation in textual form (for advanced users), e.g. 192.168-170.150.1-255 or 192.168.0.0/24.

### Fetchers ###

User selects several _fetchers_ prior to scanning. The list of selected _fetchers_ defines the type and amount of information collected about each scanned IP address. Built-in _fetchers_ include (implemented and planned):

* IP address – the simplest fetcher that just shows the current scanned IP address
* Ping – displays the roundtrip time of packets to the host and back using the selected Pinger (see below)
* TTL – displays the TTL value from the IP header of returned packets from the host (available in case of certain Pingers only)
Hostname – displays the hostname obtained using the DNS reverse lookup using the IP address
* MAC address – MAC (hardware) address of the host's physical network interface (if available), obtained using an ARP request. This only makes sense on a local network.
* NetBIOS username/computer/workgroup – three fetchers specific to Windows hosts, use NetBIOS requests to obtain the information.
* Ports – obtains the list of open TCP ports on the scanned host. Note that only port numbers specified by user are scanned.
* Filtered ports – obtains the list of filtered TCP ports on the scanning host. Filtered ports are those filtered by firewalls or routers, so there must be some reason why specifically these ports are being hidden.
* Version detection – tries to detect the services and their versions behind open ports

### Pingers ###

_Pingers_ are used internally by Ping and TTL _fetchers_, but they are special because subsequent _fetchers_ depend on pinging results for decision whether to continue scanning the address and adaptation of timeouts. The following internal _pingers_ are implemented (each appropriate for different situations):

* ICMP echo – the standard pinging method used by the ping program. The drawback of this method is that many firewalls specifically block these packets and sending of them requires the usage of raw sockets and therefore, administrator privileges on most platforms.
* Windows ICMP.DLL – because of the recent removal of raw socket support from Windows (see Scanning on Different Platforms, page 40), there was a need for alternative Windows-specific implementation for ICMP echo pinging.
* UDP – sends UDP packets to a port that is likely to be closed. Hosts usually respond with an negative * ICMP packet if this is the case, telling the scanner that host is actually alive (responding)
* TCP – makes a connection attempt to port 80 on the host, because it is likely not to be filtered. Both positive and negative responses from the host mean that it is alive.
* ARP – not implemented yet, but may provide advantages over all other methods in local networks by using a physical layer ARP requests, bypassing firewalls.

### Exporters ###

_Exporters_ are used after the scanning has been completed in order to export the results outside of Angry IP Scanner, most often to a file in some format. Built-in _exporters_ include:

* TXT – human readable plain text file
* CSV – comma-separated values (in the order of selected fetchers)
* XML – well-formed XML for machine processing. Can be later post-processed by a custom XSL template.
* IP:Port list – outputs IP:port line for each open port of each alive host. These files can be read by some popular programs.

### Openers ###

_Openers_ – are used for “opening” any scanned host in the result list. As a rule, _openers_ execute external programs in order to connect or send something to particular hosts, e.g. open a Web browser or send a shutdown message.

## The Scanning component ##

Angry IP Scanner's scanning component is implemented using the [Mediator pattern](http://en.wikipedia.org/wiki/Mediator_pattern), which routes messages between the user interface, generator of IP addresses (_feeder_), and information retrieving modules (_fetchers_), generating events for other components. This ensures that all components of the program are loosely coupled and therefore reusable and interchangeable.

The scanning component itself is very abstract – it knows nothing about what information is being collected. Information is gathered with the help of _fetchers_ that are selected by the user. Angry IP Scanner contains a number of built-in _fetchers_ (e.g. mentioned above), but additional third-party fetchers can be used with the help of plugins. This ensures very good scanning flexibility and extensibility of the program – each user can have very different and non-standard needs, especially if the user is an administrator of a large network.

During scanning, the scanning component is controlling states. In the scanning state it iterates IP addresses provided by _fetcher_ and gives control to _fetchers_ in order to do the actual scanning.
All this would be very slow without doing most of the work in parallel.

## Parallelization and Threads ##

The easiest and most reliable way to make code run in parallel is the usage of threads, because in this case the operating system is dealing with all the complexity of task switching and scheduling, making programming a lot easier.  The OS can even run several threads really in parallel if the machine has several CPUs, which is another great advantage over manual parallelizing. The programmer must only take care of proper synchronization.

But let's assume that the machine has only one CPU. Then, as opposed to microprocessor systems, threads cannot just magically increase the performance, especially in the case, when each thread needs 100% of processor time, which would result in performance degradation due to too frequent context switching compared to sequential program.

Fortunately, this problem is very improbable in case of scanning of networks:  networks are generally much slower than the processor. Consequently, time consumed on processing of each packet is mostly spent on waiting for the second party's response, allowing the processor do deal with other jobs, which can include sending and waiting for other packets at the same time, resulting in much shorter total time required to process many packets compared to the sum of each packet's individual processing times.
Without threads it is also possible to process multiple packets simultaneously with the help of asynchronous sockets. In some way they can even perform slightly better, however if any complex processing of the results is in place and invocation of third-party plugins that send completely different and unrelated packets out all make the thread usage more reliable and much easier in terms of programming. Thanks to threads, the code of each fetcher or plugin can be linear, eliminating the need to think about what other fetchers are sending or receiving at the same time. It is well accepted that good design and simplicity of the code in programming are often much more important than slight performance improvements, because quality (working software) and lower costs of maintenance are very desirable in any case. Moreover, the easier it is to write a plugin, the more third-party plugins will be written, affecting the end users positively.

Unfortunately, there is a practical limit to the number of threads used for scanning. The limit is reached when context switching starts taking a considerable amount of processor time instead of doing the actual job. As different operating systems implement different switching and scheduling algorithms, maximum practical number of threads is different on different platforms even if running on the same hardware. Trials show that Microsoft operating systems are inferior in this respect to the free systems (e.g. Linux, BSD), while older Windows versions (9X and ME) were not even able to process user events at the same number of threads that had no noticeable impact on Linux.

Because the maximum number of threads may be very different, Angry IP Scanner uses no more than 100 threads at a time by default. The user has the possibility to increase this number if their hardware and software allows that, or the opposite. Some latest combinations can even handle 500 scanning threads with no problems, however this number may be close to the situation when threads finish their jobs before the scanner is able to reach the limit by starting new ones. Another limitation may be due to instability of some network adapters or their drivers (especially wireless ones) – they just cannot process so many simultaneous connections or packets, so they start loosing them, rendering scanning results unreliable. The same problem sometimes is created artificially mostly on Windows platforms by rate limiting of connection attempts (unpatched Windows XP SP2 limits to 10 incomplete outbound connection attempts at a time) with the goal of preventing scanning by worms that are unfortunately likely to get into the system. 

## Scanning performance improvements ##

The wish to make their tools better is very natural for humans. As scanning is a process that takes time, it is very natural to think about the ways to increase its speed. The delay of getting the information depends on scanning speed, and consequently the validity of the scanning results depends on it as well, because some networks can be very dynamic – especially dial-up and Wi-Fi networks. In some cases scanning speed can become even critical, e.g. when network administrator needs to localize all infected machines because of a zero-day worm or virus getting into the network. Also productivity should not be underrated – quick scanning makes administrator's job more effective, especially in case of large corporate networks.

Besides parallelizing, there are some more possibilities how to increase the scanning speed. The easiest way that is sometimes useful is the speed-accuracy compromise: the user can decrease various timeouts and the number of probes done (in case of pinging, for example). This will increase the probability of missing some hosts or ports, but the results are returned much more quickly. This can be useful in very crowded networks for getting of statistics.

A little bit smarter development of the above-mentioned idea are adaptive timeouts. The principle is in measuring of the average roundtrip time of packets in either the whole network or to the particular host and then using the value as a timeout for sending of the following packets. This can dramatically increase port scanning speed in case the host is probed with ICMP echo (ping) packets first, especially in contemporary networks where there are many network- and even host-based firewalls blocking the packets, making most of the ports filtered (no reply is sent to the TCP SYN packets at all). This makes port scanning speed depend a lot on the length of timeout for each port (for how long we are waiting for the response), and the shorter the timeout is, the faster scanning becomes. That means the scanner must always select the shortest reliable timeout possible, during which most of the packets should have enough time for getting back. And this desired timeout can be different for each scanned host. However, if the timeout is too short, then scanner will not get any replies to most of the packets, considering that the ports are filtered, but actually the host just replies slower than expected. Angry IP Scanner measures the average roundtrip time (if possible), multiplies it by three and then uses that value as a timeout for port scanning. Even this simple solution speeds up scanning several times.

Another relatively easy idea is thread pooling. Although, creation of each thread is by no means cheaper than creation of a process (sometimes threads are even called “lightweight processes”), it still is a relatively complicated task, involving memory allocation, registration in different system tables, etc. Considering very high level of thread creation and their short-liveness, it is wiser to reuse them instead of destroying them continually. As the scanner needs to limit the maximum number of threads anyway, why not to use the same number for the size of thread pool? In this case, when any thread finishes scanning a host, it is put to sleep, marked as free, and is returned back to the pool. Then, in case there are more addresses to scan and some threads are available in the pool, they can be taken back and reused for scanning of other hosts. 

## Scanning on different platforms ##

Even though Angry IP Scanner is a cross-platform application, there is no reason to hope that each platform is suitable as a scanning source equally well.

Having mentioned the weakness of older Windows operating systems related to threads, we must say that Windows versions based on the NT architecture are a lot better in this respect - their  TCP/IP stack implementation is much better. However, starting from XP SP2, consumer versions of Windows became even worse for scanning than before because of some newly introduced limitations. Namely, Microsoft has started limiting the number of outgoing connections per minute, removed raw socket support from Windows XP that are needed for sending and receiving of ICMP packets as well as for performing of more sophisticated scanning tricks, etc. The official reason was to prevent the widely-spread Internet worms from doing scanning and DDoS (Distributed Denial of Service) attacks from the infected Windows machines, but there is some speculation that the real reason was to force more advanced users to use the more expensive Windows Server family of operating systems. According to Microsoft, Windows XP (and Vista) is made for regular users who do not need to scan their networks or do anything advanced with networking.
Open source operating systems (and even Mac OS X that has its kernel based on FreeBSD) for sure are much better suitable for network administrators, considering their security, out-of-the-box functionality, and even the price. Many of the advanced functionality has existed in them for decades, without compromising security and becoming the nest for all kinds of malware, that are ruining the reputation of Windows.

Having said that, Linux gives the users of Angry IP Scanner more features, higher scanning quality and speed. As a bonus, Linux users get a lot of other useful software for network monitoring that works only there.

In order to reduce confusion among the end-users on Windows platform, it was decided to implement some detection mechanism of the reliable values for maximum number of threads and different timeouts when starting Angry IP Scanner for the first time. It will either try to open a local port itself and try to aggressively scan it for a couple of seconds or ask the user for some host and port that they know is open and accessible and perform the test against it. Then it will set up the best values that work reliably on the given machine. Unfortunately, it will make scanning a lot slower on most newer Windows machines, but at least the scanning results will be reliable and trustful.

## Deployment ##

Deployment methods of cross-platform applications are usually not cross-platform at all – each operating system has its own requirements on how software is distributed and installed.
One of the goals of Angry IP Scanner is its ease of use: it does not require any complex installations and can be easily copied and run anywhere. Some of the users find it useful to store Angry IP Scanner on USB memory sticks and then just plug them in order to do a quick scan and store the results on the same memory stick as the program. 

Therefore, Angry IP Scanner does not employ traditional installers, which are common on Windows platforms. 

Instead, it was decided to use the standard Java distribution format: jar (Java Archive). Jar files are simply compressed zip files, containing compiled Java classes and resources as well as metadata. The metadata (stored in META-INF/MANIFEST.MF file) allows to specify the Main-Class attribute. This is very useful, because it makes jar files executable on all platforms. Usually such files can be executed from the command-line as java -jar jarfile.jar, however JRE for all platforms installs special hooks to make jar files “double-clickable” as well, just like native programs. In addition to that, Linux offers special jar binary handler for jar files, so they may be executed from the command-line just like any binary program provided that the file has executable permission. Drawback of jar files include the inability to specify extra options to the JVM, such as maximum memory heap size and others, but the default of 64 Mb should work fine for Angry IP Scanner. Another problem is inability to change the icon for jar file – users will always see the default jar file icon instead of the custom one until they run the application. However, this may be fixed by offering the user to create a shortcut/launcher on the first run with the icon and any required command-line options.

## Native libraries ##

While jar files are very convenient, there is still one problem left: Angry IP Scanner requires the usage of native libraries (\*.dll / \*.so / \*.jnilib) in order to do some low-level networking tasks and use native GUI controls (provided by the SWT toolkit). Unfortunately, Java offers no standard way of packaging these native libraries into jar files, because they are loaded by the operating system that has no knowledge of jar file format. As we want to distribute the program as a single file, the trick that we have to use here is automatic extraction of native libraries prior to loading them.

By default, JNI libraries are loaded using the System.loadLibrary() call, which expects to find the library from the system environment. Angry IP Scanner substitutes these calls with its own LibraryLoader.loadLibrary() that first tries to load the library from the system, then from the temporary directory, and if unsuccessful then extracts the libraries to temporary directory and loads them again. This allows the user not to know about these native libraries at all, as the whole process is very transparent. Angry IP Scanner itself does not delete the extracted files from the temporary directory because it may take advantage of them on next startup. However, if the user or some system process deletes them, there are no problems with extracting them again. Even if extraction is required, startup of Angry IP Scanner never takes more than a pair of seconds.

Since jar files are generally cross-platform, there is also a possibility that users will try to run the jar file built for another platform. In this case, Angry IP Scanner will detect the situation and will display an informative message to the user using the cross-platform Swing GUI toolkit.

## Platform Specifics ##

However, in order to meet the expectations of users on certain platforms, there is a need to provide optional packages  in other formats as well.

The biggest such example is Mac OS X. Programs for this platform are distributed as application bundles which are directories having the .app suffix with special structure  in order to include startup options, metadata for the operating system, icons, and other resources. On this platform, programs never offer “installers”. Every application bundle usually includes everything an application needs and can be freely moved or copied on the system, just like a jar file. Finder, the Mac OS X file manager, handles application bundles as single entities. Consequently, the facts that there is the standard way of packaging Java applications as bundles (using the JavaApplicationStub provided by every OS X system) and that SWT applications require passing the Mac-specific command-line option -XstartOnFirstThread to the Java runtime, which is not possible using a plain jar file, it was decided to distribute Angry IP Scanner as a zipped application bundle for this OS. The Ant build.xml script was made to build the bundle automatically.

Linux systems, on the other end, have very good standardized packaging and dependency management systems (either .rpm or .deb) that are very convenient to use, provided by each distribution. It may be wise to provide these files as well, making it possible to install Angry IP Scanner using the OS program database. In future, it may be possible to include Angry IP Scanner in the official repositories of the most popular Linux distributions (Fedora, Ubuntu, Gentoo) in order to make use of dependency management and link to SWT libraries, packaged separately. Another option would be to compile the whole program to native code using the GNU Java compiler (GCJ), just like most distributions are doing with Eclipse and other popular Java programs.

Windows applications, because of the lack of standard packaging and application management systems in the OS, often provide different installation programs that copy files and do necessary changes in the system before an application may run. Angry IP Scanner will never require such an installation, however in the future it may optionally offer the same copying and creation of the shortcut on first run by asking the user if they want to do it.
