Summary:            Angry IP Scanner - fast and friendly network scanner
Name:               ipscan
Version:            VERSION
Release:			1%{?dist}
License:            GPLv2+
Group:              Applications/Internet
Source1:            ipscan.desktop
Source2:            ipscan
BuildRoot: 			%{_builddir}/%{name}
URL:                http://angryip.org
Packager:			Anton Keks
Requires:			jre >= 1.6.0

%description
Angry IP Scanner is a cross-platform network scanner written in Java.
It can scan IP-based networks in any range, scan ports, and resolve
other information.

The program provides an easy to use GUI interface and is very extensible,
see http://angryip.org/ for more information.

%prep

%build

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/%{_libdir}/ipscan $RPM_BUILD_ROOT/%{_datadir}/applications $RPM_BUILD_ROOT/%{_bindir}
cp ../../%{name}-%{platform}-%{version}.jar $RPM_BUILD_ROOT/%{_libdir}/ipscan/
cp ../../../ext/deb-bundle/usr/share/applications/ipscan.desktop $RPM_BUILD_ROOT/%{_datadir}/applications/
echo "#/bin/sh" > $RPM_BUILD_ROOT/%{_bindir}/ipscan
echo "java -jar %{_libdir}/ipscan/ipscan*.jar" >> $RPM_BUILD_ROOT/%{_bindir}/ipscan
chmod a+x $RPM_BUILD_ROOT/%{_bindir}/ipscan

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
%{_libdir}/ipscan/%{name}-%{platform}-%{version}.jar
%{_datadir}/applications/ipscan.desktop
%{_bindir}/ipscan
