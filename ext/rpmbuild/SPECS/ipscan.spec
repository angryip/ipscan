Summary:            Angry IP Scanner - fast and friendly network scanner
Name:               ipscan
Version:            RPM_VERSION
Release:			1%{?dist}
License:            GPLv2+
Group:              Applications/Internet
BuildRoot: 			%{_builddir}/%{name}
URL:                https://angryip.org/
Packager:			Anton Keks
Requires:			java >= 21

%description
Angry IP Scanner is a cross-platform network scanner written in Java.
It can scan IP-based networks in any range, scan ports, and resolve
other information.

The program provides an easy to use GUI interface and is very extensible,
see https://angryip.org/ for more information.

%prep

%build

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/%{_libdir}/ipscan $RPM_BUILD_ROOT/%{_datadir}/applications $RPM_BUILD_ROOT/%{_datadir}/pixmaps $RPM_BUILD_ROOT/%{_bindir}
cp %{_topdir}/../%{name}-VERSION-%{platform}.jar $RPM_BUILD_ROOT/%{_libdir}/ipscan/
cp %{_topdir}/../../../ext/deb-bundle/usr/share/applications/net.azib.ipscan.desktop $RPM_BUILD_ROOT/%{_datadir}/applications/
cp %{_topdir}/../../../resources/images/icon128.png $RPM_BUILD_ROOT/%{_datadir}/pixmaps/net.azib.ipscan.png
cp %{_topdir}/../../../ext/deb-bundle/usr/bin/ipscan $RPM_BUILD_ROOT/%{_bindir}/
chmod a+x $RPM_BUILD_ROOT/%{_bindir}/ipscan

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
%{_libdir}/ipscan/%{name}-VERSION-%{platform}.jar
%{_datadir}/applications/net.azib.ipscan.desktop
%{_datadir}/pixmaps/net.azib.ipscan.png
%{_bindir}/ipscan
